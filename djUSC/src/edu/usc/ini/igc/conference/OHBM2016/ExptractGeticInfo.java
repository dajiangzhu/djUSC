package edu.usc.ini.igc.conference.OHBM2016;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uga.DICCCOL.DicccolUtilIO;

public class ExptractGeticInfo {

	public void test() throws IOException {
		FileInputStream fis = new FileInputStream(new File(
				"./COMPILED.SNPS.ISBI.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadsheet = workbook.getSheet("COMPILED.SNPS.ISBI");
		int col = 2494;
		int row = 0;
		String strFunctionalNetworkName = spreadsheet.getRow(row).getCell(col)
				.getStringCellValue();

		// File inputWorkbook = new File("./COMPILED.SNPS.ISBI.xlsx");
		// Workbook w = Workbook.getWorkbook(inputWorkbook);
		// Sheet sheet = w.getSheet("COMPILED.SNPS.ISBI");
		// int col = 2488;
		// int row = 0;
		// String strFunctionalNetworkName = sheet.getCell(col,
		// row).getContents();
		System.out.println(strFunctionalNetworkName);
	}

	public void extractSNPInfo() throws IOException {
		FileInputStream fis = new FileInputStream(new File(
				"./COMPILED.SNPS.ISBI.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadsheet = workbook.getSheet("COMPILED.SNPS.ISBI");
		int colNum = 2495;
		int rowNum = 1478;
		String currentCellContent = "";
		List<String> dataList = new ArrayList<String>();
		for (int i = 1; i < rowNum; i++) {
			System.out.println("Extracting the " + i + " th line...");
			String currentLine = "";
			currentCellContent = spreadsheet.getRow(i).getCell(0).getRawValue();
			currentLine += currentCellContent + " ";
			currentCellContent = spreadsheet.getRow(i).getCell(1)
					.getStringCellValue();
			currentLine += currentCellContent + " ";
			currentCellContent = spreadsheet.getRow(i).getCell(2).getRawValue();
			currentLine += currentCellContent + " ";
			currentCellContent = spreadsheet.getRow(i).getCell(5)
					.getStringCellValue();
			currentLine += currentCellContent + " ";
			for (int j = 7; j < colNum; j++) {
				currentCellContent = spreadsheet.getRow(i).getCell(j)
						.getStringCellValue().trim();
				currentCellContent = currentCellContent.replace("C", "G");
				currentCellContent = currentCellContent.replace("T", "A");
				if (currentCellContent.equals("G/G"))
					currentLine += "0 ";
				else if (currentCellContent.equals("G/A")
						|| currentCellContent.equals("A/G"))
					currentLine += "1 ";
				else if (currentCellContent.equals("A/A"))
					currentLine += "2 ";
				else
					System.out.println("Error when coding the SNP...."
							+ currentCellContent);
			} // for j
			dataList.add(currentLine);
		} // for i
		DicccolUtilIO.writeArrayListToFile(dataList,
				"COMPILED.SNPS.ISBI.info.txt");
	}

	public void generateGroupData() {
		int rowNum = 1477;
		int colNum = 2492;
		List<String> ADList = new ArrayList<String>();
		List<String> MCIList = new ArrayList<String>();
		List<String> CNList = new ArrayList<String>();
		String[][] allData = DicccolUtilIO.loadFileAsStringArray(
				"COMPILED.SNPS.ISBI.info.txt", rowNum, colNum);
		
		int matrixRow = 8;
		int matrixCol = 311;
		String strAD = "";
		String strMCI = "";
		String strCN = "";
		
		for (int i = 0; i < rowNum; i++) { //for each line
			String tmpLine = "";
			for (int j = 0; j < 4; j++)
				tmpLine += allData[i][j].trim() + " ";
			if (allData[i][3].trim().equals("AD"))
			{
				ADList.add(tmpLine);
				strAD += allData[i][0].trim() + " ";
			}
			if (allData[i][3].trim().equals("MCI"))
			{
				MCIList.add(tmpLine);
				strMCI += allData[i][0].trim() + " ";
			}
			if (allData[i][3].trim().equals("CN"))
			{
				CNList.add(tmpLine);
				strCN += allData[i][0].trim() + " ";
			}

			//fill into a matrix
			int[][] currentMatrix = new int[matrixRow][matrixCol];
			int count = 0;
			for (int j = 4; j < colNum; j++) {
				currentMatrix[count/matrixCol][count%matrixCol] = Integer.valueOf( allData[i][j] );
				count++;
			} // for j
			DicccolUtilIO.writeIntArrayToFile(currentMatrix, matrixRow, matrixCol, " ", "./"+allData[i][3].trim()+"/"+allData[i][0].trim()+"_"+allData[i][3].trim()+".txt");
		} // for i
		DicccolUtilIO.writeArrayListToFile(ADList, "AD_info.txt");
		DicccolUtilIO.writeArrayListToFile(MCIList, "MCI_info.txt");
		DicccolUtilIO.writeArrayListToFile(CNList, "CN_info.txt");
		System.out.println("AD: "+strAD);
		System.out.println("MCI: "+strMCI);
		System.out.println("CN: "+strCN);
		

	}

	public static void main(String[] args) throws IOException {
		ExptractGeticInfo mainHandler = new ExptractGeticInfo();
//		mainHandler.extractSNPInfo();
		mainHandler.generateGroupData();

	}

}
