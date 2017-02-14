package eu.watchme.sm.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * @author isrlab
 * Provides General Shared configuration variables
 */
public class GeneralConfigs {
	@Context ServletContext context;
	public String ContextVar;
	public  String LanguageFolder;
	public String LanguageIdentifierUrl ;
	public  int MaxGram ;
	public String TaggerUrl;
	public String TokenizerUrl;
	public String UserAgent ;

	public GeneralConfigs(String parent) {
		Properties prop = new Properties();
		String propFileName = parent+"/config.properties";
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(propFileName));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}



		// get the property value and print it out
		setLanguageFolder(prop.getProperty("LanguageFolder").trim());
		setLanguageIdentifierUrl(prop.getProperty("LanguageIdentifierUrl").trim());
		setMaxGram(Integer.parseInt(prop.getProperty("MaxGram").trim()));
		setTaggerUrl(prop.getProperty("TaggerUrl").trim());
		setTokenizerUrl(prop.getProperty("TokenizerUrl").trim());
		setUserAgent(prop.getProperty("UserAgent").trim());
		setContextVar(prop.getProperty("ContextVar").trim());

	}
	/**
	 * @return the contextVar
	 */
	public String getContextVar() {
		return ContextVar;
	}
	/**
	 * @return the LanguageFolder
	 */
	public String getLanguageFolder() {
		return LanguageFolder;
	}
	/**
	 * @return the LanguageIdentifierUrl
	 */
	public String getLanguageIdentifierUrl() {
		return LanguageIdentifierUrl;
	}
	/**
	 * @return the MaxGram
	 */
	public int getMaxGram() {
		return MaxGram;
	}
	/**
	 * @return the TaggerUrl
	 */
	public String getTaggerUrl() {
		return TaggerUrl;
	}
	/**
	 * @return the TokenizerUrl
	 */
	public String getTokenizerUrl() {
		return TokenizerUrl;
	}
	/**
	 * @return the UserAgent
	 */
	public String getUserAgent() {
		return UserAgent;
	}
	/**
	 * @param contextVar the contextVar to set
	 */
	public void setContextVar(String contextVar) {
		ContextVar = contextVar;
	}
	/**
	 * @param LanguageFolder the LanguageFolder to set
	 */
	public void setLanguageFolder(String LanguageFolder) {
		this.LanguageFolder = LanguageFolder;
	}
	/**
	 * @param LanguageIdentifierUrl the LanguageIdentifierUrl to set
	 */
	public void setLanguageIdentifierUrl(String LanguageIdentifierUrl) {
		this.LanguageIdentifierUrl = LanguageIdentifierUrl;
	}
	/**
	 * @param MaxGram the MaxGram to set
	 */
	public void setMaxGram(int MaxGram) {
		this.MaxGram = MaxGram;
	}
	/**
	 * @param TaggerUrl the TaggerUrl to set
	 */
	public void setTaggerUrl(String TaggerUrl) {
		this.TaggerUrl = TaggerUrl;
	}
	/**
	 * @param TokenizerUrl the TokenizerUrl to set
	 */
	public void setTokenizerUrl(String TokenizerUrl) {
		this.TokenizerUrl = TokenizerUrl;
	}
	/**
	 * @param UserAgent the UserAgent to set
	 */
	public void setUserAgent(String UserAgent) {
		this.UserAgent = UserAgent;
	}
}
