package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class J_GenerateInitialConfigFile {

	public static void main(String[] args) {
		for(int i=1;i<=68;i++)
		{
			List<String> tmpList = new ArrayList<String>();
			tmpList.add("0 0");
			DicccolUtilIO.writeArrayListToFile(tmpList, "config_sub_"+i+".txt");
		}

	}

}
