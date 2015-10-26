package edu.usc.ini.igc.conference.ISBI2016;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;


public class ESL_AnalyseVR {
	
	public int optDicIndex;
	public int dicSize;
	public int subStartID = 1;
	public int subEndID = 68;
	public int subNum = 0;
	public double voteRateThreshold = 0.5;
	public double pValueThreshold = 0.5;
	public String ESL_OutPutDir = "";

	public boolean foundTemplate = false;
	public double savedVR = 0.0;
	public int savedSubID = -1;
	public int savedDicIndex = -1;

	List<List<String>> allVRList = new ArrayList<List<String>>();
	public int tSize = 284;
	public List<double[][]> allDMatrix = new ArrayList<double[][]>();
	Correlation correlation = null;

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	public void initialization() {
		System.out
				.println("######################   Loading File  ##########################");
		for (int s = subStartID; s <= subEndID; s++) {
			String curFileVRMap = ESL_OutPutDir
					+ "/"
					+ subStartID
					+ "_"
					+ subEndID
					+ "/"
					+ s
					+ "/OptDicIndex_"
					+ optDicIndex
					+ "/VR_sub_"
					+ s
					+ "_OptDicIndex_"
					+ optDicIndex
					+ "_start_"
					+ subStartID
					+ "_end_" + subEndID + ".txt";
			System.out.println("Loading : " + curFileVRMap);
			List<String> curSubVRList = DicccolUtilIO
					.loadFileToArrayList(curFileVRMap);
			allVRList.add(curSubVRList);
		} // for s
		System.out
				.println("######################   Loading File End! ##########################");
		// *************initial correlation handler
		correlation = new Correlation();
	}

	public void updateDMatrix() {
		System.out
				.println("######################   Updating the DMatrix  ##########################");
		System.out
				.println("######################   Loading DMatrix  ##########################");
		for (int s = subStartID; s <= subEndID; s++) {
			String configFile = ESL_OutPutDir
					+ "/"
					+ subStartID
					+ "_"
					+ subEndID
					+ "/"
					+ s
					+ "/config_sub_" + s + ".txt";
			List<String> configInfo = DicccolUtilIO
					.loadFileToArrayList(configFile);
			String[] lastLineArray = configInfo.get(configInfo.size() - 1)
					.split("\\s+");
			String curFile = ESL_OutPutDir
					+ "/"
					+ subStartID
					+ "_"
					+ subEndID
					+ "/"
					+ s
					+ "/OptDicIndex_"
					+ lastLineArray[0].trim()
					+ "/sub_"
					+ s
					+ "_OptDicIndex_"
					+ lastLineArray[0].trim()
					+ "_Round_"
					+ lastLineArray[1].trim() + "_D.txt";
			System.out.println("Loading : " + curFile);
			double[][] curSigM = this.trsposeM(
					DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize),
					tSize, dicSize);
			allDMatrix.add(curSigM);
		} // for s
		System.out
				.println("######################   Loading DMatrix End! ##########################");

		double[][] candidateMap = new double[subNum][3];
		candidateMap[savedSubID - subStartID][0] = savedSubID;
		candidateMap[savedSubID - subStartID][1] = savedDicIndex;
		candidateMap[savedSubID - subStartID][2] = 1.0;
		double[] tmplateSig = allDMatrix.get(savedSubID - subStartID)[savedDicIndex];
		this.updateTemplateInfo(tmplateSig);
		for (int s = subStartID; s <= subEndID; s++) {
			double[][] curComparedSubD = allDMatrix.get(s - subStartID);
			if (s != savedSubID) {
				double tmpMax = 0.0;
				for (int tmpD = optDicIndex; tmpD < dicSize; tmpD++) {
					double tmpCorreValue = correlation.Correlation_Pearsons(
							tmplateSig, curComparedSubD[tmpD]);
					if (tmpCorreValue > tmpMax) {
						candidateMap[s - subStartID][0] = s;
						candidateMap[s - subStartID][1] = tmpD;
						candidateMap[s - subStartID][2] = tmpCorreValue;
						tmpMax = tmpCorreValue;
					} // if current pValue is larger than threshold
				} // for tmpD
			} // if

			// Move candidate signals
			System.out
					.println("######################   Moving the candidate signals  ##########################");
			double[] tmp = curComparedSubD[(int) candidateMap[s - subStartID][1]];
			curComparedSubD[(int) candidateMap[s - subStartID][1]] = curComparedSubD[optDicIndex];
			curComparedSubD[optDicIndex] = tmp;
			System.out
					.println("######################   Moving the candidate signals end  ##########################");
			// save updatedDMatrix
			System.out
					.println("######################   Saving the updated DMatrix   ##########################");
			DicccolUtilIO.writeArrayToFile(
					this.trsposeM(curComparedSubD, dicSize, tSize), tSize,
					dicSize, " ",
					ESL_OutPutDir + "/" + subStartID + "_" + subEndID
							+ "/" + s + "/OptDicIndex_" + optDicIndex + "/sub_"
							+ s + "_OptDicIndex_" + optDicIndex
							+ "_Round_1_D.txt");
			System.out
					.println("######################   Saving the updated DMatrix end  ##########################");
			// update config file
			System.out
					.println("######################   Updating the config file  ##########################");
			List<String> configList = DicccolUtilIO
					.loadFileToArrayList(ESL_OutPutDir
							+ "/"
							+ subStartID
							+ "_"
							+ subEndID
							+ "/" + s + "/config_sub_" + s + ".txt");
			configList.add(optDicIndex + " 1");
			DicccolUtilIO.writeArrayListToFile(configList,
					ESL_OutPutDir + "/" + subStartID + "_" + subEndID
							+ "/" + s + "/config_sub_" + s + ".txt");
		} // for s
		System.out
				.println("######################   Updating the config file end  ##########################");
		// save candidate information
		System.out
				.println("######################   Saving the candidate information   ##########################");
		DicccolUtilIO.writeArrayToFile(candidateMap, subNum, 3, " ",
				ESL_OutPutDir + "/" + subStartID + "_" + subEndID
						+ "/CandidateInfo_OptDicIndex_" + optDicIndex + ".txt");
		System.out
				.println("######################   Saving the candidate information end  ##########################");
	}

	public void updateTemplateInfo(double[] tmplateSig) {
		List<String> templateList = new ArrayList<String>();
		for (int t = 0; t < tSize; t++) {
			String newTmpLine = "" + tmplateSig[t];
			templateList.add(newTmpLine);
		}
		DicccolUtilIO.writeArrayListToFile(templateList,
				ESL_OutPutDir + "/" + subStartID + "_" + subEndID
						+ "/TemplateSig_" + optDicIndex + ".txt");

	}

	public boolean findTheTemplate() {
		System.out
				.println("######################   Analysing the VR files  ##########################");
		double maxPvalue = 0.5;
		for (int s = subStartID; s <= subEndID; s++) {
			List<String> curSubVRList = allVRList.get(s - subStartID);
			for (int i = 0; i < curSubVRList.size(); i++) {
				String curLine = curSubVRList.get(i);
				String[] curLineArray = curLine.split("\\s+");
				double curPValue = Double.valueOf(curLineArray[0].trim());
				double curVR = Double.valueOf(curLineArray[1].trim());
				int curDicIndex = Integer.valueOf(curLineArray[2].trim());
				if (curPValue >= pValueThreshold && curVR >= voteRateThreshold)
					if (curPValue > maxPvalue) {
						foundTemplate = true;
						maxPvalue = curPValue;
						savedVR = curVR;
						savedSubID = s;
						savedDicIndex = curDicIndex;
					}
			} // for i
		} // for s
		System.out
				.println("######################   Analysing the VR files end  ##########################");
		System.out.println("Center subject: " + savedSubID + "   DicIndex: "
				+ savedDicIndex + "   VR: " + savedVR);
		if (foundTemplate) {
			this.updateDMatrix();
		} // if
		return foundTemplate;
	}

	public void printParaInfo() {
		System.out.println("***************  Paramater Info  ***************");
		System.out.println("ESL_OutPutDir: " + ESL_OutPutDir);
		System.out.println("optDicIndex: " + optDicIndex);
		System.out.println("dicSize: " + dicSize);
		System.out.println("subStartID: " + subStartID);
		System.out.println("subEndID: " + subEndID);
		System.out.println("subNum: " + subNum);
		System.out.println("voteRateThreshold: " + voteRateThreshold);
		System.out.println("pValueThreshold: " + pValueThreshold);
		System.out.println("************************************************");
	}

}
