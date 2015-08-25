package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkDataDictionary;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkHybridData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class TestGenerateFibers {

	public void generateDicccolFibers(List<Integer> DICCCOLID_List) {
		String strDirPre = "/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/dicccol_model/";
		String[][] modelInfoM = DicccolUtilIO.loadFileAsStringArray(strDirPre
				+ "modelInfo", 10, 4);
		int[][] DICCCOLPT_Map = DicccolUtilIO.loadFileAsIntArray(strDirPre
				+ "model.roi.allMat.txt", 358, 11);
		
		for (int i = 0; i < 10; i++) {
			djVtkSurData surData = new djVtkSurData(strDirPre
					+ modelInfoM[i][0].trim());
			djVtkFiberData fiberData = new djVtkFiberData(strDirPre
					+ modelInfoM[i][1].trim());
			djVtkHybridData hybridData = new djVtkHybridData(surData, fiberData);
			hybridData.mapSurfaceToBox();
			hybridData.mapFiberToBox();
			for (int d = 0; d < DICCCOLID_List.size(); d++) {
				int DICCCOLID = DICCCOLID_List.get(d);
				int ptID = DICCCOLPT_Map[DICCCOLID][i];
				// extract fibers
				Set ptSet = surData.getNeighbourPoints(ptID, 3);
				djVtkFiberData tmpFiberData = (djVtkFiberData) hybridData
						.getFibersConnectToPointsSet(ptSet).getCompactData();

				tmpFiberData.cell_alias = djVtkDataDictionary.VTK_FIELDNAME_FIBER_CELL;
					tmpFiberData.writeToVtkFileCompact(strDirPre+"fibers/Sub."+i
							+ ".DICCCOL." + DICCCOLID +".vtk");
			} //for all ids in DICCCOLID_List
		} //for all modle subjects
	}

	public static void main(String[] args) {
		TestGenerateFibers mainHandler = new TestGenerateFibers();
		//******************************
		List<Integer> DICCCOLIDList = new ArrayList<Integer>();
		DICCCOLIDList.add(0);
		DICCCOLIDList.add(159);
		DICCCOLIDList.add(256);
		DICCCOLIDList.add(285);
		DICCCOLIDList.add(340);
		mainHandler.generateDicccolFibers(DICCCOLIDList);
		
		

	}

}
