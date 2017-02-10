package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class J_AnaClassificationResult {
	
	J_SiteDictionary siteDic = new J_SiteDictionary();
	public String homeDataDir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\";
	
	public void AnaSMOResult(String siteConfig) {
		System.out.println("#####################AnaSMOResult...");
		List<String> siteConfigList = DicccolUtilIO
				.loadFileToArrayList(siteConfig);
		for (String line : siteConfigList) {
			List<String> currentSiteList = new ArrayList<String>();
			String fileName = "J_LassoInput_";
			String[] lineArray = line.split(":");
			for (int i = 0; i < lineArray.length; i++) {
				String siteName = lineArray[i].trim();
				currentSiteList.add(siteName);
				fileName += siteDic.getCodeFromSite(siteName);
			} // for i

			System.out.println("");
			System.out.println("-----------------------------------------------------Analyzing Classification Result for " + fileName + "("
					+ currentSiteList + ")...");
			double aMax = 0.0;
			double sMax = 0.0;
			double gMax = 0.0;
			String paraLine = "";
			String recordLine = "";
			File dir = new File(homeDataDir+"ClassificationResult\\SingleSite\\"+fileName);
			String[] files = dir.list();
			for(int i=1;i<=100;i++)
			{
				for(String logFile:files)
				{
					if(logFile.endsWith("."+i))
					{
//						System.out.println("File: "+logFile);
						List<String> resultList = DicccolUtilIO.loadFileToArrayList(homeDataDir+"ClassificationResult\\SingleSite\\"+fileName+"\\"+logFile);
						for(int l=0;l<resultList.size();l++)
						{
							String currentResultLine = resultList.get(l);
							if(currentResultLine.startsWith("All"))
							{
								String[] tmpLine = currentResultLine.split("\\s+");
								double tmpA = Double.valueOf( tmpLine[1].split("\\(")[0].trim() );
								if(tmpA>aMax)
								{
									double tmpS = Double.valueOf( tmpLine[3].split("\\(")[0].trim() );
									double tmpG = Double.valueOf( tmpLine[5].split("\\(")[0].trim() );
									aMax = tmpA;
									sMax = tmpS;
									gMax = tmpG;
									recordLine = currentResultLine;
									paraLine = resultList.get(l-1);
								}
							} //if
						} //for currentResultLine
					} //if
				} //for files
			} //for i
			System.out.println("aMax:"+aMax+" sMax:"+sMax+" gMax:"+gMax);
			System.out.println(""+paraLine);
			System.out.println(recordLine);


		} // for line
		System.out.println("#####################AnaSMOResult finished!");
	}

	public static void main(String[] args) {
		J_AnaClassificationResult mainHandler = new J_AnaClassificationResult();
		mainHandler.AnaSMOResult("J_PrepareLassoInput_Site_Imputed_Over21_20.txt");

	}

}
