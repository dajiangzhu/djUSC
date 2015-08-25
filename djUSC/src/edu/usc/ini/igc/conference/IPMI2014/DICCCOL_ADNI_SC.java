package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class DICCCOL_ADNI_SC {
	
	public int RoundNum = 100;
	public int SubsNum = 20;
	
	public void groupFiberEncoding(String group,int round,List<Integer> DICCCOLID_List, List<String> SubList) {
		System.out.println("Group Fiber Encoding!++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		//if DICCCOLID_List is null, do all 358 DICCCOLs
		if(DICCCOLID_List.size()==0)
			for(int d=0;d<358;d++)
				DICCCOLID_List.add(d);
		//Container of 358.mat
		Map<String,int[][]> DICCCOLPT_Map = new HashMap<String,int[][]>();

		// ***********************************************
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			System.out.println("################### DICCCOL: "+DICCCOLID+" ###############################################");
			List<double[][]> traceMapMList = new ArrayList<double[][]>();
			List<Integer> fiberCountList = new ArrayList<Integer>();
			int totalFiberCount = 0;
			int bigTraceMapM_RowNum = -1;
			
			for (int s = 0; s < SubList.size(); s++) {
				String subID = SubList.get(s).trim();
				System.out.println("---------------Sub: "+subID+" ------------------------");
				if(DICCCOLPT_Map.get(subID)==null)
				{
//					int[][] tmpDicccolMap = DicccolUtilIO.loadFileAsIntArray("/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"+subID+".allMat", 358, 11);
					int[][] tmpDicccolMap = DicccolUtilIO.loadFileAsIntArray("/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI/"+subID+"/DicccolPrediction/"+subID+".allMat", 358, 11);
					DICCCOLPT_Map.put(subID, tmpDicccolMap);
				}
					
				TraceMapService ts = new TraceMapService();
				int ptID = DICCCOLPT_Map.get(subID)[DICCCOLID][10];
//				djVtkSurData surData = new djVtkSurData("/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI_Compact/"+subID+".surf.reg.asc.vtk");
				djVtkSurData surData = new djVtkSurData("/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI/"+subID+"/DicccolPrediction/"+subID+".surf.reg.asc.vtk");
				djVtkFiberData curFiber = new djVtkFiberData("/ifs/loni/faculty/thompson/four_d/dzhu/data/ADNI/DICCCOL_Fibers/"+subID+"/fiber.roi.sub."+DICCCOLID+".sid."+ptID+".vtk");
				totalFiberCount = totalFiberCount + curFiber.nCellNum;
				fiberCountList.add(curFiber.nCellNum);

				ts.fiberBuldleData = curFiber;
				ts.initialPt = surData.getPoint(ptID);
				ts.initialization();
				ts.traceMapMName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/Sub." + subID + ".DICCCOL." + DICCCOLID+ ".round."+round+"_traceMapM.txt";
				ts.fiberEncoding();
				traceMapMList.add(ts.traceMapM);
				bigTraceMapM_RowNum = ts.segNum * ts.samp_num;
			} // for all model subjects

			// Begin to combing to a big matrix
			double[][] bigTraceMapM = new double[bigTraceMapM_RowNum][totalFiberCount];
			int columnCount = 0;
			for (int s = 0; s < SubList.size(); s++) {
				for (int row = 0; row < bigTraceMapM_RowNum; row++)
					for (int column = 0; column < fiberCountList.get(s); column++)
						bigTraceMapM[row][columnCount + column] = traceMapMList
								.get(s)[row][column];
				columnCount = columnCount + fiberCountList.get(s);
			}
			fiberCountList.add(columnCount);
			DicccolUtilIO.writeIntegerListToFile(fiberCountList, "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/FiberCountList.DICCCOL." + DICCCOLID+ ".round."+round+".txt");
			DicccolUtilIO.writeArrayToFile(bigTraceMapM,
					bigTraceMapM_RowNum, totalFiberCount, " ",
					"/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/BigTraceMapM.DICCCOL."+DICCCOLID+".round."+round+".txt");
		} // for all items in DICCCOLID_List
	}
	
	public void groupFiberDecoding(String group,int round,List<Integer> DICCCOLID_List, List<String> SubList) {
		System.out.println("Group Fiber Decoding!++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			System.out.println("################### DICCCOL: "+DICCCOLID+" ###############################################");
			int columnCount = 0;
			List<Integer> fiberCountList = DicccolUtilIO
					.loadFileToIntegerArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/FiberCountList.DICCCOL." + DICCCOLID+ ".round."+round+".txt");
			double[][] bigTraceMapM = DicccolUtilIO.loadFileAsArray("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/BigTraceMapM.DICCCOL."+DICCCOLID+".round."+round+".txt", 48 * 20,
					fiberCountList.get(fiberCountList.size() - 1));
			for (int s = 0; s < SubList.size(); s++) {
				String subID = SubList.get(s).trim();
				TraceMapService ts = new TraceMapService();
				double[][] traceMapM = new double[48 * 20][fiberCountList.get(s)];
				for (int row = 0; row < 48 * 20; row++)
					for (int column = 0; column < fiberCountList.get(s); column++)
						traceMapM[row][column] = bigTraceMapM[row][columnCount
								+ column];
				ts.traceMapM = traceMapM;
				djVtkSurData startPoints = new djVtkSurData("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixEncoding/round"+round+"/Sub." + subID + ".DICCCOL." + DICCCOLID+ ".round."+round+"_traceMapM.txt_startPoints.vtk");
				ts.startPointList = startPoints.points;
				ts.initialization();
				ts.traceMapMRowNu = 48 * 20;
				ts.traceMapColNum = fiberCountList.get(s);
				ts.outputFiberName = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixDecoding/round"+round+"/Sub." + subID + ".DICCCOL." + DICCCOLID+ ".round."+round+".FiberRecon.vtk";
				ts.fiberDecoding();
				columnCount = columnCount + fiberCountList.get(s);
			} // for all subjects
		} // for all items in DICCCOLID_List
	}
	
	public void fiberDictionaryDecoding(String group,int round,List<Integer> DICCCOLID_List)
	{
		System.out.println("Fiber Dictionary Decoding!++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=");
		TraceMapService ts = new TraceMapService();
		for (int d = 0; d < DICCCOLID_List.size(); d++) {
			int DICCCOLID = DICCCOLID_List.get(d);
			System.out.println("################### DICCCOL: "+DICCCOLID+" ###############################################");
			int dictionaryDim = 1000;
			String traceMapMFile = "/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/SC/"+group+".DICCCOL."+DICCCOLID+".Round."+round+".dictionary.txt";
			double[][] dictionaryTraceMapM = DicccolUtilIO.loadFileAsArray(traceMapMFile, 48*20, dictionaryDim);
			ts.traceMapM = dictionaryTraceMapM;
			ts.traceMapMRowNu = 48*20;
			ts.traceMapColNum = dictionaryDim;
			ts.outputFiberName="/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/Result/"+group+"/DICCCOL"+DICCCOLID+"/MatrixDecoding/round"+round+"/DICCCOL."+DICCCOLID+".round."+round+".DicFiber.vtk";
			ts.initialization();
			ts.fiberDecoding();
		}
		
	}
	
	public void dicccolSC(String group, int DicccolID)
	{
		List<Integer> DicccolList = new ArrayList<Integer>();
		DicccolList.add(DicccolID);
//		for(int r=0;r<this.RoundNum;r++)
		for(int r=110;r<120;r++)
		{
			System.out.println("$$$$$$$$$$$$$ ROUND: "+r+" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			List<String> SubList = DicccolUtilIO.loadFileToArrayList("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/RandomSubList/"+group+"RandomList_"+(r+1)+".txt");
			
			//GroupFiberEncoding
//			this.groupFiberEncoding(group,r,DicccolList, SubList);
			
			//GroupFiberDecoding
//			this.groupFiberDecoding(group,r,DicccolList, SubList);
			
			//DictionaryFiberDecoding
			this.fiberDictionaryDecoding(group,r,DicccolList);
			
		}
		
	}

	public static void main(String[] args) {
		DICCCOL_ADNI_SC mainHandler = new DICCCOL_ADNI_SC();
		if(args.length==2)
		{
		String group = args[0].trim();
		int DicccolID = Integer.valueOf(args[1]);
		System.out.println("$$$$$$$$$$$$$ Will deal with "+group+": DICCCOL: "+DicccolID+" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		mainHandler.dicccolSC(group,DicccolID);
		}
		else
			System.out.println("Need group(AD/MCI/CN) and DicccolID(0-357) information...");

	}

}
