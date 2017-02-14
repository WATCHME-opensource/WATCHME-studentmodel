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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.time.Instant;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FeedbackMessageModel {

	@JsonProperty("type")
	@SerializedName("type")
	private String mType;

	@JsonProperty("level")
	@SerializedName("level")
	private String mLevel;

	@JsonProperty("messages")
	@SerializedName("messages")
	private List<FeedbackMessageRecommendationModel> mMessages;

	private Instant mLastFeedbackDate;

	public FeedbackMessageModel() {
	}

	public FeedbackMessageModel(String type, String level, List<FeedbackMessageRecommendationModel> messages) {
		mType = type;
		mLevel = level;
		mMessages = messages;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public String getLevel() {
		return mLevel;
	}

	public void setLevel(String level) {
		this.mLevel = level;
	}

	@JsonIgnore
	public List<FeedbackMessageRecommendationModel> getMessages() {
		return mMessages;
	}

	public void setMessages(List<FeedbackMessageRecommendationModel> messages) {
		this.mMessages = messages;
	}

	@JsonIgnore
	public Instant getLastFeedbackDate() {
		if (mLastFeedbackDate == null) {
			mLastFeedbackDate = mMessages == null || mMessages.isEmpty() ? null : mMessages.get(0).getDateReceived();
		}
		return mLastFeedbackDate;
	}

	public void setLastFeedbackDate(Instant lastFeedbackDate) {
		mLastFeedbackDate = lastFeedbackDate;
	}
}