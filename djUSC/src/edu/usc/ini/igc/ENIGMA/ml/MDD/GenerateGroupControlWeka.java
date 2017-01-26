/**
 * 
 */
package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;

/**
 * @author djzhu
 *
 */
public class GenerateGroupControlWeka {
	
	public void generateWeka(int fold, String wekaFileAll)
	{
		List<String> wekaAllList = DicccolUtilIO.loadFileToArrayList(wekaFileAll);
		List<String> headList = new ArrayList<String>();
		List<String> patientList = new ArrayList<String>();
		List<String> healthList = new ArrayList<String>();
		for(int i=0;i<wekaAllList.size();i++)
		{
			String currentLine = wekaAllList.get(i);
			if( currentLine.startsWith("@") )
				headList.add(currentLine);
			else
			{
				String[] lineArray = currentLine.split(",");
				if( lineArray[ lineArray.length-1 ].trim().equals("1") )
					patientList.add(currentLine);
				if( lineArray[ lineArray.length-1 ].trim().equals("0") )
					healthList.add(currentLine);
				System.out.println("################   patientList.size(): "+patientList.size()+"           healthList.size(): "+healthList.size());
			} //else
		} //for i
		
		List<String> dataWekaList = new ArrayList<String>();
		for(int i=0;i<fold;i++)
		{
			dataWekaList.clear();
			dataWekaList.addAll(headList);
			dataWekaList.addAll(patientList);
			List<Integer> randomList = DicccolUtil.geneRandom(patientList.size(), healthList.size());
			for(int r=0;r<randomList.size();r++)
				dataWekaList.add( healthList.get( randomList.get(r)-1 ) );
			DicccolUtilIO.writeArrayListToFile(dataWekaList,"DataWekaList_AllFeature_ICV_GroupControl_"+i+".arff");			
		} //for
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int fold = 10;
		String wekaFileAll = "DataWekaList_Lasso_AllFeature_ICV.arff";
		GenerateGroupControlWeka mainHandler = new GenerateGroupControlWeka();
		mainHandler.generateWeka(fold, wekaFileAll);

	}

}
