package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;

public class J_Test {

	J_SiteDictionary siteDic = new J_SiteDictionary();

	public void displaySiteEffets() {
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResult\\siteEffect\\onesite\\";
		File folder = new File(dir);
		String[] files = folder.list();
		for (String file : files) {
			List<String> lineList = DicccolUtilIO.loadFileToArrayList(dir
					+ file);
			String[] results = lineList.get(lineList.size() - 2).split("\\s+");
			System.out.println(file + ": " + results[3] + " " + results[4]
					+ " " + results[5]);
		}
	}

	public void dispalyClassificationPerformance_singlesite() {
		double SENThreshold = 0.5;
		double SPEThreshold = 0.5;

		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResult\\siteEffect\\onesite\\MF\\";
		File folder = new File(dir);
		String[] files = folder.list();
		for (String file : files) {
			List<String> lineList = DicccolUtilIO.loadFileToArrayList(dir
					+ file);
			double bestACC = 0.0;
			String bestLine = "";
			for (String line : lineList)
				if (line.startsWith("SVMResult")) {
					String[] lineArray = line.split("\\s+");
					double tmpACC = Double.valueOf(lineArray[1].split("\\:")[1]
							.trim());
					double tmpSPE = Double.valueOf(lineArray[2].split("\\:")[1]
							.trim());
					double tmpSEN = Double.valueOf(lineArray[3].split("\\:")[1]
							.trim());
					if (tmpSPE >= SPEThreshold && tmpSEN > SENThreshold)
						if (tmpACC > bestACC) {
							bestACC = tmpACC;
							bestLine = line;
						} // if
				} // if
			System.out.println(file + ": " + bestLine);
		}

	}

	public void dispalyClassificationPerformance_qsub_singlesite() {
		double SENThreshold = 0.5;
		double SPEThreshold = SENThreshold;

		int featureNum = 25;
		String category = "Complete";
		List<String> subFolderList = new ArrayList<String>();
		// ********************************* Complete
		// ******************************************
		// ******************* Over21
		// String subgroup = "Over21";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_G");
		// subFolderList.add("J_SVMOutput_GC");
		// subFolderList.add("J_SVMOutput_GCP");
		// subFolderList.add("J_SVMOutput_GCPB");
		// subFolderList.add("J_SVMOutput_GCPBL");
		// subFolderList.add("J_SVMOutput_GCPBLO");
		// subFolderList.add("J_SVMOutput_GCPBLOI");
		// subFolderList.add("J_SVMOutput_GCPBLOIN");
		// subFolderList.add("J_SVMOutput_GCPBLOINS");
		// subFolderList.add("J_SVMOutput_GCPBLOINSM");
		// subFolderList.add("J_SVMOutput_GCPBLOINSME");

		// ******************* Recurrent
		// String subgroup = "Recurrent";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_C");
		// subFolderList.add("J_SVMOutput_CM");
		// subFolderList.add("J_SVMOutput_CMI");
		// subFolderList.add("J_SVMOutput_CMIO");
		// subFolderList.add("J_SVMOutput_CMIOL");
		// subFolderList.add("J_SVMOutput_CMIOLB");
		// subFolderList.add("J_SVMOutput_CMIOLBS");
		// subFolderList.add("J_SVMOutput_CMIOLBSP");
		// subFolderList.add("J_SVMOutput_CMIOLBSPE");

		// ******************* Males
		// String subgroup = "Males";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_G");
		// subFolderList.add("J_SVMOutput_GI");
		// subFolderList.add("J_SVMOutput_GIM");
		// subFolderList.add("J_SVMOutput_GIMB");
		// subFolderList.add("J_SVMOutput_GIMBNC");
		// subFolderList.add("J_SVMOutput_GIMBNCO");
		// subFolderList.add("J_SVMOutput_GIMBNCOP");
		// subFolderList.add("J_SVMOutput_GIMBNCOPS");
		// subFolderList.add("J_SVMOutput_GIMBNCOPSE");

		// ******************* Females
		String subgroup = "Females";
		subFolderList.clear();
		subFolderList.add("J_SVMOutput_G");
		subFolderList.add("J_SVMOutput_GI");
		subFolderList.add("J_SVMOutput_GIM");
		subFolderList.add("J_SVMOutput_GIMB");
		subFolderList.add("J_SVMOutput_GIMBNC");
		subFolderList.add("J_SVMOutput_GIMBNCO");
		subFolderList.add("J_SVMOutput_GIMBNCOP");
		subFolderList.add("J_SVMOutput_GIMBNCOPS");
		subFolderList.add("J_SVMOutput_GIMBNCOPSE");

		// ******************* MF
		// String subgroup = "MF";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_EN");
		// subFolderList.add("J_SVMOutput_ENG");
		// subFolderList.add("J_SVMOutput_ENGM");
		// subFolderList.add("J_SVMOutput_ENGMI");
		// subFolderList.add("J_SVMOutput_ENGMIC");
		// subFolderList.add("J_SVMOutput_ENGMICO");
		// subFolderList.add("J_SVMOutput_ENGMICOS");
		// subFolderList.add("J_SVMOutput_ENGMICOSB");

		// ********************************* Imputed
		// ******************************************
		// ******************* Over21
		// String subgroup = "Over21";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_G");
		// subFolderList.add("J_SVMOutput_GR");
		// subFolderList.add("J_SVMOutput_GRH");
		// subFolderList.add("J_SVMOutput_GRHL");
		// subFolderList.add("J_SVMOutput_GRHLK");
		// subFolderList.add("J_SVMOutput_GRHLKA");
		// subFolderList.add("J_SVMOutput_GRHLKAI");
		// subFolderList.add("J_SVMOutput_GRHLKAIC");
		// subFolderList.add("J_SVMOutput_GRHLKAICB");
		// subFolderList.add("J_SVMOutput_GRHLKAICBP");
		// subFolderList.add("J_SVMOutput_GRHLKAICBPM");
		// subFolderList.add("J_SVMOutput_GRHLKAICBPME");
		// subFolderList.add("J_SVMOutput_GRHLKAICBPMEO");
		// subFolderList.add("J_SVMOutput_GRHLKAICBPMEOS");
		// subFolderList.add("J_SVMOutput_GRHLKAICBPMEOSN");

		// // ******************* Recurrent
		// String subgroup = "Recurrent";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_C");
		// subFolderList.add("J_SVMOutput_CS");
		// subFolderList.add("J_SVMOutput_CSI");
		// subFolderList.add("J_SVMOutput_CSIH");
		// subFolderList.add("J_SVMOutput_CSIHO");
		// subFolderList.add("J_SVMOutput_CSIHOL");
		// subFolderList.add("J_SVMOutput_CSIHOLM");
		// subFolderList.add("J_SVMOutput_CSIHOLMB");
		// subFolderList.add("J_SVMOutput_CSIHOLMBK");
		// subFolderList.add("J_SVMOutput_CSIHOLMBKP");
		// subFolderList.add("J_SVMOutput_CSIHOLMBKPE");

		// ******************* Males
		// String subgroup = "Males";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_G");
		// subFolderList.add("J_SVMOutput_GI");
		// subFolderList.add("J_SVMOutput_GIB");
		// subFolderList.add("J_SVMOutput_GIBR");
		// subFolderList.add("J_SVMOutput_GIBRS");
		// subFolderList.add("J_SVMOutput_GIBRSP");
		// subFolderList.add("J_SVMOutput_GIBRSPC");
		// subFolderList.add("J_SVMOutput_GIBRSPCM");
		// subFolderList.add("J_SVMOutput_GIBRSPCMO");
		// subFolderList.add("J_SVMOutput_GIBRSPCMON");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONH");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONHL");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONHLE");

		// ******************* Females
		// String subgroup = "Females";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_G");
		// subFolderList.add("J_SVMOutput_GI");
		// subFolderList.add("J_SVMOutput_GIB");
		// subFolderList.add("J_SVMOutput_GIBR");
		// subFolderList.add("J_SVMOutput_GIBRS");
		// subFolderList.add("J_SVMOutput_GIBRSP");
		// subFolderList.add("J_SVMOutput_GIBRSPC");
		// subFolderList.add("J_SVMOutput_GIBRSPCM");
		// subFolderList.add("J_SVMOutput_GIBRSPCMO");
		// subFolderList.add("J_SVMOutput_GIBRSPCMON");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONH");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONHL");
		// subFolderList.add("J_SVMOutput_GIBRSPCMONHLE");

		// ******************* MF
		// String subgroup = "MF";
		// subFolderList.clear();
		// subFolderList.add("J_SVMOutput_E");
		// subFolderList.add("J_SVMOutput_EK");
		// subFolderList.add("J_SVMOutput_EKM");
		// subFolderList.add("J_SVMOutput_EKMR");
		// subFolderList.add("J_SVMOutput_EKMRL");
		// subFolderList.add("J_SVMOutput_EKMRLG");
		// subFolderList.add("J_SVMOutput_EKMRLGH");
		// subFolderList.add("J_SVMOutput_EKMRLGHN");
		// subFolderList.add("J_SVMOutput_EKMRLGHNP");
		// subFolderList.add("J_SVMOutput_EKMRLGHNPB");
		// subFolderList.add("J_SVMOutput_EKMRLGHNPBI");
		// subFolderList.add("J_SVMOutput_EKMRLGHNPBIS");
		// subFolderList.add("J_SVMOutput_EKMRLGHNPBISC");
		// subFolderList.add("J_SVMOutput_EKMRLGHNPBISCO");

		// List<String> balanceInfo = DicccolUtilIO
		// .loadFileToArrayList("E:\\GITWorkSpace\\djUSC\\" + subgroup
		// + "_Combine_Single_" + category
		// + "_SiteSequence_0.06.txt");
		List<String> balanceInfo = DicccolUtilIO
				.loadFileToArrayList("E:\\GITWorkSpace\\djUSC\\" + subgroup
						+ "_Over21_" + category + "_SiteSequence.txt");
		Map<String, String> siteBalanceInfo = new HashMap<String, String>();
		Map<String, Integer> siteSubNumInfo = new HashMap<String, Integer>();
		for (String line : balanceInfo) {
			String[] lineArray = line.split("\\s+");
			String[] nameArray = lineArray[0].trim().split(";");
			String siteCodeLine = "";
			for (String name : nameArray)
				siteCodeLine += siteDic.getCodeFromSite(name);
			String balanceData = lineArray[1].trim().substring(1,
					lineArray[1].trim().length() - 1);
			String normalPart = balanceData.split("/")[0];
			String mddPart = balanceData.split("/")[1];
			int normalTotal = 0;
			int mddTotal = 0;

			if (normalPart.contains("+")) {
				String[] normalPartArray;
				String[] mddPartArray;
				normalPartArray = normalPart.split("\\+");
				mddPartArray = mddPart.split("\\+");
				for (String normalCount : normalPartArray)
					normalTotal += Integer.valueOf(normalCount);
				for (String mddCount : mddPartArray)
					mddTotal += Integer.valueOf(mddCount);
			} else {
				normalTotal = Integer.valueOf(normalPart);
				mddTotal = Integer.valueOf(mddPart);
			}

			siteBalanceInfo.put(siteCodeLine, "(" + normalTotal + "/"
					+ mddTotal + ")");
			siteSubNumInfo.put(siteCodeLine, (normalTotal + mddTotal));
		}

		List<String> outputFig2 = new ArrayList<String>();
		String siteNumLine = "";
		String siteSubBalanceLine = "";
		String siteSubNumLine = "";
		String ACCLine = "";
		String SPELine = "";
		String SENLine = "";
		for (String subFolder : subFolderList) {
			double bestACC = 0.0;
			String bestLine = "";
			String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResultNew\\"
					+ "\\SingleSite\\"
					+ category
					+ "\\"
					+ subgroup
					+ "\\feature" + featureNum + "\\" + subFolder + "\\";
			String codeOfSubFolder = subFolder.split("_")[2].trim();
			// siteNumLine += codeOfSubFolder.length()+" ";
			File folder = new File(dir);
			String[] files = folder.list();
			for (String file : files) {
				List<String> lineList = DicccolUtilIO.loadFileToArrayList(dir
						+ file);
				for (String line : lineList)
					if (line.startsWith("SVMResult")) {
						String[] lineArray = line.split("\\s+");
						double tmpACC = Double.valueOf(lineArray[1]
								.split("\\:")[1].trim());
						double tmpSPE = Double.valueOf(lineArray[2]
								.split("\\:")[1].trim());
						double tmpSEN = Double.valueOf(lineArray[3]
								.split("\\:")[1].trim());
						if (tmpSPE >= SPEThreshold && tmpSEN > SENThreshold)
							if (tmpACC > bestACC) {
								bestACC = tmpACC;
								bestLine = line;
							} // if
					} // if
			} // for each file
			System.out.println(subFolder + ": " + bestLine);
			if (bestACC > 0.01) {
				ACCLine += bestACC + " ";
				String[] tmpA = bestLine.split("\\s+");
				SPELine += tmpA[2].split(":")[1].trim() + " ";
				SENLine += tmpA[3].split(":")[1].trim() + " ";
				siteSubNumLine += siteSubNumInfo.get(codeOfSubFolder) + " ";
				siteSubBalanceLine += siteBalanceInfo.get(codeOfSubFolder)
						+ " ";
				siteNumLine += codeOfSubFolder.length() + " ";
			}
		} // for each sub folder

		outputFig2.add(siteNumLine);
		outputFig2.add(ACCLine);
		outputFig2.add(SENLine);
		outputFig2.add(SPELine);
		outputFig2.add(siteSubNumLine);
		outputFig2.add(siteSubBalanceLine);

		DicccolUtilIO
				.writeArrayListToFile(
						outputFig2,
						"E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResultNew\\SingleSite\\"
								+ category
								+ "\\"
								+ subgroup
								+ "\\feature"
								+ featureNum + "\\Fig2.txt");
	}

	public void dispalyClassificationPerformance_qsub_seperatesites() {
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\";
		Map<String, List<String>> siteInfoMap = new HashMap<String, List<String>>();
		List<String> siteInfoFile = DicccolUtilIO.loadFileToArrayList(dir
				+ "DataInfoForSingleSite.txt");
		String currentKey = "";
		List<String> currentSiteList = new ArrayList<String>();
		for (String currentLine : siteInfoFile) {
			if (currentLine.trim().length() != 0)
				if (currentLine.startsWith("***")) {
					if (currentKey.length() != 0)
						siteInfoMap.put(currentKey, currentSiteList);
					currentKey = currentLine.replace('*', ' ').trim();
					currentSiteList = new ArrayList<String>();
				} else
					currentSiteList.add(currentLine);
		} // for
		siteInfoMap.put(currentKey, currentSiteList);

		String category = "";
		String subgroup = "";
		String siteName = "";
		String siteSubInfo = "";
		double SENThreshold = 0.5;
		double SPEThreshold = SENThreshold;
		int featureNum = 25;

		for (String key : siteInfoMap.keySet()) {
			System.out.println("******************************" + key + "******************************");
			int ControlCount = 0;
			int PatientCount = 0;
			String[] keyArray = key.split(":");
			category = keyArray[1].trim();
			subgroup = keyArray[0].trim();
			DescriptiveStatistics stats_ACC = new DescriptiveStatistics();
			DescriptiveStatistics stats_SPE = new DescriptiveStatistics();
			DescriptiveStatistics stats_SEN = new DescriptiveStatistics();
			for (String siteinfo : siteInfoMap.get(key)) {
				System.out.println(siteinfo);
				String[] siteinfoArray = siteinfo.split("\\s+");
				siteName = siteinfoArray[0].trim();
				siteSubInfo = siteinfoArray[1].trim();
				siteSubInfo = siteSubInfo.substring(1, siteSubInfo.length()-1);
				String[] siteSubInfoArray = siteSubInfo.replace('/', ' ').split("\\s+");
				ControlCount += Integer.valueOf(siteSubInfoArray[0]);
				PatientCount += Integer.valueOf(siteSubInfoArray[1]);

				List<String> subFolderList = new ArrayList<String>();
				for (int i = 0; i < 10; i++)
					subFolderList.add("J_SVMOutput_Random" + i);

				double globalBestACC = 0.0;
				double globalBestSPE = 0.0;
				double globalBestSEN = 0.0;
				for (String subFolder : subFolderList) {
					double currentBestACC = 0.0;
					double currentBestSPE = 0.0;
					double currentBestSEN = 0.0;
					String currentBestLine = "";
					String currentDir = dir + "SVMResultNew\\" + category
							+ "\\" + subgroup + "\\" + siteName + "\\feature"
							+ featureNum + "\\" + subFolder + "\\";
					File folder = new File(currentDir);
					String[] files = folder.list();
					for (String file : files) {
						List<String> lineList = DicccolUtilIO
								.loadFileToArrayList(currentDir + file);
						for (String line : lineList)
							if (line.startsWith("SVMResult")) {
								String[] lineArray = line.split("\\s+");
								double tmpACC = Double.valueOf(lineArray[1]
										.split("\\:")[1].trim());
								double tmpSPE = Double.valueOf(lineArray[2]
										.split("\\:")[1].trim());
								double tmpSEN = Double.valueOf(lineArray[3]
										.split("\\:")[1].trim());
								if (tmpSPE >= SPEThreshold
										&& tmpSEN > SENThreshold)
									if (tmpACC > currentBestACC) {
										currentBestACC = tmpACC;
										currentBestSPE = tmpSPE;
										currentBestSEN = tmpSEN;
										currentBestLine = line;
									} // if
							} // if
					} // for each file
					// System.out.println(subFolder + ":    ACC:" +
					// currentBestACC
					// + "   SPE:" + currentBestSPE + "   SEN:" +
					// currentBestSEN);
					globalBestACC += currentBestACC;
					globalBestSPE += currentBestSPE;
					globalBestSEN += currentBestSEN;
				} // for each sub folder

				globalBestACC /= 10.0;
				globalBestSPE /= 10.0;
				globalBestSEN /= 10.0;
				
				stats_ACC.addValue(globalBestACC);
				stats_SPE.addValue(globalBestSPE);
				stats_SEN.addValue(globalBestSEN);

				System.out.println(category + " -> " + subgroup + " -> "
						+ siteName + " -> ACC:" + globalBestACC + "   SPE:"
						+ globalBestSPE + "   SEN:" + globalBestSEN);

			} // for each site

			System.out.println(">>>>>>>>>>>>>>>"+category + " -> " + subgroup + " -> "
					+ " -> ACC_Mean:" + stats_ACC.getMean() + "   ACC_STD:"
					+ stats_ACC.getStandardDeviation() );
			System.out.println(">>>>>>>>>>>>>>>"+category + " -> " + subgroup + " -> "
					+ " -> SPE_Mean:" + stats_SPE.getMean() + "   SPE_STD:"
					+ stats_SPE.getStandardDeviation() );
			System.out.println(">>>>>>>>>>>>>>>"+category + " -> " + subgroup + " -> "
					+ " -> SEN_Mean:" + stats_SEN.getMean() + "   SEN_STD:"
					+ stats_SEN.getStandardDeviation() );
			System.out.println(">>>>>>>>>>>>>>>"+"Total Subjects: "+(ControlCount+PatientCount)+"("+ControlCount+"/"+PatientCount+")");
			
		} // for each category+subgroup
	}

	public void dispalyClassificationPerformance_qsub_random() {
		double SENThreshold = 0.5;
		double SPEThreshold = SENThreshold;

		int featureNum = 25;
		String category = "Imputed";
		String subgroup = "Epi3";
		int randomNum = 1;
		List<String> subFolderList = new ArrayList<String>();
		for (int i = 0; i < randomNum; i++)
			subFolderList.add("J_SVMOutput_Random" + i);

		double globalBestACC = 0.0;
		double globalBestSPE = 0.0;
		double globalBestSEN = 0.0;
		for (String subFolder : subFolderList) {
			double currentBestACC = 0.0;
			double currentBestSPE = 0.0;
			double currentBestSEN = 0.0;
			String currentBestLine = "";
			String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResult\\"
					+ category
					+ "\\"
					+ subgroup
					+ "\\feature"
					+ featureNum
					+ "\\" + subFolder + "\\";
			File folder = new File(dir);
			String[] files = folder.list();
			for (String file : files) {
				List<String> lineList = DicccolUtilIO.loadFileToArrayList(dir
						+ file);
				for (String line : lineList)
					if (line.startsWith("SVMResult")) {
						String[] lineArray = line.split("\\s+");
						double tmpACC = Double.valueOf(lineArray[1]
								.split("\\:")[1].trim());
						double tmpSPE = Double.valueOf(lineArray[2]
								.split("\\:")[1].trim());
						double tmpSEN = Double.valueOf(lineArray[3]
								.split("\\:")[1].trim());
						if (tmpSPE >= SPEThreshold && tmpSEN > SENThreshold)
							if (tmpACC > currentBestACC) {
								currentBestACC = tmpACC;
								currentBestSPE = tmpSPE;
								currentBestSEN = tmpSEN;
								currentBestLine = line;
							} // if
					} // if
			} // for each file
			System.out.println(subFolder + ":    ACC:" + currentBestACC
					+ "   SPE:" + currentBestSPE + "   SEN:" + currentBestSEN);
			globalBestACC += currentBestACC;
			globalBestSPE += currentBestSPE;
			globalBestSEN += currentBestSEN;
		} // for each sub folder

		globalBestACC /= (float)randomNum;
		globalBestSPE /= (float)randomNum;
		globalBestSEN /= (float)randomNum;

		System.out.println(category + " -> " + subgroup + " -> " + "ACC:"
				+ globalBestACC + "   SPE:" + globalBestSPE + "   SEN:"
				+ globalBestSEN);

	}

	public void combineSingleSiteToOneFile() throws BiffException, IOException,
			RowsExceededException, WriteException {
		String category = "MF";
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\Imputed\\Single_Sites_Only\\Single_Sites_"
				+ category + "\\";
		File folder = new File(dir);
		String[] files = folder.list();
		List<List<String>> allDataList = new ArrayList<List<String>>();
		boolean hasReadHead = false;
		for (String file : files) {
			if (file.endsWith("xls")) {
				String filePath = dir + "\\" + file;
				System.out.println("#####################Reading file: " + file
						+ " ...");
				File excel_Current = new File(filePath);
				Workbook w_Current = Workbook.getWorkbook(excel_Current);
				String sheetName = file.split("\\.")[0];
				if (sheetName.length() > 31)
					sheetName = sheetName.substring(0, 31);
				System.out.println("sheetName: " + sheetName);
				String siteName = sheetName.split("_")[2].trim();
				siteName = siteName.replaceAll("Only", " ").trim();
				System.out.println("siteName: " + siteName);
				Sheet sheet_Current = w_Current.getSheet(sheetName);
				int feaNum = sheet_Current.getColumns();
				System.out.println("colNum: " + feaNum);
				int subNum = sheet_Current.getRows() - 1;
				System.out.println("subNum: " + subNum);
				if (!hasReadHead) {
					List<String> headList = new ArrayList<String>();
					for (int col = 0; col < feaNum; col++)
						headList.add(sheet_Current.getCell(col, 0)
								.getContents().trim());
					if (feaNum == 154) // 150 for complete(Over21,Recurrent) 155
										// for imputed(Over21,Recurrent)
						headList.add(2, "Site");
					allDataList.add(headList);
					hasReadHead = true;
				} // if !hasReadHead

				for (int row = 1; row <= subNum; row++) {
					List<String> currentRow = new ArrayList<String>();
					for (int col = 0; col < feaNum; col++) {
						currentRow.add(sheet_Current.getCell(col, row)
								.getContents().trim());
					} // for col
					if (feaNum == 154)
						currentRow.add(2, siteName);
					allDataList.add(currentRow);
				} // for row
			} // if xls file
		} // for all files in this folder

		// Write to a new xls (Over21_Combine_SingleSite)
		File exlFile = new File(dir + category + "Combine_SingleSite" + ".xls");
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(exlFile);
		WritableSheet writableSheet = writableWorkbook.createSheet("Combine_"
				+ category, 0);
		for (int row = 0; row < allDataList.size(); row++) {
			List<String> currentRow = allDataList.get(row);
			for (int col = 0; col < currentRow.size(); col++) {
				Label label = new Label(col, row, currentRow.get(col));
				writableSheet.addCell(label);
			} // for col
		} // for row
		writableWorkbook.write();
		writableWorkbook.close();
	}

	public void test() {
		List<String> testList = new ArrayList<String>();
		testList.add("a");
		testList.add("b");
		testList.add("c");
		testList.add("d");
		System.out.println(testList);
		testList.add(1, "haha");
		System.out.println(testList);
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

	public void test1() {
		// List<Set<Integer>> possibleList = this.getAllPossibilites(6);
		// for(Set<Integer> currentList:possibleList)
		// {
		// System.out.println(currentList);
		// }

		// System.out.println(this.calDifference(45, 50));

		Map<String, Integer> featureCountMap = new HashMap<String, Integer>();
		featureCountMap.put("aaa", 1);
		System.out.println(featureCountMap.toString());
		featureCountMap.put("bbb", 10);
		System.out.println(featureCountMap.toString());
		featureCountMap.put("aaa", 8);
		System.out.println(featureCountMap.toString());
	}

	public void generateRandomBrainMeasures() {
		for (int i = 0; i < 20; i++) {
			List<Integer> numList = DicccolUtil.geneRandom(25, 152);
			List<String> outputList = new ArrayList<String>();
			for (int n : numList) {
				outputList.add("BM" + n + " 0 2");
				outputList.add("BM" + n + " 0 2");
			} // for n
			DicccolUtilIO.writeArrayListToFile(outputList,
					"D:\\circos\\Test\\test1_highlight_list" + i + ".txt");
		}
	}

	public void calFeatureFrequency() {
		int featureNum = 25;
		String category = "Complete";
		String subgroup = "Males";
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\LassoInput\\"
				+ category + "\\" + subgroup + "\\";
		Map<String, Integer> featureCountMap = new HashMap<String, Integer>();
		for (int i = 0; i < 100; i++) {
			List<String> linesInFile = DicccolUtilIO.loadFileToArrayList(dir
					+ "J_LassoInput_" + subgroup + "_Random" + i + "_Feature"
					+ featureNum + ".arff");
			for (String currentLine : linesInFile) {
				if (currentLine.startsWith("@ATTRIBUTE")) {
					String tmpFeatureName = currentLine.split("\\s+")[1].trim();
					if (featureCountMap.containsKey(tmpFeatureName))
						featureCountMap.put(tmpFeatureName,
								featureCountMap.get(tmpFeatureName) + 1);
					else
						featureCountMap.put(tmpFeatureName, 1);
				}// if
			}// for
		}// for i

		List<String> outFrequency = new ArrayList<String>();
		for (String featureName : featureCountMap.keySet())
			outFrequency.add(featureName + " "
					+ featureCountMap.get(featureName));
		DicccolUtilIO.writeArrayListToFile(outFrequency, dir + "featureQuency_"
				+ category + "_" + subgroup + ".txt");
	}

	public static void main(String[] args) throws BiffException, IOException,
			RowsExceededException, WriteException {
		J_Test mainHandler = new J_Test();
		 mainHandler.dispalyClassificationPerformance_qsub_random();
		// mainHandler.dispalyClassificationPerformance_qsub_singlesite();
//		mainHandler.dispalyClassificationPerformance_qsub_seperatesites();
		// mainHandler.dispalyClassificationPerformance_singlesite();
		// mainHandler.displaySiteEffets();
		// mainHandler.combineSingleSiteToOneFile();
		// mainHandler.test();
		// System.out.println(mainHandler.calWishNum(6, 6));
		// mainHandler.test1();
		// mainHandler.generateRandomBrainMeasures();

		// mainHandler.calFeatureFrequency();

	}

}
