/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 16/2/2016
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
package eu.watchme.modules.domainmodel.viz.supervisor;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.domainmodel.viz.AuthorizationModel;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SupervisorQueryModel {

    @JsonProperty("authorisationData")
    @SerializedName("authorisationData")
    private AuthorizationModel mAuthorisationData;

    @JsonProperty("modelId")
    @SerializedName("modelId")
    private String mModelId;

    @JsonProperty("students")
    @SerializedName("students")
    private List<SupervisorQueryStudentModel> mStudents;

    public AuthorizationModel getAuthorisationData() {
        return mAuthorisationData;
    }

    public void setAuthorisationData(AuthorizationModel authorisationData) {
        this.mAuthorisationData = authorisationData;
    }

    public String getModelId() {
        return mModelId;
    }

    public void setModelId(String modelId) {
        this.mModelId = modelId;
    }

    public List<SupervisorQueryStudentModel> getStudents() {
        return mStudents;
    }

    public void setStudents(List<SupervisorQueryStudentModel> students) {
        this.mStudents = students;
    }

}
