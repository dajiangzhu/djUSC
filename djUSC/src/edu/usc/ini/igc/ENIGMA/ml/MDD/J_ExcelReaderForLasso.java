package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class J_ExcelReaderForLasso {
	
	J_SiteDictionary siteDic = new J_SiteDictionary();

	public String homeDataDir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\";
	
	public int feaNum = 0;
	public int subNum = 0;
	public Set<String> siteSet = new HashSet<String>();
	public Map<String,List> siteMap_G0 = new HashMap<String,List>();
	public Map<String,List> siteMap_G1 = new HashMap<String,List>();
	

	public void readExcel(String category, String fileName) throws BiffException, IOException
	{
		
		String filePath = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\"
				+ category + "\\" + fileName + ".xls";
		System.out.println("#####################Reading file: "+filePath+" ...");
		File excel_Current = new File(filePath);
		Workbook w_Current = Workbook.getWorkbook(excel_Current);
		Sheet sheet_Current = w_Current.getSheet(fileName.substring(0, 31));
		
		//find how many features
		feaNum = sheet_Current.getColumns()-3;
		//find how many subjects and sites
		subNum = sheet_Current.getRows()-1;
		String currentCell = null;
		for(int row=1;row<=subNum;row++)
		{
			currentCell = sheet_Current.getCell(2, row).getContents().trim().split("_")[0].trim();
			siteSet.add(currentCell);
			String group = sheet_Current.getCell(1, row).getContents().trim();
			Map<String,List> siteMap = null;
			if(group.equals("0"))
				siteMap = siteMap_G0;
			else
				siteMap = siteMap_G1;
			
			String[] currentFeatures = new String[feaNum];
			for(int col = 0;col<feaNum;col++)
				currentFeatures[col] = sheet_Current.getCell(col+3, row).getContents().trim();
			
			if(siteMap.containsKey(currentCell))				
				siteMap.get(currentCell).add(currentFeatures);
			else
			{
				List<String[]> newList = new ArrayList<String[]>();
				newList.add(currentFeatures);
				siteMap.put(currentCell,newList);
			}
		} //for row
		System.out.println("#####################Reading file: "+filePath+" finished!");
	}
	
	public void prepareForLasso(String siteConfig)
	{
		System.out.println("#####################prepareForLasso...");
		List<String> siteConfigList = DicccolUtilIO.loadFileToArrayList(siteConfig);
		for(String line:siteConfigList)
		{
			String fileName = "J_LassoInput_";
			List<String> distributedLassoInput = new ArrayList<String>();
			String[] lineArray = line.split(":");
			for(int i=0;i<lineArray.length;i++)
			{
				String siteName = lineArray[i].trim();
				fileName += siteDic.getCodeFromSite(siteName);
				List<String[]> dataG0 = siteMap_G0.get(siteName);
				if(dataG0!=null)
					for(String[] currentdata:dataG0)
					{
						String currentLine = "0.0,";
						for(int f=0;f<feaNum;f++)
							currentLine = currentLine + currentdata[f] + " ";
						distributedLassoInput.add(currentLine);
					} //for
				List<String[]> dataG1 = siteMap_G1.get(siteName);
				if(dataG1!=null)
					for(String[] currentdata:dataG1)
					{
						String currentLine = "1.0,";
						for(int f=0;f<feaNum;f++)
							currentLine = currentLine + currentdata[f] + " ";
						distributedLassoInput.add(currentLine);
					} //for
			} //for i
			DicccolUtilIO.writeArrayListToFile(distributedLassoInput, fileName+".txt");
		} //for line
			
		System.out.println("#####################prepareForLasso finished!");
	}
	
	public void printDataInfo(String category, String fileName) {
		int subNumThre = 20;
		
		System.out.println("#####################PrintingDataInfo...");
		System.out.println("#####File: "+category+"\\"+fileName);
		System.out.println("#####There are "+subNum+" subjects in total.");
		System.out.println("#####There are "+feaNum+" features in total.");
		System.out.println("#####There are "+siteSet.size()+" Sites in total.");
		for(String site:siteSet)
		{
			System.out.println("-------------"+site);
			System.out.println("Group-0:");
			if(siteMap_G0.containsKey(site))
				System.out.println(siteMap_G0.get(site).size());
			else
				System.out.println("0");
			System.out.println("Group-1:");
			if(siteMap_G1.containsKey(site))
				System.out.println(siteMap_G1.get(site).size());
			else
				System.out.println("0");
		}	
		System.out.println("#####Sites (Group-0>"+subNumThre+" and Group-1>"+subNumThre+"):");
		for(String site:siteSet)
			if(siteMap_G0.containsKey(site) && siteMap_G1.containsKey(site))
				if(siteMap_G0.get(site).size()>subNumThre && siteMap_G1.get(site).size()>subNumThre)
					System.out.println(site);
		System.out.println("#####################PrintingDataInfo finished!");
	}

	public static void main(String[] args) throws BiffException, IOException {
		if (args.length == 2) {
			String category = args[0].trim();
			String fileName = args[1].trim();

			J_ExcelReaderForLasso mainHandler = new J_ExcelReaderForLasso();
			mainHandler.readExcel(category, fileName);
			mainHandler.printDataInfo(category, fileName);
			mainHandler.prepareForLasso("J_PrepareLassoInput_Site_Imputed_Over21_20.txt");

		} else
			System.out
					.println("Need Complete/Imputed MaleFemale/Over21/Recurrent/Under21");

	}

}
