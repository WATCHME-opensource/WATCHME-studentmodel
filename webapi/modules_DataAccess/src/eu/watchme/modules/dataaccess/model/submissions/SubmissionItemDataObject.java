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

package eu.watchme.modules.dataaccess.model.submissions;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.HashMap;
import java.util.List;

@Embedded
public class SubmissionItemDataObject {
    @Property(Properties.Id)
    private Integer mId;
    @Property(Properties.Links)
    private String[] mLinks;
    @Embedded(Properties.Form)
    private SubmissionFormDataObject mForm;
    @Property(Properties.ReferenceDate)
    private String mReferenceDate;
    @Property(Properties.SubmissionDate)
    private String mSubmissionDate;
    @Embedded(Properties.Authority)
    private SubmissionAuthorityDataObject mAuthority;
    @Property(Properties.Cohort)
    private String mCohort;
    @Property(Properties.EPAs)
    private List<String> mEPAs;
    @Embedded(Properties.Answers)
    private HashMap<String, Object> mAnswers;
    @Embedded(Properties.AverageScores)
    private HashMap<String, String> mAverageScores;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String[] getLinks() {
        return mLinks;
    }

    public void setLinks(String[] links) {
        mLinks = links;
    }

    public SubmissionFormDataObject getForm() {
        return mForm;
    }

    public void setForm(SubmissionFormDataObject form) {
        mForm = form;
    }

    public String getReferenceDate() {
        return mReferenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        mReferenceDate = referenceDate;
    }

    public String getSubmissionDate() {
        return mSubmissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        mSubmissionDate = submissionDate;
    }

    public SubmissionAuthorityDataObject getAuthority() {
        return mAuthority;
    }

    public void setAuthority(SubmissionAuthorityDataObject authority) {
        mAuthority = authority;
    }

    public String getCohort() {
        return mCohort;
    }

    public void setCohort(String cohort) {
        mCohort = cohort;
    }

    public List<String> getEPAs() {
        return mEPAs;
    }

    public void setEPAs(List<String> ePAs) {
        mEPAs = ePAs;
    }

    public HashMap<String, Object> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(HashMap<String, Object> answers) {
        mAnswers = answers;
    }

    public HashMap<String, String> getAverageScores() {
        return mAverageScores;
    }

    public void setAverageScores(HashMap<String, String> averageScores) {
        mAverageScores = averageScores;
    }

    public static class Properties {
        public static final String Id = "id";
        public static final String Links = "links";
        public static final String Form = "form";
        public static final String ReferenceDate = "referenceDate";
        public static final String SubmissionDate = "submissionDate";
        public static final String Authority = "authority";
        public static final String Cohort = "cohort";
        public static final String EPAs = "epas";
        public static final String Answers = "answers";
        public static final String AverageScores = "averagescores";
    }
}
