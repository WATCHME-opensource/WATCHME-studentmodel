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

package eu.watchme.modules.epass;

import com.sun.jersey.api.client.ClientResponse;
import eu.watchme.modules.domainmodel.epass.forms.*;
import eu.watchme.modules.domainmodel.exceptions.FormIdNotFoundException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EpassFormMapper {

    public EpassFormModel mapToFormModel(ClientResponse clientResponse, Integer formId) {
        EpassFormModel formModel = new EpassFormModel();

        JsonNode jsonNode = clientResponse.getEntity(JsonNode.class);

        if (jsonNode.has("error")) {
            throw new FormIdNotFoundException(formId);
        }

        formModel.setType(jsonNode.get("type").asInt());
        formModel.setCategory(jsonNode.get("category").asText());
        formModel.setQuestions(getQuestions(jsonNode.get("questions")));

        return formModel;
    }

    private List<EpassFormQuestionModel> getQuestions(JsonNode jsonNode) {
        HashMap<String, JsonNode> questionsHashMap = new ObjectMapper().convertValue(jsonNode, HashMap.class);

        List<EpassFormQuestionModel> questions = new ArrayList<EpassFormQuestionModel>();

        for (HashMap.Entry<String, JsonNode> entry : questionsHashMap.entrySet()) {
            String id = entry.getKey();
            JsonNode value = new ObjectMapper().convertValue(entry.getValue(), JsonNode.class);

            EpassFormQuestionModel questionModel = new EpassFormQuestionModel();
            questionModel.setId(id);
            questionModel.setLabels(getLabels(value.get("label")));
            questionModel.setOptions(getOptions(value.get("options")));
            questionModel.setRubrics(getRubrics(value.get("rubrics")));
            questionModel.setCompetencies(getCompetencies(value.get("competencies")));
            if (!value.get("narrativefeedback").isNull()) {
                questionModel.setNarrativeFeedback(value.get("narrativefeedback").asText());
            }

            questions.add(questionModel);
        }

        return questions;
    }

    private List<EpassFormLabelModel> getLabels(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            return new ArrayList<EpassFormLabelModel>();
        }

        HashMap<String, String> labelsHashMap = new ObjectMapper().convertValue(jsonNode, HashMap.class);

        List<EpassFormLabelModel> labels = new ArrayList<EpassFormLabelModel>();

        for (HashMap.Entry<String, String> entry : labelsHashMap.entrySet()) {
            String languageCode = entry.getKey();
            String text = entry.getValue();

            EpassFormLabelModel labelModel = new EpassFormLabelModel();
            labelModel.setLanguageCode(languageCode);
            labelModel.setText(text);

            labels.add(labelModel);
        }

        return labels;
    }

    private List<EpassFormQuestionOptionModel> getOptions(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            return new ArrayList<EpassFormQuestionOptionModel>();
        }

        HashMap<String, JsonNode> optionsHashMap = new ObjectMapper().convertValue(jsonNode, HashMap.class);

        List<EpassFormQuestionOptionModel> options = new ArrayList<EpassFormQuestionOptionModel>();

        for (HashMap.Entry<String, JsonNode> entry : optionsHashMap.entrySet()) {
            String id = entry.getKey();
            JsonNode labels = new ObjectMapper().convertValue(entry.getValue(), JsonNode.class);

            EpassFormQuestionOptionModel optionModel = new EpassFormQuestionOptionModel();
            optionModel.setId(id);
            optionModel.setLabels(getLabels(labels));

            options.add(optionModel);
        }

        return options;
    }

    private List<EpassFormQuestionRubricModel> getRubrics(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            return new ArrayList<EpassFormQuestionRubricModel>();
        }

        HashMap<String, JsonNode> rubricsHashMap = new ObjectMapper().convertValue(jsonNode, HashMap.class);

        List<EpassFormQuestionRubricModel> rubrics = new ArrayList<EpassFormQuestionRubricModel>();

        for (HashMap.Entry<String, JsonNode> entry : rubricsHashMap.entrySet()) {
            String id = entry.getKey();
            JsonNode labels = new ObjectMapper().convertValue(entry.getValue(), JsonNode.class);

            EpassFormQuestionRubricModel rubricModel = new EpassFormQuestionRubricModel();
            rubricModel.setId(id);
            rubricModel.setLabels(getLabels(labels));

            rubrics.add(rubricModel);
        }

        return rubrics;
    }

    private List<Integer> getCompetencies(JsonNode jsonNode) {
        List<Integer> competencies = new ObjectMapper().convertValue(jsonNode, ArrayList.class);

        return competencies;
    }
}
