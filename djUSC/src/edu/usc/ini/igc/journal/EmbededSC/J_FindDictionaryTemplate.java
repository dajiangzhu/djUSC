package edu.usc.ini.igc.journal.EmbededSC;

import edu.uga.DICCCOL.DicccolUtilIO;

public class J_FindDictionaryTemplate {

	public int optDicIndex;
	public int dicSize = 400;
	public double pValueThresholdUB = 0.95;
	public double pValueThresholdLB = 0.5;
	public double pValueThresholdStep = 0.05;
	public double voteRateThreshold = 0.5;
	public int subStartID = 1;
	public int subEndID = 68;

	public void findTheTemplate() {
		System.out.println("**********************   optDicIndex: "
				+ optDicIndex + "   **********************");
		System.out.println("**********************   dicSize: " + dicSize
				+ "   **********************");
		System.out.println("**********************   pValueThresholdUB: "
				+ pValueThresholdUB + "   **********************");
		System.out.println("**********************   pValueThresholdLB: "
				+ pValueThresholdLB + "   **********************");
		System.out.println("**********************   pValueThresholdStep: "
				+ pValueThresholdStep + "   **********************");
		System.out.println("**********************   voteRateThreshold: "
				+ voteRateThreshold + "   **********************");
		System.out.println("**********************   subStartID: " + subStartID
				+ "   **********************");
		System.out.println("**********************   subEndID: " + subEndID
				+ "   **********************");

		int subNum = subEndID - subStartID + 1;
		double[][] voteMap = new double[subNum][dicSize];
		boolean templateFound = false;

		// Record regarding current max vote rate
		int subIDWithMaxVoteRate = -1;
		int dicIndexWithMaxVoteRate = -1;
		double maxVoteRate = 0.0;

		double currentThreshold = pValueThresholdUB + pValueThresholdStep;
		do {
			currentThreshold = currentThreshold - pValueThresholdStep;
			System.out
					.println("######################   Using currentThreshold: "
							+ currentThreshold);
			for (int subID = subStartID; subID < (subEndID + 1); subID++) {
				System.out.println("----------   Centered subject: " + subID);
				// Loading the CorrMap
				double[][] currentCorrMap = DicccolUtilIO.loadFileAsArray(
						"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/OptDicIndex_"
								+ optDicIndex + "/Center_Sub" + subID
								+ "_OptDicIndex_" + this.optDicIndex + ".txt",
						dicSize, dicSize * 68);
				// Calculating the vote status
				for (int d = optDicIndex; d < dicSize; d++) {
					for (int compareSubID = subStartID; compareSubID < (subEndID + 1); compareSubID++) {
						if (compareSubID != subID)
							for (int tmpD = 0; tmpD < dicSize; tmpD++) {
								if (currentCorrMap[d][(compareSubID - 1)
										* dicSize + tmpD] >= currentThreshold) {
									voteMap[subID - subStartID][d]++;
									break;
								} // if current pValue is larger than threshold
							} // for tmpD
					} // for all the subjects to be compared
					voteMap[subID - subStartID][d] = voteMap[subID - subStartID][d]
							/ (subNum - 1);
					// System.out.println("voteRate: "+voteMap[subID - 1][d]);
					if (voteMap[subID - subStartID][d] >= voteRateThreshold) {
						System.out.println("$$$ subID: " + subID
								+ " and dicIndex: " + d
								+ " (Candidate granted!)");
						templateFound = true;
						if (voteMap[subID - subStartID][d] > maxVoteRate) {
							System.out
									.println("$$$ Top candidate granted! (VR = "
											+ voteMap[subID - subStartID][d]
											+ ")");
							subIDWithMaxVoteRate = subID;
							dicIndexWithMaxVoteRate = d;
							maxVoteRate = voteMap[subID - subStartID][d];
						} // if > maxVoteRate
					} // if > voteRateThreshold
				} // for d
			} // for each subject as center
		} while (!templateFound);

		// Finding the candidate of each subject
		if (templateFound) {
			System.out
					.println("######################   Finding the candidates of each subject (Center subject: "
							+ subIDWithMaxVoteRate
							+ " dicIndex: "
							+ dicIndexWithMaxVoteRate
							+ " voteRate: "
							+ maxVoteRate + ") ");
			double[][] candidateMap = new double[subNum][3];
			candidateMap[subIDWithMaxVoteRate - subStartID][0] = subIDWithMaxVoteRate;
			candidateMap[subIDWithMaxVoteRate - subStartID][1] = dicIndexWithMaxVoteRate;
			candidateMap[subIDWithMaxVoteRate - subStartID][2] = 1.0;
			double[][] currentCorrMap = DicccolUtilIO.loadFileAsArray(
					"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/OptDicIndex_"
							+ optDicIndex + "/Center_Sub"
							+ subIDWithMaxVoteRate + "_OptDicIndex_"
							+ this.optDicIndex + ".txt", dicSize, dicSize * 68);
			for (int compareSubID = subStartID; compareSubID < (subEndID + 1); compareSubID++)
				if (subIDWithMaxVoteRate != compareSubID) {
					double tmpMax = 0.0;
					for (int tmpD = 0; tmpD < dicSize; tmpD++) {
						if (currentCorrMap[dicIndexWithMaxVoteRate][(compareSubID - 1)
								* dicSize + tmpD] > tmpMax) {
							candidateMap[compareSubID - subStartID][0] = compareSubID;
							candidateMap[compareSubID - subStartID][1] = tmpD;
							candidateMap[compareSubID - subStartID][2] = currentCorrMap[dicIndexWithMaxVoteRate][(compareSubID - 1)
									* dicSize + tmpD];
							tmpMax = currentCorrMap[dicIndexWithMaxVoteRate][(compareSubID - 1)
									* dicSize + tmpD];
						} // if current pValue is larger than threshold
					} // for tmpD
				} // if
			System.out
					.println("######################   All Candidates:  ######################");
			for (int i = 0; i < subNum; i++)
				System.out.println(candidateMap[i][0] + ": "
						+ candidateMap[i][1] + "(" + candidateMap[i][2] + ")");
		} // if templateFound is true

	}

	public static void main(String[] args) {
		if (args.length == 8) {
			J_FindDictionaryTemplate mainHandler = new J_FindDictionaryTemplate();
			mainHandler.optDicIndex = Integer.valueOf(args[0].trim()); // 0-?
			mainHandler.dicSize = Integer.valueOf(args[1].trim()); // 400
			mainHandler.voteRateThreshold = Double.valueOf(args[2].trim());
			mainHandler.pValueThresholdUB = Double.valueOf(args[3].trim());
			mainHandler.pValueThresholdLB = Double.valueOf(args[4].trim());
			mainHandler.pValueThresholdStep = Double.valueOf(args[5].trim());
			mainHandler.subStartID = Integer.valueOf(args[6].trim()); // 1-68?
			mainHandler.subEndID = Integer.valueOf(args[7].trim()); // 0-?
			mainHandler.findTheTemplate();
		} else
			System.out
					.println("Input: optDicIndex (0-399), dicSize(400), voteRateThreshold(0.5), pValueThresholdUB(0.95), pValueThresholdLB(0.5), pValueThresholdStep(0.05), subStartID(1) and subEndID(68)");
	}

}
