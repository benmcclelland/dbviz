package gui.models;

import gui.models.Line.PointStyle;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sf.json.JSONObject;

//TODO: handle bar graph case
/**
 * Persistently maintains colors for data lines
 */
public class PersistentColors extends Persistent {

    private File file;
    private final String filePath = "configs/colorsConfig.json";
    private ArrayList<String> keys;
    private ArrayList<String> values;
    private ArrayList<Pair> pairs;
    
    public PersistentColors() throws IOException {
        file = new File(filePath);
        keys = new ArrayList<String>();
        values = new ArrayList<String>();
        if (!file.exists()) {
			File parentFile = new File(file.getParent());
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			file.createNewFile();
		} else {
        	parse();
        }
        init();
    }
    
    private void init() {

		List<Color> colors = new ArrayList<Color>();
		colors.add(new Color(255, 0, 0));
		colors.add(new Color(0, 0, 255));
		colors.add(new Color(0, 255, 0));
		colors.add(new Color(0, 255, 255));
		colors.add(new Color(255, 0, 255));
		colors.add(new Color(255, 255, 0));
		colors.add(new Color(0, 0, 128));
		colors.add(new Color(0, 128, 0));
		colors.add(new Color(128, 0, 0));
		colors.add(new Color(0, 128, 128));
		colors.add(new Color(128, 0, 128));
		colors.add(new Color(128, 128, 0));
		
		PointStyle[] styles = PointStyle.values();
		pairs = new ArrayList<Pair>();
		for (int i=0; i<styles.length; i++) {
			for (int j=0; j<colors.size(); j++) {
				pairs.add(new Pair(colors.get(j), styles[i]));
			}
		}
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject parse() throws IOException {

        BufferedReader br = null;
        String temp = null;
        String full = "";
        br = new BufferedReader(new FileReader(file));
        while ((temp = br.readLine()) != null) {
        	full = full.concat(temp);
        }
        if (full.length() >= 0 && full.startsWith("{")) {
        	JSONObject json = JSONObject.fromObject(full);
        	Iterator<String> it = (Iterator<String>) json.keys();
        	String key = null;
        	while (it.hasNext()) {
        		key = (String) it.next();
        		keys.add(key);
        		values.add((String) json.get(key));
        	}
        }
        return null;
	}
	
	/*
	 * pass in all lines from the model
	 * for each line
	 * 	check to see if it's key is in the config
	 * 	if it is
	 * 		assign the corresponding color to it
	 * 	if not
	 * 		keep track of this line
	 * for each unassigned line
	 * 	assign an available color to it
	 */
	public List<Line> checkLines(List<Line> lines) {
		
		Line line = null;
		List<Line> assigned = new ArrayList<Line>();
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			
			for (int j=0; j<keys.size(); j++) {
				if (line.getCompares().equals(keys.get(j))) {
					Color c = parseColor(values.get(j));					//parse the color
					line.setColor(c);										//set the color
					PointStyle ps = parsePointStyle(values.get(j));
					line.setPointStyle(ps);		//set the point style
					int index = pairs.indexOf(new Pair(c,ps));
					pairs.remove(index);
					assigned.add(line);										//add it to the assigned list
					
					/* Add this key-value pair to the top of the list since it was used again */
					if (j!=0) {
						String key = keys.get(j);
						String value = values.get(j);
						keys.remove(j);
						values.remove(j);
						keys.add(0, key);
						values.add(0, value);
					}
				}
			}
		}
		
		/* At this point all of the remaining lines in "lines" should not have colors assigned by this method */
		Random r = new Random();
		for (int i=0; i<lines.size(); i++) {
			line = lines.get(i);
			if (assigned.indexOf(line) < 0) {
				Pair p = pairs.get(r.nextInt(pairs.size()));
				pairs.remove(p);
				line.setColor(p.getColor());
				line.setPointStyle(p.getPointStyle());
				assigned.add(line);
				
				/* add this key-value pair to the bottom of the list */
				if (keys.size()<pairs.size()) {
					keys.add(line.getCompares());
					Color c = line.getColor();
					values.add(c.getRed()+","+c.getGreen()+","+c.getBlue()+","+line.getPointStyle().toString());
				}
			}
		}
		try {
			writeOutJson();
		} catch (IOException ioe) {
			;//do nothing
		}
		return assigned;
	}
	
	private Color parseColor(String s) {
		String[] tokens = s.split(",");
		if (tokens!=null && tokens.length>=3) {
			try {
				return new Color(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
			} catch (NumberFormatException nfe) {
				return new Color(255,0,0);
			}
		} else {
			return new Color(255,0,0);
		}
	}
	
	private PointStyle parsePointStyle(String s) {
		String[] tokens = s.split(",");
		if (tokens!=null && tokens.length>2) {
			try {
				return PointStyle.valueOf(tokens[3]);
			} catch (IllegalArgumentException iae) {
				return PointStyle.CIRCLE;
			} catch (NullPointerException npe) {
				return PointStyle.CIRCLE;
			}
		} else {
			return PointStyle.CIRCLE;
		}
	}
	
	public void writeOutJson() throws IOException {
		
		JSONObject json = new JSONObject();
		
		for (int i=0; i<Math.min(keys.size(), values.size()); i++) {
			json.put(keys.get(i), values.get(i));
		}

		FileWriter fw = null;
		fw = new FileWriter(filePath);
		json.write(fw);
		fw.flush();
	}
	
	class Pair {
		private Color c;
		private PointStyle ps;
		
		public Pair (Color color, PointStyle p) {
			c = color;
			ps = p;
		}
		
		public Color getColor() {
			return c;
		}
		
		public PointStyle getPointStyle() {
			return ps;
		}
		
		public int hashCode() {
			return c.hashCode() ^ ps.hashCode();
		}
		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof Pair)) return false;
			Pair p = (Pair) o;
			return c.equals(p.getColor()) && ps.equals(p.getPointStyle());
		}
	}

}
