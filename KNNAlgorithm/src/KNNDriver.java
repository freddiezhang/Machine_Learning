import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class KNNDriver extends Configured implements Tool{

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new KNNDriver(), args);
        System.exit(res);
    }
	
    public int run(String[] args) throws Exception {
    	Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
          System.err.println("Usage: KNNClassifier <in> <out> k");
          System.exit(2);
        }
        Job job = Job.getInstance(conf,"KNN Classfier");
        job.setJarByClass(KNNDriver.class);

        job.setMapperClass(KNNMapper.class);
        job.setReducerClass(KNNReducer.class);
        job.setCombinerClass(KNNCombiner.class);
        job.setOutputKeyClass(Text.class);     
        job.setMapOutputValueClass(Vector2SF.class);
        
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(KNNInputFormat.class);
  
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        Path out = new Path(otherArgs[1]);
        FileSystem.get(conf).delete(out, true);
        FileOutputFormat.setOutputPath(job, out);
       
        int res = job.waitForCompletion(true) ? 0 : 1;
        if (res != 0) {
            return res;
        }
		return 0;
    }
    
}