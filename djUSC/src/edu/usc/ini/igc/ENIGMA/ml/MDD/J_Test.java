package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public void dispalyClassificationPerformance_qsub() {
		double SENThreshold = 0.4;
		double SPEThreshold = 0.4;

		String category = "Over21";
		int featureNum = 25;
		List<String> subFolderList = new ArrayList<String>();
		
		//******************* Over21
		subFolderList.clear();
		subFolderList.add("J_SVMOutput_E");
		subFolderList.add("J_SVMOutput_EB");
		subFolderList.add("J_SVMOutput_EBG");
		subFolderList.add("J_SVMOutput_EBGI");
		subFolderList.add("J_SVMOutput_EBGIN");
		subFolderList.add("J_SVMOutput_EBGINO");
		subFolderList.add("J_SVMOutput_EBGINOC");
		subFolderList.add("J_SVMOutput_EBGINOCS");
		subFolderList.add("J_SVMOutput_EBGINOCSM");
		
		//******************* Recurrent
//		subFolderList.clear();
//		subFolderList.add("J_SVMOutput_E");
//		subFolderList.add("J_SVMOutput_EI");
//		subFolderList.add("J_SVMOutput_EIB");
//		subFolderList.add("J_SVMOutput_EIBC");
//		subFolderList.add("J_SVMOutput_EIBCO");
//		subFolderList.add("J_SVMOutput_EIBCOS");
//		subFolderList.add("J_SVMOutput_EIBCOSM");
		
		//******************* MF

		for (String subFolder : subFolderList) {
			double bestACC = 0.0;
			String bestLine = "";
			String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\SVMResult\\siteEffect\\onesite\\"+category+"\\feature"+featureNum+"\\"
					+ subFolder + "\\";
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
		} //for each sub folder
	}

	public void combineSingleSiteToOneFile() throws BiffException, IOException,
			RowsExceededException, WriteException {
		String category = "MF_MDD";
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\Complete\\Single_Sites_Only\\Single_Sites_"
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
					if (feaNum == 150)
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
					if (feaNum == 150)
						currentRow.add(2, siteName);
					allDataList.add(currentRow);
				} // for row
			} // if xls file
		} // for all files in this folder

		// Write to a new xls
		File exlFile = new File(dir + "Combine_" + category + ".xls");
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
	
	public int calWishNum(int n, int m)
	{
		int a = 1;
		int b = 1;
		for(int i=1;i<=n;i++)
		{
			a = (m-i+1)*a;
			b = b*i;
		}
		return a/b;
	}
	
	public List<Set<Integer>> getAllPossibilites(int gSize)
	{
		List<Set<Integer>> possibleList = new ArrayList<Set<Integer>>();
		for(int bagSize=1;bagSize<=gSize;bagSize++)
		{
			List<Set<Integer>> tmpList = new ArrayList<Set<Integer>>();
			int wishNum = this.calWishNum(bagSize, gSize);
			while(tmpList.size()<wishNum)
			{
				Set<Integer> tmpSet = new HashSet<Integer>();
				List numList = DicccolUtil.geneRandom(bagSize, gSize);
				tmpSet.addAll(numList);
				if(!tmpList.contains(tmpSet))
					tmpList.add(tmpSet);
			} //while
			possibleList.addAll(tmpList);
		} //for		
		return possibleList;
	}
	
	double calDifference(double numG0, double numG1)
	{
		return Math.abs(numG0-numG1)/(numG0+numG1);
	}
	
	public void test1() {
//		List<Set<Integer>> possibleList = this.getAllPossibilites(6);
//		for(Set<Integer> currentList:possibleList)
//		{
//			System.out.println(currentList);
//		}
		
		System.out.println(this.calDifference(45, 50));

	}

	public static void main(String[] args) throws BiffException, IOException,
			RowsExceededException, WriteException {
		J_Test mainHandler = new J_Test();
//		mainHandler.dispalyClassificationPerformance_qsub();
//		mainHandler.dispalyClassificationPerformance_singlesite();
		// mainHandler.displaySiteEffets();
		// mainHandler.combineSingleSiteToOneFile();
		// mainHandler.test();
//		System.out.println(mainHandler.calWishNum(6, 6));
		mainHandler.test1();

	}

}
