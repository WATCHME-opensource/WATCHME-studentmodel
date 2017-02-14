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

package eu.watchme.modules.unbbayes.model.queries.results;

import eu.watchme.modules.unbbayes.model.findings.GetLevel;
import eu.watchme.modules.unbbayes.model.findings.GetLevelEpa;
import eu.watchme.modules.unbbayes.model.findings.HasSupervisorFeedbackEPA;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FeedbackResultsInterpreter implements ResultsInterpreter<FeedbackResult> {
	public static final FeedbackResultsInterpreter feedbackInterpreter = new FeedbackResultsInterpreter();

	@Override
	public FeedbackResult interpret(QueryResult result) {
		String epa = result.getQueryItem().getNodeArguments().get(0);
		String performanceIndicator;
		String feedbackType;
		if (result.getQueryItem().getNodeName().equals(GetLevelEpa.emptyLevel.getNodeName())) {
			performanceIndicator = "*";
			feedbackType = "FB_IMPROVEMENT_POSITIVE";
		} else if (result.getQueryItem().getNodeName().equals(HasSupervisorFeedbackEPA.emptyLevel.getNodeName())) {
			performanceIndicator = "*";
			feedbackType = "FB_SUPERVISOR";
		} else {
			performanceIndicator = getPerformanceIndicator(result);
			feedbackType = result.getProbableState().getStateName();
		}
		String time = getTime(result);
		String level = getLevelPI(result, time);
		return new FeedbackResult(feedbackType, epa, performanceIndicator, time, level);
	}

	public String getLevelPI(QueryResult result, String time) {
		String nodeName = GetLevel.emptyLevel.getNodeName();
		String epaLevelNodeName = GetLevelEpa.emptyLevel.getNodeName();
		List<Evidence> sortedEvidences = result.getProbableState().getEvidences().stream().filter(e -> e.getEvidenceNodeName().equals(nodeName) || e.getEvidenceNodeName().equals(epaLevelNodeName))
				.sorted((e1, e2) -> compareEvidenceTimes(e1.getEvidenceTime(), e2.getEvidenceTime())).collect(Collectors.toList());
		String evidenceNodeState = "*";
		Optional<Evidence> firstForTime = sortedEvidences.stream().filter(e -> e.getEvidenceTime().equals(time)).findFirst();
		if (firstForTime.isPresent()) {
			evidenceNodeState = firstForTime.get().getEvidenceNodeState();
		} else if (!sortedEvidences.isEmpty()) {
			evidenceNodeState = sortedEvidences.get(0).getEvidenceNodeState();
		}
		return evidenceNodeState;
	}

	private int compareEvidenceTimes(String time1, String time2) {
		try {
			int t1 = Integer.parseInt(time1.substring(1));
			int t2 = Integer.parseInt(time2.substring(1));
			return t2 - t1;
		} catch (Exception e) {
			return time2.compareToIgnoreCase(time1);
		}
	}

	protected String getPerformanceIndicator(QueryResult result) {
		String fv = result.getFunctionValue();
		int commaIndex = fv.indexOf(',');
		if (commaIndex >= 0) {
			String secondChunk = fv.substring(commaIndex + 1, fv.indexOf(')'));
			String[] tokens = secondChunk.split(",");
			if (tokens.length > 1)
				return tokens[0];
			else
				return "*";
		} else {
			//this is a generic PI (e.g. some EPA value without any PI)
			return "*";
		}
	}

	protected String getTime(QueryResult result) {
		String fv = result.getFunctionValue();
		int commaIndex = fv.indexOf(',');
		if (commaIndex >= 0) {
			String secondChunk = fv.substring(commaIndex + 1, fv.indexOf(')'));
			String[] tokens = secondChunk.split(",");
			if (tokens.length > 1)
				return tokens[1];
			else
				return tokens[0];
		} else {
			//this is a generic PI (e.g. some EPA value without any PI)
			return "*";
		}
	}
}
