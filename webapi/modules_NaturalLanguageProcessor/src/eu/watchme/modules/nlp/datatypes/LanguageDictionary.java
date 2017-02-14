package eu.watchme.modules.nlp.datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public class LanguageDictionary {
	private String lang;
	private String name;
	private List<DictionaryPhrase> phrases = new ArrayList<>();

	private LanguageDictionary(String l, String n) {
		setName(n);
		setLang(l);
	}

	@SuppressWarnings("unused")
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

	public ArrayList<DictionaryPhrase> loadDictionaryFile(String path){
		return new ArrayList<>();
	}

	public DictionaryPhrase matchedIdentifier(List<String> search){

		for(DictionaryPhrase phrase: phrases)	{

			if(search.equals(phrase.getIdentifiers())){
				return phrase;
			}
		}
		return null;
	}

	public DictionaryPhrase matchedIdentifier(String text){
		List<String> search = Arrays.asList(text.trim().split(" "));
		for(DictionaryPhrase phrase: phrases)	{

			if(search.equals(phrase.getIdentifiers())){
				return phrase;
			}
		}
		return null;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	private void setName(String name) {
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
