package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class J_TestMixed {

	Correlation correlation = new Correlation();

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	public void checkTmpSigInnerCorre() {
		double[][] tmpSigA = new double[29][284];
		for (int t = 0; t < 29; t++) {
			double[][] tmp = DicccolUtilIO.loadFileAsArray(
					"/home/dzhu/Projects/Journal_ESL/ana_templatesig/TemplateSig_"
							+ t + ".txt", 284, 1);
			for (int i = 0; i < 284; i++)
				tmpSigA[t][i] = tmp[i][0];
		} // for i

		// calculate the corre within the templateSig
		int[][] indexStatus = new int[29][2];
		for (int i = 0; i < 29; i++) {
			indexStatus[i][0] = i;
			indexStatus[i][1] = 1;
		}
		double[][] corrM_inTmpSig = new double[29][29];
		for (int i = 0; i < 28; i++)
			for (int j = i + 1; j < 29; j++) {
				if (indexStatus[i][1] == 1 && indexStatus[j][1] == 1) {
					corrM_inTmpSig[i][j] = correlation.Correlation_Pearsons(
							tmpSigA[i], tmpSigA[j]);
					if (corrM_inTmpSig[i][j] >= 0.2) {
						indexStatus[j][1] = 0;
						System.out.println(i + " - " + j + ": "
								+ corrM_inTmpSig[i][j]);
					}
				} // if
			} // for
		for (int i = 0; i < 29; i++)
			if (indexStatus[i][1] == 0)
				System.out.println(i);
		// DicccolUtilIO
		// .writeVtkMatrix1(corrM_inTmpSig, 29, 26,
		// "/home/dzhu/Projects/Journal_ESL/ana_templatesig/corrM_inTmpSig.vtk");
		// DicccolUtilIO
		// .writeArrayToFile(corrM_inTmpSig, 29, 26, "   ",
		// "/home/dzhu/Projects/Journal_ESL/ana_templatesig/corrM_inTmpSig.txt");

	}

	public boolean checkcorrM_TS(double[][] corrM_TS, int currentIndex,
			int checkIndex) {
		boolean flag = true;
		for (int i = 0; i < checkIndex; i++) {
			double tmpCorr = correlation.Correlation_Pearsons(
					corrM_TS[currentIndex], corrM_TS[i]);
			if (tmpCorr > 0.8)
				flag = false;
		}
		return flag;
	}

	public void exploreTemSigCorrespondence() {

		// load matrix of stimulus and tmplateSig
		double[][] stimulusA = this
				.trsposeM(
						DicccolUtilIO
								.loadFileAsArray(
										"/home/dzhu/Projects/Journal_ESL/ana_templatesig/MOTOR_taskdesign_hrf.txt",
										284, 26), 284, 26);
		double[][] tmpSigA = new double[29][284];
		for (int t = 0; t < 29; t++) {
			double[][] tmp = DicccolUtilIO.loadFileAsArray(
					"/home/dzhu/Projects/Journal_ESL/ana_templatesig/TemplateSig_"
							+ t + ".txt", 284, 1);
			for (int i = 0; i < 284; i++)
				tmpSigA[t][i] = tmp[i][0];
		} // for i

		// calculate TT
		double[][] corrM_TT = new double[29][29];
		for (int i = 0; i < 29; i++)
			for (int j = 0; j < 29; j++)
				corrM_TT[i][j] = correlation.Correlation_Pearsons(tmpSigA[i],
						tmpSigA[j]);

		double[][] corrM_TS = new double[29][26];
		for (int i = 0; i < 29; i++)
			for (int j = 0; j < 26; j++)
				corrM_TS[i][j] = correlation.Correlation_Pearsons(tmpSigA[i],
						stimulusA[j]);

		double[][] corrM_TSTS = new double[29][29];
		for (int i = 0; i < 29; i++)
			for (int j = 0; j < 29; j++)
				corrM_TSTS[i][j] = correlation.Correlation_Pearsons(
						corrM_TS[i], corrM_TS[j]);
		DicccolUtilIO
				.writeVtkMatrix1(corrM_TSTS, 29, 29,
						"/home/dzhu/Projects/Journal_ESL/ana_templatesig/corrM_TSTS.vtk");
		DicccolUtilIO
				.writeArrayToFile(corrM_TSTS, 29, 29, "   ",
						"/home/dzhu/Projects/Journal_ESL/ana_templatesig/corrM_TSTS.txt");

		// order the matrix by stimulus
		int[] orderIndex = new int[29];
		for(int i=0;i<29;i++)
			orderIndex[i] = i;
		double[] tmpPlace = new double[284];
		int tmpIndex = -1;
		for (int i = 0; i < 26; i++) {
			double tmpMax = -1.0;
			int tmpPointer = -1;
			for (int j = i; j < 29; j++)
				if (corrM_TS[j][i] > tmpMax
						&& this.checkcorrM_TS(corrM_TS, j, i)) {
					tmpMax = corrM_TS[j][i];
					tmpPointer = j;
				} // if
			if (tmpPointer != -1) {
				System.out.println("Good: "+i);
				tmpPlace = corrM_TS[tmpPointer];
				tmpIndex = orderIndex[tmpPointer];
				corrM_TS[tmpPointer] = corrM_TS[i];
				orderIndex[tmpPointer] = orderIndex[i];				
				corrM_TS[i] = tmpPlace;
				orderIndex[i] = tmpIndex;
			}
			else
				System.out.println("Begin to similar: "+i);
		}
		for(int i=0;i<29;i++)
		System.out.println(orderIndex[i]);
		DicccolUtilIO
				.writeVtkMatrix1(corrM_TS, 29, 26,
						"/home/dzhu/Projects/Journal_ESL/ana_templatesig/correM_ordered_again.vtk");

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		J_TestMixed mainHandler = new J_TestMixed();
		mainHandler.exploreTemSigCorrespondence();

	}

}
