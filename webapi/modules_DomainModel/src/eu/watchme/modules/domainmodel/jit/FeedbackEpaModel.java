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

package eu.watchme.modules.domainmodel.jit;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.domainmodel.serialization.UtcDateTimeDeserializer;
import eu.watchme.modules.domainmodel.serialization.UtcDateTimeSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.time.Instant;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FeedbackEpaModel {

	@JsonProperty("epaName")
	@SerializedName("epaName")
	private String mEpaName;

	@JsonProperty("epaId")
	@SerializedName("epaId")
	private String mEpaId;

	@JsonProperty("epaLastFeedbackDate")
	@SerializedName("epaLastFeedbackDate")
	@JsonSerialize(using = UtcDateTimeSerializer.class)
	@JsonDeserialize(using = UtcDateTimeDeserializer.class)
	public Instant mLastFeedbackDate;

	@JsonProperty("feedback")
	@SerializedName("feedback")
	private List<FeedbackMessageModel> mFeedbackEntries;

	public FeedbackEpaModel() {
		super();
	}

	public FeedbackEpaModel(String epaId, String epaName, List<FeedbackMessageModel> feedback, Instant lastFeedbackDate) {
		this();
		mEpaId = epaId;
		mEpaName = epaName;
		mFeedbackEntries = feedback;
		mLastFeedbackDate = lastFeedbackDate;
	}

	public String getEpaName() {
		return mEpaName;
	}

	public void setEpaName(String epaName) {
		mEpaName = epaName;
	}

	public void setFeedbackEntries(List<FeedbackMessageModel> entries) {
		mFeedbackEntries = entries;
	}

	@JsonIgnore
	public List<FeedbackMessageModel> getFeedbackEntries() {
		return mFeedbackEntries;
	}

	public String getEpaId() {
		return mEpaId;
	}

	public void setEpaId(String epaId) {
		this.mEpaId = epaId;
	}

	@JsonIgnore
	public Instant getLastFeedbackDate() {
		return mLastFeedbackDate;
	}

	public void setLastFeedbackDate(Instant lastFeedbackDate) {
		mLastFeedbackDate = lastFeedbackDate;
	}
}
