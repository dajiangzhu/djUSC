package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;

public class TestUtil {

	// public void findSubIDs()
	// {
	// List<String> subList =
	// DicccolUtilIO.loadFileToArrayList("ADNISubIDs.txt");
	// List<String> outputList = new ArrayList<String>();
	// for(int i=0;i<subList.size();i++)
	// {
	// String[] currentArray = subList.get(i).split("_");
	// if(currentArray[3].equals("1"))
	// outputList.add(subList.get(i));
	// }
	// DicccolUtilIO.writeArrayListToFile(outputList, "ADNI_MICCAI_SubIDs.txt");
	//
	// }

	public void findSubIDs() {
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("ADNISubIDs.txt");
		List<String> outputList = new ArrayList<String>();

		int count = 0;
		int totalCount = 0;
		String tmpStr = "";
		boolean flag = false;
		for (int i = 0; i < subList.size(); i++) {

			String[] currentArray = subList.get(i).split("_");
			if (currentArray[3].equals("1")) {
				tmpStr += subList.get(i) + " ";
				flag = false;
				count++;
				totalCount++;
			}
			if (count == 10) {
				outputList.add(tmpStr);
				flag = true;
				count = 0;
				tmpStr = "";
			}
		}
		if (!flag)
			outputList.add(tmpStr);
		System.out.println("total count is :" + totalCount);
		DicccolUtilIO.writeArrayListToFile(outputList,
				"ADNI_MICCAI_SubIDs_block.txt");

	}

	public void generateGroupSubList() {
		List<String> allSubList = DicccolUtilIO
				.loadFileToArrayList("DeInfo.txt");
		List<String> MiccaiSubList = DicccolUtilIO
				.loadFileToArrayList("ADNI_MICCAI_SubIDs.txt");
		List<String> ADSubList = new ArrayList<String>();
		List<String> MCISubList = new ArrayList<String>();
		List<String> CNSubList = new ArrayList<String>();
		List<String> NASubList = new ArrayList<String>();
		List<String> SMCSubList = new ArrayList<String>();
		for (int i = 0; i < allSubList.size(); i++) {
			String[] currentArray = allSubList.get(i).split("\\s+");
			if (MiccaiSubList.contains(currentArray[0] + "_1")) {
				if (currentArray[1].equalsIgnoreCase("AD"))
					ADSubList.add(currentArray[0] + "_1" + " "
							+ currentArray[1] + " " + currentArray[2] + " "
							+ currentArray[3] + " " + currentArray[4] + " "
							+ currentArray[5] + " " + currentArray[6]);
				if (currentArray[1].equalsIgnoreCase("LMCI")
						|| currentArray[1].equalsIgnoreCase("EMCI")
						|| currentArray[1].equalsIgnoreCase("MCI"))
					MCISubList.add(currentArray[0] + "_1" + " "
							+ currentArray[1] + " " + currentArray[2] + " "
							+ currentArray[3] + " " + currentArray[4] + " "
							+ currentArray[5] + " " + currentArray[6]);
				if (currentArray[1].equalsIgnoreCase("CN"))
					CNSubList.add(currentArray[0] + "_1" + " "
							+ currentArray[1] + " " + currentArray[2] + " "
							+ currentArray[3] + " " + currentArray[4] + " "
							+ currentArray[5] + " " + currentArray[6]);
				if (currentArray[1].equalsIgnoreCase("NA"))
					NASubList.add(currentArray[0] + "_1" + " "
							+ currentArray[1] + " " + currentArray[2] + " "
							+ currentArray[3] + " " + currentArray[4] + " "
							+ currentArray[5] + " " + currentArray[6]);
				if (currentArray[1].equalsIgnoreCase("SMC"))
					SMCSubList.add(currentArray[0] + "_1" + " "
							+ currentArray[1] + " " + currentArray[2] + " "
							+ currentArray[3] + " " + currentArray[4] + " "
							+ currentArray[5] + " " + currentArray[6]);
			} // if
			else
				System.out.println(currentArray[0] + " not found!");
		} // for allSubList
		DicccolUtilIO.writeArrayListToFile(ADSubList, "MICCAI_AD_SubList.txt");
		DicccolUtilIO
				.writeArrayListToFile(MCISubList, "MICCAI_MCI_SubList.txt");
		DicccolUtilIO.writeArrayListToFile(CNSubList, "MICCAI_CN_SubList.txt");
		DicccolUtilIO.writeArrayListToFile(NASubList, "MICCAI_NA_SubList.txt");
		DicccolUtilIO
				.writeArrayListToFile(SMCSubList, "MICCAI_SMC_SubList.txt");
	}

	public void generateRandomSubList() {
		int RoundNum = 100;
		int SubsNum = 20;

		List<String> ADSubList = DicccolUtilIO
				.loadFileToArrayList("MICCAI_AD_SubList.txt");
		List<String> MCISubList = DicccolUtilIO
				.loadFileToArrayList("MICCAI_MCI_SubList.txt");
		List<String> CNSubList = DicccolUtilIO
				.loadFileToArrayList("MICCAI_CN_SubList.txt");
		List<String> ADRandomList = new ArrayList<String>();
		List<String> MCIRandomList = new ArrayList<String>();
		List<String> CNRandomList = new ArrayList<String>();
		int numAD = ADSubList.size();
		int numMCI = MCISubList.size();
		int numCN = CNSubList.size();

		for (int i = 0; i < RoundNum; i++) {
			System.out.println("Round---" + i);
			List<Integer> ADTmpList = DicccolUtil.geneRandom(SubsNum, numAD);
			ADRandomList.clear();
			// for(int j=0;j<ADTmpList.size();j++)
			// ADRandomList.add(
			// ADSubList.get(ADTmpList.get(j)-1).split("\\s+")[0].trim() );
			// DicccolUtilIO.writeArrayListToFile(ADRandomList,
			// "./MICCAI2015/ADRandomList_"+(i+1)+".txt");

			List<Integer> MCITmpList = DicccolUtil.geneRandom(SubsNum, numMCI);
			MCIRandomList.clear();
			// for(int j=0;j<MCITmpList.size();j++)
			// MCIRandomList.add(
			// MCISubList.get(MCITmpList.get(j)-1).split("\\s+")[0].trim() );
			// DicccolUtilIO.writeArrayListToFile(MCIRandomList,
			// "./MICCAI2015/MCIRandomList_"+(i+1)+".txt");

			List<Integer> CNTmpList = DicccolUtil.geneRandom(SubsNum, numCN);
			CNRandomList.clear();
			for (int j = 0; j < CNTmpList.size(); j++)
				CNRandomList.add(CNSubList.get(CNTmpList.get(j) - 1).split(
						"\\s+")[0].trim());
			DicccolUtilIO.writeArrayListToFile(CNRandomList,
					"./MICCAI2015/CNRandomList_" + (i + 1) + ".txt");
		} // for i
	}
	
	public void generateRandomSubList_sfn() {
		int RoundNum = 10;
		int SubsNum = 5;

		List<String> CNE3SubList = DicccolUtilIO
				.loadFileToArrayList("./MICCAI2015/sfn/CNE3_SubList.txt");
		List<String> CNE4SubList = DicccolUtilIO
				.loadFileToArrayList("./MICCAI2015/sfn/CNE4_SubList.txt");
		List<String> ADE4SubList = DicccolUtilIO
				.loadFileToArrayList("./MICCAI2015/sfn/ADE4_SubList.txt");
		List<String> CNE3RandomList = new ArrayList<String>();
		List<String> CNE4RandomList = new ArrayList<String>();
		List<String> ADE4RandomList = new ArrayList<String>();
		int numCNE3 = 15;//CNE3SubList.size();
		int numCNE4 = 15;//CNE4SubList.size();
		int numADE4 = 15;//ADE4SubList.size();

		for (int i = 0; i < RoundNum; i++) {
			System.out.println("Round---" + i);
			List<Integer> CNE3TmpList = DicccolUtil.geneRandom(SubsNum, numCNE3);
			CNE3RandomList.clear();
			 for(int j=0;j<CNE3TmpList.size();j++)
				 CNE3RandomList.add(
						 CNE3SubList.get(CNE3TmpList.get(j)-1).split("\\s+")[0].trim() );
			 DicccolUtilIO.writeArrayListToFile(CNE3RandomList,
			 "./MICCAI2015/sfn/CNRandomList_"+(i+1+100)+".txt");

			List<Integer> CNE4TmpList = DicccolUtil.geneRandom(SubsNum, numCNE4);
			CNE4RandomList.clear();
			 for(int j=0;j<CNE4TmpList.size();j++)
				 CNE4RandomList.add(
			 CNE4SubList.get(CNE4TmpList.get(j)-1).split("\\s+")[0].trim() );
			 DicccolUtilIO.writeArrayListToFile(CNE4RandomList,
			 "./MICCAI2015/sfn/CNRandomList_"+(i+1+110)+".txt");

			List<Integer> ADE4TmpList = DicccolUtil.geneRandom(SubsNum, numADE4);
			ADE4RandomList.clear();
			for (int j = 0; j < ADE4TmpList.size(); j++)
				ADE4RandomList.add(ADE4SubList.get(ADE4TmpList.get(j) - 1).split(
						"\\s+")[0].trim());
			DicccolUtilIO.writeArrayListToFile(ADE4RandomList,
					"./MICCAI2015/sfn/ADRandomList_" + (i + 1+100) + ".txt");
		} // for i
	}

	public void generateGroupBlockList() {
		String group = "CN";
		List<String> subList = DicccolUtilIO
				.loadFileToArrayList("MICCAI_"+group+"_SubList.txt");
		List<String> outputList = new ArrayList<String>();

		int count = 0;
		int totalCount = 0;
		String tmpStr = "";
		boolean flag = false;
		for (int i = 0; i < subList.size(); i++) {

			String[] currentArray = subList.get(i).split("\\s+");
			tmpStr += currentArray[0] + " ";
			flag = false;
			count++;
			totalCount++;
			if (count == 10) {
				outputList.add(tmpStr);
				flag = true;
				count = 0;
				tmpStr = "";
			}
		}
		if (!flag)
			outputList.add(tmpStr);
		System.out.println("total count is :" + totalCount);
		DicccolUtilIO.writeArrayListToFile(outputList,
				"ADNI_MICCAI_"+group+"_block.txt");

	}

	public static void main(String[] args) {
		TestUtil mainHandler = new TestUtil();
		// mainHandler.findSubIDs();
		// mainHandler.generateGroupSubList();
//		mainHandler.generateRandomSubList();
//		mainHandler.generateGroupBlockList();
		///////////////////////////
		mainHandler.generateRandomSubList_sfn();

	}

}
