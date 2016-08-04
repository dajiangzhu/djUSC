package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import edu.uga.DICCCOL.DicccolUtilIO;
public class ExcelReaderForGLM {
	
	final int FeatureNum_SurfAvg = 72;
	final int FeatureNum_ThickAvg = 72;
	final int FeatureNum_LRVolume = 16;

	public String homeDir = "";
	public List<String> CenterList = new ArrayList<String>();
	public Map<String, CenterInfo> allData = new HashMap<String, CenterInfo>();

	public void formatForGLM() {
		for (int i = 0; i < CenterList.size(); i++) {
			String currentCenterName = CenterList.get(i);
			System.out.println("Loading Center - " + currentCenterName);
		}
	}

	public void loadingAllCenters() throws BiffException, IOException {
		this.CenterList = DicccolUtilIO.loadFileToArrayList(this.homeDir+"\\MDD_Center_List.txt");
		File excel_SurfAvg = null;
		Workbook w_SurfAvg = null;
		Sheet sheet_SurfAvg = null;
		File excel_ThickAvg = null;
		Workbook w_ThickAvg = null;
		Sheet sheet_ThickAvg = null;
		File excel_LRVolume = null;
		Workbook w_LRVolume = null;
		Sheet sheet_LRVolume = null;
		for (int i = 0; i < CenterList.size(); i++) {
			String[] lineArray = CenterList.get(i).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			String[][] currentData = new String[numOfSub][FeatureNum_SurfAvg+FeatureNum_ThickAvg+FeatureNum_LRVolume];
			int featureCount = 0;
			System.out.println("Loading Center - " + currentCenterName + "              NumOfSub - " + numOfSub);
			
			//SurfAvg
			excel_SurfAvg = new File(this.homeDir+"\\"+currentCenterName+"\\CorticalMeasuresENIGMA_SurfAvg.xls");
			w_SurfAvg = Workbook.getWorkbook(excel_SurfAvg);
			sheet_SurfAvg = w_SurfAvg.getSheet("CorticalMeasuresENIGMA_SurfAvg");
			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_SurfAvg; col++) {
					Cell cell = sheet_SurfAvg.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						currentData[row-1][featureCount+col-1] = (cell.getContents().trim());
				} // for col
			} // for row
			featureCount += FeatureNum_SurfAvg;
			
			//ThickAvg
			excel_ThickAvg = new File(this.homeDir+"\\"+currentCenterName+"\\CorticalMeasuresENIGMA_ThickAvg.xls");
			w_ThickAvg = Workbook.getWorkbook(excel_ThickAvg);
			sheet_ThickAvg = w_ThickAvg.getSheet("CorticalMeasuresENIGMA_ThickAvg");
			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_ThickAvg; col++) {
					Cell cell = sheet_ThickAvg.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						currentData[row-1][featureCount+col-1] = (cell.getContents().trim());
				} // for col
			} // for row
			featureCount += FeatureNum_ThickAvg;
			
			//LRVolume
			excel_LRVolume = new File(this.homeDir+"\\"+currentCenterName+"\\LandRvolumes.xls");
			w_LRVolume = Workbook.getWorkbook(excel_LRVolume);
			sheet_LRVolume = w_LRVolume.getSheet("LandRvolumes");
			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_LRVolume; col++) {
					Cell cell = sheet_LRVolume.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						currentData[row-1][featureCount+col-1] = (cell.getContents().trim());
				} // for col
			} // for row
			allData.put(currentCenterName, new CenterInfo(currentCenterName,numOfSub,currentData));
			//DicccolUtilIO.writeStringArrayToFile(currentData, numOfSub, FeatureNum_SurfAvg+FeatureNum_ThickAvg+FeatureNum_LRVolume, " ", "testData.txt");
		} //for centers
	}

	public static void main(String[] args) throws BiffException, IOException {
		if (args.length != 1) {
			System.out.println("Need input the directory of data...");
			System.exit(0);
		}
		ExcelReaderForGLM mainHandler = new ExcelReaderForGLM();
		mainHandler.homeDir = args[0].trim();
		mainHandler.loadingAllCenters();

		// /////////////////////
		// mainHandler.test();

	}
	
	public void test() {
		OLSMultipleLinearRegression regression2 = new OLSMultipleLinearRegression();
		double[] y = { 4, 8, 13, 18 };
		double[][] x2 = { { 1, 1, 1 }, { 1, 2, 4 }, { 1, 3, 9 }, { 1, 4, 16 }, };

		regression2.newSampleData(y, x2);
		regression2.setNoIntercept(true);
		regression2.newSampleData(y, x2);
		double[] beta = regression2.estimateRegressionParameters();
		double[] residuals = regression2.estimateResiduals();
		for (double d : beta) {
			System.out.println("D: " + d);
		}
		for (double d : residuals) {
			System.out.println("Residuals: " + d);
		}
	}

}
