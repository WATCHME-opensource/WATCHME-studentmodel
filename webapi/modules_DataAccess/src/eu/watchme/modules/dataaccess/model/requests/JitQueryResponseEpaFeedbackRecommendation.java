/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 17/6/2016
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

package eu.watchme.modules.dataaccess.model.requests;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;

@Embedded
public class JitQueryResponseEpaFeedbackRecommendation {

	@Property(Properties.Text)
	private String mText;

	@Property(Properties.SubmissionId)
	private Integer mSubmissionId;

	@Property(Properties.FormId)
	private Integer mFormId;

	@Property(Properties.DateReceived)
	private Instant mDateReceived;

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		mText = text;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public void setSubmissionId(Integer submissionId) {
		mSubmissionId = submissionId;
	}

	public Integer getFormId() {
		return mFormId;
	}

	public void setFormId(Integer formId) {
		mFormId = formId;
	}

	public Instant getDateReceived() {
		return mDateReceived;
	}

	public void setDateReceived(Instant dateReceived) {
		mDateReceived = dateReceived;
	}

	public static class Properties {
		public static final String Text = "text";
		public static final String SubmissionId = "submissionId";
		public static final String FormId = "formId";
		public static final String DateReceived = "dateReceived";
	}
}
