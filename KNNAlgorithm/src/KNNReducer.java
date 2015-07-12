import java.util.ArrayList;

import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KNNReducer extends Reducer<Text, Vector2SF, Text, Text> {

    protected void reduce(
            Text key,
            java.lang.Iterable<Vector2SF> value,
            org.apache.hadoop.mapreduce.Reducer<Text, Vector2SF, Text, Text>.Context context)
            throws java.io.IOException, InterruptedException {
        Map<String,Integer> votes = new LinkedHashMap<String,Integer>();
        Map<String,String> results = new HashMap<String,String>();
        Map<String,Float> comparison = new HashMap<String,Float>();
        for (Vector2SF v : value) {
        	String lab = v.getV3();
        	if(votes.containsKey(lab)){
        		votes.put(lab, votes.get(lab)+1 );
        		results.put(lab, v.getV1());
        		comparison.put(lab, v.getV2());
        	}
        	else{
        		votes.put(v.getV3(), 1);
        		results.put(lab, v.getV1());
        		comparison.put(lab, v.getV2());
        	}
        }
        
        List<Map.Entry<String,Integer>> voteList =
    	        new LinkedList<Map.Entry<String,Integer>>( votes.entrySet() );
	    Collections.sort( voteList, new Comparator<Map.Entry<String,Integer>>()
	    {
	        public int compare( Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2 )
	        {
	            return (o2.getValue()).compareTo( o1.getValue() );
	        }
	    });
	    
	    int curCount = 0;
	    String curLabel = "";
	    ArrayList<String> candidates = new ArrayList<String>();
	    for(Map.Entry<String,Integer> vt: voteList){
	    	if(vt.getValue() < curCount)
	    		break;
	    	curLabel = vt.getKey();
	    	curCount = vt.getValue();
	    	candidates.add(curLabel);
	    }
	    String outputResult = "";
    	String label = candidates.get(0);
    	outputResult = results.get(label) + "," + label;
	    
        context.write(new Text(outputResult), new Text(""));
    }

}