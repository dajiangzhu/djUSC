package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;

public class GenerateFreeSurferLabel {

	public static void main(String[] args) {
		
		String headSide = "R";
		String headStr = "rh";
		
		List<String> LabelDicList = DicccolUtilIO.loadFileToArrayList("aparc.annot.ctab");
		List<String> ROIList = DicccolUtilIO.loadFileToArrayList("features0831.txt");
		
		List<String> outList = new ArrayList<String>();
		outList.add( LabelDicList.get(0) );
		int count = 1;
		
		String strCmd = "mris_label2annot --s bert --h "+headStr+" --l "+headStr+".unknown.label";
		for(int i=0;i<ROIList.size();i++)
		{
			if(ROIList.get(i).startsWith(headSide))
			{
				String currentROI = ROIList.get(i).split("\\s+")[0].split("_")[1].trim();
				for(int l=0;l<LabelDicList.size();l++)
				{
					String[] currentLabelLine = LabelDicList.get(l).split("\\s+");
					String currentLabel = currentLabelLine[2].trim();
					if(currentROI.equals(currentLabel))
					{
						String colorR = currentLabelLine[3].trim();
						String colorG = currentLabelLine[4].trim();
						String colorB = currentLabelLine[5].trim();
						String tmpLine = "  "+count+"  "+currentROI+"  "+colorR+"  "+colorG+"  "+colorB+"  0";
						outList.add(tmpLine);
						count++;
						strCmd += " --l "+headStr+"."+currentROI+".label";
						
					} //if
				} //for l
			} //if
		} //for i
		String outCtabName = "sipaim_"+headSide+".annot.ctab";
		DicccolUtilIO.writeArrayListToFile(outList,outCtabName );
		
		strCmd += " --ctab "+outCtabName+" --a mySIPAIM --nhits nhits.mgh";
		List<String> cmdList = new ArrayList<String>();
		cmdList.add(strCmd);
		DicccolUtilIO.writeArrayListToFile(cmdList, "mySIPAIM_CMD_"+headSide+".txt");
		System.out.println(strCmd);

	}

}
