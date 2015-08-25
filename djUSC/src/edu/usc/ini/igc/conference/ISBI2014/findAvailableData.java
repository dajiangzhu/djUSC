package edu.usc.ini.igc.conference.ISBI2014;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uga.DICCCOL.DicccolUtilIO;


public class findAvailableData {
	
	private boolean findTarget(String strPre, List<String> subList)
	{
		for(int i=0;i<subList.size();i++)
		{
			String strCurrentPart = subList.get(i).split("_")[0].trim();
			if(strCurrentPart.equals(strPre))
				return true;
		}
		return false;
	}
	
	public void findData()
	{
		List<String> oriSubList = DicccolUtilIO.loadFileToArrayList("DataName_List_Twin_DTI105_Good.txt");
		List<String> finalSubPreList = new ArrayList<String>();
		List<String> finalSubFullList = new ArrayList<String>();
		System.out.println("Removing sub-448:"+oriSubList.get(448));
		oriSubList.remove(448);
		System.out.println("Removing sub-521:"+oriSubList.get(521));
		oriSubList.remove(521);
		System.out.println("Removing sub-522:"+oriSubList.get(522));
		oriSubList.remove(522);
		Set<String> oriSubPreSet = new HashSet<String>();
		
		for(int i=0;i<oriSubList.size();i++)
		{
			String strCurrentPre = oriSubList.get(i).substring(0, 6);
			oriSubPreSet.add(strCurrentPre.trim());
		}
		List<String> uniSubPreList = new ArrayList<String>(oriSubPreSet);
		System.out.println("#############There are totally "+uniSubPreList.size()+" pairs of twins before screening!");
		
		for(int i=0;i<uniSubPreList.size();i++)
		{
			if( this.findTarget(uniSubPreList.get(i)+"1", oriSubList)&&this.findTarget(uniSubPreList.get(i)+"2", oriSubList) )
				finalSubPreList.add(uniSubPreList.get(i));
		}
		System.out.println("#############There are totally "+finalSubPreList.size()+" pairs of twins after screening!");
		
		List<String> outUniPreList = new ArrayList<String>();
		for(int i=800310;i<894920;i++)
			if(finalSubPreList.contains(String.valueOf(i)))
				outUniPreList.add(String.valueOf(i));
		DicccolUtilIO.writeArrayListToFile(outUniPreList, "DataNamePre_List_Twin_DTI105_Final.txt");
		
//		for(int i=0;i<oriSubList.size();i++)
//		{
//			String strCurrentPre = oriSubList.get(i).substring(0, 6);
//			if(finalSubPreList.contains(strCurrentPre))
//				finalSubFullList.add(oriSubList.get(i));
//		}
//		DicccolUtilIO.writeArrayListToFile(finalSubFullList, "DataName_List_Twin_DTI105_Final.txt");
	}

	public static void main(String[] args) {
		findAvailableData mainHandler = new findAvailableData();
		mainHandler.findData();
	}

}
