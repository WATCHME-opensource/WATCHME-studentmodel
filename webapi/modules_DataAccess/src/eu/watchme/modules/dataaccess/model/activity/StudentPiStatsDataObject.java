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

@Embedded
public class StudentPiStatsDataObject {

	@Property(Properties.UnbId)
	private String mUnbId;

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

	public String getUnbId() {
		return mUnbId;
	}

	public void setUnbId(String unbId) {
		mUnbId = unbId;
	}

	public Integer getSubmissionsCount() {
		return mSubmissionsCount;
	}

	public void setSubmissionsCount(Integer submissionsCount) {
		mSubmissionsCount = submissionsCount;
	}

	public Integer getLastSubmissionId() {
		return mLastSubmissionId;
	}

	public void setLastSubmissionId(Integer lastSubmissionId) {
		mLastSubmissionId = lastSubmissionId;
	}

	public Integer getLastSubmissionFormId() {
		return mLastSubmissionFormId;
	}

	public void setLastSubmissionFormId(Integer lastSubmissionFormId) {
		mLastSubmissionFormId = lastSubmissionFormId;
	}

	public String getLastSubmissionReferenceDate() {
		return mLastSubmissionReferenceDate;
	}

	public void setLastSubmissionReferenceDate(String lastSubmissionReferenceDate) {
		mLastSubmissionReferenceDate = lastSubmissionReferenceDate;
	}

	public Instant getLastSubmissionDate() {
		return mLastSubmissionDate;
	}

	public void setLastSubmissionDate(Instant lastSubmissionDate) {
		mLastSubmissionDate = lastSubmissionDate;
	}

	public void newSubmission(Integer submissionId, Integer formId, String submissionReferenceDate, Instant dateReceived) {
		mLastSubmissionId = submissionId;
		mLastSubmissionFormId = formId;
		mLastSubmissionReferenceDate = submissionReferenceDate;
		mLastSubmissionDate = dateReceived;
		mSubmissionsCount = mSubmissionsCount == null ? 1 : mSubmissionsCount + 1;
	}

	@Override
	public String toString() {
		return "StudentPiStatsDataObject{" +
				"mUnbId='" + mUnbId + '\'' +
				", mSubmissionsCount=" + mSubmissionsCount +
				", mLastSubmissionId=" + mLastSubmissionId +
				", mLastSubmissionFormId=" + mLastSubmissionFormId +
				", mLastSubmissionReferenceDate='" + mLastSubmissionReferenceDate + '\'' +
				", mLastSubmissionDate=" + mLastSubmissionDate +
				'}';
	}

	public static class Properties {
		public static final String UnbId = "unbId";
		public static final String SubmissionsCount = "submissionsCount";
		public static final String LastSubmissionId = "lastSubmissionId";
		public static final String LastSubmissionFormId = "lastSubmissionFormId";
		public static final String LastSubmissionReferenceDate = "lastSubmissionReferenceDate";
		public static final String LastSubmissionDate = "lastSubmissionDate";
	}
}
