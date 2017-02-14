package eu.watchme.sm.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageDictionary {
	private String name;
	private String lang;

	private List<DictionaryPhrase> phrases = new ArrayList<>();

	public LanguageDictionary(String l, String n) {
		setName(n);
		setLang(l);
	}

	public void addPhrase(DictionaryPhrase word){
		phrases.add(word);
	}

	public String getLang() {
		return lang;
	}

	public String getName() {
		return name;
	}

	public List<DictionaryPhrase> getPhrases() {
		return phrases;
	}

	public DictionaryPhrase matchedIdentifier(String text) {
		List<String> search = Arrays.asList(text.toLowerCase().trim().split(" "));
		for(DictionaryPhrase phrase: phrases)
			if(search.equals(phrase.getLowerCaseIdentifiers()))
				return phrase;
		return null;
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

	@Override
	public String toString() {
		return "{ \"phrases\":" + phrases + ", \"lang\":\"" + lang
				+ "\", \"name\":\"" + name + "\"}";
	}
}
