package practice.email.clustering

import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.getEditDistance
import weka.clusterers.HierarchicalClusterer
import weka.core.Instances
import weka.core.Utils
import weka.core.converters.ArffLoader
import java.io.File
import java.util.*
import kotlin.concurrent.thread

data class ClusteringParams(var clustersAmount: Int, var estimate: Double) {
    private val delta = 0.01

    override fun toString(): String {
        return "Clusters: $clustersAmount \tEstimate: $estimate\n"
    }

    fun isWorse(cp: ClusteringParams): Boolean {
        val diff = cp.estimate - estimate
        if (Math.abs(diff) < delta) {
            return cp.clustersAmount < clustersAmount
        } else {
            return Math.signum(diff) == -1.0
        }
    }
}

/**
 * Defines the maximum number of clusters as a percentage of data.numInstances()
 */
val CLUSTERS_CAP = 7 / 10.0

fun getBestClusteringParams(data: Instances, options: String, debug: Boolean = false): ClusteringParams {
    val start = System.currentTimeMillis()

    var bestParams = ClusteringParams(data.numInstances(), Double.MAX_VALUE)
    val distances = getDistanceMatrix(data)

    val maxClusters = (data.numInstances() * CLUSTERS_CAP).toInt()
    for (clustersAmount in 1..maxClusters) {
        val clusterer = HierarchicalClusterer()
        clusterer.options = Utils.splitOptions("$options -N $clustersAmount")
        clusterer.buildClusterer(Instances(data))

        val classesToInstances = clustersAmount / (1.0 * data.numInstances())
        if (classesToInstances > bestParams.estimate) {
            if (debug) {
                println("For $clustersAmount clusters estimate is too bad. Skipping...\n")
            }
            continue
        }

        val estimate = estimateClusterer(clusterer, data, distances)
        val currentParams = ClusteringParams(clustersAmount, estimate)

        if (debug) {
            println(currentParams)
        }

        if (bestParams.isWorse(currentParams)) {
            bestParams = currentParams
        }
    }

    val finish = System.currentTimeMillis()

    if (debug) {
        println("Best params is: ${bestParams.toString()}")
        println("Working time: ${finish - start} ms.")
    }

    return bestParams
}

var bestParamsGlobal = ClusteringParams(Int.MAX_VALUE, Double.MAX_VALUE)
var THREADS_AMOUNT = 4;

fun getBestClusteringParamsParallel(data: Instances, options: String, debug: Boolean = false): ClusteringParams {
    val start = System.currentTimeMillis()
    val distances = getDistanceMatrix(data)
    val numClusters = (data.numInstances() * CLUSTERS_CAP).toInt()

    var from = 1;
    val perTread = numClusters / THREADS_AMOUNT
    val rest = numClusters % THREADS_AMOUNT
    var to = perTread + rest
    val trds: ArrayList<ClusterizeTread> = ArrayList(THREADS_AMOUNT)
    for (i in 1..THREADS_AMOUNT) {
        val thrd = ClusterizeTread(from, to, data, options, distances, debug)
        trds.add(thrd)
        thrd.start()
        from = to+1
        to += perTread
    }

    trds.forEach { it.join() }

    val finish = System.currentTimeMillis()

    if (debug) {
        println("Best params is: ${bestParamsGlobal.toString()}")
        println("Working time: ${finish - start} ms.")
    }

    return bestParamsGlobal
}

class ClusterizeTread(val from: Int, val to: Int, val data: Instances, val options: String,
                      val distances: Array<IntArray>, val debug: Boolean = false): Thread() {

    override fun run() {
        clusterize()
    }

    private fun clusterize() {
        for (clustersAmount in from..to) {
            val clusterer = HierarchicalClusterer()
            clusterer.options = Utils.splitOptions("$options -N $clustersAmount")
            clusterer.buildClusterer(Instances(data))

            val classesToInstances = clustersAmount / (1.0 * data.numInstances())
            var isContinue = false
            synchronized(bestParamsGlobal) {
                if (classesToInstances > bestParamsGlobal.estimate) {
                    isContinue = true
                }
            }
            if (isContinue) {
                if (debug) {
                    println("For $clustersAmount clusters estimate is too bad. Skipping...\n")
                }
                continue
            }


            val estimate = estimateClusterer(clusterer, data, distances)
            val currentParams = ClusteringParams(clustersAmount, estimate)

            if (debug) {
                println(currentParams)
            }

            synchronized(bestParamsGlobal) {
                if (bestParamsGlobal.isWorse(currentParams)) {
                    bestParamsGlobal = currentParams
                }
            }
        }
    }
}


fun estimateClusterer(clusterer: HierarchicalClusterer, data: Instances, distances: Array<IntArray>): Double {
    val n = data.numInstances()
    var innerClustersDistance = 0.0
    var outerClustersDistance = 0.0
    var totalClustersDistance: Double
    for (i in 1..n - 1) {
        for (j in 0..i - 1) {
            val firstCluster = clusterer.clusterInstance(data.instance(i))
            val secondCluster = clusterer.clusterInstance(data.instance(j))
            if (firstCluster == secondCluster) {
                innerClustersDistance += distances[i][j]
            } else {
                outerClustersDistance += distances[i][j]
            }
        }
    }
    totalClustersDistance = innerClustersDistance + outerClustersDistance
    return innerClustersDistance / totalClustersDistance + clusterer.numClusters / (1.0 * n)
}

fun getDistanceMatrix(data: Instances): Array<IntArray> {
    val n = data.numInstances()
    val distances = Array(n) { IntArray(n) { 0 } }
    for (i in 1..n - 1) {
        for (j in 0..i - 1) {
            distances[i][j] =
                    getEditDistance(data.instance(i).stringValue(0), data.instance(j).stringValue(0))

        }
    }
    return distances
}

fun getEstimate(eml1: File, eml2: File): Int {
    val content1 = EmailParser(eml1).parse().content.body
    val content2 = EmailParser(eml2).parse().content.body

    val header1 = QuotesHeaderSuggestions.getQuoteHeaderLine(content1)
    val header2 = QuotesHeaderSuggestions.getQuoteHeaderLine(content2)

    return when {
        header1 == null && header2 == null -> 0
        header1 == null || header2 == null -> Int.MAX_VALUE
        else -> getEditDistance(header1, header2)
    }
}