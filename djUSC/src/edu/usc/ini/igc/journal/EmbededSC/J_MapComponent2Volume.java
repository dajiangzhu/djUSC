package edu.usc.ini.igc.journal.EmbededSC;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.xinapse.loadableimage.InvalidImageException;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class J_MapComponent2Volume {

	private djNiftiData maskData = null;
	private djNiftiData outputData = null;

	private void mapComponent2Volume(int subID, String AMatrixFilePre,
			String configInfoFile, String maskFile, int startComIndex,
			int endComIndex, String outputVolPre) throws NumberFormatException,
			InvalidImageException, IOException {
		List<String> configInfo = DicccolUtilIO
				.loadFileToArrayList(configInfoFile);
		String[] lastLine = configInfo.get(configInfo.size() - 1).split("\\s+");
		int lastOptComponentIndex = Integer.valueOf(lastLine[0].trim());
//		int lastOptRoundNum = Integer.valueOf(lastLine[1].trim()) + 1;
		System.out.println("--- lastOptComponentIndex:" + lastOptComponentIndex+ " ---");
		String AMtrixFile = AMatrixFilePre + "/OptDicIndex_"
				+ lastOptComponentIndex + "/sub_"+subID+"_OptDicIndex_"
				+ lastOptComponentIndex + "_Round_final_A.txt";
		System.out.println("Load the maskfile...");
		maskData = new djNiftiData(maskFile);
		outputData = new djNiftiData(maskFile);

		int voxCount = 0;
		for (int x = 0; x < maskData.xSize; x++)
			for (int y = 0; y < maskData.ySize; y++)
				for (int z = 0; z < maskData.zSize; z++)
					if (maskData.getValueBasedOnVolumeCoordinate(x, y, z, 0) > 0.5)
						voxCount++;

		System.out.println("There are total " + voxCount + " voxels!");
		try {
			FileInputStream fstream = new FileInputStream(AMtrixFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] tmpLine = null;
			int componentIndex = -1;

			for (int i = 0; i < startComIndex; i++)
			{
				strLine = br.readLine();
				componentIndex++;
			}
			for (int i = startComIndex; i <= endComIndex; i++) {
				strLine = br.readLine();
				componentIndex++;
				if (strLine != null) {
					System.out.println("Mapping the " + componentIndex
							+ " th component...");
					float[][][] resultVol = new float[maskData.xSize][maskData.ySize][maskData.zSize];
					tmpLine = strLine.split("\\s+");
					if (tmpLine.length != voxCount)
						System.out.println("ERROR with voxNum!!");
					int count = 0;
					for (int x = 0; x < maskData.xSize; x++)
						for (int y = 0; y < maskData.ySize; y++)
							for (int z = 0; z < maskData.zSize; z++)
								if (maskData.getValueBasedOnVolumeCoordinate(x,
										y, z, 0) > 0.5) {
									float val = Math.abs(Float
											.valueOf(tmpLine[count].trim()));
									resultVol[x][y][z] = val;
									count++;
								} // if

					for (int x = 0; x < maskData.xSize; x++)
						for (int y = 0; y < maskData.ySize; y++)
							for (int z = 0; z < maskData.zSize; z++) {
								int[] seedVolCoordsReverse = { z, y, x };
								this.outputData.rawNiftiData.putPix(
										resultVol[x][y][z],
										seedVolCoordsReverse);
							}
					this.outputData.rawNiftiData.write(outputVolPre + "Com_"
							+ componentIndex);
				} // if strLine!=null
				else
					System.out.println("ERROR: strLine is Null!!!");
			} //for startComIndex---endComIndex
			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static void main(String[] args) throws NumberFormatException,
			InvalidImageException, IOException {
		if (args.length == 7) {
			int subID = Integer.valueOf(args[0].trim());
			String AMatrixFilePre = args[1].trim();
			String configInfo = args[2].trim();
			String mask = args[3].trim();
			int StartComIndex = Integer.valueOf(args[4].trim());
			int EndComIndex = Integer.valueOf(args[5].trim());
			String outPutPre = args[6].trim();
			J_MapComponent2Volume mainHandler = new J_MapComponent2Volume();
			mainHandler.mapComponent2Volume(subID, AMatrixFilePre, configInfo, mask,
					StartComIndex, EndComIndex, outPutPre);
		} else
			System.out
					.println("Need para: subID AMatrixFilePre configInfoFile mask startComIndex endComIndex and outputpre");

	}

}
