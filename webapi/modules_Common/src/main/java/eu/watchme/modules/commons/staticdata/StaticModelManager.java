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

package eu.watchme.modules.commons.staticdata;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.enums.FeedbackSeekingStrategyUnb;
import eu.watchme.modules.commons.enums.FrustrationAlertUnb;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.enums.PortfolioConsistencyUnb;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackMessages;
import eu.watchme.modules.commons.staticdata.model.form.mapper.FormMapper;
import eu.watchme.modules.commons.staticdata.model.portfolio.PortfolioMessages;
import eu.watchme.modules.commons.staticdata.model.recommendation.mapper.RubricMatrix;

import java.util.*;

import static eu.watchme.modules.commons.staticdata.model.form.mapper.FormMapper.buildMapper;
import static eu.watchme.modules.commons.staticdata.model.recommendation.mapper.RubricMatrix.buildRubric;

public class StaticModelManager {
	private static final HashMap<String, StaticModelManager> staticModelManagers = new HashMap<>();

	private FormMapper mFormMapper;
	private RubricMatrix mRubricMatrix;
	private FeedbackMessages mFeedbackMessages;
	private PortfolioMessages mPortfolioMessages;
	private PortfolioMessages mSupervisorPortfolioMessages;

	private Set<LanguageCode> supportedLanguages = new HashSet<>();

	private StaticModelManager(String domainId) throws Exception {
		System.setProperty("jsse.enableSNIExtension", "false");
		mFormMapper = buildMapper(domainId, StaticDataLoader.loadFormRelatedData(domainId));
		mRubricMatrix = buildRubric(domainId, StaticDataLoader.loadRecommendations(domainId));
		mFeedbackMessages = StaticDataLoader.loadFeedbackMessages(domainId);
		mPortfolioMessages = StaticDataLoader.loadPortfolioMessages(domainId);
		mSupervisorPortfolioMessages = StaticDataLoader.loadSupervisorPortfolioMessages(domainId);
		buildSupportedLanguages();
	}

	public static void preloadManagers(Set<String> domainsList) {
		domainsList.forEach(StaticModelManager::getManager);
	}

	public static StaticModelManager getManager(String domainId) {
		synchronized (staticModelManagers) {
			StaticModelManager result = staticModelManagers.get(domainId);
			if (result == null) {
				try {
					result = new StaticModelManager(domainId);
				} catch (Exception e) {
					throw new StaticModelException(e);
				}
				staticModelManagers.put(domainId, result);
			}
			return result;
		}
	}

	public FormMapper getFormMapper() {
		return mFormMapper;
	}

	public RubricMatrix getRubricMatrix() {
		return mRubricMatrix;
	}

	public FeedbackMessages getFeedbackMessages() {
		return mFeedbackMessages;
	}

	private String selectTranslation(LanguageCode langCode, Map<LanguageCode, List<String>> translations) {
		if (translations == null)
			return null;
		List<String> messages = translations.get(langCode);
		if (langCode != ApplicationSettings.DEFAULT_LANGUAGE && (messages == null || messages.isEmpty()))
			messages = translations.get(ApplicationSettings.DEFAULT_LANGUAGE);

		return pickRandom(messages);
	}

	private String pickRandom(List<String> list) {
		if (list == null || list.isEmpty())
			return null;
		if (list.size() == 1)
			return list.get(0);
		return list.get(new Random().nextInt(list.size()));
	}

	public String getFeedbackSeekingStrategyMessage(FeedbackSeekingStrategyUnb feedbackSeekingStrategy, LanguageCode langCode) {
		if (feedbackSeekingStrategy == null || mPortfolioMessages == null || mPortfolioMessages.getFeedbackSeekingStrategyMap() == null)
			return null;
		return selectTranslation(langCode, mPortfolioMessages.getFeedbackSeekingStrategyMap().get(feedbackSeekingStrategy));
	}

	public String getSupervisorFeedbackSeekingStrategyMessage(FeedbackSeekingStrategyUnb feedbackSeekingStrategy, LanguageCode langCode) {
		if (feedbackSeekingStrategy == null || mSupervisorPortfolioMessages == null || mSupervisorPortfolioMessages.getFeedbackSeekingStrategyMap() == null)
			return null;
		return selectTranslation(langCode, mSupervisorPortfolioMessages.getFeedbackSeekingStrategyMap().get(feedbackSeekingStrategy));
	}

	public String getPortfolioConsistencyMessage(PortfolioConsistencyUnb portfolioConsistency, LanguageCode langCode) {
		if (portfolioConsistency == null || mPortfolioMessages == null || mPortfolioMessages.getPortfolioConsistencyMap() == null)
			return null;
		return selectTranslation(langCode, mPortfolioMessages.getPortfolioConsistencyMap().get(portfolioConsistency));
	}

	public String getSupervisorPortfolioConsistencyMessage(PortfolioConsistencyUnb portfolioConsistency, LanguageCode langCode) {
		if (portfolioConsistency == null || mSupervisorPortfolioMessages == null || mSupervisorPortfolioMessages.getPortfolioConsistencyMap() == null)
			return null;
		return selectTranslation(langCode, mSupervisorPortfolioMessages.getPortfolioConsistencyMap().get(portfolioConsistency));
	}

	public String getSupervisorFrustrationAlertMessage(FrustrationAlertUnb frustrationAlert, LanguageCode langCode) {
		if (frustrationAlert == null || mSupervisorPortfolioMessages == null || mSupervisorPortfolioMessages.getFrustrationAlertMap() == null)
			return null;
		return selectTranslation(langCode, mSupervisorPortfolioMessages.getFrustrationAlertMap().get(frustrationAlert));
	}

	public String getInformationLevelTemplate(LanguageCode langCode) {
		if (mPortfolioMessages == null || mPortfolioMessages.getInformationLevelMap() == null)
			return null;
		return selectTranslation(langCode, mPortfolioMessages.getInformationLevelMap());
	}

	public String getSupervisorInformationLevelTemplate(LanguageCode langCode) {
		if (mSupervisorPortfolioMessages == null || mSupervisorPortfolioMessages.getInformationLevelMap() == null)
			return null;
		return selectTranslation(langCode, mSupervisorPortfolioMessages.getInformationLevelMap());
	}

	private void buildSupportedLanguages() {
		supportedLanguages.addAll(Arrays.asList(LanguageCode.values()));
		supportedLanguages.retainAll(mRubricMatrix.getSupportedLanguages());
		supportedLanguages.retainAll(buildFeedbackSupportingLanguages());
	}

	private Set<LanguageCode> buildFeedbackSupportingLanguages() {
		Set<LanguageCode> result = new HashSet<>();

		result.addAll(mFeedbackMessages.getImprovementFeedback().getLevel1Feedback().getTypeNegativeTextTranslations()
				.keySet());
		result.addAll(mFeedbackMessages.getImprovementFeedback().getLevel1Feedback().getTypeNeutralTextTranslations()
				.keySet());
		result.addAll(mFeedbackMessages.getImprovementFeedback().getLevel1Feedback().getTypePositiveTextTranslations()
				.keySet());
		result.addAll(mFeedbackMessages.getImprovementFeedback().getLevel2FeedbackTranslations().keySet());

		result.addAll(mFeedbackMessages.getSupervisorFeedback().getLevel1Feedback().getTypeNeutralTextTranslations()
				.keySet());
		result.addAll(mFeedbackMessages.getSupervisorFeedback().getLevel2FeedbackTranslations().keySet());

		return result;
	}

	public boolean isLanguageSupported(LanguageCode languageCode) {
		return supportedLanguages.contains(languageCode);
	}
}
