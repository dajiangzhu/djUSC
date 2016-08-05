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

	// haha
	// jj
	final int FeatureNum_SurfAvg = 72;
	final int FeatureNum_ThickAvg = 72;
	final int FeatureNum_LRVolume = 14;// 16
	int FeatureNum_Total = FeatureNum_SurfAvg + FeatureNum_ThickAvg
			+ FeatureNum_LRVolume;

	public String homeDir = "";
	public List<String> CenterList = new ArrayList<String>();
	public Map<String, CenterInfo> allCenter = new HashMap<String, CenterInfo>();
	public List<Integer> featureRemoveList = new ArrayList<Integer>();
	public String[] oriFeatureIDList = new String[FeatureNum_Total];

	public void formatForGLM() {
		for (int i = 0; i < CenterList.size(); i++) {
			String currentCenterName = CenterList.get(i);
			System.out.println("Loading Center - " + currentCenterName);
		}
	}

	public double[][] calMissingRate(String strPre) {
		System.out.println("##############calMissingRate(" + strPre
				+ ")############");
		System.out.println("---feature removed: " + featureRemoveList.size());
		System.out.println(featureRemoveList);
		int totalSubRemoved = 0;
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			System.out.println("---" + currentCenterName);
			System.out.println("subjects removed: "
					+ allCenter.get(currentCenterName).subRemoveList.size());
			System.out.println(allCenter.get(currentCenterName).subRemoveList);
			totalSubRemoved += allCenter.get(currentCenterName).subRemoveList
					.size();
		}
		System.out.println("############## FeatureRemoved:"
				+ featureRemoveList.size()
				+ "                 TotalSubRemoved:" + totalSubRemoved
				+ "  ############");

		double[][] dataMissingNum = new double[FeatureNum_Total][CenterList
				.size()];
		double[][] dataMissingRate = new double[FeatureNum_Total][CenterList
				.size()];
		for (int i = 0; i < FeatureNum_Total; i++)
			for (int j = 0; j < CenterList.size(); j++)
				dataMissingRate[i][j] = -1.0;

		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			String[][] currentData = allCenter.get(currentCenterName).oriData;
			for (int f = 0; f < FeatureNum_Total; f++) {
				if (!featureRemoveList.contains(f)) {
					for (int s = 0; s < numOfSub; s++) {
						if (!allCenter.get(currentCenterName).subRemoveList
								.contains(s))
							if (currentData[s][f].equals("NA") || currentData[s][f].equals("x"))
								dataMissingNum[f][c]++;
					} // for s
					dataMissingRate[f][c] = dataMissingNum[f][c]
							/ (double) numOfSub;
				} // if
			} // for f
		} // for c
		DicccolUtilIO.writeArrayToFile(dataMissingRate, FeatureNum_Total,
				CenterList.size(), " ", "DataMissingRate_" + strPre + ".txt");
		return dataMissingRate;
	}

	public void screenData() {
		System.out.println("############## screenData ############");
		double removeRate = 0.03;
		double[][] dataMissingRate = this.calMissingRate("before");

		// Remove features which have the most NAs
		for (int f = 0; f < FeatureNum_Total; f++) {
			boolean bRemove = false;
			for (int c = 0; c < CenterList.size(); c++)
				if (dataMissingRate[f][c] > removeRate)
					bRemove = true;
			if (!featureRemoveList.contains(f) && bRemove)
				featureRemoveList.add(f);
		} // for f

		// Remove subjects which have NAs
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			String[][] currentData = allCenter.get(currentCenterName).oriData;
			for (int f = 0; f < FeatureNum_Total; f++) {
				if (!featureRemoveList.contains(f)) {
					for (int s = 0; s < numOfSub; s++)
						if (currentData[s][f].equals("NA") || currentData[s][f].equals("x"))
							if (!allCenter.get(currentCenterName).subRemoveList
									.contains(s))
								allCenter.get(currentCenterName).subRemoveList
										.add(s);
				} // if
			} // for f
		} // for c
		this.calMissingRate("after");

		// Fill the dataAfterScreen
		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			int numOfLeftSub = numOfSub
					- allCenter.get(currentCenterName).subRemoveList.size();
			allCenter.get(currentCenterName).numOfLeftSub = numOfLeftSub;
			double[][] dataAfterScreen = new double[numOfLeftSub][numOfLeftFeature];
			double[][] covariatesAfterScreen = new double[numOfLeftSub][2];
			String[] labelAfterScreen = new String[numOfLeftSub];
			int leftSubCount = 0;
			for (int s = 0; s < numOfSub; s++) {
				int leftFeatureCount = 0;
				if (!allCenter.get(currentCenterName).subRemoveList.contains(s)) {
					for (int f = 0; f < FeatureNum_Total; f++) {
						if (!featureRemoveList.contains(f)) {
							dataAfterScreen[leftSubCount][leftFeatureCount] = Double
									.valueOf(allCenter.get(currentCenterName).oriData[s][f]);
							leftFeatureCount++;
						} // if this feature was not removed
					} // for f
					labelAfterScreen[leftSubCount] = String.valueOf(allCenter
							.get(currentCenterName).oriCovariates[s][0]);
					covariatesAfterScreen[leftSubCount][0] = allCenter
							.get(currentCenterName).oriCovariates[s][1]; // Age
					covariatesAfterScreen[leftSubCount][1] = allCenter
							.get(currentCenterName).oriCovariates[s][2]; // Sex
					leftSubCount++;
				} // if this subject was not removed
			} // for s
			allCenter.get(currentCenterName).dataAfterScreen = dataAfterScreen;
			allCenter.get(currentCenterName).covariatesAfterScreen = covariatesAfterScreen;
			allCenter.get(currentCenterName).labelAfterScreen = labelAfterScreen;
			DicccolUtilIO.writeArrayToFile(dataAfterScreen,
					numOfLeftSub,
					numOfLeftFeature, " ", "dataAfterScreen_" + currentCenterName
							+ ".txt");
		} // for c
	}

	public void GlmFit() {
		System.out.println("############## GlmFit ############");
		int numOfSubTotalLeft = 0;
		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			numOfSubTotalLeft += allCenter.get(currentCenterName).numOfLeftSub;
		} // for c
		double[][] allY = new double[numOfSubTotalLeft][numOfLeftFeature];
		double[][] allX = new double[numOfSubTotalLeft][2];

		// Fill allX and allY
		int totalSubCount = 0;
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			for (int s = 0; s < allCenter.get(currentCenterName).numOfLeftSub; s++) {
				for (int f = 0; f < numOfLeftFeature; f++)
					allY[totalSubCount][f] = allCenter.get(currentCenterName).dataAfterScreen[s][f];
				allX[totalSubCount][0] = allCenter.get(currentCenterName).covariatesAfterScreen[s][0];
				allX[totalSubCount][1] = allCenter.get(currentCenterName).covariatesAfterScreen[s][1];
				totalSubCount++;
			}
		} // for c

		// GLM fit
		double[][] allResiduals = new double[numOfSubTotalLeft][numOfLeftFeature];
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
//		regression.setNoIntercept(true);

		double[] currentY = new double[numOfSubTotalLeft];
		for (int f = 0; f < numOfLeftFeature; f++) {
			System.out.println("GLM for the " + f + "/" + numOfLeftFeature
					+ " th feature...");
			for (int s = 0; s < numOfSubTotalLeft; s++)
				currentY[s] = allY[s][f];
			regression.newSampleData(currentY, allX);
			double[] residuals = regression.estimateResiduals();
			for (int s = 0; s < numOfSubTotalLeft; s++)
				allResiduals[s][f] = residuals[s];
		} // for f

		// Save to different center
		totalSubCount = 0;
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			double[][] dataAfterGLM = new double[allCenter
					.get(currentCenterName).numOfLeftSub][numOfLeftFeature];
			for (int s = 0; s < allCenter.get(currentCenterName).numOfLeftSub; s++) {
				for (int f = 0; f < numOfLeftFeature; f++)
					dataAfterGLM[s][f] = allResiduals[totalSubCount][f];
				totalSubCount++;
			} // for s
			allCenter.get(currentCenterName).dataAfterGLM = dataAfterGLM;
			DicccolUtilIO.writeArrayToFile(dataAfterGLM,
					allCenter.get(currentCenterName).numOfLeftSub,
					numOfLeftFeature, " ", "dataAfterGLM_" + currentCenterName
							+ ".txt");
		} // for c
		DicccolUtilIO.writeArrayToFile(allResiduals, numOfSubTotalLeft,
				numOfLeftFeature, " ", "dataAfterGLM_All.txt");

	}

	public void loadingAllCenters() throws BiffException, IOException {
		System.out.println("############## loadingAllCenters ############");
		this.CenterList = DicccolUtilIO.loadFileToArrayList(this.homeDir
				+ "\\MDD_Center_List.txt");
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
		for (int i = 0; i < CenterList.size(); i++) {
			String[] lineArray = CenterList.get(i).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			String[] oriSubIDList = new String[numOfSub];
			String[][] oriData = new String[numOfSub][FeatureNum_SurfAvg
					+ FeatureNum_ThickAvg + FeatureNum_LRVolume];
			double[][] oriCovariates = new double[numOfSub][3]; // Dx, Age, Sex
			int featureCount = 0;
			System.out.println("Loading Center - " + currentCenterName
					+ "              NumOfSub - " + numOfSub);

			// SurfAvg
			excel_SurfAvg = new File(this.homeDir + "\\" + currentCenterName
					+ "\\CorticalMeasuresENIGMA_SurfAvg.xls");
			w_SurfAvg = Workbook.getWorkbook(excel_SurfAvg);
			sheet_SurfAvg = w_SurfAvg
					.getSheet("CorticalMeasuresENIGMA_SurfAvg");

			for (int col = 1; col <= FeatureNum_SurfAvg; col++)
				oriFeatureIDList[featureCount + col - 1] = sheet_SurfAvg
						.getCell(col, 0).getContents().trim();

			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_SurfAvg; col++) {
					Cell cell = sheet_SurfAvg.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						oriData[row - 1][featureCount + col - 1] = (cell
								.getContents().trim());
				} // for col
			} // for row
			featureCount += FeatureNum_SurfAvg;

			// ThickAvg
			excel_ThickAvg = new File(this.homeDir + "\\" + currentCenterName
					+ "\\CorticalMeasuresENIGMA_ThickAvg.xls");
			w_ThickAvg = Workbook.getWorkbook(excel_ThickAvg);
			sheet_ThickAvg = w_ThickAvg
					.getSheet("CorticalMeasuresENIGMA_ThickAvg");

			for (int col = 1; col <= FeatureNum_ThickAvg; col++)
				oriFeatureIDList[featureCount + col - 1] = sheet_ThickAvg
						.getCell(col, 0).getContents().trim();

			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_ThickAvg; col++) {
					Cell cell = sheet_ThickAvg.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						oriData[row - 1][featureCount + col - 1] = (cell
								.getContents().trim());
				} // for col
			} // for row
			featureCount += FeatureNum_ThickAvg;

			// LRVolume
			excel_LRVolume = new File(this.homeDir + "\\" + currentCenterName
					+ "\\LandRvolumes.xls");
			w_LRVolume = Workbook.getWorkbook(excel_LRVolume);
			sheet_LRVolume = w_LRVolume.getSheet("LandRvolumes");

			for (int col = 1; col <= FeatureNum_LRVolume; col++)
				oriFeatureIDList[featureCount + col - 1] = sheet_LRVolume
						.getCell(col, 0).getContents().trim();

			for (int row = 1; row <= numOfSub; row++) {
				for (int col = 1; col <= FeatureNum_LRVolume; col++) {
					Cell cell = sheet_LRVolume.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						oriData[row - 1][featureCount + col - 1] = (cell
								.getContents().trim());
				} // for col
			} // for row

			// Covariates
			excel_Covariates = new File(this.homeDir + "\\" + currentCenterName
					+ "\\Covariates.xls");
			w_Covariates = Workbook.getWorkbook(excel_Covariates);
			sheet_Covariates = w_Covariates.getSheet("Covariates");
			for (int row = 1; row <= numOfSub; row++) {
				oriSubIDList[row - 1] = sheet_Covariates.getCell(0, row)
						.getContents().trim();
				for (int col = 1; col <= 3; col++) { // Dx Age Sex
					Cell cell = sheet_Covariates.getCell(col, row);
					if (cell.getContents().trim().length() != 0)
						oriCovariates[row - 1][col - 1] = Double.valueOf(cell
								.getContents().trim());
				} // for col
			} // for row

			allCenter.put(currentCenterName, new CenterInfo(currentCenterName,
					numOfSub, oriSubIDList, oriData, oriCovariates));
		} // for centers
	}

	public static void main(String[] args) throws BiffException, IOException {
		if (args.length != 1) {
			System.out.println("Need input the directory of data...");
			System.exit(0);
		}
		ExcelReaderForGLM mainHandler = new ExcelReaderForGLM();
		mainHandler.homeDir = args[0].trim();
		mainHandler.loadingAllCenters();
		mainHandler.screenData();
		mainHandler.GlmFit();

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
