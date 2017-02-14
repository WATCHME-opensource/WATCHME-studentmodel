/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 11/2/2016
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

import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.commons.staticdata.model.form.json.*;
import eu.watchme.modules.commons.staticdata.model.form.mapper.FormMapper;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotEpaDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotNumericalPiDataObject;
import eu.watchme.modules.domainmodel.viz.timeline.*;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TimelineResponseMapper {
	private static final HashMap<String, TimelineResponseMapper> sResponseMappersMap = new HashMap<>();
	private String mModelId;

	private TimelineResponseMapper(String modelId) {
		mModelId = modelId;
	}

	static TimelineResponseMapper getMapper(String modelId) {
		synchronized (sResponseMappersMap) {
			if (!sResponseMappersMap.containsKey(modelId)) {
				sResponseMappersMap.put(modelId, new TimelineResponseMapper(modelId));
			}
			return sResponseMappersMap.get(modelId);
		}
	}

	public TimelineResponseModel map(Set<String> epaIdsForFiltering, LanguageCode languageCode, List<TimelineSnapshotDataObject> timelineSnapshots) {
		if (timelineSnapshots == null) {
			timelineSnapshots = new ArrayList<>(0);
		}
		timelineSnapshots = timelineSnapshots.stream().filter(timelineSnapshot -> timelineSnapshot.getEpas() != null && !timelineSnapshot.getEpas().isEmpty()).collect(Collectors.toList());
		List<ResponseRoleModel> roles = getRoles(mModelId, epaIdsForFiltering, timelineSnapshots, languageCode);
		Instant modelTimestamp = timelineSnapshots.isEmpty() ? Instant.now(Clock.systemUTC()) : timelineSnapshots.get(timelineSnapshots.size() - 1).getReceivedDate();
		return new TimelineResponseModel(roles, modelTimestamp);
	}

	private List<ResponseRoleModel> getRoles(String modelId, Set<String> epaIdsForFiltering, List<TimelineSnapshotDataObject> timelineSnapshots, LanguageCode languageCode) {
		List<VizRoleField> allRoles = StaticModelManager.getManager(modelId).getFormMapper().getVizStructureMapper().getAllRoles();
		if (!epaIdsForFiltering.isEmpty()) {
			allRoles = allRoles.stream()
					.map(vizRoleField -> filterEpas(vizRoleField.getCopy(), epaIdsForFiltering))
					.filter(vizRoleField -> !vizRoleField.getEpas().isEmpty())
					.collect(Collectors.toList());
		}
		return allRoles.stream().map(vizRoleField -> getRole(modelId, timelineSnapshots, languageCode, vizRoleField)).collect(Collectors.toList());
	}

	private VizRoleField filterEpas(VizRoleField vizRoleField, Set<String> epaIdsForFiltering) {
		List<VizEpaField> filteredEpas = vizRoleField.getEpas().stream().filter(epa -> epaIdsForFiltering.stream().anyMatch(filter -> filter.endsWith(epa.getEpassId()))).collect(Collectors.toList());
		vizRoleField.setEpas(filteredEpas);
		return vizRoleField;
	}

	private ResponseRoleModel getRole(String modelId, List<TimelineSnapshotDataObject> timelineSnapshots, LanguageCode languageCode, VizRoleField role) {
		ResponseRoleModel responseRoleModel = new ResponseRoleModel();
		responseRoleModel.setName(role.getName(languageCode));
		responseRoleModel.setEPAs(getEpas(modelId, timelineSnapshots, languageCode, role.getEpas()));
		return responseRoleModel;
	}

	private List<ResponseEpaModel> getEpas(String modelId, List<TimelineSnapshotDataObject> timelineSnapshots, LanguageCode languageCode, List<VizEpaField> allEpas) {
		HashMap<String, ResponseEpaModel> epasMap = new HashMap<>();
		return allEpas == null ?
				new ArrayList<>(0) :
				allEpas.stream().map(vizEpaField -> getEpa(epasMap, vizEpaField, modelId, languageCode, timelineSnapshots)).filter(epa -> epa != null).collect(Collectors.toList());
	}

	private ResponseEpaModel getEpa(HashMap<String, ResponseEpaModel> epasMap, VizEpaField vizEpaField, String modelId, LanguageCode languageCode,
			List<TimelineSnapshotDataObject> timelineSnapshotsForEpa) {
		EpaMappedField epa = StaticModelManager.getManager(modelId).getFormMapper().getMappedEpaByEpass(vizEpaField.getEpassId());
		if (epa != null) {
			ResponseEpaModel responseRoleEpaModel;
			if (epasMap.get(epa.getEpassId()) == null) {
				responseRoleEpaModel = new ResponseEpaModel();
				responseRoleEpaModel.setEpassId(epa.getEpassId());
				responseRoleEpaModel.setName(epa.getShortNameTranslation(languageCode));
				responseRoleEpaModel.setDescription(epa.getTranslation(languageCode));
				timelineSnapshotsForEpa = timelineSnapshotsForEpa.stream().filter(timelineSnapshot -> timelineSnapshot.hasEpa(epa.getUnbId())).collect(Collectors.toList());
				responseRoleEpaModel.setPerformanceIndicators(getPerformanceIndicators(modelId, timelineSnapshotsForEpa, languageCode, vizEpaField.getPIs(), epa.getUnbId()));
				responseRoleEpaModel.setLevels(getEpaLevels(epa.getUnbId(), responseRoleEpaModel.getPerformanceIndicators(), timelineSnapshotsForEpa));
				epasMap.put(epa.getEpassId(), responseRoleEpaModel);
			} else {
				responseRoleEpaModel = epasMap.get(epa.getEpassId());
			}
			return responseRoleEpaModel;
		}
		return null;
	}

	private List<ResponsePIModel> getPerformanceIndicators(String modelId, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa, LanguageCode languageCode, List<VizPIField> allPis,
			String epaUnbId) {
		List<ResponsePIModel> performanceIndicators;
		if (allPis != null) {
			performanceIndicators = allPis.stream().map(vizPIField -> getPi(modelId, epaUnbId, vizPIField.getUnbId(), timelineSnapshotsForEpa, languageCode)).filter(pi -> pi != null)
					.collect(Collectors.toList());
		} else {
			performanceIndicators = new ArrayList<>(0);
		}
		return performanceIndicators;
	}

	private ResponsePIModel getPi(String modelId, String epaUnbId, String piUnbId, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa, LanguageCode languageCode) {
		List<PIMappedField> listPiMappedField = StaticModelManager.getManager(modelId).getFormMapper().getMappedPerformanceIndicatorByUnb(epaUnbId, piUnbId);
		if (listPiMappedField != null && !listPiMappedField.isEmpty()) {
			PIMappedField piMappedField = listPiMappedField.get(0);
			ResponsePIModel responseRoleEpaPIModel = new ResponsePIModel();
			responseRoleEpaPIModel.setName(piMappedField.getShortNameTranslation(languageCode));
			responseRoleEpaPIModel.setDescription(piMappedField.getTranslation(languageCode));
			responseRoleEpaPIModel.setLevels(getPiLevels(epaUnbId, piUnbId, timelineSnapshotsForEpa));
			return responseRoleEpaPIModel;
		}
		return null;
	}

	private List<ResponseLevelModel> getPiLevels(String epaUnbId, String piUnbId, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa) {
		List<ResponseLevelModel> results = new ArrayList<>();
		if (timelineSnapshotsForEpa != null) {
			timelineSnapshotsForEpa.stream().filter(timelineSnapshot -> timelineSnapshot.hasPi(epaUnbId, piUnbId)).forEach(result -> addLevel(epaUnbId, piUnbId, results, result));
			results.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
		}
		return results;
	}

	private void addLevel(String epaUnbId, String piUnbId, List<ResponseLevelModel> results, TimelineSnapshotDataObject timelineSnapshotDataObject) {
		TimelineSnapshotNumericalPiDataObject piSnapshot = timelineSnapshotDataObject.getPiSnapshot(epaUnbId, piUnbId);
		if (piSnapshot != null) {
			FormMapper formMapper = StaticModelManager.getManager(mModelId).getFormMapper();
			int levelForSelf = formMapper.getMappedPerformanceIndicatorLevelByUnb(piSnapshot.getActualScoreUnbId()).getRank();
			ResponseLevelModel levelModel = new ResponseLevelModel();
			levelModel.setScore(levelForSelf);
			levelModel.setDate(timelineSnapshotDataObject.getSubmissionDate());
			levelModel.setFormId(timelineSnapshotDataObject.getFormId());
			levelModel.setSubmissionId(timelineSnapshotDataObject.getSubmissionId());
			results.add(levelModel);
		}
	}

	private List<ResponseLevelModel> getEpaAverageLevels(String epaUnbId, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa) {
		List<ResponseLevelModel> results = new ArrayList<>();
		if (timelineSnapshotsForEpa != null) {
			timelineSnapshotsForEpa.stream().filter(timelineSnapshot -> timelineSnapshot.hasEpaAverageLevel(epaUnbId)).forEach(result -> addAverageLevel(epaUnbId, results, result));
			results.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
		}
		return results;
	}

	private void addAverageLevel(String epaUnbId, List<ResponseLevelModel> results, TimelineSnapshotDataObject timelineSnapshotDataObject) {
		TimelineSnapshotEpaDataObject epaSnapshot = timelineSnapshotDataObject.getEpaSnapshot(epaUnbId);
		if (epaSnapshot != null && epaSnapshot.getActualScoreUnbId() != null) {
			FormMapper formMapper = StaticModelManager.getManager(mModelId).getFormMapper();
			int levelForSelf = formMapper.getMappedPerformanceIndicatorLevelByUnb(epaSnapshot.getActualScoreUnbId()).getRank();
			ResponseLevelModel levelModel = new ResponseLevelModel();
			levelModel.setScore(levelForSelf);
			levelModel.setDate(timelineSnapshotDataObject.getSubmissionDate());
			levelModel.setFormId(timelineSnapshotDataObject.getFormId());
			levelModel.setSubmissionId(timelineSnapshotDataObject.getSubmissionId());
			results.add(levelModel);
		}
	}

	private List<ResponseLevelModel> getEpaLevels(String epaUnbId, List<ResponsePIModel> performanceIndicators, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa) {
		List<ResponseLevelModel> allPiLevels = new ArrayList<>();
		performanceIndicators.stream().filter(pi -> pi.getLevels() != null).forEach(pi -> allPiLevels.addAll(pi.getLevels()));
		List<ResponseLevelModel> epaLevelAverageLevels = getEpaAverageLevels(epaUnbId, timelineSnapshotsForEpa);
		allPiLevels.addAll(epaLevelAverageLevels);

		List<ResponseLevelModel> allEpaLevels = new ArrayList<>();
		Map<Instant, Map<Integer, List<ResponseLevelModel>>> mappedToSubmission = allPiLevels.stream()
				.collect(Collectors.groupingBy(ResponseLevelModel::getDate, Collectors.groupingBy(ResponseLevelModel::getSubmissionId)));
		mappedToSubmission.keySet().stream().forEach(instant -> {
			List<ResponseLevelModel> list = mapToEpaLevel(instant, mappedToSubmission.get(instant));
			allEpaLevels.addAll(list);
		});
		allEpaLevels.stream().forEach(epaLevel -> updateSentiment(epaUnbId, epaLevel, timelineSnapshotsForEpa));
		allEpaLevels.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
		return allEpaLevels;
	}

	private void updateSentiment(String epaUnbId, ResponseLevelModel epaLevel, List<TimelineSnapshotDataObject> timelineSnapshotsForEpa) {
		Optional<TimelineSnapshotDataObject> queryResult = timelineSnapshotsForEpa.stream().filter(result -> result.getSubmissionId().equals(epaLevel.getSubmissionId())).findFirst();
		if (queryResult.isPresent()) {
			TimelineSnapshotEpaDataObject epaSnapshot = queryResult.get().getEpaSnapshot(epaUnbId);
			if (epaSnapshot != null) {
				epaLevel.setSentiment(epaSnapshot.getSentiment());
			}
		}
	}

	private List<ResponseLevelModel> mapToEpaLevel(Instant instant, Map<Integer, List<ResponseLevelModel>> mappedToSubmission) {
		return mappedToSubmission.keySet().stream().map(submissionId -> getAggregatedLevel(instant, mappedToSubmission.get(submissionId))).collect(Collectors.toList());
	}

	private ResponseLevelModel getAggregatedLevel(Instant instant, List<ResponseLevelModel> list) {
		ResponseLevelModel epaLevelModel = new ResponseLevelModel();
		epaLevelModel.setDate(instant);
		epaLevelModel.setFormId(list.isEmpty() ? 0 : list.get(0).getFormId());
		epaLevelModel.setScore(getAverage(list));
		epaLevelModel.setSubmissionId(list.isEmpty() ? null : list.get(0).getSubmissionId());
		return epaLevelModel;
	}

	private double getAverage(List<ResponseLevelModel> list) {
		OptionalDouble optionalDouble = list.stream().filter(level -> level.getScore() > 0.0f).mapToDouble(ResponseLevelModel::getScore).average();
		return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 0.0f;
	}
}
