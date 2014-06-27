package gui.models;

import java.io.FileWriter;
import java.io.IOException;

import net.sf.json.JSONObject;

public abstract class Persistent {
	
	public abstract JSONObject parse() throws IOException;
	public void writeOutJson(JSONObject jobj, String filePath) throws IOException {
		if (jobj != null) {
			FileWriter fw = null;
			fw = new FileWriter(filePath);
			jobj.write(fw);
			fw.flush();
		}
	}

}
