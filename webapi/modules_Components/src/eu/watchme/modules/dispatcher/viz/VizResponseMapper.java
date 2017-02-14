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

import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.dataaccess.model.snapshots.StudentSnapshotDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.VizQueryResultDataObject;
import eu.watchme.modules.domainmodel.exceptions.NoMappingAvailableException;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceQueryModel;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceResponseModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryStudentModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseStudentModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineResponseModel;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface VizResponseMapper {

	static CurrentPerformanceResponseModel mapToCurrentPerformanceResult(CurrentPerformanceQueryModel source, VizQueryResultDataObject vizQueryResultDataObject, HashMap<String, Double> epaCohortLevel) {
		CurrentPerformanceResponseMapper mapper = CurrentPerformanceResponseMapper.getMapper(source.getModelId());
		if (mapper == null) {
			throw new NoMappingAvailableException("CurrentPerformanceResponseMapper in domain", source.getModelId());
		}
		return mapper.map(source, vizQueryResultDataObject, epaCohortLevel);
	}

	static TimelineResponseModel mapToTimelineResponse(String modelId, Set<String> epaIdsForFiltering, LanguageCode languageCode, List<TimelineSnapshotDataObject> timelineSnapshots) {
		TimelineResponseMapper mapper = TimelineResponseMapper.getMapper(modelId);
		if (mapper == null) {
			throw new NoMappingAvailableException("TimelineResponseMapper in domain", modelId);
		}
		return mapper.map(epaIdsForFiltering, languageCode, timelineSnapshots);
	}

	static SupervisorResponseStudentModel map(SupervisorQueryStudentModel queryStudent, String modelId, StudentSnapshotDataObject studentSnapshotDataObject) {
		SupervisorResponseMapper mapper = SupervisorResponseMapper.getMapper(modelId);
		if (mapper == null) {
			throw new NoMappingAvailableException("SupervisorResponseMapper in domain", modelId);
		}
		return mapper.map(queryStudent, studentSnapshotDataObject);
	}
}
