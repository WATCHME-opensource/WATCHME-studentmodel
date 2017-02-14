/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 19/5/2016
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

package eu.watchme.modules.dispatcher.epass;

import eu.watchme.modules.dataaccess.model.snapshots.TimelineSnapshotDataObject;
import eu.watchme.modules.ndp.model.NumericOutputData;
import eu.watchme.modules.nlp.model.NlpOutputData;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SubmissionResult {
	private Integer mFormId;
	private Integer mSubmissionId;
	private String mSubmissionReferenceDate;
	private List<String> mEpaList;
	private NlpOutputData mNlpOutputData;
	private NumericOutputData mNumericOutputData;
	private TimelineSnapshotDataObject mUnbResult;
	private HashMap<String, Set<String>> mEpaStats;

	public SubmissionResult(Integer formId, Integer submissionId, String submissionReferenceDate, List<String> epaList, NlpOutputData nlpOutputData, NumericOutputData numericOutputData,
			TimelineSnapshotDataObject unbResult, HashMap<String, Set<String>> epaStats) {
		mFormId = formId;
		mSubmissionId = submissionId;
		mSubmissionReferenceDate = submissionReferenceDate;
		mEpaList = epaList;
		mNlpOutputData = nlpOutputData;
		mNumericOutputData = numericOutputData;
		mUnbResult = unbResult;
		mEpaStats = epaStats;
	}

	public Integer getFormId() {
		return mFormId;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public String getSubmissionReferenceDate() {
		return mSubmissionReferenceDate;
	}

	public NlpOutputData getNlpOutputData() {
		return mNlpOutputData;
	}

	public NumericOutputData getNumericOutputData() {
		return mNumericOutputData;
	}

	public List<String> getEpaList() {
		return mEpaList;
	}

	public TimelineSnapshotDataObject getUnbResult() {
		return mUnbResult;
	}

	public HashMap<String, Set<String>> getEpaStats() {
		return mEpaStats;
	}
}
