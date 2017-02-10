package edu.usc.ini.igc.conference.MICCAI2017;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.Lasso.LassoFit;
import edu.uga.DICCCOL.Lasso.LassoFitGenerator;

public class SiteProfile {
	
	public String lassoInputFile = "";
	public int subNum = 0;
	public int feaNum = 0;
	public float[][] OriObservations;
	public List<Integer> labelList = new ArrayList<Integer>();
	public LassoFitGenerator lassoFitGenerator = null;
	public LassoFit lassoFit = null;
	public boolean hasImproved = true;
	public List<double[]> improvmentHistory = new ArrayList<double[]>();
	public List<List<Integer>> selectedFeaturesHistory = new ArrayList<List<Integer>>();
	
	public void copyObvservition()
	{
		OriObservations = new float[this.feaNum][];
		for (int t = 0; t < feaNum; t++) {
			OriObservations[t] = new float[this.subNum];
		}
		for(int f=0;f<feaNum;f++)
			for(int s=0;s<subNum;s++)
				OriObservations[f][s] = lassoFitGenerator.observations[f][s];
	}

}
