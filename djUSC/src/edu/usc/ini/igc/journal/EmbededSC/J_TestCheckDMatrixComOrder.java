package edu.usc.ini.igc.journal.EmbededSC;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class J_TestCheckDMatrixComOrder {

	public int tSize = 284;
	public int dicSize = 400;
	public int templateNum = 29;
	public String taskName = "MOTOR";

	Correlation correlation = new Correlation();
	double[][] tmpSigA = new double[templateNum][tSize];

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	public void loadTmpSigA() {
		for (int t = 0; t < templateNum; t++) {
			double[][] tmp = DicccolUtilIO.loadFileAsArray(
					"/home/dzhu/Projects/Journal_ESL/ana_templatesig/TemplateSig_"
							+ t + ".txt", 284, 1);
			for (int i = 0; i < 284; i++)
				tmpSigA[t][i] = tmp[i][0];
		} // for i
	}

	public void checkDMatrixCorreWithTemplate() {
		String curFile = "/home/dzhu/Projects/Journal_ESL/DMatrix/sub_1_OptDicIndex_28_Round_final_D.txt";
		System.out.println("Loading : " + curFile);
		double[][] curSigM = this.trsposeM(
				DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize), tSize,
				dicSize);
		double[][] corrM = new double[dicSize][templateNum];
		for(int d=0;d<dicSize;d++)
			for(int t=0;t<templateNum;t++)
			{
				corrM[d][t] = correlation
						.Correlation_Pearsons(curSigM[d], tmpSigA[t]);
			}
		DicccolUtilIO.writeArrayToFile(corrM, dicSize, templateNum, " ", "/home/dzhu/Projects/Journal_ESL/checkDMatrix_sub1.txt");
		DicccolUtilIO.writeVtkMatrix1(corrM, dicSize, templateNum, "/home/dzhu/Projects/Journal_ESL/checkDMatrix_sub1.vtk");

	}

	public static void main(String[] args) {
		J_TestCheckDMatrixComOrder mainHandler = new J_TestCheckDMatrixComOrder();
		mainHandler.loadTmpSigA();
		mainHandler.checkDMatrixCorreWithTemplate();

	}

}
