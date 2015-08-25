package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;
import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;

public class statTMDistance {

	String[][] subNameArray = new String[261][2];
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
		System.out.println("*************************** Will initialize Subname Array...");
		List<String> subPreList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/DataNamePre_List_Twin_DTI105_Final.txt");
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/DataName_List_Twin_DTI105_Final.txt");
		if (subPreList.size() != 261 || subList.size() != 522) {
			System.out
					.println("Error with reading DataNamePre or DataName file");
			System.exit(0);
		}
		for (int i = 0; i < subPreList.size(); i++) {
			String strCurrentPre = subPreList.get(i).trim();
			for (int j = 0; j < subList.size(); j++) {
				String strCurrentName = subList.get(j);
				if (strCurrentName.startsWith(strCurrentPre + "1")) {
					subNameArray[i][0] = strCurrentName;
					continue;
				} // if
				if (strCurrentName.startsWith(strCurrentPre + "2")) {
					subNameArray[i][1] = strCurrentName;
					continue;
				} // if
			} // for j
		} // for i
	}

	private void calTwinDMDistance() {
		System.out
				.println("***************************Will calculate the TraceMap distance of twins...");
		for (int t = 0; t < 261; t++) {
//			System.out.println("Twin index: " + t);
			double[][] twinFeature_1 = DicccolUtilIO.loadFileAsArray(
					"/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/" + subNameArray[t][0]
							+ "_TracemapFeatures.txt", 358, 144);
			double[][] twinFeature_2 = DicccolUtilIO.loadFileAsArray(
					"/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/" + subNameArray[t][1]
							+ "_TracemapFeatures.txt", 358, 144);
			for (int d = 0; d < 358; d++)
				twinTMDistance[d][t] = this.calFeatureDis(twinFeature_1[d],
						twinFeature_2[d]);
		} // for all twins
	}

	private void generateGroupTMDistanceList(int shuffleTimes) {
		System.out
				.println("***************************Will calculate the TraceMap distance of group with "
						+ shuffleTimes + " times shuffling...");
		for (int s = 0; s < shuffleTimes; s++) {
			System.out.println("Shuffle - " + (s + 1) + "...");
			double[][] currentShufflingTMDistance = new double[358][261];
			List<Integer> currentShufflingList = DicccolUtil.geneRandom(261,
					261); // including 1-261
			for (int i = 0; i < 261; i++) {
				int currentTwin1;
				int currentTwin1_ind;
				int currentTwin2;
				int currentTwin2_ind;

				if (i == 260) {
					currentTwin1 = currentShufflingList.get(i) - 1;
					currentTwin1_ind = DicccolUtil.geneRandom(1, 2).get(0) - 1;
					currentTwin2 = currentShufflingList.get(0) - 1;
					currentTwin2_ind = DicccolUtil.geneRandom(1, 2).get(0) - 1;

				} else {
					currentTwin1 = currentShufflingList.get(i) - 1;
					currentTwin1_ind = DicccolUtil.geneRandom(1, 2).get(0) - 1;
					currentTwin2 = currentShufflingList.get(i + 1) - 1;
					currentTwin2_ind = DicccolUtil.geneRandom(1, 2).get(0) - 1;
				}

//				System.out.println("Calculate between " + currentTwin1 + ":"
//						+ subNameArray[currentTwin1][currentTwin1_ind]
//						+ " and " + currentTwin2 + ":"
//						+ subNameArray[currentTwin2][currentTwin2_ind]);
				double[][] feature_1 = DicccolUtilIO.loadFileAsArray(
						"/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/"
								+ subNameArray[currentTwin1][currentTwin1_ind]
								+ "_TracemapFeatures.txt", 358, 144);
				double[][] feature_2 = DicccolUtilIO.loadFileAsArray(
						"/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TraceFeature/"
								+ subNameArray[currentTwin2][currentTwin2_ind]
								+ "_TracemapFeatures.txt", 358, 144);
				for (int d = 0; d < 358; d++)
					currentShufflingTMDistance[d][i] = this.calFeatureDis(
							feature_1[d], feature_2[d]);
			} // for i
			groupTMDistance.add(currentShufflingTMDistance);
		} // for s
	}

	private double[][] chang2DArray(double[] oriArray, int dim) {
		double[][] changedArray = new double[dim][1];
		for (int i = 0; i < dim; i++)
			changedArray[i][0] = oriArray[i];
		return changedArray;
	}

	private void calculatePValue(int shuffleTimes)
			throws MatlabConnectionException, MatlabInvocationException {
		System.out.println("***************************Will calculate pValues...");
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
		double[][] pValueM = new double[358][shuffleTimes];
		for (int s = 0; s < groupTMDistance.size(); s++) {
			System.out.println("shuffle:"+(s+1));
			double[][] currentShufflingTMDistance = groupTMDistance.get(s);
			for (int d = 0; d < 358; d++) {

				processor.setNumericArray("dis_twin", new MatlabNumericArray(
						this.chang2DArray(twinTMDistance[d], 261), null));
				processor.setNumericArray("dis_group", new MatlabNumericArray(
						this.chang2DArray(currentShufflingTMDistance[d], 261),
						null));
				proxy.eval("[h p] = ttest2(dis_twin,dis_group,0.05,'left','unequal')");
				double pValueTmp = ((double[]) proxy.getVariable("p"))[0];
				if(d%10==0)
					System.out.println("DICCCOL-"+d+": "+pValueTmp);
				pValueM[d][s] = pValueTmp;
			} // for all dicccols
		} // for all shuffleing
		DicccolUtilIO.writeArrayToFile(pValueM, 358, shuffleTimes, " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TD_pValue_" + shuffleTimes + ".txt");
		DicccolUtilIO.writeVtkMatrix1(pValueM, 358, shuffleTimes, "/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TD_pValue_"
				+ shuffleTimes + ".vtk");
	}
	
	private void printTMDistanceM(int shuffleTimes)
	{
		System.out
		.println("***************************Will print the TraceMap distance matrix ...");
		DicccolUtilIO.writeArrayToFile(twinTMDistance, 358, 261, " ", "/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TMDistanceM_Twin_" + shuffleTimes + ".txt");
		for(int s=0;s<groupTMDistance.size();s++)
		{
			double[][] currentShufflingTMDistance = groupTMDistance.get(s);
			DicccolUtilIO.writeArrayToFile(currentShufflingTMDistance, 358, 261, " ", "/ifs/loni/faculty/thompson/four_d/dzhu/ISBI2014/TMDistanceM_Group."+s+"_" + shuffleTimes + ".txt");
		}
	}

	public static void main(String[] args) throws MatlabConnectionException,
			MatlabInvocationException {
		if (args.length == 1) {
			int shuffleTimes = Integer.valueOf(args[0].trim());
			statTMDistance mainHandler = new statTMDistance();
			mainHandler.initialSubNameArray();
			mainHandler.calTwinDMDistance();
			mainHandler.generateGroupTMDistanceList(shuffleTimes);
			mainHandler.calculatePValue(shuffleTimes);
			mainHandler.printTMDistanceM(shuffleTimes);
		} else
			System.out.println("Error with paramater! Input shuffleTimes...");

	}

}
