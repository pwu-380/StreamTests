/**
 * Created by Peter on 9/7/2017.
 * Testing stagger
 */

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import java.util.HashMap;

public class Test4 {
    /*User Defined Constants-----------------------------------------*/
    //Attributes 0)size 1)color 2)shape 4)class
    //Target Concept 1 : size = small AND color = red
    //Target Concept 2 : color = green OR shape = circular
    //Target Concept 3 : size = medium OR size = large
    //Only one of the target concepts is active at any time
    private static NewSTAGGERGenerator STREAM = new NewSTAGGERGenerator();
    public static int STREAM_SIZE = 20;

    //Define number of elements saved in each buffer
    private static final int BUFFER_SIZE = 50;

    //Define a mapping of each class to a buffer
    private static final HashMap CLASSES =
            new HashMap<Double, Integer>() {{
                put(0.0, 0);
                put(1.0, 1);
            }};
    //Needs to be of the same type as above hashkeys
    private static String classValue = "";

    public static void main(String[] args) throws Exception{
        STREAM.prepareForUse();
        STREAM.setClassBalance(true);
        STREAM.setConcept(2);

        //Get the stream attributes
        InstancesHeader header = STREAM.getHeader();
        int numclasses = header.numClasses();

        //Initialize buffer manager
        InstanceBuffer instanceBuffer = new InstanceBuffer(header, CLASSES, BUFFER_SIZE);

                int num_instances = 0;
        Instance newInstance;
        while (((newInstance = STREAM.nextInstance().getData()) != null) && (num_instances < STREAM_SIZE)){
            instanceBuffer.addInstance(newInstance);
            num_instances++;
        }

        Instances[] buffer_copies = buffer_copies = instanceBuffer.getBuffers();

        for (int i = 0; i < buffer_copies.length; i++){
            System.out.println(String.format("\nClass number %d", i));
            for (int j = 0; j < buffer_copies[i].numInstances(); j++) {
                System.out.println(buffer_copies[i].instance(j));
            }
        }
    }
}
