/**
 * Created by Peter on 9/21/2017.
 * This class maintains a windowed confusion matrix (actual class v. predicted class) incrementally for a set of
 * test samples
 */

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.core.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PredictionMatrix {
    private Classifier model;           //Trained model
    private int numClasses;             //Number of different classes
    private HashMap classes;            //Mapping of class values to indices

    private boolean windowed = false;   //Without a sliding window it just records all test results
    private int windowLength = 0;       //Width of sliding window

    private Queue<Integer[]> window;    //Test results for all samples in the window
    private int matrix[][];             //"Confusion matrix"

    //Constructor for non-windowed matrix
    public PredictionMatrix(Classifier clf, HashMap classes){
        this(clf, classes, 0);
    }

    //Constructor for windowed matrix
    public PredictionMatrix(Classifier clf, HashMap classes, int windowLength){
        this.model = clf;
        numClasses = classes.size();
        this.classes = classes;

        if (windowLength > 0) {
            this.windowLength = windowLength;
            windowed = true;
            window = new LinkedList<>();
        }

        matrix =  new int[numClasses][numClasses];
    }

    //Tests a single instance and records results
    public void predictUpdate (Instance instance){
        int actualClass = (int) classes.get(instance.classValue());
        int predictedClass = Utils.maxIndex(model.getVotesForInstance(instance));
        Integer[] pair = {actualClass, predictedClass};

        //If windowed, whenever a new prediction is made, the oldest is removed from the matrix
        if (windowed){
            window.add(pair);
            if (window.size() > windowLength){
                Integer[] removed = window.remove();
                matrix[removed[0]][removed[1]]--;
            }
        }

        //Updates matrix
        matrix[actualClass][predictedClass]++;
    }

    //Returns confusion matrix
    public int[][] getMatrix(){
        return matrix;
    }

    public String toString (){
        return  "I need to implenment this";
    }
}
