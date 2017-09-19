/**
 * Created by Peter on 9/17/2017.
 * Purpose is to test using MOA's incremenatal classifiers
 */
import com.yahoo.labs.samoa.instances.*;
import moa.classifiers.Classifier;
import moa.classifiers.bayes.NaiveBayesMultinomial;
import moa.core.Utils;
import moa.streams.InstanceStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Test3 {
    /*User Defined Constants-----------------------------------------*/
    //First 7 attributes correspond to LED lights, next 17 are noise attributes and last is class
    //Named 'att1', 'att2', 'att3'..., 'class'
    private static newLEDGenerator STREAM = new newLEDGenerator(0, 0);     //Parameters set true to UCI's original set
    //Number of instances to be generated
    private static final int STREAMSIZE = 100;

    //Define number of elements saved in each buffer
    private static final int BUFFERSIZE = 5;

    //Define the number of classes
    private static final int NUMCLASSES = 10;
    //Define a mapping of each class to a buffer
    //The key is what is returned when we check Instance.classValue
    private static final HashMap CLASSES =
            new HashMap<Double, Integer>() {{
                put(0.0, 0);
                put(1.0, 1);
                put(2.0, 2);
                put(3.0, 3);
                put(4.0, 4);
                put(5.0, 5);
                put(6.0, 6);
                put(7.0, 7);
                put(8.0, 8);
                put(9.0, 9);
            }};
    //Needs to be of the same type as above hashkeys
    private static double classValue = 0;

    private static int NUMTESTS = 40;

    private static Classifier clf = new NaiveBayesMultinomial();


    /*Program-----------------------------------------*/
    private static boolean existsSufficientExamples(Instances[] buffers){
        boolean sufficientExamples = true;

        //Check if each class buffer has at least one example
        for (int i = 0; i < buffers.length; i++){
            if (buffers[i].numInstances() == 0){
                sufficientExamples = false;
                break;
            }
        }
        return sufficientExamples;
    }

    //Trains a classifier if all the buffers have an example, and then removes them from the buffers
    private static void trainClassifier (Classifier clf, Instances[] instanceQueues){
        for (int i = 0; i < instanceQueues.length; i++){
            clf.trainOnInstance(instanceQueues[i].instance(0));
            instanceQueues[i].delete(0);
        }
    }

    //Returns the likeliest predicted class
    private static int predictMaxClass (Classifier clf, Instance instance){
        //This was easier than I expected..
        int maxClass = Utils.maxIndex(clf.getVotesForInstance(instance));
        return maxClass;
    }

    //Creates a prediction matrix (rows are actual class, columns are predicted class)
    private static int[][] buildPredictionMatrix(Classifier clf, InstanceStream stream, int numtests){
        int[][] matrix = new int[NUMCLASSES][NUMCLASSES];
        int predictedClass;
        int actualClass;
        Instance instance;

        for (int i = 1; i < numtests; i++){
            instance = stream.nextInstance().getData();
            predictedClass = predictMaxClass(clf, instance);
            actualClass = (int) CLASSES.get(instance.classValue());
            matrix[actualClass][predictedClass]++;
        }

        return matrix;
    }

    //Trains incrementally on almost 100 examples, and then tests on 10.
    public static void main(String[] args) {
        //Initialize the stream
        STREAM.prepareForUse();

        //Get the stream attributes
        InstancesHeader header = STREAM.getHeader();

        List<Attribute> streamAttributes = new ArrayList<>();
        for (int i = 0; i < header.numAttributes(); i++){
            streamAttributes.add(header.attribute(i));
        }

        //Initialize the classifier
        clf.setModelContext(header);
        clf.prepareForUse();

        //Initialize buffers
        Instances[] buffers = new Instances[NUMCLASSES];
        for (int i = 0; i < NUMCLASSES; i++){
            buffers[i] = new Instances("buffer", streamAttributes, BUFFERSIZE);
        }

        //Adds instances to buffers
        int count = 0;
        int buffer_ind = 0;
        Instance current;
        while (((current = STREAM.nextInstance().getData()) != null) && (count < STREAMSIZE)) {
            classValue = current.classValue();
            buffer_ind = (int) CLASSES.get(classValue);

            if (buffers[buffer_ind].numInstances() == BUFFERSIZE){
                buffers[buffer_ind].delete(0);
            }

            buffers[buffer_ind].add(current);
            count++;

            if (existsSufficientExamples(buffers)){
                trainClassifier(clf, buffers);
            }
        }

        int[][] predictionMatrix = buildPredictionMatrix(clf, STREAM, NUMTESTS);

        //Prints all the predictions
        System.out.println("    0  1  2  3  4  5  6  7  8  9");
        for (int i = 0; i < predictionMatrix.length; i++){
            System.out.println(i + ": " + Arrays.toString(predictionMatrix[i]));
        }

    }
}
