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

package eu.watchme.modules.unbbayes.model.queries.results;

import eu.watchme.modules.unbbayes.model.findings.GetLevel;
import eu.watchme.modules.unbbayes.model.findings.GetLevelEpa;

public class SnapshotResultsInterpreter implements ResultsInterpreter<SnapshotResult> {
	public static final SnapshotResultsInterpreter snapshotInterpreter = new SnapshotResultsInterpreter();

	@Override
	public SnapshotResult interpret(QueryResult result) {
		String epa = result.getQueryItem().getNodeArguments().get(0);
		String performanceIndicator = getPerformanceIndicator(result);
		String level = getLevelPI(result);
		return new SnapshotResult(epa, performanceIndicator, level);
	}

	public String getLevelPI(QueryResult result) {
		String evidenceNodeState = "*";
		for (Evidence e : result.getProbableState().getEvidences()) {
			if (e.getEvidenceNodeName().equals(GetLevel.emptyLevel.getNodeName()) || e.getEvidenceNodeName().equals(GetLevelEpa.emptyLevel.getNodeName()))
				evidenceNodeState = e.getEvidenceNodeState();
		}
		return evidenceNodeState;
	}

	protected String getPerformanceIndicator(QueryResult result) {
		String fv = result.getFunctionValue();
		int commaIndex = fv.indexOf(',');
		if (commaIndex >= 0) {
			int nextCommaIndex = fv.indexOf(',', commaIndex + 1);
			if (nextCommaIndex >= 0) {
				return fv.substring(commaIndex + 1, nextCommaIndex);
			}
		}
		//this is a generic PI (e.g. some EPA value without any PI)
		return "*";
	}
}
