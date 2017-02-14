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

package eu.watchme.modules.dataaccess.model.requests.jit;

import eu.watchme.modules.commons.enums.LanguageCode;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

@Embedded
public class JitQueryParametersDataObject {
    @Embedded(Properties.AuthorisationData)
    private JitQueryAuthorizationDataObject mAuthorisationData;
    @Property(Properties.SourceUrl)
    private String mSourceUrl;
    @Property(Properties.ModelId)
    private String mModelId;
    @Property(Properties.GroupId)
    private String mGroupId;
    @Property(Properties.LanguageCode)
    private LanguageCode mLanguageCode;
    @Property(Properties.EpaId)
    private String mEpaId;
    @Property(Properties.FeedbackType)
    private String mFeedbackType;

    public JitQueryAuthorizationDataObject getAuthorisationData() {
        return mAuthorisationData;
    }

    public void setAuthorisationData(JitQueryAuthorizationDataObject authorisationData) {
        mAuthorisationData = authorisationData;
    }

    public String getSourceUrl() {
        return mSourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        mSourceUrl = sourceUrl;
    }

    public String getModelId() {
        return mModelId;
    }

    public void setModelId(String modelId) {
        mModelId = modelId;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public LanguageCode getLanguageCode() {
        return mLanguageCode;
    }

    public void setLanguageCode(LanguageCode languageCode) {
        mLanguageCode = languageCode;
    }

    public String getEpaId() {
        return mEpaId;
    }

    public void setEpaId(String epaId) {
        mEpaId = epaId;
    }

    public String getFeedbackType() {
        return mFeedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        mFeedbackType = feedbackType;
    }

    public static class Properties {
        public static final String AuthorisationData = "authorisationData";
        public static final String SourceUrl = "sourceUrl";
        public static final String ModelId = "modelId";
        public static final String GroupId = "groupId";
        public static final String LanguageCode = "languageCode";
        public static final String EpaId = "epaId";
        public static final String FeedbackType = "feedbackType";
    }
}
