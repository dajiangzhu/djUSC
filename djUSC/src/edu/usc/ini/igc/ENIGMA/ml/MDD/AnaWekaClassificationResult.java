package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class AnaWekaClassificationResult {

	public void anaLog() {
		for (int l = 2; l < 8; l++) {
			String fileName = "log_layer_" + l + ".txt";
			System.out.println("**********************************   "
					+ fileName
					+ "        ********************************************");

			double tp = 100.0;
			double tn = 0.0;
			double sum = 0.0;

			List<String> logList = DicccolUtilIO.loadFileToArrayList(fileName);
			for (int i = 0; i < logList.size(); i++) {
				if (logList.get(i).startsWith("TP")) {
					String[] currentLine = logList.get(i).split("\\s+");
					double tmpTP = Double.valueOf(currentLine[0].split(":")[1]
							.trim());
					double tmpTN = Double.valueOf(currentLine[1].split(":")[1]
							.trim());
					double tmpSum = tmpTP + tmpTN;
					// if(tmpTP>100.0 && tmpTN>500.0 && tmpSum>sum)
					if (tmpTP > tp && tmpSum > 630) {
						tp = tmpTP;
						tn = tmpTN;
						sum = tmpSum;
						System.out.println("Sum: " + sum + "    "
								+ logList.get(i));
					}
				}
			} // for i
		} // for l
	}

	public boolean checkTPTN(double tp, double tn) {
		double tpThreshold = 355.0;
		double tnThreshold = 355.0;
		double sumThreshold = 700.0;
		boolean result = true;
		if (tp < tpThreshold)
			result = false;
		if (tn < tnThreshold)
			result = false;
		if ((tp + tn) < sumThreshold)
			result = false;
		return result;
	}

	public void anaWekaResults() {
//		String resultDir = "E:\\GITWorkSpace\\djUSC\\WekaResult\\";
		String resultDir = "E:\\GITWorkSpace\\djUSC\\AllFeatures\\";
		int roundStart = 0;
		int roundEnd = 9;
		int layerStart = 3;
//		int layerEnd = 6;
		int layerEnd = 3;

		double bestSum = 0.0;
		double bestTP = 0.0;
		double bestTN = 0.0;
		
		for (int r = roundStart; r <= roundEnd; r++) {
			double tmpBestSum = 0.0;
			double tmpBestTP = 0.0;
			double tmpBestTN = 0.0;
			String tmpBestLine = "";
			
			for (int l = layerStart; l <= layerEnd; l++) {
//				String currentResult = resultDir
//						+ "DataWekaList_Lasso_ICV_GroupControl_" + r
//						+ ".arff_layer_" + l + ".txt";
				String currentResult = resultDir
						+ "DataWekaList_AllFeature_ICV_GroupControl_" + r
						+ ".arff_layer_" + l + ".txt";
				System.out.println("####################### "+currentResult);
				List<String> resultList = DicccolUtilIO
						.loadFileToArrayList(currentResult);
				for (int i = 0; i < resultList.size(); i++) {
					String[] lineArray = resultList.get(i).split("\\s+");
					double currentTP = Double.valueOf(lineArray[0].trim());
					double currentTN = Double.valueOf(lineArray[1].trim());
					double currentSum = currentTP+currentTN;
					if(this.checkTPTN(currentTP, currentTN) && currentSum>tmpBestSum)
					{
						tmpBestSum = currentSum;
						tmpBestTP = currentTP;
						tmpBestTN = currentTN;
						tmpBestLine = resultList.get(i);
					} //if
//					if(this.checkTPTN(currentTP, currentTN))
//						System.out.println( (currentTP+currentTN) + "   " + resultList.get(i));
				} // for i
			} // for l
			bestSum += tmpBestSum;
			bestTP += tmpBestTP;
			bestTN += tmpBestTN;
			System.out.println( tmpBestSum + "   " + tmpBestLine);
		} // for r
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$   AVG-BestSum: "+bestSum/10.0+"("+bestSum/10.0/1430.0+")   AVG-BestTP: "+bestTP/10.0+"("+bestTP/10.0/715.0+")   AVG-BestTN: "+bestTN/10.0+"("+bestTN/10.0/715.0+")");

	}

	public static void main(String[] args) {
		AnaWekaClassificationResult mainHandler = new AnaWekaClassificationResult();
		mainHandler.anaWekaResults();

	}

}
