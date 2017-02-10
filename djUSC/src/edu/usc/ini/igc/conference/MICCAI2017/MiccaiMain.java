package edu.usc.ini.igc.conference.MICCAI2017;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.Lasso.LassoFitGenerator;
import edu.uga.DICCCOL.SVM.SVM_Classifier;

public class MiccaiMain {

	public String homeDataDir = "E:\\GITWorkSpace\\djUSC\\2017MICCAI\\";
	public int feaNumWant = 40;
	public int maxRound = 10;
	public float shinkRate = 0.1f;
	public List<String> SiteList;
	public List<String> FeatureList;
	public List<SiteProfile> SiteProfileList = new ArrayList<SiteProfile>();
	public float[] penalityW;
	public float subTotalNum = 0.0f;

	public void init(String siteListFile, String featureListFile) {
		SiteList = DicccolUtilIO
				.loadFileToArrayList(homeDataDir + siteListFile);
		FeatureList = DicccolUtilIO.loadFileToArrayList(homeDataDir
				+ featureListFile);
		penalityW = new float[FeatureList.size()];
		for (int f = 0; f < FeatureList.size(); f++)
			penalityW[f] = 1.0f;
	}

	public void loadLassoInput() throws Exception {
		System.out
				.println("##################### loadLassoInput... #####################");
		for (String currentLassoInputFile : SiteList) {
			System.out.println("Loading " + currentLassoInputFile);
			// Create new SiteProfile
			SiteProfile currentSiteProfile = new SiteProfile();
			currentSiteProfile.lassoInputFile = currentLassoInputFile;
			// create LassoFitGenerator
			List<String> currentLassoInputData = DicccolUtilIO
					.loadFileToArrayList(currentLassoInputFile);
			String[] firstLineFeatures = currentLassoInputData.get(0)
					.split(",")[1].split("\\s+");
			int subCount = currentLassoInputData.size();
			this.subTotalNum += (float) subCount;
			currentSiteProfile.subNum = subCount;
			int featuresCount = firstLineFeatures.length;
			currentSiteProfile.feaNum = featuresCount;
			LassoFitGenerator currentLassoFitGenerator = new LassoFitGenerator();
			currentLassoFitGenerator.init(featuresCount, subCount);
			for (int s = 0; s < subCount; s++) {
				String line = currentLassoInputData.get(s);
				String[] lineString = line.split(",");
				String[] feaString = lineString[1].split("\\s+");
				currentSiteProfile.labelList.add((Float.valueOf(lineString[0]
						.trim())).intValue());
				float y = Float.valueOf(lineString[0].trim());
				float[] x = new float[featuresCount];
				for (int i = 0; i < featuresCount; i++)
					x[i] = Float.valueOf(feaString[i].trim());
				currentLassoFitGenerator.setObservationValues(s, x);
				currentLassoFitGenerator.setTarget(s, y);
			} // for s
			currentSiteProfile.lassoFitGenerator = currentLassoFitGenerator;
			currentSiteProfile.copyObvservition();
			SiteProfileList.add(currentSiteProfile);
		} // for SiteList
		System.out
				.println("##################### loadLassoInput finished! #####################");
	}

	public void runLasso() {
		System.out
				.println("##################### runLasso... #####################");
		for (SiteProfile currentSiteProfile : SiteProfileList) {
			System.out.println("Running Lasso fit for: "
					+ currentSiteProfile.lassoInputFile);
			currentSiteProfile.lassoFit = currentSiteProfile.lassoFitGenerator
					.fit(this.feaNumWant, this.penalityW);
		}
		System.out
				.println("##################### runLasso finished! #####################");
	}

	public void generateSVMInput() {
		System.out
				.println("##################### generateSVMInput... #####################");
		for (SiteProfile currentSiteProfile : SiteProfileList) {
			System.out.println("Generating SVM Input (weka) for: "
					+ currentSiteProfile.lassoInputFile);
			List<String> dataWekaList = new ArrayList<String>();
			List<Integer> selectedFeatureList = currentSiteProfile.lassoFit
					.getSelectedFeatureList();
			currentSiteProfile.selectedFeaturesHistory.add(selectedFeatureList);
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
			for (int s = 0; s < currentSiteProfile.subNum; s++) {
				String currentLine = "";
				for (int selectedFeaIndex : selectedFeatureList)
					currentLine += currentSiteProfile.OriObservations[selectedFeaIndex][s]
							+ ",";
				currentLine += currentSiteProfile.labelList.get(s);
				dataWekaList.add(currentLine);
			} // for s
			DicccolUtilIO.writeArrayListToFile(dataWekaList, homeDataDir
					+ currentSiteProfile.lassoInputFile + ".arff");
		} // for currentSiteProfile
		System.out
				.println("##################### generateSVMInput finished! #####################");
	}

	public void runSVM() throws Exception {
		System.out
				.println("##################### runSVM... #####################");
		SVM_Classifier svm_Classifier = new SVM_Classifier();
		for (SiteProfile currentSiteProfile : SiteProfileList) {
			System.out.println("Running SVM for: "
					+ currentSiteProfile.lassoInputFile + ".arff");
			double[] bestPerformance = svm_Classifier.SMO_Classification(
					homeDataDir + currentSiteProfile.lassoInputFile + ".arff",
					0.01, 0.05, 1.0, 200.0);
			currentSiteProfile.improvmentHistory.add(bestPerformance);
		} // for currentSiteProfile
		System.out
				.println("##################### runSVM finished! #####################");
	}

	public boolean checkImprovment() {
		boolean flag = false;
		for (SiteProfile currentSiteProfile : SiteProfileList)
			if (currentSiteProfile.hasImproved)
				flag = true;
		return flag;
	}

	public void updatePW() {
		System.out
				.println("##################### updatePW... #####################");
		for (int f = 0; f < this.FeatureList.size(); f++) {
			float newPenaltyW = 0.0f;
			for (SiteProfile currentSiteProfile : SiteProfileList) {
				if (currentSiteProfile.selectedFeaturesHistory.get(
								currentSiteProfile.selectedFeaturesHistory
										.size() - 1).contains(f)) {
					float subProportion = (float) (currentSiteProfile.subNum)
							/ this.subTotalNum;
					newPenaltyW = newPenaltyW
							+ subProportion
							* (float) currentSiteProfile.improvmentHistory
									.get(currentSiteProfile.improvmentHistory
											.size() - 1)[0];
				} // if
			} // for currentSiteProfile
			this.penalityW[f] = 1.0f + (this.shinkRate * newPenaltyW);
		} // for f
		System.out
				.println("##################### updatePW finished! #####################");
	}

	public void updateImprovmentStatus() {
		System.out
				.println("##################### updateImprovmentStatus... #####################");
		for (SiteProfile currentSiteProfile : SiteProfileList) {
			if (currentSiteProfile.improvmentHistory.size() < 2
					|| currentSiteProfile.improvmentHistory
							.get(currentSiteProfile.improvmentHistory.size() - 1)[0] > currentSiteProfile.improvmentHistory
							.get(currentSiteProfile.improvmentHistory.size() - 2)[0]) {
				if (currentSiteProfile.improvmentHistory.size() > 1)
					System.out.println(currentSiteProfile.lassoInputFile
							+ " has improvment: "
							+ currentSiteProfile.improvmentHistory
									.get(currentSiteProfile.improvmentHistory
											.size() - 2)[0]
							+ " -> "
							+ currentSiteProfile.improvmentHistory
									.get(currentSiteProfile.improvmentHistory
											.size() - 1)[0]);
				currentSiteProfile.hasImproved = true;
			} else {
				currentSiteProfile.hasImproved = false;
				currentSiteProfile.selectedFeaturesHistory
						.remove(currentSiteProfile.selectedFeaturesHistory
								.size() - 1);
				currentSiteProfile.improvmentHistory
						.remove(currentSiteProfile.improvmentHistory.size() - 1);
			} // else
		} // for currentSiteProfile
		System.out
				.println("##################### updateImprovmentStatus finished! #####################");
	}

	public void do_optimization() throws Exception {
		System.out
				.println("##################### do_optimization... #####################");
		this.init("SiteList.txt", "FeatureList_Imputed_Over21.txt");
		this.loadLassoInput();

		int roundCount = 0;
		do {
			System.out
					.println("----------------------------------------------------------  Round "
							+ roundCount
							+ "   ----------------------------------------------------------");
			if (roundCount != 0)
				this.updatePW();

			this.runLasso();
			this.generateSVMInput();
			this.runSVM();

			this.updateImprovmentStatus();

			roundCount++;
		} while (this.checkImprovment() && (roundCount < this.maxRound));
		System.out
				.println("##################### do_optimization finished! #####################");
	}

	public static void main(String[] args) throws Exception {
		MiccaiMain mainHandler = new MiccaiMain();
		mainHandler.do_optimization();
	}

}
