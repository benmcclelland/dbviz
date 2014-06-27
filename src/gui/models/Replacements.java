package gui.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Replacements implements Serializable {

	private static final long serialVersionUID = 9040963671452880156L;
	
	private ArrayList<String> comps;
	private ArrayList<String> reps;
	
	public Replacements() {
		comps = new ArrayList<String>();
		reps = new ArrayList<String>();
	}
	
	public void addReplacement(String toRep, String repWith) {
		comps.add(toRep);
		reps.add(repWith);
	}
	
	public String checkString(String s) {
		
		Pattern p = null;
		Matcher m = null;
		int start = 0;
		int end = 0;

		for (int i=0; i<comps.size(); i++) {
			try {
				p = Pattern.compile(comps.get(i));
			} catch (PatternSyntaxException pse) {
//				throw new PatternSyntaxException("Invalid search pattern", comps.get(i), i);
				;		//don't do anything here
			}
			m = p.matcher(s);
			while (m.find()) {
				start = m.start();
				end = m.end();
				if (start == 0 && end == s.length()) {
					s = reps.get(i);
				} else if (start == 0) {
					s = reps.get(i) + s.substring(end);
				} else if (end == s.length()) {
					s = s.substring(0, start) + reps.get(i);
				} else {
					s = s.substring(0, start) + reps.get(i) + s.substring(end);
				}
			}
		}
		return s; 
	}
	
	public void setComps(ArrayList<String> al) {
		comps = al;
	}
	
	public void setReps(ArrayList<String> al) {
		reps = al;
	}

	public int size() {
		return comps.size();
	}
	
}
