package edu.usc.ini.igc.journal.EmbededSC;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.stat.Correlation;

public class FindStableIndividualTemplate {

	// ********** pre-set parameters
	public int tSize = 284;
	public double correlationSimilarThreshold = 0.7;
	public double countThreshold = 0.9;
	// ********** global parameters
	public List<double[][]> allRunsDMatrix = new ArrayList<double[][]>();
	public double[][] runsRepeatCounter;
	Correlation correlation = null;
	public List<Integer> stableRowList = new ArrayList<Integer>();
	public List<Integer> stableColumnList = new ArrayList<Integer>();

	private List<Integer> getRunsIndexList(int numOfRuns) {
		List<Integer> runsIndexList = new ArrayList<Integer>();
		for (int i = 0; i < numOfRuns; i++)
			runsIndexList.add((i + 1));
		return runsIndexList;
	}

	public double[][] trsposeM(double[][] data, int row, int column) {
		double[][] result = new double[column][row];
		for (int i = 0; i < row; i++)
			for (int j = 0; j < column; j++)
				result[j][i] = data[i][j];
		return result;
	}

	private void initial(String subID, int dicSize, int numOfRuns) {
		List<Integer> runsIndexList = this.getRunsIndexList(numOfRuns);
		for (int r = 0; r < runsIndexList.size(); r++) {
			// ***********loadAllRunsDMatrix
			int currentRun = runsIndexList.get(r);
			String curFile = "/ifs/loni/faculty/thompson/four_d/dzhu/data/HCP/TaskFMRI/multipleRuns/"
					+ subID + "/sub_" + subID + "_run_" + currentRun + "_D.txt";
			System.out.println("curFile: " + curFile);
			double[][] curSigM = this.trsposeM(
					DicccolUtilIO.loadFileAsArray(curFile, tSize, dicSize),
					tSize, dicSize);
			allRunsDMatrix.add(curSigM);
		} // for multiple runs
			// ***********initial repeat counter
		runsRepeatCounter = DicccolUtilIO.loadFileAsArray(
				"/ifs/loni/faculty/thompson/four_d/dzhu/data/HCP/TaskFMRI/multipleRuns/"
						+ subID + "/sub.allRunsRepeatCounter." + numOfRuns
						+ ".txt", dicSize, numOfRuns);
		// *************initial correlation handler
		correlation = new Correlation();
	}

	private boolean findTopStableAtom(String subID, int dicSize, int numOfRuns) {

		boolean flag = false;
		double tmpTop = 0.0;
		int tmpRow = -1;
		int tmpColumn = -1;

		// Find the top stable atom
		for (int r = 0; r < dicSize; r++)
			for (int c = 0; c < numOfRuns; c++)
				if (runsRepeatCounter[r][c] >= countThreshold
						&& runsRepeatCounter[r][c] > tmpTop) {
					tmpTop = runsRepeatCounter[r][c];
					tmpRow = r;
					tmpColumn = c;
					flag = true;
				} // if
		if (flag) {
			System.out.println("Found Template: Run-"+tmpColumn+"  DictionaryAtom-"+tmpRow);
			double[] curTopAtom = allRunsDMatrix.get(tmpColumn)[tmpRow];
			runsRepeatCounter[tmpRow][tmpColumn] = 0.0;
			stableRowList.add(tmpRow);
			stableColumnList.add(tmpColumn);
			
			// Find the other similar atoms
			for (int r = 0; r < dicSize; r++)
				for (int c = 0; c < numOfRuns; c++)
					if (runsRepeatCounter[r][c] >= countThreshold) {
							double[] tmpAtom = allRunsDMatrix.get(c)[r];
							double tmpCorreValue = correlation
									.Correlation_Pearsons(curTopAtom, tmpAtom);
							if (tmpCorreValue >= correlationSimilarThreshold)
								runsRepeatCounter[r][c] = 0.0; // this atom is
																// similar to
																// the current
																// top atom
						} // if
		}
		return flag;
	}

	private void findStableAtoms(String subID, int dicSize, int numOfRuns) {

		for (int r = 0; r < dicSize; r++)
			for (int c = 0; c < numOfRuns; c++)
				runsRepeatCounter[r][c] = runsRepeatCounter[r][c]
						/ (double) numOfRuns;
		boolean flag = false;
		do
		{
			flag = this.findTopStableAtom(subID, dicSize, numOfRuns);
		} while (flag);
		double[][] templateList = new double[stableRowList.size()][tSize];
		for(int i=0; i < stableRowList.size();i++)
		{
			System.out.println("r:"+stableRowList.get(i)+"  c:"+stableColumnList.get(i));
			templateList[i] = allRunsDMatrix.get(stableColumnList.get(i))[stableRowList.get(i)];
		}
		DicccolUtilIO.writeArrayToFile(templateList, stableRowList.size(), tSize, " ", "/ifs/loni/faculty/thompson/four_d/dzhu/data/HCP/TaskFMRI/multipleRuns/"
					+ subID + "/TempletList.txt");
	}

	public static void main(String[] args) {
		if (args.length == 5) {
			String subID = args[0].trim();
			int dicSize = Integer.valueOf(args[1].trim());
			int numOfRuns = Integer.valueOf(args[2].trim());
			double countThreshold = Double.valueOf(args[3].trim());
			double correlationSimilarThreshold = Double.valueOf(args[4].trim());
			System.out.println("subID:" + subID + " dicSize:" + dicSize
					+ " numOfRuns:" + numOfRuns);
			FindStableIndividualTemplate mainHandler = new FindStableIndividualTemplate();
			mainHandler.initial(subID, dicSize, numOfRuns);
			mainHandler.countThreshold = countThreshold;
			mainHandler.correlationSimilarThreshold = correlationSimilarThreshold;
			mainHandler.findStableAtoms(subID, dicSize, numOfRuns);

		} else {
			System.out
					.println("Need five paramaters: subID, dictionary size,numOfRuns, countThreshold and correlationSimilarThreshold");
			System.exit(0);
		}

	}

}
