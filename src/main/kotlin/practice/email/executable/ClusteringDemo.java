package practice.email.executable;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.core.EditDistance;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;
import weka.clusterers.XMeans;
import weka.core.Instances;
import weka.core.Utils;

public class ClusteringDemo
{
    public static BufferedReader readDataFile(String filename)
    {
        BufferedReader inputReader = null;
        try
        {
            inputReader = new BufferedReader(new FileReader(filename));
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("File not found: " + filename);
        }
        return inputReader;
    }

    public static void main(String[] args) throws Exception
    {
        String optionsLine = "-I 10 -M 1000 -J 1000 -L 1 -H 100 -B 1.0 -C 0.5 -D \"practice.email.clustering.TokenEditDistance -D -R first\" -S 10";

        String[] options = Utils.splitOptions(optionsLine);
        XMeans xmean = new XMeans();
        xmean.setOptions(options);

        BufferedReader datafile = readDataFile("test.arff");
        Instances data = new Instances(datafile);
                
        xmean.buildClusterer(data);
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(xmean);
        eval.evaluateClusterer(new Instances(data));
        System.out.println(eval.clusterResultsToString());
        for(int i=0;i<data.numInstances();i++)
        {
            System.out.println(data.instance(i));
            System.out.printf(" -> Cluster %d \n", xmean.clusterInstance(data.instance(i)));
        }
    }
} 
