package eu.watchme.sm.nlp;

import eu.watchme.sm.datatypes.*;
import eu.watchme.sm.helpers.GeneralConfigs;
import eu.watchme.sm.helpers.GeneralNLP;
import eu.watchme.sm.helpers.GeneralUtils;
import eu.watchme.sm.models.KAF;
import eu.watchme.sm.models.KAF.Terms.Term;
import eu.watchme.sm.models.KAF.Terms.Term.Span;
import eu.watchme.sm.models.KAF.Terms.Term.Span.Target;
import eu.watchme.sm.models.KAF.Text;
import eu.watchme.sm.models.KAF.Text.Wf;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author isrlab
 * Language processor collects and processes data from opeNER endpoints
 * There are three format to retrieve the information.
 * 1. use getTokenKaf or getPosKaf to get orginal KAF XML format
 * 2. use getTokensJson or getTermsJson to get CUSTOMISED JSON format look at @link {@link TextTerm} @link {@link TextToken}
 * 3. use getTokensRaw or getTermsRaw to get the object fromat of the data look at @link {@link TextTerm} @link {@link TextToken}
 */
public class LanguageProcessor {
	Logger logger = LoggerFactory.getLogger(LanguageProcessor.class);

	private GeneralUtils utils = null;
	protected GeneralConfigs configs = null;

	protected String lang = "en";

	protected LinkedHashMap<String, LanguageDictionary> languageDictionaries = new LinkedHashMap<>();
	protected List<String> dictionaryNames = new LinkedList<>();

	private boolean dictionaryLoaded;

	protected String encodedText = "";
	protected String posKaf = "";
	protected String tokenKaf = "";

	List<TextToken> tokens = new LinkedList<>();

	protected List<TextTerm> terms = new LinkedList<>();
	private LinkedHashMap<String,Integer> termHistory = new LinkedHashMap<>();

	List<String> detectedGrams = new LinkedList<>();
	List<String> undetectedGrams = new LinkedList<>();

	final private String SEPARATOR_WID = "@@";
	final private String POS_REGEX = "_[\\wäéëêïóöüß!'($.)]*[^\\W\\s]*";
	final private String GRAM_REGEX = "[\\wäéëêïóöüß!'($.)]*[^\\W\\s]*_";

	/**
	 * @author isrlab
	 * Language processor collects and processes data from opeNER endpoints
	 * There are three format to retrieve the information.
	 * 1. use getTokenKaf or getPosKaf to get orginal KAF XML format
	 * 2. use getTokensJson or getTermsJson to get CUSTOMISED JSON format look at @link {@link TextTerm} @link {@link TextToken}
	 * 3. use getTokensRaw or getTermsRaw to get the object fromat of the data look at @link {@link TextTerm} @link {@link TextToken}
	 */
	LanguageProcessor(String l, String parent) {
		//Init new processor
		dictionaryLoaded = false;

		dictionaryNames.clear();
		languageDictionaries.clear();

		if(!l.isEmpty())
			lang = l;

		configs = new GeneralConfigs(parent);
		utils = new GeneralUtils(parent);

		File LangDir = new File(parent+"/"+configs.getLanguageFolder()+l);

		if(LangDir.getAbsoluteFile().exists()) {
			//Load dictionaries here
			File[] listDictionaries = LangDir.listFiles();

			for(File dictionary : listDictionaries) {
				if(dictionary.getName().contains(".csv"))
					continue;
				String name = utils.removeExtension(dictionary.getName());
				String jsonPath = parent+"/"+configs.getLanguageFolder()+l+"/"+name+".json";
				LanguageDictionary tmp = null;
				if((new File(jsonPath)).exists()) {
					tmp = loadJsonDictionary(jsonPath);
					dictionaryLoaded = true;
				}
				dictionaryNames.add(name);
				languageDictionaries.put(name, tmp);
			}
		}
	}

	public void createProcessor(String text) {
		try {
			encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("running language processor lang {} for text {}", getLang(), getText());
	}

	public List<ResultWordSet> dictionaryLookUp() {
		detectedGrams.clear();
		undetectedGrams.clear();
		termHistory.clear();
		if(isDictionaryLoaded()) {
			LinkedHashMap<String,String> grams = GeneralNLP.nGram(getIdentifierString(getTermsRaw()), configs.getMaxGram(), SEPARATOR_WID);
			LinkedList<ResultWordSet> lookupResult = new LinkedList<>();
			LinkedList<String> detectedSentiment = new LinkedList<>();
			for(String dicLang : dictionaryNames) {
				LanguageDictionary tempDict = languageDictionaries.get(dicLang);
				for(String gram : grams.keySet()) {
					gram = gram.substring(gram.indexOf(SEPARATOR_WID) + SEPARATOR_WID.length());
					if(tempDict.matchedIdentifier(gram)!= null) {
						ResultWordSet tmpRes = new ResultWordSet();
						DictionaryPhrase newTmp = tempDict.matchedIdentifier(gram);
						Integer ovPos = getOverallPos(gram.replaceAll(POS_REGEX, "").trim());
						newTmp.setPhrase(gram.replaceAll(POS_REGEX, "").trim());
						tmpRes.setPos(ovPos.toString());
						tmpRes.setPhrase(newTmp);

						tmpRes.setDictionary_name(dicLang);
						detectedSentiment.add(newTmp.getPhrase());

						lookupResult.add(tmpRes);
						if(!detectedGrams.contains(gram.trim()))
							detectedGrams.add(gram.trim());
					}
				}
			}
			LinkedList<SentimentCounter> recommendations = new LinkedList<>();
			for(String gram : grams.values()) {
				if(!detectedGrams.contains(gram.trim())) {
					logger.info("{} and {}", gram, detectedGrams.toString());
					SentimentCounter rec = analyseGram(gram, detectedSentiment);
					if(rec!=null)
						if(!recommendations.contains(rec))
							recommendations.add(rec);
					undetectedGrams.add(gram.replaceAll(POS_REGEX, "").trim());
				}
			}

			Collections.sort(recommendations, new Comparator<SentimentCounter>() {
				@Override
				public int compare(SentimentCounter o1, SentimentCounter o2) {
					return (int)(Math.round((o2.getScore()-o1.getScore())*1000));
				}
			});

			if(recommendations.size()>1) {
				System.out.println(recommendations.subList(0, 1).toString());
				ResultWordSet tmpRes = new ResultWordSet();
				DictionaryPhrase newTmp = new DictionaryPhrase();
				newTmp.setIdentifiers(Arrays.asList(recommendations.subList(0, 1).get(0).getIdentifiers().split(" ")));
				newTmp.setTags(Arrays.asList(recommendations.subList(0, 1).get(0).getTag().split(" ")));

				Integer ovPos = getOverallPos(recommendations.subList(0, 1).get(0).getPhrase());
				newTmp.setType("recommendation");
				newTmp.setPhrase(recommendations.subList(0,1).get(0).getPhrase());
				tmpRes.setPos(ovPos.toString());
				tmpRes.setPhrase(newTmp);

				tmpRes.setDictionary_name("*");
				lookupResult.add(tmpRes);
			}
			return sortResult(uniqueResult(lookupResult));
		}
		return null;
	}

	public SentimentCounter analyseGram(String gram, LinkedList<String> sentimentList) {
		int matchCount = 0;
		double score = 0;

		String gramText = gram.replaceAll(POS_REGEX, "").replaceAll("\\s{1,5}"," ").trim();
		String gramPos = gram.replaceAll(GRAM_REGEX, "").replaceAll("\\s{1,5}"," ").trim();
		for(int i=0; i<sentimentList.size(); i++)
			if(gramText.contains(sentimentList.get(i)))
				matchCount++;
		if(sentimentList.size()>0)
			score = gramText.length()*((double)matchCount/(double)sentimentList.size());
		SentimentCounter tmp = new SentimentCounter(gramText, gramPos, matchCount, score);

		return filterGrams(tmp);
	}

	public SentimentCounter filterGrams(SentimentCounter s){
		String[] tokens = s.getPhrase().split(" ");
		String[] pos = s.getTag().split(" ");
		String newToken = "";
		String newTag = "";
		for(int i=0;i<tokens.length;i++){
			switch(getLang()) {
				case "en":
						newToken += tokens[i] + " ";
						newTag += pos[i] + " ";
					break;
				case "de":
						newToken += tokens[i] + " ";
						newTag += pos[i] + " ";
					break;
				case "nl":
						newToken += tokens[i] + " ";
						newTag += pos[i] + " ";
					break;
			}
		}
		if(newTag.isEmpty())
			return null;
		return new SentimentCounter(newToken.trim(),newTag.trim(),s.getMatchedSentiment(),s.getScore());
	}

	public LinkedList<ResultWordSet> sortResult(LinkedList<ResultWordSet> in){
		for(int i=1;i<in.size(); i++){
			for(int k=0;k<in.size()-1;k++){
				if(Integer.parseInt(in.get(i).getPos())<Integer.parseInt(in.get(k).getPos())){
					ResultWordSet tmp = in.get(i);
					in.set(i,in.get(k));
					in.set(k,tmp);
				}
			}
		}
		return in;
	}

	public LinkedList<ResultWordSet> uniqueResult(LinkedList<ResultWordSet> in) {
		HashMap<HashMap<String,String>,String> lastPos= new HashMap<>();
		LinkedList<ResultWordSet> out = new LinkedList<>();
		for(int i=0;i<in.size();i++) {
			LinkedHashMap<String,String> tmpH = new LinkedHashMap<>();
			tmpH.put(in.get(i).getDictionary_name(), in.get(i).getPos());
			if(!lastPos.containsKey(tmpH)) {
				out.add(in.get(i));
				lastPos.put(tmpH, in.get(i).getPos());
			}
		}
		return out;
	}

	/**
	 * @param tokens
	 * @param wid
	 * @return finds the Token based on its wID value in the KAF data
	 */
	public TextToken findTokenByWID(LinkedList<TextToken> tokens ,String wid) {
		TextToken res =new TextToken();
		for(int i=0; i < tokens.size(); i++)
			if(tokens.get(i).getWid().equals(wid))
				return tokens.get(i);
		return res;
	}

	/**
	 * @return Returns the name of currently loaded dictionaries for the language
	 */
	public List<String> getUndetected() {
		return undetectedGrams;
	}

	public List<String> getDetected() {
		return detectedGrams;
	}

	public  LinkedHashMap<String,String> getIdentifierString(List<TextTerm> terms) {
		LinkedHashMap<String, String> res = new LinkedHashMap<>();
		for(TextTerm term : terms) {
			String morpho = term.getMorphofeat();
			res.put(term.getTargets().get(0).getWid()+ SEPARATOR_WID +term.getLemma() + "_" + morpho, term.getTargets().get(0).getToken().getValue() +"_"+morpho);
		}
		return res;
	}

	/**
	 * @return returns the current language of the processor
	 */
	public String getLang()	{
		return lang;
	}

	/**
	 * @author isrlab
	 * Language processor collects and processes data from opeNER endpoints
	 * creates  a JSON message of text,language, tokens and POS terms
	 */
	public JSONObject getResultJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("text", getText().replace("'", "\u0027"));
			json.put("language", getLang());
			json.put("terms", getTermsJson());
			json.put("tokens", getTokensJson());
		} catch (JSONException e) {
			logger.error("JSONException {} in getResultJson - language = {} , text = {} , terms = {} ,tokens = {}",e.getMessage(),getLang(),getText(),getTermsJson(),getTokensJson());
		}
		return json;
	}

	/**
	 * @return Returns POS result in JSON format
	 */
	public  JSONArray getTermsJson() {
		JSONArray jArray = new JSONArray();
		try {
			jArray = new JSONArray(getTermsRaw().toString());
		} catch (JSONException e) {
			logger.error("JSONException {} in getTermsJson - language = {} , text = {} ",e.getMessage(),lang,encodedText);
		}
		return jArray;
	}

	/**
	 * @return Returns POS result in as an Object
	 */
	public  List<TextTerm> getTermsRaw() {
		return terms;
	}

	public String getTermPos(String term){
		List<String> tmpRes = new LinkedList<> ();
		for(TextTerm oneTerm : terms) {
			if(oneTerm.getLemma().equalsIgnoreCase(term)) {
				if(oneTerm.getTargets().get(0).getToken().getOffset()!=null && oneTerm.getTargets().get(0).getToken().getLength()!=null) {
					Integer midPos = Integer.parseInt(oneTerm.getTargets().get(0).getToken().getOffset()) + Math.toIntExact(Integer.parseInt(oneTerm.getTargets().get(0).getToken().getLength()) / 2);
					tmpRes.add(midPos.toString());
				}
			}
		}
		Integer index = getTermPosID(term);
		if(index<tmpRes.size()-1)
			updatePosHistory(term, index+1);
		if(tmpRes.isEmpty())
			return "-1";
		return tmpRes.get(index);
	}

	// Calling this function will return the Index of the term position in a list returned by getTermPos
	public Integer getTermPosID(String Term){
		Integer index = 0;
		if(termHistory.containsKey(Term.toLowerCase())) {
			index = termHistory.get(Term.toLowerCase());
		} else {
			termHistory.put(Term.toLowerCase(), 0);
		}
		return index;
	}

	public void updatePosHistory(String Term,Integer newIndex){
		termHistory.put(Term.toLowerCase(), newIndex);
	}

	public Integer getOverallPos(String Phrase) {
		String[] terms = Phrase.split(" ");
		Integer value = 0;
		for(String t : terms)
			value += Integer.parseInt(getTermPos(t));
		return Math.toIntExact(value/terms.length);
	}

	@SuppressWarnings("deprecation")
	public  String getText() {
		String res = URLDecoder.decode(encodedText);
		try {
			return URLDecoder.decode(encodedText, StandardCharsets.UTF_8.name()) ;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * @return Returns token result in JSON format
	 */
	public  JSONArray getTokensJson() {
		JSONArray jArray = new JSONArray();
		try {
			jArray = new JSONArray(getTokensRaw().toString());
		} catch(JSONException e) {
			logger.error("JSONException {} in getTokensJson - language = {} , text = {} ", e.getMessage(), lang, encodedText);
		}
		return jArray;
	}

	/**
	 * @return Returns token result in as an Object
	 */
	public  List<TextToken> getTokensRaw() {
		return tokens;
	}

	public boolean isDictionaryLoaded() {
		return dictionaryLoaded;
	}

	private  LanguageDictionary loadJsonDictionary(String path)	{
		String line;
		String jsonText = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
			while((line = br.readLine()) != null)
				jsonText = jsonText.concat(line);
		} catch (IOException e) {
			logger.error("IOException {} in loadJsonDictionary - language = {} , text = {} ", e.getMessage(), lang, encodedText);
		} finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LanguageDictionary dict = null;
		try {
			JSONObject json = new JSONObject(jsonText);
			JSONArray words = json.getJSONArray("phrases");
			dict = new LanguageDictionary(json.getString("lang"), json.getString("name"));
			for(int i = 0; i < words.length(); i++)	{
				DictionaryPhrase tmpWord = new DictionaryPhrase();
				tmpWord.setType(words.getJSONObject(i).getString("type"));
				tmpWord.setPhrase(words.getJSONObject(i).getString("phrase"));
				JSONArray jsonArray = new JSONArray(words.getJSONObject(i).getJSONArray("tags").toString());
				List<String> list = new LinkedList<>();
				for (int i1=0; i1<jsonArray.length(); i1++)
					list.add( jsonArray.getString(i1) );
				tmpWord.setTags(list);
				JSONArray jsonArray2  =  new JSONArray(words.getJSONObject(i).getJSONArray("identifiers").toString());
				List<String> list2 = new ArrayList<>();
				for (int i1=0; i1<jsonArray2.length(); i1++)
					list2.add( jsonArray2.getString(i1) );
				tmpWord.setIdentifiers(list2);
				dict.addPhrase(tmpWord);
			}
		} catch (JSONException e) {
			logger.error("JSONException {} in loadJsonDictionary - language = {} , text = {} ", e.getMessage(), lang, encodedText);
		}
		return dict;
	}

	/**
	 * @author isrlab
	 * This function processes both Tokens and Pos Terms, THIS MUST BE CALLED BEFORE ANY OTHER FUNCTIONS
	 */
	public void process() {
		String urlParameters = "input="+encodedText+"&language="+lang+"&kaf=false";
		tokenKaf = "";
		try {
			logger.info("called tokenizer {} \n {}", encodedText, urlParameters);
			tokenKaf = (utils.sendPost(configs.getTokenizerUrl(), urlParameters));
			logger.info(" tokenizer  response{}", tokenKaf);
		} catch (Exception e) {
			logger.error("Exception {} in process - language = {} , text = {} for Tokenizer", e.getMessage(), lang, encodedText);
		}
		String tokenKafEnc = tokenKaf;
		try {
			tokenKafEnc = URLEncoder.encode(tokenKaf, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		urlParameters = "input="+tokenKafEnc;
		posKaf = "";
		try {
			logger.info("called tagger {}", tokenKaf);
			posKaf=(utils.sendPost(configs.getTaggerUrl(), urlParameters));
		} catch (Exception e) {
			logger.error("Exception {} in process - language = {} , text = {} for POS tagger", e.getMessage(), lang, encodedText);
		}
		logger.info("POS result {}",posKaf);
		processTerms(posKaf,processTokens(posKaf));
	}

	/**
	 * @param posKaf2 :
	 * @param tokens :
	 * @return given the XML document and the List of tokens (@link {@link TextToken}) it will get the processed POS result (@link {@link TextTerm})
	 */
	public void processTerms(String posKaf2, LinkedList<TextToken> tokens) {
		terms.clear();
		KAF kaf = utils.loadKAF(posKaf2);
		if(posKaf2!=null){
			KAF.Terms el = kaf.getTerms();
			List<Term> cList = el.getTerm();
			List<TextTerm> res = new LinkedList<>();
			for(int i=0 ; i<cList.size(); i++) {
				Term ele =  cList.get(i);
				TextTerm term = new TextTerm();
				term.setLemma(ele.getLemma());
				term.setMorphofeat(ele.getMorphofeat());
				term.setPos(ele.getPos());
				term.setTid(ele.getTid());
				term.setType(ele.getType());
				Span span = ele.getSpan();
				Target targetE = span.getTarget();
				TargetToken target = new TargetToken();
				target.setWid(targetE.getId());
				target.setToken(findTokenByWID(tokens, target.getWid()));
				term.addTarget(target);
				res.add(term);
			}
			terms = res;
		}
	}

	/**
	 * @param kaf2 :
	 * @return given the XML document  it will get the processed Token result (@link {@link TextToken})
	 */
	public  LinkedList<TextToken> processTokens(String kaf2){
		tokens.clear();
		KAF kaf = utils.loadKAF(kaf2);
		LinkedList<TextToken> res = new LinkedList<>();
		if(kaf!=null) {
			Text text = kaf.getText();
			List<Wf> wList = text.getWf();
			for(int i=0; i<wList.size(); i++) {
				Wf ele =  wList.get(i);
				TextToken token = new TextToken();
				token.setValue(ele.getValue());
				token.setLength(ele.getLength());
				token.setOffset(ele.getOffset());
				token.setPara(ele.getPara());
				token.setSent(ele.getSent());
				token.setWid(ele.getWid());
				res.add(token);
			}
			tokens = res;
		}
		return res;
	}

	public void setLang(String l) {
		lang = l;
	}
}
