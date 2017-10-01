package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.DICCCOL.DicccolUtilIO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class J_circos {

	public void generateROI() throws BiffException, IOException {
		List<String> featureList = new ArrayList<String>();
		String fileName = "Over21_Site_Age_Sex_ICV_MPIP_Imp.xls";
		String filePath = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\Imputed\\"
				+ fileName;
		System.out.println("#####################Reading file: " + filePath
				+ " ...");
		File excel_Current = new File(filePath);
		Workbook w_Current = Workbook.getWorkbook(excel_Current);
		String sheetName = fileName.split("\\.")[0];
		if (sheetName.length() > 31)
			sheetName = sheetName.substring(0, 31);
		Sheet sheet_Current = w_Current.getSheet(sheetName);

		// find how many features
		int feaNum = sheet_Current.getColumns() - 3;
		// read feature list
		for (int col = 0; col < feaNum; col++)
			featureList.add(sheet_Current.getCell(col + 3, 0).getContents()
					.trim());

		List<String> ROIList = new ArrayList<String>();
		List<String> orderList = new ArrayList<String>();
		orderList.add("SA");
		orderList.add("VL");
		orderList.add("Thk");
		Map BM_Color = new HashMap<String, String>();
//		BM_Color.put("SA", "dyellow");
//		BM_Color.put("VL", "dj_SkyBlue");
//		BM_Color.put("Thk", "lpred");
		BM_Color.put("SA", "vdgrey");
		BM_Color.put("VL", "vvlgrey");
		BM_Color.put("Thk", "lgrey");

		for (String currentOrder : orderList)
			for (int i = 0; i < featureList.size(); i++) {
				String brainMeasure = featureList.get(i);
				if (brainMeasure.startsWith(currentOrder)) {
					String line = "chr - BM" + (i + 1) + " " + brainMeasure
							+ " 0 2 " + BM_Color.get(currentOrder);
					ROIList.add(line);
				}

			}
		DicccolUtilIO
				.writeArrayListToFile(
						ROIList,
						"E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\Circos_BrainMeasureList.conf");
	}
	
	public void extractList() throws BiffException, IOException
	{
		int countThreshold = 90;
		String category = "Complete";
		String subgroup = "Males";
		List<String> featureList = new ArrayList<String>();
		
		String filePath = "C:\\Users\\djzhu\\Google Drive\\JournalPapers\\MDD\\featureFrequency.xls";
		System.out.println("#####################Reading file: " + filePath
				+ " ...");
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\LassoInput\\"
				+ category + "\\" + subgroup + "\\";
		
		File excel_Current = new File(filePath);
		Workbook w_Current = Workbook.getWorkbook(excel_Current);
		String sheetName = category+"_"+subgroup;

		Sheet sheet_Current = w_Current.getSheet(sheetName);

		// find how many features
		int feaNum = sheet_Current.getRows();
		// read feature list
		for (int row = 0; row < feaNum; row++)
		{
			int count = Integer.valueOf( sheet_Current.getCell(1, row).getContents()
					.trim() );
			if (count>=countThreshold)
				featureList.add(sheet_Current.getCell(0, row).getContents()
					.trim());
		}//for row
		DicccolUtilIO.writeArrayListToFile(featureList, dir+"FeatureListHighlight__"+category+"_"+subgroup+"_FreThre_"+countThreshold+".txt");
	}
	
	public void generateHighLightList()
	{		
		int countThreshold = 90;
		String method = "Composed";
		String category = "Imputed";
		String subgroup = "Epi3";
//		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\LassoInput\\Composed\\"
//				+ category + "\\" + subgroup + "\\";
		String dir = "E:\\data\\Machine_Learning_MDD\\Journal\\FromBrandy\\Composed\\";
		
		String featureDicFile = "D:\\circos\\Test\\test1_roi.conf";
		List<String> featureDicList = DicccolUtilIO.loadFileToArrayList(featureDicFile);
		Map<String,String> featureMap = new HashMap<String,String>();
		for(String currentLine:featureDicList)
		{
			String[] currentLineArray = currentLine.split("\\s+");
			featureMap.put(currentLineArray[3].trim(), currentLineArray[2].trim());
		}
//		System.out.println(featureMap.toString());
		
		List<String> outputList = new ArrayList<String>();
		String featureListFile = dir+category+"_"+subgroup+"_ComposedFeatures.txt";
		List<String> featureList = DicccolUtilIO.loadFileToArrayList(featureListFile);
		for(String feature:featureList)
			outputList.add(featureMap.get(feature.trim())+" 0 2");
		DicccolUtilIO.writeArrayListToFile(outputList, "D:\\circos\\Test\\"+method+".Highlight."+category+"."+subgroup+".txt");
		
	}

	public static void main(String[] args) throws BiffException, IOException {
		J_circos mainHandler = new J_circos();
//		mainHandler.generateROI();
//		mainHandler.extractList();
		mainHandler.generateHighLightList();

	}

}
