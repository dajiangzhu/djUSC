package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkDataDictionary;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkHybridData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class fig {

	private void drawScreenedDICCCOL() {
		String dirPre = "../../data/twin/twinCompactData/";
		String subPre = "8003201_RC";
		// GenerateDICCCOL DicccolService = new GenerateDICCCOL(
		// "./ISBI2014/TD_pValue_100.txt.screen.txt",
		// "../surf10to10.wavelet.5.vtk", dirPre + subPre + ".allMat", 0,
		// "./ISBI2014/fig/ScreenedDICCCOLs.vtk");
		GenerateDICCCOL DicccolService = new GenerateDICCCOL(
				"./twinACE/result/screenDICCCOLs_a2_0.5.txt",
				"../surf10to10.wavelet.5.vtk", dirPre + subPre + ".allMat", 0,
				"./twinACE/result/ScreenedDICCCOLs_0.5.vtk");
		DicccolService.generatePointsVtk();
	}

	private void checkPredictionResult() {
		String dirPre = "../../data/twin/twinCompactData/";
		String subPre = "8003201_RC";
		GenerateDICCCOL DicccolService = new GenerateDICCCOL(dirPre + subPre
				+ ".surf.reg.asc.vtk", dirPre + subPre + ".allMat", 10, subPre
				+ "_358.vtk");
		DicccolService.generatePointsVtk();
	}

	private void drawTable() {
		double[][] pValueMatrix = DicccolUtilIO.loadFileAsArray(
				"./ISBI2014/TD_pValue_100.txt", 358, 100);
		List<Integer> screenedDicccolList = DicccolUtilIO
				.loadFileToIntegerArrayList("./ISBI2014/TD_pValue_100.txt.screen.txt");
		double[][] screenedPValueMatrix = new double[screenedDicccolList.size()][100];
		for (int i = 0; i < screenedDicccolList.size(); i++)
			screenedPValueMatrix[i] = pValueMatrix[screenedDicccolList.get(i)];
		DicccolUtilIO.writeArrayToFile(screenedPValueMatrix,
				screenedDicccolList.size(), 100, " ",
				"./ISBI2014/screenedPValueMatrix.txt");
	}

	private void geneROIViewPrifile(String subAlignMatrix, int row, int column) {
		int totalSurfaceNum = row * column;
		List<String> outPutList = new ArrayList<String>();
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("/home/dzhu/workspace/data/DataName_List_Twin_DTI105_Final.txt");
		List<Integer> screenedDicccolList = DicccolUtilIO
				.loadFileToIntegerArrayList("/home/dzhu/workspace/data/dicccolList.txt"); // only
																							// pick
																							// part
																							// of
																							// the
																							// screened
																							// DICCCOLs
		outPutList.add("SURFACES " + (row * column));
		int[][] subMatrix = DicccolUtilIO.loadFileAsIntArray(subAlignMatrix,
				row, column);
		Map<String, int[][]> allPredictionMat = new HashMap<String, int[][]>();
		for (int r = 0; r < row; r++)
			for (int c = 0; c < column; c++) {
				int subIndex = subMatrix[r][c];
				String currentSubName = subList.get(subIndex).trim();
				int[][] currentMat = DicccolUtilIO.loadFileAsIntArray(
						"/home/dzhu/workspace/data/twinCompactData/"
								+ currentSubName + ".allMat", 358, 11);
				allPredictionMat.put(currentSubName, currentMat);
				outPutList.add("/home/dzhu/workspace/data/twinCompactData/"
						+ currentSubName + ".surf.reg.asc.vtk");
			} // for columns

		for (int i = 0; i < screenedDicccolList.size(); i++) {
			outPutList.add("ROI roi." + i);
			int currentDICCCOLID = screenedDicccolList.get(i);
			for (int r = 0; r < row; r++)
				for (int c = 0; c < column; c++) {
					String currentSubName = subList.get(subMatrix[r][c]).trim();
					int ptID = allPredictionMat.get(currentSubName)[currentDICCCOLID][10];
					outPutList.add("/home/dzhu/workspace/data/TraceFiber/"
							+ currentSubName + "/fiber.roi.sub."
							+ currentDICCCOLID + ".sid." + ptID + ".vtk");
				} // for columns
		} // for all screened DICCCOLs
		DicccolUtilIO.writeArrayListToFile(outPutList, "Profile.Twin.1");
	}

	private void generateScatterFigureData(int DICCCOLID) {
		List<String> outPutList = new ArrayList<String>();
		List<String> twinDis = DicccolUtilIO
				.loadFileToArrayList("//home/dzhu/workspace/data/TraceMapDistanceM/TMDistanceM_Twin_100.txt");
		List<String> currentGroupDis;
		outPutList.add(twinDis.get(DICCCOLID));
		for (int g = 0; g < 100; g++) {
			currentGroupDis = DicccolUtilIO
					.loadFileToArrayList("//home/dzhu/workspace/data/TraceMapDistanceM/TMDistanceM_Group."
							+ g + "_100.txt");
			outPutList.add(currentGroupDis.get(DICCCOLID));
		} // for
		DicccolUtilIO.writeArrayListToFile(outPutList, "scatterFigData.txt");
	}

	private void generateHeritableFibers() {
		String fileForDICCCOLList = "./twinACE/result/screenDICCCOLs_a2_0.5.txt";
		List<Integer> screenedDicccolList = DicccolUtilIO
				.loadFileToIntegerArrayList(fileForDICCCOLList);
		int[][] allMat = DicccolUtilIO.loadFileAsIntArray(
				"../../data/twin/twinCompactData/8003201_RC.allMat", 358, 11);
		djVtkSurData surData = new djVtkSurData("../surf10to10.wavelet.5.vtk");
		djVtkFiberData fiberData = new djVtkFiberData(
				"../../data/model/fibers10.regto10.asc.vtk");
		djVtkHybridData hybridData = new djVtkHybridData(surData, fiberData);
		hybridData.mapSurfaceToBox();
		hybridData.mapFiberToBox();

		Set<Integer> ptIDSet = new HashSet<Integer>();
		Set<djVtkPoint> ptSet = new HashSet<djVtkPoint>();
		for (int i = 0; i < screenedDicccolList.size(); i++) {
			int currentDICCCOLID = screenedDicccolList.get(i);
			int currentPTID = allMat[currentDICCCOLID][0];
			Set currentPtSet = surData.getNeighbourPoints(currentPTID, 3);
			List<djVtkPoint> currentPtIDList = new ArrayList<djVtkPoint>(currentPtSet);
			for (int p = 0; p < currentPtIDList.size(); p++)
				ptIDSet.add(currentPtIDList.get(p).pointId);
		}
		List<Integer> ptIDList = new ArrayList<Integer>(ptIDSet);
		for (int i = 0; i < ptIDList.size(); i++) 
			ptSet.add(surData.getPoint(ptIDList.get(i)));
		djVtkFiberData tmpFiberData = (djVtkFiberData) hybridData
				.getFibersConnectToPointsSet(ptSet).getCompactData();

		tmpFiberData.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_FIBER_CELL;
			tmpFiberData.writeToVtkFileCompact(fileForDICCCOLList+"Fiber.vtk");
	}

	public static void main(String[] args) {
		fig mainHandler = new fig();
		// mainHandler.checkPredictionResult();
//		mainHandler.drawScreenedDICCCOL();
		// mainHandler.drawTable();
		// mainHandler.geneROIViewPrifile("/home/dzhu/workspace/data/ROIView_SubAlignMatrix1.txt",
		// 2, 5);
		// mainHandler.generateScatterFigureData(99);
		mainHandler.generateHeritableFibers();
//		 mainHandler.drawScreenedDICCCOL();
//		mainHandler.drawTable();
		mainHandler.geneROIViewPrifile("/home/dzhu/workspace/data/ROIView_SubAlignMatrix1.txt", 2, 3);
//		mainHandler.generateScatterFigureData(99);


	}

}
