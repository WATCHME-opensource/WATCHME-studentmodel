/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 10/6/2016
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

package eu.watchme.modules.dataaccess.model.activity;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Embedded
public class StudentEpaStatsDataObject {

	@Property(Properties.UnbId)
	private String mEpaUnbId;

	@Property(Properties.Cohort)
	private String mCohort;

	@Property(Properties.SubmissionsCount)
	private Integer mSubmissionsCount;

	@Property(Properties.LastSubmissionId)
	private Integer mLastSubmissionId;

	@Property(Properties.LastSubmissionFormId)
	private Integer mLastSubmissionFormId;

	@Property(Properties.LastSubmissionReferenceDate)
	private String mLastSubmissionReferenceDate;

	@Property(Properties.LastSubmissionDate)
	private Instant mLastSubmissionDate;

	@Embedded(Properties.PiStats)
	private List<StudentPiStatsDataObject> mPiStats;

	public StudentEpaStatsDataObject() {
		mPiStats = new ArrayList<>(0);
	}

	public String getEpaUnbId() {
		return mEpaUnbId;
	}

	public void setEpaUnbId(String epaUnbId) {
		mEpaUnbId = epaUnbId;
	}

	public String getCohort() {
		return mCohort;
	}

	public Integer getSubmissionsCount() {
		return mSubmissionsCount;
	}

	public void newSubmission(Integer submissionId, Integer formId, String cohort, String submissionReferenceDate, Instant dateReceived, Collection<String> piUnbIds) {
		mLastSubmissionId = submissionId;
		mLastSubmissionFormId = formId;
		mCohort = cohort;
		mLastSubmissionReferenceDate = submissionReferenceDate;
		mLastSubmissionDate = dateReceived;
		for (String piUnbId : piUnbIds) {
			addNewPiSubmission(piUnbId, submissionId, formId, submissionReferenceDate, dateReceived);
		}
		mSubmissionsCount = mSubmissionsCount == null ? 1 : mSubmissionsCount + 1;
	}

	public Integer getLastSubmissionId() {
		return mLastSubmissionId;
	}

	public Integer getLastSubmissionFormId() {
		return mLastSubmissionFormId;
	}

	public String getLastSubmissionReferenceDate() {
		return mLastSubmissionReferenceDate;
	}

	public Instant getLastSubmissionDate() {
		return mLastSubmissionDate;
	}

	public List<StudentPiStatsDataObject> getPiStats() {
		return mPiStats;
	}

	private void addNewPiSubmission(String piUnbId, Integer submissionId, Integer formId, String submissionReferenceDate, Instant dateReceived) {
		StudentPiStatsDataObject piStats = findPiStats(piUnbId);
		piStats.newSubmission(submissionId, formId, submissionReferenceDate, dateReceived);
	}

	public StudentPiStatsDataObject findPiStats(String piUnbId) {
		StudentPiStatsDataObject result = null;
		for (StudentPiStatsDataObject piStats : mPiStats) {
			if (piStats.getUnbId().equals(piUnbId)) {
				result = piStats;
				break;
			}
		}
		if (result == null) {
			result = new StudentPiStatsDataObject();
			result.setUnbId(piUnbId);
			mPiStats.add(result);
		}
		return result;
	}

	@Override
	public String toString() {
		return "StudentEpaStatsDataObject{" +
				"mEpaUnbId='" + mEpaUnbId + '\'' +
				", mCohort='" + mCohort + '\'' +
				", mSubmissionsCount=" + mSubmissionsCount +
				", mLastSubmissionId=" + mLastSubmissionId +
				", mLastSubmissionFormId=" + mLastSubmissionFormId +
				", mLastSubmissionReferenceDate='" + mLastSubmissionReferenceDate + '\'' +
				", mLastSubmissionDate=" + mLastSubmissionDate +
				", mPiStats=" + mPiStats +
				'}';
	}

	public int getPiSubmissionsCount(String piUnbId) {
		if (mPiStats == null)
			return 0;
		Optional<StudentPiStatsDataObject> first = mPiStats.stream().filter(piStats -> piStats.getUnbId().equals(piUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getSubmissionsCount();
		}
		return 0;
	}

	public static class Properties {
		public static final String UnbId = "unbId";
		public static final String Cohort = "cohort";
		public static final String SubmissionsCount = "submissionsCount";
		public static final String LastSubmissionId = "lastSubmissionId";
		public static final String LastSubmissionFormId = "lastSubmissionFormId";
		public static final String LastSubmissionReferenceDate = "lastSubmissionReferenceDate";
		public static final String LastSubmissionDate = "lastSubmissionDate";
		public static final String PiStats = "piStats";
	}
}
