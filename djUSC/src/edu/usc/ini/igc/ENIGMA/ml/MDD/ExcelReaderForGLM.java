package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class ExcelReaderForGLM {

	List<String> CenterList = new ArrayList<String>();
	
	public void formatForGLM()
	{
		for(int i=0;i<CenterList.size();i++)
		{
			String currentCenterName = CenterList.get(i);
			System.out.println("Loading Center - "+currentCenterName);
		}
	}
	
	public static void main(String[] args) {
		if(args.length!=1)
		{
			System.out.println("Need input the file of CenterList...");
			System.exit(0);
		}
		ExcelReaderForGLM mainHandler = new ExcelReaderForGLM();
		mainHandler.CenterList = DicccolUtilIO.loadFileToArrayList(args[0].trim());

	}

}
