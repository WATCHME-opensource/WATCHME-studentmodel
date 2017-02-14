/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 27/11/2015
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

package eu.watchme.modules.dispatcher.viz;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.commons.staticdata.model.form.json.*;
import eu.watchme.modules.dataaccess.adapters.StudentStatsAdapter;
import eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.VizPredictedLevelDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.VizQueryResultDataObject;
import eu.watchme.modules.domainmodel.viz.current_performance.*;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;
import static java.util.stream.Collectors.groupingBy;

class CurrentPerformanceResponseMapper {
	private static final HashMap<String, CurrentPerformanceResponseMapper> sResponseMappersMap = new HashMap<>();
	private String mModelId;

	private CurrentPerformanceResponseMapper(String modelId) {
		mModelId = modelId;
	}

	static CurrentPerformanceResponseMapper getMapper(String modelId) {
		synchronized (sResponseMappersMap) {
			if (!sResponseMappersMap.containsKey(modelId)) {
				sResponseMappersMap.put(modelId, new CurrentPerformanceResponseMapper(modelId));
			}
			return sResponseMappersMap.get(modelId);
		}
	}

	public CurrentPerformanceResponseModel map(CurrentPerformanceQueryModel object, VizQueryResultDataObject vizQueryResultDataObject, HashMap<String, Double> epaCohortLevel) {
		Map<String, List<VizPredictedLevelDataObject>> groupedByEpa = vizQueryResultDataObject == null || vizQueryResultDataObject.getPredictedLevels() == null ?
				new HashMap<>(0) :
				vizQueryResultDataObject.getPredictedLevels().stream().collect(groupingBy(
						VizPredictedLevelDataObject::getEpaUnbId));
		final LanguageCode languageCode = getLanguageCode(object);
		List<VizRoleField> allRoles = StaticModelManager.getManager(object.getModelId()).getFormMapper().getVizStructureMapper().getAllRoles();
		if (!object.getEpaIdsForFiltering().isEmpty()) {
			allRoles = allRoles.stream()
					.map(vizRoleField -> filterEpas(vizRoleField.getCopy(), object.getEpaIdsForFiltering()))
					.filter(vizRoleField -> !vizRoleField.getEpas().isEmpty())
					.collect(Collectors.toList());
		}

		List<ResponseRoleModel> list = new ArrayList<>(allRoles.size());
		for (VizRoleField role : allRoles) {
			ResponseRoleModel responseRoleModel = getResponseRoleModel(object, groupedByEpa, languageCode, role, epaCohortLevel);
			list.add(responseRoleModel);
		}

		return new CurrentPerformanceResponseModel(list, Instant.now(Clock.systemUTC()));
	}

	private VizRoleField filterEpas(VizRoleField vizRoleField, Set<String> epaIdsForFiltering) {
		List<VizEpaField> filteredEpas = vizRoleField.getEpas().stream().filter(epa -> epaIdsForFiltering.stream().anyMatch(filter -> filter.endsWith(epa.getEpassId()))).collect(Collectors.toList());
		vizRoleField.setEpas(filteredEpas);
		return vizRoleField;
	}

	private ResponseRoleModel getResponseRoleModel(CurrentPerformanceQueryModel object, Map<String, List<VizPredictedLevelDataObject>> groupedByEpa, LanguageCode languageCode, VizRoleField role,
			HashMap<String, Double> epaCohortLevel) {
		ResponseRoleModel responseRoleModel = new ResponseRoleModel();
		responseRoleModel.setName(role.getName(languageCode));
		List<ResponseRoleEpaModel> epas = null;
		if (role.getEpas() != null) {
			epas = new ArrayList<>(role.getEpas().size());
			for (VizEpaField vizEpaField : role.getEpas()) {
				EpaMappedField epa = StaticModelManager.getManager(object.getModelId()).getFormMapper().getMappedEpaByEpass(vizEpaField.getEpassId());
				if (epa != null) {
					ResponseRoleEpaModel responseRoleEpaModel = new ResponseRoleEpaModel();
					responseRoleEpaModel.setName(epa.getShortNameTranslation(languageCode));
					responseRoleEpaModel.setEpassId(epa.getEpassId());
					responseRoleEpaModel.setDescription(epa.getTranslation(languageCode));
					List<VizPredictedLevelDataObject> predictedLevels = groupedByEpa.get(epa.getUnbId());
					List<ResponseRoleEpaPIModel> performanceIndicators = getResponseRoleEpaPIModels(object, predictedLevels, languageCode, epa.getUnbId(), vizEpaField.getPIs());
					responseRoleEpaModel.setPerformanceIndicators(performanceIndicators);
					ResponseRoleEpaPILevelModel epaLevelAverage = predictedLevels == null ? null : getLevelForEpaAverage(languageCode, predictedLevels);
					double average = computeEpaLevel(performanceIndicators, epaLevelAverage);
					responseRoleEpaModel.setLevel(getResponseRoleEpaLevelModel(average, epaCohortLevel.get(epa.getUnbId())));
					responseRoleEpaModel.setDeliveredForms(getDeliveredFormsCount(object.getModelId(), object.getAuthorisationData().getStudentHash(), epa.getUnbId()));
					epas.add(responseRoleEpaModel);
				}
			}
		}
		responseRoleModel.setEPAs(epas);
		return responseRoleModel;
	}

	private double computeEpaLevel(List<ResponseRoleEpaPIModel> performanceIndicators, ResponseRoleEpaPILevelModel epaLevelAverage) {
		List<Double> levels = new ArrayList<>();
		if (epaLevelAverage != null) {
			double levelAverage = epaLevelAverage.getSelf();
			if (levelAverage > 0.0f) {
				levels.add(levelAverage);
			}
		}
		if (performanceIndicators != null) {
			levels.addAll(performanceIndicators.stream().filter(pi -> pi.getLevel() != null && pi.getLevel().getSelf() > 0.0f).map(this::getLevelForSelf).collect(Collectors.toList()));
		}
		double average = 0.0f;
		if (!levels.isEmpty()) {
			OptionalDouble optionalDouble = levels.stream().mapToDouble(d -> d).average();
			if (optionalDouble.isPresent()) {
				average = Math.round(optionalDouble.getAsDouble());
			}
		}
		return average;
	}

	private ResponseRoleEpaPILevelModel getLevelForEpaAverage(LanguageCode languageCode, List<VizPredictedLevelDataObject> predictedLevels) {
		Optional<VizPredictedLevelDataObject> optional = predictedLevels.stream().filter(predictedLevel -> predictedLevel.getPerformanceIndicatorUnbId().equals("*")).findFirst();
		return optional.isPresent() ? getResponseRoleEpaPILevelModel(languageCode, optional.get()) : null;
	}

	private List<ResponseRoleEpaPIModel> getResponseRoleEpaPIModels(CurrentPerformanceQueryModel object, List<VizPredictedLevelDataObject> predictedLevels, LanguageCode languageCode, String epaUnbId,
			List<VizPIField> allPIs) {
		List<ResponseRoleEpaPIModel> performanceIndicators = null;
		if (allPIs != null) {
			performanceIndicators = new ArrayList<>(allPIs.size());
			Map<String, VizPredictedLevelDataObject> groupedByPi = map(predictedLevels);
			for (VizPIField vizPIField : allPIs) {
				List<PIMappedField> listPiMappedField = StaticModelManager.getManager(object.getModelId()).getFormMapper().getMappedPerformanceIndicatorByUnb(epaUnbId, vizPIField.getUnbId());
				if (listPiMappedField != null && !listPiMappedField.isEmpty()) {
					PIMappedField piMappedField = listPiMappedField.get(0);
					String piUnbId = piMappedField.getUnbKey().getUnbId();
					ResponseRoleEpaPIModel responseRoleEpaPIModel = new ResponseRoleEpaPIModel();
					responseRoleEpaPIModel.setName(piMappedField.getShortNameTranslation(languageCode));
					responseRoleEpaPIModel.setDescription(piMappedField.getTranslation(languageCode));
					VizPredictedLevelDataObject predictedLevel = groupedByPi.get(piUnbId);
					ResponseRoleEpaPILevelModel levelModel = getResponseRoleEpaPILevelModel(languageCode, predictedLevel);
					responseRoleEpaPIModel.setLevel(levelModel);
					responseRoleEpaPIModel.setDeliveredForms(getDeliveredFormsCount(object.getModelId(), object.getAuthorisationData().getStudentHash(), epaUnbId, piUnbId));
					performanceIndicators.add(responseRoleEpaPIModel);
				}
			}
		}
		return performanceIndicators;
	}

	private int getDeliveredFormsCount(String modelId, String studentId, String epaUnbId) {
		StudentStatsAdapter studentStatsAdapter = new StudentStatsAdapter();
		StudentStatsDataObject studentEpaStats = studentStatsAdapter.getStats(studentId, modelId);
		return studentEpaStats == null ? 0 : studentEpaStats.getSubmissionsCount(epaUnbId);
	}

	private int getDeliveredFormsCount(String modelId, String studentId, String epaUnbId, String piUnbId) {
		StudentStatsAdapter studentStatsAdapter = new StudentStatsAdapter();
		StudentStatsDataObject studentEpaStats = studentStatsAdapter.getStats(studentId, modelId);
		return studentEpaStats == null ? 0 : studentEpaStats.getSubmissionsCount(epaUnbId, piUnbId);
	}

	private ResponseRoleEpaLevelModel getResponseRoleEpaLevelModel(double average, Double cohortAverage) {
		ResponseRoleEpaLevelModel epaLevelModel = new ResponseRoleEpaLevelModel();
		epaLevelModel.setSelf(average);
		epaLevelModel.setGroup(cohortAverage == null ? 0 : cohortAverage);
		return epaLevelModel;
	}

	private ResponseRoleEpaPILevelModel getResponseRoleEpaPILevelModel(LanguageCode languageCode, VizPredictedLevelDataObject predictedLevel) {
		ResponseRoleEpaPILevelModel levelModel = new ResponseRoleEpaPILevelModel(0, null);
		if (predictedLevel != null) {
			int levelForSelf = getLevel(predictedLevel.getLevelUnbId());
			String recommendation = getImprovementRecommendation(predictedLevel.getEpaUnbId(), predictedLevel.getPerformanceIndicatorUnbId(), predictedLevel.getLevelUnbId(), languageCode);
			levelModel.setSelf(levelForSelf);
			levelModel.setRecommendation(recommendation);
		}
		return levelModel;
	}

	private int getLevel(String levelUnbId) {
		RankedMappedField piLevel = getManager(mModelId).getFormMapper().getMappedPerformanceIndicatorLevelByUnb(levelUnbId);
		return piLevel == null ? 0 : piLevel.getRank();
	}

	private Map<String, VizPredictedLevelDataObject> map(List<VizPredictedLevelDataObject> predictedLevels) {
		if (predictedLevels == null) {
			return new HashMap<>(0);
		}
		Map<String, VizPredictedLevelDataObject> resultMap = new HashMap<>(predictedLevels.size());
		for (VizPredictedLevelDataObject predictedLevel : predictedLevels) {
			resultMap.put(predictedLevel.getPerformanceIndicatorUnbId(), predictedLevel);
		}
		return resultMap;
	}

	private LanguageCode getLanguageCode(CurrentPerformanceQueryModel object) {
		LanguageCode languageCode = object.getLanguageCode();
		if (!StaticModelManager.getManager(mModelId).isLanguageSupported(languageCode)) {
			languageCode = ApplicationSettings.DEFAULT_LANGUAGE;
		}
		return languageCode;
	}

	private double getLevelForSelf(ResponseRoleEpaPIModel pi) {
		if (pi.getLevel() == null) {
			return 0;
		}
		return pi.getLevel().getSelf();
	}

	private String getImprovementRecommendation(String epaId, String pi, String piLevel, LanguageCode langCode) {
		String result = getManager(mModelId).getRubricMatrix().getRecommendation(new UnbKey(epaId, pi), piLevel, langCode);
		if (result == null) {
			result = "";
		}
		return result;
	}

}
