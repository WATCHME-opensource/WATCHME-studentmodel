/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 14/6/2016
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

import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.nlp.datatypes.NLPFindingSet;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.List;
import java.util.Set;

@Embedded
public class TimelineSnapshotTextPiDataObject {
	@Property(Properties.UnbId)
	private String mUnbId;
	@Property(Properties.Feedback)
	private String mFeedback;
	@Property(Properties.Sentiment)
	private String mSentiment;
	@Property(Properties.FeedbackLevel)
	private FeedbackLevel mFeedbackLevel;
	@Property(Properties.LinkedKeywords)
	private Set<String> mLinkedKeywords;
	@Embedded(Properties.FindingList)
	private List<NLPFindingSet> mFindingList;

	public TimelineSnapshotTextPiDataObject() {
	}

	public TimelineSnapshotTextPiDataObject(String unbId, String feedback, String sentiment, FeedbackLevel feedbackLevel, Set<String> linkedKeywords,
			List<NLPFindingSet> findingList) {
		mUnbId = unbId;
		mFeedback = feedback;
		mSentiment = sentiment;
		mFeedbackLevel = feedbackLevel;
		mLinkedKeywords = linkedKeywords;
		mFindingList = findingList;
	}

	public String getUnbId() {
		return mUnbId;
	}

	public String getFeedback() {
		return mFeedback;
	}

	public String getSentiment() {
		return mSentiment;
	}

	public FeedbackLevel getFeedbackLevel() {
		return mFeedbackLevel;
	}

	public Set<String> getLinkedKeywords() {
		return mLinkedKeywords;
	}

	public List<NLPFindingSet> getFindingList() {
		return mFindingList;
	}

	public static class Properties {
		public static final String UnbId = "unbId";
		public static final String Feedback = "feedback";
		public static final String Sentiment = "sentiment";
		public static final String FeedbackLevel = "feedbackLevel";
		public static final String LinkedKeywords = "linkedKeywords";
		public static final String FindingList = "findingList";
	}
}
