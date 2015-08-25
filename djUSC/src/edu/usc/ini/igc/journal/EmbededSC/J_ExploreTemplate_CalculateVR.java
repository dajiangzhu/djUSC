package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class J_ExploreTemplate_CalculateVR {

	public int optDicIndex;
	public int dicSize;
	public int currentSubID;
	public int subStartID = 1;
	public int subEndID = 68;

	public double pValueThresholdUB = 0.95;
	public double pValueThresholdLB = 0.5;
	public double pValueThresholdStep = 0.05;
	public double pValueCheckThreshold = 0.5;
	public String taskName = "";

	public int subNum = 0;
	public double[][] currentCorrMap;
	public double[][] previousTemplate;
	public List<double[][]> allDMatrix = new ArrayList<double[][]>();
	Correlation correlation = null;

	// ********** pre-set parameters
	public int tSize = 284;

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	public void initialization() {
		currentCorrMap = new double[dicSize][dicSize * subNum];
		System.out
				.println("######################   Loading File  ##########################");
		for (int s = subStartID; s <= subEndID; s++) {
			String configFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"+taskName+"/"+subStartID+"_"+subEndID+"/"+s+"/config_sub_"+s+".txt";
			List<String> configInfo = DicccolUtilIO.loadFileToArrayList(configFile);
			String[] lastLineArray = configInfo.get(configInfo.size()-1).split("\\s+");
			String curFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"+taskName+"/"+subStartID+"_"+subEndID+"/"+s+"/OptDicIndex_"+lastLineArray[0].trim()+"/sub_"+s+"_OptDicIndex_"+lastLineArray[0].trim()+"_Round_"+lastLineArray[1].trim()+"_D.txt";
			System.out.println("Loading : " + curFile);
			double[][] curSigM = this.trsposeM(
					DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize),
					tSize, dicSize);
			allDMatrix.add(curSigM);
		} // for s
		System.out
				.println("######################   Loading File End! ##########################");
		if (optDicIndex > 0) // if not the first time to find the dictionary
								// template
		{
			System.out
					.println("######################   Loading previous dictionary templates  ##########################");
			previousTemplate =  new double[optDicIndex][tSize];
			for(int i=0;i<optDicIndex;i++)
			{
				double[][] tmpSigTemplate = this.trsposeM(DicccolUtilIO
						.loadFileAsArray(
								"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"+taskName+"/"+subStartID+"_"+subEndID+"/TemplateSig_"
										+ i + ".txt", tSize, 1), tSize, 1);
				previousTemplate[i] = tmpSigTemplate[0];
			} //for
			System.out
					.println("######################   Loading previous dictionary templates end  ##########################");
		}
		// *************initial correlation handler
		correlation = new Correlation();
	}

	public boolean checkWithPreviousTemplates(double[] sigNeedToBeChecked) {
		boolean isSimilar = false;
		for (int i = 0; i < optDicIndex; i++) {
			double tmpCorreValue = correlation.Correlation_Pearsons(
					sigNeedToBeChecked, previousTemplate[i]);
			if (tmpCorreValue >= pValueCheckThreshold) {
				isSimilar = true;
				System.out.println("!!!!!!!!!! Similar with the " + i
						+ " th template !!!!!!!");
				break;
			}
		}

		return isSimilar;
	}

	public void calCurrentCorreMap() {
		System.out
				.println("######################   Calculating the CorreMap  ##########################");
		for (int d = optDicIndex; d < this.dicSize; d++) {
			double[] currentD = allDMatrix.get(currentSubID - subStartID)[d];
			if (optDicIndex > 0)
				if (this.checkWithPreviousTemplates(currentD))
					continue;
			System.out
					.println("Calculating the " + d + "th dictionary item...");
			for (int s = subStartID; s <= subEndID; s++) {
				if (s != currentSubID) {
					for (int tmpD = optDicIndex; tmpD < this.dicSize; tmpD++) {
						double[] compareD = allDMatrix.get((s - subStartID))[tmpD];
						double tmpCorreValue = correlation
								.Correlation_Pearsons(currentD, compareD);
						currentCorrMap[d][(s - subStartID) * dicSize + tmpD] = tmpCorreValue;
					} // for tmpD
				} // if
			} // for s
		} // for d
		System.out
				.println("######################   Calculating the CorreMap End!  ##########################");
	}

	public void findTheTemplate() {
		System.out
				.println("######################   Finding the template  ##########################");
		List<String> VROutPut = new ArrayList<String>();
		for (double currentThreshold = pValueThresholdUB; currentThreshold >= pValueThresholdLB; currentThreshold = currentThreshold
				- pValueThresholdStep) {
			System.out.println(" Calculating currentThreshold: "
					+ currentThreshold);
			double maxVR = 0.0;
			int dicIndexWithMaxVR = -1;
			for (int d = optDicIndex; d < dicSize; d++) {
				double tmpVR = 0.0;
				for (int compareSubID = subStartID; compareSubID <= subEndID; compareSubID++) {
					if (compareSubID != currentSubID)
						for (int tmpD = optDicIndex; tmpD < dicSize; tmpD++) {
							if (currentCorrMap[d][(compareSubID - subStartID)
									* dicSize + tmpD] >= currentThreshold) {
								tmpVR++;
								break;
							} // if current pValue is larger than threshold
						} // for tmpD
				} // for all the subjects to be compared
				tmpVR = tmpVR / (subNum - 1);
				if (tmpVR > maxVR) {
					maxVR = tmpVR;
					dicIndexWithMaxVR = d;
				} // if tmpVR>maxVR
			} // for d
			VROutPut.add("" + currentThreshold + " " + maxVR + " "
					+ dicIndexWithMaxVR);
		} // for each threshold
		System.out
				.println("######################   Finding the template end  ##########################");

		System.out
				.println("######################   Writing the VR info  ##########################");
		DicccolUtilIO.writeArrayListToFile(VROutPut,
				"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"+taskName+"/"+subStartID+"_"+subEndID+"/"+currentSubID+"/OptDicIndex_"+optDicIndex+"/VR_sub_" + currentSubID
						+ "_OptDicIndex_" + optDicIndex + "_start_"
						+ subStartID + "_end_" + subEndID + ".txt");
		System.out
				.println("######################   Writing the VR info end  ##########################");
	}

	public void printParaInfo() {
		System.out.println("***************  Paramater Info  ***************");
		System.out.println("taskName: " + taskName);
		System.out.println("optDicIndex: " + optDicIndex);
		System.out.println("dicSize: " + dicSize);
		System.out.println("currentSubID: " + currentSubID);
		System.out.println("subStartID: " + subStartID);
		System.out.println("subEndID: " + subEndID);
		System.out.println("subNum: " + subNum);
		System.out.println("pValueThresholdUB: " + pValueThresholdUB);
		System.out.println("pValueThresholdLB: " + pValueThresholdLB);
		System.out.println("pValueThresholdStep: " + pValueThresholdStep);
		System.out.println("pValueCheckThreshold: " + pValueCheckThreshold);
		System.out.println("************************************************");
	}

	public static void main(String[] args) {

		if (args.length == 10) {
			J_ExploreTemplate_CalculateVR mainHandler = new J_ExploreTemplate_CalculateVR();

			mainHandler.optDicIndex = Integer.valueOf(args[0].trim()); // 0-399
			mainHandler.dicSize = Integer.valueOf(args[1].trim()); // 400
			mainHandler.currentSubID = Integer.valueOf(args[2].trim()); // [subStartID,subEndID]
			mainHandler.subStartID = Integer.valueOf(args[3].trim()); // 1-58?
			mainHandler.subEndID = Integer.valueOf(args[4].trim()); // 10-68
			mainHandler.subNum = mainHandler.subEndID - mainHandler.subStartID
					+ 1;
			mainHandler.pValueThresholdUB = Double.valueOf(args[5].trim());
			mainHandler.pValueThresholdLB = Double.valueOf(args[6].trim());
			mainHandler.pValueThresholdStep = Double.valueOf(args[7].trim());
			mainHandler.pValueCheckThreshold = Double.valueOf(args[8].trim());
			mainHandler.taskName = args[9].trim();
			if (mainHandler.currentSubID < mainHandler.subStartID
					|| mainHandler.currentSubID > mainHandler.subEndID) {
				System.out.println("currentSubID should be within the range!");
				System.exit(0);
			}
			if (mainHandler.pValueThresholdUB < mainHandler.pValueThresholdLB) {
				System.out
						.println("pValueThresholdUB should be larger than pValueThresholdLB!");
				System.exit(0);
			}

			mainHandler.printParaInfo();
			mainHandler.initialization();
			mainHandler.calCurrentCorreMap();
			mainHandler.findTheTemplate();
		} else
			System.out
					.println("Input: optDicIndex (0-399), dicSize(400), currentSubID[subStartID,subEndID], subStartID(1-58), subEndID(10-68), pValueThresholdUB(0.75), pValueThresholdLB(0.5), pValueThresholdStep(0.05), pValueCheckThreshold(0.5) and taskName");

	}

}
