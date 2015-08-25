package edu.usc.ini.igc.journal.EmbededSC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class J_ExploreTemplate_AnalyseVR {

	public int optDicIndex;
	public int dicSize;
	public int subStartID = 1;
	public int subEndID = 68;
	public int subNum = 0;
	public double voteRateThreshold = 0.5;
	public double pValueThreshold = 0.5;
	public String taskName = "";

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
			String curFileVRMap = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
					+ taskName
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
			String configFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
					+ taskName
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
			String curFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
					+ taskName
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
			else // also need to move A matrix, since this subject will not run
					// in the next round
			{
				if (optDicIndex > 0) {
					System.out.println("Need to update A as well...");
					String configFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName
							+ "/"
							+ subStartID
							+ "_"
							+ subEndID
							+ "/" + s + "/config_sub_" + s + ".txt";
					List<String> configInfo = DicccolUtilIO
							.loadFileToArrayList(configFile);
					String[] lastLineArray = configInfo.get(
							configInfo.size() - 1).split("\\s+");
					String previousAMatrixFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName
							+ "/"
							+ subStartID
							+ "_"
							+ subEndID
							+ "/"
							+ s
							+ "/OptDicIndex_"
							+ (optDicIndex - 1)
							+ "/sub_"
							+ s
							+ "_OptDicIndex_"
							+ (optDicIndex - 1)
							+ "_Round_" + lastLineArray[1].trim() + "_A.txt";
					File f = new File(previousAMatrixFile);
					if (f.exists() && !f.isDirectory()) {
						System.out.println("Will update A: "+previousAMatrixFile);
						List<String> tmpResult = DicccolUtilIO
								.loadFileToArrayList(previousAMatrixFile);
						if(tmpResult.size()==dicSize)
						{
							System.out.println("The number of rows is ok:" +dicSize);
							String tmpLine = tmpResult.get(savedDicIndex);
							tmpResult.set(savedDicIndex, tmpResult.get(optDicIndex));
							tmpResult.set(optDicIndex, tmpLine);
							String newAMatrixFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName
							+ "/"
							+ subStartID
							+ "_"
							+ subEndID
							+ "/"
							+ s
							+ "/OptDicIndex_"
							+ optDicIndex
							+ "/sub_"
							+ s
							+ "_OptDicIndex_"
							+ optDicIndex
							+ "_Round_1_A.txt";
							DicccolUtilIO.writeArrayListToFile(tmpResult, newAMatrixFile);
//							String[] tmpLine = tmpResult.get(0).split("\\s+");
//							int sampleNum = tmpLine.length;
//							double[][] previousA = new double[dicSize][sampleNum];
//							for(int i=0;i<dicSize;i++)
//							{
//								tmpLine = tmpResult.get(i).split("\\s+");
//								for (int j = 0; j < sampleNum; j++)
//									previousA[i][j] = Double
//										.valueOf(tmpLine[j]);
//							} //for i
//							
//							double[] tmp = previousA[savedDicIndex];
//							previousA[savedDicIndex] = previousA[optDicIndex];
//							previousA[optDicIndex] = tmp;
//
//							String newAMatrixFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
//									+ taskName
//									+ "/"
//									+ subStartID
//									+ "_"
//									+ subEndID
//									+ "/"
//									+ s
//									+ "/OptDicIndex_"
//									+ optDicIndex
//									+ "/sub_"
//									+ s
//									+ "_OptDicIndex_"
//									+ optDicIndex
//									+ "_Round_1_A.txt";
//							DicccolUtilIO.writeArrayToFile(previousA, dicSize, sampleNum, " ", newAMatrixFile);
						} //if A file is ok
						else
						{
							System.out
							.println("Error in DicccolUtilIO.loadFileAsArray: dimRow is incorrect, shoule be"+ dicSize);
							System.exit(0);
						} //if A file is NOT ok
					} // if this A exists
				} // if optDicIndex > 0
			} // else (s == savedSubID)

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
					"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName + "/" + subStartID + "_" + subEndID
							+ "/" + s + "/OptDicIndex_" + optDicIndex + "/sub_"
							+ s + "_OptDicIndex_" + optDicIndex
							+ "_Round_1_D.txt");
			System.out
					.println("######################   Saving the updated DMatrix end  ##########################");
			// update config file
			System.out
					.println("######################   Updating the config file  ##########################");
			List<String> configList = DicccolUtilIO
					.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName
							+ "/"
							+ subStartID
							+ "_"
							+ subEndID
							+ "/" + s + "/config_sub_" + s + ".txt");
			configList.add(optDicIndex + " 1");
			DicccolUtilIO.writeArrayListToFile(configList,
					"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
							+ taskName + "/" + subStartID + "_" + subEndID
							+ "/" + s + "/config_sub_" + s + ".txt");
		} // for s
		System.out
				.println("######################   Updating the config file end  ##########################");
		// save candidate information
		System.out
				.println("######################   Saving the candidate information   ##########################");
		DicccolUtilIO.writeArrayToFile(candidateMap, subNum, 3, " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
						+ taskName + "/" + subStartID + "_" + subEndID
						+ "/CandidateInfo_OptDicIndex_" + optDicIndex + ".txt");
		System.out
				.println("######################   Saving the candidate information end  ##########################");
	}

	public void updateTemplateInfo(double[] tmplateSig) {
		// System.out.println("######################   Updating template info  ##########################");
		// List<String> templateList;
		// if (optDicIndex > 0) // if not the first time to find the dictionary
		// // template
		// templateList = DicccolUtilIO
		// .loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/template/TemplateSig_"
		// + (optDicIndex - 1) + ".txt");
		// else
		// templateList = new ArrayList<String>();
		// String newTmpLine = "";
		// for (int t = 0; t < tSize; t++)
		// newTmpLine = newTmpLine + tmplateSig[t] + " ";
		// templateList.add(newTmpLine);
		// DicccolUtilIO
		// .writeArrayListToFile(
		// templateList,
		// "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/template/TemplateSig_"
		// + optDicIndex + ".txt");
		// System.out.println("######################   Updating template info end  ##########################");
		List<String> templateList = new ArrayList<String>();
		for (int t = 0; t < tSize; t++) {
			String newTmpLine = "" + tmplateSig[t];
			templateList.add(newTmpLine);
		}
		DicccolUtilIO.writeArrayListToFile(templateList,
				"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/"
						+ taskName + "/" + subStartID + "_" + subEndID
						+ "/TemplateSig_" + optDicIndex + ".txt");

	}

	public void findTheTemplate() {
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
	}

	public void printParaInfo() {
		System.out.println("***************  Paramater Info  ***************");
		System.out.println("taskName: " + taskName);
		System.out.println("optDicIndex: " + optDicIndex);
		System.out.println("dicSize: " + dicSize);
		System.out.println("subStartID: " + subStartID);
		System.out.println("subEndID: " + subEndID);
		System.out.println("subNum: " + subNum);
		System.out.println("voteRateThreshold: " + voteRateThreshold);
		System.out.println("pValueThreshold: " + pValueThreshold);
		System.out.println("************************************************");
	}

	public static void main(String[] args) {
		if (args.length == 7) {
			J_ExploreTemplate_AnalyseVR mainHandler = new J_ExploreTemplate_AnalyseVR();

			mainHandler.optDicIndex = Integer.valueOf(args[0].trim()); // 0-399
			mainHandler.dicSize = Integer.valueOf(args[1].trim()); // 400
			mainHandler.subStartID = Integer.valueOf(args[2].trim()); // 1-58?
			mainHandler.subEndID = Integer.valueOf(args[3].trim()); // 10-68
			mainHandler.subNum = mainHandler.subEndID - mainHandler.subStartID
					+ 1;
			mainHandler.voteRateThreshold = Double.valueOf(args[4].trim());
			mainHandler.pValueThreshold = Double.valueOf(args[5].trim());
			mainHandler.taskName = args[6].trim();

			mainHandler.printParaInfo();
			mainHandler.initialization();
			mainHandler.findTheTemplate();
		} else
			System.out
					.println("Input: optDicIndex (0-399), dicSize(400), subStartID(1-58), subEndID(10-68), voteRateThreshold(0.5), pValueThreshold(0.5) and taskName");

	}

}
