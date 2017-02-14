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

package eu.watchme.modules.unbbayes.model.queries;

import eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject;
import eu.watchme.modules.unbbayes.model.queries.results.FeedbackResult;
import eu.watchme.modules.unbbayes.model.queries.results.ResultsInterpreter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedbackForSpecificEpasAllPIAllFB extends Query<FeedbackResult> {
	private static final String NODE_LEVEL_EPA = "getLevelEpa";
	private static final String NODE_FEEDBACK_PI = "getFeedbackEpaPI";
	private static final String NODE_FEEDBACK_SUPERVISOR = "hasSupervisorFeedbackEPA";

	public FeedbackForSpecificEpasAllPIAllFB(Map<String, List<String>> epaMap, HashSet<String> epasWithGeneralLevel,
			ResultsInterpreter<FeedbackResult> interpreter, StudentTimeMapDataObject bayesTime) {
		super(interpreter);
		super.setQueriesList(getQueryItems(epaMap, epasWithGeneralLevel, bayesTime));
	}

	private List<QueryItem> getQueryItems(Map<String, List<String>> epaMap, HashSet<String> epasWithGeneralLevel, StudentTimeMapDataObject bayesTime) {
		List<QueryItem> epaQueryItemList = epasWithGeneralLevel.stream().map(unbEpaId -> new QueryItem(NODE_LEVEL_EPA, Arrays.asList(unbEpaId, bayesTime.getUnbId()))).collect(Collectors.toList());
		List<QueryItem> epaPiQueryItemList = epaMap.keySet().stream()
				.map(unbEpaId -> new QueryItem(NODE_FEEDBACK_SUPERVISOR, Arrays.asList(unbEpaId, bayesTime.getUnbId())))
				.collect(Collectors.toList());
		epaQueryItemList.addAll(epaPiQueryItemList);

		for (Map.Entry<String, List<String>> item : epaMap.entrySet()) {
			epaQueryItemList.addAll(item.getValue().stream()
					.map(pi -> new QueryItem(NODE_FEEDBACK_PI, Arrays.asList(item.getKey(), pi, bayesTime.getUnbId())))
					.collect(Collectors.toList()));
		}

		return epaQueryItemList;
	}
}
