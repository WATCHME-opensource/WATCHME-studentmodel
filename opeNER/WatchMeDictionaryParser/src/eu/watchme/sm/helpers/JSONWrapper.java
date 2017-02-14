/**
 *
 */
package eu.watchme.sm.helpers;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JSONWrapper {
	private Logger logger = LoggerFactory.getLogger(JSONWrapper.class);

    public void saveJSON(String path, String content) {
		try {
			JSONObject json =  new JSONObject(content.replace("'", "\u0027")); /* Convert apostrophes to unicode 'Right single quotation mark' character to avoid escaping the json string */
			FileOutputStream out = new FileOutputStream(path);
			out.write(json.toString(1).getBytes(StandardCharsets.UTF_8));
			out.close();
		} catch (IOException e) {
			logger.error("IOException {} in saveJsonDictionary -  text = {} ", e.getMessage(), content);
		} catch (JSONException e) {
			logger.error("JSONException {} in saveJsonDictionary -  text = {} ", e.getMessage(), content);
		}
    }
}
