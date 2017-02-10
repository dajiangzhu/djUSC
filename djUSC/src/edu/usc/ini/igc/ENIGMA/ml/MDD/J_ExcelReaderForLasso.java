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
	int subNumThre = 20;
	public double lassoFrequencyThre = 100.0;

	public int feaNum = 0;
	public int subNum = 0;
	public Set<String> siteSet = new HashSet<String>();
	public List<String> featureList = new ArrayList<String>();
	public Map<String, List> siteMap_G0 = new HashMap<String, List>();
	public Map<String, List> siteMap_G1 = new HashMap<String, List>();
	double[] featureMin = null;
	double[] featureMax = null;

	public void normalizeFeatures() {
		System.out.println("#####################normalizeFeatures...");
			for (String site : siteSet) {
				System.out.println(site);
				List<double[]> currentFeatueList_0 = siteMap_G0.get(site);
				List<double[]> currentFeatueList_1 = siteMap_G1.get(site);
				if (currentFeatueList_0 != null
						&& currentFeatueList_0.size() != 0)
					for (double[] currentNormal : currentFeatueList_0)
						for (int i = 0; i < feaNum; i++)
							currentNormal[i] = (currentNormal[i] - featureMin[i])
									/ (featureMax[i] - featureMin[i]);
				if (currentFeatueList_1 != null
						&& currentFeatueList_1.size() != 0)
					for (double[] currentPatient : currentFeatueList_1)
						for (int i = 0; i < feaNum; i++)
							currentPatient[i] = (currentPatient[i] - featureMin[i])
									/ (featureMax[i] - featureMin[i]);
			} // for site
		System.out
				.println("#####################normalizeFeatures finished...");
	}

	public void readExcel(String category, String fileName)
			throws BiffException, IOException {

		String filePath = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\"
				+ category + "\\" + fileName + ".xls";
		System.out.println("#####################Reading file: " + filePath
				+ " ...");
		File excel_Current = new File(filePath);
		Workbook w_Current = Workbook.getWorkbook(excel_Current);
		Sheet sheet_Current = w_Current.getSheet(fileName.substring(0, 31));

		// find how many features
		feaNum = sheet_Current.getColumns() - 3;
		// read feature list
		for (int col = 0; col < feaNum; col++)
			featureList.add(sheet_Current.getCell(col + 3, 0).getContents()
					.trim());

		// find how many subjects and sites
		subNum = sheet_Current.getRows() - 1;
		String currentCell = null;
		featureMin = new double[feaNum];
		featureMax = new double[feaNum];
		for (int f = 0; f < feaNum; f++) {
			featureMin[f] = 10000;
			featureMax[f] = -10000;
		}
		for (int row = 1; row <= subNum; row++) {
			currentCell = sheet_Current.getCell(2, row).getContents().trim()
					.split("_")[0].trim();
			siteSet.add(currentCell);
			String group = sheet_Current.getCell(1, row).getContents().trim();
			Map<String, List> siteMap = null;
			if (group.equals("0"))
				siteMap = siteMap_G0;
			else
				siteMap = siteMap_G1;

			double[] currentFeatures = new double[feaNum];
			for (int col = 0; col < feaNum; col++) {
				double currentFeature = Double.valueOf(sheet_Current
						.getCell(col + 3, row).getContents().trim());
				if (currentFeature > featureMax[col])
					featureMax[col] = currentFeature;
				if (currentFeature < featureMin[col])
					featureMin[col] = currentFeature;
				currentFeatures[col] = currentFeature;
			}

			if (siteMap.containsKey(currentCell))
				siteMap.get(currentCell).add(currentFeatures);
			else {
				List<double[]> newList = new ArrayList<double[]>();
				newList.add(currentFeatures);
				siteMap.put(currentCell, newList);
			}
		} // for row
		this.normalizeFeatures();
		System.out.println("#####################Reading file: " + filePath
				+ " finished!");
	}

	public void prepareForLasso(String siteConfig) {
		System.out.println("#####################prepareForLasso...");

		List<String> siteConfigList = DicccolUtilIO
				.loadFileToArrayList(siteConfig);
		for (String line : siteConfigList) {
			String fileName = "J_LassoInput_";
			List<String> distributedLassoInput = new ArrayList<String>();
			String[] lineArray = line.split(";");
			for (int i = 0; i < lineArray.length; i++) {
				String siteName = lineArray[i].trim();
				fileName += siteDic.getCodeFromSite(siteName);
				List<double[]> dataG0 = siteMap_G0.get(siteName);
				if (dataG0 != null)
					for (double[] currentdata : dataG0) {
						String currentLine = "0.0,";
						for (int f = 0; f < feaNum; f++)
							currentLine = currentLine + currentdata[f] + " ";
						distributedLassoInput.add(currentLine);
					} // for
				List<double[]> dataG1 = siteMap_G1.get(siteName);
				if (dataG1 != null)
					for (double[] currentdata : dataG1) {
						String currentLine = "1.0,";
						for (int f = 0; f < feaNum; f++)
							currentLine = currentLine + currentdata[f] + " ";
						distributedLassoInput.add(currentLine);
					} // for
			} // for i
			DicccolUtilIO.writeArrayListToFile(distributedLassoInput, fileName
					+ ".txt");
		} // for line

		System.out.println("#####################prepareForLasso finished!");
	}

	public void prepareForWeka(String siteConfig) {
		System.out.println("#####################prepareForWeka...");
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

			System.out.println("-----Generating arff for " + fileName + "("
					+ currentSiteList + ")...");

			double[] lassoResult = new double[feaNum];
			for (int i = 1; i <= 100; i++) {
				// System.out.println("***********  i: " + i + " ************");
				List<String> currentLassoResultList = DicccolUtilIO
						.loadFileToArrayList(homeDataDir + "LassoResult\\"
								+ fileName + "\\" + fileName + ".txt_" + i
								+ "\\part-00000");

				if (currentLassoResultList.size() != feaNum) {
					System.out
							.println("currentLassoResultList.size()!=featureNum  i: "
									+ i);
					System.exit(0);
				}

				for (int f = 0; f < feaNum; f++)
					if (Math.abs(Double.valueOf(currentLassoResultList.get(f)
							.trim())) > 0.0)
						lassoResult[f]++;
			} // for i

			List<String> outList = new ArrayList<String>();
			for (int f = 0; f < feaNum; f++)
				outList.add(String.valueOf(lassoResult[f]));
			DicccolUtilIO.writeArrayListToFile(outList, homeDataDir
					+ "LassoResult\\" + fileName + "\\" + fileName
					+ "_LassoFeatureFrequency.txt");

			List<Integer> selectedFeatureList = new ArrayList<Integer>();
			for (int f = 0; f < feaNum; f++)
				if (lassoResult[f] >= lassoFrequencyThre)
					selectedFeatureList.add(f);

			List<String> dataWekaList = new ArrayList<String>();
			dataWekaList.add("@RELATION  MDD");
			dataWekaList.add(" ");
			for (int f = 0; f < selectedFeatureList.size(); f++)
				dataWekaList
						.add("@ATTRIBUTE "
								+ featureList.get(selectedFeatureList.get(f))
								+ " REAL");
			dataWekaList.add("@ATTRIBUTE class {0,1}");
			dataWekaList.add(" ");
			dataWekaList.add("@DATA");
			for (String currentSite : currentSiteList) {
				List<double[]> G0List = siteMap_G0.get(currentSite);
				for (double[] feaArray : G0List) {
					String currentLine = "";
					for (int selectedFeaIndex : selectedFeatureList)
						currentLine += feaArray[selectedFeaIndex] + ",";
					currentLine += "0";
					dataWekaList.add(currentLine);
				} // feaArray
				List<double[]> G1List = siteMap_G1.get(currentSite);
				for (double[] feaArray : G1List) {
					String currentLine = "";
					for (int selectedFeaIndex : selectedFeatureList)
						currentLine += feaArray[selectedFeaIndex] + ",";
					currentLine += "1";
					dataWekaList.add(currentLine);
				} // feaArray
			} // for currentSite
			DicccolUtilIO
					.writeArrayListToFile(dataWekaList, fileName + ".arff");

		} // for line
		System.out.println("#####################prepareForWeka finished!");
	}

	public void printDataInfo(String category, String fileName) {

		System.out.println("#####################PrintingDataInfo...");
		System.out.println("#####File: " + category + "\\" + fileName);
		System.out.println("#####There are " + subNum + " subjects in total.");
		System.out.println("#####There are " + feaNum + " features in total.");
		System.out.println("#####There are " + siteSet.size()
				+ " Sites in total.");
		for (String site : siteSet) {
			System.out.println("-------------" + site);
			System.out.println("Group-0:");
			if (siteMap_G0.containsKey(site))
				System.out.println(siteMap_G0.get(site).size());
			else
				System.out.println("0");
			System.out.println("Group-1:");
			if (siteMap_G1.containsKey(site))
				System.out.println(siteMap_G1.get(site).size());
			else
				System.out.println("0");
		}
		System.out.println("#####Sites (Group-0>" + subNumThre
				+ " and Group-1>" + subNumThre + "):");
		for (String site : siteSet)
			if (siteMap_G0.containsKey(site) && siteMap_G1.containsKey(site))
				if (siteMap_G0.get(site).size() > subNumThre
						&& siteMap_G1.get(site).size() > subNumThre)
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
//			mainHandler
//					.prepareForLasso("J_PrepareLassoInput_Site_Imputed_Over21_20.txt");
			 mainHandler.prepareForWeka("J_PrepareLassoInput_Site_Imputed_Over21_20.txt");

		} else
			System.out
					.println("Need Complete/Imputed MaleFemale/Over21/Recurrent/Under21");

	}

}
