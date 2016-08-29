package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class AnaLassoResult {

	public void anaTwoParts() {
		int featureNum = 102;
		double frequencyThreshold = 500.0;

		double[] lassoResult_part1 = new double[featureNum];
		// double[] lassoResult_part2 = new double[featureNum];
		List<String> outList = new ArrayList<String>();
		for (int i = 1; i <= 1000; i++) {
			System.out.println("***********  i: " + i + " ************");
			List<String> currentLassoResultList_part1 = DicccolUtilIO
					.loadFileToArrayList("E:\\GITWorkSpace\\djUSC\\w_RDD_All_ICV\\w_"
							+ i + "_RDD_All_ICV.txt\\part-00000");
			// List<String> currentLassoResultList_part2 = DicccolUtilIO
			// .loadFileToArrayList("E:\\GITWorkSpace\\djUSC\\w_RDD_part2\\w_"
			// + i + "_RDD_part2.txt\\part-00000");
			if (currentLassoResultList_part1.size() != featureNum) {
				System.out
						.println("currentLassoResultList.size()!=featureNum  i: "
								+ i);
				System.exit(0);
			}

			for (int f = 0; f < featureNum; f++)
				if (Math.abs(Double.valueOf(currentLassoResultList_part1.get(f)
						.trim())) > 0.0)
					lassoResult_part1[f]++;

			// for (int f = 0; f < featureNum; f++)
			// if (Math.abs(Double.valueOf(currentLassoResultList_part2.get(f)
			// .trim())) > 0.0)
			// lassoResult_part2[f]++;
		} // for i

		String line = "";
		for (int f = 0; f < featureNum; f++)
			if (lassoResult_part1[f] >= frequencyThreshold) {
				System.out.println("Selected: " + f);
				line += "1.0,";
			} else
				line += "0.0,";
		outList.add(line);
		DicccolUtilIO.writeArrayListToFile(outList,
				"SelectedFeatureFromLassoFrequency_ICV.txt");

		outList.clear();
		for (int f = 0; f < featureNum; f++)
			outList.add(String.valueOf(lassoResult_part1[f]));
		DicccolUtilIO.writeArrayListToFile(outList,
				"LassoFeatureFrequency_ICV.txt");
	}

	public void anaAll() {
		int featureNum = 102;
		double frequencyThreshold = 1000.0;

		double[] lassoResult = new double[featureNum];
		List<String> outList = new ArrayList<String>();
		for (int i = 1; i <= 1000; i++) {
			System.out.println("***********  i: " + i + " ************");
			List<String> currentLassoResultList = DicccolUtilIO
					.loadFileToArrayList("E:\\GITWorkSpace\\djUSC\\w_RDD_All_ICV.txt\\w_"
							+ i + "_RDD_All_ICV.txt\\part-00000");

			if (currentLassoResultList.size() != featureNum) {
				System.out
						.println("currentLassoResultList.size()!=featureNum  i: "
								+ i);
				System.exit(0);
			}

			for (int f = 0; f < featureNum; f++)
				if (Math.abs(Double.valueOf(currentLassoResultList.get(f)
						.trim())) > 0.0)
					lassoResult[f]++;

		} // for i

		String line = "";
		for (int f = 0; f < featureNum; f++)
			if (lassoResult[f] >= frequencyThreshold) {
				System.out.println("Selected: " + f);
				line += "1.0,";
			} else
				line += "0.0,";
		outList.add(line);
		DicccolUtilIO.writeArrayListToFile(outList,
				"SelectedFeatureFromLassoFrequencyAll_ICV.txt");

		line = "";
		outList.clear();
		for (int f = 0; f < featureNum; f++)
			outList.add(String.valueOf(lassoResult[f]));
		DicccolUtilIO.writeArrayListToFile(outList,
				"LassoFeatureFrequencyAll_ICV.txt");

	}

	public static void main(String[] args) {
		AnaLassoResult mainHandler = new AnaLassoResult();
		mainHandler.anaAll();

	}

}
