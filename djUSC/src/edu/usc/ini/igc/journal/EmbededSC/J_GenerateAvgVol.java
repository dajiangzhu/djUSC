package edu.usc.ini.igc.journal.EmbededSC;

import java.io.IOException;

import com.xinapse.loadableimage.InvalidImageException;

import edu.uga.liulab.djVtkBase.djNiftiData;

public class J_GenerateAvgVol {

	public void generateAvgVol(String inputDirPre, int startSubID,
			int endSubID, String comID, String outputFile)
			throws InvalidImageException, IOException {
		float subNum = endSubID-startSubID+1;
		String avgVolFile = inputDirPre + "/"+startSubID+"/map2vol/Com_" + comID + "_std.nii.gz";
		System.out.println("Loading " + avgVolFile);
		djNiftiData avgData = new djNiftiData(avgVolFile);
		for (int s = startSubID + 1; s <= endSubID; s++) {
			String culVolFile = inputDirPre + "/"+startSubID+"/map2vol/Com_" + comID + "_std.nii.gz";
			System.out.println("Loading " + culVolFile);
			djNiftiData curData = new djNiftiData(culVolFile);

			for (int x = 0; x < avgData.xSize; x++)
				for (int y = 0; y < avgData.ySize; y++)
					for (int z = 0; z < avgData.zSize; z++) {
						int[] seedVolCoordsReverse = { z, y, x };
						float tmpSub = avgData.getValueBasedOnVolumeCoordinate(
								x, y, z, 0)
								+ curData.getValueBasedOnVolumeCoordinate(x, y,
										z, 0);
						avgData.rawNiftiData.putPix(tmpSub/subNum,
								seedVolCoordsReverse);
					} // for z
		} // for all subjects
		avgData.rawNiftiData.write(outputFile);

	}

	public static void main(String[] args) throws InvalidImageException,
			IOException {

		if (args.length == 5) {
			String inputDirPre = args[0].trim();
			int startSubID = Integer.valueOf(args[1].trim());
			int endSubID = Integer.valueOf(args[2].trim());
			String comID = args[3].trim();
			String outputFile = args[4].trim();

			J_GenerateAvgVol mainHandler = new J_GenerateAvgVol();
			mainHandler.generateAvgVol(inputDirPre, startSubID, endSubID,
					comID, outputFile);
		} else
			System.out
					.println("Need para: inputDirPre startSubID endSubID comID outputFile");

	}

}
