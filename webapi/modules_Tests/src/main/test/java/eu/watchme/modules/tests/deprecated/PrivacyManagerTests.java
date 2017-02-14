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

package eu.watchme.modules.tests.deprecated;

import eu.watchme.modules.commons.constants.ModelId;
import eu.watchme.modules.domainmodel.privacymanager.*;
import eu.watchme.modules.epass.EpassAuthenticationServiceClient;
import eu.watchme.modules.epass.EpassPrivacyManager;
import eu.watchme.modules.epass.EpassPrivacyManagerServiceClient;

@Deprecated
public class PrivacyManagerTests {

    public static void main(String args[]) {

        EpassPrivacyManager privacyManager = new EpassPrivacyManager();

        AuthenticationRequestModel authenticationRequestModel = new AuthenticationRequestModel();
        authenticationRequestModel.setUsername("watchme");
        authenticationRequestModel.setPassword("CCCC");
        authenticationRequestModel.setGrantType("client_credentials");

        AuthenticationModel authModel = privacyManager.getAuthenticationModel(ModelId.TTUU);

        AuthorizationRequestModel authorizationRequestModel = new AuthorizationRequestModel();
        authorizationRequestModel.setApplicant("XXX");
        authorizationRequestModel.setStudent("YYY");
        authorizationRequestModel.setSessionToken("ZZZ");
        authorizationRequestModel.setAccessToken(authModel.getAccessToken());

        Boolean hasAccess = privacyManager.hasAccess(authorizationRequestModel);
    }

    private AuthenticationResponseModel authenticate() {
        EpassAuthenticationServiceClient authClient = new EpassAuthenticationServiceClient();

        AuthenticationRequestModel requestModel = new AuthenticationRequestModel();
        requestModel.setUsername("watchme");
        requestModel.setPassword("ZZZZ");
        requestModel.setGrantType("client_credentials");

        return authClient.authenticate(requestModel);
    }

    private AuthorizationResponseModel authorize(String accessToken) {
        EpassPrivacyManagerServiceClient privacyClient = new EpassPrivacyManagerServiceClient();

        AuthorizationRequestModel requestModel = new AuthorizationRequestModel();
        requestModel.setApplicant("CCC");
        requestModel.setStudent("DDD");
        requestModel.setSessionToken("EEE");
        requestModel.setAccessToken(accessToken);

        return privacyClient.authorize(requestModel);
    }
}
