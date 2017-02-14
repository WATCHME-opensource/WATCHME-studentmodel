/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 16/2/2016
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

import eu.watchme.modules.commons.staticdata.model.form.json.RankedMappedField;
import eu.watchme.modules.dataaccess.model.snapshots.StudentSnapshotDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotEpaDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotNumericalPiDataObject;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryStudentModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseScoreModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseStudentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;

public class SupervisorResponseMapper {
	private static final HashMap<String, SupervisorResponseMapper> sResponseMappersMap = new HashMap<>();
	private String mModelId;

	private SupervisorResponseMapper(String modelId) {
		mModelId = modelId;
	}

	static SupervisorResponseMapper getMapper(String modelId) {
		synchronized (sResponseMappersMap) {
			if (!sResponseMappersMap.containsKey(modelId)) {
				sResponseMappersMap.put(modelId, new SupervisorResponseMapper(modelId));
			}
			return sResponseMappersMap.get(modelId);
		}
	}

	public SupervisorResponseStudentModel map(SupervisorQueryStudentModel queryStudent, StudentSnapshotDataObject studentSnapshotDataObject) {
		SupervisorResponseStudentModel result = new SupervisorResponseStudentModel();
		result.setName(queryStudent.getStudentName());
		result.setHash(queryStudent.getStudentHash());
		if (studentSnapshotDataObject != null && studentSnapshotDataObject.getTimelineSnapshots() != null) {
			List<SupervisorResponseScoreModel> scores = getScores(studentSnapshotDataObject.getTimelineSnapshots());
			result.setScores(scores);
		}
		return result;
	}

	private List<SupervisorResponseScoreModel> getScores(List<TimelineSnapshotDataObject> timelineSnapshots) {
		List<SupervisorResponseScoreModel> scores = new ArrayList<>(timelineSnapshots.size());
		timelineSnapshots.stream().filter(timelineSnapshot -> timelineSnapshot.getEpas() != null && !timelineSnapshot.getEpas().isEmpty()).forEach(timelineSnapshot -> {
			SupervisorResponseScoreModel score = new SupervisorResponseScoreModel();
			score.setDate(timelineSnapshot.getSubmissionDate());
			score.setScore(averageEpaScores(timelineSnapshot.getEpas()));
			scores.add(score);
		});
		return scores;
	}

	private double averageEpaScores(List<TimelineSnapshotEpaDataObject> epaSnapshots) {
		double sum = 0;
		for (TimelineSnapshotEpaDataObject epaSnapshot : epaSnapshots) {
			if (epaSnapshot.getPerformanceIndicators() == null || epaSnapshot.getPerformanceIndicators().isEmpty()) {
				sum += getScore(epaSnapshot.getActualScoreUnbId());
			} else {
				sum += computeEpaScore(epaSnapshot.getPerformanceIndicators());
			}
		}
		return sum / epaSnapshots.size();
	}

	private double getScore(String levelUnbId) {
		if (levelUnbId == null)
			return 0;

		RankedMappedField field = getManager(mModelId).getFormMapper().getMappedPerformanceIndicatorLevelByUnb(levelUnbId);
		if (field != null) {
			return field.getRank();
		}
		return 0;
	}

	private double computeEpaScore(List<TimelineSnapshotNumericalPiDataObject> piSnapshots) {
		int count = 0;
		double sum = 0;
		for (TimelineSnapshotNumericalPiDataObject piSnapshot : piSnapshots) {
			RankedMappedField field = getManager(mModelId).getFormMapper().getMappedPerformanceIndicatorLevelByUnb(piSnapshot.getActualScoreUnbId());
			if (field != null) {
				sum += field.getRank();
				count++;
			}
		}
		return sum / count;
	}
}
