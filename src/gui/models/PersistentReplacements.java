package gui.models;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

public class PersistentReplacements extends Persistent {
    
    private File file;
    private final String filePath = "configs/replacementsConfig.json";
    private ArrayList<String> keys;
    private ArrayList<String> values;
    
    public PersistentReplacements() throws IOException {
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
    }
    
    @SuppressWarnings("unchecked")
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
    
    public Replacements getReplacements() {
    	
    	if (Math.min(keys.size(), values.size()) <= 0) {
    		return null;
    	}
    	
    	Replacements reps = new Replacements();
    	
    	for (int i=0; i<Math.min(keys.size(), values.size()); i++) {
    		reps.addReplacement(keys.get(i), values.get(i));
    	}
    	
    	return reps;
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

	public List<String> getKeys() {
		return keys;
	}

	public List<String> getValues() {
		return values;
	}
	
	public void setKeys(List<String> array) {
		keys = (ArrayList<String>) array;
	}
	
	public void setValues(List<String> array) {
		values = (ArrayList<String>) array;
	}
}
