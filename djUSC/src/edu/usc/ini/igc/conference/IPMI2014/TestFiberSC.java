package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class TestFiberSC {

	public void groupFiberEncoding(List<Integer> DICCCOLID_List) {
		String strDirPre = "/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/";
		String[][] modelInfoM = DicccolUtilIO.loadFileAsStringArray(strDirPre
				+ "modelInfo", 10, 4);
		int[][] DICCCOLPT_Map = DicccolUtilIO.loadFileAsIntArray(strDirPre
				+ "model.roi.allMat.txt", 358, 11);

		// ***********************************************
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			List<double[][]> traceMapMList = new ArrayList<double[][]>();
			List<Integer> fiberCountList = new ArrayList<Integer>();
			int totalFiberCount = 0;
			int bigTraceMapM_RowNum = -1;
			for (int m = 0; m < 10; m++) {
				TraceMapService ts = new TraceMapService();
				int ptID = DICCCOLPT_Map[DICCCOLID][m];
				djVtkSurData surData = new djVtkSurData(strDirPre
						+ modelInfoM[m][0].trim());
				djVtkFiberData curFiber = new djVtkFiberData(strDirPre
						+ "/fibers/Sub." + m + ".DICCCOL." + DICCCOLID + ".vtk");
				totalFiberCount = totalFiberCount + curFiber.nCellNum;
				fiberCountList.add(curFiber.nCellNum);

				ts.fiberBuldleData = curFiber;
				ts.initialPt = surData.getPoint(ptID);
				ts.initialization();
				ts.traceMapMName = "Sub." + m + ".DICCCOL." + DICCCOLID
						+ "_traceMapM.txt";
				ts.fiberEncoding();
				traceMapMList.add(ts.traceMapM);
				bigTraceMapM_RowNum = ts.segNum * ts.samp_num;
			} // for all model subjects

			// Begin to combing to a big matrix
			double[][] bigTraceMapM = new double[bigTraceMapM_RowNum][totalFiberCount];
			int columnCount = 0;
			for (int m = 0; m < 10; m++) {
				for (int row = 0; row < bigTraceMapM_RowNum; row++)
					for (int column = 0; column < fiberCountList.get(m); column++)
						bigTraceMapM[row][columnCount + column] = traceMapMList
								.get(m)[row][column];
				columnCount = columnCount + fiberCountList.get(m);
			}
			fiberCountList.add(columnCount);
			DicccolUtilIO.writeIntegerListToFile(fiberCountList,
					"FiberCountList_DICCCOL_" + DICCCOLID + ".txt");
			DicccolUtilIO.writeArrayToFile(bigTraceMapM,
					bigTraceMapM_RowNum, totalFiberCount, " ",
					"BigTraceMapM_DICCCOL_"+DICCCOLID+".txt");
		} // for all items in DICCCOLID_List
	}

	public int[][] quickCheck(double[][] scResult, int totalFiberCount) {
		int[][] checkResult = new int[20][totalFiberCount];
		for (int c = 0; c < totalFiberCount; c++) {
			for (int s = 0; s < 20; s++) {
				int flag = 0;
				for (int r = 0; r < 162; r++)
					if (scResult[s * 162 + r][c] > 0.0001)
						flag = 1;
				checkResult[s][c] = flag;
			} // for s
		} // for c
		return checkResult;
	}

	public void analyzeSCResult(List<Integer> DICCCOLID_List) {
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			double[][] scResult = DicccolUtilIO.loadFileAsArray(
					"Dictionary_Dicccol_"+DICCCOLID+".txt", 162 * 20,215);
			DicccolUtilIO.writeIntArrayToFile(this.quickCheck(scResult, 215), 20, 215, " ", "CheckResultFlagM_DICCCOL_"+DICCCOLID+".txt");

		} // for all items in DICCCOLID_List

	}

	public void groupFiberDcoding(List<Integer> DICCCOLID_List) {
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			int columnCount = 0;
			List<Integer> fiberCountList = DicccolUtilIO
					.loadFileToIntegerArrayList("FiberCountList_DICCCOL_"
							+ DICCCOLID + ".txt");
			double[][] bigTraceMapM = DicccolUtilIO.loadFileAsArray(
					"BigTraceMapM_DICCCOL_" + DICCCOLID + ".txt", 162 * 20,
					fiberCountList.get(fiberCountList.size() - 1));
			for (int m = 0; m < fiberCountList.size() - 1; m++) {
				TraceMapService ts = new TraceMapService();
				double[][] traceMapM = new double[162 * 20][fiberCountList.get(m)];
				for (int row = 0; row < 162 * 20; row++)
					for (int column = 0; column < fiberCountList.get(m); column++)
						traceMapM[row][column] = bigTraceMapM[row][columnCount
								+ column];
				ts.traceMapM = traceMapM;
				djVtkSurData startPoints = new djVtkSurData("Sub." + m
						+ ".DICCCOL." + DICCCOLID
						+ "_traceMapM.txt_startPoints.vtk");
				ts.startPointList = startPoints.points;
				ts.initialization();
				ts.traceMapMRowNu = 162 * 20;
				ts.traceMapColNum = fiberCountList.get(m);
				ts.outputFiberName = "FibeRecon_Sub." + m + ".DICCCOL."
						+ DICCCOLID + ".vtk";
				ts.fiberDecoding();
				columnCount = columnCount + fiberCountList.get(m);
			} // for all subjects
		} // for all items in DICCCOLID_List
	}
	
	public void fiberDictionaryDecoding(List<Integer> DICCCOLID_List)
	{
		TraceMapService ts = new TraceMapService();
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			List<Integer> fiberCountList = DicccolUtilIO
					.loadFileToIntegerArrayList("FiberCountList_DICCCOL_"
							+ DICCCOLID + ".txt");
			int dictionaryDim = fiberCountList.get(fiberCountList.size() - 1)/10;
			
			String traceMapMFile = "Dictionary_Dicccol_"+DICCCOLID+".txt";
			double[][] dictionaryTraceMapM = DicccolUtilIO.loadFileAsArray(traceMapMFile, 162*20, dictionaryDim);
			ts.traceMapM = dictionaryTraceMapM;
//			djVtkSurData startPoints = new djVtkSurData("8003201_RC.dicccol.12.Fiber.vtk_traceMapM.txt_startPoints.vtk");
//			ts.startPointList = startPoints.points;
			ts.traceMapMRowNu = 162*20;
			ts.traceMapColNum = dictionaryDim;
			ts.outputFiberName="DictionaryFiber_DICCCOL_"+DICCCOLID+"_recon.vtk";
			ts.initialization();
			ts.fiberDecoding();
		}
		
	}

	public static void main(String[] args) {
		TestFiberSC mainHandler = new TestFiberSC();
		// ******************************
		List<Integer> DICCCOLIDList = new ArrayList<Integer>();
//		DICCCOLIDList.add(0);
//		 DICCCOLIDList.add(159);
//		 DICCCOLIDList.add(256);
//		 DICCCOLIDList.add(285);
		 DICCCOLIDList.add(340);
		// ***********************************
		// ***********************************
//		 mainHandler.groupFiberEncoding(DICCCOLIDList);
//		mainHandler.groupFiberDcoding(DICCCOLIDList);
		// ***********************************
		// ***********************************
//		mainHandler.analyzeSCResult(DICCCOLIDList);
		mainHandler.fiberDictionaryDecoding(DICCCOLIDList);

	}

}
