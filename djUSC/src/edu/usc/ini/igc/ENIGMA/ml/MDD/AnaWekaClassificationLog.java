package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class AnaWekaClassificationLog {

	public static void main(String[] args) {
		
		
		
		for(int l=2;l<8;l++)
		{
			String fileName = "log_layer_"+l+".txt";
			System.out.println("**********************************   "+fileName+"        ********************************************");
			
			double tp = 100.0;
			double tn = 0.0;
			double sum = 0.0;
			
			List<String> logList = DicccolUtilIO.loadFileToArrayList(fileName);
			for(int i=0;i<logList.size();i++)
			{
				if(logList.get(i).startsWith("TP"))
				{
					String[] currentLine = logList.get(i).split("\\s+");
					double tmpTP = Double.valueOf(currentLine[0].split(":")[1].trim());
					double tmpTN = Double.valueOf(currentLine[1].split(":")[1].trim());
					double tmpSum = tmpTP+tmpTN;
//					if(tmpTP>100.0 && tmpTN>500.0 && tmpSum>sum)
					if(tmpTP>tp && tmpSum>630)
					{
						tp = tmpTP;
						tn = tmpTN;
						sum = tmpSum;
						System.out.println("Sum: "+sum+"    "+logList.get(i));
					}
				}
			} //for i
			
		}


	}

}
