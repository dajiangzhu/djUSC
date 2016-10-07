package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FormatMelbourneData {
	
	public void test()
	{
		
		List<Integer> list = DicccolUtil.geneRandom(10, 20);
		System.out.println(list);
	}
	
	public void dealWithMelbourne() throws BiffException, IOException
	{
		File excel_SurfAvg = null;
		Workbook w_SurfAvg = null;
		Sheet sheet_SurfAvg = null;
		File excel_ThickAvg = null;
		Workbook w_ThickAvg = null;
		Sheet sheet_ThickAvg = null;
		File excel_LRVolume = null;
		Workbook w_LRVolume = null;
		Sheet sheet_LRVolume = null;
		File excel_Covariates = null;
		Workbook w_Covariates = null;
		Sheet sheet_Covariates = null;
		
		excel_SurfAvg = new File("E:\\data\\Machine_Learning_MDD\\Melbourne\\CorticalMeasuresENIGMA_SurfAvg.xls");
		w_SurfAvg = Workbook.getWorkbook(excel_SurfAvg);
		sheet_SurfAvg = w_SurfAvg
				.getSheet("CorticalMeasuresENIGMA_SurfAvg");
		
		excel_ThickAvg = new File("E:\\data\\Machine_Learning_MDD\\Melbourne\\CorticalMeasuresENIGMA_ThickAvg.xls");
		w_ThickAvg = Workbook.getWorkbook(excel_ThickAvg);
		sheet_ThickAvg = w_ThickAvg
				.getSheet("CorticalMeasuresENIGMA_ThickAvg");
		
		excel_LRVolume = new File("E:\\data\\Machine_Learning_MDD\\Melbourne\\LandRvolumes.xls");
		w_LRVolume = Workbook.getWorkbook(excel_LRVolume);
		sheet_LRVolume = w_LRVolume.getSheet("LandRvolumes");

		//check T1 measure
		List<String> subIdList = new ArrayList<String>();
		for (int row = 1; row < 187; row++)
		{
			String subID_SurfAvg = sheet_SurfAvg.getCell(0, row).getContents().trim();
			String subID_ThickAvg = sheet_ThickAvg.getCell(0, row).getContents().trim();
			String subID_LRVolume = sheet_LRVolume.getCell(0, row).getContents().trim();
			if( subID_SurfAvg.equals(subID_ThickAvg) && subID_SurfAvg.equals(subID_LRVolume) )
			{
				System.out.println(row + ": Good!");
				subIdList.add(subID_SurfAvg);
			}
			else
				System.out.println("********* Error! line: "+row+"  ***************");
		}
		System.out.println("### subIdList.size: "+subIdList.size());
		
		//extract the subject needed
		excel_Covariates = new File("E:\\data\\Machine_Learning_MDD\\Melbourne\\CovariatesOri.xls");
		w_Covariates = Workbook.getWorkbook(excel_Covariates);
		sheet_Covariates = w_Covariates.getSheet("Sheet1");
		List<String> formatedCovariates = new ArrayList<String>();
		for(int s=0;s<subIdList.size();s++)
		{
			String line = "";
			for (int row = 1; row < 200; row++)
			{
				String currentSubID = sheet_Covariates.getCell(0, row).getContents().trim();
				if(currentSubID.equals(subIdList.get(s)))
				{
					System.out.println("***** Found at row: "+row);
					for(int col = 0;col<4;col++)
						line += sheet_Covariates.getCell(col, row).getContents().trim() +" ";
					formatedCovariates.add(line);
				} //if
			} //for row
		} //for s
		DicccolUtilIO.writeArrayListToFile(formatedCovariates, "E:\\data\\Machine_Learning_MDD\\Melbourne\\CovariatesFormated.txt");

	}

	public static void main(String[] args) throws BiffException, IOException {
		FormatMelbourneData mainHandler = new FormatMelbourneData();
//		mainHandler.dealWithMelbourne();
		mainHandler.test();

	}

}
