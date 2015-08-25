package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class screenDicccols {
	
	String dirPre = "../../data/twin/twinCompactData/";
	List<String> screenDicccolList = new ArrayList<String>();
	public void screenHomogenousDicccols(String fileMatrix, double pThreshold)
	{
		double[][] pValueMatrix = DicccolUtilIO.loadFileAsArray(fileMatrix, 358, 100);
		for(int d=0;d<358;d++)
		{
			boolean flag = true;
			for(int c=0;c<100;c++)
				if(pValueMatrix[d][c]>pThreshold)
					flag = false;
			if(flag)
				screenDicccolList.add(String.valueOf(d));
		} //for all Dicccols
//		DicccolUtilIO.writeArrayListToFile(screenDicccolList, fileMatrix+".screen.txt");
		System.out.println(screenDicccolList.size());
		
		pValueMatrix = new double[358][100];
		for(int i=0;i<screenDicccolList.size();i++)
		{
			int dicccolID = Integer.valueOf( screenDicccolList.get(i) );
			for(int c=0;c<100;c++)
				pValueMatrix[dicccolID][c] = 1.0;
		}
		DicccolUtilIO.writeVtkMatrix1(pValueMatrix, 358, 100, fileMatrix+".screenBinaryM.vtk");
	}
	

	public static void main(String[] args) {
		screenDicccols mainHandler = new screenDicccols();
		String fileMatrix = "./ISBI2014/TD_pValue_100.txt";
		mainHandler.screenHomogenousDicccols(fileMatrix, 0.01/358);
	}

}
