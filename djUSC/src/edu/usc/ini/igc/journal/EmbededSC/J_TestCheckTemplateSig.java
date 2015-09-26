package edu.usc.ini.igc.journal.EmbededSC;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.MapComparator;
import edu.uga.DICCCOL.stat.Correlation;

public class J_TestCheckTemplateSig {

	int numOfTemplate = -1;
	double[][] stimulusA;
	double[][] templateSigA;
	Correlation correlation = new Correlation();

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	public void loadStimulusSig() {
		stimulusA = this
				.trsposeM(
						DicccolUtilIO
								.loadFileAsArray(
										"/home/dzhu/Projects/Journal_ESL/ana_templatesig/MOTOR_taskdesign_hrf.txt",
										284, 26), 284, 26);
	}

	public void loadTemplateSig() {
		templateSigA = new double[numOfTemplate][284];
		for (int t = 0; t < numOfTemplate; t++) {
			double[][] tmp = DicccolUtilIO.loadFileAsArray(
					"/home/dzhu/Projects/Journal_ESL/ana_templatesig/35_68/TemplateSig_"
							+ t + ".txt", 284, 1);
			for (int i = 0; i < 284; i++)
				templateSigA[t][i] = tmp[i][0];
		} // for i
	}
	
	public void checkCorrelation()
	{
		this.loadStimulusSig();
		this.loadTemplateSig();
		for(int s=0;s<26;s++)
		{
			double[] currentStimulus = stimulusA[s];
			HashMap tmpMap = new HashMap();
			for(int t=0;t<this.numOfTemplate;t++)
			{
				double[] currentTemplateSig = templateSigA[t];
				double cor = correlation.Correlation_Pearsons(currentStimulus, currentTemplateSig);
				tmpMap.put("Template_"+t, cor);
			} //for t
			J_UtilMapComparator com = new J_UtilMapComparator(tmpMap,false);
			TreeMap sortedMap = new TreeMap(com);
			sortedMap.putAll(tmpMap);
			System.out.println("Stimulus-"+s+": "+sortedMap);
		} //for s
	}

	public static void main(String[] args) {
		J_TestCheckTemplateSig mainHandler = new J_TestCheckTemplateSig();
		mainHandler.numOfTemplate = 15;
		mainHandler.checkCorrelation();

	}

}
