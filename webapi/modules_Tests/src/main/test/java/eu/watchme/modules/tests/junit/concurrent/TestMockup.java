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

package eu.watchme.modules.tests.junit.concurrent;

import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionAuthorityModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionFormModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import eu.watchme.modules.domainmodel.jit.AuthorizationModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class TestMockup {
    public static TPost<SubmissionModel> createDummySubmissionData() {
        TPost<SubmissionModel> postData = new TPost<>();
        postData.setStudentId("XXXX");
        postData.setModelId("EXAMPLE-SCHOOL");
        postData.setSource("https://www.example.eu");
        postData.setTopics(new String[]{"topic"});
        postData.setContent(new SubmissionModel[]{getRequestModelInstance()});
        return postData;
    }

    public static SubmissionModel getRequestModelInstance() {
        SubmissionModel model = new SubmissionModel();

        model.setId(6);
        model.setLinks(new LinkedList<>());
        model.setForm(new SubmissionFormModel(3, "006"));
        model.setReferenceDate("2015-07-01");
        model.setSubmissionDate("2015-07-01");
        model.setAuthority(new SubmissionAuthorityModel("xxxx", "null"));
        model.setCohort("1-1");
        model.setEPAs(Collections.singletonList("1"));

        model.setAnswers(new ObjectMapper().valueToTree(new HashMap<String, Object>() {{
            put("vraag1", 1);
            put("vraag2", 2);
            put("vraag3", 3);
            put("vraag4", 4);
        }}));

        model.setAverageScores(new ObjectMapper().valueToTree(new HashMap<String, String>() {{
            put("10", "10");
            put("20", "20");
        }}));

        return model;
    }

    public static FeedbackQueryModel createDummyFeedbackQueryData() {
        FeedbackQueryModel model = new FeedbackQueryModel();
        model.setAuthorisationData(new AuthorizationModel());
        model.getAuthorisationData().setApplicantHash("ZZZ");
        model.getAuthorisationData().setStudentHash("YYY");
        model.getAuthorisationData().setSessionToken("ZZZ");

        model.setEpaId("*");
        model.setGroupId("XXX");
        model.setLanguageCode(LanguageCode.EN);
        model.setModelId("EXAMPLE-SCHOOL");
        model.setSourceUrl("https://www.example.eu");

        return model;
    }
}
