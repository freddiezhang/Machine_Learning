import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class decisionTree {

	static ArrayList<Integer> lineIndex = new ArrayList<Integer>();
	static ArrayList<Instance> instances = new ArrayList<Instance>();
	static ArrayList<TreeNode> leaves = new ArrayList<TreeNode>();
	static HashMap<Integer,String> posAttrVal = new HashMap<Integer,String>();
	static HashMap<Integer,String> negAttrVal = new HashMap<Integer,String>();
	static ArrayList<Concept> concepts = new ArrayList<Concept>();
	static String[] attrName;
	static int trainErrors = 0;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File inTrain = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(inTrain));
		String doc;
		String[] firstLine = br.readLine().trim().split(",");
		attrName = Arrays.copyOfRange(firstLine, 0, firstLine.length-1);
		ArrayList<Integer> restAttr = new  ArrayList<Integer>();
		for(int i = 0; i< attrName.length; i++){
			restAttr.add(i);
		}
		String labelName = firstLine[firstLine.length-1];
		int posCount = 0;
		int negCount = 0;
		
		int line = 0;
		while((doc = br.readLine())!= null){
			String[] attrs = doc.split(",");
			String[] attrsValue = Arrays.copyOfRange(attrs, 0, attrs.length-1);
			for(int i=0;i<attrsValue.length;i++){
				if(!posAttrVal.containsKey(i)){
					posAttrVal.put(i, attrsValue[i]);
				}
				else if(!negAttrVal.containsKey(i)){
					if(!posAttrVal.get(i).equals(attrsValue[i]) )
						negAttrVal.put(i, attrsValue[i]);
				}
			}
			String labelValue = attrs[attrs.length-1];
			if(labelValue.equals("yes") || labelValue.equals("A")){
				labelValue = "+";
			}
			else
				labelValue = "-";
			Instance instance = new Instance(attrsValue,labelValue);
			instances.add(instance);
			lineIndex.add(line);
			line ++;
			if(labelValue.equals("+")){
				posCount ++;
			}
			else
				negCount ++;
		}
		br.close();
		int[] assN = {-1,-1};
		String[] assVal = {"",""};
		TreeNode root = new TreeNode(lineIndex,posCount,negCount,assN,assVal,
				restAttr,0);
		splitTree(root);
		double trainError = trainErrors/(lineIndex.size()*1.0);
		output(root);
		System.out.println("error(train): "+trainError);
//		for(Concept cp:concepts){
//			System.out.println(cp.attr[0]+ " " + cp.val[0] +" "+cp.attr[1]+ " " + cp.val[1]+" " + cp.label);
//
//		}
		File inTest = new File(args[1]);
		br = new BufferedReader(new FileReader(inTest));
		br.readLine();
		int testLineCount = 0;
		int testRightLine = 0;
		while((doc = br.readLine())!=null){
			String[] testLine = doc.trim().split(",");
			String[] testAttrs = Arrays.copyOfRange(testLine, 0, testLine.length-1);
			String result = testLine[testLine.length-1];
			boolean hasError = true;
			if(result.equals("yes") || result.equals("A")){
				result = "+";
				for(Concept cp: concepts){
					for(int i=0;i<2;i++){
						if(cp.attr[i] ==-1)
							continue;
						else{
							if(cp.val[i].equals(testAttrs[cp.attr[i]])){
								if(result.equals(cp.label))
									hasError = false;
							}
							else{
								hasError = true;
								break;
							}
						}
					}
					if(hasError == false){
						testRightLine++;
						break;
					}
				}
			}
			else{
				result = "-";
				for(Concept cp: concepts){
					for(int i=0;i<2;i++){
						if(cp.attr[i] ==-1)
							continue;
						else{
							if(cp.val[i].equals(testAttrs[cp.attr[i]])){
								if(result.equals(cp.label))
									hasError = false;	
							}
							else{
								hasError = true;
								break;
							}
						}
					}
					if(hasError == false){
						testRightLine++;
						break;
					}
				}
			}
			testLineCount++;
		}
		double testError = (testLineCount-testRightLine) / (testLineCount * 1.0);
		System.out.println("error(test): "+testError);
		
	}
	static void output(TreeNode t){
		if(t.depth == 0){
			System.out.println("["+t.posCount +"+/"+t.negCount+"-]");
			if(t.lchild!=null){
				//System.out.println(t.lchild);
				output(t.lchild);
			}
			if(t.rchild!=null){
				//System.out.println(2);
				output(t.rchild);
			}
		}
		else if(t.depth == 1){
			System.out.println(attrName[t.assAttrName[0]]+" = "+t.assAttrVal[0]+": ["+
					t.posCount +"+/"+t.negCount+"-]");
			if(t.lchild!=null){
				output(t.lchild);
			}
			if(t.rchild!=null){
				output(t.rchild);
			}
		}
		else if(t.depth == 2){
			System.out.println("| "+attrName[t.assAttrName[1]]+" = "+t.assAttrVal[1]+": ["+
					t.posCount +"+/"+t.negCount+"-]");
			
		}
		
	}
	static int splitTree(TreeNode t){
		if(t!=null){
			double highestMutual = 0.0;
			int splitLcPos = 0;
			int splitLcNeg = 0;
			int splitRcPos = 0;
			int splitRcNeg = 0;
			int splitAttr = 0;
			if(t.depth == 2){
				vote(t);
				return -1;
			}
			for(int curAttr: t.restAttr){
				int lPos = 0;
				int lNeg = 0;
				int rPos = 0;
				int rNeg = 0;
				double curMutualInfo = 0.0;
				for(int ind: t.indexOfInstances){
					Instance curIns = instances.get(ind);
					if(curIns.getLabel()=="+"){
						if(posAttrVal.get(curAttr).equals(curIns.getInputs()[curAttr])){
							lPos++;
						}
						else
							rPos++;
					}
					else{
						if(posAttrVal.get(curAttr).equals(curIns.getInputs()[curAttr])){
							lNeg++;
						}
						else
							rNeg++;
					}
				}
				curMutualInfo = Entropy.calMutualEntropy(t.entropy, lPos, lNeg, rPos, rNeg);
				if(highestMutual<curMutualInfo){
					highestMutual = curMutualInfo;
					splitLcPos = lPos;
					splitLcNeg = lNeg;
					splitRcPos = rPos;
					splitRcNeg = rNeg;
					splitAttr = curAttr;
				}
			}
			if(highestMutual<= 0.1){
				vote(t);
				return -1;
			}
			else{
				ArrayList<Integer> lIns = new ArrayList<Integer>();
				ArrayList<Integer> rIns = new ArrayList<Integer>();
				for(int i:t.indexOfInstances){
					if(instances.get(i).getInputs()[splitAttr].equals(posAttrVal.get(splitAttr)))
						lIns.add(i);
					else
						rIns.add(i);
				}
				t.splitAttr = splitAttr;
				int[] childAssAttr = new int[2];
				String[] lCAssVal = new String[2];
				String[] rCAssVal = new String[2];
				for(int i = 0;i<2;i++){
					childAssAttr[i] = t.assAttrName[i];
					lCAssVal[i] = t.assAttrVal[i];
					rCAssVal[i] = t.assAttrVal[i];
				}
				ArrayList<Integer> restAt = new ArrayList<Integer>();
				if(t.depth == 0){
					childAssAttr[0] = splitAttr;
					lCAssVal[0] = posAttrVal.get(splitAttr);
					rCAssVal[0] = negAttrVal.get(splitAttr);
				}
				else if(t.depth == 1){
					//System.out.println(splitAttr);
					childAssAttr[1] = splitAttr;
					lCAssVal[1] = posAttrVal.get(splitAttr);
					rCAssVal[1] = negAttrVal.get(splitAttr);
				}
				for(int att:t.restAttr){
					if(att !=splitAttr)
						restAt.add(att);
				}
				t.lchild = new TreeNode(lIns,splitLcPos,splitLcNeg,childAssAttr,lCAssVal,
						restAt,t.depth+1);
				splitTree(t.lchild);
				t.rchild = new TreeNode(rIns,splitRcPos,splitRcNeg,childAssAttr,rCAssVal,
						restAt,t.depth+1);
				splitTree(t.rchild);
				return t.splitAttr;
			}
		}
		return -1;
	}
	static void vote(TreeNode t){
		//System.out.println("vote");
		leaves.add(t);
		HashMap<Integer,String> assign = new HashMap<Integer,String>();
		for(int i = 0; i<2; i++){
			if(t.assAttrName[i]!=-1)
				assign.put(t.assAttrName[i], t.assAttrVal[i]);
		}
		if(t.posCount > t.negCount){
			t.leafLabel ="+";
			t.errorCount = t.negCount;
			concepts.add(new Concept(t.assAttrName,t.assAttrVal,t.leafLabel));
		}
		else{
			t.leafLabel ="-";
			t.errorCount = t.posCount;
			concepts.add(new Concept(t.assAttrName,t.assAttrVal,t.leafLabel));
		}
		trainErrors += t.errorCount;
	}

}
class Concept{
	int[] attr;
	String[] val;
	String label;
	Concept(int[] attr, String[] val, String label){
		this.attr = attr;
		this.val = val;
		this.label = label;
	}
}