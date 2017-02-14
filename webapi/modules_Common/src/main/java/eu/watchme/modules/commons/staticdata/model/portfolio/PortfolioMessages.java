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

package eu.watchme.modules.commons.staticdata.model.portfolio;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.commons.enums.FeedbackSeekingStrategyUnb;
import eu.watchme.modules.commons.enums.FrustrationAlertUnb;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.enums.PortfolioConsistencyUnb;

import java.util.List;
import java.util.Map;

public class PortfolioMessages {
	@SerializedName("FeedbackSeekingStrategy")
	private Map<FeedbackSeekingStrategyUnb, Map<LanguageCode, List<String>>> mFeedbackSeekingStrategyMap;

	@SerializedName("PortfolioConsistency")
	private Map<PortfolioConsistencyUnb, Map<LanguageCode, List<String>>> mPortfolioConsistencyMap;

	@SerializedName("InformationLevel")
	private Map<LanguageCode, List<String>> mInformationLevelMap;

	@SerializedName("isFrustrated")
	private Map<FrustrationAlertUnb, Map<LanguageCode, List<String>>> mFrustrationAlertMap;

	public Map<FeedbackSeekingStrategyUnb, Map<LanguageCode, List<String>>> getFeedbackSeekingStrategyMap() {
		return mFeedbackSeekingStrategyMap;
	}

	public Map<PortfolioConsistencyUnb, Map<LanguageCode, List<String>>> getPortfolioConsistencyMap() {
		return mPortfolioConsistencyMap;
	}

	public Map<LanguageCode, List<String>> getInformationLevelMap() {
		return mInformationLevelMap;
	}

	public Map<FrustrationAlertUnb, Map<LanguageCode, List<String>>> getFrustrationAlertMap() {
		return mFrustrationAlertMap;
	}
}
