package edu.usc.ini.igc.util;

import java.io.IOException;
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
import edu.uga.DICCCOL.tool.GenerateDICCCOLConnVtk;
import edu.uga.DICCCOL.visualization.GenerateDICCCOL;

public class GenerateDTS {
	private CommandLineParser cmdParser;
	private Options options;
	private CommandLine cmdLine;
	private HelpFormatter formatter;

	private String bvecFileName;
	private String dtsFileName;


	public GenerateDTS() {
		cmdParser = new GnuParser();
		formatter = new HelpFormatter();
	}

	private void createOptions() {
		options = new Options();
		Option oInputFile_bvec = OptionBuilder.withArgName("String").hasArg().isRequired(true)
				.withDescription("input bvector file").create("bvec");
		Option oOutputFile_DTS = OptionBuilder.withArgName("String").hasArg().isRequired(false)
				.withDescription("output dts file").create("dts");

		Option ohelp = new Option("help", "print this message");
		options.addOption(oInputFile_bvec);
		options.addOption(oOutputFile_DTS);
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


	private void dispatch(String[] strInputs) throws Exception {
		this.createOptions();
		this.parseArgs(strInputs);
		if (cmdLine == null || cmdLine.hasOption("help")) {
			formatter.printHelp("Usage Info : ", this.options);
			return;
		}

		this.bvecFileName = cmdLine.getOptionValue("bvec");
		this.dtsFileName = cmdLine.getOptionValue("dts");

		this.do_writeDTS();
	}
	
	private List<String> formated3(List<String> dataList)
	{
		List<String> resultList = new ArrayList<String>();
		List<String[]> tmpList = new ArrayList<String[]>();
		int nDirection = -1;
		for(int i=0;i<dataList.size();i++)
		{
			String[] currentArray = dataList.get(i).split("\\s+");
			nDirection = currentArray.length;
			tmpList.add(currentArray);
		}
			for(int i=0;i<nDirection;i++)
				resultList.add(tmpList.get(0)[i]+" "+tmpList.get(1)[i]+" "+tmpList.get(2)[i]);
		return resultList;
	}
	
	private void do_writeDTS()
	{
		List <String> bvecList = DicccolUtilIO.loadFileToArrayList(bvecFileName);
		bvecList = this.formated3(bvecList);
		List<String> outList = new ArrayList<String>();
		outList.add("<StudyName>");
		int len = dtsFileName.split("/").length;
		outList.add(dtsFileName.split("/")[len-1]);
		outList.add("<PatientName>");
		outList.add("Not_Communicated");
		outList.add("<NumberOfDTIs>");
		outList.add("1");
		outList.add("<DTIs>");
		outList.add("DTI_noskull.nii.gz");
		outList.add("<GradientList>");
		for(int i=0;i<bvecList.size();i++)
			outList.add(bvecList.get(i));
		outList.add("<ModelType>");
		outList.add("Tensor");
		outList.add("<Tensors>");
		outList.add("Not_Computed");
		outList.add("<AllFibers>");
		outList.add("Not_Computed");
		outList.add("<NumberOfFibers>");
		outList.add("0");
		outList.add("<BST>");
		outList.add("200");
		outList.add("<Smoothness>");
		outList.add("0.2");
		outList.add("<MinLength>");
		outList.add("10");
		outList.add("<MaxLength>");
		outList.add("200");
		outList.add("<FAThreshold>");
		outList.add("0.3");
		outList.add("<TimeStep>");
		outList.add("2");
		outList.add("<UseTriLinearInterpolation>");
		outList.add("1");
		outList.add("<Sampling>");
		outList.add("1");
		outList.add("<AffineTransform>");
		outList.add("1 0 0 0 1 0 0 0 1 0 0 0");
		outList.add("<FlipAxes>");
		outList.add("0 0 0");
		DicccolUtilIO.writeArrayListToFile(outList, dtsFileName);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateDTS mainHandler = new GenerateDTS();
		try {
			mainHandler.dispatch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
