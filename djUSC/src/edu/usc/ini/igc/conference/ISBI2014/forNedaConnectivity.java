package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.GenerateTracemap;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkHybridData;
import edu.uga.liulab.djVtkBase.djVtkSurData;

public class forNedaConnectivity {

	private CommandLineParser cmdParser;
	private Options options;
	private CommandLine cmdLine;
	private HelpFormatter formatter;

	private String surFileName;
	private String fiberFileName;
	private String predictionFileName;
	private String DICCCOLListFileName;
	private int dicccolColumn = 10;
	private int ringNum = 3;
	private List<Integer> dicccolList = new ArrayList<Integer>();
	private List<Integer> predictedDicccol;
	private String outputlFileName;
	double[][] connectivityMatrix = new double[358][358];

	public forNedaConnectivity() {
		cmdParser = new GnuParser();
		formatter = new HelpFormatter();
	}

	private void createOptions() {
		options = new Options();
		Option oInputFile_s = OptionBuilder.withArgName("String").hasArg()
				.isRequired(true).withDescription("input surface file(*.vtk)")
				.create("s");
		Option oInputFile_f = OptionBuilder.withArgName("String").hasArg()
				.isRequired(true).withDescription("input fiber file(*.vtk)")
				.create("f");
		Option oInputFile_p = OptionBuilder.withArgName("String").hasArg()
				.isRequired(true).withDescription("input prediction file")
				.create("p");
		Option oInputFile_c = OptionBuilder
				.withArgName("Integer")
				.hasArg()
				.isRequired(false)
				.withDescription(
						"The column index of prediction result (default 10. Model: 0-9  Prediction: 10)")
				.create("c");
		Option oInputFile_r = OptionBuilder.withArgName("Integer").hasArg()
				.isRequired(false).withDescription("ring number (default 2)")
				.create("r");
		Option oInputFile_dl = OptionBuilder.withArgName("String").hasArg()
				.isRequired(false).withDescription("DICCCOL List File")
				.create("dl");
		Option oInputFile_o = OptionBuilder.withArgName("String").hasArg()
				.isRequired(true).withDescription("output prefex file")
				.create("o");

		Option ohelp = new Option("help", "print this message");

		options.addOption(oInputFile_s);
		options.addOption(oInputFile_f);
		options.addOption(oInputFile_c);
		options.addOption(oInputFile_r);
		options.addOption(oInputFile_dl);
		options.addOption(oInputFile_p);
		options.addOption(oInputFile_o);
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

	public void setPredictedDicccol(String predictedDicccolFile, int columnIndex) {
		this.predictedDicccol = new ArrayList<Integer>();
		double[][] allContent = DicccolUtilIO.loadFileAsArray(
				predictedDicccolFile, 358, 11);
		if (this.dicccolList == null || this.dicccolList.size() == 0) {
			System.out
					.println("Beging to build the structural connectivity of all 358 DICCCOLs...");
			connectivityMatrix = new double[358][358];
			for (int i = 0; i < 358; i++)
				this.predictedDicccol.add((int) allContent[i][columnIndex]);
		} else {
			System.out
					.println("Beging to build the structural connectivity of "
							+ this.dicccolList.size() + " DICCCOLs...");
			connectivityMatrix = new double[this.dicccolList.size()][this.dicccolList.size()];
			for (int i = 0; i < this.dicccolList.size(); i++)
				this.predictedDicccol.add((int) allContent[this.dicccolList
						.get(i)][columnIndex]);
		}
		allContent = null;
	}
	
	private void doNormalize()
	{
		double tmpSum = 0.0;
		for(int i=0;i<this.predictedDicccol.size();i++)
			for(int j=i+1;j<this.predictedDicccol.size()-1;j++)
				tmpSum +=connectivityMatrix[i][j];
		for(int i=0;i<this.predictedDicccol.size();i++)
			for(int j=0;j<this.predictedDicccol.size();j++)
				connectivityMatrix[i][j]/=tmpSum;		
	}

	private void do_ComputeConnectivity() {
		djVtkSurData surData = new djVtkSurData(this.surFileName);
		djVtkFiberData fiberData = new djVtkFiberData(this.fiberFileName);
		this.setPredictedDicccol(this.predictionFileName, this.dicccolColumn);

		List<List<Integer>> rawConnectivityInfo = new ArrayList<List<Integer>>();

		djVtkHybridData hybridData = new djVtkHybridData(surData, fiberData);
		hybridData.mapSurfaceToBox();
		hybridData.mapFiberToBox();
		for (int i = 0; i < this.predictedDicccol.size(); i++) {
			List<djVtkCell> tmpCellList = hybridData
					.getFibersConnectToPointsSet(surData
							.getNeighbourPoints(this.predictedDicccol.get(i), this.ringNum)).cellsOutput;
			List<Integer> newFiberIDList = new ArrayList<Integer>();
			for (int j = 0; j < tmpCellList.size(); j++)
				newFiberIDList.add(tmpCellList.get(j).cellId);
			rawConnectivityInfo.add(newFiberIDList);
		}

		for (int i = 0; i < rawConnectivityInfo.size() - 1; i++)
			for (int j = i + 1; j < rawConnectivityInfo.size(); j++) {
				int count = 0;
				for (int m = 0; m < rawConnectivityInfo.get(i).size(); m++) {
					int currentCellID = rawConnectivityInfo.get(i).get(m);
					if (rawConnectivityInfo.get(j).contains(currentCellID))
						count++;
				} // for m
				connectivityMatrix[i][j] = connectivityMatrix[j][i] = count;
			} // for j
		this.doNormalize();
		DicccolUtilIO.writeArrayToFile(connectivityMatrix, this.predictedDicccol.size(), this.predictedDicccol.size(), " ", this.outputlFileName+"."+this.predictedDicccol.size()+".conn.txt");
		DicccolUtilIO.writeVtkMatrix1(connectivityMatrix, this.predictedDicccol.size(), this.predictedDicccol.size(), this.outputlFileName+"."+this.predictedDicccol.size()+".conn.vtk");

	}

	private void dispatch(String[] strInputs) throws Exception {
		this.createOptions();
		this.parseArgs(strInputs);
		if (cmdLine == null || cmdLine.hasOption("help")) {
			formatter.printHelp("Usage Info : ", this.options);
			return;
		}

		this.surFileName = cmdLine.getOptionValue("s");
		this.fiberFileName = cmdLine.getOptionValue("f");
		this.predictionFileName = cmdLine.getOptionValue("p");
		if (cmdLine.hasOption("c"))
			this.dicccolColumn = Integer.valueOf(cmdLine.getOptionValue("c")
					.trim());
		this.outputlFileName = cmdLine.getOptionValue("o");
		if (cmdLine.hasOption("r"))
			this.ringNum = Integer.valueOf(cmdLine.getOptionValue("r").trim());
		if (cmdLine.hasOption("dl"))
			this.dicccolList = DicccolUtilIO.loadFileToIntegerArrayList(cmdLine
					.getOptionValue("dl").trim());

		this.do_ComputeConnectivity();
	}

	public static void main(String[] args) {
		forNedaConnectivity mainHandler = new forNedaConnectivity();
		try {
			mainHandler.dispatch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
