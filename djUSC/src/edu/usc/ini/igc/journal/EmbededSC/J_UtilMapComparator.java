package edu.usc.ini.igc.journal.EmbededSC;

import java.util.Comparator;
import java.util.Map;

public class J_UtilMapComparator implements Comparator<String> {
	private boolean ascending;
	private Map<String, Double> map;

	public J_UtilMapComparator(Map<String, Double> map, boolean ascending) {
		this.ascending = ascending;
		this.map = map;
	}

	public int compare(String s1, String s2) {
		Double x = map.get(s1);
		Double y = map.get(s2);
		if (x.equals(y)) {
			if (this.ascending)
				return s1.compareTo(s2);
			else
				return s2.compareTo(s1);
		}
		if (this.ascending)
			return x.compareTo(y);
		else
			return y.compareTo(x);
	}

}
