/**
 * Created by Peter on 9/21/2017.
 */

import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.core.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PredictionMatrix {
    private Classifier model;
    private int numClasses;
    private HashMap classes;

    private boolean windowed = false;
    private int windowLength = 0;

    private Queue<Integer[]> window;
    private int matrix[][];

    public PredictionMatrix(Classifier clf, HashMap classes){
        this(clf, classes, 0);
    }

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

    public void predictUpdate (Instance instance){
        //THIS MAY BREAK LATER, CROSS THAT BRIDGE WHEN I GET TO IT
        int actualClass = (int) classes.get(instance.classValue());
        int predictedClass = Utils.maxIndex(model.getVotesForInstance(instance));
        Integer[] pair = {actualClass, predictedClass};

        if (windowed){
            window.add(pair);
            if (window.size() > windowLength){
                Integer[] removed = window.remove();
                matrix[removed[0]][removed[1]]--;
            }
        }

        matrix[actualClass][predictedClass]++;
    }

    public int[][] getMatrix(){
        return matrix;
    }

    public String toString (){
        return  "I need to implenment this";
    }
}
