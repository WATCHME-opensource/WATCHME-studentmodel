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

package eu.watchme.modules.dispatcher.converters;

import eu.watchme.modules.dataaccess.model.submissions.EpassRequestDataObject;
import eu.watchme.modules.dataaccess.model.submissions.SubmissionAuthorityDataObject;
import eu.watchme.modules.dataaccess.model.submissions.SubmissionFormDataObject;
import eu.watchme.modules.dataaccess.model.submissions.SubmissionItemDataObject;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionAuthorityModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionFormModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class EpassRequestConverter extends Converter<TPost<SubmissionModel>, EpassRequestDataObject> {

	private static SubmissionItemDataObject[] toDataObjectArray(SubmissionModel[] submissionModelItems) {
		if (submissionModelItems == null) {
			return null;
		}
		List<SubmissionItemDataObject> result = new ArrayList<>(submissionModelItems.length);
		for (SubmissionModel item : submissionModelItems) {
			result.add(toDataObject(item));
		}
		SubmissionItemDataObject[] dataObjects = result.toArray(new SubmissionItemDataObject[result.size()]);
		return dataObjects;
	}

	private static SubmissionItemDataObject toDataObject(SubmissionModel item) {
		if (item == null) {
			return null;
		}
		SubmissionItemDataObject dataObject = new SubmissionItemDataObject();
		dataObject.setId(item.getId());
		dataObject.setLinks(item.getLinks() == null ? null : item.getLinks().toArray(new String[item.getLinks().size()]));
		dataObject.setForm(toDataObject(item.getForm()));
		dataObject.setReferenceDate(item.getReferenceDate());
		dataObject.setSubmissionDate(item.getSubmissionDate());
		dataObject.setAuthority(toDataObject(item.getAuthority()));
		dataObject.setCohort(item.getCohort());
		dataObject.setEPAs(item.getEPAs());
		dataObject.setAnswers(answersToHashMap(item.getAnswers()));
		dataObject.setAverageScores(toHashMap(item.getAverageScores()));
		return dataObject;
	}

	private static SubmissionFormDataObject toDataObject(SubmissionFormModel formModel) {
		if (formModel == null) {
			return null;
		}
		SubmissionFormDataObject dataObject = new SubmissionFormDataObject();
		dataObject.setId(formModel.getId());
		dataObject.setVersion(formModel.getVersion());
		return dataObject;
	}

	private static SubmissionAuthorityDataObject toDataObject(SubmissionAuthorityModel authorityModel) {
		if (authorityModel == null) {
			return null;
		}
		SubmissionAuthorityDataObject dataObject = new SubmissionAuthorityDataObject();
		dataObject.setHash(authorityModel.getHash());
		dataObject.setRole(authorityModel.getRole());
		return dataObject;
	}

	private static HashMap<String, String> toHashMap(JsonNode model) {
		if (model == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, String> result = mapper.convertValue(model, HashMap.class);
		return result;
	}

	private static HashMap<String, Object> answersToHashMap(JsonNode model) {
		if (model == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> result = mapper.convertValue(model, HashMap.class);
		return result;
	}

	private SubmissionModel[] toModelArray(SubmissionItemDataObject[] submissions) {
		if (submissions == null) {
			return null;
		}
		List<SubmissionModel> result = new ArrayList<>(submissions.length);
		for (SubmissionItemDataObject submission : submissions) {
			result.add(toModel(submission));
		}
		SubmissionModel[] models = result.toArray(new SubmissionModel[result.size()]);
		return models;
	}

	private SubmissionModel toModel(SubmissionItemDataObject submission) {
		if (submission == null) {
			return null;
		}
		SubmissionModel model = new SubmissionModel();
		model.setId(submission.getId());
		model.setLinks(submission.getLinks() == null ? null : Arrays.asList(submission.getLinks()));
		model.setForm(toModel(submission.getForm()));
		model.setReferenceDate(submission.getReferenceDate());
		model.setSubmissionDate(submission.getSubmissionDate());
		model.setAuthority(toModel(submission.getAuthority()));
		model.setCohort(submission.getCohort());
		model.setEPAs(submission.getEPAs());
		model.setAnswers(answersFromHashMap(submission.getAnswers()));
		model.setAverageScores(fromHashMap(submission.getAverageScores()));
		return model;
	}

	private SubmissionFormModel toModel(SubmissionFormDataObject form) {
		if (form == null) {
			return null;
		}
		SubmissionFormModel formModel = new SubmissionFormModel();
		formModel.setId(form.getId());
		formModel.setVersion(form.getVersion());
		return formModel;
	}

	private SubmissionAuthorityModel toModel(SubmissionAuthorityDataObject authority) {
		if (authority == null) {
			return null;
		}
		SubmissionAuthorityModel authorityModel = new SubmissionAuthorityModel();
		authorityModel.setHash(authority.getHash());
		authorityModel.setRole(authority.getRole());
		return authorityModel;
	}

	private JsonNode answersFromHashMap(HashMap<String, Object> answers) {
		if (answers == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.convertValue(answers, JsonNode.class);
		return result;
	}

	private JsonNode fromHashMap(HashMap<String, String> averageScores) {
		if (averageScores == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode result = mapper.convertValue(averageScores, JsonNode.class);
		return result;
	}

	@Override
	public EpassRequestDataObject toDataObject(TPost<SubmissionModel> model) {
		if (model == null) {
			return null;
		}
		EpassRequestDataObject dataObject = new EpassRequestDataObject();
		dataObject.setSource(model.getSource());
		dataObject.setTopics(model.getTopics());
		dataObject.setStudentId(model.getStudentId());
		dataObject.setGroupId(model.getGroupId());
		dataObject.setModelId(model.getModelId());
		dataObject.setContent(toDataObjectArray(model.getContent()));
		return dataObject;
	}

	@Override
	public TPost<SubmissionModel> fromDataObject(EpassRequestDataObject dataObject) {
		if (dataObject == null) {
			return null;
		}
		TPost<SubmissionModel> model = new TPost<>();
		model.setSource(dataObject.getSource());
		model.setTopics(dataObject.getTopics());
		model.setStudentId(dataObject.getStudentId());
		model.setGroupId(dataObject.getGroupId());
		model.setModelId(dataObject.getModelId());
		model.setContent(toModelArray(dataObject.getContent()));
		return model;
	}
}
