package edu.usc.ini.igc.conference.ISBI2014;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;
import edu.uga.liulab.djVtkBase.djVtkData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class ACE_AnaResult {
	
	private double correctedPValue = 0.037; //0.05/358;
	private double a2Threshold = 0.01;
	
	private List<String> screenDICCCOLList = new ArrayList<String>();
	private List<String> attriList_a2 = new ArrayList<String>();
	private List<String> attriList_c2 = new ArrayList<String>();
	private List<String> attriList_e2 = new ArrayList<String>();
	private List<String> attriList_a2lb = new ArrayList<String>();
	private List<String> attriList_a2ub = new ArrayList<String>();
	
	private djVtkSurData inputData;
	private djVtkSurData normalModelData;
	private djVtkSurData highlightModeData;
	
	private void outPut()
	{
		//OutPut DICCCOL list
		DicccolUtilIO.writeArrayListToFile(screenDICCCOLList, "./twinACE/result/screenDICCCOLs_a2_"+a2Threshold+".txt");
		//OutPut DICCCOL Points Vtk
		GenerateDICCCOL DicccolService = new GenerateDICCCOL(
				"./twinACE/result/screenDICCCOLs_a2_"+a2Threshold+".txt",
				"../surf10to10.wavelet.5.vtk", "../../data/twin/twinCompactData/8003201_RC.allMat", 0,
				"./twinACE/result/screenDICCCOLs_a2_"+a2Threshold+".vtk");
		DicccolService.generatePointsVtk();
		
		//OutPut Bubbles with ACE values
		this.inputData = new djVtkSurData("./twinACE/result/screenDICCCOLs_a2_"+a2Threshold+".vtk");
		this.normalModelData = new djVtkSurData("../sphere_radius2.vtk");
		this.highlightModeData = new djVtkSurData("../sphere_radius2.vtk");

		String fileName = "./twinACE/result/ACE_"+a2Threshold+".vtk";
		System.out.println("Begin to write file:" + fileName + "...");
		Map<Integer, String> highLightROI = new HashMap<Integer, String>();
		FileWriter fw = null;

		try {
			// fw = new FileWriter(roiGroupInfo + ".vtk");
			fw = new FileWriter(fileName);
			fw.write("# vtk DataFile Version 3.0\r\n");
			fw.write("vtk output\r\n");
			fw.write("ASCII\r\n");
			fw.write("DATASET POLYDATA\r\n");

			int roiNum = this.inputData.nPointNum;
			int highROINum = highLightROI.size();
			int normalModelPtNum = this.normalModelData.nPointNum;
			int highModelPtNum = this.highlightModeData.nPointNum;
			int normalModelCellNum = this.normalModelData.nCellNum;
			int highModelCellNum = this.highlightModeData.nCellNum;

			System.out.println("the number of points in the input vtk is : " + roiNum);
			System.out.println("the number of points need to be highlighted is : " + highROINum);
			System.out.println("the number of points in the normalModel vtk is : " + normalModelPtNum);
			System.out.println("the number of points in the highModel vtk is : " + highModelPtNum);
			// print points info
			List<Integer> offsetList = new ArrayList<Integer>();
			fw.write("POINTS " + ((roiNum - highROINum) * normalModelPtNum + (highROINum * highModelPtNum))
					+ " float\r\n");
			for (int roiIndex = 0; roiIndex < roiNum; roiIndex++) {
				int countNormal = 0;
				int countHigh = 0;
				djVtkPoint currentROIPt = this.inputData.getPoint(roiIndex);
				if (highLightROI.containsKey(roiIndex)) {
					for (int modelPtIndex = 0; modelPtIndex < highModelPtNum; modelPtIndex++) {
						float x = this.highlightModeData.getPoint(modelPtIndex).x + currentROIPt.x;
						float y = this.highlightModeData.getPoint(modelPtIndex).y + currentROIPt.y;
						float z = this.highlightModeData.getPoint(modelPtIndex).z + currentROIPt.z;
						fw.write(x + " " + y + " " + z + "\r\n");
						offsetList.add(highModelPtNum);
						countHigh++;
					}
				} else {
					for (int modelPtIndex = 0; modelPtIndex < normalModelPtNum; modelPtIndex++) {
						float x = this.normalModelData.getPoint(modelPtIndex).x + currentROIPt.x;
						float y = this.normalModelData.getPoint(modelPtIndex).y + currentROIPt.y;
						float z = this.normalModelData.getPoint(modelPtIndex).z + currentROIPt.z;
						fw.write(x + " " + y + " " + z + "\r\n");
						offsetList.add(normalModelPtNum);
						countNormal++;
					} // for all points in the model vtk file
				}

			} // for all points in the input vtk file

			// print cells info
			int totalCellNum = (roiNum - highROINum) * normalModelCellNum + (highROINum * highModelCellNum);
			fw.write("POLYGONS " + totalCellNum + " " + (totalCellNum * 4) + " \r\n");
			int offset = 0;
			for (int roiIndex = 0; roiIndex < roiNum; roiIndex++) {
				if (highLightROI.containsKey(roiIndex)) {
					for (int modelCellIndex = 0; modelCellIndex < highModelCellNum; modelCellIndex++) {
						int ptId1 = this.highlightModeData.getcell(modelCellIndex).pointsList.get(0).pointId + offset;
						int ptId2 = this.highlightModeData.getcell(modelCellIndex).pointsList.get(1).pointId + offset;
						int ptId3 = this.highlightModeData.getcell(modelCellIndex).pointsList.get(2).pointId + offset;
						fw.write("3 " + ptId1 + " " + ptId2 + " " + ptId3 + " \r\n");
					} // for all cells in the model vtk file
					offset = offset + highModelPtNum;
				} else {
					for (int modelCellIndex = 0; modelCellIndex < normalModelCellNum; modelCellIndex++) {
						int ptId1 = this.normalModelData.getcell(modelCellIndex).pointsList.get(0).pointId + offset;
						int ptId2 = this.normalModelData.getcell(modelCellIndex).pointsList.get(1).pointId + offset;
						int ptId3 = this.normalModelData.getcell(modelCellIndex).pointsList.get(2).pointId + offset;
						fw.write("3 " + ptId1 + " " + ptId2 + " " + ptId3 + " \r\n");
					} // for all cells in the model vtk file
					offset = offset + normalModelPtNum;
				}

			} // for all points in the input vtk file

			fw.write("POINT_DATA " + ((roiNum - highROINum) * normalModelPtNum + (highROINum * highModelPtNum))
					+ "\r\n");
			fw.write("SCALARS a2 float 1 \r\n");
			fw.write("LOOKUP_TABLE default \r\n");
			for (int roiIndex = 0; roiIndex < roiNum; roiIndex++)
				for (int modelPtIndex = 0; modelPtIndex < normalModelPtNum; modelPtIndex++)
					fw.write(attriList_a2.get(roiIndex) + " \r\n");

			fw.write("SCALARS c2 float 1 \r\n");
			fw.write("LOOKUP_TABLE default \r\n");
			for (int roiIndex = 0; roiIndex < roiNum; roiIndex++)
				for (int modelPtIndex = 0; modelPtIndex < normalModelPtNum; modelPtIndex++)
					fw.write(attriList_c2.get(roiIndex) + " \r\n");

			fw.write("SCALARS e2 float 1 \r\n");
			fw.write("LOOKUP_TABLE default \r\n");
			for (int roiIndex = 0; roiIndex < roiNum; roiIndex++)
				for (int modelPtIndex = 0; modelPtIndex < normalModelPtNum; modelPtIndex++)
					fw.write(attriList_e2.get(roiIndex) + " \r\n");

		} catch (IOException ex) {
			Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				fw.close();
			} catch (IOException ex) {
				Logger.getLogger(djVtkData.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		System.out.println("Write file:" + fileName + ".vtk done!");
	}
	
	private void screenDICCCOLs()
	{

		List<String> ACE_p_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_p.txt");
		List<String> ACE_a2_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_a2.txt");
		List<String> ACE_a2lb_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_a2lb.txt");
		List<String> ACE_a2ub_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_a2ub.txt");
		List<String> ACE_c2_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_c2.txt");
		List<String> ACE_e2_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_e2.txt");
		if(ACE_p_List.size()!=358)
		{
			System.out.println("Error with result!");
			System.exit(0);
		}
		
		for(int d=0;d<358;d++)
		{
			double currentPValue = Double.valueOf(ACE_p_List.get(d).trim());
			if(currentPValue>0.05)
			{
//				System.out.println("DICCCOL: "+d+"--pvalue check passed...");
				double currentLb = Double.valueOf(ACE_a2lb_List.get(d).trim());
				if(currentLb>0.0)
				{
//					System.out.println("DICCCOL: "+d+"--lb check passed...");
					double currenta2 = Double.valueOf(ACE_a2_List.get(d).trim());
					if(currenta2>=a2Threshold)
					{
						double currentc2 = Double.valueOf(ACE_c2_List.get(d).trim());
						double currente2 = Double.valueOf(ACE_e2_List.get(d).trim());
						System.out.println("DICCCOL:"+d+"   a2="+currenta2+"   pValue="+currentPValue+"   lb="+currentLb+"   c2="+currentc2+"    e2="+currente2);
						screenDICCCOLList.add(String.valueOf(d));
						attriList_a2.add(String.valueOf(currenta2));
						attriList_c2.add(String.valueOf(currentc2));
						attriList_e2.add(String.valueOf(currente2));
						attriList_a2lb.add(String.valueOf(currentLb));
						attriList_a2ub.add(String.valueOf(ACE_a2ub_List.get(d)));
					} //check a2					
				} //check lb
			} //check pvalue			
		} //for all DICCCOLs
		DicccolUtilIO.writeArrayListToFile(attriList_a2, "./twinACE/result/stat_a2_list.txt");
		DicccolUtilIO.writeArrayListToFile(attriList_c2, "./twinACE/result/stat_c2_list.txt");
		DicccolUtilIO.writeArrayListToFile(attriList_e2, "./twinACE/result/stat_e2_list.txt");
		DicccolUtilIO.writeArrayListToFile(attriList_a2lb, "./twinACE/result/stat_a2lb_list.txt");
		DicccolUtilIO.writeArrayListToFile(attriList_a2ub, "./twinACE/result/stat_a2ub_list.txt");
		
	}
	
	private void selectModel()
	{
		List<String> ACE_AE_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_AE.txt");
		List<String> ACE_CE_List = DicccolUtilIO
				.loadFileToArrayList("./twinACE/result/ACE_CE.txt");
		List<String> ACE_AE_selectedList = new ArrayList<String>();
		List<String> ACE_CE_selectedList = new ArrayList<String>();
		
		for(int i=0;i<ACE_AE_List.size();i++)
		{
			double currentACE_AE = Double.valueOf(ACE_AE_List.get(i).trim());
			double currentACE_CE = Double.valueOf(ACE_CE_List.get(i).trim());
			if(currentACE_AE<=correctedPValue)
				ACE_AE_selectedList.add(String.valueOf(i));
			if(currentACE_CE<=correctedPValue)
				ACE_CE_selectedList.add(String.valueOf(i));
		}
		System.out.println("Size of ACE_AE_selectedList:"+ACE_AE_selectedList.size());
		System.out.println("Size of ACE_CE_selectedList:"+ACE_CE_selectedList.size());

		
	}

	public static void main(String[] args) {
		ACE_AnaResult mainHandler = new ACE_AnaResult();
		mainHandler.screenDICCCOLs();
//		mainHandler.outPut();
		
//		mainHandler.selectModel();

	}

}
