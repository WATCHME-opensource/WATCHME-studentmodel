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

package eu.watchme.modules.commons.staticdata.model.feedback;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.commons.enums.LanguageCode;

import java.util.Map;

public class Feedback {
	@SerializedName("Level1")
	private Level1Feedback mLevel1Feedback;
	@SerializedName("Level2")
	private Map<LanguageCode, String> mLevel2FeedbackTranslations;
	@SerializedName("Level2General")
	private Map<LanguageCode, String> mLevel2GeneralFeedbackTranslations;

	public Feedback() {
	}

	public Feedback(Level1Feedback level1Feedback, Map<LanguageCode, String> level2FeedbackTranslations,
			Map<LanguageCode, String> level2GeneralFeedbackTranslations) {
		mLevel1Feedback = level1Feedback;
		mLevel2FeedbackTranslations = level2FeedbackTranslations;
		mLevel2GeneralFeedbackTranslations = level2GeneralFeedbackTranslations;
	}

	public Level1Feedback getLevel1Feedback() {
		return mLevel1Feedback;
	}

	public Map<LanguageCode, String> getLevel2FeedbackTranslations() {
		return mLevel2FeedbackTranslations;
	}

	public Map<LanguageCode, String> getLevel2GeneralFeedbackTranslations() {
		return mLevel2GeneralFeedbackTranslations;
	}
}