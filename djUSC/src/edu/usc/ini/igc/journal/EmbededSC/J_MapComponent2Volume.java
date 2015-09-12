package edu.usc.ini.igc.journal.EmbededSC;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.xinapse.loadableimage.InvalidImageException;

import edu.uga.liulab.djVtkBase.djNiftiData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class J_MapComponent2Volume {

	private djNiftiData maskData = null;
	private djNiftiData outputData = null;

	private void mapComponent2Volume(String AMtrixFile, String maskFile,
			int startComIndex, int endComIndex, String outputVolPre)
			throws NumberFormatException, InvalidImageException, IOException {
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
			int componentIndex = 0;
			while ((strLine = br.readLine()) != null) {
				if (componentIndex >= startComIndex
						&& componentIndex <= endComIndex) {
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
								this.maskData.rawNiftiData.putPix(
										resultVol[x][y][z],
										seedVolCoordsReverse);
							}
					this.maskData.rawNiftiData.write(outputVolPre + "Com_"+componentIndex);
				} // if
				componentIndex++;
			}// while
			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}

	public static void main(String[] args) throws NumberFormatException,
			InvalidImageException, IOException {
		if (args.length == 5) {
			String AMatrix = args[0].trim();
			String mask = args[1].trim();
			int StartComIndex = Integer.valueOf(args[2].trim());
			int EndComIndex = Integer.valueOf(args[3].trim());
			String outPutPre = args[4].trim();
			J_MapComponent2Volume mainHandler = new J_MapComponent2Volume();
			mainHandler.mapComponent2Volume(AMatrix, mask, StartComIndex,
					EndComIndex, outPutPre);
		} else
			System.out
					.println("Need para: AMatrix mask startComIndex endComIndex and outputpre");

	}

}
