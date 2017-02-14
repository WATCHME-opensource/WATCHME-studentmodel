/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 10/9/2015
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

package eu.watchme.modules.dispatcher.converters;

import eu.watchme.modules.dataaccess.model.requests.JitQueryResponseEpaFeedbackRecommendation;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponse;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponseEpa;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponseEpaFeedback;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponsePortfolio;
import eu.watchme.modules.domainmodel.jit.*;

import java.util.List;
import java.util.stream.Collectors;

public class JitQueryResponseConverter extends Converter<FeedbackResponseModel, JitQueryResponse> {

	@Override
	public JitQueryResponse toDataObject(FeedbackResponseModel model) {
		if (model == null) {
			return null;
		}
		JitQueryResponse dataObject = new JitQueryResponse();
		dataObject.setUpdatedInstant(model.getUpdatedInstant());
		dataObject.setEPAs(toEpasDataObjects(model.getFeedbackForEpas()));
		dataObject.setPortfolio(toDataObject(model.getPortfolio()));
		return dataObject;
	}

	@Override
	public FeedbackResponseModel fromDataObject(JitQueryResponse dataObject) {
		if (dataObject == null) {
			return null;
		}
		FeedbackResponseModel model = new FeedbackResponseModel();
		model.setUpdatedInstant(dataObject.getUpdatedInstant());
		model.setFeedbackForEpas(fromEpasDataObjects(dataObject.getEPAs()));
		model.setPortfolio(fromDataObject(dataObject.getPortfolio()));
		return model;
	}

	private static List<JitQueryResponseEpa> toEpasDataObjects(List<FeedbackEpaModel> models) {
		if (models == null) {
			return null;
		}
		return models.stream().map(JitQueryResponseConverter::toDataObject).collect(Collectors.toList());
	}

	private JitQueryResponsePortfolio toDataObject(PortfolioModel model) {
		if (model == null) {
			return null;
		}
		JitQueryResponsePortfolio dataObject = new JitQueryResponsePortfolio();
		dataObject.setInformationLevel(model.getInformationLevel());
		dataObject.setFeedbackSeekingStrategy(model.getFeedbackSeekingStrategy());
		dataObject.setPortfolioConsistency(model.getPortfolioConsistency());
		return dataObject;
	}

	private static JitQueryResponseEpa toDataObject(FeedbackEpaModel model) {
		if (model == null) {
			return null;
		}
		JitQueryResponseEpa result = new JitQueryResponseEpa();
		result.setEpaId(model.getEpaId());
		result.setEpaName(model.getEpaName());
		result.setFeedback(toFeedbackDataObjects(model.getFeedbackEntries()));
		result.setLastFeedbackDate(model.getLastFeedbackDate());
		return result;
	}

	private static List<JitQueryResponseEpaFeedback> toFeedbackDataObjects(List<FeedbackMessageModel> models) {
		if (models == null) {
			return null;
		}
		return models.stream().map(JitQueryResponseConverter::toDataObject).collect(Collectors.toList());
	}

	private static JitQueryResponseEpaFeedback toDataObject(FeedbackMessageModel model) {
		if (model == null) {
			return null;
		}
		JitQueryResponseEpaFeedback result = new JitQueryResponseEpaFeedback();
		result.setType(model.getType());
		result.setLevel(model.getLevel());
		result.setMessages(toRecommendationDataObjects(model.getMessages()));
		result.setLastFeedbackDate(model.getLastFeedbackDate());
		return result;
	}

	private static List<JitQueryResponseEpaFeedbackRecommendation> toRecommendationDataObjects(List<FeedbackMessageRecommendationModel> models) {
		if (models == null) {
			return null;
		}
		return models.stream().map(JitQueryResponseConverter::toDataObject).collect(Collectors.toList());
	}

	private static JitQueryResponseEpaFeedbackRecommendation toDataObject(FeedbackMessageRecommendationModel model) {
		if (model == null) {
			return null;
		}
		JitQueryResponseEpaFeedbackRecommendation result = new JitQueryResponseEpaFeedbackRecommendation();
		result.setText(model.getText());
		result.setSubmissionId(model.getSubmissionId());
		result.setFormId(model.getFormId());
		result.setDateReceived(model.getDateReceived());
		return result;
	}

	private static List<FeedbackEpaModel> fromEpasDataObjects(List<JitQueryResponseEpa> dataObjects) {
		if (dataObjects == null) {
			return null;
		}
		return dataObjects.stream().map(JitQueryResponseConverter::fromDataObject).collect(Collectors.toList());
	}

	private static FeedbackEpaModel fromDataObject(JitQueryResponseEpa dataObject) {
		if (dataObject == null) {
			return null;
		}
		FeedbackEpaModel result = new FeedbackEpaModel();
		result.setEpaId(dataObject.getEpaId());
		result.setEpaName(dataObject.getEpaName());
		result.setFeedbackEntries(fromFeedbackDataObjects(dataObject.getFeedback()));
		result.setLastFeedbackDate(dataObject.getLastFeedbackDate());
		return result;
	}

	private PortfolioModel fromDataObject(JitQueryResponsePortfolio dataObject) {
		if (dataObject == null) {
			return null;
		}
		PortfolioModel model = new PortfolioModel();
		model.setFeedbackSeekingStrategy(dataObject.getFeedbackSeekingStrategy());
		model.setInformationLevel(dataObject.getInformationLevel());
		model.setPortfolioConsistency(dataObject.getPortfolioConsistency());
		return model;
	}

	private static List<FeedbackMessageModel> fromFeedbackDataObjects(List<JitQueryResponseEpaFeedback> dataObjects) {
		if (dataObjects == null) {
			return null;
		}
		return dataObjects.stream().map(JitQueryResponseConverter::fromDataObject).collect(Collectors.toList());
	}

	private static FeedbackMessageModel fromDataObject(JitQueryResponseEpaFeedback dataObject) {
		if (dataObject == null) {
			return null;
		}
		FeedbackMessageModel result = new FeedbackMessageModel();
		result.setType(dataObject.getType());
		result.setLevel(dataObject.getLevel());
		result.setMessages(fromRecommendationDataObjects(dataObject.getMessages()));
		result.setLastFeedbackDate(dataObject.getLastFeedbackDate());
		return result;
	}

	private static List<FeedbackMessageRecommendationModel> fromRecommendationDataObjects(List<JitQueryResponseEpaFeedbackRecommendation> dataObjects) {
		if (dataObjects == null) {
			return null;
		}
		return dataObjects.stream().map(JitQueryResponseConverter::fromDataObject).collect(Collectors.toList());
	}

	private static FeedbackMessageRecommendationModel fromDataObject(JitQueryResponseEpaFeedbackRecommendation dataObject) {
		if (dataObject == null) {
			return null;
		}
		FeedbackMessageRecommendationModel result = new FeedbackMessageRecommendationModel();
		result.setText(dataObject.getText());
		result.setFormId(dataObject.getFormId());
		result.setSubmissionId(dataObject.getSubmissionId());
		result.setDateReceived(dataObject.getDateReceived());
		return result;
	}
}
