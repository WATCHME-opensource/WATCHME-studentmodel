package eu.watchme.sm.nlp;

import eu.watchme.sm.datatypes.*;
import eu.watchme.sm.helpers.GeneralConfigs;
import eu.watchme.sm.helpers.GeneralNLP;
import eu.watchme.sm.helpers.GeneralUtils;
import eu.watchme.sm.helpers.JSONWrapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author isrlab
 * Language processor collects and processes data from opeNER endpoints
 * There are three format to retrieve the information.
 * 1. use getTokenKaf or getPosKaf to get orginal KAF XML format
 * 2. use getTokensJson or getTermsJson to get CUSTOMISED JSON format look at @link {@link TextTerm} @link {@link TextToken}
 * 3. use getTokensRaw or getTermsRaw to get the object fromat of the data look at @link {@link TextTerm} @link {@link TextToken}
 */
public class LanguageProcessor {
	public Logger logger = LoggerFactory.getLogger(LanguageProcessor.class);

	private GeneralUtils utils = null;
    protected GeneralConfigs configs = null;

	protected  String lang = "en";

	protected  HashMap<String, LanguageDictionary> languageDictionaries = new HashMap<>();
	protected List<String> dictionaryNames = new ArrayList<>();

    protected  String encodedText = "";
    protected  String posKaf ="";
	protected  String tokenKaf = "";

    protected  List<TextTerm> terms = new ArrayList<>();
    public List<TextToken> tokens = new ArrayList<>();

    /**
     * @author isrlab
     * Language processor collects and processes data from opeNER endpoints
     * There are three format to retrieve the information.
     * 1. use getTokenKaf or getPosKaf to get orginal KAF XML format
     * 2. use getTokensJson or getTermsJson to get CUSTOMISED JSON format look at @link {@link TextTerm} @link {@link TextToken}
     * 3. use getTokensRaw or getTermsRaw to get the object fromat of the data look at @link {@link TextTerm} @link {@link TextToken}
     */
    public LanguageProcessor(String l, String parent) {
        dictionaryNames.clear();
        languageDictionaries.clear();

        if(!l.isEmpty())
            lang = l;

        configs = new GeneralConfigs(parent);
        utils = new GeneralUtils(parent);

        File LangDir = new File(parent+""+configs.getLanguageFolder()+l);

        if(LangDir.getAbsoluteFile().exists()) {
            //Load dictionaries here
            File[] listDictionaries = LangDir.listFiles();

            for(File dictionary : listDictionaries) {
                if(dictionary.getName().contains(".json"))
                    continue;
                String name = utils.removeExtension(dictionary.getName());
                String jsonPath = parent+""+configs.getProcessedLanguageFolder()+l+"/"+name+".json";
                logger.info("json path {}",jsonPath);
                File jsonFile = new File(jsonPath, "UTF-8");
                String path = parent+""+configs.getLanguageFolder()+l+"/"+dictionary.getName();
                LanguageDictionary tmp = loadDictionary(path, l, name);
                JSONWrapper json_wrapper = new JSONWrapper();
                json_wrapper.saveJSON(jsonPath, tmp.toString());
                dictionaryNames.add(name);
                languageDictionaries.put(name, tmp);
            }
        }
    }

    public void createProcessor(String text) {
		try {
			encodedText = URLEncoder.encode(text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    logger.info("running language processor lang {} for text {}", getLang(),getText());
    }

    /**
     * @param tokens
     * @param wid
     * @return finds the Token based on its wID value in the KAF data
     */
    public TextToken findTokenByWID(List<TextToken> tokens, String wid) {
        TextToken res =new TextToken();
        for(int i=0; i < tokens.size(); i++)
            if(tokens.get(i).getWid().equals(wid))
                return tokens.get(i);
	    return res;
    }

    public List<String> getIdentifierString(List<TextTerm> terms) {
        List<String> res = new ArrayList<>();

        for(TextTerm term : terms) {
            String morpho = term.getMorphofeat();
            res.add(term.getLemma()+"_"+morpho);
        }
        return res;
    }

    /**
     * @return returns the current language of the processor
     */
    public String getLang() {
	    return lang;
    }

    public List<String> getTagsString(List<TextTerm> terms) {
	List<String> res = new ArrayList<>();
        for(TextTerm term : terms) {
            String morpho = term.getMorphofeat();
            res.add(morpho);
        }
        return res;
    }

    /**
     * @return Returns POS result in as an Object
     */
    public List<TextTerm> getTermsRaw() {
	    return terms;
    }

    @SuppressWarnings("deprecation")
    public String getText() {
	    return URLDecoder.decode(encodedText);
    }

    /**
     * @return Returns token result in as an Object
     */
    public List<TextToken> getTokensRaw() {
	    return tokens;
    }

    /**
     * @param path : path to csv file containing terms and sentiments
     * @param l : language to parse
     * @param n : dictionary name
     */
    private LanguageDictionary loadDictionary(String path, String l, String n) {
        LanguageDictionary dict = new LanguageDictionary(l, n);
        String csvFilePath = path;
        File csvFile = new File(csvFilePath);
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String oldText = getText();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile.getAbsolutePath()), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
            line = line.replaceAll("\"","");
            String[] element = line.split(cvsSplitBy);
            String[] actualWords = element[1].toLowerCase().split(" ");
            List<String> combinedWords = GeneralNLP.combineWords(actualWords);
            for(String word : combinedWords) {
                DictionaryPhrase tmpWord = new DictionaryPhrase();
                tmpWord.setType(element[0]);
                tmpWord.setPhrase(word.trim());
                setText(word);
                process();
                tmpWord.setTags(getTagsString(getTermsRaw()));
                tmpWord.setIdentifiers(getIdentifierString(getTermsRaw()));
                DictionaryPhrase tmp2 = dict.matchedIdentifier(tmpWord.getIdentifiers()) ;
                if(tmp2 == null)
                    dict.addPhrase(tmpWord);
            }
            logger.info("processed \"{}\" for  \"{}\" in language \"{}\" " , element[1] , n,l);
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFound {} in loadDictionary - language = {} , text = {} , file path = {}", e.getMessage(), lang, encodedText, csvFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("IOException {} in loadDictionary - language = {} , text = {} , file path = {}", e.getMessage(), lang, encodedText, csvFile.getAbsolutePath());
        } finally {
            if (br != null)
                try {
                    br.close();
                    setText(oldText);
                } catch (IOException e) {
                    logger.error("IOException {} in loadDictionary - language = {} , text = {} , file path = {}", e.getMessage(), lang, encodedText, csvFile.getAbsolutePath());
                }
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
            tokenKaf = (utils.sendPost(configs.getTokenizerUrl(),urlParameters));
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
            posKaf=(utils.sendPost(configs.getTaggerUrl(),urlParameters));
        } catch (Exception e) {
            logger.error("Exception {} in process - language = {} , text = {} for POS tagger", e.getMessage(), lang, encodedText);
        }
		if(!posKaf.equals(""))
            processTerms(posKaf,processTokens(posKaf));
    }
    /**
     * @param posKaf2
     * @param tokens
     * @return given the XML document and the List of tokens (@link {@link TextToken}) it will get the processed POS result (@link {@link TextTerm})
     */
    public void processTerms(String posKaf2, List<TextToken> tokens) {
        terms.clear();
        KAF kaf = utils.loadKAF(posKaf2);
        if(posKaf2!=null) {
            KAF.Terms el = kaf.getTerms();
            List<Term> cList = el.getTerm();
            List<TextTerm> res = new ArrayList<>();
            for(int i=0 ; i<cList.size(); i++) {
                Term ele = cList.get(i);
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
     * @return given the XML document  it will get the processed Token result (@link {@link TextToken})
     */
    public List<TextToken> processTokens(String kaf2) {
        tokens.clear();
		KAF kaf = utils.loadKAF(kaf2);
		List<TextToken> res = new ArrayList<>();
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

    public void setText(String text) {
        try {
            encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
