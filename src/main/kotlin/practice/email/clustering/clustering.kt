package practice.email.clustering

import practice.email.parser.EmailParser
import practice.email.parser.QuotesHeaderSuggestions
import practice.email.parser.getEditDistance
import weka.clusterers.HierarchicalClusterer
import weka.core.Instances
import weka.core.Utils
import weka.core.converters.ArffLoader
import java.io.File

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

fun getBestClusteringParams(data: Instances, options: String, debug: Boolean = false): ClusteringParams {
    var bestParams = ClusteringParams(data.numInstances(), Double.MAX_VALUE)
    val distances = getDistanceMatrix(data)


    for (clustersAmount in 1..data.numInstances()) {
        val clusterer = HierarchicalClusterer()
        clusterer.options = Utils.splitOptions("$options -N $clustersAmount")
        clusterer.buildClusterer(Instances(data))
        val estimate = estimateClusterer(clusterer, data, distances)
        val currentParams = ClusteringParams(clustersAmount, estimate)

        if (debug) {
            println(currentParams)
        }

        if (bestParams.isWorse(currentParams)) {
            bestParams = currentParams
        }
    }

    if (debug) {
        println("Best params is: ${bestParams.toString()}")
    }

    return bestParams
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

    val header1 = QuotesHeaderSuggestions.getQuoteHeader(content1)
    val header2 = QuotesHeaderSuggestions.getQuoteHeader(content2)

    return when {
        header1 == null && header2 == null -> 0
        header1 == null || header2 == null -> Int.MAX_VALUE
        else -> getEditDistance(header1, header2)
    }
}