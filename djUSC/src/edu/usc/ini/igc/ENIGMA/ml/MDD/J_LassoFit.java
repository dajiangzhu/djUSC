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
		System.out
				.println("##################### runLasso... ");
		float[] penalityW = new float[this.featuresNum];
		for (int f = 0; f < this.featuresNum; f++)
			penalityW[f] = 1.0f;

		lassoFit = this.lassoFitGenerator.fit(feaNumWant, penalityW);
		System.out
				.println("##################### runLasso finished! ");
	}
	
	public void generateSVMInput(String dir, String lassoInputFile) {
		System.out
				.println("##################### generateSVMInput... #####################");
			System.out.println("Generating SVM Input (weka) for: "
					+ lassoInputFile);
			String filePre = lassoInputFile.split("_")[2].trim();
			List<String> FeatureList = DicccolUtilIO.loadFileToArrayList(dir+"FeatureList_"+filePre+".txt");
			List<String> dataWekaList = new ArrayList<String>();
			List<Integer> selectedFeatureList = lassoFit
					.getSelectedFeatureList();
			System.out.println("Selected features:");
			System.out.println(selectedFeatureList);
			dataWekaList.add("@RELATION  MDD");
			dataWekaList.add(" ");
			for (int f = 0; f < selectedFeatureList.size(); f++)
				dataWekaList
						.add("@ATTRIBUTE "
								+ FeatureList.get(selectedFeatureList.get(f))
								+ " REAL");
			dataWekaList.add("@ATTRIBUTE class {0,1}");
			dataWekaList.add(" ");
			dataWekaList.add("@DATA");
			for (int s = 0; s < this.subNum; s++) {
				String currentLine = "";
				for (int selectedFeaIndex : selectedFeatureList)
					currentLine += this.OriObservations[selectedFeaIndex][s]
							+ ",";
				currentLine += this.labelList.get(s);
				dataWekaList.add(currentLine);
			} // for s
			DicccolUtilIO.writeArrayListToFile(dataWekaList, dir
					+ lassoInputFile.substring(0, lassoInputFile.length()-4) + ".arff");
		System.out
				.println("##################### generateSVMInput finished! #####################");
	}

	public static void main(String[] args) throws Exception {
//		if (args.length != 3) {
//			System.out
//					.println("Need: Dir(String) LassoInputFile(String) featuresWant(int)");
//			System.exit(0);
//		}
//		String dir = args[0].trim();
//		String lassoInputFile = args[1].trim();
//
//		File f = new File(dir + lassoInputFile);
//		if (!f.exists() || f.isDirectory()) {
//			System.out.println("Shit! " + dir + lassoInputFile
//					+ " does not exist or it is a directory!");
//			System.exit(0);
//		}
//		int feaNumWant = Integer.valueOf(args[2].trim());
		
		/////////////////////
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\LassoInput\\Complete\\";
		String lassoInputFile = "J_LassoInput_Over21_S.txt";
		int feaNumWant = 25;
		//////////////////////

		J_LassoFit mainHandler = new J_LassoFit();
		mainHandler.loadLassoInput(dir, lassoInputFile);
		mainHandler.runLasso(feaNumWant);
		mainHandler.generateSVMInput(dir, lassoInputFile);

	}

}
