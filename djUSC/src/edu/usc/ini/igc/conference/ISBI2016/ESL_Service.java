package edu.usc.ini.igc.conference.ISBI2016;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class ESL_Service {

	public ProcessInfo processInfo = new ProcessInfo();
	public ProgressService progressService = new ProgressService();

	public CommandLineParser cmdParser;
	public Options options;
	public CommandLine cmdLine;
	public HelpFormatter formatter;

	public ESL_Service() {
		cmdParser = new GnuParser();
		formatter = new HelpFormatter();
	}

	private void createOptions() {
		options = new Options();
		//**************** For multiple run
		Option oMultiRun_OriDataDir = OptionBuilder.withArgName("String")
				.hasArg().isRequired(false)
				.withDescription("input MultiRun_OriDataDir").create("od");
		Option oMultiRun_OutPutDir = OptionBuilder.withArgName("String")
				.hasArg().isRequired(true)
				.withDescription("input MultiRun_OutPutDir").create("mo");
		Option oMultiRun_NumOfRuns = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input MultiRun_NumOfRuns").create("mnr");
		Option oMultiRun_DicSize = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input MultiRun_DicSize").create("mds");
		Option oMultiRun_SampleEleNum = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input MultiRun_SampleEleNum").create("msn");
		Option oMultiRun_EpochNum = OptionBuilder.withArgName("Double")
				.hasArg().isRequired(false)
				.withDescription("input MultiRun_EpochNum").create("men");
		Option oMultiRun_Lambda = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input MultiRun_Lambda")
				.create("ml");
		//**************** For ESL
		Option oESL_OutPutDir = OptionBuilder.withArgName("String")
				.hasArg().isRequired(false)
				.withDescription("input ESL_OutPutDir").create("eslo");
		Option oESL_SubStartID = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input ESL_SubStartID").create("eslss");
		Option oESL_SubEndID = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input ESL_SubEndID").create("eslse");
		Option oESL_SampleEleNum = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input ESL_SampleEleNum").create("eslsn");
		Option oESL_pValueThresholdLB = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_pValueThresholdLB")
				.create("esllb");
		Option oESL_pValueThresholdUB = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_pValueThresholdUB")
				.create("eslub");
		Option oESL_pValueThresholdStep = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_pValueThresholdStep")
				.create("eslts");
		Option oESL_pValueCheckSimilarThreshold = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_pValueCheckSimilarThreshold")
				.create("eslst");
		Option oESL_voteRateThreshold = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_voteRateThreshold")
				.create("eslvrt");
		Option oESL_pValueVRThreshold = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_pValueVRThreshold")
				.create("eslpvrt");
		
		Option oESL_DicSize = OptionBuilder.withArgName("Integer")
				.hasArg().isRequired(false)
				.withDescription("input ESL_DicSize").create("eslds");
		Option oESL_EpochNum = OptionBuilder.withArgName("Double")
				.hasArg().isRequired(false)
				.withDescription("input ESL_EpochNum").create("eslen");
		Option oESL_Lambda = OptionBuilder.withArgName("Double").hasArg()
				.isRequired(false).withDescription("input ESL_Lambda")
				.create("esll");
		//**************** For all
		Option oDoMR = new Option("mr", "do multiple run");
		Option oDoESL = new Option("esl", "do esl");
		Option ohelp = new Option("help", "print this message");

		//**************** For multiple run
		options.addOption(oMultiRun_OriDataDir);
		options.addOption(oMultiRun_OutPutDir);
		options.addOption(oMultiRun_NumOfRuns);
		options.addOption(oMultiRun_DicSize);
		options.addOption(oMultiRun_SampleEleNum);
		options.addOption(oMultiRun_EpochNum);
		options.addOption(oMultiRun_Lambda);
		//**************** For ESL
		options.addOption(oESL_OutPutDir);
		options.addOption(oESL_SubStartID);
		options.addOption(oESL_SubEndID);
		options.addOption(oESL_SampleEleNum);
		options.addOption(oESL_pValueThresholdLB);
		options.addOption(oESL_pValueThresholdUB);
		options.addOption(oESL_pValueThresholdStep);
		options.addOption(oESL_pValueCheckSimilarThreshold);
		options.addOption(oESL_voteRateThreshold);
		options.addOption(oESL_pValueVRThreshold);
		options.addOption(oESL_DicSize);
		options.addOption(oESL_EpochNum);
		options.addOption(oESL_Lambda);
		//**************** For all
		options.addOption(oDoMR);
		options.addOption(oDoESL);
		options.addOption(ohelp);
	}

	private void parseArgs(String[] strInputs) {
		try {
			cmdLine = this.cmdParser.parse(this.options, strInputs);
		} catch (ParseException e) {
			formatter.printHelp("Usage Info : ", this.options);
			System.exit(0);
		}
	}
	
	public void MR_preset()
	{
		processInfo.multiRun_OriDataDir = cmdLine.getOptionValue("od");
		processInfo.multiRun_OutPutDir = cmdLine.getOptionValue("mo");
		processInfo.multiRun_NumOfRuns = Integer.valueOf(cmdLine
				.getOptionValue("mnr").trim());
		processInfo.multiRun_DicSize = Integer.valueOf(cmdLine.getOptionValue(
				"mds").trim());
		processInfo.multiRun_SampleEleNum = Integer.valueOf(cmdLine
				.getOptionValue("msn").trim());
		processInfo.multiRun_EpochNum = Double.valueOf(cmdLine.getOptionValue(
				"men").trim());
		processInfo.multiRun_Lambda = Double.valueOf(cmdLine.getOptionValue(
				"ml").trim());
	}
	
	public void step1_preset() {
		processInfo.ESL_OutPutDir = cmdLine.getOptionValue("eslo");
		processInfo.ESL_OptDicIndex++;
		// save paramaters
		processInfo.ESL_SubStartID = Integer.valueOf(cmdLine
				.getOptionValue("eslss").trim());
		processInfo.ESL_SubEndID = Integer.valueOf(cmdLine
				.getOptionValue("eslse").trim());
		
		processInfo.ESL_SampleEleNum = Integer.valueOf(cmdLine
				.getOptionValue("eslsn").trim());

		processInfo.ESL_pValueThresholdLB = Double.valueOf(cmdLine.getOptionValue(
				"esllb").trim());
		processInfo.ESL_pValueThresholdUB = Double.valueOf(cmdLine.getOptionValue(
				"eslub").trim());
		processInfo.ESL_pValueThresholdStep = Double.valueOf(cmdLine.getOptionValue(
				"eslts").trim());
		processInfo.ESL_pValueCheckSimilarThreshold = Double.valueOf(cmdLine.getOptionValue(
				"eslst").trim());
		processInfo.ESL_voteRateThreshold =  Double.valueOf(cmdLine.getOptionValue(
				"eslvrt").trim());
		processInfo.ESL_pValueVRThreshold = Double.valueOf(cmdLine.getOptionValue(
				"eslpvrt").trim());
	}
	
	public void step1_checkProgress() {
		int checkProgress = 0;
		do {
			try {
				Thread.sleep(2000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			checkProgress = (int) progressService
					.checkStep1_Progress(processInfo);
			System.out.println("Check Step-1 progress..." + checkProgress+"%...");
		} while (checkProgress != 100.0);
	}
	
	public boolean step1_integration() {
		boolean foundTemplate = false;
		// Analyse VR
		System.out.println("Integrating template info ...");
		ESL_AnalyseVR anaVR = new ESL_AnalyseVR();
		anaVR.optDicIndex = processInfo.ESL_OptDicIndex; // 0-399
		anaVR.dicSize = processInfo.ESL_DicSize; // 400
		anaVR.tSize = processInfo.ESL_SampleEleNum;
		anaVR.subStartID = processInfo.ESL_SubStartID; // 1-58?
		anaVR.subEndID = processInfo.ESL_SubEndID; // 10-68
		anaVR.subNum = anaVR.subEndID - anaVR.subStartID + 1;
		anaVR.voteRateThreshold = Double
				.valueOf(processInfo.ESL_voteRateThreshold);
		anaVR.pValueThreshold = Double
				.valueOf(processInfo.ESL_pValueVRThreshold);
		anaVR.ESL_OutPutDir = processInfo.ESL_OutPutDir;
		anaVR.printParaInfo();
		anaVR.initialization();
		foundTemplate = anaVR.findTheTemplate();
		return foundTemplate;
	}
	
	public void step2_preset()
	{
		processInfo.ESL_DicSize = Integer.valueOf(cmdLine.getOptionValue(
				"eslds").trim());
		processInfo.ESL_EpochNum = Double.valueOf(cmdLine.getOptionValue(
				"eslen").trim());
		processInfo.ESL_Lambda = Double.valueOf(cmdLine.getOptionValue(
				"esll").trim());
	}
	
	public void step2_checkProgress() 
	{
		int checkProgress = 0;
		do {
			try {
				Thread.sleep(10000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			checkProgress = (int) progressService
					.checkStep2_Progress(processInfo);
			System.out.println("Check Step-2 progress..."
					+ checkProgress+"%");
		} while (checkProgress != 100.0);
	}
	
	public void doESL() {
		boolean foundTemplate = false;
		System.out.println("*************************************************** ESL started ... ***************************************************");
		do {
			System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-1 started ...");
			step1_preset();
			progressService.do_Step1_qsub(processInfo);
			step1_checkProgress();
			// the first time executing this bash script
			if (processInfo.ESL_on_off_firstTime == 1)
				processInfo.ESL_on_off_firstTime = 0;
			foundTemplate = step1_integration();
			if (foundTemplate) { // do step-2
				System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-1 finished ...");
				System.out.println("Template:" + processInfo.ESL_OptDicIndex+" Found:Yes  Optimized:No");
				// //////////////////////////////
				System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-2 started ...");
				step2_preset();
				progressService.do_Step2(processInfo);
				step2_checkProgress();
				System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-1 finished ...");
				System.out.println("Template:" + processInfo.ESL_OptDicIndex+" Found:Yes  Optimized:Yes");
			} //if foundTemplate
		} while (foundTemplate);
		System.out.println("------- No more templates found! -------");
		processInfo.ESL_OptDicIndex--;
		System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-3 started ...");
		progressService.individualReportRun(processInfo);
		// check progress
		int checkProgress = 0;
		do {
			try {
				Thread.sleep(10000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			checkProgress = (int) progressService
					.checkIndividualReport_Progress(processInfo);
			System.out.println("Check individual report progress..."
					+ checkProgress+"%");
		} while (checkProgress != 100.0);
		System.out.println("############ OptDicIndex:"+processInfo.ESL_OptDicIndex+" Step-3 finished ...");
		System.out.println("*************************************************** ESL finished ... ***************************************************");
	}

	private void dispatch(String[] strInputs) throws Exception {
		this.createOptions();
		this.parseArgs(strInputs);
		if (cmdLine == null || cmdLine.hasOption("help")) {
			formatter.printHelp("Usage Info : ", this.options);
			return;
		}
		if (cmdLine.hasOption("mr"))
			this.do_MR();
		
		if (cmdLine.hasOption("esl"))
			this.doESL();
	}

	public void do_MR() {
		this.MR_preset();
		System.out.println("### Will do Multiple Run and estimate the errorLimit ...");
		//initial numOfSub
		File dir = new File(processInfo.multiRun_OriDataDir);
		String[] files = dir.list();
		for (int f = 0; f < files.length; f++) {
			String filename = files[f];
			if (filename.startsWith("sub")
					&& filename.endsWith(".txt"))
				processInfo.multiRun_TotalSubNumber++;
		} // for
		System.out.println("### There are "+processInfo.multiRun_TotalSubNumber+" subjects in the original data dir...");
		// run MutipleRun.sh
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(
					"sh /ifshome/dzhu/scripts/Journal_ESL/codeDemo/MultipleRun.sh "
							+ processInfo.multiRun_OriDataDir + " "
							+ processInfo.multiRun_TotalSubNumber + " "
							+ processInfo.multiRun_OutPutDir + " "
							+ processInfo.multiRun_NumOfRuns + " "
							+ processInfo.multiRun_DicSize + " "
							+ processInfo.multiRun_SampleEleNum + " "
							+ processInfo.multiRun_EpochNum + " "
							+ processInfo.multiRun_Lambda);
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
		// checkProgress
		int checkProgress = 0;
		do {
			try {
				Thread.sleep(6000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			checkProgress = (int) progressService.checkMultipleRun(processInfo);
			System.out.println("Check multipleRun progress..." + checkProgress+"%");
		} while (checkProgress != 100.0);
		System.out.println("Integrating parallel results ...");
		// Calculate the error limit
		progressService.calculateErrorLimit(processInfo);
		System.out.println("Integrating parallel results finished!");
	}

	public static void main(String[] args) {
		ESL_Service mainHandler = new ESL_Service();
		try {
			mainHandler.dispatch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
