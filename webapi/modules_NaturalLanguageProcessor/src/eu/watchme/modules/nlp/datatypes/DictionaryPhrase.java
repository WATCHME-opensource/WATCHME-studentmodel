package eu.watchme.modules.nlp.datatypes;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class DictionaryPhrase {
	private List<String> identifiers = new ArrayList<>();
	private String phrase;
	private List<String> tags= new ArrayList<>();
	private String type;

	public  List<String> getIdentifiers() {
		return identifiers;
	}

	public String getPhrase() {
		return phrase;
	}

	public  List<String> getTags() {
		return tags;
	}

	public String getType() {
		return type;
	}

	public void setIdentifiers(List<String> identifierList) {
		this.identifiers = identifierList;
	}

	public void setPhrase(String word) {
		this.phrase = word;
	}

	public void setTags(List<String> tagList) {
		this.tags = tagList;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "{\"phrase\":\"" + phrase + "\", \"type\":\"" + type + "\", \"tags\":" + tags.toString() + ", \"identifiers\":" + identifiers.toString() + "}";
	}
}
