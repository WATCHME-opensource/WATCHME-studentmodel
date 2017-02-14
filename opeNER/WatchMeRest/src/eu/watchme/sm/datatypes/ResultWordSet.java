/**
 *
 */
package eu.watchme.sm.datatypes;

public class ResultWordSet {
	String dictionary_name;
	DictionaryPhrase phrase;
	String pos ="0";
	public String getDictionary_name() {
		return dictionary_name;
	}

	public DictionaryPhrase getPhrase() {
		return phrase;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}

	public void setDictionary_name(String dictionary_name) {
		this.dictionary_name = dictionary_name;
	}

	public void setPhrase(DictionaryPhrase word) {
		this.phrase = word;
	}


	@Override
	public String toString() {
		return "{ \"element\":" + phrase+ ", \"pos\":" + pos + ", \"dictionary_name\":\""
				 +  dictionary_name + "\"}";
	}
}
