package eu.watchme.sm.dictionary;

import eu.watchme.sm.nlp.LanguageProcessor;

import java.io.IOException;

public class ParseDictionary {
    public static void main(String[] args) {
	String lang = "";
	if(args.length>0) {
	    lang = args[0];
	} else {
	    lang="en";
	}
	System.out.println("Ready to start - parser for \""+lang+"\"?");
	try {
	    System.in.read();
	    new LanguageProcessor(lang.trim(),"");
	} catch (IOException e) {
	    e.printStackTrace();
	}
	System.out.println("FINISHED - parser for \""+lang+"\"");
    }
}
