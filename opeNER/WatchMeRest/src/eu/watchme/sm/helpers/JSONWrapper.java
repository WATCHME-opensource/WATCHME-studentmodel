/**
 *
 */
package eu.watchme.sm.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONWrapper {
	private Logger logger = LoggerFactory.getLogger(JSONWrapper.class);

	private JSONObject json_object = null;
	private JSONArray json_array = null;

	public JSONWrapper() {
		json_object = new JSONObject();
	}
	public JSONWrapper(JSONObject json) {
		json_object = json;
	}
	public JSONWrapper(String json) {
		try {
			json_array = new JSONArray(json.replaceAll("\\s?'\\s?", "\u2019")); /* Escape apostrophes */
		} catch (JSONException e) {
			logger.error("JSONException {} \n response {} \n json {}", e.getMessage(), json);
		}
	}
	public void addElement(String name, Object value) {
		try{
			json_object.put(name, value);
		} catch (JSONException e) {
			logger.error("JSONException {} \n response {} \n name {} value {}",e.getMessage(),name,value.toString());
		}
	}

	public String getString() {
		try {
			if(json_object!=null)
				return json_object.toString(1);
			if(json_array!=null)
				return json_array.toString(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	public void saveJSON(String path) {
		String content="";
		try {
			content = json_object.toString(1);
			FileOutputStream out = new FileOutputStream(path);
			out.write(content.getBytes(StandardCharsets.UTF_8));
			out.close();
		} catch (IOException e) {
			logger.error("IOException {} in saveJsonDictionary -  text = {} ", e.getMessage(), content);
		} catch (JSONException e) {
			logger.error("JSONException {} in saveJsonDictionary -  text = {} ", e.getMessage(), content);
		}
	}
}
