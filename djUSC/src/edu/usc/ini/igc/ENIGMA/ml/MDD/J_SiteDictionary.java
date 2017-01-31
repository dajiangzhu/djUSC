package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uga.DICCCOL.DicccolUtilIO;

public class J_SiteDictionary {

	List<String> SiteDic = new ArrayList<String>();
	Map<String, String> Site2Code = new HashMap<String, String>();
	Map<String, String> Code2Site = new HashMap<String, String>();

	public J_SiteDictionary() {
		SiteDic = DicccolUtilIO.loadFileToArrayList("J_SitesDictionary.txt");
		for (String line : SiteDic) {
			String[] strRow = line.split("\\s+");
			Code2Site.put(strRow[0].trim(), strRow[1].trim());
			Site2Code.put(strRow[1].trim(), strRow[0].trim());
		}
	}

	public String getSiteFromCode(String code) {
		return Code2Site.get(code);
	}

	public String getCodeFromSite(String site) {
		return Site2Code.get(site);
	}

}
