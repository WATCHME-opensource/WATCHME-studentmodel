/**
 *
 */
package eu.watchme.sm.helpers;

import java.util.*;

public final class GeneralNLP {
	public static LinkedHashMap<String,String> nGram(LinkedHashMap<String, String> tokenMap, int maxG, String Separator) {
		LinkedHashMap<String,String>  res = new LinkedHashMap<>();
		if(maxG<1)
			maxG=1;

		LinkedList<String> tokens = new LinkedList<>(tokenMap.keySet());
		LinkedList<String> labels = new LinkedList<>(tokenMap.values());
		if(tokens.size()<maxG)
			maxG = tokens.size();

		for(int i=0; i<tokens.size(); i++){
			int offset = tokens.size()-i;
			if(offset>0) {
				if(offset>maxG)
					offset = maxG;
              res.putAll(combineWords(tokens.subList(i, i+(offset)), labels.subList(i,i+(offset)), Separator));
			} else if((tokens.size())!=i) {
				res.putAll(combineWords(tokens.subList(i, tokens.size()), labels.subList(i, tokens.size()), Separator));
			}
		}
		return (res);
	}

	static LinkedHashMap<String,String> combineWords(List<String> tokens, List<String> labels, String Separator) {
		LinkedHashMap<String, String> res = new LinkedHashMap<>();
		String toks = tokens.get(0).substring(tokens.get(0).indexOf(Separator)+Separator.length());

		String lbls = labels.get(0);
		String prefix = tokens.get(0).substring(0,tokens.get(0).indexOf(Separator));
		res.put(prefix+Separator+toks, lbls);
		for (int i = 1; i < tokens.size(); i++) {
			toks += " " + tokens.get(i).substring(tokens.get(i).indexOf(Separator) + Separator.length());
			lbls += " " + labels.get(i);
			prefix = tokens.get(i).substring(0, tokens.get(i).indexOf(Separator));
			res.put(prefix+Separator+toks.trim(), lbls.trim());
		}
		System.out.println(res.keySet());
		return res;
	}
}
