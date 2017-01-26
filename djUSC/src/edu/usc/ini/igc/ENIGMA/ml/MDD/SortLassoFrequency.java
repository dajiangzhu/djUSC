package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class SortLassoFrequency {

	public static void main(String[] args) {
		List<String> freList = DicccolUtilIO
				.loadFileToArrayList("LassoFeatureFrequency_ICV.txt");
		List<String> orderedList = new ArrayList<String>();
		for (int s = 2000; s >= 0; s--) {
			for (int i = 0; i < freList.size(); i++) {
				String[] currentLine = freList.get(i).split(":");
				int f1 = Double.valueOf(currentLine[0].trim()).intValue();
				int f2 = Double.valueOf(currentLine[1].trim()).intValue();
				int sum = f1 + f2;
				if (sum == s)
					orderedList.add((i+1)+":"+freList.get(i));
			} //fori
		} //for s

		DicccolUtilIO.writeArrayListToFile(orderedList, "LassoFeatureFrequency_ICV_ordered.txt");
	}
}
