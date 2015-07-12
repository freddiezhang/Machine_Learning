import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NN_music {
	static int HIDDENNUM = 3;
	static int INPUTNUM = 4;
	static int TRAINNUM = 3000;
	static double[][] hiddenWeights= new double[HIDDENNUM][INPUTNUM];
	static double[] outputWeights = new double[HIDDENNUM];
	static double trainRate = 0.01;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		for(int i=0;i<HIDDENNUM;i++){
			for(int j=0;j<INPUTNUM;j++){
				hiddenWeights[i][j] = Math.random();
			}
			outputWeights[i] = Math.random();
		}
		File in = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = new String();
		br.readLine();
		ArrayList<Double[]> datasets = new ArrayList<Double[]>();
		while((line = br.readLine())!=null){
			String[] inputs = line.trim().split(",");
			Double[] normalInputs = normalize(inputs);
			datasets.add(normalInputs);
		}
		br.close();
		ArrayList<Double[]> outputs = new ArrayList<Double[]>();
		for(int i=0;i<100;i++){
			outputs.add(new Double[HIDDENNUM+1]);
		}
		double curError = 1000000;
		double nextError = updateOutput(datasets, outputs);
		System.out.println(nextError);
		int count = 0;
		while( count < TRAINNUM && (curError-nextError) >= 0.0001){
			//System.out.println(nextError);
			curError = nextError;
			BP(datasets,outputs);
			nextError = updateOutput(datasets, outputs);
			System.out.println(nextError);
			count++;
		}
		System.out.println("TRAINING COMPLETED! NOW PREDICTING.");
		File inTest = new File(args[1]);
		br = new BufferedReader(new FileReader(inTest));
		br.readLine();
		ArrayList<Double[]> testInputs = new ArrayList<Double[]>();
		while((line = br.readLine())!=null){
			String[] inputs = line.trim().split(",");
			Double[] normalInputs = normalizeTest(inputs);
			testInputs.add(normalInputs);
		}
//		for(int i=0;i<HIDDENNUM;i++){
//			System.out.println(outputWeights[i]);
//		}
		br.close();
		double test = 0.0;
		for(Double[] tt:testInputs){
			test = outNetwork(tt);
			test = Math.floor(test + 0.5);
			if(test<1)
				System.out.println("no");
			else 
				System.out.println("yes");
		}
	}
	
	static Double[] normalize(String[] in){
		Double[] line = new Double[5];
		line[0] = (Double.parseDouble(in[0])-1900) / 100;
		line[1] = Double.parseDouble(in[1]) / 7;
		line[2] = new Double(in[2].equals("yes")?1:0);
		line[3] = new Double(in[3].equals("yes")?1:0);
		line[4] = new Double(in[4].equals("yes")?1:0);
		return line;
	}
	static Double[] normalizeTest(String[] in){
		Double[] line = new Double[4];
		line[0] = (Double.parseDouble(in[0])-1900) / 100;
		line[1] = Double.parseDouble(in[1]) / 7;
		line[2] = new Double(in[2].equals("yes")?1:0);
		line[3] = new Double(in[3].equals("yes")?1:0);
		return line;
	}
	static double outNetwork(Double[] in){
		double[] hiddenTest = new double[HIDDENNUM];
		double outTest = 0.0;
		for(int i=0;i<HIDDENNUM;i++){
			double net = 1.0;
			for(int j=0;j<INPUTNUM;j++){
				net+=hiddenWeights[i][j]* (double)in[j];
			}
			hiddenTest[i] = 1/(1+Math.exp(-net));
		}
		double netOut = 1.0;
		for(int i=0;i<HIDDENNUM;i++){
			netOut+=outputWeights[i]*hiddenTest[i];
		}
		outTest = 1/(1+Math.exp(-netOut));
		return outTest;
	}
	static double updateOutput(ArrayList<Double[]> inputs, ArrayList<Double[]> outputs){
		double squaredErr = 0.0;
		for(int k=0;k<100;k++){
			for(int i=0;i<HIDDENNUM;i++){
				double net = 1.0;
				for(int j=0;j<INPUTNUM;j++){
					net+=hiddenWeights[i][j]*inputs.get(k)[j];
				}
				outputs.get(k)[i] = 1/(1+Math.exp(-net));
			}
			double outNet = 1.0;
			for(int i=0;i<HIDDENNUM;i++){
				outNet+=outputWeights[i]*outputs.get(k)[i];
			}
			double networkOut = 1/(1+Math.exp(-outNet));
			outputs.get(k)[HIDDENNUM] = networkOut;
			double abb = inputs.get(k)[INPUTNUM]-networkOut;
			squaredErr += 0.5 * abb * abb;
		}
		return squaredErr;
	}
	static void BP(ArrayList<Double[]> inputs, ArrayList<Double[]> outputs){
		double deltaK = 0.0;
		double[] deltaH;
		double[][] deltaHidden= new double[HIDDENNUM][INPUTNUM];
		double[] deltaOut = new double[HIDDENNUM];
		for(int k=0;k<100;k++){
			double outputK = outputs.get(k)[HIDDENNUM];
			deltaK = outputK * (1 - outputK) * (inputs.get(k)[INPUTNUM] - outputK );
			deltaH = new double[HIDDENNUM];
			for(int i=0;i<HIDDENNUM;i++){
				deltaH[i] = outputs.get(k)[i] * (1 - outputs.get(k)[i]) * outputWeights[i] * deltaK;
			}
			for(int i=0;i<HIDDENNUM;i++){
				for(int j=0;j<INPUTNUM;j++){
					deltaHidden[i][j] = deltaHidden[i][j] + trainRate*deltaH[i]* inputs.get(k)[j];
				}
				deltaOut[i] = deltaOut[i] + trainRate * deltaK * outputs.get(k)[i];
			}
		}
		for(int i=0;i<HIDDENNUM;i++){
			for(int j=0;j<INPUTNUM;j++){
				hiddenWeights[i][j] = hiddenWeights[i][j] + deltaHidden[i][j];
			}
			outputWeights[i] = outputWeights[i] + deltaOut[i];
		}
	}
}
