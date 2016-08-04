package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.fiberBundleService;
import edu.uga.DICCCOL.stat.SimpleTTest;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class DICCCOL_ADNI_SC_Analysis {

	public int RoundNum = 100;
	public int SubsNum = 20;

	public void anaCodingResult(String group, String subID) {
		djVtkSurData surData = new djVtkSurData(
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"
						+ subID + ".surf.reg.asc.vtk");
		int[][] tmpDicccolMap = DicccolUtilIO.loadFileAsIntArray(
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"
						+ subID + ".allMat", 358, 11);

		for (int DICCCOLID = 0; DICCCOLID < 358; DICCCOLID++) {
			System.out
					.println("$$$$$$$$$$$$$ DICCCOL: "
							+ DICCCOLID
							+ " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

			// Fiber Encoding
			System.out.println("Beging Fiber Encoding: ...");
			TraceMapService ts = new TraceMapService();
			int ptID = tmpDicccolMap[DICCCOLID][10];
			djVtkFiberData curFiber = new djVtkFiberData(
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI/DICCCOL_Fibers/"
							+ subID + "/fiber.roi.sub." + DICCCOLID + ".sid."
							+ ptID + ".vtk");
			int fiberCount = curFiber.nCellNum;
			ts.fiberBuldleData = curFiber;
			ts.initialPt = surData.getPoint(ptID);
			ts.initialization();
			ts.traceMapMName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
					+ group
					+ "/DICCCOL"
					+ DICCCOLID
					+ "/Sub."
					+ subID
					+ ".DICCCOL." + DICCCOLID + "_traceMapM.txt";
			ts.fiberEncoding();

			// Fiber Decoding
			System.out.println("Beging Fiber Decoding: ...");
			ts = new TraceMapService();
			double[][] traceMapM = DicccolUtilIO.loadFileAsArray(
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
							+ group + "/DICCCOL" + DICCCOLID + "/Sub." + subID
							+ ".DICCCOL." + DICCCOLID + "_traceMapM.txt",
					48 * 20, fiberCount);
			ts.traceMapM = traceMapM;
			djVtkSurData startPoints = new djVtkSurData(
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
							+ group + "/DICCCOL" + DICCCOLID + "/Sub." + subID
							+ ".DICCCOL." + DICCCOLID
							+ "_traceMapM.txt_startPoints.vtk");
			ts.startPointList = startPoints.points;
			ts.initialization();
			ts.traceMapMRowNu = 48 * 20;
			ts.traceMapColNum = fiberCount;
			ts.outputFiberName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
					+ group
					+ "/DICCCOL"
					+ DICCCOLID
					+ "/Sub."
					+ subID
					+ ".DICCCOL." + DICCCOLID + "_RecFiber.vtk";
			ts.fiberDecoding();
		}
	}

	public void generateTraceMapMatrix(String group, int DICCCOLID) {
		System.out
				.println("GenerateTraceMapMatrix!++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		List<List<Float>> modelFeature = new ArrayList<List<Float>>();
		for (int round = 110; round < 120; round++) {
			System.out.println("################### DICCCOL: " + DICCCOLID
					+ " ###############################################");
			String fiberFileName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
					+ group
					+ "/DICCCOL"
					+ DICCCOLID
					+ "/MatrixDecoding/round"
					+ round
					+ "/DICCCOL."
					+ DICCCOLID
					+ ".round."
					+ round
					+ ".DicFiber.vtk";
			djVtkFiberData fiberData = new djVtkFiberData(fiberFileName);
			fiberBundleService fiberBundleDescriptor = new fiberBundleService();
			fiberBundleDescriptor.setFiberData(fiberData);
			djVtkPoint tmPoint = new djVtkPoint();
			tmPoint.x = 1.5f;
			tmPoint.y = 1.5f;
			tmPoint.z = 1.5f;
			fiberBundleDescriptor.setSeedPnt(tmPoint);
			fiberBundleDescriptor.createFibersTrace();
			List<djVtkPoint> allTracePointsList = fiberBundleDescriptor
					.getAllPoints();
			djVtkUtil.writeToPointsVtkFile(fiberFileName
					+ ".TracemapPoints.vtk", allTracePointsList);
			List<Float> tmpFeature = fiberBundleDescriptor
					.calFeatureOfTrace(allTracePointsList);
			modelFeature.add(tmpFeature);
		} // for all rounds
		String outputlFileName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"
				+ group
				+ "/DICCCOL"
				+ DICCCOLID
				+ "/MatrixDecoding/DICCCOL."
				+ DICCCOLID + ".TMV120Rounds.txt";
		djVtkUtil.writeArrayListToFile(modelFeature, " ", 144, outputlFileName);
	}
	
	public float calTMDistance(double[] data1, double[] data2) {
		float dis = 0.0f;
		for(int i=0;i<144;i++)
			dis += Math.pow(Math.abs(data1[i] - data2[i]), 2);
		return dis;
	}

	public void anaTMDistance() throws IllegalArgumentException {
		double pValuethreshold = 0.05;
		pValuethreshold /= 2.0;
		pValuethreshold /= 358.0;
		double[][] AD_Array;
		double[][] MCI_Array;
		double[][] CN_Array;
		SimpleTTest ttestService = new SimpleTTest();
		int roundSize = 50;
		
		for (int d = 0; d < 358; d++) {
			//System.out.println("Dealing with DICCCOL - "+d+" ...");
			double[] AD_CN_pValues = new double[100];
			double[] MCI_CN_pValues = new double[100];
			int[] AD_CN_count = new int[358];
			int[] MCI_CN_count = new int[358];
			AD_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/AD.DICCCOL."+d+".TMV100Rounds.txt", 100, 144);
			MCI_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/MCI.DICCCOL."+d+".TMV100Rounds.txt", 100, 144);
			CN_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/CN.DICCCOL."+d+".TMV100Rounds.txt", 100, 144);
			int countAD = 0;
			int countMCI = 0;
			for (int i = 50; i < 100; i++) {
				double[] inGroupDisAD = new double[roundSize];
				double[] inGroupDisMCI = new double[roundSize];
				double[] outGroupDisAD = new double[roundSize];
				double[] outGroupDisMCI = new double[roundSize];
				for(int j=0;j<50;j++)
				{
					//calculate inner distance
					inGroupDisAD[j] = this.calTMDistance(AD_Array[i], AD_Array[j]);
					inGroupDisMCI[j] = this.calTMDistance(MCI_Array[i], MCI_Array[j]);
					//calculate out distance
					outGroupDisAD[j] = this.calTMDistance(AD_Array[i], CN_Array[j]);
					outGroupDisMCI[j] = this.calTMDistance(MCI_Array[i], CN_Array[j]);
				} //for j
				double pValueAD = ttestService.tTest(inGroupDisAD, outGroupDisAD);
				double pValueMCI = ttestService.tTest(inGroupDisMCI, outGroupDisMCI);
				AD_CN_pValues[i] = pValueAD;
				MCI_CN_pValues[i] = pValueMCI;
				if(pValueAD<=pValuethreshold)
					countAD++;
				if(pValueMCI<=pValuethreshold)
					countMCI++;
			} // for i
			AD_CN_count[d] = countAD;
			MCI_CN_count[d] = countMCI;
			//System.out.println("CountAD-CountMCI: "+countAD +" - "+countMCI);
			System.out.println(countAD);
		} //for all DICCCOLs
	}

	public void anaTMDistance_sfn() throws IllegalArgumentException {
		double pValuethreshold = 0.05;
		pValuethreshold /= 358.0;
		double[][] CNE3_Array;
		double[][] CNE4_Array;
		double[][] MCIE4_Array;
		SimpleTTest ttestService = new SimpleTTest();
		List<String> DicccolList_CNE3CNE4 = new ArrayList<String>();
		List<String> DicccolList_CNE4MCIE4 = new ArrayList<String>();
		List<String> DicccolList_common = new ArrayList<String>();
		
		
		for (int d = 0; d < 358; d++) {
			System.out.println("Dealing with DICCCOL - "+d+" ...");

			CNE3_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/CN.DICCCOL."+d+".sfnCNE3.txt", 10, 144);
			CNE4_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/CN.DICCCOL."+d+".sfnCNE4.txt", 10, 144);
			MCIE4_Array = DicccolUtilIO.loadFileAsArray("./MICCAI2015/TMMatrix/MCI.DICCCOL."+d+".sfnMCIE4.txt", 10, 144);
			double[] inGroupDisCNE3 = new double[45];
			double[] inGroupDisCNE4 = new double[45];
			double[] inGroupDisMCIE4 = new double[45];
			double[] DisCNE3CNE4 = new double[100];
			double[] DisCNE4MCIE4 = new double[100];
			int count=0;
			for(int i=0;i<9;i++)
				for(int j=i+1;j<10;j++)
				{
					inGroupDisCNE3[count] = this.calTMDistance(CNE3_Array[i], CNE3_Array[j]);
					inGroupDisCNE4[count] = this.calTMDistance(CNE4_Array[i], CNE4_Array[j]);
					inGroupDisMCIE4[count] = this.calTMDistance(MCIE4_Array[i], MCIE4_Array[j]);
					count++;
				} //for j
			count=0;
			for(int i=0;i<10;i++)
				for(int j=0;j<10;j++)
				{
					DisCNE3CNE4[count] = this.calTMDistance(CNE3_Array[i], CNE4_Array[j]);
					DisCNE4MCIE4[count] = this.calTMDistance(CNE4_Array[i], MCIE4_Array[j]);
					count++;
				} //for j
			double pValueCNE3CNE4_1 = ttestService.tTest(inGroupDisCNE3, DisCNE3CNE4);
			double pValueCNE3CNE4_2 = ttestService.tTest(inGroupDisCNE4, DisCNE3CNE4);
			if(pValueCNE3CNE4_1<=pValuethreshold && pValueCNE3CNE4_2<=pValuethreshold)
			{
				DicccolList_CNE3CNE4.add(String.valueOf(d));
				System.out.println("CNE3-CNE4: DICCCOL "+d);
			}
			
			double pValueCNE4MCIE4_1 = ttestService.tTest(inGroupDisCNE4, DisCNE4MCIE4);
			double pValueCNE4MCIE4_2 = ttestService.tTest(inGroupDisMCIE4, DisCNE4MCIE4);
			if(pValueCNE4MCIE4_1<=pValuethreshold && pValueCNE4MCIE4_2<=pValuethreshold)
			{
				DicccolList_CNE4MCIE4.add(String.valueOf(d));
				System.out.println("CNE4-MCIE4: DICCCOL "+d);
			}
			
		} //for all DICCCOLs
		System.out.println("DicccolList_CNE3CNE4 size: "+DicccolList_CNE3CNE4.size());
		System.out.println(DicccolList_CNE3CNE4);
		System.out.println("DicccolList_CNE4MCIE4 size: "+DicccolList_CNE4MCIE4.size());
		System.out.println(DicccolList_CNE4MCIE4);
		for(int i=0;i<DicccolList_CNE3CNE4.size();i++)
			if(DicccolList_CNE4MCIE4.contains(DicccolList_CNE3CNE4.get(i)))
				DicccolList_common.add(DicccolList_CNE3CNE4.get(i));
		System.out.println("DicccolList_common size: "+DicccolList_common.size());
		System.out.println(DicccolList_common);
		
		GenerateDICCCOL drawDicccol_1 = new GenerateDICCCOL("../surf10to10.wavelet.5.vtk","../DICCCOL.allMat",0,"sfn_CNE3-CNE4.vtk");
		drawDicccol_1.setDicccolIDList(DicccolList_CNE3CNE4);
		drawDicccol_1.generatePointsVtk();
		
		GenerateDICCCOL drawDicccol_2 = new GenerateDICCCOL("../surf10to10.wavelet.5.vtk","../DICCCOL.allMat",0,"sfn_CNE4-MCIE4.vtk");
		drawDicccol_2.setDicccolIDList(DicccolList_CNE4MCIE4);
		drawDicccol_2.generatePointsVtk();
		
		GenerateDICCCOL drawDicccol_3 = new GenerateDICCCOL("../surf10to10.wavelet.5.vtk","../DICCCOL.allMat",0,"sfn_common.vtk");
		drawDicccol_3.setDicccolIDList(DicccolList_common);
		drawDicccol_3.generatePointsVtk();
		
	}
	
	public static void main(String[] args) throws IllegalArgumentException {
		DICCCOL_ADNI_SC_Analysis mainHandler = new DICCCOL_ADNI_SC_Analysis();

		// For anaCodingResult
		// if (args.length == 2) {
		// String group = args[0].trim();
		// String subID = args[1].trim();
		// mainHandler.anaCodingResult(group, subID);
		// } else
		// System.out
		// .println("Need group(AD/MCI/CN) and subID information...");
		
		// For generateTraceMapMatrix
//		if (args.length == 2) {
//			String group = args[0].trim();
//			String DICCCOLID = args[1].trim();
//			mainHandler.generateTraceMapMatrix(group,
//					Integer.valueOf(DICCCOLID));
//		} else
//			System.out
//					.println("Need group(AD/MCI/CN) and DICCCOLID(0-357) information...");
		
		//For anaTMDistance
//		mainHandler.anaTMDistance();
		mainHandler.anaTMDistance_sfn();

	}

}
