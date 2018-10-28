package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class ExtractCoordOfDicccol {
	
	public void getCoords(String subID, String matFile, String surfFile)
	{
		int[][] dicccolIDs = DicccolUtilIO.loadFileAsIntArray(matFile, 358, 11);
		djVtkSurData surf = new djVtkSurData(surfFile);
		List<String> outCoords = new ArrayList<String>();
		for(int d=0;d<358;d++)
		{
			int ptID = dicccolIDs[d][10];
			String currentLine = surf.getPoint(ptID).x+" "+surf.getPoint(ptID).y+" "+surf.getPoint(ptID).z;
			outCoords.add(currentLine);
		}
		DicccolUtilIO.writeArrayListToFile(outCoords, subID+"_DICCCOL_Coordinates.txt");
	}

	public static void main(String[] args) {
		if(args.length!=3)
		{
			System.out.println("Need paras:subID+mat+surf");
			System.exit(0);
		}
		ExtractCoordOfDicccol MainHandler = new ExtractCoordOfDicccol();
		MainHandler.getCoords(args[0], args[1], args[2]);

	}

}
