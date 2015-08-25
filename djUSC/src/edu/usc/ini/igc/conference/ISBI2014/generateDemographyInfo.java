package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class generateDemographyInfo {

	public static void main(String[] args) {
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/DataName_List_Twin_DTI105_Final.txt");
		List<String> allInfo = DicccolUtilIO
				.loadFileToArrayList("./ISBI2014/AllDemographyInfo.txt");
		List<String> selectedSubDemoInfoList = new ArrayList<String>();
		for (int l = 0; l < subList.size(); l++) {
			String currentSub = subList.get(l).trim();
			for (int i = 1; i < allInfo.size(); i++) {
				String tmpLine = allInfo.get(i).trim();
				String[] tmpParts = tmpLine.split("\\s+");
				if (currentSub.equals(tmpParts[2].trim())) {
					System.out.println(currentSub + " found...");
					currentSub = currentSub + " " + tmpParts[3] + " "
							+ tmpParts[6] +" "+ tmpParts[4];
					selectedSubDemoInfoList.add(currentSub);
				}
			} // for i
		} // for l
		DicccolUtilIO.writeArrayListToFile(selectedSubDemoInfoList,
				"selectedDemoInfoCompact.txt");

	}

}
