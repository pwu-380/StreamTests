/**
 * Created by Peter on 9/7/2017.
 * Purpose is to test fetching an ARFF stream in Java and then sorting it into buffers
 */

import weka.core.Instance;                  //Note: These are WEKA instances
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.util.HashMap;

public class Test {
    /*User Defined Constants-----------------------------------------*/
    //Attributes 0)date, 1)yyyy, 2)mm, 3)dd, 4)age, 5)job, 6)marital, 7)education, 8)default, 9)housing, 10)loan,
    //1)contact, 12)month, 13)day_of_week, 14)duration, 15)campaign, 16)pdays, 17)previous, 18)poutcome,
    //19)emp.var.rate, 20)cons.price.idx, 21)cons.conf.idx, 22)euribor3m, 23)nr.employed, 24)y
    private static final String FILEDIR = "G:\\Documents\\Sept 17-Dec 17\\Directed Project\\Data" +
            "\\bank-additional-testingtruncated.arff";

    //Define number of elements saved in each buffer
    private static final int BUFFERSIZE = 20;

    //Define the number of classes
    private static final int NUMCLASSES = 2;
    //Define a mapping of each class to a buffer
    private static final HashMap CLASSES =
            new HashMap<String, Integer>() {{put("no", 0); put("yes", 1);}};
    //Needs to be of the same type as above hashkeys
    private static String classValue = "";

    public static void main(String[] args) throws Exception{
        //Load the file
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(FILEDIR));

        Instances data = loader.getStructure();
        Instances[] buffer = new Instances[NUMCLASSES];

        //Initialize buffers
        for (int i = 0; i < NUMCLASSES; i++){
            buffer[i] = new Instances(data,0);
        }

        data.setClassIndex(data.numAttributes() - 1);

        Instance current;
        while ((current = loader.getNextInstance(data)) != null) {
            classValue = current.toString(current.numAttributes()-1);
            buffer[(int) CLASSES.get(classValue)].add(current);
        }

        System.out.println("\nClass no:");
        for (int i = 0; i < buffer[0].numInstances(); i++){
            System.out.println(buffer[0].instance(i));
        }

        System.out.println("\nClass yes:");
        for (int i = 0; i < buffer[1].numInstances(); i++){
            System.out.println(buffer[1].instance(i));
        }
    }
}
