package practice.email.executable

import practice.email.clustering.getBestClusteringParams
import weka.clusterers.ClusterEvaluation
import weka.clusterers.HierarchicalClusterer
import weka.core.Instances
import weka.core.Utils
import weka.core.converters.ArffLoader
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val options = "-L SINGLE -A \"practice.email.clustering.TokenEditDistanceFunction -D -R first\" -B"
    val loader = ArffLoader()

    loader.setSource(File("test.arff"))
    val data: Instances = loader.dataSet

    val params = getBestClusteringParams(data, options, debug = true)

    // Instantiate clusterer
    val clusterer = HierarchicalClusterer()
    clusterer.options = Utils.splitOptions("$options -N ${params.clustersAmount}")
    clusterer.buildClusterer(data)

    // Evaluate clusterer
    val eval = ClusterEvaluation()
    eval.setClusterer(clusterer)
    eval.evaluateClusterer(Instances(data))
    println(eval.clusterResultsToString())

    // Clustering data
    val clusters = Array<MutableList<String>>(clusterer.numClusters) { ArrayList<String>() }

    data.forEach {
        clusters[clusterer.clusterInstance(it)].add(it.toString())
    }
    clusters.forEachIndexed { clusterIndex, clusterList ->
        println (
                clusterList.joinToString(
                        prefix = "Cluster $clusterIndex:\n",
                        separator = "\n",
                        postfix = "\n"
                )
        )
    }
}