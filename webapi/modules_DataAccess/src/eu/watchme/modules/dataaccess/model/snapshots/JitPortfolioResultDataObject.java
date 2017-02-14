/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 30/6/2016
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

@Embedded
public class JitPortfolioResultDataObject {

	@Property(Properties.FeedbackSeekingStrategy)
	private String mFeedbackSeekingStrategyUnbId;

	@Property(Properties.PortfolioConsistency)
	private String mPortfolioConsistencyUnbId;

	@Property(Properties.EpasNeedingInformation)
	private List<String> mEpaNeedingInformationUnbIds;

	@Property(Properties.Frustration)
	private String mFrustrationUnbId;

	public JitPortfolioResultDataObject() {
	}

	public JitPortfolioResultDataObject(String feedbackSeekingStrategyUnbId, String portfolioConsistencyUnbId, List<String> epaNeedingInformationUnbIds, String frustrationUnbId) {
		mFeedbackSeekingStrategyUnbId = feedbackSeekingStrategyUnbId;
		mPortfolioConsistencyUnbId = portfolioConsistencyUnbId;
		mEpaNeedingInformationUnbIds = epaNeedingInformationUnbIds;
		mFrustrationUnbId = frustrationUnbId;
	}

	public String getFeedbackSeekingStrategyUnbId() {
		return mFeedbackSeekingStrategyUnbId;
	}

	public void setFeedbackSeekingStrategyUnbId(String feedbackSeekingStrategyUnbId) {
		mFeedbackSeekingStrategyUnbId = feedbackSeekingStrategyUnbId;
	}

	public String getPortfolioConsistencyUnbId() {
		return mPortfolioConsistencyUnbId;
	}

	public void setPortfolioConsistencyUnbId(String portfolioConsistencyUnbId) {
		mPortfolioConsistencyUnbId = portfolioConsistencyUnbId;
	}

	public List<String> getEpaNeedingInformationUnbIds() {
		return mEpaNeedingInformationUnbIds;
	}

	public void setEpaNeedingInformationUnbIds(List<String> epaNeedingInformationUnbIds) {
		mEpaNeedingInformationUnbIds = epaNeedingInformationUnbIds;
	}

	public String getFrustrationUnbId() {
		return mFrustrationUnbId;
	}

	public void setFrustrationUnbId(String frustrationUnbId) {
		mFrustrationUnbId = frustrationUnbId;
	}

	public static class Properties {
		public static final String FeedbackSeekingStrategy = "feedbackSeekingStrategy";
		public static final String PortfolioConsistency = "portfolioConsistency";
		public static final String EpasNeedingInformation = "epasNeedingInformation";
		public static final String Frustration = "frustration";
	}
}
