package edu.usc.ini.igc.journal.EmbededSC;

import edu.uga.DICCCOL.DicccolUtilIO;

public class J_TestMixed {
	
	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}
	
	public void exploreTemSigCorrespondence()
	{
		double[][] stimulusA = this.trsposeM(DicccolUtilIO.loadFileAsArray("../../JournalPaper/ESL/MOTOR_taskdesign_hrf.txt", 284, 26), 284, 26) ;
		System.out.println("stimulusA[0][0]: " +stimulusA[0][0]);
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		J_TestMixed  mainHandler = new J_TestMixed();
		mainHandler.exploreTemSigCorrespondence();

	}

}
