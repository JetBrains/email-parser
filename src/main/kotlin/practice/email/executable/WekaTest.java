package practice.email.executable;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EditDistance;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.gui.hierarchyvisualizer.HierarchyVisualizer;


public class WekaTest {

    static HierarchicalClusterer clusterer;
    static Instances data;

    public static void main(String[] args) throws Exception {
        // Instantiate clusterer
        clusterer = new HierarchicalClusterer();
        clusterer.setOptions(new String[]{"-L", "COMPLETE"});
        clusterer.setNumClusters(3);
        clusterer.setDistanceFunction(new EditDistance());
        clusterer.setDistanceIsBranchLength(true);

        // Build dataset
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute("A", (ArrayList<String>) null));
        data = new Instances("Weka test", attributes, 7);

        // Add data
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("aaa")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("aas")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("sss")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("ddd")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("ddd")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("asd")}));
        data.add(new DenseInstance(1.0, new double[]{data.attribute(0).addStringValue("aas")}));

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