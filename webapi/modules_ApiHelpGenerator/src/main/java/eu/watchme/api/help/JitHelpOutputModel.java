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

package eu.watchme.api.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.domainmodel.jit.*;

import java.util.ArrayList;
import java.util.List;

public class JitHelpOutputModel {

	public String getRequestModelAsJson() {
		String result = null;

		Gson gson = new GsonBuilder().create();

		try {
			result = gson.toJson(getRequestModelInstance());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public String getResponseModelType1AsJson() {
		String result = null;

		Gson gson = new GsonBuilder().create();
		result = gson.toJson(getResponseModelType1Instance());

		return result;
	}

	public String getResponseModelType2AsJson() {
		String result = null;

		Gson gson = new GsonBuilder().create();
		result = gson.toJson(getResponseModelType2Instance());

		return result;
	}

	private Object getRequestModelInstance() {
		FeedbackQueryModel model = new FeedbackQueryModel();
		model.setAuthorisationData(new AuthorizationModel());
		model.getAuthorisationData().setApplicantHash("XXX");
		model.getAuthorisationData().setStudentHash("YYY");
		model.getAuthorisationData().setSessionToken("ZZZ");

		model.setEpaId("EPA1");
		model.setFeedbackType("improvement=0 | positive=1 | supervisor=2 | cohort=3 | gaps=4 | trend=5");
		model.setGroupId("ZZZ");
		model.setLanguageCode(LanguageCode.EN);//"ISO 639-1 langauge code (e.g. nl)"
		model.setModelId("example-school");
		model.setSourceUrl("JIT url");

		return model;
	}

	private Object getResponseModelType1Instance() {
		FeedbackResponseModel model = new FeedbackResponseModel();
		List<FeedbackEpaModel> epas = new ArrayList<>();

		//Epa #1
		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("EPA1");
		epa.setEpaName("EPA1 name goes here");

		//Feedback for Epa #1
		List<FeedbackMessageModel> messagesEPA1 = new ArrayList<>();

		FeedbackMessageModel messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("1");
		messageL1.setType("improvement");
		List<FeedbackMessageRecommendationModel> messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 1 improvement feedback message for EPA 1", null, null, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("1");
		messageL1.setType("trend");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 1 trend  feedback message for EPA 1", null, null, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("2");
		messageL1.setType("improvement");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 2 improvement feedback message for EPA 1", 1, 1, null));
		messages.add(new FeedbackMessageRecommendationModel("Another level 2 improvement feedback message for EPA 1", 2, 1, null));
		messages.add(new FeedbackMessageRecommendationModel("Yet another level 2 improvement feedback message for EPA 1", 3, 1, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		epa.setFeedbackEntries(messagesEPA1);
		epas.add(epa);

		epa = new FeedbackEpaModel();
		epa.setEpaId("EPA2");
		epa.setEpaName("EPA2 name goes here");

		//Feedback for Epa #2
		List<FeedbackMessageModel> messagesEPA2 = new ArrayList<>();

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("1");
		messageL1.setType("supervisor");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 1 supervisor feedback message for EPA 1", null, null, null));
		messageL1.setMessages(messages);
		messagesEPA2.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("1");
		messageL1.setType("cohort");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 1 cohort feedback message for EPA 2", null, null, null));
		messageL1.setMessages(messages);
		messagesEPA2.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("2");
		messageL1.setType("supervisor");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 2 supervisor feedback message for EPA 2", 1, 2, null));
		messages.add(new FeedbackMessageRecommendationModel("Another level 2 supervisor feedback message for EPA 2", 2, 2, null));
		messages.add(new FeedbackMessageRecommendationModel("Yet another level 2 supervisor feedback message for EPA 2", 3, 2, null));
		messageL1.setMessages(messages);
		messagesEPA2.add(messageL1);

		epa.setFeedbackEntries(messagesEPA1);
		epas.add(epa);

		model.setFeedbackForEpas(epas);

		return model;
	}

	public Object getResponseModelType2Instance() {
		FeedbackResponseModel model = new FeedbackResponseModel();
		List<FeedbackEpaModel> epas = new ArrayList<>();

		//Epa #1
		FeedbackEpaModel epa = new FeedbackEpaModel();
		epa.setEpaId("EPA3");
		epa.setEpaName("EPA3 name goes here");

		//Feedback for Epa #1
		List<FeedbackMessageModel> messagesEPA1 = new ArrayList<>();

		FeedbackMessageModel messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("2");
		messageL1.setType("improvement");
		List<FeedbackMessageRecommendationModel> messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 2 improvement feedback message for EPA 3", 1, 3, null));
		messages.add(new FeedbackMessageRecommendationModel("Another level 2 improvement feedback message for EPA 3", 2, 3, null));
		messages.add(new FeedbackMessageRecommendationModel("And another level 2 improvement feedback message for EPA 3", 3, 3, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("2");
		messageL1.setType("trend");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 2 trend  feedback message for EPA 3", 1, 4, null));
		messages.add(new FeedbackMessageRecommendationModel("Another level 2 trend feedback message for EPA 3", 2, 4, null));
		messages.add(new FeedbackMessageRecommendationModel("Yet another level 2 trend feedback message for EPA 3", 3, 4, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		messageL1 = new FeedbackMessageModel();
		messageL1.setLevel("2");
		messageL1.setType("positive");
		messages = new ArrayList<>();
		messages.add(new FeedbackMessageRecommendationModel("Level 2 positive feedback message for EPA 3", 1, 5, null));
		messages.add(new FeedbackMessageRecommendationModel("Another level 2 positive feedback message for EPA 3", 2, 5, null));
		messages.add(new FeedbackMessageRecommendationModel("Yet another level 2 positive feedback message for EPA 3", 3, 5, null));
		messageL1.setMessages(messages);
		messagesEPA1.add(messageL1);

		epa.setFeedbackEntries(messagesEPA1);
		epas.add(epa);
		model.setFeedbackForEpas(epas);

		return model;
	}
}