import java.util.ArrayList;
import java.util.HashMap;


public class TreeNode {
	ArrayList<Integer> indexOfInstances;
	int setCount;
	int posCount;
	int negCount;
	int splitAttr;
	int[] assAttrName;
	String[] assAttrVal;
	ArrayList<Integer> restAttr;
	TreeNode lchild;
	TreeNode rchild;
	double entropy;
	int depth;
	String leafLabel;
	int errorCount;
	TreeNode(ArrayList<Integer> indexOfInstances,int posCount,int negCount, int[] assAttrName
			, String[] assAttrVal, ArrayList<Integer> restAttr, int depth){
		this.indexOfInstances = indexOfInstances;
		this.posCount = posCount;
		this.negCount = negCount;
		this.assAttrName = assAttrName;
		this.assAttrVal = assAttrVal;
		this.restAttr = restAttr;
		this.setCount = posCount + negCount;
		this.entropy = Entropy.calEntropy(posCount, negCount);
		this.leafLabel = "";
		this.errorCount = 0;
		this.depth = depth;
	}
	
}
