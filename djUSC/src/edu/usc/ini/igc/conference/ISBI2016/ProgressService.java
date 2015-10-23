package edu.usc.ini.igc.conference.ISBI2016;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import edu.uga.DICCCOL.DicccolUtilIO;

public class ProgressService {

	public double checkMultipleRun(ProcessInfo processInfo) {
		double totalFileNum = processInfo.multiRun_TotalSubNumber
				* processInfo.multiRun_NumOfRuns;
		double fileNumCount = 0.0;
		for (int s = 1; s <= processInfo.multiRun_TotalSubNumber; s++) {
			if (processInfo.multiRun_OutPutDir != null
					&& processInfo.multiRun_OutPutDir.trim().length() > 1) {
				File dir = new File(processInfo.multiRun_OutPutDir + "/" + s
						+ "/");
				String[] files = dir.list();
				for (int f = 0; f < files.length; f++) {
					String filename = files[f];
					if (filename.endsWith("_D.txt"))
						fileNumCount++;
				} // for
			} // if
		}
		return (fileNumCount / totalFileNum) * 100;
	}

	public void calculateErrorLimit(ProcessInfo processInfo) {
		String files;

		List<String> distributionInfo = new ArrayList<String>();
		for (int s = 1; s <= processInfo.multiRun_TotalSubNumber; s++) {
			String currentLogDir = processInfo.multiRun_OutPutDir + "/" + s
					+ "/log";
			File folder = new File(currentLogDir);
			File[] listOfFiles = folder.listFiles();
			String fileFilter = "MultipleRun";
			String tmpLine = "";
			DescriptiveStatistics stats = new DescriptiveStatistics();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					files = listOfFiles[i].getName();
					if (files.startsWith(fileFilter)) {
						System.out.println("Dealling with " + files);
						List<String> currentFileContent = DicccolUtilIO
								.loadFileToArrayList(currentLogDir + "/"
										+ files);
						for (int r = 0; r < currentFileContent.size(); r++)
							if (currentFileContent.get(r).startsWith(
									"Total Decode Error")) {
								double tmpError = Double
										.valueOf(currentFileContent.get(r)
												.split("\\s+")[4]);
								System.out.println(tmpError);
								stats.addValue(tmpError);
							} // if
					} // if
				} // if
			} // for all the files
			tmpLine += stats.getStandardDeviation() + " ";
			tmpLine += stats.getMean() + " ";
			tmpLine += stats.getPercentile(50) + " ";
			tmpLine += (stats.getMax());
			distributionInfo.add(tmpLine);
		} // for every subject
		DicccolUtilIO.writeArrayListToFile(distributionInfo,
				processInfo.multiRun_OutPutDir + "/errorLimit.txt");
	}

	public double checkStep1_Progress(ProcessInfo processInfo) {
		double totalFileNum = processInfo.ESL_SubEndID
				- processInfo.ESL_SubStartID + 1;
		double fileNumCount = 0.0;
		for (int s = processInfo.ESL_SubStartID; s <= processInfo.ESL_SubEndID; s++) {
			if (processInfo.ESL_OutPutDir != null
					&& processInfo.ESL_OutPutDir.trim().length() > 1) {
				File dir = new File(processInfo.ESL_OutPutDir + "/"
						+ processInfo.ESL_SubStartID + "_"
						+ processInfo.ESL_SubEndID + "/" + s + "/OptDicIndex_"
						+ processInfo.ESL_OptDicIndex + "/");
				String[] files = dir.list();
				for (int f = 0; f < files.length; f++) {
					String filename = files[f];
					if (filename.startsWith("VR_sub_")
							&& filename.endsWith(".txt"))
						fileNumCount++;
				} // for
			} // if
		}
		return (fileNumCount / totalFileNum) * 100;
	}

	public void do_Step1_qsub(ProcessInfo processInfo) {
		// setup on_off switch
		processInfo.ESL_on_off_OptDicIndexNew = 1;
		// execute the bash script
		Process proc = null;
		try {
			String strCmd = "sh /ifshome/dzhu/scripts/Journal_ESL/codeDemo/ESL_Step1.sh "
					+ processInfo.ESL_on_off_firstTime
					+ " "
					+ processInfo.ESL_on_off_OptDicIndexNew
					+ " "
					+ processInfo.multiRun_OutPutDir
					+ " "
					+ processInfo.ESL_OutPutDir
					+ " "
					+ processInfo.ESL_SubStartID
					+ " "
					+ processInfo.ESL_SubEndID
					+ " "
					+ processInfo.ESL_OptDicIndex
					+ " "
					+ processInfo.ESL_pValueThresholdUB
					+ " "
					+ processInfo.ESL_pValueThresholdLB
					+ " "
					+ processInfo.ESL_pValueThresholdStep
					+ " "
					+ processInfo.ESL_pValueCheckSimilarThreshold
					+ " "
					+ processInfo.ESL_voteRateThreshold
					+ " "
					+ processInfo.ESL_pValueVRThreshold
					+ " "
					+ processInfo.ESL_DicSize
					+ " "
					+ processInfo.ESL_SampleEleNum;
			System.out.println("CMD: " + strCmd);
			proc = Runtime.getRuntime().exec(strCmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			proc.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String stdOutput = null;
			while ((stdOutput = stdInput.readLine()) != null) {
				System.out.println(stdOutput);
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public double checkStep2_Progress(ProcessInfo processInfo) {
		double totalFileNum = processInfo.ESL_SubEndID
				- processInfo.ESL_SubStartID + 1;
		double fileNumCount = 0.0;
		for (int s = processInfo.ESL_SubStartID; s <= processInfo.ESL_SubEndID; s++) {
			if (processInfo.ESL_OutPutDir != null
					&& processInfo.ESL_OutPutDir.trim().length() > 1) {
				File dir = new File(processInfo.ESL_OutPutDir + "/"
						+ processInfo.ESL_SubStartID + "_"
						+ processInfo.ESL_SubEndID + "/" + s + "/OptDicIndex_"
						+ processInfo.ESL_OptDicIndex + "/");
				String[] files = dir.list();
				for (int f = 0; f < files.length; f++) {
					String filename = files[f];
					if (filename.endsWith("flag.txt"))
						fileNumCount++;
				} // for
			} // if
		}
		return (fileNumCount / totalFileNum) * 100;
	}

	public void do_Step2(ProcessInfo processInfo) {
		// setup on_off switch
		processInfo.ESL_on_off_OptDicIndexNew = 0;
		// execute the bash script
		Process proc = null;
		try {
			String strCmd = "sh /ifshome/dzhu/scripts/Journal_ESL/codeDemo/ESL_Step2.sh "
					+ processInfo.multiRun_OriDataDir
					+ " "
					+ processInfo.multiRun_OutPutDir
					+ " "
					+ processInfo.ESL_OutPutDir
					+ " "
					+ processInfo.ESL_SubStartID
					+ " "
					+ processInfo.ESL_SubEndID
					+ " "
					+ processInfo.ESL_OptDicIndex
					+ " "
					+ processInfo.ESL_DicSize
					+ " "
					+ processInfo.ESL_SampleEleNum
					+ " "
					+ processInfo.ESL_EpochNum + " " + processInfo.ESL_Lambda;
			System.out.println("CMD: " + strCmd);
			proc = Runtime.getRuntime().exec(strCmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			proc.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String stdOutput = null;
			while ((stdOutput = stdInput.readLine()) != null) {
				System.out.println(stdOutput);
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public double checkIndividualReport_Progress(ProcessInfo processInfo) {
		double totalFileNum = processInfo.ESL_SubEndID
				- processInfo.ESL_SubStartID + 1;
		double fileNumCount = 0.0;
		for (int s = processInfo.ESL_SubStartID; s <= processInfo.ESL_SubEndID; s++) {
			if (processInfo.ESL_OutPutDir != null
					&& processInfo.ESL_OutPutDir.trim().length() > 1) {
				File dir = new File(processInfo.ESL_OutPutDir + "/"
						+ processInfo.ESL_SubStartID + "_"
						+ processInfo.ESL_SubEndID + "/Report/" + s + "/");
				String[] files = dir.list();
				for (int f = 0; f < files.length; f++) {
					String filename = files[f];
					if (filename.startsWith("individual")
							&& filename.endsWith("flag.txt"))
						fileNumCount++;
				} // for
			} // if
		}
		return (fileNumCount / totalFileNum) * 100;
	}

	public void individualReportRun(ProcessInfo processInfo) {
		// execute the bash script
		Process proc = null;
		try {
			String strCmd = "sh /ifshome/dzhu/scripts/Journal_ESL/codeDemo/ESL_Step3.sh "
					+ processInfo.ESL_SubStartID
					+ " "
					+ processInfo.ESL_SubEndID
					+ " "
					+ processInfo.ESL_OptDicIndex
					+ " "
					+ processInfo.multiRun_OriDataDir
					+ " "
					+ processInfo.ESL_OutPutDir
					+ " "
					+ processInfo.ESL_DicSize
					+ " "
					+ processInfo.ESL_SampleEleNum
					+ " "
					+ processInfo.ESL_EpochNum + " " + processInfo.ESL_Lambda;
			System.out.println("CMD: " + strCmd);
			proc = Runtime.getRuntime().exec(strCmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			proc.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String stdOutput = null;
			while ((stdOutput = stdInput.readLine()) != null) {
				System.out.println(stdOutput);
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public double checkGroupReport_Progress(ProcessInfo processInfo) {
		double totalFileNum = processInfo.ESL_OptDicIndex + 1;
		double fileNumCount = 0.0;
		if (processInfo.ESL_OutPutDir != null
				&& processInfo.ESL_OutPutDir.trim().length() > 1) {
			File dir = new File(processInfo.ESL_OutPutDir + "/"
					+ processInfo.ESL_SubStartID + "_"
					+ processInfo.ESL_SubEndID + "/Report/");
			String[] files = dir.list();
			for (int f = 0; f < files.length; f++) {
				String filename = files[f];
				if (filename.startsWith("group")
						&& filename.endsWith("flag.txt"))
					fileNumCount++;
			} // for
		} // if
		return (fileNumCount / totalFileNum) * 100;
	}

	public void groupReportRun(ProcessInfo processInfo) {
		// execute the bash script
		Process proc = null;
		try {
			String strCmd = "sh /ifshome/dzhu/scripts/Journal_ESL/codeDemo/Report_Group.sh "
					+ processInfo.ESL_SubStartID
					+ " "
					+ processInfo.ESL_SubEndID
					+ " "
					+ processInfo.ESL_OutPutDir
					+ " "
					+ processInfo.ESL_DicSize
					+ " "
					+ processInfo.ESL_SampleEleNum
					+ " "
					+ (processInfo.ESL_OptDicIndex + 1); // when doing qsub,
															// 1-(OptDicIndex+1):1
			System.out.println("CMD: " + strCmd);
			proc = Runtime.getRuntime().exec(strCmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			proc.waitFor();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			String stdOutput = null;
			while ((stdOutput = stdInput.readLine()) != null) {
				System.out.println(stdOutput);
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
