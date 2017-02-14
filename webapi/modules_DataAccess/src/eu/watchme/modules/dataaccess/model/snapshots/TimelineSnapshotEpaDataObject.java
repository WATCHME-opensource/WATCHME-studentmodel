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

package eu.watchme.modules.dataaccess.model.snapshots;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.List;
import java.util.Optional;

@Embedded
public class TimelineSnapshotEpaDataObject {
	@Property(Properties.UnbId)
	private String mUnbId;
	@Property(Properties.ActualScoreUnbId)
	private String mActualScoreUnbId;
	@Property(Properties.Sentiment)
	private String mSentiment;
	@Embedded(Properties.PerformanceIndicators)
	private List<TimelineSnapshotNumericalPiDataObject> mPerformanceIndicators;
	@Embedded(Properties.FeedbackIndicators)
	private List<TimelineSnapshotTextPiDataObject> mFeedbackIndicators;

	public TimelineSnapshotEpaDataObject() {
	}

	public TimelineSnapshotEpaDataObject(String unbId, String actualScoreUnbId, String sentiment,
			List<TimelineSnapshotNumericalPiDataObject> performanceIndicators, List<TimelineSnapshotTextPiDataObject> feedbackIndicators) {
		mUnbId = unbId;
		mActualScoreUnbId = actualScoreUnbId;
		mSentiment = sentiment;
		mPerformanceIndicators = performanceIndicators;
		mFeedbackIndicators = feedbackIndicators;
	}

	public String getUnbId() {
		return mUnbId;
	}

	public String getActualScoreUnbId() {
		return mActualScoreUnbId;
	}

	public String getSentiment() {
		return mSentiment;
	}

	public List<TimelineSnapshotNumericalPiDataObject> getPerformanceIndicators() {
		return mPerformanceIndicators;
	}

	public List<TimelineSnapshotTextPiDataObject> getFeedbackIndicators() {
		return mFeedbackIndicators;
	}

	public boolean hasPi(String piUnbId) {
		return mPerformanceIndicators != null && mPerformanceIndicators.stream().filter(pi -> pi.getUnbId().equals(piUnbId)).count() > 0;
	}

	public TimelineSnapshotNumericalPiDataObject getPiSnapshot(String piUnbId) {
		if (mPerformanceIndicators == null)
			return null;
		Optional<TimelineSnapshotNumericalPiDataObject> first = mPerformanceIndicators.stream().filter(pi -> pi.getUnbId().equals(piUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get();
		}
		return null;
	}

	public static class Properties {
		public static final String UnbId = "unbId";
		public static final String ActualScoreUnbId = "actualScoreUnbId";
		public static final String Sentiment = "sentiment";
		public static final String PerformanceIndicators = "performanceIndicators";
		public static final String FeedbackIndicators = "feedbackIndicators";
	}
}
