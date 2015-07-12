import org.apache.hadoop.mapreduce.RecordReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

public class KNNInputFormat extends FileInputFormat<Text, Text> {

    static class Reader extends RecordReader<Text, Text> {

        private Text key;
        private Text value;
        private final LineRecordReader r;
        private long start;

        public Reader() {
            r = new LineRecordReader();
        }

        @Override
        public void close() throws IOException {
            r.close();
        }

        @Override
        public Text getCurrentKey() throws IOException, InterruptedException {
            return key;
        }

        @Override
        public Text getCurrentValue() throws IOException,
                InterruptedException {
            return value;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            return r.getProgress();
        }

        @Override
        public void initialize(InputSplit split, TaskAttemptContext context)
                throws IOException, InterruptedException {
            r.initialize(split, context);
            FileSplit fs = (FileSplit) split;
            start = fs.getStart();
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            if (r.nextKeyValue()) {
                Text line = r.getCurrentValue();
                key = new Text(String.valueOf(start));
                value = line;
                start ++;
                return true;
            }
            return false;
        }
    }

    public static Vector2<String, SparseVector, String> readLine(long start, String line, String label) {
            // offset as ID
            String key = String.valueOf(start);
            // read value
            SparseVector value = new SparseVector();
            String[] valueStr = line.split(",");
            String newlabel = label;
            if (label == "label"){
            	for(int i = 1; i <= 4; i++)
            	{
            		value.put("base"+ String.valueOf(i), Float.parseFloat(valueStr[i-1]));
            	}
            	newlabel = valueStr[4];
            }
            else{
            	for(int i = 1; i <= 4; i++)
            	{
            		value.put("base"+ String.valueOf(i), Float.parseFloat(valueStr[i-1]));
            	}
            }
            return new Vector2<String, SparseVector, String>(key, value, newlabel);
        
    }

	@Override
	public RecordReader<Text, Text> createRecordReader(
            final InputSplit split, final TaskAttemptContext context) {
        return new Reader();
    }

}