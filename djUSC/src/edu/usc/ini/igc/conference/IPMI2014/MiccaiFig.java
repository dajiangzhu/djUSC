package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.fiberBundleService;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class MiccaiFig {

	public float calTMFeatureDis_SD(List<Float> f1, List<Float> f2) {
		float dis = 0.0f;
		if (f1.size() == f2.size()) {
			for (int i = 0; i < f1.size(); i++) {
				dis += Math.pow(Math.abs(f1.get(i) - f2.get(i)), 2);
			}
		} else {
			System.out.println("the size of f1 and f2 are not equal!!");
		}
		return dis;
	}

	public float calGeoDis(djVtkFiberData oriFiber, djVtkFiberData recFiber) {
		djVtkPoint curOriPoint;
		djVtkPoint curRecPoint;

		float sumDis_ori = 0.0f;
		for (int i = 0; i < oriFiber.nCellNum; i++) {
			djVtkCell currentOriCell = oriFiber.getcell(i);
			float sumDis_rec = 0.0f;
			for (int j = 0; j < recFiber.nCellNum; j++) {
				djVtkCell currentRecCell = recFiber.getcell(j);
				float sumDis = 0.0f;
				for (int m = 0; m < currentOriCell.pointsList.size(); m++) {
					float minDis = 100.0f;
					curOriPoint = oriFiber.getPoint(i);
					for (int n = 0; n < currentRecCell.pointsList.size(); n++) {
						curRecPoint = recFiber.getPoint(j);
						float tmpDis = djVtkUtil.calDistanceOfPoints(
								curOriPoint, curRecPoint);
						if (tmpDis < minDis)
							minDis = tmpDis;
					} // for n
					sumDis += minDis;
				} // for m
				sumDis /= currentOriCell.pointsList.size();
				sumDis_rec += sumDis;
			} // for j
			sumDis_rec /= recFiber.nCellNum;
			sumDis_ori += sumDis_rec;
		} // for i
		sumDis_ori /= oriFiber.nCellNum;
		return sumDis_ori;
	}

	public void fig3_b() {
		List<String> modelInfo = DicccolUtilIO
				.loadFileToArrayList("/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/modelInfo");
		List<djVtkSurData> allSurData = new ArrayList<djVtkSurData>();
		List<String> subList = new ArrayList<String>();
		djVtkSurData surData;
		djVtkFiberData fiberData;
		System.out.println("Loading DICCCOL map...");
		int[][] tmpDicccolMap = DicccolUtilIO
				.loadFileAsIntArray(
						"/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/Dicccol.allMat",
						358, 11);
		System.out.println("Loading all sur and fiber data...");
		for (int i = 0; i < modelInfo.size(); i++) {
			String surName = modelInfo.get(i).split("\\s+")[0].trim();
			subList.add(surName.split("\\.")[0].trim());
			System.out.println("---Loading " + surName);
			surData = new djVtkSurData(
					"/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/"
							+ surName);
			allSurData.add(surData);
		} // for

		double[][] TMDistanceArray = new double[358][45];
		double[][] GeoDistanceArray = new double[358][45];
		fiberBundleService fiberBundleDescriptor1;
		fiberBundleService fiberBundleDescriptor2;
		float tmDistance = 0.0f;
		float geoDistance = 0.0f;
		for (int DICCCOLID = 0; DICCCOLID < 358; DICCCOLID++) {
			System.out
					.println("----------------------------------------------------Dealing with DICCCOL - "
							+ DICCCOLID);
			int subCount = 0;
			for (int s1 = 0; s1 < subList.size() - 1; s1++) {
				int ptID1 = tmpDicccolMap[DICCCOLID][s1];
				djVtkPoint tmPoint1 = allSurData.get(s1).getPoint(ptID1);
				fiberBundleDescriptor1 = new fiberBundleService();
				fiberBundleDescriptor1.setSeedPnt(tmPoint1);
				djVtkFiberData fiber1 = new djVtkFiberData(
						"/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/AllDicccolModelFibers/"
								+ subList.get(s1)
								+ "/fiber.roi.sub."
								+ DICCCOLID
								+ ".sid."
								+ tmpDicccolMap[DICCCOLID][s1] + ".vtk");
				fiberBundleDescriptor1.setFiberData(fiber1);
				fiberBundleDescriptor1.createFibersTrace();
				List<djVtkPoint> tracePointsList1 = fiberBundleDescriptor1
						.getAllPoints();
				List<Float> feature1 = fiberBundleDescriptor1
						.calFeatureOfTrace(tracePointsList1);
				for (int s2 = s1 + 1; s2 < subList.size(); s2++) {
					int ptID2 = tmpDicccolMap[DICCCOLID][s2];
					djVtkPoint tmPoint2 = allSurData.get(s2).getPoint(ptID2);
					fiberBundleDescriptor2 = new fiberBundleService();
					fiberBundleDescriptor2.setSeedPnt(tmPoint2);
					djVtkFiberData fiber2 = new djVtkFiberData(
							"/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/AllDicccolModelFibers/"
									+ subList.get(s2)
									+ "/fiber.roi.sub."
									+ DICCCOLID
									+ ".sid."
									+ tmpDicccolMap[DICCCOLID][s2] + ".vtk");
					fiberBundleDescriptor2.setFiberData(fiber2);
					fiberBundleDescriptor2.createFibersTrace();
					List<djVtkPoint> tracePointsList2 = fiberBundleDescriptor2
							.getAllPoints();
					List<Float> feature2 = fiberBundleDescriptor2
							.calFeatureOfTrace(tracePointsList2);

					// System.out.println("##Calculating the TMDistance...");
					tmDistance = this.calTMFeatureDis_SD(feature1, feature2);
					TMDistanceArray[DICCCOLID][subCount] = tmDistance;
					// System.out.println("TMDistance = " + tmDistance);
					// calculate GeoDistance between the original fiber and the
					// reconstructed fiber
					// System.out.println("##Calculating the GeoDistance...");
					geoDistance = this.calGeoDis(fiber1, fiber2);
					GeoDistanceArray[DICCCOLID][subCount] = geoDistance;
					// System.out.println("GeoDistance = " + geoDistance);
					subCount++;
				} // for s2
			} // for s1
		} // for all the DICCCOLs
		DicccolUtilIO.writeArrayToFile(TMDistanceArray, 358, 45, " ",
				"/home/dzhu/workspace/MICCAI2015/TMDistance.txt");
		DicccolUtilIO.writeArrayToFile(GeoDistanceArray, 358, 45, " ",
				"/home/dzhu/workspace/MICCAI2015/GeoDistance.txt");

	}

	public void fig3_a(String subID) {
		// combine the sublist
		System.out.println("Combine the subList....");
		List<String> allSubList = new ArrayList<String>();
		List<String> ADSubList = new ArrayList<String>();
		List<String> MCISubList = new ArrayList<String>();
		List<String> CNSubList = new ArrayList<String>();
		List<String> tmpSubList;
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_AD_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			ADSubList.add(sub_ID);
		}
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_MCI_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			MCISubList.add(sub_ID);
		}
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_CN_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			CNSubList.add(sub_ID);
		}
		double[][] TMDistanceArray = new double[358][1];
		double[][] GeoDistanceArray = new double[358][1];

		// Load fibers and calculate the distance
		System.out
				.println("Begin to load fibers and calculate the distance....");
		String group = "";
		// String subID = "";
		float tmDistance = 0.0f;
		float geoDistance = 0.0f;
		int subCount = 0;
		// for (int s = 0; s < allSubList.size(); s++) {
		// subID = allSubList.get(s);
		if (ADSubList.contains(subID))
			group = "AD";
		if (MCISubList.contains(subID))
			group = "MCI";
		if (CNSubList.contains(subID))
			group = "CN";
		System.out.println("++++++++++++++++++++SubID: " + subID + "  Group: "
				+ group);
		djVtkSurData surData = new djVtkSurData(
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"
						+ subID + ".surf.reg.asc.vtk");
		int[][] tmpDicccolMap = DicccolUtilIO.loadFileAsIntArray(
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"
						+ subID + ".allMat", 358, 11);
		fiberBundleService fiberBundleDescriptor = new fiberBundleService();

		// for each DICCCOL
		for (int DICCCOLID = 0; DICCCOLID < 358; DICCCOLID++) {
			System.out.println("---------Dealing with DICCCOL - " + DICCCOLID);
			int ptID = tmpDicccolMap[DICCCOLID][10];
			djVtkPoint tmPoint = surData.getPoint(ptID);
			fiberBundleDescriptor.setSeedPnt(tmPoint);
			// get TraceFeature of original fiber
			djVtkFiberData oriFiber = new djVtkFiberData(
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI/DICCCOL_Fibers/"
							+ subID + "/fiber.roi.sub." + DICCCOLID + ".sid."
							+ ptID + ".vtk");
			fiberBundleDescriptor.setFiberData(oriFiber);
			fiberBundleDescriptor.createFibersTrace();
			List<djVtkPoint> oriTracePointsList = fiberBundleDescriptor
					.getAllPoints();
			List<Float> oriFeature = fiberBundleDescriptor
					.calFeatureOfTrace(oriTracePointsList);
			// get TraceFeature of reconstructed fiber
			djVtkFiberData recFiber = new djVtkFiberData(
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
							+ group + "/DICCCOL" + DICCCOLID + "/Sub." + subID
							+ ".DICCCOL." + DICCCOLID + "_RecFiber.vtk");
			fiberBundleDescriptor.setFiberData(recFiber);
			fiberBundleDescriptor.createFibersTrace();
			List<djVtkPoint> recTracePointsList = fiberBundleDescriptor
					.getAllPoints();
			List<Float> recFeature = fiberBundleDescriptor
					.calFeatureOfTrace(recTracePointsList);
			// calculate TMDistance between the original fiber and the
			// reconstructed fiber
			// System.out.println("##Calculating the TMDistance...");
			tmDistance = this.calTMFeatureDis_SD(oriFeature, recFeature);
			TMDistanceArray[DICCCOLID][subCount] = tmDistance;
			// System.out.println("TMDistance = " + tmDistance);
			// calculate GeoDistance between the original fiber and the
			// reconstructed fiber
			// System.out.println("##Calculating the GeoDistance...");
			geoDistance = this.calGeoDis(oriFiber, recFiber);
			GeoDistanceArray[DICCCOLID][subCount] = geoDistance;
			// System.out.println("GeoDistance = " + geoDistance);
		} // for all the DICCCOLs
		subCount++;
		// } // for all the subjects
		DicccolUtilIO.writeArrayToFile(TMDistanceArray, 358, 1, " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/"
						+ subID + ".TMDistance.txt");
		DicccolUtilIO.writeArrayToFile(GeoDistanceArray, 358, 1, " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/"
						+ subID + ".GeoDistance.txt");
	}

	public void fig3_c() {
		// combine the sublist
		System.out.println("Combine the subList....");
		List<String> allSubList = new ArrayList<String>();
		List<String> ADSubList = new ArrayList<String>();
		List<String> MCISubList = new ArrayList<String>();
		List<String> CNSubList = new ArrayList<String>();
		List<String> tmpSubList;
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_AD_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			ADSubList.add(sub_ID);
		}
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_MCI_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			MCISubList.add(sub_ID);
		}
		tmpSubList = DicccolUtilIO
				.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/MICCAI_CN_SubList.txt");
		for (int i = 0; i < tmpSubList.size(); i++) {
			String sub_ID = tmpSubList.get(i).split("\\s+")[0].trim();
			allSubList.add(sub_ID);
			CNSubList.add(sub_ID);
		}
		double[][] TMDistanceArray = new double[358][allSubList.size()];
		double[][] GeoDistanceArray = new double[358][allSubList.size()];
		for(int i=0;i<allSubList.size();i++)
		{
			String subID = allSubList.get(i);
			System.out.println("---------Dealing with sub - " + subID);
			double[][] tmpTMDistanceArray = DicccolUtilIO.loadFileAsArray("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/"+subID+".TMDistance.txt", 358, 1);
			double[][] tmpGeoDistanceArray = DicccolUtilIO.loadFileAsArray("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/"+subID+".GeoDistance.txt", 358, 1);
			for(int d=0;d<358;d++)
			{
				TMDistanceArray[d][i] = tmpTMDistanceArray[d][0];
				GeoDistanceArray[d][i] = tmpGeoDistanceArray[d][0];
			} //for d
		} //for i
		DicccolUtilIO.writeArrayToFile(TMDistanceArray, 358, allSubList.size(), " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/all.TMDistance.txt");
		DicccolUtilIO.writeArrayToFile(GeoDistanceArray, 358, allSubList.size(), " ",
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/fig3/all.GeoDistance.txt");

	}
	
	public void fig4()
	{
		djVtkSurData ptData = new djVtkSurData("../surf10to10.wavelet.5.vtk.358DICCCOLPts.vtk");
		djVtkSurData ballTemplate = new djVtkSurData("../sphere_radius2.vtk");
		List<String> attList = DicccolUtilIO.loadFileToArrayList("./MICCAI2015/MCI_attr.txt");
		GenerateDICCCOL drawDicccolBall = new GenerateDICCCOL();
		drawDicccolBall.GenerateDICCCOLBallWithColor(ptData, ballTemplate, attList, "./MICCAI2015/MCI_DICCCOL_Color.vtk");

	}

	public static void main(String[] args) {
		MiccaiFig mainHandler = new MiccaiFig();
		// for fig3_a
		// String subID = args[0].trim();
		// mainHandler.fig3_a(subID);
		// for fig3_b
//		mainHandler.fig3_b();
//		mainHandler.fig3_c();
		mainHandler.fig4();

	}

}
