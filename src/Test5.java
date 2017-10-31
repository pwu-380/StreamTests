/**
 * Created by Peter on 10/30/2017.
 */
public class Test5 {
    public static void main(String[] args) {
        int matrix1[][] = {{1,2,3},{4,5,6},{7,8,9}};
        int matrix2[][] = {{1,2,3},{4,6,6},{7,8,9}};
        int matrix3[][] = {{9,8,7},{6,5,4},{3,2,1}};

        PerfSim detector = new PerfSim(matrix1);

        System.out.println(detector.testDrift(matrix2));
        System.out.println(detector.testDrift(matrix3));
    }
}
