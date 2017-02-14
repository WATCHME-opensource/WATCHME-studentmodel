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

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.constants.GrantType;
import eu.watchme.modules.dataaccess.dataservices.AuthenticationDataService;
import eu.watchme.modules.dataaccess.model.AuthenticationDataObject;
import eu.watchme.modules.domainmodel.exceptions.ValidationException;
import eu.watchme.modules.domainmodel.privacymanager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class EpassPrivacyManager {

	public static AuthenticationModel mAuthenticationModel;
	private Logger logger = LoggerFactory.getLogger(EpassAuthenticationServiceClient.class);

	public String getAccessToken(String modelId) {
		AuthenticationModel authenticationModel = getAuthenticationModel(modelId);
		return authenticationModel == null ? null : authenticationModel.getAccessToken();
	}

	public AuthenticationModel getAuthenticationModel(String modelId) {
		if (mAuthenticationModel == null || !mAuthenticationModel.getModelId().equals(modelId)) {
			//get Auth from DB
			AuthenticationModel dbAuthModel = getAuthModelFromDB(modelId);

			if (dbAuthModel != null && !isExpired(dbAuthModel.getExpirationDate())) {
				return dbAuthModel;
			}
		} else {
			if (!isExpired(mAuthenticationModel.getExpirationDate())) {
				return mAuthenticationModel;
			}
		}
		// make Service client call
		EpassAuthenticationServiceClient authClient = new EpassAuthenticationServiceClient();
		AuthenticationRequestModel requestModel = new AuthenticationRequestModel();
		requestModel.setUsername(ApplicationSettings.getDomainCredentialsUsername(modelId));
		requestModel.setPassword(ApplicationSettings.getDomainCredentialsSecret(modelId));
		requestModel.setGrantType(GrantType.CLIENT_CREDENTIALS);
		AuthenticationResponseModel authResponseModel = authClient.authenticate(requestModel);

		mAuthenticationModel = toAuthenticationModel(storeAuthenticationModel(authResponseModel, modelId));
		return mAuthenticationModel;
	}

	public Boolean hasAccess(AuthorizationRequestModel authorizationRequestModel) {
		if (authorizationRequestModel == null || isNullOrEmpty(authorizationRequestModel.getApplicant()) || isNullOrEmpty(
				authorizationRequestModel.getAccessToken()) || isNullOrEmpty(authorizationRequestModel.getSessionToken()) || isNullOrEmpty(
				authorizationRequestModel.getStudent())) {
			throw new ValidationException("Missing required authorization fields");
		}

		EpassPrivacyManagerServiceClient privacyClient = new EpassPrivacyManagerServiceClient();
		AuthorizationResponseModel authorizationResponseModel = privacyClient.authorize(authorizationRequestModel);

		return authorizationResponseModel.getAccess();
	}

	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	private AuthenticationModel getAuthModelFromDB(String modelId) {
		AuthenticationDataService dataService = new AuthenticationDataService();
		AuthenticationDataObject dataObject = dataService.getAuthenticationData(modelId);
		return toAuthenticationModel(dataObject);
	}

	private AuthenticationDataObject storeAuthenticationModel(AuthenticationResponseModel authModel, String modelId) {
		if(authModel.getAccessToken() == null || authModel.getAccessToken().trim().isEmpty())
			return null;
		logger.debug("Thread-{} : Storing the authentication response...", Thread.currentThread().getId());
		AuthenticationDataService dataService = new AuthenticationDataService();
		AuthenticationDataObject dataObject = toAuthenticationDataObject(authModel);
		dataObject.setModelId(modelId);
		dataService.storeAuthenticationData(dataObject);
		logger.debug("Thread-{} : Authentication stored in the database", Thread.currentThread().getId());
		return dataObject;
	}

	private Boolean isExpired(LocalDateTime expirationDate) {
		return expirationDate.isBefore(LocalDateTime.now());
	}

	private AuthenticationModel toAuthenticationModel(AuthenticationDataObject dataObject) {
		if(dataObject == null)
			return null;
		AuthenticationModel authModel = new AuthenticationModel();
		authModel.setAccessToken(dataObject.getAccessToken());
		authModel.setExpirationDate(dataObject.getExpirationDate());
		authModel.setScope(dataObject.getScope());
		authModel.setTokenType(dataObject.getTokenType());
		authModel.setModelId(dataObject.getModelId());

		return authModel;
	}

	private AuthenticationDataObject toAuthenticationDataObject(AuthenticationResponseModel authModel) {
		AuthenticationDataObject dataObject = new AuthenticationDataObject();
		dataObject.setAccessToken(authModel.getAccessToken());
		dataObject.setExpirationDate(LocalDateTime.now().plusSeconds(authModel.getExpiresIn()));
		dataObject.setScope(authModel.getScope());
		dataObject.setTokenType(authModel.getTokenType());

		return dataObject;
	}
}
