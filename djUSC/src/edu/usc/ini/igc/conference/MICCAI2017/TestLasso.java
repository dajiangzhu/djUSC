package edu.usc.ini.igc.conference.MICCAI2017;

import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.Lasso.LassoFit;
import edu.uga.DICCCOL.Lasso.LassoFitGenerator;

public class TestLasso {
	
	public void test() throws Exception
	{
		List<String> inputData = DicccolUtilIO.loadFileToArrayList("J_LassoInput_C.txt");
		String[] firstLine = inputData.get(0).split(",")[1].split("\\s+");
		int subCount = inputData.size();
		int featuresCount = firstLine.length;
		
		LassoFitGenerator fitGenerator = new LassoFitGenerator();
		fitGenerator.init(featuresCount, subCount);
		
		for(int s=0;s<subCount;s++)
		{
			String line = inputData.get(s);
			String[] lineString = line.split(",");
			String[] feaString = lineString[1].split("\\s+");
			float y = Float.valueOf(lineString[0].trim());
			float[] x = new float[featuresCount];
			for(int i=0;i<featuresCount;i++)
				x[i] = Float.valueOf(feaString[i].trim());
			fitGenerator.setObservationValues(s, x);
			fitGenerator.setTarget(s, y);
		} //for
		
//		int wantNum = 15;
//		LassoFit fit = fitGenerator.fit(wantNum);
//		System.out.println(fit);
//		double[] weights = fit.getWeights(fit.numberOfLambdas-1);
//		System.out.println(fit.nonZeroWeights[fit.numberOfLambdas-1]+":");
//		for(int i=0;i<weights.length;i++)
//			System.out.println(weights[i]);
//		
	}

	public static void main(String[] args) throws Exception {
		TestLasso mainHandler = new TestLasso();
		mainHandler.test();

	}

}
