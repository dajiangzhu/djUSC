package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class ACE_TMD {

	String[][][] subInfoArray = new String[261][2][4]; // [0]:name [1]:zyg(1:M
														// 2:D) [2]:age [3]:sex
														// (1:M 2:F)
	double[][] twinTMDistanceMean = new double[358][144];
	double[][] twinTMDistance = new double[358][261];
	List<double[][]> groupTMDistance = new ArrayList<double[][]>();

	private float calFeatureDis(double[] f1, double[] f2) {
		float dis = 0.0f;
		for (int i = 0; i < 144; i++) {
			dis += Math.pow(Math.abs(f1[i] - f2[i]), 2);
		}
		return dis;
	}

	private void initialSubNameArray() {
		System.out
				.println("*************************** Will initialize Subname Array...");
		// List<String> subPreList = DicccolUtilIO
		// .loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/DataNamePre_List_Twin_DTI105_Final.txt");
		// List<String> subList = DicccolUtilIO
		// .loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/DataName_List_Twin_DTI105_Final.txt");
		List<String> subPreList = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/DataNamePre_List_Twin_DTI105_Final.txt");
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/DataName_List_Twin_DTI105_Final.txt");
		List<String> subDemList = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/selectedDemoInfoCompact.txt");
		if (subPreList.size() != 261 || subList.size() != 522) {
			System.out
					.println("Error with reading DataNamePre or DataName file");
			System.exit(0);
		}
		for (int i = 0; i < subPreList.size(); i++) {
			String strCurrentPre = subPreList.get(i).trim();
			for (int j = 0; j < subList.size(); j++) {
				String strCurrentName = subList.get(j);
				String[] tmpParts = subDemList.get(j).split("\\s+");
				if (!strCurrentName.equalsIgnoreCase(tmpParts[0].trim())) {
					System.out
							.println("The names of current subjects are not correct! ");
					System.exit(0);
				}
				if (strCurrentName.startsWith(strCurrentPre + "1")) {
					subInfoArray[i][0][0] = strCurrentName;
					subInfoArray[i][0][1] = tmpParts[3].trim();
					subInfoArray[i][0][2] = tmpParts[2].trim();
					subInfoArray[i][0][3] = tmpParts[1].trim();
					continue;
				} // if
				if (strCurrentName.startsWith(strCurrentPre + "2")) {
					subInfoArray[i][1][0] = strCurrentName;
					subInfoArray[i][1][1] = tmpParts[3].trim();
					subInfoArray[i][1][2] = tmpParts[2].trim();
					subInfoArray[i][1][3] = tmpParts[1].trim();
					continue;
				} // if
			} // for j
		} // for i
	}

	private void calTwinTMDMean() {
		System.out
				.println("*************************** Will calculate TMDMean...");
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/DataName_List_Twin_DTI105_Final.txt");
		if (subList.size() != 522) {
			System.out
					.println("Error with reading DataNamePre or DataName file");
			System.exit(0);
		}
		for (int i = 0; i < subList.size(); i++) {
			System.out
			.println("Finished subject-"+i+"...");
			double[][] twinFeature = DicccolUtilIO.loadFileAsArray(
					"../../data/twin/TraceFeature/" + subList.get(i).trim()
							+ "_TracemapFeatures.txt", 358, 144);
			for (int d = 0; d < 358; d++)
				for (int v = 0; v < 144; v++)
					twinTMDistanceMean[d][v] += twinFeature[d][v];
		} // for all subjects
		for (int d = 0; d < 358; d++)
			for (int v = 0; v < 144; v++)
				twinTMDistanceMean[d][v] /= subList.size();
	}

	private void calTMDValue() {
		System.out
				.println("*************************** Will calculate TMD values...");
		for (int d = 0; d < 358; d++) {
			System.out.println("------------DICCCOL: " + d);
			List<String> outList = new ArrayList<String>();
			String HeadLine = "FamID,TMD_1,TMD_2,Zyg";
			outList.add(HeadLine);
			for (int t = 0; t < 261; t++) {
				System.out.println("Twin index: " + t);
				String tmpLine = "";
				// double[][] twinFeature_1 = DicccolUtilIO.loadFileAsArray(
				// "/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/"
				// +
				// subInfoArray[t][0]
				// + "_TracemapFeatures.txt", 358, 144);
				// double[][] twinFeature_2 = DicccolUtilIO.loadFileAsArray(
				// "/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/"
				// +
				// subInfoArray[t][1]
				// + "_TracemapFeatures.txt", 358, 144);
				double[][] twinFeature_1 = DicccolUtilIO.loadFileAsArray(
						"../../data/twin/TraceFeature/" + subInfoArray[t][0][0]
								+ "_TracemapFeatures.txt", 358, 144);
				double[][] twinFeature_2 = DicccolUtilIO.loadFileAsArray(
						"../../data/twin/TraceFeature/" + subInfoArray[t][1][0]
								+ "_TracemapFeatures.txt", 358, 144);
				String famID = subInfoArray[t][0][0].substring(0, 6);
				double tmd_1 = this.calFeatureDis(twinFeature_1[d],
						this.twinTMDistanceMean[d]);
				double tmd_2 = this.calFeatureDis(twinFeature_2[d],
						this.twinTMDistanceMean[d]);
				String zyg = subInfoArray[t][0][1];
				tmpLine = tmpLine + famID + "," + tmd_1 + "," + tmd_2 + ","
						+ zyg;
				outList.add(tmpLine);
			} // for all twins
			DicccolUtilIO.writeArrayListToFile(outList, "ACE_TMD_D_" + d
					+ ".csv");
		} // for all DICCCOLs
	}

	private void geneDataForPipeline() {
		System.out
		.println("*************************** Will generate pipeline inputs...");
		List<String> outInfoList = new ArrayList<String>();
		String HeadLine = "\"familyID\"\t\"subjectID1\"\t\"zygosity1\"\t\"TMDpath1\"\t\"Age1\"\t\"Sex1\"\t\"subjectID2\"\t\"zygosity2\"\t\"TMDpath2\"\t\"Age2\"\t\"Sex2\"";
		outInfoList.add(HeadLine);
		for (int t = 0; t < 261; t++) {
			System.out.println("Twin index: " + t);
			String tmpLine = "";
			List<String> outList1 = new ArrayList<String>();
			List<String> outList2 = new ArrayList<String>();
			double[][] twinFeature_1 = DicccolUtilIO.loadFileAsArray(
					"../../data/twin/TraceFeature/" + subInfoArray[t][0][0]
							+ "_TracemapFeatures.txt", 358, 144);
			double[][] twinFeature_2 = DicccolUtilIO.loadFileAsArray(
					"../../data/twin/TraceFeature/" + subInfoArray[t][1][0]
							+ "_TracemapFeatures.txt", 358, 144);
			for (int d = 0; d < 358; d++) {
				double tmd_1 = this.calFeatureDis(twinFeature_1[d],
						this.twinTMDistanceMean[d]);
				outList1.add(String.valueOf(tmd_1));
				double tmd_2 = this.calFeatureDis(twinFeature_2[d],
						this.twinTMDistanceMean[d]);
				outList2.add(String.valueOf(tmd_2));
			}
			DicccolUtilIO.writeArrayListToFile(outList1, "./twinACE/"+subInfoArray[t][0][0]
					+ "_TMDWithMean.txt");
			DicccolUtilIO.writeArrayListToFile(outList2, "./twinACE/"+subInfoArray[t][1][0]
					+ "_TMDWithMean.txt");
			tmpLine = tmpLine + subInfoArray[t][0][0].substring(0, 5) + "\t"
					+ subInfoArray[t][0][0] + "\t" + subInfoArray[t][0][1] + "\t"
					+ "/ifs/loni/faculty/thompson/four_d/dzhu/data/twinACE/" + subInfoArray[t][0][0] + "_TMDWithMean.txt"
					+ "\t" + subInfoArray[t][0][2] + "\t" + subInfoArray[t][0][3]
					+ "\t" + subInfoArray[t][1][0] + "\t" + subInfoArray[t][1][1]
					+ "\t" + "/ifs/loni/faculty/thompson/four_d/dzhu/data/twinACE/" + subInfoArray[t][1][0]
					+ "_TMDWithMean.txt" + "\t" + subInfoArray[t][1][2] + "\t"
					+ subInfoArray[t][1][3];
			outInfoList.add(tmpLine);
		} // for all twins
		DicccolUtilIO.writeArrayListToFile(outInfoList,"./twinACE/ACE_TMD_Info.txt");

	}

	public static void main(String[] args) {
		ACE_TMD mainHandler = new ACE_TMD();
		mainHandler.calTwinTMDMean();
		mainHandler.initialSubNameArray();
//		mainHandler.calTMDValue();
		mainHandler.geneDataForPipeline();
	}

}
