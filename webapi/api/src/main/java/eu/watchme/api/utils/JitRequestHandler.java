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

package eu.watchme.api.utils;

import eu.watchme.modules.domainmodel.jit.*;

import java.util.Arrays;

/**
 * Helper class for some hardcoded JIT responses. Can stay until the workflow is good enough to return real data to JIT.
 */

public class JitRequestHandler {

	public FeedbackResponseModel getRepoonse(FeedbackQueryModel queryModel) {
		FeedbackResponseModel responseModel;

		if (queryModel.getEpaId().equals("*")) {
			responseModel = getFirstScenario();
		} else if (queryModel.getFeedbackType().isEmpty()) {
			responseModel = getSecondScenario(queryModel.getEpaId());
		} else {
			responseModel = getThirdScenario(queryModel.getFeedbackType());
		}

		return responseModel;
	}

	private FeedbackResponseModel getFirstScenario() {
		FeedbackResponseModel responseModel = new FeedbackResponseModel();

		FeedbackMessageModel firstFeedbackMesasge = new FeedbackMessageModel();
		firstFeedbackMesasge.setType("Improvement");
		firstFeedbackMesasge.setLevel("1");
		firstFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("There is room for improvement on this task.", null, null, null)));

		FeedbackMessageModel secondFeedbackMesasge = new FeedbackMessageModel();
		secondFeedbackMesasge.setType("Supervisor");
		secondFeedbackMesasge.setLevel("1");
		secondFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("Your Supervisor added an improvement suggestion on this task.", null, null, null)));

		FeedbackMessageModel thirdFeedbackMessage = new FeedbackMessageModel();
		thirdFeedbackMessage.setType("Trend");
		thirdFeedbackMessage.setLevel("1");
		thirdFeedbackMessage.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("You currently have a trend for decreasing scores on this task.", null, null, null)));

		FeedbackMessageModel fourthFeedbackMessage = new FeedbackMessageModel();
		fourthFeedbackMessage.setType("Positive");
		fourthFeedbackMessage.setLevel("1");
		fourthFeedbackMessage.setMessages(
				Arrays.asList(new FeedbackMessageRecommendationModel("You have recently received good scores on assessment of this task. You can further improve on this task.", null, null, null)));

		FeedbackMessageModel fifthFeedbackMessage = new FeedbackMessageModel();
		fifthFeedbackMessage.setType("Cohort");
		fifthFeedbackMessage.setLevel("1");
		fifthFeedbackMessage
				.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("Compared to your cohort, you have received better scored on this task than you peers", null, null, null)));

		FeedbackMessageModel sixthFeedbackMessageModel = new FeedbackMessageModel();
		sixthFeedbackMessageModel.setType("Gaps");
		sixthFeedbackMessageModel.setLevel("1");
		sixthFeedbackMessageModel.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("On this task, you have received less assessments than you peers.", null, null, null)));

		FeedbackEpaModel firstEpa = new FeedbackEpaModel();
		firstEpa.setEpaId("1");
		firstEpa.setEpaName("Task 1: Set learning goals for the whole curriculum and specific lessons");
		firstEpa.setFeedbackEntries(Arrays.asList(firstFeedbackMesasge, secondFeedbackMesasge));

		FeedbackEpaModel secondEpa = new FeedbackEpaModel();
		secondEpa.setEpaId("2");
		secondEpa.setEpaName("Task 2: Design learning activities (incl. materials and methods) for the learning goals");
		secondEpa.setFeedbackEntries(Arrays.asList(thirdFeedbackMessage));

		FeedbackEpaModel thirdEpa = new FeedbackEpaModel();
		thirdEpa.setEpaId("3");
		thirdEpa.setEpaName("Task 3: Plan the execution and supervision of learning");
		thirdEpa.setFeedbackEntries(Arrays.asList(fourthFeedbackMessage));

		FeedbackEpaModel fourthEpa = new FeedbackEpaModel();
		fourthEpa.setEpaId("4");
		fourthEpa.setEpaName("Task 4: Supervise the execution and supervision of learning activities");
		fourthEpa.setFeedbackEntries(Arrays.asList(fifthFeedbackMessage));

		FeedbackEpaModel fifthEpa = new FeedbackEpaModel();
		fifthEpa.setEpaId("5");
		fifthEpa.setEpaName("Task 5: Test to which extent the set learning goals have been met");
		fifthEpa.setFeedbackEntries(Arrays.asList(sixthFeedbackMessageModel));

		responseModel.setFeedbackForEpas(Arrays.asList(firstEpa, secondEpa, thirdEpa, fourthEpa, fifthEpa));

		return responseModel;
	}

	private FeedbackResponseModel getSecondScenario(String epaId) {
		if (epaId.equals("1")) {
			return getSecondScenarioFirst();
		} else {
			return getSecondScenarioSecond();
		}
	}

	private FeedbackResponseModel getSecondScenarioFirst() {
		FeedbackResponseModel responseModel = new FeedbackResponseModel();

		FeedbackMessageModel firstFeedbackMesasge = new FeedbackMessageModel();
		firstFeedbackMesasge.setType("Improvement");
		firstFeedbackMesasge.setLevel("1");
		firstFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("There is room for improvement on this task.", null, null, null)));

		FeedbackMessageModel secondFeedbackMesasge = new FeedbackMessageModel();
		secondFeedbackMesasge.setType("Improvement");
		secondFeedbackMesasge.setLevel("2");
		secondFeedbackMesasge.setMessages(Arrays.asList(
				new FeedbackMessageRecommendationModel("You are level 2 on your Learning Goals. In order to achieve the next level, you should improve the formulation of you specific learning goals.",
						1, 4, null), new FeedbackMessageRecommendationModel(
						"You are level 1 on your SMART Learning Goals. In order to achieve the next level, you should check regularly whether your goals are SMART formulated.", 1, 1, null),
				new FeedbackMessageRecommendationModel("You are level 3 on all other Performance Indicators for this task.", 1, 2, null)));

		FeedbackMessageModel thirdFeedbackModel = new FeedbackMessageModel();
		thirdFeedbackModel.setType("Supervisor");
		thirdFeedbackModel.setLevel("1");
		thirdFeedbackModel.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("Your Supervisor added an improvement suggestion on this task.", null, null, null)));

		FeedbackMessageModel fourthFeedbackModel = new FeedbackMessageModel();
		fourthFeedbackModel.setType("Supervisor");
		fourthFeedbackModel.setLevel("2");
		fourthFeedbackModel.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel(
				"Your Supervisor commented: \"You are definitely improving, but your SMART goals could be even sharper. Remember that your goals have to be linked to the specific subject content.\" (07 May 2015)",
				1, 3, null)));

		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("1");
		epa.setEpaName("Task 1: Set learning goals for the whole curriculum and specific lessons");
		epa.setFeedbackEntries(Arrays.asList(firstFeedbackMesasge, secondFeedbackMesasge, thirdFeedbackModel, fourthFeedbackModel));

		responseModel.setFeedbackForEpas(Arrays.asList(epa));

		return responseModel;
	}

	private FeedbackResponseModel getSecondScenarioSecond() {
		FeedbackResponseModel responseModel = new FeedbackResponseModel();

		FeedbackMessageModel firstFeedbackMesasge = new FeedbackMessageModel();
		firstFeedbackMesasge.setType("Trend");
		firstFeedbackMesasge.setLevel("1");
		firstFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel("You currently have a trend for decreasing scores on this task.", 1, 1, null)));

		FeedbackMessageModel secondFeedbackMesasge = new FeedbackMessageModel();
		secondFeedbackMesasge.setType("Trend");
		secondFeedbackMesasge.setLevel("2");
		secondFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel(
				"On Interpersonal Competencies, your previous level was 3, but now you achieved level 2. To improve this Performance Indicator, you need to know the background information of most students and know what moves and motivates other students.",
				1, 1, null)));

		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("2");
		epa.setEpaName("Task 2: Design learning activities (incl. materials and methods) for the learning goals");
		epa.setFeedbackEntries(Arrays.asList(firstFeedbackMesasge, secondFeedbackMesasge));

		responseModel.setFeedbackForEpas(Arrays.asList(epa));

		return responseModel;
	}

	private FeedbackResponseModel getThirdScenario(String feedbackType) {
		if (feedbackType.equals("improvement")) {
			return getThirdScenarioFirst();
		} else {
			return getThirdScenarioSecond();
		}
	}

	private FeedbackResponseModel getThirdScenarioFirst() {
		FeedbackResponseModel responseModel = new FeedbackResponseModel();

		FeedbackMessageModel firstFeedbackMesasge = new FeedbackMessageModel();
		firstFeedbackMesasge.setType("Improvement");
		firstFeedbackMesasge.setLevel("2");
		firstFeedbackMesasge.setMessages(Arrays.asList(
				new FeedbackMessageRecommendationModel("You are level 2 on your Learning Goals. In order to achieve the next level, you should improve the formulation of you specific learning goals.",
						1, 1, null),
				new FeedbackMessageRecommendationModel(
						"You are level 1 on your SMART Learning Goals. In order to achieve the next level, you should check regularly whether your goals are SMART formulated.", 1, 1, null),
				new FeedbackMessageRecommendationModel("You are level 3 on all other Performance Indicators for this task.", 1, 1, null)));

		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("1");
		epa.setEpaName("Task 1: Set learning goals for the whole curriculum and specific lessons");
		epa.setFeedbackEntries(Arrays.asList(firstFeedbackMesasge));

		responseModel.setFeedbackForEpas(Arrays.asList(epa));

		return responseModel;
	}

	private FeedbackResponseModel getThirdScenarioSecond() {
		FeedbackResponseModel responseModel = new FeedbackResponseModel();

		FeedbackMessageModel firstFeedbackMesasge = new FeedbackMessageModel();
		firstFeedbackMesasge.setType("Supervisor");
		firstFeedbackMesasge.setLevel("2");
		firstFeedbackMesasge.setMessages(Arrays.asList(new FeedbackMessageRecommendationModel(
				"Your Supervisor commented: \"You are definitely improving, but your SMART goals could be even sharper. Remember that your goals have to be linked to the specific subject content.\" (07 May 2015)",
				1, 1, null)));

		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("1");
		epa.setEpaName("Task 1: Set learning goals for the whole curriculum and specific lessons");
		epa.setFeedbackEntries(Arrays.asList(firstFeedbackMesasge));

		responseModel.setFeedbackForEpas(Arrays.asList(epa));

		return responseModel;
	}
}
