package eu.watchme.sm.datatypes;

import java.util.ArrayList;
import java.util.List;

public class DictionaryPhrase {
	private List<String> identifiers = new ArrayList<String>();
	private String phrase;
	private List<String> tags= new ArrayList<String>();
	private String type;


	public  List<String> getIdentifiers() {
		return identifiers;
	}

	public  List<String> getLowerCaseIdentifiers() {
		List<String> newIdentifiers = new ArrayList<String>();
		for(String id:identifiers){
			newIdentifiers.add(id.toLowerCase());
		}
		return newIdentifiers;
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
