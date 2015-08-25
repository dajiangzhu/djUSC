package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class FindStableDictionaryAtoms {
	//********** pre-set parameters
	public int tSize = 284;
	public double correlationLowThreshold = 0.1;
	public double correlationHighThreshold = 0.9;
	//********** global parameters
	public List<double[][]> allRunsDMatrix = new ArrayList<double[][]>();
	public double[][] allRunsRepeatCounter;
	Correlation correlation = null;
	
	private List<Integer> getRunsIndexList(int numOfRuns)
	{
		List<Integer> runsIndexList = new ArrayList<Integer>();
		for(int i=0;i<numOfRuns;i++)
			runsIndexList.add((i+1));
		return runsIndexList;
	}
	
	private List<Integer> getRandomRunsIndexList(int numOfRuns)
	{
		List<Integer> runsIndexList = DicccolUtil.geneRandom(numOfRuns, 100);
		return runsIndexList;
	}
	
	private void findStableAtoms(String subID, int dicSize, int numOfRuns)
	{
		for(int r1=0;r1<numOfRuns;r1++)
		{
			System.out.println("##### Dealing with Run - "+(r1+1)+" ... ");
			double[][] currentD = allRunsDMatrix.get(r1);
			for(int r2=0;r2<numOfRuns;r2++)
			{
				if(r1!=r2)
				{
					System.out.println("Compare with Run - "+(r2+1)+" ... ");
					double[][] compareD = allRunsDMatrix.get(r2);
					for(int d1=0;d1<dicSize;d1++)
						for(int d2=0;d2<dicSize;d2++)
						{
							double tmpCorreValue = correlation.Correlation_Pearsons(
									currentD[d1], compareD[d2]);
							if (tmpCorreValue >= correlationHighThreshold) {
								allRunsRepeatCounter[d1][r1]++;
								// System.out.println("Preserve "+tmpSigIndex+"...");
								break;
							} //if
						} //for d2
				} //if r1!=r2
			} //for r2
		} //for r1
		DicccolUtilIO.writeArrayToFile(allRunsRepeatCounter, dicSize, numOfRuns, " ", "/ifs/loni/faculty/thompson/four_d/dzhu/data/HCP/TaskFMRI/multipleRuns/"+subID+"/sub.allRunsRepeatCounter."+numOfRuns+".txt");
//		DicccolUtilIO.writeArrayToFile(allRunsRepeatCounter, dicSize, numOfRuns, " ", "/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/HCP/TaskFMRI/multipleRuns/"+subID+"/sub.RunsRepeatCounter_"+numOfRuns+".txt");
		
	}
	
	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}
	
	private void initial(String subID, int dicSize, int numOfRuns)
	{
		List<Integer> runsIndexList = this.getRunsIndexList(numOfRuns);
		for(int r=0;r<runsIndexList.size();r++)
		{
			//***********loadAllRunsDMatrix
			int currentRun = runsIndexList.get(r);
			String curFile = "/ifs/loni/faculty/thompson/four_d/dzhu/data/HCP/TaskFMRI/multipleRuns/"+subID+"/sub_"+subID+"_run_"+currentRun+"_D.txt";
//			String curFile = "/run/media/dzhu/89a4b846-ed28-43fc-aed4-28eb5a26ff4f/AllData/HCP/TaskFMRI/multipleRuns/"+subID+"/sub_"+subID+"_run_"+currentRun+"_D.txt";
			System.out.println("curFile: " + curFile);
			double[][] curSigM = this.trsposeM(
					DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize),
					tSize, dicSize);
			allRunsDMatrix.add(curSigM);	
		} //for multiple runs
		//***********initial repeat counter
		allRunsRepeatCounter = new double[dicSize][numOfRuns];
		//*************initial correlation handler
		correlation = new Correlation();
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==3)
		{
			String subID = args[0].trim();
			int dicSize = Integer.valueOf(args[1].trim());
			int numOfRuns = Integer.valueOf(args[2].trim());
			System.out.println("subID:"+subID+" dicSize:"+dicSize+" numOfRuns:"+numOfRuns);
			FindStableDictionaryAtoms mainHandler = new FindStableDictionaryAtoms();
			mainHandler.initial(subID, dicSize, numOfRuns);
			mainHandler.findStableAtoms(subID, dicSize, numOfRuns);
		}
		else
		{
			System.out.println("Need four paramaters: subID, dictionary size and numOfRuns");
			System.exit(0);
		}
			

	}

}
