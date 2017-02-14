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

package eu.watchme.modules.dispatcher.jit;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackType;
import eu.watchme.modules.domainmodel.jit.FeedbackMessageRecommendationModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

public final class JitMessageFormatter {
	private static JitMessageFormatter sInstance;

	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private JitMessageFormatter() {
	}

	public static JitMessageFormatter getInstance() {
		if (sInstance == null) {
			sInstance = new JitMessageFormatter();
		}
		return sInstance;
	}

	public String formatImprovementMessage(String template, String epaName, FeedbackType feedbackType) {
		switch (feedbackType) {
		case IMPROVEMENT_NEGATIVE:
			return formatImprovementNegativeMessage(template, epaName);
		case IMPROVEMENT_NEUTRAL:
			return formatImprovementNeutralMessage(template, epaName);
		case IMPROVEMENT_POSITIVE:
			return formatImprovementPositiveMessage(template, epaName);
		}
		return null;
	}

	public String formatImprovementPositiveMessage(String template, String epaName) {
		return template.replace(ApplicationSettings.getPlaceholderEpaName(), epaName);
	}

	public String formatImprovementNeutralMessage(String template, String epaName) {
		return template.replace(ApplicationSettings.getPlaceholderEpaName(), epaName);
	}

	public String formatImprovementNegativeMessage(String template, String epaName) {
		return template.replace(ApplicationSettings.getPlaceholderEpaName(), epaName);
	}

	public FeedbackMessageRecommendationModel formatImprovementLevel2Message(String template, String performanceIndicator,
			String performanceIndicatorLevel, String recommendation, Integer submissionId, Integer formId, Instant feedbackDate) {
		String text = template.replace(ApplicationSettings.getPlaceholderPerformanceIndicator(), performanceIndicator)
				.replace(ApplicationSettings.getPlaceholderPerformanceIndicatorLevel(),
						performanceIndicatorLevel)
				.replace(ApplicationSettings.getPlaceholderRecommendation(), recommendation);
		return new FeedbackMessageRecommendationModel(text, submissionId, formId, feedbackDate);
	}

	public String formatSupervisorLevel1Message(String template, String epaName) {
		return template.replace(ApplicationSettings.getPlaceholderEpaName(), epaName);
	}

	public FeedbackMessageRecommendationModel formatSupervisorLevel2Message(String template, String supervisorFeedback, Integer submissionId, Integer formId, Instant feedbackDate) {
		String formattedDate = "unknown";
		if (feedbackDate != null) {
			formattedDate = dateFormat.format(feedbackDate.toEpochMilli());
		}
		String text = template.replace(ApplicationSettings.getPlaceholderSupervisorFeedback(), supervisorFeedback)
				.replace(ApplicationSettings.getPlaceholderSupervisorFeedbackCreationTime(), formattedDate);
		return new FeedbackMessageRecommendationModel(text, submissionId, formId, feedbackDate);
	}

	public String formatPortfolioInformationLevel(String template, String joinedEpaNames) {
		return template.replace(ApplicationSettings.getPlaceholderEpaName(), joinedEpaNames);
	}
}
