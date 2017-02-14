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

package eu.watchme.modules.dataaccess.model.forms;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.List;

@Embedded
public class QuestionDataObject {
    @Property(Properties.Id)
    private String mQuestionId;
    @Embedded(Properties.Labels)
    private LabelDataObject[] mLabels;
    @Embedded(Properties.Options)
    private QuestionOptionDataObject[] mOptions;
    @Embedded(Properties.Rubrics)
    private QuestionRubricDataObject[] mRubrics;
    @Property(Properties.Competencies)
    private List<Integer> mCompetencies;
    @Property(Properties.Narrativefeedback)
    private String mNarrativefeedback;

    public QuestionDataObject() {
    }

    public QuestionDataObject(String id, LabelDataObject[] labelss, QuestionOptionDataObject[] options, QuestionRubricDataObject[] rubrics, List<Integer> competencies,
                              String narrativefeedback) {
        mQuestionId = id;
        mLabels = labelss;
        mOptions = options;
        mRubrics = rubrics;
        mCompetencies = competencies;
        mNarrativefeedback = narrativefeedback;
    }

    public String getQuestionId() {
        return mQuestionId;
    }

    public void setQuestionId(String id) {
        mQuestionId = id;
    }

    public LabelDataObject[] getLables() {
        return mLabels;
    }

    public void setLabels(LabelDataObject[] labels) {
        mLabels = labels;
    }

    public QuestionOptionDataObject[] getOptions() {
        return mOptions;
    }

    public void setOptions(QuestionOptionDataObject[] options) {
        mOptions = options;
    }

    public QuestionRubricDataObject[] getRubrics() {
        return mRubrics;
    }

    public void setRubrics(QuestionRubricDataObject[] rubrics) {
        mRubrics = rubrics;
    }

    public List<Integer> getCompetencies() {
        return mCompetencies;
    }

    public void setCompetencies(List<Integer> competencies) {
        mCompetencies = competencies;
    }

    public String getNarrativefeedback() {
        return mNarrativefeedback;
    }

    public void setNarrativefeedback(String narrativefeedback) {
        mNarrativefeedback = narrativefeedback;
    }

    public static class Properties {
        public static final String Id = "id";
        public static final String Labels = "label";
        public static final String Options = "options";
        public static final String Rubrics = "rubrics";
        public static final String Competencies = "competencies";
        public static final String Narrativefeedback = "narrativefeedback";
    }

}
