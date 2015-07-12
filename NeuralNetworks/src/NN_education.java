import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class NN_education {
	static int TRAINSETNUM = 400;
	static int HIDDENNUM = 12;
	static int INPUTNUM = 5;
	static int TRAINNUM = 5000;
	static double[][] hiddenWeights= new double[HIDDENNUM][INPUTNUM];
	static double[] outputWeights = new double[HIDDENNUM];
	static double trainRate = 0.01;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		long beginTime = System.currentTimeMillis();
		for(int i=0;i<HIDDENNUM;i++){
			for(int j=0;j<INPUTNUM;j++){
				hiddenWeights[i][j] = Math.random();
			}
			outputWeights[i] = Math.random();
		}
		//initialize();
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
		for(int i=0;i<TRAINSETNUM;i++){
			outputs.add(new Double[HIDDENNUM+1]);
		}
		double curError = 1000000;
		double nextError = updateOutput(datasets, outputs);
		System.out.println(nextError);
		int count = 0;
		long difference = 0;
		while( nextError >= 0.009 && (curError-nextError) >= 0.00000000001 && difference < 179950){
			//System.out.println(nextError);
			curError = nextError;
			System.out.println(curError);
			BP(datasets,outputs);
			nextError = updateOutput(datasets, outputs);
			count++;
			difference = System.currentTimeMillis() - beginTime;
		}
		//System.out.println(count);
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
		double[] testResult = new double[50];
		int current = 0;
		for(Double[] tt:testInputs){
			test = outNetwork(tt);
			test = test * 100;
			testResult[current] = test;
			current ++;
			System.out.println(test);
		}
		current = 0;
//		br = new BufferedReader(new FileReader("education_dev_keys.txt"));
//		double[] testGold = new double[50];
//		while((line = br.readLine())!=null){
//			String inputs = line.trim();
//			testGold[current] = (double) Double.valueOf(inputs);
//			current ++;
//		}
//		br.close();
//		double errorCal = 0.0;
//		for(int i=0;i<50;i++){
//			errorCal += Math.abs(testGold[i] - testResult[i]) / 50;
//		}
//		System.out.println(errorCal);
//		difference = System.currentTimeMillis() - beginTime;
//		System.out.println(difference);
//		for(int i = 0;i< HIDDENNUM; i++){
//			for(int j=0;j<INPUTNUM;j++)
//				System.out.println(hiddenWeights[i][j]);
//		}
//		for(int i=0;i< HIDDENNUM; i++){
//			System.out.println(outputWeights[i]);
//		}
	}
	
	static Double[] normalize(String[] in){
		Double[] line = new Double[INPUTNUM+1];
		for(int i=0;i<in.length;i++){
			line[i] = Double.valueOf(in[i]) / 100;
		}
		return line;
	}
	static Double[] normalizeTest(String[] in){
		Double[] line = new Double[INPUTNUM];
		for(int i=0;i<in.length;i++){
			line[i] = Double.valueOf(in[i]) / 100;
		}
		return line;
	}
	static double outNetwork(Double[] in){
		double[] hiddenTest = new double[HIDDENNUM];
		double outTest = 0.0;
		for(int i=0;i<HIDDENNUM;i++){
			//double net = 1.0;
			double net = 0.0;
			for(int j=0;j<INPUTNUM;j++){
				net+=hiddenWeights[i][j]* (double)in[j];
			}
			hiddenTest[i] = 1/(1+Math.exp(-net));
		}
		//double netOut = 1.0;
		double netOut = 0.0;
		for(int i=0;i<HIDDENNUM;i++){
			netOut+=outputWeights[i]*hiddenTest[i];
		}
		outTest = 1/(1+Math.exp(-netOut));
		return outTest;
	}
	static double updateOutput(ArrayList<Double[]> inputs, ArrayList<Double[]> outputs){
		double squaredErr = 0.0;
		for(int k=0;k<TRAINSETNUM;k++){
			for(int i=0;i<HIDDENNUM;i++){
				//double net = 1.0;
				double net = 0.0;
				for(int j=0;j<INPUTNUM;j++){
					net+=hiddenWeights[i][j]*inputs.get(k)[j];
				}
				outputs.get(k)[i] = 1/(1+Math.exp(-net));
			}
			//double outNet = 1.0;
			double outNet = 0.0;
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
		for(int k=0;k<TRAINSETNUM;k++){
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
	
	static void initialize(){
		hiddenWeights[0][0] = 0.07887;
		hiddenWeights[0][1] = -0.045090;
		hiddenWeights[0][2] = -0.068392;
		hiddenWeights[0][3] = 0.41431;
		hiddenWeights[0][4] = 0.2543;
		hiddenWeights[1][0] = 6.03839;
		hiddenWeights[1][1] = 5.122415;
		hiddenWeights[1][2] = 4.389435;
		hiddenWeights[1][3] = 4.92824;
		hiddenWeights[1][4] = 4.228;
		hiddenWeights[2][0] = 0.07856;
		hiddenWeights[2][1] = 0.22893;
		hiddenWeights[2][2] = 0.121938;
		hiddenWeights[2][3] = -0.0372885;
		hiddenWeights[2][4] = 0.541707;
		hiddenWeights[3][0] = 0.07856;
		hiddenWeights[3][1] = 0.22893;
		hiddenWeights[3][2] = 0.121938;
		hiddenWeights[3][3] = -0.0372885;
		hiddenWeights[3][4] = 0.541707;
		hiddenWeights[4][0] = 0.07856;
		hiddenWeights[4][1] = 0.22893;
		hiddenWeights[4][2] = 0.121938;
		hiddenWeights[4][3] = -0.0372885;
		hiddenWeights[4][4] = 0.541707;
		
		outputWeights[0] = 5.1461;
		outputWeights[1] = 3.7319;
		outputWeights[2] = 4.211;
		outputWeights[3] = 4.709;
		outputWeights[4] = -11.17;
	}
}
