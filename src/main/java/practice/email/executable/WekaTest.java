package practice.email.executable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import practice.email.clustering.TokenEditDistance;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Instances;


public class WekaTest {

    static HierarchicalClusterer clusterer;

    public static BufferedReader readDataFile(String filename) {
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException ex) {
            System.err.println("File not found: " + filename);
        }
        return inputReader;
    }

    public static void main(String[] args) throws Exception {
        // Instantiate clusterer
        clusterer = new HierarchicalClusterer();
        clusterer.setOptions(new String[]{"-L", "SINGLE"});
        clusterer.setNumClusters(5);
        clusterer.setDistanceFunction(new TokenEditDistance());
        clusterer.setDistanceIsBranchLength(true);

        BufferedReader datafile = readDataFile("test.arff");
        Instances data = new Instances(datafile);

        // Cluster network
        clusterer.buildClusterer(data);

        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.evaluateClusterer(new Instances(data));
        System.out.println(eval.clusterResultsToString());

        for (int i = 0; i < data.numInstances(); i++) {
            System.out.println(data.instance(i));
            System.out.printf(" -> Cluster %d \n", clusterer.clusterInstance(data.instance(i)));
        }
    }

}