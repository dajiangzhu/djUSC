package edu.usc.ini.igc.conference.MICCAI2017;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class MiccaiStatistics {

	public String homeDataDir = "E:\\GITWorkSpace\\djUSC\\2017MICCAI\\";
	public List<String> SiteList;
	public List<String> FeatureList;
	public List<SiteProfile> SiteProfileList = new ArrayList<SiteProfile>();

	public void init(String siteListFile, String featureListFile) {
		SiteList = DicccolUtilIO
				.loadFileToArrayList(homeDataDir + siteListFile);
		FeatureList = DicccolUtilIO.loadFileToArrayList(homeDataDir
				+ featureListFile);
	}

	public void analyzeLog(int featureIndex) {
		System.out.println("##################### analyzeLog log_"
				+ featureIndex + "... #####################");
		File dir = new File(homeDataDir + "log_" + featureIndex);
		String[] files = dir.list();
		int maxLinNum = 0;
		String wantFile = "";
		for (String logFile : files) {
			List<String> currentFileLines = DicccolUtilIO
					.loadFileToArrayList(homeDataDir + "log_" + featureIndex
							+ "\\" + logFile);
			if (currentFileLines.size() > maxLinNum) {
				maxLinNum = currentFileLines.size();
				wantFile = logFile;
			} // if
		} // logFile
		System.out.println("The best file is " + wantFile + ".");

		StatisticResult statisticResult = new StatisticResult(SiteList.size(),
				FeatureList.size());
		List<String> bestFileLines = DicccolUtilIO
				.loadFileToArrayList(homeDataDir + "log_" + featureIndex + "\\"
						+ wantFile);
		String currentLine = "";
		String[] currentLineArr;
		int currentRoundIndex = -1;
		for (int l = 0; l < bestFileLines.size(); l++) {
			currentLine = bestFileLines.get(l);
			if(currentLine.trim().length()==0)
				continue;
			currentLineArr = currentLine.split("\\s+");
			if (currentLineArr[1].trim().equals("Round")) {
				currentRoundIndex = Integer.valueOf(currentLineArr[2].trim());
				boolean roundOver = false;
				int currentSiteIndex = -1;
				List<List<Integer>> tmpAllFeaList = new ArrayList<List<Integer>>();
				while (!roundOver) {
					currentLine = bestFileLines.get(++l);
					currentLineArr = currentLine.split("\\s+");
					if (currentLineArr[1].trim().equals(
							"updateImprovmentStatus"))
						roundOver = true;
					else if (currentLineArr[0].trim().equals("Selected")) {
						List<Integer> tmpCurrentFeaList = new ArrayList<Integer>();
						currentLine = bestFileLines.get(++l);
						currentLine = currentLine.substring(1,
								currentLine.length() - 1);
						// currentLine = currentLine.replace(',', ' ');
						currentLineArr = currentLine.split(",");
						for (int i = 0; i < currentLineArr.length; i++) {
							int feaIndex = Integer.valueOf(currentLineArr[i]
									.trim());
							tmpCurrentFeaList.add(feaIndex);
							// statisticResult.feaDistribution[feaIndex][currentRoundIndex]++;
						} // for i
						tmpAllFeaList.add(tmpCurrentFeaList);
						if (currentRoundIndex == 0)
							statisticResult.previousFeatureList
									.add(tmpCurrentFeaList);
					} // if(currentLineArr[0].trim().equals("Selected"))
					else if (currentLine
							.contains("SMO_Classification finished!")) {
						currentSiteIndex++;
						currentLine = currentLine.replace('#', ' ');
						currentLine = currentLine.replace(':', ' ');
						currentLineArr = currentLine.split("\\s+");
						if (currentRoundIndex == 0) {
							statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][0] = Double
									.valueOf(currentLineArr[4].trim());
							statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][1] = Double
									.valueOf(currentLineArr[6].trim());
							statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][2] = Double
									.valueOf(currentLineArr[8].trim());
						} // currentRoundIndex==0
						else {
							if (Double.valueOf(currentLineArr[4].trim()) > statisticResult.optimizationProfile[currentRoundIndex - 1][currentSiteIndex][0]) {
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][0] = Double
										.valueOf(currentLineArr[4].trim());
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][1] = Double
										.valueOf(currentLineArr[6].trim());
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][2] = Double
										.valueOf(currentLineArr[8].trim());
								statisticResult.previousFeatureList.set(
										currentSiteIndex,
										tmpAllFeaList.get(currentSiteIndex));
							} // has improvement
							else {
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][0] = statisticResult.optimizationProfile[currentRoundIndex - 1][currentSiteIndex][0];
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][1] = statisticResult.optimizationProfile[currentRoundIndex - 1][currentSiteIndex][1];
								statisticResult.optimizationProfile[currentRoundIndex][currentSiteIndex][2] = statisticResult.optimizationProfile[currentRoundIndex - 1][currentSiteIndex][2];
							} // no improvement
						} // currentRoundIndex!=0
					} // if
				} // while this round is over

				// compute the feature distribution using the updated
				// previousFeatureList
				for (List<Integer> currentSiteFeatureList : statisticResult.previousFeatureList)
					for (Integer selectedFeatureIndex : currentSiteFeatureList)
						statisticResult.feaDistribution[selectedFeatureIndex][currentRoundIndex]++;
			} // if(currentLineArr[1].trim().equals("Round"))
		} // for l
		statisticResult.roundNum = currentRoundIndex;
		statisticResult.summarization();
		statisticResult.printStatisticResult();

		System.out.println("##################### analyzeLog log_"
				+ featureIndex + " finished! #####################");
		System.out.println("");
	}

	public static void main(String[] args) {
		MiccaiStatistics mainHandler = new MiccaiStatistics();
		String siteListFile = "SiteList.txt";
		String featureListFile = "FeatureList_Imputed_Over21.txt";
		mainHandler.init(siteListFile, featureListFile);
//		for (int i = 20; i <= 80; i = i + 5)
			mainHandler.analyzeLog(25);

	}

}
