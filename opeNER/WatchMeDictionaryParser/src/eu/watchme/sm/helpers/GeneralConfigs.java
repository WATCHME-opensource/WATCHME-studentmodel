package eu.watchme.sm.helpers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class GeneralConfigs {
    public String ContextVar;
    public  String LanguageFolder;
    public String LanguageIdentifierUrl ;
    public  int MaxGram ;
    public  String ProcessedLanguageFolder;
    public String TaggerUrl;
    public String TokenizerUrl;
    public String UserAgent ;

    public GeneralConfigs(String parent) {
	Properties prop = new Properties();
	String propFileName = "config.properties";
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
	setProcessedLanguageFolder(prop.getProperty("ProcessedLanguageFolder").trim());
	setLanguageIdentifierUrl(prop.getProperty("LanguageIdentifierUrl").trim());
	setMaxGram(Integer.parseInt(prop.getProperty("MaxGram").trim()));
	setTaggerUrl(prop.getProperty("TaggerUrl").trim());
	setTokenizerUrl(prop.getProperty("TokenizerUrl").trim());
	setUserAgent(prop.getProperty("UserAgent").trim());
    }
    public String getContextVar() {
	return ContextVar;
    }

    public String getLanguageFolder() {
	return LanguageFolder;
    }

    public String getLanguageIdentifierUrl() {
	return LanguageIdentifierUrl;
    }

    public int getMaxGram() {
	return MaxGram;
    }

    public String getProcessedLanguageFolder() {
	return ProcessedLanguageFolder;
    }

    public String getTaggerUrl() {
	return TaggerUrl;
    }

    public String getTokenizerUrl() {
	return TokenizerUrl;
    }

    public String getUserAgent() {
	return UserAgent;
    }

    public void setContextVar(String contextVar) {
	ContextVar = contextVar;
    }

    public void setLanguageFolder(String LanguageFolder) {
	this.LanguageFolder = LanguageFolder;
    }

    public void setLanguageIdentifierUrl(String LanguageIdentifierUrl) {
	this.LanguageIdentifierUrl = LanguageIdentifierUrl;
    }

    public void setMaxGram(int MaxGram) {
	this.MaxGram = MaxGram;
    }

    public void setProcessedLanguageFolder(String processedLanguageFolder) {
	this.ProcessedLanguageFolder = processedLanguageFolder;
    }

    public void setTaggerUrl(String TaggerUrl) {
	this.TaggerUrl = TaggerUrl;
    }

    public void setTokenizerUrl(String TokenizerUrl) {
	this.TokenizerUrl = TokenizerUrl;
    }

    public void setUserAgent(String UserAgent) {
	this.UserAgent = UserAgent;
    }
}
