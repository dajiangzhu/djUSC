package edu.usc.ini.igc.journal.EmbededSC;

import java.io.IOException;

import com.xinapse.loadableimage.InvalidImageException;

import edu.uga.liulab.djVtkBase.djNiftiData;

public class J_GenerateAvgVol {

	public void generateAvgVol(int startSubID, int endSubID, String comID)
			throws InvalidImageException, IOException {
		String avgVolFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/MOTOR/1_68/"
				+ startSubID + "/map2vol/Com_" + comID + "_std.nii.gz";
		System.out.println("Loading "+avgVolFile);
		djNiftiData avgData = new djNiftiData(avgVolFile);
		for (int s = startSubID + 1; s <= endSubID; s++) {
			String culVolFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/MOTOR/1_68/"
					+ s + "/map2vol/Com_" + comID + "_std.nii.gz";
			System.out.println("Loading "+culVolFile);
			djNiftiData curData = new djNiftiData(culVolFile);

			for (int x = 0; x < avgData.xSize; x++)
				for (int y = 0; y < avgData.ySize; y++)
					for (int z = 0; z < avgData.zSize; z++) {
						int[] seedVolCoordsReverse = { z, y, x };
						float tmpSub = avgData.getValueBasedOnVolumeCoordinate(
								x, y, z, 0)
								+ curData.getValueBasedOnVolumeCoordinate(x, y,
										z, 0);
						avgData.rawNiftiData.putPix(tmpSub,
								seedVolCoordsReverse);
					} // for z
		} // for all subjects
		avgData.rawNiftiData
				.write("/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/MOTOR/1_68/stdResult/avg_"
						+ startSubID + "_" + endSubID + "_com_" + comID);

	}

	public static void main(String[] args) throws InvalidImageException,
			IOException {

		if (args.length == 3) {
			int startSubID = Integer.valueOf(args[0].trim());
			int endSubID = Integer.valueOf(args[1].trim());
			String comID = args[2].trim();

			J_GenerateAvgVol mainHandler = new J_GenerateAvgVol();
			mainHandler.generateAvgVol(startSubID, endSubID, comID);
		} else
			System.out.println("Need para: startSubID endSubID comID");

	}

}
