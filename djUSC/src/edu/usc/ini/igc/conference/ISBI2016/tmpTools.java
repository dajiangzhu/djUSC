package edu.usc.ini.igc.conference.ISBI2016;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class tmpTools {

	public void checkResults() {
		List<String> resultInfo = new ArrayList<String>();
		for (int block = 2; block <= 100; block++) {
			String fileName = "/ifs/loni/faculty/thompson/four_d/dzhu/ESL_JobMap/Block_"
					+ block + "/log.txt";
			List<String> currentContent = DicccolUtilIO
					.loadFileToArrayList(fileName);
			if (currentContent != null && currentContent.size() != 0) {
				String lastLine = currentContent.get(currentContent.size() - 1);
				if (lastLine
						.equals("*************************************************** ESL finished ... ***************************************************")) {
					System.out.println("################# Block - "+block+" is good!");
					lastLine = currentContent.get(currentContent.size() - 2);
					int optIndex = Integer.valueOf( lastLine.split(":")[1].split("\\s+")[0].trim() );
					resultInfo.add(block+" "+(optIndex+1));
				}

			} else
				System.out.println("!!!!!!!!!!!!!!!!!!! Error! Block - "
						+ block + " has problem!!");
		} //for
		DicccolUtilIO.writeArrayListToFile(resultInfo, "/ifs/loni/faculty/thompson/four_d/dzhu/ESL_JobMap/RuningInfo.txt");
	}

	public static void main(String[] args) {
		tmpTools mainHandler = new tmpTools();
		mainHandler.checkResults();

	}

}
