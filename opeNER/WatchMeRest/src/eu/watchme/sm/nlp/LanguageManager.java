package eu.watchme.sm.nlp;

import eu.watchme.sm.helpers.GeneralConfigs;
import eu.watchme.sm.helpers.GeneralUtils;
import eu.watchme.sm.models.KAF;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
	Logger logger = LoggerFactory.getLogger(LanguageManager.class);

	private GeneralUtils utils = null;
	private GeneralConfigs configs = null;

	private LanguageProcessor de_processor = null;
	private LanguageProcessor en_processor = null;
	private LanguageProcessor nl_processor = null;

	public LanguageManager(String parent) {
		configs = new GeneralConfigs(parent);
		utils = new GeneralUtils(parent);
		de_processor = new LanguageProcessor("de", parent);
		en_processor = new LanguageProcessor("en", parent);
		nl_processor = new LanguageProcessor("nl", parent);
	}

	/**
	 * @param text : text required for processing
//	 * @param parent : Parent directory of the file (use servletContext getRealPath)
	 * @return Creates appropriate processor depending on the language of the text
	 */
	public LanguageProcessor loadLanguageProcessor(String text) {
		String encodedText = "";
		try {
			text = URLDecoder.decode(text, "UTF-8");
			text = StringEscapeUtils.unescapeHtml4(text);
			text = text.replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>","");
			text = text.replaceAll("\\s{2,8}?", " ");
			encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String urlParameters = "input="+encodedText+"";
		String langKaf = "";
		try {
			logger.info("called language detector");
			langKaf = (utils.sendPost(configs.getLanguageIdentifierUrl(), urlParameters));
		} catch (Exception e) {
			logger.error("Could not detect language for  {}", text);
		}
		KAF kaf = utils.loadKAF(langKaf);
		logger.info("text: \"{}\"", langKaf);
		String lang = kaf.getLang();
		logger.info("language: \"{}\"", lang);
		LanguageProcessor processor;
		switch(lang) {
		case "en":
			processor = en_processor;
			break;
		case "nl":
			processor = nl_processor;
			break;
		case "de":
			processor = de_processor;
			break;
		default:
			processor = en_processor;
			break;
		}
		if(processor!=null)
			processor.createProcessor(text);
		return processor;
	}
}
