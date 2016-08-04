package edu.usc.ini.igc.ENIGMA.ml.MDD;

public class CenterInfo {

	String centerName = "";
	int numOfSub = -1;
	String[][] oriData = null;

	public CenterInfo(String centerName, int numOfSub, String[][] oriData) {
		super();
		this.centerName = centerName;
		this.numOfSub = numOfSub;
		this.oriData = oriData;
	}
}
