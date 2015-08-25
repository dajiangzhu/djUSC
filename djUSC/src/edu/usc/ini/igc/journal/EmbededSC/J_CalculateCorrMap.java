package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class J_CalculateCorrMap {

	public int optDicIndex;
	public int currentSubID;
	public int dicSize;
	public double[][] correMap;
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
		correMap = new double[dicSize][dicSize * 68];
		System.out
				.println("######################   Loading File  ##########################");
		for (int s = 1; s <= 68; s++) {
			String curFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/OptDicIndex_"
					+ optDicIndex
					+ "/sub_"
					+ s
					+ "_round_"
					+ optDicIndex
					+ "_D.txt";
//			String curFile = "/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/HCP/TaskFMRI/OptDicIndex_"
//					+ optDicIndex
//					+ "/sub_"
//					+ s
//					+ "_round_"
//					+ optDicIndex
//					+ "_D.txt";
			System.out.println("Loading : " + curFile);
			double[][] curSigM = this.trsposeM(
					DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize),
					tSize, dicSize);
			allDMatrix.add(curSigM);
		} // for s
		System.out
				.println("######################   Loading File End! ##########################");
		// *************initial correlation handler
		correlation = new Correlation();
	}

	public void calCorreMap() {
		System.out
				.println("######################   Calculating the CorreMap  ##########################");
		for (int d = optDicIndex; d < this.dicSize; d++) {
			double[] currentD = allDMatrix.get(currentSubID-1)[d];
			System.out.println("Calculating the "+d+"th dictionary item...");
			for (int s = 1; s <= 68; s++) {
				if (s != currentSubID) {
					for (int tmpD = optDicIndex; tmpD < this.dicSize; tmpD++) {
						double[] compareD = allDMatrix.get((s-1))[tmpD];
						double tmpCorreValue = correlation
								.Correlation_Pearsons(currentD, compareD);
						correMap[d][(s - 1) * dicSize + tmpD] = tmpCorreValue;
					} // for tmpD
				} // if
			} // for s
		} // for d
		System.out
				.println("######################   Calculating the CorreMap End!  ##########################");

		System.out
				.println("######################   Writing the CorreMap  ##########################");
		DicccolUtilIO.writeArrayToFile(correMap, dicSize, dicSize * 68, " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/OptDicIndex_"
						+ optDicIndex + "/Center_Sub" + this.currentSubID
						+ "_OptDicIndex_" + this.optDicIndex + ".txt");
//		DicccolUtilIO.writeArrayToFile(correMap, dicSize, dicSize * 68, " ",
//				"/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/HCP/TaskFMRI/OptDicIndex_"
//						+ optDicIndex + "/Center_Sub" + this.currentSubID
//						+ "_OptDicIndex_" + this.optDicIndex + ".txt");
		System.out
				.println("######################   Writing the CorreMap End!  ##########################");

	}

	public static void main(String[] args) {
		if (args.length == 3) {
			J_CalculateCorrMap mainHandler = new J_CalculateCorrMap();
			mainHandler.optDicIndex = Integer.valueOf(args[0].trim()); // 0-?
			mainHandler.currentSubID = Integer.valueOf(args[1].trim()); // 1-68
			mainHandler.dicSize = Integer.valueOf(args[2].trim()); // 400
			mainHandler.initialization();
			mainHandler.calCorreMap();
		} else
			System.out.println("Input: optDicIndex (0-399), currentSubID(1-68), dicSize(400)");
	}

}
