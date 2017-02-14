package eu.watchme.sm.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageDictionary {
    private String lang;
    private String name;
    private List<DictionaryPhrase> phrases = new ArrayList<>();

    public LanguageDictionary(String l, String n) {
        setName(n);
        setLang(l);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhrases(ArrayList<DictionaryPhrase> phrases) {
        this.phrases = phrases;
    }

    public String getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }

    public void addPhrase(DictionaryPhrase word){
	phrases.add(word);
    }

    public List<DictionaryPhrase> getPhrases() {
	return phrases;
    }

    public DictionaryPhrase matchedIdentifier(List<String> search) {
        for(DictionaryPhrase phrase: phrases)
            if(search.equals(phrase.getIdentifiers()))
                return phrase;
        return null;
    }

    @Override
    public String toString() {
	return "{ \"phrases\":" + phrases + ", \"lang\":\"" + lang
		+ "\", \"name\":\"" + name + "\"}";
    }
}
