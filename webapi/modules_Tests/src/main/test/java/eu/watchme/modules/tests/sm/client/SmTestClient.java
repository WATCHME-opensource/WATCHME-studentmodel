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

package eu.watchme.modules.tests.sm.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.multipart.FormDataMultiPart;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import eu.watchme.modules.domainmodel.jit.FeedbackResponseModel;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.net.URI;

import static eu.watchme.modules.tests.sm.client.TestHelper.asString;
import static eu.watchme.modules.tests.sm.client.TestHelper.fromString;

public class SmTestClient {
    private WebResource epassEndPointResource;
    private WebResource jitEndPointResource;

    private ClientConfig getClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        return clientConfig;
    }

    public void initClient(URI epassEndPoint, URI jitEndPoint) {
        Client client = Client.create(getClientConfig());
        epassEndPointResource = client.resource(epassEndPoint);
        jitEndPointResource = client.resource(jitEndPoint);
    }

    public boolean postEpassForm(TPost<SubmissionModel> data) {
        ClientResponse response = epassEndPointResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, convert(data));
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public FeedbackResponseModel queryJitData(FeedbackQueryModel queryModel) {
        String queryModelJson = asString(queryModel);
        ClientResponse response = jitEndPointResource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, queryModelJson);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            FeedbackResponseModel responseModel = fromString(response.getEntity(String.class),
                    FeedbackResponseModel.class);
            if (responseModel != null) {
                return responseModel;
            }
        }
        return null;
    }

    private String getStringValue(Field field, Object data) {
        try {
            field.setAccessible(true);
            return asString(field.get(data));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private FormDataMultiPart convert(TPost<SubmissionModel> data) {
        FormDataMultiPart multipart = new FormDataMultiPart();
        for (Field field : data.getClass().getDeclaredFields()) {
            JsonProperty fieldAnnotation = field.getAnnotation(JsonProperty.class);
            String fieldValue = getStringValue(field, data);
            if (fieldValue != null) {
                multipart.field(fieldAnnotation.value(), fieldValue);
            }
        }

        return multipart;
    }
}
