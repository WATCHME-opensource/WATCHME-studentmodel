/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 8/6/2016
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

import eu.watchme.modules.datamerger.BayesianNlpFinding;
import eu.watchme.modules.datamerger.BayesianNumericalFinding;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessedSubmission {
	private Integer mFormId;
	private Integer mSubmissionId;
	private String mSubmissionDate;
	private String mSubmissionReferenceDate;
	private String mCohort;
	private String mRole;
	private List<ProcessedEpa> mProcessedEpas;
	private Instant mDateReceived;

	public ProcessedSubmission(Integer formId, Integer submissionId, String submissionDate, String submissionReferenceDate, String cohort, String role, List<ProcessedEpa> processedEpas) {
		mFormId = formId;
		mSubmissionId = submissionId;
		mSubmissionDate = submissionDate;
		mSubmissionReferenceDate = submissionReferenceDate;
		mCohort = cohort;
		mRole = role;
		mProcessedEpas = processedEpas;
		mDateReceived = Instant.now(Clock.systemUTC());
	}

	public Integer getFormId() {
		return mFormId;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public String getSubmissionDate() {
		return mSubmissionDate;
	}

	public String getSubmissionReferenceDate() {
		return mSubmissionReferenceDate;
	}

	public String getCohort() {
		return mCohort;
	}

	public String getRole() {
		return mRole;
	}

	public List<ProcessedEpa> getProcessedEpas() {
		return mProcessedEpas == null ? new ArrayList<>(0) : mProcessedEpas;
	}

	public Instant getDateReceived() {
		return mDateReceived;
	}

	public List<BayesianNumericalFinding> getBayesianNumericalFindings() {
		ArrayList<BayesianNumericalFinding> bayesianNumericalFindings = new ArrayList<>();
		if (mProcessedEpas != null) {
			mProcessedEpas.stream().forEach(processedEpa -> {
				if (processedEpa.getMeanRankNumericOutputData() != null) {
					bayesianNumericalFindings.add(new BayesianNumericalFinding(processedEpa.getUnbId(), processedEpa.getMeanRankNumericOutputData().getLevelUnbId()));
				}
				if (processedEpa.getProcessedPis() != null) {
					bayesianNumericalFindings.addAll(processedEpa.getProcessedPis().stream().filter(processedPi -> processedPi.getNumericOutputData() != null)
							.map(processedPi -> new BayesianNumericalFinding(processedEpa.getUnbId(), processedPi.getUnbId(), processedPi.getNumericOutputData().getLevelUnbId()))
							.collect(Collectors.toList()));
				}
			});
		}
		return bayesianNumericalFindings;
	}

	public List<BayesianNlpFinding> getBayesianNlpFindings() {
		ArrayList<BayesianNlpFinding> bayesianNlpFindings = new ArrayList<>();
		if (mProcessedEpas != null) {
			mProcessedEpas.stream().filter(processedEpa -> processedEpa.getProcessedPis() != null)
					.forEach(processedEpa -> bayesianNlpFindings.addAll(processedEpa.getProcessedPis().stream().filter(processedPi -> processedPi.getNlpOutputData() != null)
							.map(processedPi -> new BayesianNlpFinding(processedEpa.getUnbId(), processedPi.getNlpOutputData().getFeedbackLevel())).collect(Collectors.toList())));
		}
		return bayesianNlpFindings;
	}
}
