package edu.usc.ini.igc.journal.EmbededSC;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class CorrectComponent {
	
	public int dicSize=400;
	public int tSize = 284;
	Correlation correlation = new Correlation();

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}
	
	public void correctCom(int comID)
	{
		String templateFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/MOTOR/1_68/TemplateSig_"+comID+".txt";
		double[][]tmpSig = this.trsposeM(DicccolUtilIO.loadFileAsArray(templateFile, tSize, 1), tSize, 1);
		
		for(int s=1;s<=68;s++)
		{
			System.out.println("Checking sub - "+s+" ...............................");
			String curDFile = "/ifs/loni/faculty/thompson/four_d/dzhu/Journal_ESL/results/MOTOR/1_68/"+s+"/OptDicIndex_28/sub_"+s+"_OptDicIndex_28_Round_final_D.txt";
			double[][]curDMatrix = this.trsposeM(DicccolUtilIO.loadFileAsArray(curDFile, tSize, dicSize),tSize,dicSize);
			double optmizedPvalue = correlation.Correlation_Pearsons(tmpSig[0], curDMatrix[comID]);
			System.out.println("optmizedPvalue: "+optmizedPvalue);
			for(int d=29;d<tSize;d++)
			{
				double tmpPvalue = correlation.Correlation_Pearsons(tmpSig[0], curDMatrix[d]);
				if(tmpPvalue>optmizedPvalue)
					System.out.println("component - "+d+" is better!");
				
			} //for d
		}
		
	}

	public static void main(String[] args) {

		if (args.length == 1) {
			int comID = Integer.valueOf(args[0].trim());
			CorrectComponent mainHandler = new CorrectComponent();
			mainHandler.correctCom(comID);
		} else
			System.out.println("Need para: comID");
	}

}
