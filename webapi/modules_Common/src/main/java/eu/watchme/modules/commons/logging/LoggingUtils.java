package eu.watchme.modules.commons.logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoggingUtils {
	private static Gson sGson;

	static {
		sGson = new GsonBuilder().setPrettyPrinting().create();
	}

	public static String format(Object object) {
		if (object != null) {
			try {
				return sGson.toJson(object);
			} catch (Exception e) {
				return String.format("Could not deserialize type %s. Error message: %s", object.getClass().getSimpleName(), e.getMessage());
			}
		}
		return "";
	}
}
