/**
 * Created by Peter on 9/22/2017.
 * Starting to break things out into their own classes
 */

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.classifiers.Classifier;
import moa.classifiers.bayes.NaiveBayesMultinomial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestPrequential2 {

    /*User Defined Constants-----------------------------------------*/
    public static int PROBE_INSTANCES = 1;
    public static boolean BALANCE_PROBE_SET = false;
    public static int STREAM_SIZE = 150;

    //First 7 attributes correspond to LED lights, next 17 are noise attributes and last is class
    //Named 'att1', 'att2', 'att3'..., 'class'
    private static NewLEDGenerator STREAM = new NewLEDGenerator(0, 0);

    //Define number of elements saved in each buffer
    private static final int BUFFER_SIZE = 5;

    //Sliding window size for prequential window;
    private static final int PREQUENTIAL_WINDOW_SIZE = 50;

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

    private static Classifier clf = new NaiveBayesMultinomial();

    /*Program-----------------------------------------*/

    //Adds new instance to buffers, if there exists at least one element in each buffer, then train
    private static boolean handleTrainingCandidate (Classifier clf, InstanceBuffer instanceBuffer, Instance newInstance){
        double classValue = newInstance.classValue();
        boolean modelUpdated = false;

        instanceBuffer.addInstance(newInstance);

        if (instanceBuffer.existsSampleInAllClasses()){
            Instance[] samples = instanceBuffer.removeHead();
            for (int i = 0; i < samples.length; i++){
                clf.trainOnInstance(samples[i]);
            }
            modelUpdated = true;
        }

        return modelUpdated;
    }

    /*Main-----------------------------------------*/
    public static void main(String[] args) {
        //Initialize the stream
        STREAM.prepareForUse();

        //Get the stream attributes
        InstancesHeader header = STREAM.getHeader();
        int numclasses = header.numClasses();

        //Initialize the classifier
        clf.setModelContext(header);
        clf.prepareForUse();

        //Initialize buffers
        InstanceBuffer instanceBuffer = new InstanceBuffer(header, CLASSES, BUFFER_SIZE);

        //Trains classifier on a probe set
        int num_trained = 0;
        boolean trained = false;
        Instance current_inst;
        while (((current_inst = STREAM.nextInstance().getData()) != null) && (num_trained < PROBE_INSTANCES)) {
            //Uses buffering mechanism to balance probe set if desired
            if (BALANCE_PROBE_SET) {
                //Adds new instance to buffers, if there exists at least one element in each buffer, then train
                trained = handleTrainingCandidate(clf, instanceBuffer, current_inst);
                if (trained) {
                    num_trained += numclasses;
                    trained = false;
                }
             //Otherwise just train on whatever instance comes out
            } else {
                clf.trainOnInstance(current_inst);
                num_trained++;
            }
        }

        instanceBuffer.emptyBuffers();

        //Begins prequential test than train
        PredictionMatrix predictionMatrix = new PredictionMatrix(clf, CLASSES, PREQUENTIAL_WINDOW_SIZE);
        int[][] matrix;

        num_trained = 0;
        while (((current_inst = STREAM.nextInstance().getData()) != null) && (num_trained < STREAM_SIZE)){
            //Predict and score
            predictionMatrix.predictUpdate(current_inst);

            //Add to training buffers
            handleTrainingCandidate(clf, instanceBuffer, current_inst);
            num_trained++;

            if (num_trained == 50){
                matrix = predictionMatrix.getMatrix();
                System.out.println("    0  1  2  3  4  5  6  7  8  9");
                for (int i = 0; i < matrix.length; i++){
                    System.out.println(i + ": " + Arrays.toString(matrix[i]));
                }
                System.out.println("\n");
            }
        }

        instanceBuffer.emptyBuffers();
        matrix = predictionMatrix.getMatrix();

        //Prints all the predictions
        System.out.println("    0  1  2  3  4  5  6  7  8  9");
        for (int i = 0; i < matrix.length; i++){
            System.out.println(i + ": " + Arrays.toString(matrix[i]));
        }
    }
}
