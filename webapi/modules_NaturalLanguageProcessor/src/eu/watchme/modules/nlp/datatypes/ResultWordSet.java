/**
 *
 */
 package eu.watchme.modules.nlp.datatypes;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("ALL")
public class ResultWordSet {
	@SuppressWarnings("WeakerAccess")
	String dictionary_name;
	@SuppressWarnings("WeakerAccess")
	@SerializedName(value="element")
	DictionaryPhrase phrase;
    @SerializedName(value="pos")
	String pos ="0";
	@SuppressWarnings("unused")
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

	@SuppressWarnings("unused")
	public void setDictionary_name(String dictionary_name) {
		this.dictionary_name = dictionary_name;
	}

	@SuppressWarnings("unused")
	public void setPhrase(DictionaryPhrase word) {
		this.phrase = word;
	}

	@Override
	public String toString() {
		return "{ \"element\":" + phrase+ ", \"pos\":" + pos + ", \"dictionary_name\":\""
				+  dictionary_name + "\"}";
	}
}
