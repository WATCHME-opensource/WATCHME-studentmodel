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

package eu.watchme.api.help;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionAuthorityModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionFormModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;

public class EpassHelpOutputModel {

    public String getRequestModelAsJson() {
        String result = null;

        Gson gson = new GsonBuilder().create();

        try {
            result = gson.toJson(getRequestModelInstance());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private Object getRequestModelInstance() {
        SubmissionModel model = new SubmissionModel();

        model.setId(1);
        model.setLinks(Arrays.asList("link 1", "link 2"));
        model.setForm(new SubmissionFormModel(1, "1.0.0"));
        model.setReferenceDate("2015-06-25");
        model.setSubmissionDate("2015-06-25");
        model.setAuthority(new SubmissionAuthorityModel("hash", "role"));
        model.setCohort("cohort");
        model.setEPAs(Arrays.asList("1", "2", "3"));

        model.setAnswers(new ObjectMapper().valueToTree(new HashMap<String, Object>() {{
            put("1", 1);
            put("2", Arrays.asList(1, 2, 3));
            put("3", "3");
            put("4", Arrays.asList("string 1", "string 2"));
        }}));

        model.setAverageScores(new ObjectMapper().valueToTree(new HashMap<String, String>() {{
            put("key", "score");
            put("key2", "score");
        }}));

        return model;
    }
}
