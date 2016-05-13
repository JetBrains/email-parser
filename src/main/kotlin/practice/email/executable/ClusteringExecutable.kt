package practice.email.executable

import practice.email.clustering.TokenEditDistance
import practice.email.clustering.getBestClusteringParams
import weka.clusterers.ClusterEvaluation
import weka.clusterers.HierarchicalClusterer
import weka.core.Instances
import weka.core.Utils
import java.io.*

fun main(args: Array<String>) {
    val options = "-L SINGLE -A \"practice.email.clustering.TokenEditDistance -D -R first\" -B"
    val datasetFilename = "test.arff"

    val params =
            getBestClusteringParams(File(datasetFilename), options, true)

    // Instantiate clusterer
    val clusterer = HierarchicalClusterer()
    clusterer.options = Utils.splitOptions("$options -N ${params.clustersAmount}")

    val datafile = readDataFile("test.arff")
    val data = Instances(datafile)

    // Cluster network
    clusterer.buildClusterer(data)

    val eval = ClusterEvaluation()
    eval.setClusterer(clusterer)
    eval.evaluateClusterer(Instances(data))
    println(eval.clusterResultsToString())

    for (i in 0..data.numInstances() - 1) {
        println(data.instance(i))
        System.out.printf(" -> Cluster %d \n", clusterer.clusterInstance(data.instance(i)))
    }

}

fun readDataFile(filename: String): BufferedReader? {
    var inputReader: BufferedReader? = null
    try {
        inputReader = BufferedReader(FileReader(filename))
    } catch (ex: FileNotFoundException) {
        System.err.println("File not found: " + filename)
    }

    return inputReader
}