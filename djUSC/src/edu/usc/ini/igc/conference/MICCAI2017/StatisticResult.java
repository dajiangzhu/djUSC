package edu.usc.ini.igc.conference.MICCAI2017;

import java.util.ArrayList;
import java.util.List;

public class StatisticResult {
	int maxRoundIndex = 10;

	public StatisticResult(int siteNum, int feaNum) {
		this.siteNum = siteNum;
		this.feaNum = feaNum;
		accuracyImprovment = new double[siteNum][3]; // 0-ACC 1-SPE 2-SEN
		avgAccuracyImprovment = new double[3]; // 0-ACC 1-SPE 2-SEN
		optimizationProfile = new double[maxRoundIndex][siteNum][3]; // 0-ACC
																		// 1-SPE
																		// 2-SEN
		feaDistribution = new double[feaNum][maxRoundIndex];
		numOfSelectedFeatures = new double[maxRoundIndex];
		avgFeaWeight = new double[maxRoundIndex];
	}

	public int siteNum = -1;
	public int feaNum = -1;
	public int roundNum = -1;
	List<List<Integer>> previousFeatureList = new ArrayList<List<Integer>>();
	public double[][] accuracyImprovment;
	public double[] avgAccuracyImprovment;
	public double[][][] optimizationProfile; // for Fig.1
	public double[][] feaDistribution; // for Fig.2
	public double[] numOfSelectedFeatures;
	public double[] avgFeaWeight;

	public void summarization() {
		for (int s = 0; s < this.siteNum; s++) {
			accuracyImprovment[s][0] = this.optimizationProfile[this.roundNum - 1][s][0]
					- this.optimizationProfile[0][s][0];
			accuracyImprovment[s][1] = this.optimizationProfile[this.roundNum - 1][s][1]
					- this.optimizationProfile[0][s][1];
			accuracyImprovment[s][2] = this.optimizationProfile[this.roundNum - 1][s][2]
					- this.optimizationProfile[0][s][2];

			avgAccuracyImprovment[0] += accuracyImprovment[s][0];
			avgAccuracyImprovment[1] += accuracyImprovment[s][1];
			avgAccuracyImprovment[2] += accuracyImprovment[s][2];
		} // for s
		for (int i = 0; i < 3; i++)
			avgAccuracyImprovment[i] /= this.siteNum;
		
		for(int r=0;r<this.roundNum;r++)
		{
			double countSelectedFeature=0.0;
			double countOverLapse = 0.0;
			for(int f=0;f<this.feaNum;f++)
				if(feaDistribution[f][r]>0)
				{
					countSelectedFeature++;
					countOverLapse += feaDistribution[f][r];
				}
			numOfSelectedFeatures[r] = countSelectedFeature;
			avgFeaWeight[r] = countOverLapse/countSelectedFeature;
		}
	}

	public void printStatisticResult() {
		System.out
				.println("*******************************PRINT STATISTIC RESULT INFO************************************************");
		System.out.println("######################################There are "
				+ this.roundNum + " Rounds!");
		System.out
				.println("######################################avgAccuracyImprovment:");
		for (int i = 0; i < 3; i++)
			System.out.println(avgAccuracyImprovment[i]);

		System.out
				.println("######################################accuracyImprovment:");
		for (int s = 0; s < this.siteNum; s++) {
			System.out.println("----------------- Site-" + s + ":");
			System.out
					.println(accuracyImprovment[s][0] + " "
							+ accuracyImprovment[s][1] + " "
							+ accuracyImprovment[s][2]);
		}

		System.out
				.println("######################################optimizationProfile:");

		for (int s = 0; s < this.siteNum; s++) {
			System.out.println("-------Site-" + s + ":");
			for (int r = 0; r < this.roundNum; r++) {
				System.out.println("----------------- Round: " + r + ":");
				System.out.println(optimizationProfile[r][s][0] + " "
						+ optimizationProfile[r][s][1] + " "
						+ optimizationProfile[r][s][2]);
			}
		}

		System.out
				.println("######################################optimizationProfile:");
		for (int f = 0; f < this.feaNum; f++) {
			String tmpStr = "";
			for (int r = 0; r < this.roundNum; r++)
				tmpStr += feaDistribution[f][r] + " ";
			System.out.println("Feature " + (f + 1) + ": " + tmpStr);
		}
		
		System.out
		.println("######################################numOfSelectedFeatures:");
		for (int r = 0; r < this.roundNum; r++)
			System.out.println(numOfSelectedFeatures[r]);
		
		System.out
		.println("######################################avgFeaWeight:");
		for (int r = 0; r < this.roundNum; r++)
			System.out.println(avgFeaWeight[r]);
		
		
		
		System.out
				.println("*******************************PRINT STATISTIC RESULT INFO FINISHED!************************************************");
	}

}
