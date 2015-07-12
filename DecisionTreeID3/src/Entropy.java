public class Entropy {
	public static double calEntropy(int pos, int neg){
		double entropy = 0.0;
		int count = pos+neg;
		double probPos = pos / (count*1.0);
		double probNeg = neg / (count*1.0);
		if(pos == 0 || neg == 0){
			entropy = 0;
		}
		else
			entropy = -(Math.log(probPos)/Math.log(2))*probPos -(Math.log(probNeg)/Math.log(2))*probNeg;
		return entropy;
	}
	public static double calMutualEntropy(double curEntro, int lPos, int lNeg, int rPos, int rNeg){
		double mutualEntropy = 0.0;
		int allCount = lPos + lNeg + rPos + rNeg;
		double lProb = (lPos + lNeg) / (allCount * 1.0);
		double rProb = (rPos + rNeg) / (allCount * 1.0);
		double lEntropy = calEntropy(lPos,lNeg);
		double rEntropy = calEntropy(rPos,rNeg);
		mutualEntropy = curEntro - lProb * lEntropy - rProb * rEntropy;
		return mutualEntropy;
	}
}
