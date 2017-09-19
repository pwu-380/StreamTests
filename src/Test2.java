/**
 * Created by Peter on 9/8/2017.
 * Purpose is to test MOA's stream generators
 */

import com.yahoo.labs.samoa.instances.Instance;     //Note: These are MOA instances
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Attribute;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Test2 {
    /*User Defined Constants-----------------------------------------*/
    //First 7 attributes correspond to LED lights, next 17 are noise attributes and last is class
    //Named 'att1', 'att2', 'att3'..., 'class'
    private static newLEDGenerator STREAM = new newLEDGenerator(17, 10);
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

    public static void main(String[] args) {
        //Initialize the stream
        STREAM.prepareForUse();

        //Get the stream attributes
        InstancesHeader header = STREAM.getHeader();

        List<Attribute> streamAttributes = new ArrayList<>();
        for (int i = 0; i < header.numAttributes(); i++){
            streamAttributes.add(header.attribute(i));
        }

        //Initialize buffers
        Instances[] buffer = new Instances[NUMCLASSES];
        for (int i = 0; i < NUMCLASSES; i++){
            buffer[i] = new Instances("buffer", streamAttributes, BUFFERSIZE);
        }

        //Adds instances to buffers
        int count = 0;
        int buffer_ind = 0;
        Instance current;
        while (((current = STREAM.nextInstance().getData()) != null) && (count < STREAMSIZE)) {
            classValue = current.classValue();
            buffer_ind = (int) CLASSES.get(classValue);

            if (buffer[buffer_ind].numInstances() == BUFFERSIZE){
                buffer[buffer_ind].delete(0);
            }

            buffer[buffer_ind].add(current);
            count++;
        }

        //Print out the contents of each buffer
        for (int i = 0; i < buffer.length; i++){
            System.out.println(String.format("\nClass number %d", i));
            for (int j = 0; j < buffer[i].numInstances(); j++) {
                System.out.println(buffer[i].instance(j));
            }
        }
    }
}
