package edu.usc.ini.igc.conference.ISBI2016;

public class ProcessInfo {
	//***************Tab-SetUp***************
	//composite_SetUp_Data
	public int multiRun_TotalSubNumber=0;
	public String multiRun_OriDataDir="";
	
	//composite_SetUp_MultiRun
	public String multiRun_OutPutDir="";
	public int multiRun_NumOfRuns = 0;
	public int multiRun_DicSize = 0;
	public int multiRun_SampleEleNum = 0;
	public double multiRun_EpochNum = 0.0;
	public double multiRun_Lambda = 0.0;
	
	//***************Tab-ESL***************
	public String ESL_OutPutDir="";
	int ESL_OptDicIndex = -1;
	int ESL_on_off_firstTime = 1;
	int ESL_on_off_OptDicIndexNew = 0;
	
	int ESL_SubStartID = -1;
	int ESL_SubEndID = -1;
	
	//composite_ESL_step1
	public double ESL_pValueThresholdUB = 0.0;
	public double ESL_pValueThresholdLB = 0.0;
	public double ESL_pValueThresholdStep = 0.0;
	public double ESL_pValueCheckSimilarThreshold = 0.0;
	public double ESL_voteRateThreshold = 0.0;
	public double ESL_pValueVRThreshold = 0.0;
	
	//composite_ESL_step2
	public int ESL_DicSize = 400;
	public int ESL_SampleEleNum = 284;
	public double ESL_EpochNum = 0.0;
	public double ESL_Lambda = 0.0;
	
	//composite_Report
	public String Report_LoadDir="";

}
