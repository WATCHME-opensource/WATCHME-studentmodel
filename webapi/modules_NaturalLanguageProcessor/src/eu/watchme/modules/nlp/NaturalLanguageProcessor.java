/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 31/7/2015
 * Copyright: Copyright (C) 2014-2017 WATCHME Consortium
 * License: The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.watchme.modules.nlp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.commons.staticdata.StaticModelException;
import eu.watchme.modules.commons.staticdata.model.form.json.SupervisorFeedbackField;
import eu.watchme.modules.domainmodel.exceptions.NoMappingAvailableException;
import eu.watchme.modules.nlp.datatypes.NLPFindingSet;
import eu.watchme.modules.nlp.datatypes.ResultWordSet;
import eu.watchme.modules.nlp.general.NetworkingUtils;
import eu.watchme.modules.nlp.model.NlpFieldResult;
import eu.watchme.modules.nlp.model.NlpOutputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.*;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;

public class NaturalLanguageProcessor {
	private Logger logger = LoggerFactory.getLogger(NaturalLanguageProcessor.class);
	private static final String HTML_PATTERN = "<(.|\n)*?>";
	private static final List<String> NLP_GROUP = Arrays.asList("positive", "negative");
	private static final List<Integer> NLP_GROUP_SCORE = Arrays.asList(1, -1);

	private final static HashMap<String, NaturalLanguageProcessor> naturalLanguageProcessorsMap = new HashMap<>();

	private String domainId;

	private NaturalLanguageProcessor(String domainId) throws FileNotFoundException {
		this.domainId = domainId;
	}

	public static NaturalLanguageProcessor getProcessor(String domainId) throws FileNotFoundException {
		synchronized (naturalLanguageProcessorsMap) {
			if (!naturalLanguageProcessorsMap.containsKey(domainId)) {
				naturalLanguageProcessorsMap.put(domainId, new NaturalLanguageProcessor(domainId));
			}
			return naturalLanguageProcessorsMap.get(domainId);
		}
	}

	public NlpOutputData process(Integer epassFormType, String epassQuestionId, String unbEpaId, String answer) {
		String answerText = stripHtmlCode(answer);
		if (answerText == null || answerText.isEmpty()) {
			return null;
		}
		FeedbackLevel suggestedLevel = getSuggestedLevel(epassFormType, epassQuestionId, unbEpaId);
		NlpFieldResult resultAnswer = processAnswer(answerText, suggestedLevel);
		FeedbackLevel feedbackLevel = resultAnswer.getFeedbackLevel();
		Set<String> linkedKeywords = resultAnswer.getLinkedKeywords();
		List<NLPFindingSet> findingList = resultAnswer.getFindingList();
		String sentiment = getSentiment(resultAnswer.getFindingList());
		return new NlpOutputData(answerText, sentiment, feedbackLevel, linkedKeywords, findingList);
	}

	private String getSentiment(List<NLPFindingSet> findingList) {
		if (findingList == null || findingList.isEmpty()) {
			return null;
		}
		Optional<NLPFindingSet> optionalFinding = findingList.stream().filter(nlpFindingSet -> nlpFindingSet.getType() != null && nlpFindingSet.getType().equals("display")).findFirst();
		if (optionalFinding.isPresent()) {
			return optionalFinding.get().getWord();
		}
		return null;
	}

	private NlpFieldResult processAnswer(String epassAnswer, FeedbackLevel suggestedLevel) {
		if (ApplicationSettings.hasNlpProcessorEnabled(domainId)) {
			return processAnswerNlp(epassAnswer, suggestedLevel);
		} else {
			return wrapAnswer(suggestedLevel);
		}
	}

	private NlpFieldResult wrapAnswer(FeedbackLevel suggestedLevel) {
		return new NlpFieldResult(suggestedLevel, Collections.emptySet(), Collections.emptyList());
	}

	private NlpFieldResult processAnswerNlp(String epassAnswer, FeedbackLevel suggestedLevel) {
		//todo; process this data using the OpenNER wrapper
		String encodedText = "";
		List<NLPFindingSet> nlpList = new ArrayList<>();
		epassAnswer = epassAnswer.replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
		try {
			encodedText = URLEncoder.encode(stripHtmlCode(epassAnswer), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String urlParameters = "text=" + encodedText + "";
		NetworkingUtils network = new NetworkingUtils();
		Set<String> result = new LinkedHashSet<>();
		try {
			String resp = network.sendPost(ApplicationSettings.getNLPendpoint(), urlParameters);

			Type listType = new TypeToken<List<ResultWordSet>>() {
			}.getType();
			List<ResultWordSet> res = new Gson().fromJson(resp, listType);
			Integer levelScore = 0;

			for (ResultWordSet item : res) {
				if (isDomainMatched(item.getDictionary_name()) || item.getDictionary_name().equalsIgnoreCase("*")) {
					NLPFindingSet tmpSet = new NLPFindingSet();
					tmpSet.setPos(item.getPos());
					tmpSet.setScore("0");
					tmpSet.setWord(item.getPhrase().getPhrase());
					nlpList.add(tmpSet);

					if (item.getPhrase().getType().equalsIgnoreCase("recommendation")) {
						tmpSet.setType("display");
					} else {
						tmpSet.setType("identifier");
						result.add(item.getPhrase().getPhrase());
					}

				} else if (item.getDictionary_name().startsWith("sentiment")) {

					Integer ind = NLP_GROUP.indexOf(item.getPhrase().getType().toLowerCase());
					if (ind >= 0) {
						levelScore += NLP_GROUP_SCORE.get(ind);
						NLPFindingSet tmpSet = new NLPFindingSet();
						tmpSet.setPos(item.getPos());
						tmpSet.setScore(NLP_GROUP_SCORE.get(ind).toString());
						tmpSet.setType("polarity");
						tmpSet.setWord(item.getPhrase().getPhrase());
						nlpList.add(tmpSet);
					}
				}
			}
			logger.info("Language Processor  {}", nlpList);
			if (levelScore > 0) {
				suggestedLevel = FeedbackLevel.positive;
			} else if (levelScore < 0) {
				suggestedLevel = FeedbackLevel.negative;
			} else {
				suggestedLevel = FeedbackLevel.neutral;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new NlpFieldResult(suggestedLevel, result, nlpList);
	}

	private static String stripHtmlCode(String text) {
		if (text != null) {
			return text.replaceAll(HTML_PATTERN, " ").replaceAll("\n", " ").replaceAll("[ ]+", " ").trim();
		} else {
			return null;
		}
	}

	private boolean isDomainMatched(String domain) {
		String[] tokens = domainId.split("-");
		return tokens.length > 0 && tokens[0].equalsIgnoreCase(domain.trim());

	}

	private FeedbackLevel getSuggestedLevel(Integer epassFormType, String epassQuestionId, String unbEpaId) {
		try {
			List<SupervisorFeedbackField> list = getManager(domainId).getFormMapper().getSupervisorFeedbackField(epassFormType, epassQuestionId);
			Optional<SupervisorFeedbackField> field = list.stream().filter(sp -> sp.getUnbKey().getEpaId().equals(unbEpaId)).findAny();

			if (field != null && field.isPresent()) {
				return field.get().getFeedbackLevel();
			} else {
				throw new NoMappingAvailableException("epass question id", epassQuestionId);
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("epass question id", epassQuestionId, e);
		}
	}
}
