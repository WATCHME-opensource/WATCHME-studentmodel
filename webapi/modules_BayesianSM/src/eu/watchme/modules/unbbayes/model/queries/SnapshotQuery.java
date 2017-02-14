/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 5/4/2016
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

package eu.watchme.modules.unbbayes.model.queries;

import eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject;
import eu.watchme.modules.unbbayes.model.queries.results.QueryResult;
import eu.watchme.modules.unbbayes.model.queries.results.ResultsInterpreter;
import eu.watchme.modules.unbbayes.model.queries.results.SnapshotResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SnapshotQuery extends Query<SnapshotResult> {
	private static final String NODE_LEVEL_EPA = "getLevelEpa";
	private static final String NODE_FEEDBACK_PI = "getFeedbackEpaPI";

	public SnapshotQuery(Map<String, List<String>> epaMap, ResultsInterpreter<SnapshotResult> interpreter, StudentTimeMapDataObject bayesTime) {
		super(interpreter);
		super.setQueriesList(getQueryItems(epaMap, bayesTime));
	}

	@Override
	public List<QueryResult> filterResults(List<QueryResult> resultsToFilter) {
		if (resultsToFilter == null) {
			return Collections.emptyList();
		}

		return resultsToFilter;
	}

	private List<QueryItem> getQueryItems(Map<String, List<String>> epaMap, StudentTimeMapDataObject bayesTime) {
		List<QueryItem> queryItemList = epaMap.keySet().stream()
				.map(unbEpaId -> new QueryItem(NODE_LEVEL_EPA, Arrays.asList(unbEpaId, bayesTime.getUnbId())))
				.collect(Collectors.toList());

		for (Map.Entry<String, List<String>> item : epaMap.entrySet()) {
			queryItemList.addAll(item.getValue().stream()
					.map(pi -> new QueryItem(NODE_FEEDBACK_PI, Arrays.asList(item.getKey(), pi, bayesTime.getUnbId())))
					.collect(Collectors.toList()));
		}

		return queryItemList;
	}
}
