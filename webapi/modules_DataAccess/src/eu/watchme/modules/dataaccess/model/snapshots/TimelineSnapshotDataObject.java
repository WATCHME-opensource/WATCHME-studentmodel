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
package eu.watchme.modules.dataaccess.model.snapshots;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Embedded
public class TimelineSnapshotDataObject {

	@Property(Properties.FormId)
	private Integer mFormId;
	@Property(Properties.SubmissionId)
	private Integer mSubmissionId;
	@Property(Properties.SubmissionDate)
	private Instant mSubmissionDate;
	@Property(Properties.Cohort)
	private String mCohort;
	@Property(Properties.ReceivedDate)
	private Instant mReceivedDate;
	@Embedded(Properties.Epas)
	private List<TimelineSnapshotEpaDataObject> mEpas;

	public TimelineSnapshotDataObject() {

	}

	public TimelineSnapshotDataObject(Integer formId, Integer submissionId, Instant submissionDate, String cohort, Instant receivedDate,
			List<TimelineSnapshotEpaDataObject> epas) {
		mFormId = formId;
		mSubmissionId = submissionId;
		mSubmissionDate = submissionDate;
		mCohort = cohort;
		mReceivedDate = receivedDate;
		mEpas = epas;
	}

	public Integer getFormId() {
		return mFormId;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public Instant getSubmissionDate() {
		return mSubmissionDate;
	}

	public String getCohort() {
		return mCohort;
	}

	public Instant getReceivedDate() {
		return mReceivedDate;
	}

	public List<TimelineSnapshotEpaDataObject> getEpas() {
		return mEpas;
	}

	public boolean hasEpa(String unbId) {
		return mEpas != null && mEpas.stream().filter(epa -> epa.getUnbId().equals(unbId)).count() > 0;
	}

	public boolean hasEpaAverageLevel(String unbId) {
		return mEpas != null && mEpas.stream().filter(epa -> epa.getUnbId().equals(unbId) && epa.getActualScoreUnbId() != null).count() > 0;
	}

	public boolean hasPi(String epaUnbId, String piUnbId) {
		return mEpas != null && mEpas.stream().filter(epa -> epa.getUnbId().equals(epaUnbId) && epa.hasPi(piUnbId)).count() > 0;
	}

	public TimelineSnapshotEpaDataObject getEpaSnapshot(String epaUnbId) {
		if (mEpas == null)
			return null;
		Optional<TimelineSnapshotEpaDataObject> first = mEpas.stream().filter(epa -> epa.getUnbId().equals(epaUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get();
		}
		return null;
	}

	public TimelineSnapshotNumericalPiDataObject getPiSnapshot(String epaUnbId, String piUnbId) {
		if (mEpas == null)
			return null;
		Optional<TimelineSnapshotEpaDataObject> first = mEpas.stream().filter(epa -> epa.getUnbId().equals(epaUnbId) && epa.hasPi(piUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getPiSnapshot(piUnbId);
		}
		return null;
	}

	public static class Properties {
		public static final String FormId = "formId";
		public static final String SubmissionId = "submissionId";
		public static final String SubmissionDate = "submissionDate";
		public static final String Cohort = "cohort";
		public static final String ReceivedDate = "receivedDate";
		public static final String Epas = "EPAs";
	}
}
