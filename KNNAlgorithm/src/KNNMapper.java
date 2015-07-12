import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class KNNMapper extends Mapper<Text, Text, Text, Vector2SF> {

    private Vector<Vector2<String, SparseVector, String>> test =
            new Vector<Vector2<String, SparseVector, String>>();

    protected void map(
            Text key,
            Text value,
            org.apache.hadoop.mapreduce.Mapper<Text, Text, Text, Vector2SF>.Context context)
            throws java.io.IOException, InterruptedException {
    	
    	String line = value.toString();
    	Vector2<String, SparseVector, String> testVal = KNNInputFormat.readLine(
    			Integer.parseInt(key.toString()), line, "");
        context.setStatus(key.toString());
        for (Vector2<String, SparseVector, String> testCase : test) {
            double d = testCase.getV2().euclideanDistance(testVal.getV2());
            context.write(key, new Vector2SF(line,
                    (float) d, testCase.getV3()));
        }

    }

    protected void setup(
            org.apache.hadoop.mapreduce.Mapper<Text, Text, Text, Vector2SF>.Context context)
            throws java.io.IOException, InterruptedException {
        FileSystem fs = FileSystem.get(context.getConfiguration());
        BufferedReader br = new BufferedReader(new FileReader("input/train/iris_train.csv"));
        String line = br.readLine();
        int count = 0;
        while (line != null) {
            Vector2<String, SparseVector, String> v = KNNInputFormat.readLine(count, line, "label");
            test.add(new Vector2<String, SparseVector, String>(v.getV1(), v.getV2(), v.getV3()));
            line = br.readLine();
            count++;
        }
        br.close();
    }

}