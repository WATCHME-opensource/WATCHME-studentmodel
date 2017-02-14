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

package eu.watchme.modules.domainmodel.epass.submissions;

import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmissionModel {

    @JsonProperty("id")
    @SerializedName("id")
    private Integer mId;

    @JsonProperty("links")
    @SerializedName("links")
    private List<String> mLinks;

    @JsonProperty("form")
    @SerializedName("form")
    private SubmissionFormModel mForm;

    @JsonProperty("referencedate")
    @SerializedName("referencedate")
    private String mReferenceDate;

    @JsonProperty("submissiondate")
    @SerializedName("submissiondate")
    private String mSubmissionDate;

    @JsonProperty("authority")
    @SerializedName("authority")
    private SubmissionAuthorityModel mAuthority;

    @JsonProperty("cohort")
    @SerializedName("cohort")
    private String mCohort;

    @JsonProperty("epas")
    @SerializedName("epas")
    private List<String> mEPAs;

    @JsonProperty("answers")
    @SerializedName("answers")
    private JsonNode mAnswers;

    @JsonProperty("averagescores")
    @SerializedName("averagescores")
    private JsonNode mAverageScores;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public List<String> getLinks() {
        return mLinks;
    }

    public void setLinks(List<String> links) {
        mLinks = links;
    }

    public SubmissionFormModel getForm() {
        return mForm;
    }

    public void setForm(SubmissionFormModel form) {
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

    public SubmissionAuthorityModel getAuthority() {
        return mAuthority;
    }

    public void setAuthority(SubmissionAuthorityModel authority) {
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

    public JsonNode getAnswers() {
        return mAnswers;
    }

    public void setAnswers(JsonNode answers) {
        mAnswers = answers;
    }

    public JsonNode getAverageScores() {
        return mAverageScores;
    }

    public void setAverageScores(JsonNode averageScores) {
        mAverageScores = averageScores;
    }

}
