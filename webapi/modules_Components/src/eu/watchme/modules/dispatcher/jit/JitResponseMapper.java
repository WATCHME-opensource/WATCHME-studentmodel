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

package eu.watchme.modules.dispatcher.jit;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.enums.FeedbackSeekingStrategyUnb;
import eu.watchme.modules.commons.enums.FrustrationAlertUnb;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.enums.PortfolioConsistencyUnb;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.commons.staticdata.model.feedback.Feedback;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackType;
import eu.watchme.modules.commons.staticdata.model.form.json.EpaMappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.MappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.PIMappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.TypeMappedField;
import eu.watchme.modules.commons.staticdata.model.form.mapper.FormMapper;
import eu.watchme.modules.dataaccess.adapters.StudentStatsAdapter;
import eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject;
import eu.watchme.modules.dataaccess.model.activity.StudentEpaStatsDataObject;
import eu.watchme.modules.dataaccess.model.activity.StudentPiStatsDataObject;
import eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.JitFeedbackResultDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.JitPortfolioResultDataObject;
import eu.watchme.modules.domainmodel.jit.*;
import eu.watchme.modules.domainmodel.jit.supervisor.SupervisedStudentPortfolioModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;
import static eu.watchme.modules.commons.staticdata.model.feedback.FeedbackType.*;
import static eu.watchme.modules.dataaccess.adapters.SupervisorFeedbackAdapter.supervisorFeedbackAdapter;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class JitResponseMapper {
	private static final FeedbackType[] improvementFeedbackPriorityList = new FeedbackType[] { IMPROVEMENT_NEGATIVE, IMPROVEMENT_NEUTRAL, IMPROVEMENT_POSITIVE };
	private static final HashMap<String, JitResponseMapper> sResponseMappersMap = new HashMap<>();

	private String domainId;
	private JitMessageFormatter mMessageFormatter;
	private Logger logger = LoggerFactory.getLogger(JitResponseMapper.class);

	private JitResponseMapper(String domainId) throws FileNotFoundException {
		this.domainId = domainId;

		mMessageFormatter = JitMessageFormatter.getInstance();
	}

	public static JitResponseMapper getMapper(String domainId) throws FileNotFoundException {
		synchronized (sResponseMappersMap) {
			if (!sResponseMappersMap.containsKey(domainId)) {
				sResponseMappersMap.put(domainId, new JitResponseMapper(domainId));
			}
			return sResponseMappersMap.get(domainId);
		}
	}

	public final FeedbackResponseModel processFeedbackResults(String studentId, String modelId, List<JitFeedbackResultDataObject> jitFeedbackResults, JitPortfolioResultDataObject jitPortfolioResult,
			LanguageCode langCode) throws FileNotFoundException {
		if (!StaticModelManager.getManager(domainId).isLanguageSupported(langCode)) {
			langCode = ApplicationSettings.DEFAULT_LANGUAGE;
		}

		Map<String, List<JitFeedbackResultDataObject>> epaFeedback = jitFeedbackResults.stream().collect(groupingBy(JitFeedbackResultDataObject::getEpaUnbId));

		StudentStatsDataObject stats = new StudentStatsAdapter().getStats(studentId, modelId);
		List<FeedbackEpaModel> epasList = new ArrayList<>(epaFeedback.size());
		for (String unbEpaId : epaFeedback.keySet()) {
			MappedField epaMappedField = getManager(domainId).getFormMapper().getMappedEpaByUnb(unbEpaId);

			List<JitFeedbackResultDataObject> allFeedbackResultsForEpa = epaFeedback.get(unbEpaId);
			List<FeedbackMessageModel> messages = getFeedbackMessages(studentId, epaMappedField,
					allFeedbackResultsForEpa, langCode);
			if (messages != null && !messages.isEmpty()) {
				epasList.add(new FeedbackEpaModel(epaMappedField.getEpassId(),
						epaMappedField.getTranslation(langCode),
						messages,
						stats.getLastUpdatedInstant(unbEpaId)));
			} else {
				logger.debug("Thread-{} : Invalid message mapping for EPA {}, langCode {}", Thread.currentThread().getId(), epaMappedField.getEpassId(), langCode);
			}
		}
		PortfolioModel portfolio = mapPortfolioResult(jitPortfolioResult, langCode);
		sortEpas(epasList);
		return new FeedbackResponseModel(epasList, portfolio, Instant.now(Clock.systemUTC()));
	}

	private void sortEpas(List<FeedbackEpaModel> epasList) {
		if (epasList != null) {
			Collections.sort(epasList, (o1, o2) -> o1.mLastFeedbackDate == null ? -1 : o2.mLastFeedbackDate == null ? 1 : o1.mLastFeedbackDate.isAfter(o2.mLastFeedbackDate) ? -1 : 1);
		}
	}

	private List<FeedbackMessageModel> getFeedbackMessages(String studentId,
			MappedField epaMappedField,
			List<JitFeedbackResultDataObject> allFeedbackResults,
			LanguageCode langCode) {
		List<FeedbackMessageModel> result = new LinkedList<>();
		StudentStatsDataObject stats = new StudentStatsAdapter().getStats(studentId, domainId);
		result.addAll(getImprovementMessages(epaMappedField,
				filterImprovementMessages(allFeedbackResults),
				stats,
				langCode));
		result.addAll(getSupervisorMessages(studentId, epaMappedField,
				filterByFeedbackType(allFeedbackResults, SUPERVISOR),
				langCode));
		sortFeedbackMessages(result);
		return result;
	}

	private void sortFeedbackMessages(List<FeedbackMessageModel> list) {
		if (list != null) {
			Collections.sort(list, (o1, o2) -> o1.getLastFeedbackDate() == null ? -1 : o2.getLastFeedbackDate() == null ? 1 : o1.getLastFeedbackDate().isAfter(o2.getLastFeedbackDate()) ? -1 : 1);
		}
	}

	private List<JitFeedbackResultDataObject> filterImprovementMessages(List<JitFeedbackResultDataObject> list) {
		if (list.isEmpty()) {
			return list;
		}

		for (FeedbackType feedbackType : improvementFeedbackPriorityList) {
			List<JitFeedbackResultDataObject> filteredList = filterByFeedbackType(list, feedbackType);
			if (!filteredList.isEmpty()) {
				return filteredList;
			}
		}

		return Collections.emptyList();
	}

	private List<JitFeedbackResultDataObject> filterByFeedbackType(List<JitFeedbackResultDataObject> list, FeedbackType feedbackType) {
		FormMapper fm = getManager(domainId).getFormMapper();
		return list.stream().filter(fr -> fm.getFeedbackTypeByUnb(fr.getFeedbackType()) == feedbackType).collect(toList());
	}

	private List<FeedbackMessageModel> getImprovementMessages(MappedField epaMappedField,
			List<JitFeedbackResultDataObject> filteredResults,
			StudentStatsDataObject stats,
			LanguageCode langCode) {
		List<FeedbackMessageModel> result = new ArrayList<>(2);
		if (!filteredResults.isEmpty()) {
			TypeMappedField epaFeedbackTypeId = getEpassEpaFeedbackTypeId(filteredResults);
			FeedbackMessageModel level1Feedback = getImprovementLevel1Message(epaMappedField,
					epaFeedbackTypeId,
					langCode);
			if (level1Feedback != null) {
				result.add(level1Feedback);
			}
			StudentEpaStatsDataObject epaStats = stats.findEpaStats(epaMappedField.getUnbId());
			FeedbackMessageModel level2Feedback = getImprovementLevel2Message(filteredResults,
					epaFeedbackTypeId,
					epaStats,
					langCode);
			if (level2Feedback != null) {
				result.add(level2Feedback);
				if (level1Feedback != null) {
					level1Feedback.setLastFeedbackDate(level2Feedback.getLastFeedbackDate());
				}
			}
		}
		return result;
	}

	private Map<LanguageCode, String> getLevel1TranslationMap(Feedback feedback, FeedbackType feedbackType) {
		switch (feedbackType) {
		case IMPROVEMENT_NEGATIVE:
			return feedback.getLevel1Feedback().getTypeNegativeTextTranslations();
		case IMPROVEMENT_NEUTRAL:
			return feedback.getLevel1Feedback().getTypeNeutralTextTranslations();
		case IMPROVEMENT_POSITIVE:
			return feedback.getLevel1Feedback().getTypePositiveTextTranslations();
		}
		return null;
	}

	private FeedbackMessageModel getImprovementLevel1Message(MappedField epaMappedField,
			TypeMappedField epaFeedbackTypeId,
			LanguageCode lang) {
		FeedbackMessageModel feedbackMessageModel = null;
		FeedbackType improvementFeedbackType = FeedbackType.getEnumItem(epaFeedbackTypeId.getTypeId());
		Map<LanguageCode, String> translations = getLevel1TranslationMap(getManager(domainId).getFeedbackMessages()
						.getImprovementFeedback(),
				improvementFeedbackType);
		String translation = null;
		if (translations != null && !translations.isEmpty()) {
			translation = translations.get(lang);
		}

		if (translation != null) {
			String formattedText = mMessageFormatter.formatImprovementMessage(translation,
					epaMappedField.getTranslation(lang),
					improvementFeedbackType);
			if (formattedText != null) {
				FeedbackMessageRecommendationModel recommendationModel = new FeedbackMessageRecommendationModel(formattedText, null, null, null);
				feedbackMessageModel = new FeedbackMessageModel(epaFeedbackTypeId.getEpassId(),
						ApplicationSettings.getFeedbackMessageLevel1(),
						Collections.singletonList(recommendationModel));
			}
		}
		return feedbackMessageModel;
	}

	private FeedbackMessageModel getImprovementLevel2Message(List<JitFeedbackResultDataObject> feedbackResults,
			TypeMappedField epaFeedbackTypeId,
			StudentEpaStatsDataObject epaStats,
			LanguageCode langCode) {
		List<FeedbackMessageRecommendationModel> list = feedbackResults.stream()
				.map(result -> getImprovementLevel2Feedback(result.getEpaUnbId(), result.getPerformanceIndicatorUnbId(), result.getLevelUnbId(), epaStats, langCode))
				.collect(Collectors.toList());
		sortList(list);
		return new FeedbackMessageModel(epaFeedbackTypeId.getEpassId(),
				ApplicationSettings.getFeedbackMessageLevel2(),
				list);
	}

	private void sortList(List<FeedbackMessageRecommendationModel> list) {
		if (list != null) {
			Collections.sort(list, (o1, o2) -> o1.getDateReceived() == null ? -1 : o2.getDateReceived() == null ? 1 : o1.getDateReceived().isAfter(o2.getDateReceived()) ? -1 : 1);
		}
	}

	private FeedbackMessageRecommendationModel getImprovementLevel2Feedback(String epaUnbId, String piUnbId, String piLevelUnbId,
			StudentEpaStatsDataObject epaStats, LanguageCode langCode) {
		Feedback improvementFeedback = getManager(domainId).getFeedbackMessages().getImprovementFeedback();
		if (improvementFeedback == null) {
			return null;
		}
		Integer submissionId = null;
		Integer formId = null;
		Instant feedbackDate = null;
		if (piUnbId.equals("*")) {
			if (improvementFeedback.getLevel2GeneralFeedbackTranslations() == null)
				return null;
		} else {
			if (improvementFeedback.getLevel2FeedbackTranslations() == null) {
				return null;
			}
			StudentPiStatsDataObject piStats = epaStats.findPiStats(piUnbId);
			if (piStats != null) {
				submissionId = piStats.getLastSubmissionId();
				formId = piStats.getLastSubmissionFormId();
				feedbackDate = piStats.getLastSubmissionDate();
			}
		}
		FeedbackMessageRecommendationModel result = null;
		Map<LanguageCode, String> translations = piUnbId.equals("*") ? improvementFeedback.getLevel2GeneralFeedbackTranslations() : improvementFeedback.getLevel2FeedbackTranslations();
		if (translations != null && !translations.isEmpty()) {
			String translation = translations.get(langCode);
			String recommendation = getImprovementRecommendation(epaUnbId, piUnbId, piLevelUnbId, langCode);

			result = mMessageFormatter.formatImprovementLevel2Message(translation,
					getPiName(epaUnbId, piUnbId, langCode),
					getPiLevelName(piLevelUnbId, langCode),
					recommendation,
					submissionId,
					formId,
					feedbackDate);
		}
		return result;
	}

	private List<FeedbackMessageModel> getSupervisorMessages(String studentId,
			MappedField epaMappedField,
			List<JitFeedbackResultDataObject> filteredFeedbackResults,
			LanguageCode langCode) {
		List<FeedbackMessageModel> result = new ArrayList<>(2);
		if (!filteredFeedbackResults.isEmpty()) {
			TypeMappedField epaFeedbackTypeId = getEpassEpaFeedbackTypeId(filteredFeedbackResults);
			FeedbackMessageRecommendationModel level1Message = getSupervisorLevelOneMessage(epaMappedField, langCode);
			FeedbackMessageModel level1Feedback =
					new FeedbackMessageModel(epaFeedbackTypeId.getEpassId(),
							ApplicationSettings.getFeedbackMessageLevel1(),
							Collections.singletonList(level1Message));
			result.add(level1Feedback);
			FeedbackMessageModel level2Feedback = getLevelTwoSupervisorFeedback(studentId, epaMappedField, epaFeedbackTypeId, langCode);
			if (level2Feedback != null) {
				result.add(level2Feedback);
				level1Feedback.setLastFeedbackDate(level2Feedback.getLastFeedbackDate());
			} else {
				result = Collections.emptyList();
			}
		}
		return result;
	}

	private FeedbackMessageRecommendationModel getSupervisorLevelOneMessage(MappedField epaMappedField, LanguageCode langCode) {
		FeedbackMessageRecommendationModel result = null;
		if (getManager(domainId).getFeedbackMessages().getSupervisorFeedback() != null &&
				getManager(domainId).getFeedbackMessages().getSupervisorFeedback().getLevel1Feedback() != null) {
			Map<LanguageCode, String> translations = getManager(domainId).getFeedbackMessages()
					.getSupervisorFeedback().getLevel1Feedback()
					.getTypeNeutralTextTranslations();
			if (translations != null) {
				String translation = translations.get(langCode);
				if (translation != null) {
					String text = mMessageFormatter.formatSupervisorLevel1Message(translation,
							epaMappedField.getTranslation(langCode));
					result = new FeedbackMessageRecommendationModel(text, null, null, null);
				}
			}
		}
		return result;
	}

	private FeedbackMessageModel getLevelTwoSupervisorFeedback(String studentId, MappedField epaMappedField,
			TypeMappedField epaFeedbackTypeId,
			LanguageCode langCode) {
		List<FeedbackMessageRecommendationModel> messages = getSupervisorLevel2Feedback(studentId, epaMappedField.getUnbId(), langCode);

		FeedbackMessageModel feedbackMessageModel = null;
		if (!messages.isEmpty()) {
			feedbackMessageModel =
					new FeedbackMessageModel(epaFeedbackTypeId.getEpassId(),
							ApplicationSettings.getFeedbackMessageLevel2(), messages);
		}
		return feedbackMessageModel;
	}

	private List<FeedbackMessageRecommendationModel> getSupervisorLevel2Feedback(String studentId, String epaUnbId,
			LanguageCode langCode) {
		List<FeedbackMessageRecommendationModel> result = Collections.emptyList();
		if (getManager(domainId).getFeedbackMessages().getSupervisorFeedback() != null &&
				getManager(domainId).getFeedbackMessages().getSupervisorFeedback()
						.getLevel2FeedbackTranslations() != null) {
			Map<LanguageCode, String> translations = getManager(domainId).getFeedbackMessages().getSupervisorFeedback()
					.getLevel2FeedbackTranslations();
			if (translations != null && !translations.isEmpty()) {
				String translation = translations.get(langCode);
				int limit = getManager(domainId).getFormMapper().getMaxSupervisorFeedback();
				List<SupervisorFeedbackDataObject> list = supervisorFeedbackAdapter.getLastSupervisorFeedback(studentId, epaUnbId, limit);
				result = list.stream().map(sf -> mMessageFormatter.formatSupervisorLevel2Message(translation,
						sf.getSupervisorFeedback(),
						sf.getSubmissionId(),
						sf.getFormId(),
						sf.getCreationDate())).collect(toList());
			}
		}
		sortList(result);
		return result;
	}

	private TypeMappedField getEpassEpaFeedbackTypeId(List<JitFeedbackResultDataObject> filteredFeedbackResults) {
		if (!filteredFeedbackResults.isEmpty()) {
			return getManager(domainId).getFormMapper()
					.getMappedFeedbackIdByUnb(filteredFeedbackResults.get(0).getFeedbackType());
		} else {
			return new TypeMappedField();
		}
	}

	private String getPiName(String epaUnbId, String piUnbId, LanguageCode lang) {
		String result = "";
		List<PIMappedField> piLabel =
				getManager(domainId).getFormMapper().getMappedPerformanceIndicatorByUnb(epaUnbId, piUnbId);
		if (piLabel != null && !piLabel.isEmpty()) {
			//the epass PIs should have different ids but the same name
			result = piLabel.get(0).getTranslation(lang);
		}
		return result;
	}

	private String getPiLevelName(String piLevelUnbId, LanguageCode lang) {
		String result = "";
		MappedField piLevelLabel = getManager(domainId).getFormMapper()
				.getMappedPerformanceIndicatorLevelByUnb
						(piLevelUnbId);
		if (piLevelLabel != null) {
			result = piLevelLabel.getTranslation(lang);
		}
		return result;
	}

	private String getImprovementRecommendation(String epaId, String pi, String piLevel, LanguageCode langCode) {
		String result =
				getManager(domainId).getRubricMatrix().getRecommendation(new UnbKey(epaId, pi), piLevel, langCode);
		if (result == null) {
			result = "";
		}
		return result;
	}

	private String getEpaName(String epaUnbId, LanguageCode lang) {
		String result = "";
		EpaMappedField epaMappedField = getManager(domainId).getFormMapper().getMappedEpaByUnb(epaUnbId);
		if (epaMappedField != null) {
			result = epaMappedField.getTranslation(lang);
		}
		return result;
	}

	private PortfolioModel mapPortfolioResult(JitPortfolioResultDataObject jitPortfolioResult, LanguageCode langCode) throws FileNotFoundException {
		if (jitPortfolioResult == null)
			return null;
		String feedbackSeekingStrategy = getFeedbackSeekingStrategy(jitPortfolioResult.getFeedbackSeekingStrategyUnbId(), langCode);
		String portfolioConsistency = getPortfolioConsistency(jitPortfolioResult.getPortfolioConsistencyUnbId(), langCode);
		String informationLevel = getInformationLevel(jitPortfolioResult.getEpaNeedingInformationUnbIds(), langCode);
		if (feedbackSeekingStrategy != null || portfolioConsistency != null || informationLevel != null) {
			return new PortfolioModel(feedbackSeekingStrategy, portfolioConsistency, informationLevel);
		}
		return null;
	}

	private String getFeedbackSeekingStrategy(String feedbackSeekingStrategyUnbId, LanguageCode langCode) {
		FeedbackSeekingStrategyUnb feedbackSeekingStrategyEnumItem = FeedbackSeekingStrategyUnb.fromValue(feedbackSeekingStrategyUnbId);
		if (feedbackSeekingStrategyEnumItem != FeedbackSeekingStrategyUnb.POOR) {
			return null;
		}
		return getManager(domainId).getFeedbackSeekingStrategyMessage(FeedbackSeekingStrategyUnb.POOR, langCode);
	}

	private String getPortfolioConsistency(String portfolioConsistencyUnbId, LanguageCode langCode) {
		PortfolioConsistencyUnb portfolioConsistencyEnumItem = PortfolioConsistencyUnb.fromValue(portfolioConsistencyUnbId);
		if (portfolioConsistencyEnumItem != PortfolioConsistencyUnb.INCONSISTENCY_HIGH) {
			return null;
		}
		return getManager(domainId).getPortfolioConsistencyMessage(PortfolioConsistencyUnb.INCONSISTENCY_HIGH, langCode);
	}

	private String getInformationLevel(List<String> epaNeedingInformationUnbIds, LanguageCode langCode) throws FileNotFoundException {
		if (epaNeedingInformationUnbIds == null || epaNeedingInformationUnbIds.isEmpty())
			return null;
		String template = getManager(domainId).getInformationLevelTemplate(langCode);
		if (template == null)
			return null;
		JitResponseMapper mapper = getMapper(domainId);
		String joinedEpaNames = epaNeedingInformationUnbIds.stream().map(epaUnbId -> ("\"" + mapper.getEpaName(epaUnbId, langCode) + "\"")).collect(Collectors.joining(", "));
		return mMessageFormatter.formatPortfolioInformationLevel(template, joinedEpaNames);
	}

	public SupervisedStudentPortfolioModel processPortfolioResults(JitPortfolioResultDataObject jitPortfolioResult, LanguageCode languageCode) throws FileNotFoundException {
		if (jitPortfolioResult == null)
			return null;
		if (!StaticModelManager.getManager(domainId).isLanguageSupported(languageCode)) {
			languageCode = ApplicationSettings.DEFAULT_LANGUAGE;
		}
		String feedbackSeekingStrategy = getSupervisorFeedbackSeekingStrategy(jitPortfolioResult.getFeedbackSeekingStrategyUnbId(), languageCode);
		String portfolioConsistency = getSupervisorPortfolioConsistency(jitPortfolioResult.getPortfolioConsistencyUnbId(), languageCode);
		String informationLevel = getSupervisorInformationLevel(jitPortfolioResult.getEpaNeedingInformationUnbIds(), languageCode);
		String frustrationAlert = getSupervisorFrustrationAlert(jitPortfolioResult.getFrustrationUnbId(), languageCode);
		if (feedbackSeekingStrategy != null || portfolioConsistency != null || informationLevel != null || frustrationAlert != null) {
			return new SupervisedStudentPortfolioModel(feedbackSeekingStrategy, portfolioConsistency, informationLevel, frustrationAlert);
		}
		return null;
	}

	private String getSupervisorFeedbackSeekingStrategy(String feedbackSeekingStrategyUnbId, LanguageCode langCode) {
		FeedbackSeekingStrategyUnb feedbackSeekingStrategyEnumItem = FeedbackSeekingStrategyUnb.fromValue(feedbackSeekingStrategyUnbId);
		if (feedbackSeekingStrategyEnumItem != FeedbackSeekingStrategyUnb.POOR) {
			return null;
		}
		return getManager(domainId).getSupervisorFeedbackSeekingStrategyMessage(FeedbackSeekingStrategyUnb.POOR, langCode);
	}

	private String getSupervisorPortfolioConsistency(String portfolioConsistencyUnbId, LanguageCode langCode) {
		PortfolioConsistencyUnb portfolioConsistencyEnumItem = PortfolioConsistencyUnb.fromValue(portfolioConsistencyUnbId);
		if (portfolioConsistencyEnumItem != PortfolioConsistencyUnb.INCONSISTENCY_HIGH) {
			return null;
		}
		return getManager(domainId).getSupervisorPortfolioConsistencyMessage(PortfolioConsistencyUnb.INCONSISTENCY_HIGH, langCode);
	}

	private String getSupervisorInformationLevel(List<String> epaNeedingInformationUnbIds, LanguageCode langCode) throws FileNotFoundException {
		if (epaNeedingInformationUnbIds == null || epaNeedingInformationUnbIds.isEmpty())
			return null;
		String template = getManager(domainId).getSupervisorInformationLevelTemplate(langCode);
		if (template == null)
			return null;
		JitResponseMapper mapper = getMapper(domainId);
		String joinedEpaNames = epaNeedingInformationUnbIds.stream().map(epaUnbId -> ("\"" + mapper.getEpaName(epaUnbId, langCode) + "\"")).collect(Collectors.joining(", "));
		return mMessageFormatter.formatPortfolioInformationLevel(template, joinedEpaNames);
	}

	private String getSupervisorFrustrationAlert(String frustrationAlertUnbId, LanguageCode langCode) {
		FrustrationAlertUnb frustrationAlertEnumItem = FrustrationAlertUnb.fromValue(frustrationAlertUnbId);
		if (frustrationAlertEnumItem != FrustrationAlertUnb.FRUSTRATION_HIGH) {
			return null;
		}
		return getManager(domainId).getSupervisorFrustrationAlertMessage(FrustrationAlertUnb.FRUSTRATION_HIGH, langCode);
	}
}
