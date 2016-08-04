package edu.usc.ini.igc.conference.OHBM2016;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uga.DICCCOL.DicccolUtilIO;

public class AnaDMatrix {

	public List<double[][]> allDMatrixList = new ArrayList<double[][]>();
	public List<double[][]> allAMatrixList = new ArrayList<double[][]>();
	public int DMatrixRowNum = 8;
	public int DMatrixColNum = 15;
	public int AMatrixRowNum = DMatrixColNum;
	public int AMatrixColNum = 311;
	public double rateThreshold = 0.91;
	public int NumOfCommonSNP = 10;
	public double[][] stdMatrix = new double[DMatrixRowNum][DMatrixColNum];

	public List<Double> orderedSTD = new ArrayList<Double>();
	public List<Integer> orderedSTD_Row = new ArrayList<Integer>();
	public List<Integer> orderedSTD_Col = new ArrayList<Integer>();

	public List<Double> orderedSNP = new ArrayList<Double>();
	public List<Integer> orderedSNP_Row = new ArrayList<Integer>();
	public List<Integer> orderedSNP_Col = new ArrayList<Integer>();

	public double percentile = 0.1;

	public void loadDMatrix(String groupName, String subGroup) {
		List<String> dataInfo = DicccolUtilIO.loadFileToArrayList("./"
				+ groupName + "_" + subGroup + "_info.txt");
		for (int i = 0; i < dataInfo.size(); i++) {
			String PID = dataInfo.get(i).split("\\s+")[0].trim();
			System.out.println("Loading ./" + groupName + "/DMatrix/" + PID
					+ "_" + groupName + "_D.txt");
			double[][] currentDMatrix = DicccolUtilIO.loadFileAsArray("./"
					+ groupName + "/DMatrix/" + PID + "_" + groupName
					+ "_D.txt", DMatrixRowNum, DMatrixColNum);
			allDMatrixList.add(currentDMatrix);
		} // for i
	}

	public void loadAMatrix(String groupName, String subGroup) {
		List<String> dataInfo = DicccolUtilIO.loadFileToArrayList("./"
				+ groupName + "_" + subGroup + "_info.txt");
		for (int i = 0; i < dataInfo.size(); i++) {
			String PID = dataInfo.get(i).split("\\s+")[0].trim();
			System.out.println("Loading ./" + groupName + "/DMatrix/" + PID
					+ "_" + groupName + "_A.txt");
			double[][] currentAMatrix = DicccolUtilIO.loadFileAsArray("./"
					+ groupName + "/DMatrix/" + PID + "_" + groupName
					+ "_A.txt", AMatrixRowNum, AMatrixColNum);
			allAMatrixList.add(currentAMatrix);
		} // for i
	}

	public void findCommonDicAtom(String groupName, String subGroup) {

		this.loadDMatrix(groupName, subGroup);

		// Calculate the STD matrix
		DescriptiveStatistics stats;
		for (int i = 0; i < DMatrixRowNum; i++)
			for (int j = 0; j < DMatrixColNum; j++) {
				stats = new DescriptiveStatistics();
				for (int m = 0; m < allDMatrixList.size(); m++)
					stats.addValue(allDMatrixList.get(m)[i][j]);
				stdMatrix[i][j] = stats.getStandardDeviation();
			} // for j
		DicccolUtilIO.writeArrayToFile(stdMatrix, DMatrixRowNum, DMatrixColNum,
				" ", groupName + "_" + subGroup + "_STD.txt");

		// order the STD matrix and save the corresponding element
		for (int n = 0; n < DMatrixRowNum * DMatrixColNum; n++) {
			double tmpMinStd = 1000;
			int tmpRow = -1;
			int tmpCol = -1;
			for (int i = 0; i < DMatrixRowNum; i++)
				for (int j = 0; j < DMatrixColNum; j++) {
					if (stdMatrix[i][j] < tmpMinStd) {
						tmpMinStd = stdMatrix[i][j];
						tmpRow = i;
						tmpCol = j;
					}
				} // for j
			orderedSTD.add(tmpMinStd);
			orderedSTD_Row.add(tmpRow);
			orderedSTD_Col.add(tmpCol);
			stdMatrix[tmpRow][tmpCol] = 1000;
		} // for n

		// output the required flag matrix
		int[][] flagMatrix = new int[DMatrixRowNum][DMatrixColNum];
		int requiredNum = (int) ((double) DMatrixRowNum
				* (double) DMatrixColNum * percentile);
		System.out.println("Percentile: " + percentile + "    NumOfEle: "
				+ requiredNum);
		for (int i = 0; i < requiredNum; i++)
			flagMatrix[orderedSTD_Row.get(i)][orderedSTD_Col.get(i)] = 1;
		DicccolUtilIO.writeIntArrayToFile(flagMatrix, DMatrixRowNum,
				DMatrixColNum, " ", groupName + "_" + subGroup + "_STD_"
						+ percentile + "_flag.txt");
	}

	public void findCommonSNP(String groupName, String subGroup)
			throws IOException {
		this.loadAMatrix(groupName, subGroup);
		int[][] flagMatrix = DicccolUtilIO.loadFileAsIntArray(groupName
				+ "_all_STD_" + percentile + "_flag.txt", DMatrixRowNum,
				DMatrixColNum);

		int[][] SNPCountMatrix = new int[DMatrixRowNum][AMatrixColNum];
		for (int s = 0; s < allAMatrixList.size(); s++) // for each subject
		{
			System.out.println("Calculating PID: " + s);
			// calculator A vector
			double[] AColSum = new double[AMatrixColNum];
			for (int j = 0; j < AMatrixColNum; j++)
				for (int i = 0; i < AMatrixRowNum; i++)
					AColSum[j] = AColSum[j]
							+ Math.abs(allAMatrixList.get(s)[i][j]);

			// calculate the contribution from the common dictionary atoms
			for (int i = 0; i < DMatrixRowNum; i++) {
				for (int j = 0; j < AMatrixColNum; j++) {
					double tmpRate = 0.0;
					for (int m = 0; m < DMatrixColNum; m++) {
						if (flagMatrix[i][m] != 0)
							tmpRate += Math.abs(allAMatrixList.get(s)[m][j]);
						// tmpRate /= AColSum[j];
						// if(tmpRate>=rateThreshold)
						// SNPCountMatrix[i][j]++;
					} // for m
					tmpRate /= AColSum[j];
					if (tmpRate >= rateThreshold)
						SNPCountMatrix[i][j]++;
				} // for j
			} // for i
		} // for s
		DicccolUtilIO.writeIntArrayToFile(SNPCountMatrix, DMatrixRowNum,
				AMatrixColNum, " ", groupName + "_" + subGroup
						+ "_SNPCount.txt");

		// ordering SNPCount matrix
		for (int n = 0; n < DMatrixRowNum * AMatrixColNum; n++) {
			double tmpMaxCount = -1;
			int tmpRow = -1;
			int tmpCol = -1;
			for (int i = 0; i < DMatrixRowNum; i++)
				for (int j = 0; j < AMatrixColNum; j++) {
					if (SNPCountMatrix[i][j] > tmpMaxCount) {
						tmpMaxCount = SNPCountMatrix[i][j];
						tmpRow = i;
						tmpCol = j;
					}
				} // for j
			orderedSNP.add(tmpMaxCount);
			orderedSNP_Row.add(tmpRow);
			orderedSNP_Col.add(tmpCol);
			SNPCountMatrix[tmpRow][tmpCol] = -1;
		} // for n

		// Print the topest SNPs
		List<String> SNPList = DicccolUtilIO
				.loadFileToArrayList("COMPILED.SNPS.ISBI.info.SNPList.txt");
		List<String> outpubSNPList = new ArrayList<String>();
		List<String> outpubSNPGeneList = new ArrayList<String>();

		FileInputStream fis = new FileInputStream(new File(
				"./COMPILED.SNPS.ISBI.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadsheet1 = workbook.getSheet("COMPILED.SNPS.ISBI");
		XSSFSheet spreadsheet2 = workbook.getSheet("GeneInfo");

		Map geneMap = new HashMap<String, Integer>();
		for (int i = 0; i < NumOfCommonSNP; i++) {
			System.out.println("***********  SNP-"+(i+1)+" ("+groupName+")  **********");
			double maxRate = orderedSNP.get(i);
			int SNPIndex = orderedSNP_Row.get(i) * AMatrixColNum
					+ orderedSNP_Col.get(i);
			String SNPName = SNPList.get(SNPIndex).split("_")[0].trim();
			String SNPGeneName = "NotFound";
			for (int j = 1; j < 2489; j++)
				if (spreadsheet2.getRow(j).getCell(0).getStringCellValue()
						.trim().equals(SNPName))
					SNPGeneName = spreadsheet2.getRow(j).getCell(4)
							.getStringCellValue().trim();
			System.out.println(SNPName + " - " + SNPGeneName);
			outpubSNPList.add(SNPName + " - " + SNPGeneName + " (" + maxRate
					+ ")");
			if(geneMap.containsKey(SNPGeneName))
			{
				int geneCount = (int)geneMap.get(SNPGeneName);
				geneCount++;
				geneMap.put(SNPGeneName, geneCount);
			}
			else
				geneMap.put(SNPGeneName, 1);

			//statistic of the current SNP
			Map variantMap = new HashMap<String, RiskVariants>();
			for (int j = 7; j < 2495; j++)
				if (spreadsheet1.getRow(0).getCell(j).getStringCellValue()
						.trim().equals(SNPName + "_combined"))
					for (int s = 1; s < 1478; s++) {
						String variant = spreadsheet1.getRow(s).getCell(j).getStringCellValue()
								.trim();
						int getAD =  (int)spreadsheet1.getRow(s).getCell(6).getNumericCellValue();
						if(variantMap.containsKey(variant))
							((RiskVariants)(variantMap.get(variant))).getAD[getAD]++;
						else
						{
							RiskVariants newRiskVariant = new RiskVariants();
							newRiskVariant.getAD[getAD]++;
							variantMap.put(variant, newRiskVariant);
						}
					} //for s
			List<String> variantList = new ArrayList<String>();
			variantList.addAll(variantMap.keySet());
			for(int v=0;v<variantList.size();v++)
			{
				String tmpV = variantList.get(v);
				RiskVariants tmpRiskVariants = (RiskVariants)variantMap.get(tmpV);
				System.out.println("variants: "+tmpV+"   NotGetAD:"+tmpRiskVariants.getAD[0]+"    GetAD:"+tmpRiskVariants.getAD[1]);
			}
		} //for each SNP
		DicccolUtilIO.writeArrayListToFile(outpubSNPList, groupName + "_"
				+ subGroup + "_CommonSNP_" + NumOfCommonSNP + ".txt");
		List<String> geneList = new ArrayList<String>();
		geneList.addAll(geneMap.keySet());
		System.out.println("Gene distribution:*************************************8");
		for(int v=0;v<geneList.size();v++)
			System.out.println(geneList.get(v)+": "+geneMap.get(geneList.get(v)));

		// extract the top SNPs info (gene and variants)

	}

	public static void main(String[] args) throws IOException {
		AnaDMatrix mainHandler = new AnaDMatrix();
		String groupName = "CN";
		String subGroup = "g2";
		mainHandler.findCommonDicAtom(groupName, subGroup);
		mainHandler.findCommonSNP(groupName, subGroup);

	}

}
