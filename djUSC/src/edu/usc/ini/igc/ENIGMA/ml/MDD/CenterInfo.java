package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

public class CenterInfo {

	String centerName = "";
	int numOfSub = -1;
	String[] oriSubIDList = null;
	double[][] oriCovariates = null; //Dx, Age, Sex
	String[][] oriData = null;
	List<Integer> subRemoveList = new ArrayList<Integer>();


	public CenterInfo(String centerName, int numOfSub,String[] oriSubIDList, String[][] oriData, double[][] oriCovariates ) {
		super();
		this.centerName = centerName;
		this.numOfSub = numOfSub;
		this.oriSubIDList = oriSubIDList;
		this.oriData = oriData;
		this.oriCovariates = oriCovariates;
	}
}
