package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.Lasso.LassoFit;
import edu.uga.DICCCOL.Lasso.LassoFitGenerator;
import edu.usc.ini.igc.conference.MICCAI2017.SiteProfile;

public class J_LassoFit {

	public List<Integer> labelList = new ArrayList<Integer>();
	public float[][] OriObservations;
	public LassoFitGenerator lassoFitGenerator = new LassoFitGenerator();
	public LassoFit lassoFit = null;
	public int featuresNum = 0;
	public int subNum = 0;

	public void loadLassoInput(String dir, String lassoInputFile)
			throws Exception {
		System.out.println("##################### loadLassoInput ("
				+ lassoInputFile + ") ... ");
		// create LassoFitGenerator
		List<String> currentLassoInputData = DicccolUtilIO
				.loadFileToArrayList(dir + lassoInputFile);
		String[] firstLineFeatures = currentLassoInputData.get(0).split(",")[1]
				.split("\\s+");
		this.subNum = currentLassoInputData.size();
		this.featuresNum = firstLineFeatures.length;
		OriObservations = new float[featuresNum][subNum];

		lassoFitGenerator.init(featuresNum, subNum);
		this.labelList.clear();
		for (int s = 0; s < subNum; s++) {
			String line = currentLassoInputData.get(s);
			String[] lineString = line.split(",");
			String[] feaString = lineString[1].split("\\s+");
			this.labelList
					.add((Float.valueOf(lineString[0].trim())).intValue());
			float y = Float.valueOf(lineString[0].trim());
			float[] x = new float[featuresNum];
			for (int i = 0; i < featuresNum; i++) {
				x[i] = Float.valueOf(feaString[i].trim());
				OriObservations[i][s] = x[i];
			}
			lassoFitGenerator.setObservationValues(s, x);
			lassoFitGenerator.setTarget(s, y);
		} // for s
		System.out.println("##################### loadLassoInput ("
				+ lassoInputFile + ") finished! ");
	}

	public void runLasso(int feaNumWant) {
		System.out.println("##################### runLasso... ");
		float[] penalityW = new float[this.featuresNum];
		for (int f = 0; f < this.featuresNum; f++)
			penalityW[f] = 1.0f;

		lassoFit = this.lassoFitGenerator.fit(feaNumWant, penalityW);
		System.out.println("##################### runLasso finished! ");
	}

	public void generateSVMInput(String dir, String lassoInputFile,
			int feaNumWant) {
		System.out
				.println("##################### generateSVMInput... #####################");
		System.out
				.println("Generating SVM Input (weka) for: " + lassoInputFile);
		String filePre = lassoInputFile.split("_")[2].trim();
		List<String> FeatureList = DicccolUtilIO.loadFileToArrayList(dir
				+ "FeatureList_" + filePre + ".txt");
		List<String> dataWekaList = new ArrayList<String>();
		List<Integer> selectedFeatureList = lassoFit.getSelectedFeatureList();
		System.out.println("Selected features:");
		System.out.println(selectedFeatureList);
		dataWekaList.add("@RELATION  MDD");
		dataWekaList.add(" ");
		for (int f = 0; f < selectedFeatureList.size(); f++)
			dataWekaList.add("@ATTRIBUTE "
					+ FeatureList.get(selectedFeatureList.get(f)) + " REAL");
		dataWekaList.add("@ATTRIBUTE class {0,1}");
		dataWekaList.add(" ");
		dataWekaList.add("@DATA");
		for (int s = 0; s < this.subNum; s++) {
			String currentLine = "";
			for (int selectedFeaIndex : selectedFeatureList)
				currentLine += this.OriObservations[selectedFeaIndex][s] + ",";
			currentLine += this.labelList.get(s);
			dataWekaList.add(currentLine);
		} // for s
		DicccolUtilIO.writeArrayListToFile(dataWekaList,
				dir + lassoInputFile.substring(0, lassoInputFile.length() - 4)
						+ "_Feature" + feaNumWant + ".arff");
		System.out
				.println("##################### generateSVMInput finished! #####################");
	}

	public static void main(String[] args) throws Exception {
		// if (args.length != 3) {
		// System.out
		// .println("Need: Dir(String) LassoInputFile(String) featuresWant(int)");
		// System.exit(0);
		// }
		// String dir = args[0].trim();
		// String lassoInputFile = args[1].trim();
		//
		// File f = new File(dir + lassoInputFile);
		// if (!f.exists() || f.isDirectory()) {
		// System.out.println("Shit! " + dir + lassoInputFile
		// + " does not exist or it is a directory!");
		// System.exit(0);
		// }
		// int feaNumWant = Integer.valueOf(args[2].trim());

		// ///////////////////
		J_SiteDictionary siteDic = new J_SiteDictionary();
		J_LassoFit mainHandler = new J_LassoFit();
		String subGroupName = "Females";
		String category = "Complete";
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\LassoInput\\"
				+ category + "\\" + subGroupName + "\\";
		int feaNumWant = 25;

		// //////////////////// for single lasso input
		// String lassoInputFile = "J_LassoInput_"+subGroupName+"_G.txt";
		// mainHandler.loadLassoInput(dir, lassoInputFile);
		// mainHandler.runLasso(feaNumWant);
		// mainHandler.generateSVMInput(dir, lassoInputFile, feaNumWant);
		// //////////////////////

		// ////////////////// for single lasso input
		// for(int i=0;i<100;i++)
		// {
		// String lassoInputFile =
		// "J_LassoInput_"+subGroupName+"_Random"+i+".txt";
		// mainHandler.loadLassoInput(dir, lassoInputFile);
		// mainHandler.runLasso(feaNumWant);
		// mainHandler.generateSVMInput(dir, lassoInputFile, feaNumWant);
		//
		// }

		// ////////////////////

		// // ////////////////////// for sequence
		 String siteConfig =
				 subGroupName+"_Over21_"+category+"_SiteSequence.txt";
		 List<String> siteConfigList = DicccolUtilIO
		 .loadFileToArrayList(siteConfig);
		 List<String> qsubLines = new ArrayList<String>();
		 for (String line : siteConfigList) {
		 String fileName = "J_LassoInput_" + subGroupName + "_";
		 List<String> distributedLassoInput = new ArrayList<String>();
		 String[] lineArray = line.split("\\s+")[0].split(";");
		 String qsubSiteCode = "";
		 for (int i = 0; i < lineArray.length; i++) {
		 String siteName = lineArray[i].trim();
		 String siteCode = siteDic.getCodeFromSite(siteName);
		 qsubSiteCode += siteCode;
		 fileName += siteCode;
		 } // for i
		 qsubLines.add(qsubSiteCode);
		 fileName += ".txt";
		 mainHandler.loadLassoInput(dir, fileName);
		 mainHandler.runLasso(feaNumWant);
		 mainHandler.generateSVMInput(dir, fileName, feaNumWant);
		 } // for line

		// ////////////////////// for seperate site
//		File folder = new File(dir);
//		String[] files = folder.list();
//		for (String fileName : files) {
//			if (fileName.startsWith("J_LassoInput") && fileName.endsWith(".txt")) {
//				mainHandler.loadLassoInput(dir, fileName);
//				mainHandler.runLasso(feaNumWant);
//				mainHandler.generateSVMInput(dir, fileName, feaNumWant);
//			}
//		} // for line

		 System.out
		 .println("##########################  qsub script ##################################");
		 for (String siteCode : qsubLines)
		 System.out.println("/ifshome/dzhu/ENGIMA_MDD/Journal/SVMInput/"
		 + category
		 + "/singlesite_SVM_qsub.sh ${category} ${subGroup} ${feaNum} "
		 + siteCode);
		 //////////////////////

	}

}
