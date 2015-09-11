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
    private djVtkSurData surData = null;
    private String AMtrixFile = "";
    private float threshold = 5.5f;


	private void mapComponent2Volume(String AMtrixFile, String maskFile,
			String outputVolPre) throws NumberFormatException,
			InvalidImageException, IOException {
		System.out.println("Load the maskfile...");
		maskData = new djNiftiData(maskFile);

		int voxCount = 0;
		for (int x = 0; x < maskData.xSize; x++)
			for (int y = 0; y < maskData.ySize; y++)
				for (int z = 0; z < maskData.zSize; z++)
					if (maskData.getValueBasedOnVolumeCoordinate(x, y, z, 0) > 0.5)
						voxCount++;

		float[][][] resultVol = new float[maskData.xSize][maskData.ySize][maskData.zSize];
		System.out.println("There are total " + voxCount + " voxels!");
		try {
			FileInputStream fstream = new FileInputStream(AMtrixFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String[] tmpLine = null;
			int lineCount = 1;
			while ((strLine = br.readLine()) != null) {
				// if(lineCount%10==0)
				System.out.println("The " + (lineCount++) + " th line...");
				tmpLine = strLine.split("\\s+");
				if (tmpLine.length != voxCount)
					System.out.println("ERROR with voxNum!!");
				int count = 0;
				for (int x = 0; x < maskData.xSize; x++)
					for (int y = 0; y < maskData.ySize; y++)
						for (int z = 0; z < maskData.zSize; z++)
							if (maskData.getValueBasedOnVolumeCoordinate(x, y,
									z, 0) > 0.5) {
								float val = Float
										.valueOf(tmpLine[count].trim());
								if (val >= threshold)
									resultVol[x][y][z] = 1.0f;
								count++;
							} // if

			}// while
			br.close();
			in.close();
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		for (int x = 0; x < maskData.xSize; x++)
			for (int y = 0; y < maskData.ySize; y++)
				for (int z = 0; z < maskData.zSize; z++) {
					int[] seedVolCoordsReverse = { z, y, x };
					if (resultVol[x][y][z] == 1.0f)
						this.maskData.rawNiftiData.putPix(Float.valueOf("1.0"),
								seedVolCoordsReverse);
					else
						this.maskData.rawNiftiData.putPix(Float.valueOf("0.0"),
								seedVolCoordsReverse);
				}
		this.maskData.rawNiftiData.write(outputVolPre + "vol_" + threshold);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
