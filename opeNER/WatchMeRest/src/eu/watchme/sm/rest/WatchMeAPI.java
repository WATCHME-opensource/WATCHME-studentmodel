
package eu.watchme.sm.rest;

import eu.watchme.sm.helpers.GeneralConfigs;
import eu.watchme.sm.helpers.JSONWrapper;
import eu.watchme.sm.nlp.LanguageManager;
import eu.watchme.sm.nlp.LanguageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author isrlab
 * NLP REST endpoint
 * @Path here defines class level path. Identifies the URI path that&nbsp;
 * a resource class will serve requests for.
  */
@Path("LanguageService")
public class WatchMeAPI {
	GeneralConfigs configs = null;
	@Context ServletContext context;
	public LanguageManager lng_mgr = null;
	Logger logger = LoggerFactory.getLogger(WatchMeAPI.class);
	public WatchMeAPI(@Context ServletContext context) {
		configs = new GeneralConfigs(context.getRealPath("/WEB-INF"));
		System.out.println("hello = "+configs.getContextVar());
		lng_mgr = (LanguageManager)context.getAttribute(configs.getContextVar());
	}

	@POST
	@Path("/process")
	@Produces(MediaType.APPLICATION_JSON)
	public String processText(@FormParam("text") String text) {
		String parent = context.getRealPath("/WEB-INF");
		String resp = "";
		if(!text.isEmpty()) {
			LanguageProcessor processor = lng_mgr.loadLanguageProcessor(text);
			if (processor != null) {
				logger.info("Dictionary loaded? " + processor.isDictionaryLoaded());
				processor.process(); // Process Tokens and POS tags
				JSONWrapper json_pos_result = new JSONWrapper(processor.getResultJson());
				resp = json_pos_result.getString();
				logger.info("Successful Request for {}, language {} ", processor.getText(), processor.getLang());
				JSONWrapper json_dictionary_result = new JSONWrapper(processor.dictionaryLookUp().toString());
				logger.info("Undetected words {}, {}  ", parent, processor.getUndetected());
				JSONWrapper json_unattached_result = new JSONWrapper();
				json_unattached_result.addElement("undetected-text", processor.getUndetected());
				json_unattached_result.addElement("detected-text", processor.getDetected());
				json_unattached_result.saveJSON(parent + "/test.json");
				resp = json_dictionary_result.getString();
			}
		}
		return resp;
	}
}
