/**
 * Created by Peter on 9/22/2017.
 */

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstanceBuffer {

    private int bufferSize;
    private HashMap classes;
    private int numBuffers;
    private Instances[] buffers;

    public InstanceBuffer(InstancesHeader streamHeader, HashMap classes){
        this(streamHeader,classes, 20);
    }

    public InstanceBuffer(InstancesHeader streamHeader, HashMap classes, int bufferSize){
        if (bufferSize < 2){
            throw new IllegalArgumentException("Invalid buffer parameters passed");
        }

        this.bufferSize = bufferSize;
        this.classes = classes;
        numBuffers = streamHeader.numClasses();
        buffers = new Instances[numBuffers];

        List<Attribute> streamAttributes = new ArrayList<>();
        for (int i = 0; i < streamHeader.numAttributes(); i++){
            streamAttributes.add(streamHeader.attribute(i));
        }

        for (int i = 0; i < numBuffers; i++){
            buffers[i] = new Instances("buffer" + i, streamAttributes, bufferSize);
        }
    }

    public void emptyBuffers(){
        for (int i = 0; i < buffers.length; i++){
            buffers[i].delete();
        }
    }

    public boolean existsSampleInAllClasses(){
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

    public void addInstance(Instance instance){
        int buffer_ind = (int) classes.get(instance.classValue());

        if (buffers[buffer_ind].numInstances() == bufferSize){
            buffers[buffer_ind].delete(0);
        }

        buffers[buffer_ind].add(instance);
    }

    public Instance[] removeHead(){
        Instance[] head = new Instance[numBuffers];
        for (int i = 0; i < numBuffers; i++){
            try {
                head[i] = buffers[i].instance(0);
                buffers[i].delete(0);
            } catch (IndexOutOfBoundsException e){
                System.out.println("Warning: Buffer " + i + " was empty [InstanceBuffer]");
            }
        }
        return head;
    }

    public Instances[] getBuffers (){
        return buffers;
    }

    public String toString (){
        return "I need to implement this";
    }

}
