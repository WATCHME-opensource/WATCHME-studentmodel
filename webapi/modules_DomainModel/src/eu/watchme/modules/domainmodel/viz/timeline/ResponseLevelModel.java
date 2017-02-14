/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 27/11/2015
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
package eu.watchme.modules.domainmodel.viz.timeline;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.domainmodel.serialization.UtcDateDeserializer;
import eu.watchme.modules.domainmodel.serialization.UtcDateSerializer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.time.Instant;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseLevelModel {

	@JsonProperty("date")
	@SerializedName("date")
	@JsonSerialize(using = UtcDateSerializer.class)
	@JsonDeserialize(using = UtcDateDeserializer.class)
	private Instant mDate;

	@JsonProperty("score")
	@SerializedName("score")
	private double mScore;

	@JsonProperty("formId")
	@SerializedName("formId")
	private int mFormId;

	@JsonProperty("submissionId")
	@SerializedName("submissionId")
	private Integer mSubmissionId;

	@JsonProperty("sentiment")
	@SerializedName("sentiment")
	private String mSentiment;

	public Instant getDate() {
		return mDate;
	}

	public void setDate(Instant mDate) {
		this.mDate = mDate;
	}

	public double getScore() {
		return mScore;
	}

	public void setScore(double mScore) {
		this.mScore = mScore;
	}

	public int getFormId() {
		return mFormId;
	}

	public void setFormId(int formId) {
		mFormId = formId;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public void setSubmissionId(Integer submissionId) {
		mSubmissionId = submissionId;
	}

	public String getSentiment() {
		return mSentiment;
	}

	public void setSentiment(String sentiment) {
		mSentiment = sentiment;
	}
}
