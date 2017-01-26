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

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.SimpleTTest;

public class ExcelReaderForGLM {

	// haha
	// jj
	final int FeatureNum_SurfAvg = 72;
	final int FeatureNum_ThickAvg = 68;
	final int FeatureNum_LRVolume = 14;// 16

	int FeatureNum_SurfAvg_screened = FeatureNum_SurfAvg;
	int FeatureNum_ThickAvg_screened = FeatureNum_ThickAvg;
	int FeatureNum_LRVolume_screened = FeatureNum_LRVolume;// 16
	int FeatureNum_Total = FeatureNum_SurfAvg + FeatureNum_ThickAvg
			+ FeatureNum_LRVolume;

	public String homeDir = "";
	public List<String> CenterList = new ArrayList<String>();
	public Map<String, CenterInfo> allCenter = new HashMap<String, CenterInfo>();
	public List<Integer> featureRemoveList = new ArrayList<Integer>();
	public String[] oriFeatureIDList = new String[FeatureNum_Total];
	String[] featureIDListAfterScreen = null;

	double[][] allY = null;
	double[][] allResiduals = null;
	List<String> allLabelAfterScreen = new ArrayList<String>();
	int numOfSubTotalLeft = 0;

	List<Integer> survivedTTestFeatures = new ArrayList<Integer>();

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
		System.out.println("FeatureNum_SurfAvg_screened:"
				+ FeatureNum_SurfAvg_screened
				+ "    FeatureNum_ThickAvg_screened:"
				+ FeatureNum_ThickAvg_screened
				+ "    FeatureNum_LRVolume_screened:"
				+ FeatureNum_LRVolume_screened);

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
							if (currentData[s][f].equals("NA")
									|| currentData[s][f].equals("x"))
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
			if (!featureRemoveList.contains(f) && bRemove) {
				featureRemoveList.add(f);
				if (f < FeatureNum_SurfAvg)
					FeatureNum_SurfAvg_screened--;
				else if (f < (FeatureNum_SurfAvg + FeatureNum_ThickAvg))
					FeatureNum_ThickAvg_screened--;
				else
					FeatureNum_LRVolume_screened--;
			} // if remove this feature
		} // for f
		featureIDListAfterScreen = new String[FeatureNum_Total
				- featureRemoveList.size()];

		// Remove subjects which have NAs
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			int numOfSub = Integer.valueOf(lineArray[1]);
			String[][] currentData = allCenter.get(currentCenterName).oriData;
			for (int f = 0; f < FeatureNum_Total; f++) {
				if (!featureRemoveList.contains(f)) {
					for (int s = 0; s < numOfSub; s++)
						if (currentData[s][f].equals("NA")
								|| currentData[s][f].equals("x"))
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
		List<String> outPutStatisticas = new ArrayList<String>();
		for (int c = 0; c < CenterList.size(); c++) {
			outPutStatisticas.clear();
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
					String outputLine = "";
					outputLine += allCenter.get(currentCenterName).oriSubIDList[s]
							.trim() + " ";
					outputLine += allCenter.get(currentCenterName).oriCovariates[s][1]
							+ " ";
					outputLine += allCenter.get(currentCenterName).oriCovariates[s][2]
							+ " ";
					outputLine += allCenter.get(currentCenterName).oriCovariates[s][0]
							+ " ";
					outPutStatisticas.add(outputLine);
					for (int f = 0; f < FeatureNum_Total; f++) {
						if (!featureRemoveList.contains(f)) {
							// dataAfterScreen[leftSubCount][leftFeatureCount] =
							// Double
							// .valueOf(allCenter.get(currentCenterName).oriData[s][f]);
							// control ICV
							double valueCorrectedICV = Double.valueOf(allCenter
									.get(currentCenterName).oriData[s][f]);
							 if(f<FeatureNum_SurfAvg) //if surfacearea
								 valueCorrectedICV /= allCenter.get(currentCenterName).ICV[s][0];
							 if(f >= (FeatureNum_SurfAvg+FeatureNum_ThickAvg) ) //if subcortical volume
								 valueCorrectedICV /= allCenter.get(currentCenterName).ICV[s][1];
							 dataAfterScreen[leftSubCount][leftFeatureCount] = valueCorrectedICV;

							featureIDListAfterScreen[leftFeatureCount] = oriFeatureIDList[f];
							leftFeatureCount++;
						} // if this feature was not removed
					} // for f
					allLabelAfterScreen.add(String.valueOf(allCenter
							.get(currentCenterName).oriCovariates[s][0]));
					labelAfterScreen[leftSubCount] = String.valueOf(allCenter
							.get(currentCenterName).oriCovariates[s][0]);
					covariatesAfterScreen[leftSubCount][0] = allCenter
							.get(currentCenterName).oriCovariates[s][1]; // Age
					covariatesAfterScreen[leftSubCount][1] = allCenter
							.get(currentCenterName).oriCovariates[s][2]; // Sex
					leftSubCount++;
				} // if this subject was not removed
			} // for s
			DicccolUtilIO.writeArrayListToFile(outPutStatisticas,
					currentCenterName + ".statistic.txt");
			allCenter.get(currentCenterName).dataAfterScreen = dataAfterScreen;
			allCenter.get(currentCenterName).covariatesAfterScreen = covariatesAfterScreen;
			allCenter.get(currentCenterName).labelAfterScreen = labelAfterScreen;
			DicccolUtilIO.writeArrayToFile(dataAfterScreen, numOfLeftSub,
					numOfLeftFeature, " ", "dataAfterScreen_"
							+ currentCenterName + "_ICV.txt");
		} // for c
	}

	public void statisticTest(double[][] data) {
		System.out.println("############## statisticTest ############");
		SimpleTTest tTest = new SimpleTTest();
		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		int numOfControl = 0;
		int numOfPatient = 0;
		for (int s = 0; s < numOfSubTotalLeft; s++)
			if (Double.valueOf(allLabelAfterScreen.get(s).trim()) == 0.0)
				numOfControl++;
			else
				numOfPatient++;

		System.out.println("NumOfControl: " + numOfControl
				+ "      NumOfPatient: " + numOfPatient);
		double[] controlData = new double[numOfControl];
		double[] patientData = new double[numOfPatient];

		for (int f = 0; f < numOfLeftFeature; f++) {
			int controlCount = 0;
			int patientCount = 0;
			for (int s = 0; s < numOfSubTotalLeft; s++)
				if (Double.valueOf(allLabelAfterScreen.get(s).trim()) == 0.0)
					controlData[controlCount++] = data[s][f];
				else
					patientData[patientCount++] = data[s][f];
			double pValue = tTest.tTest(controlData, patientData);
			if (pValue < 0.05)
				survivedTTestFeatures.add(f);
			System.out.println("Feature " + f + "("
					+ featureIDListAfterScreen[f] + "): " + pValue);
		} // for f

	}

	public void GlmFit() {
		System.out.println("############## GlmFit ############");

		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			numOfSubTotalLeft += allCenter.get(currentCenterName).numOfLeftSub;
		} // for c
		allY = new double[numOfSubTotalLeft][numOfLeftFeature];
		// double[][] allX = new double[numOfSubTotalLeft][2]; //if only regress
		// Age and Sex
		double[][] allX = new double[numOfSubTotalLeft][2 + CenterList.size()]; // regress
																				// Age,
																				// Sex
																				// and
																				// Site

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
				for (int site = 0; site < CenterList.size(); site++)
					if (site == c)
						allX[totalSubCount][2 + site] = 1.0;
					else
						allX[totalSubCount][2 + site] = 0.0;
				totalSubCount++;
			} // for s
		} // for c

		this.statisticTest(allY);

		// GLM fit
		allResiduals = new double[numOfSubTotalLeft][numOfLeftFeature];
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);

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
			// this.statisticTest(allResiduals);

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
				numOfLeftFeature, " ", "dataAfterGLM_All_ICV.txt");

		// Prepare the input data for distributed Lasso
		List<String> distributedLassoInputDataAll = new ArrayList<String>();
		List<String> distributedLassoInputDataAll_Part1 = new ArrayList<String>();
		List<String> distributedLassoInputDataAll_Part2 = new ArrayList<String>();
		List<String> distributedLassoInputDataAll_WithoutGLM = new ArrayList<String>();
		for (int c = 0; c < CenterList.size(); c++) {
			String[] lineArray = CenterList.get(c).trim().split("\\s+");
			String currentCenterName = lineArray[0];
			List<String> distributedLassoInputData = new ArrayList<String>();
			List<String> distributedLassoInputData_WithoutGLM = new ArrayList<String>();
			int numOfHalf = allCenter.get(currentCenterName).numOfLeftSub/2;
			List<Integer> randomHalf = DicccolUtil.geneRandom(numOfHalf, allCenter.get(currentCenterName).numOfLeftSub);
			DicccolUtilIO.writeIntegerListToFile(randomHalf, currentCenterName+"_RandomSubIDList.txt");
			for (int s = 0; s < allCenter.get(currentCenterName).numOfLeftSub; s++) {
				String currentSubData = "";
				String currentSubData_WithoutGLM = "";
				currentSubData += allCenter.get(currentCenterName).labelAfterScreen[s]
						+ ",";
				currentSubData_WithoutGLM += allCenter.get(currentCenterName).labelAfterScreen[s]
						+ ",";
				for (int f = 0; f < numOfLeftFeature; f++) {
					currentSubData += allCenter.get(currentCenterName).dataAfterGLM[s][f]
							+ " ";
					currentSubData_WithoutGLM += allCenter
							.get(currentCenterName).dataAfterScreen[s][f] + " ";
				}
				distributedLassoInputData.add(currentSubData);
				if(randomHalf.contains(s+1))
					distributedLassoInputDataAll_Part1.add(currentSubData);
				else
					distributedLassoInputDataAll_Part2.add(currentSubData);
				distributedLassoInputData_WithoutGLM
						.add(currentSubData_WithoutGLM);
			} // for s
			allCenter.get(currentCenterName).distributedLassoInputData = distributedLassoInputData;
			distributedLassoInputDataAll.addAll(distributedLassoInputData);
			distributedLassoInputDataAll_WithoutGLM
					.addAll(distributedLassoInputData_WithoutGLM);
			DicccolUtilIO.writeArrayListToFile(distributedLassoInputData,
					"distributedLassoInputData_" + currentCenterName + "_ICV.txt");
		} // for c
		DicccolUtilIO.writeArrayListToFile(distributedLassoInputDataAll,
				"distributedLassoInputDataAll_ICV.txt");
		DicccolUtilIO.writeArrayListToFile(distributedLassoInputDataAll_Part1,
				"distributedLassoInputDataAll_Part1_ICV.txt");
		DicccolUtilIO.writeArrayListToFile(distributedLassoInputDataAll_Part2,
				"distributedLassoInputDataAll_Part2_ICV.txt");
		DicccolUtilIO.writeArrayListToFile(
				distributedLassoInputDataAll_WithoutGLM,
				"distributedLassoInputDataAll_WithoutGLM_ICV.txt");

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
			double[][] ICV = new double[numOfSub][2]; // for surfacearea and
														// subcotrical volume
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
					// get ICV
				Cell cell = sheet_SurfAvg.getCell(FeatureNum_SurfAvg + 1, row);
				if (cell.getContents().trim().equals("NA")
						|| cell.getContents().trim().equals("x"))
					ICV[row - 1][0] = 1.0;
				else
					ICV[row - 1][0] = Double.valueOf(cell.getContents().trim());
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
				Cell cell = sheet_LRVolume
						.getCell(FeatureNum_LRVolume + 1, row);
				if (cell.getContents().trim().equals("NA")
						|| cell.getContents().trim().equals("x"))
					ICV[row - 1][1] = 1.0;
				else
					ICV[row - 1][1] = Double.valueOf(cell.getContents().trim());
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
					numOfSub, oriSubIDList, oriData, oriCovariates, ICV));
		} // for centers
	}

	public void generateSVMInput() {
		System.out.println("############## generateSVMInput ############");
		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		List<Integer> survivedLassoFeatures = new ArrayList<Integer>();
		String[] lineArray = DicccolUtilIO
				.loadFileToArrayList("DistributedLassoResult.txt").get(0)
				.split(",");
		if (lineArray.length != numOfLeftFeature) {
			System.out.println("lineArray.length!=numOfLeftFeature");
			System.exit(0);
		}
		for (int f = 0; f < numOfLeftFeature; f++)
			if (Math.abs(Double.valueOf(lineArray[f].trim())) > 1E-10)
				survivedLassoFeatures.add(f);
		System.out.println("SurvivedLassoFeatures ("
				+ survivedLassoFeatures.size() + "): " + survivedLassoFeatures);

		List<String> dataSvmList = new ArrayList<String>();
		if (allLabelAfterScreen.size() != numOfSubTotalLeft) {
			System.out.println("allLabelAfterScreen.size()!=numOfSubTotalLeft");
			System.exit(0);
		}
		for (int s = 0; s < numOfSubTotalLeft; s++) {
			String currentLine = "";
			currentLine += (Double.valueOf(allLabelAfterScreen.get(s).trim()))
					.intValue() + " ";
			int featureLassoCount = 1;
			for (int f = 0; f < numOfLeftFeature; f++) {
				if (survivedLassoFeatures.contains(f)) {
					// currentLine += featureLassoCount+":"+allResiduals[s][f] +
					// " ";
					currentLine += featureLassoCount + ":" + allY[s][f] + " ";
					featureLassoCount++;
				}// if this feature is survived from Lasso
			} // for f
			dataSvmList.add(currentLine);
		} // for s
		DicccolUtilIO.writeArrayListToFile(dataSvmList, "DataSvmList.txt");
	}

	public void generateWekaInput() {
		System.out.println("############## generateWekaInput ############");
		int numOfLeftFeature = FeatureNum_Total - featureRemoveList.size();
		List<Integer> survivedLassoFeatures = new ArrayList<Integer>();
		String[] lineArray = DicccolUtilIO
				.loadFileToArrayList("SelectedFeatureFromLassoFrequency_AND_ICV.txt")
				.get(0).split(",");
		if (lineArray.length != numOfLeftFeature) {
			System.out.println("lineArray.length!=numOfLeftFeature");
			System.exit(0);
		}
		for (int f = 0; f < numOfLeftFeature; f++)
//			if (Math.abs(Double.valueOf(lineArray[f].trim())) > 1E-10) // if
				// want to use the lasso features
				survivedLassoFeatures.add(f);
		System.out.println("SurvivedLassoFeatures ("
				+ survivedLassoFeatures.size() + "): " + survivedLassoFeatures);

		List<String> dataWekaList = new ArrayList<String>();
		if (allLabelAfterScreen.size() != numOfSubTotalLeft) {
			System.out.println("allLabelAfterScreen.size()!=numOfSubTotalLeft");
			System.exit(0);
		}

		dataWekaList.add("@RELATION  MDD");
		dataWekaList.add(" ");
		for (int f = 0; f < survivedLassoFeatures.size(); f++)
			dataWekaList.add("@ATTRIBUTE "
					+ featureIDListAfterScreen[survivedLassoFeatures.get(f)]
					+ " REAL");
		// for (int f = 0; f < survivedTTestFeatures.size(); f++)
		// dataWekaList.add("@ATTRIBUTE "
		// + featureIDListAfterScreen[survivedTTestFeatures.get(f)]
		// + " REAL");
		dataWekaList.add("@ATTRIBUTE class {0,1}");
		dataWekaList.add(" ");
		dataWekaList.add("@DATA");
		for (int s = 0; s < numOfSubTotalLeft; s++) {
			String currentLine = "";
			for (int f = 0; f < numOfLeftFeature; f++)
				if (survivedLassoFeatures.contains(f))
					// if (survivedTTestFeatures.contains(f))
					currentLine += allResiduals[s][f] + ",";
			// currentLine += allY[s][f] + ",";
			currentLine += (Double.valueOf(allLabelAfterScreen.get(s).trim()))
					.intValue();
			dataWekaList.add(currentLine);
		}

		DicccolUtilIO.writeArrayListToFile(dataWekaList,
				"DataWekaList_Lasso_AllFeature_ICV.arff");
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
		// //mainHandler.generateSVMInput();
//		mainHandler.generateWekaInput();

		// /////////////////////
		// mainHandler.test();

	}

}
