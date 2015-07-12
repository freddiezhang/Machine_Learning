import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class inspect {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File in = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(in));
		String doc;
		String[] firstLine = br.readLine().trim().split(",");
		String[] attrName = Arrays.copyOfRange(firstLine, 0, firstLine.length-1);
		String labelName = firstLine[firstLine.length-1];
		//System.out.println(labelName);
		//ArrayList<Instance> instances = new ArrayList<Instance>();
		HashMap<String,Integer> labels= new HashMap<String,Integer>();
		int most = 0;
		String labelMost = "";
		int attrCount = 0;
		while((doc = br.readLine())!= null){
			String[] attrValue = doc.split(",");
			String label = attrValue[attrValue.length-1];
			if(labels.containsKey(label)){
				int count = labels.get(label);
				count += 1;
				labels.put(label, count);
				if(most<count){
					most = count;
					labelMost = label;
				}
			}
			else
				labels.put(label, 1);
			attrCount ++;
			//System.out.println(attrValue[0]);
		}
		br.close();
		Entropy entro = new Entropy();
		double entropy = entro.calEntropy(most, attrCount - most);
		double error = (attrCount - most) / (attrCount*1.0);
		System.out.printf("entropy: %.3f\n",entropy);
		System.out.printf("error: %.2f\n",error);
	}

}
