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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.logging.LoggingUtils;
import eu.watchme.modules.domainmodel.privacymanager.AuthenticationRequestModel;
import eu.watchme.modules.domainmodel.privacymanager.AuthenticationResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class EpassAuthenticationServiceClient extends EpassBaseClient {
    private Logger logger = LoggerFactory.getLogger(EpassAuthenticationServiceClient.class);

    // Login
    public AuthenticationResponseModel authenticate(AuthenticationRequestModel requestModel) {
        logger.debug("Thread-{} : Authenticating to EPASS with request:\n{}", Thread.currentThread().getId(), LoggingUtils.format(requestModel));
        Client client = Client.create(getClientConfig());
        client.addFilter(new HTTPBasicAuthFilter(requestModel.getUsername(), requestModel.getPassword()));

        WebResource resource = client.resource(ApplicationSettings.getEpassUrlLogin());

        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("grant_type", requestModel.getGrantType());

        ClientResponse response = resource
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, formData);

        AuthenticationResponseModel result = response.getEntity(AuthenticationResponseModel.class);
		logger.debug("Thread-{} : Login response:\n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
        return result;
    }

}
