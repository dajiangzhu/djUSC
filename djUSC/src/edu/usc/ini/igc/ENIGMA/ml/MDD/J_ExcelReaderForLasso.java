package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class J_ExcelReaderForLasso {

	J_SiteDictionary siteDic = new J_SiteDictionary();

	public int numG0Total = 0;
	public int numG1Total = 0;

	public String homeDataDir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\";
	int subNumThre = 9;
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
			if (currentFeatueList_0 != null && currentFeatueList_0.size() != 0)
				for (double[] currentNormal : currentFeatueList_0)
					for (int i = 0; i < feaNum; i++)
						currentNormal[i] = (currentNormal[i] - featureMin[i])
								/ (featureMax[i] - featureMin[i]);
			if (currentFeatueList_1 != null && currentFeatueList_1.size() != 0)
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
		String sheetName = fileName.split("\\.")[0];
		if (sheetName.length() > 31)
			sheetName = sheetName.substring(0, 31);
		Sheet sheet_Current = w_Current.getSheet(sheetName);

		// find how many features
		int headNum = 3;
		feaNum = sheet_Current.getColumns() - headNum; // 3 for complete,
														// because
														// Brandy put sex on
														// imputed
														// data
		// read feature list
		for (int col = 0; col < feaNum; col++)
			featureList.add(sheet_Current.getCell(col + headNum, 0)
					.getContents().trim());

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
						.getCell(col + headNum, row).getContents().trim()); // 3
																			// for
				// complete,
				// because
				// Brandy
				// put
				// sex
				// on
				// imputed
				// data
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

	public void prepareForLasso(String siteConfig, String category,
			String excelFile) {
		System.out.println("#####################prepareForLasso...");

		String filePre = excelFile.split("_")[0].trim();
		String outPutDir = homeDataDir + "LassoInput\\" + category + "\\"
				+ filePre + "\\";
		List<String> siteConfigList = DicccolUtilIO
				.loadFileToArrayList(siteConfig);
		for (String line : siteConfigList) {
			String fileName = "J_LassoInput_" + filePre + "_";
			List<String> distributedLassoInput = new ArrayList<String>();
			String[] lineArray = line.split("\\s+")[0].split(";");
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
			DicccolUtilIO.writeArrayListToFile(distributedLassoInput, outPutDir
					+ fileName + ".txt");
		} // for line
		DicccolUtilIO.writeArrayListToFile(featureList, outPutDir
				+ "FeatureList_" + filePre + ".txt");
		System.out.println("#####################prepareForLasso finished!");
	}

	public void prepareForLassoAll(String category, String excelFile) {
		System.out.println("#####################prepareForLasso...");

		String filePre = excelFile.split("_")[0].trim();
		String outPutDir = homeDataDir + "LassoInput\\" + category + "\\"
				+ filePre + "\\";
		List<double[]> dataG0All = new ArrayList<double[]>();
		List<double[]> dataG1All = new ArrayList<double[]>();
		for (String siteName : siteSet) {
			System.out.println("-------------" + siteName);
			List<double[]> dataG0 = siteMap_G0.get(siteName);
			if (dataG0 != null)
				dataG0All.addAll(dataG0);
			List<double[]> dataG1 = siteMap_G1.get(siteName);
			if (dataG1 != null)
				dataG1All.addAll(dataG1);
		}
		System.out.println("G0: " + dataG0All.size());
		System.out.println("G1: " + dataG1All.size());

		List<double[]> dataLess;
		List<double[]> dataMore;
		String labelLess = "";
		String labelMore = "";
		int equalNum = 0;
		if (dataG0All.size() < dataG1All.size()) {
			dataLess = dataG0All;
			dataMore = dataG1All;
			labelLess = "0.0,";
			labelMore = "1.0,";
			equalNum = dataG0All.size();
		} else {
			dataLess = dataG1All;
			dataMore = dataG0All;
			labelLess = "1.0,";
			labelMore = "0.0,";
			equalNum = dataG1All.size();
		}

		int repeatNum = 1;
		for (int i = 0; i < repeatNum; i++) {
			List<String> distributedLassoInput = new ArrayList<String>();
			for (double[] currentdata : dataLess) {
				String currentLine = labelLess;
				for (int f = 0; f < feaNum; f++)
					currentLine = currentLine + currentdata[f] + " ";
				distributedLassoInput.add(currentLine);
			}

			List<Integer> listMore = DicccolUtil.geneRandom(equalNum,
					dataMore.size());
			for (int moreIndex : listMore) {
				double[] currentdata = dataMore.get(moreIndex - 1);
				String currentLine = labelMore;
				for (int f = 0; f < feaNum; f++)
					currentLine = currentLine + currentdata[f] + " ";
				distributedLassoInput.add(currentLine);
			} // for moreIndex
			DicccolUtilIO.writeArrayListToFile(distributedLassoInput, outPutDir
					+ "J_LassoInput_" + filePre + "_Random" + i + ".txt");
		} // for repeatNum

		DicccolUtilIO.writeArrayListToFile(featureList, outPutDir
				+ "FeatureList_" + filePre + ".txt");
		System.out.println("#####################prepareForLassoAll finished!");
	}

	public void prepareForLassoSeparateSite(String category, String excelFile) {
		System.out.println("#####################prepareForLasso...");

		String filePre = excelFile.split("_")[0].trim();
		String outPutDir = homeDataDir + "LassoInput\\" + category + "\\"
				+ filePre + "\\individualsite\\";
		List<double[]> dataG0All = new ArrayList<double[]>();
		List<double[]> dataG1All = new ArrayList<double[]>();
		for (String siteName : siteSet) {
			dataG0All.clear();
			dataG1All.clear();
			System.out.println("-------------" + siteName);
			List<double[]> dataG0 = siteMap_G0.get(siteName);
			List<double[]> dataG1 = siteMap_G1.get(siteName);

			if (dataG0 != null && dataG1 != null)
				if (dataG0.size() > subNumThre && dataG1.size() > subNumThre) {
					dataG0All.addAll(dataG0);
					dataG1All.addAll(dataG1);

					System.out.println("G0: " + dataG0All.size());
					System.out.println("G1: " + dataG1All.size());

					List<double[]> dataLess;
					List<double[]> dataMore;
					String labelLess = "";
					String labelMore = "";
					int equalNum = 0;
					if (dataG0All.size() < dataG1All.size()) {
						dataLess = dataG0All;
						dataMore = dataG1All;
						labelLess = "0.0,";
						labelMore = "1.0,";
						equalNum = dataG0All.size();
					} else {
						dataLess = dataG1All;
						dataMore = dataG0All;
						labelLess = "1.0,";
						labelMore = "0.0,";
						equalNum = dataG1All.size();
					}

					int repeatNum = 10;
					for (int i = 0; i < repeatNum; i++) {
						List<String> distributedLassoInput = new ArrayList<String>();
						for (double[] currentdata : dataLess) {
							String currentLine = labelLess;
							for (int f = 0; f < feaNum; f++)
								currentLine = currentLine + currentdata[f]
										+ " ";
							distributedLassoInput.add(currentLine);
						}

						List<Integer> listMore = DicccolUtil.geneRandom(
								equalNum, dataMore.size());
						for (int moreIndex : listMore) {
							double[] currentdata = dataMore.get(moreIndex - 1);
							String currentLine = labelMore;
							for (int f = 0; f < feaNum; f++)
								currentLine = currentLine + currentdata[f]
										+ " ";
							distributedLassoInput.add(currentLine);
						} // for moreIndex
						DicccolUtilIO.writeArrayListToFile(
								distributedLassoInput, outPutDir
										+ "J_LassoInput_" + filePre + "_"
										+ siteName + "_Random" + i + ".txt");
					}// for repeatNum
				} // if
		} // for each site

		DicccolUtilIO.writeArrayListToFile(featureList, outPutDir
				+ "FeatureList_" + filePre + ".txt");
		System.out.println("#####################prepareForLassoAll finished!");
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

	public int calWishNum(int n, int m) {
		int a = 1;
		int b = 1;
		for (int i = 1; i <= n; i++) {
			a = (m - i + 1) * a;
			b = b * i;
		}
		return a / b;
	}

	public List<Set<Integer>> getAllPossibilites(int gSize) {
		List<Set<Integer>> possibleList = new ArrayList<Set<Integer>>();
		for (int bagSize = 1; bagSize <= gSize; bagSize++) {
			List<Set<Integer>> tmpList = new ArrayList<Set<Integer>>();
			int wishNum = this.calWishNum(bagSize, gSize);
			while (tmpList.size() < wishNum) {
				Set<Integer> tmpSet = new HashSet<Integer>();
				List numList = DicccolUtil.geneRandom(bagSize, gSize);
				tmpSet.addAll(numList);
				if (!tmpList.contains(tmpSet))
					tmpList.add(tmpSet);
			} // while
			possibleList.addAll(tmpList);
		} // for
		return possibleList;
	}

	double calDifference(double numG0, double numG1) {
		return Math.abs(numG0 - numG1) / (numG0 + numG1);
	}

	public List<Integer> findNextSites(double diffThreshold,
			List<String> siteList, List<Integer> g0List, List<Integer> g1List) {
		// double diffThreshold = 0.1;
		if (siteList.size() == 1)
			System.out.println("Only one left!!!");

		List<Integer> indexList = new ArrayList<Integer>();
		List<Set<Integer>> possibleList = this.getAllPossibilites(siteList
				.size());
		for (Set<Integer> currentSet : possibleList) {
			int numG0 = this.numG0Total;
			int numG1 = this.numG1Total;
			System.out.println("trying: " + currentSet);
			for (int currentIndex : currentSet) {
				numG0 = numG0 + g0List.get(currentIndex - 1);
				numG1 = numG1 + g1List.get(currentIndex - 1);
			} // for
			if (this.calDifference(numG0, numG1) <= diffThreshold) {
				this.numG0Total = numG0;
				this.numG1Total = numG1;
				indexList.addAll(currentSet);
				break;
			}
		} // for
		return indexList;
	}

	public void printBalancedSiteSequence(
			List<List<String>> balancedSiteNameList,
			List<List<Integer>> balancedSiteG0List,
			List<List<Integer>> balancedSiteG1List, String outputFileName) {
		System.out.println("##########  printBalancedSiteSequence");

		String siteLine = "";
		List<Integer> g0List = new ArrayList<Integer>();
		List<Integer> g1List = new ArrayList<Integer>();
		double numG0 = 0.0;
		double numG1 = 0.0;
		List<String> outputList = new ArrayList<String>();

		for (int i = 0; i < balancedSiteNameList.size(); i++) {
			String siteG0G1Line = "(";
			for (String siteName : balancedSiteNameList.get(i))
				if (i == 0)
					siteLine = siteLine + siteName + ";";
				else
					siteLine = siteLine + ";" + siteName;
			if (i == 0)
				siteLine = siteLine.substring(0, siteLine.length() - 1);

			for (int tmpNumG0 : balancedSiteG0List.get(i)) {
				g0List.add(tmpNumG0);
				numG0 += (double) tmpNumG0;
			}
			for (int tmpNumG0 : g0List)
				siteG0G1Line = siteG0G1Line + tmpNumG0 + "+";
			siteG0G1Line = siteG0G1Line.substring(0, siteG0G1Line.length() - 1);
			siteG0G1Line += "/";

			for (int tmpNumG1 : balancedSiteG1List.get(i)) {
				g1List.add(tmpNumG1);
				numG1 += (double) tmpNumG1;
			}
			for (int tmpNumG1 : g1List)
				siteG0G1Line = siteG0G1Line + tmpNumG1 + "+";
			siteG0G1Line = siteG0G1Line.substring(0, siteG0G1Line.length() - 1);
			siteG0G1Line += ")";

			double g0 = numG0 / (numG0 + numG1);
			double g1 = numG1 / (numG0 + numG1);
			String fullLine = siteLine + " " + siteG0G1Line + " g0:" + g0
					+ " g1:" + g1;
			outputList.add(fullLine);
			System.out.println(fullLine);
		} // for
		DicccolUtilIO.writeArrayListToFile(outputList, outputFileName);
	}

	public void findBalancedSiteSequence(List<String> siteList,
			List<Integer> g0List, List<Integer> g1List, String outputFileName) {
		System.out.println("#####################findBalancedSiteSequence...");
		List<List<String>> balancedSiteNameList = new ArrayList<List<String>>();
		List<List<Integer>> balancedSiteG0List = new ArrayList<List<Integer>>();
		List<List<Integer>> balancedSiteG1List = new ArrayList<List<Integer>>();
		int count = siteList.size();
		do {
			double diffThreshold = 0.10;
			List<Integer> indexList = new ArrayList<Integer>();
			do {
				System.out.println("------tryint diffThreshold="
						+ diffThreshold + " ...");
				indexList = this.findNextSites(diffThreshold, siteList, g0List,
						g1List);
				diffThreshold += 0.01;
			} while (indexList.size() == 0);
			List<String> currentSiteNameList = new ArrayList<String>();
			List<Integer> currentSiteG0List = new ArrayList<Integer>();
			List<Integer> currentSiteG1List = new ArrayList<Integer>();
			for (int index : indexList) {
				currentSiteNameList.add(siteList.get(index - 1));
				currentSiteG0List.add(g0List.get(index - 1));
				currentSiteG1List.add(g1List.get(index - 1));
			}// for index
			balancedSiteNameList.add(currentSiteNameList);
			balancedSiteG0List.add(currentSiteG0List);
			balancedSiteG1List.add(currentSiteG1List);
			for (String tmpSiteName : currentSiteNameList)
				for (int i = 0; i < siteList.size(); i++)
					if (siteList.get(i).equals(tmpSiteName)) {
						siteList.remove(i);
						g0List.remove(i);
						g1List.remove(i);
						count--;
					} // if
		} while (count > 0);

		this.printBalancedSiteSequence(balancedSiteNameList,
				balancedSiteG0List, balancedSiteG1List, outputFileName);
		System.out
				.println("#####################findBalancedSiteSequence finished!");
	}

	public String printDataInfo(String category, String fileName) {

		List<Integer> g0List = new ArrayList<Integer>();
		List<Integer> g1List = new ArrayList<Integer>();
		List<String> siteList = new ArrayList<String>();

		int NumOfTotalG0 = 0;
		int NumOfTotalG1 = 0;
		System.out.println("#####################PrintingDataInfo...");
		System.out.println("#####File: " + category + "\\" + fileName);
		System.out.println("#####There are " + subNum + " subjects in total.");
		System.out.println("#####There are " + feaNum + " features in total.");
		System.out.println("#####There are " + siteSet.size()
				+ " Sites in total.");
		for (String site : siteSet) {
			System.out.println("-------------" + site);
			System.out.println("Group-0:");
			if (siteMap_G0.containsKey(site)) {
				System.out.println(siteMap_G0.get(site).size());
				NumOfTotalG0 += siteMap_G0.get(site).size();
				g0List.add(siteMap_G0.get(site).size());
			} else {
				System.out.println("0");
				g0List.add(0);
			}
			System.out.println("Group-1:");
			if (siteMap_G1.containsKey(site)) {
				System.out.println(siteMap_G1.get(site).size());
				NumOfTotalG1 += siteMap_G1.get(site).size();
				g1List.add(siteMap_G1.get(site).size());
			} else {
				System.out.println("0");
				g1List.add(0);
			}
			siteList.add(site);
		} //for site
		System.out.println("#####There are " + NumOfTotalG0 + " controls in total.");
		System.out.println("#####There are " + NumOfTotalG1 + " MDD in total.");
		
		
		System.out.println("#####Sites (Group-0>" + subNumThre
				+ " and Group-1>" + subNumThre + "):");
		String printSitesList = "";
		for (String site : siteSet)
			if (siteMap_G0.containsKey(site) && siteMap_G1.containsKey(site))
				if (siteMap_G0.get(site).size() > subNumThre
						&& siteMap_G1.get(site).size() > subNumThre)
					printSitesList += site + " ";
		System.out.println(printSitesList);
		for (String site : siteSet)
			if (siteMap_G0.containsKey(site) && siteMap_G1.containsKey(site))
				if (siteMap_G0.get(site).size() > subNumThre
						&& siteMap_G1.get(site).size() > subNumThre)
					System.out.println(site + " ("
							+ siteMap_G0.get(site).size() + "/"
							+ siteMap_G1.get(site).size() + ")");
		System.out.println("#####################PrintingDataInfo finished!");
		String siteSequenceFileName = fileName.substring(0,
				fileName.length() - 4)
				+ "_"
				+ category
				+ "_SiteSequence_All.txt";
		// this.findBalancedSiteSequence(siteList, g0List, g1List,
		// siteSequenceFileName);
		return siteSequenceFileName;
	}

	public void printSequenceLine(List<String> siteNameList) {
		String siteLine = "";
		List<Integer> g0List = new ArrayList<Integer>();
		List<Integer> g1List = new ArrayList<Integer>();
		double numG0 = 0.0;
		double numG1 = 0.0;
		List<String> outputList = new ArrayList<String>();

		String siteG0G1Line = "(";
		for (String siteName : siteNameList) {
			siteLine = siteLine + siteName + ";";
			int tmpNumG0 = siteMap_G0.get(siteName).size();
			g0List.add(tmpNumG0);
			numG0 += (double) tmpNumG0;

			int tmpNumG1 = siteMap_G1.get(siteName).size();
			g1List.add(tmpNumG1);
			numG1 += (double) tmpNumG1;
		} // for siteName
		siteLine = siteLine.substring(0, siteLine.length() - 1);

		for (int tmpNumG0 : g0List)
			siteG0G1Line = siteG0G1Line + tmpNumG0 + "+";
		siteG0G1Line = siteG0G1Line.substring(0, siteG0G1Line.length() - 1);
		siteG0G1Line += "/";

		for (int tmpNumG1 : g1List)
			siteG0G1Line = siteG0G1Line + tmpNumG1 + "+";
		siteG0G1Line = siteG0G1Line.substring(0, siteG0G1Line.length() - 1);
		siteG0G1Line += ")";

		double g0 = numG0 / (numG0 + numG1);
		double g1 = numG1 / (numG0 + numG1);
		String fullLine = siteLine + " " + siteG0G1Line + " g0:" + g0 + " g1:"
				+ g1;
		System.out.println(fullLine);
	}

	public String correceExcelFileName(String excelfileName) {
		excelfileName = excelfileName.replaceAll("_Dub", "_Dublin");
		excelfileName = excelfileName.replaceAll("_Hou", "_Houston");
		excelfileName = excelfileName.replaceAll("_Ber", "_Berlin");
		excelfileName = excelfileName.replaceAll("_Stan", "_Stanford");
		excelfileName = excelfileName.replaceAll("_Syd", "_Sydney");
		// excelfileName = excelfileName.replaceAll("_Ped", "_Pedro");
		excelfileName = excelfileName.replaceAll("_BRC", "_BRCDECC");
		excelfileName = excelfileName.replaceAll("_Muns", "_Munster");
		excelfileName = excelfileName.replaceAll("_Gron", "_Groningen");
		excelfileName = excelfileName.replaceAll("_Mel", "_Melbourne");
		return excelfileName;
	}

	public void prepareForLasso_BrandySequence(String category,
			String excelfileName) {
		excelfileName = this.correceExcelFileName(excelfileName);
		String[] siteNames = excelfileName.split("_");
		List<String> siteNameList = new ArrayList<String>();
		for (int i = 2; i < siteNames.length; i++)
			siteNameList.add(siteNames[i].trim());
		this.printSequenceLine(siteNameList);

		String filePre = excelfileName.split("_")[0].trim();
		String outPutDir = homeDataDir + "LassoInput\\" + category + "\\"
				+ filePre + "\\";
		String fileName = "J_LassoInput_" + filePre + "_";
		List<String> distributedLassoInput = new ArrayList<String>();
		for (String siteName : siteNameList) {
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
		DicccolUtilIO.writeArrayListToFile(distributedLassoInput, outPutDir
				+ fileName + ".txt");
		DicccolUtilIO.writeArrayListToFile(featureList, outPutDir
				+ "FeatureList_" + filePre + ".txt");

	}

	public static void main(String[] args) throws BiffException, IOException {
		// if (args.length == 2) {
		// String category = args[0].trim();
		// String excelfileName = args[1].trim();

		String category = "Imputed";
		String excelfileName = "NoAntiDep_Site_Age_Sex_ICV_MPIP_Imp";

		J_ExcelReaderForLasso mainHandler = new J_ExcelReaderForLasso();
		mainHandler.readExcel(category, excelfileName);
		String siteSequenceFileName = mainHandler.printDataInfo(category,
				excelfileName);
//		 mainHandler.prepareForLasso(siteSequenceFileName, category,
//		 excelfileName);
		 mainHandler.prepareForLassoAll(category, excelfileName);
		// mainHandler.prepareForLasso_BrandySequence(category, excelfileName);
//		 mainHandler.prepareForLassoSeparateSite(category, excelfileName);

		// mainHandler.prepareForWeka("J_PrepareLassoInput_Site_Imputed_Over21_20.txt");

		// } else
		// System.out
		// .println("Need Complete/Imputed MaleFemale/Over21/Recurrent/Under21");

	}

}
