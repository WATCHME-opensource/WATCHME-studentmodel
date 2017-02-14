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

import eu.watchme.modules.dataaccess.model.forms.*;
import eu.watchme.modules.domainmodel.epass.forms.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class EpassFormConvertor extends Converter<EpassFormModel, EpassFormDataObject> {

	private static QuestionDataObject[] toDataObjectArray(List<EpassFormQuestionModel> questions) {
		List<QuestionDataObject> result = new ArrayList<QuestionDataObject>(questions.size());
		for (EpassFormQuestionModel question : questions) {
			result.add(toDataObject(question));
		}
		QuestionDataObject[] resDataObjects = result.toArray(new QuestionDataObject[result.size()]);
		return resDataObjects;
	}

	private static QuestionDataObject toDataObject(EpassFormQuestionModel question) {
		QuestionDataObject dataObject = new QuestionDataObject();
		dataObject.setCompetencies(question.getCompetencies());
		dataObject.setNarrativefeedback(question.getNarrativefeedback());
		dataObject.setOptions(toOptionsArray(question.getOptions()));
		dataObject.setRubrics(toRubricsArray(question.getRubrics()));
		dataObject.setLabels(toLabelsArray(question.getLabels()));
		dataObject.setQuestionId(question.getId());
		return dataObject;
	}

	private static LabelDataObject[] toLabelsArray(List<EpassFormLabelModel> labels) {
		List<LabelDataObject> result = new ArrayList<LabelDataObject>(labels.size());
		for (EpassFormLabelModel label : labels) {
			result.add(toDataObject(label));
		}
		LabelDataObject[] dataObjects = result.toArray(new LabelDataObject[result.size()]);
		return dataObjects;
	}

	private static LabelDataObject toDataObject(EpassFormLabelModel label) {
		LabelDataObject dataObject = new LabelDataObject();
		dataObject.setLanguageCode(label.getLanguageCode());
		dataObject.setText(label.getText());
		return dataObject;
	}

	private static QuestionOptionDataObject[] toOptionsArray(List<EpassFormQuestionOptionModel> options) {
		List<QuestionOptionDataObject> result = new ArrayList<QuestionOptionDataObject>(options.size());
		for (EpassFormQuestionOptionModel option : options) {
			result.add(toDataObject(option));
		}
		QuestionOptionDataObject[] dataObjects = result.toArray(new QuestionOptionDataObject[result.size()]);
		return dataObjects;
	}

	private static QuestionOptionDataObject toDataObject(EpassFormQuestionOptionModel option) {
		QuestionOptionDataObject dataObject = new QuestionOptionDataObject();
		dataObject.setOptionId(option.getId());
		dataObject.setLabels(toLabelsArray(option.getLabels()));
		return dataObject;
	}

	private static QuestionRubricDataObject[] toRubricsArray(List<EpassFormQuestionRubricModel> rubrics) {
		List<QuestionRubricDataObject> result = rubrics.stream().map(EpassFormConvertor::toDataObject).collect(Collectors.toList());
		QuestionRubricDataObject[] dataObjects = result.toArray(new QuestionRubricDataObject[result.size()]);
		return dataObjects;
	}

	private static QuestionRubricDataObject toDataObject(EpassFormQuestionRubricModel rubric) {
		QuestionRubricDataObject dataObject = new QuestionRubricDataObject();
		dataObject.setRubricId(rubric.getId());
		dataObject.setLabels(toLabelsArray(rubric.getLabels()));
		return dataObject;
	}

	private List<EpassFormQuestionModel> fromDataObjectArray(QuestionDataObject[] questions) {
		if (questions == null) {
			return null;
		}
		List<EpassFormQuestionModel> result = Arrays.asList(questions).stream().map(EpassFormConvertor::toModel).collect(Collectors.toList());
		return result;
	}

	private static EpassFormQuestionModel toModel(QuestionDataObject question) {
		EpassFormQuestionModel model = new EpassFormQuestionModel();
		model.setCompetencies(question.getCompetencies());
		model.setNarrativeFeedback(question.getNarrativefeedback());
		model.setOptions(fromOptionsArray(question.getOptions()));
		model.setRubrics(fromRubricsArray(question.getRubrics()));
		model.setLabels(fromLabelsArray(question.getLables()));
		model.setId(question.getQuestionId());
		return model;
	}

	private static List<EpassFormQuestionOptionModel> fromOptionsArray(QuestionOptionDataObject[] options) {
		if (options == null) {
			return null;
		}
		List<EpassFormQuestionOptionModel> result = Arrays.asList(options).stream().map(EpassFormConvertor::fromDataObject).collect(Collectors.toList());
		return result;
	}

	private static EpassFormQuestionOptionModel fromDataObject(QuestionOptionDataObject option) {
		EpassFormQuestionOptionModel model = new EpassFormQuestionOptionModel();
		model.setId(option.getOptionId());
		model.setLabels(fromLabelsArray(option.getLabels()));
		return model;
	}

	private static List<EpassFormLabelModel> fromLabelsArray(LabelDataObject[] labels) {
		if (labels == null) {
			return null;
		}
		List<EpassFormLabelModel> result = Arrays.asList(labels).stream().map(EpassFormConvertor::fromDataObject).collect(Collectors.toList());
		return result;
	}

	private static EpassFormLabelModel fromDataObject(LabelDataObject label) {
		EpassFormLabelModel model = new EpassFormLabelModel();
		model.setLanguageCode(label.getLanguageCode());
		model.setText(label.getText());
		return model;
	}

	private static List<EpassFormQuestionRubricModel> fromRubricsArray(QuestionRubricDataObject[] rubrics) {
		List<EpassFormQuestionRubricModel> result = Arrays.asList(rubrics).stream().map(EpassFormConvertor::fromDataObject).collect(Collectors.toList());
		return result;
	}

	private static EpassFormQuestionRubricModel fromDataObject(QuestionRubricDataObject rubric) {
		EpassFormQuestionRubricModel model = new EpassFormQuestionRubricModel();
		model.setId(rubric.getRubricId());
		model.setLabels(fromLabelsArray(rubric.getLabels()));
		return model;
	}

	@Override
	public EpassFormDataObject toDataObject(EpassFormModel model) {
		EpassFormDataObject dataObject = new EpassFormDataObject();
		dataObject.setFormType(model.getType());
		dataObject.setCategory(model.getCategory());
		dataObject.setQuestions(toDataObjectArray(model.getQuestions()));
		return dataObject;
	}

	@Override
	public EpassFormModel fromDataObject(EpassFormDataObject dataObject) {
		EpassFormModel formModel = new EpassFormModel();
		formModel.setType(dataObject.getFormType());
		formModel.setCategory(dataObject.getCategory());
		formModel.setQuestions(fromDataObjectArray(dataObject.getQuestions()));
		return formModel;
	}

}
