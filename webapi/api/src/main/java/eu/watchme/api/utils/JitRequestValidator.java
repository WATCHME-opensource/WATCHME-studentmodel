/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 10/8/2015
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

package eu.watchme.api.utils;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.domainmodel.exceptions.ValidationException;
import eu.watchme.modules.domainmodel.jit.AuthorizationModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import eu.watchme.modules.domainmodel.jit.supervisor.SupervisorQueryModel;

import java.util.Set;

public class JitRequestValidator {
	public static void validate(FeedbackQueryModel data) {
		validateModelId(data.getModelId());
		validateFeedbackType(data);
		validateEpas(data);
		validateAuthorizationData(data);
	}

	public static void validate(SupervisorQueryModel data) {
		validateModelId(data.getModelId());
		validateSupervisorAuthorizationData(data.getAuthorisationData());
	}

	private static void validateAuthorizationData(FeedbackQueryModel data) {
		if (ApplicationSettings.isAuthenticateRequestsInPrivacyManagerEnabled()) {
			if (data.getAuthorisationData() == null) {
				throw new ValidationException("Authorisation data required");
			}
			testRequired("Student ID", data.getAuthorisationData().getStudentHash());
			testRequired("Session token", data.getAuthorisationData().getSessionToken());
			testRequired("Applicant hash", data.getAuthorisationData().getApplicantHash());
		}
	}

	private static void validateModelId(String modelId) {
		testRequired("Model ID", modelId);
		if (!ApplicationSettings.isDomainSupported(modelId)) {
			throw new ValidationException("Invalid Model ID");
		}
	}

	private static void validateFeedbackType(FeedbackQueryModel data) {
		String feedbackType = data.getFeedbackType();
		if (feedbackType != null) {
			if (feedbackType.trim().isEmpty() || noMappedFieldAvailable(data.getModelId(), feedbackType)) {
				throw new ValidationException("Invalid feedback type");
			}
		}
	}

	private static void validateEpas(FeedbackQueryModel data) {
		Set<String> epaIds = data.getEpaIdsForFiltering();
		for (String epaId : epaIds) {
			if (StaticModelManager.getManager(data.getModelId()).getFormMapper().getEpasMapper().getMappedFieldFromEpass(epaId) == null) {
				throw new ValidationException(String.format("Invalid epa ( %s )", epaId));
			}
		}
	}

	private static boolean noMappedFieldAvailable(String modelId, String feedbackType) {
		Object mappedField = StaticModelManager.getManager(modelId).getFormMapper().getFeedbackIndicatorMapper().getMappedFieldFromEpass(feedbackType);
		return mappedField == null;
	}

	private static void testRequired(String fieldName, String fieldValue) {
		if (fieldValue == null || fieldValue.trim().isEmpty()) {
			throw new ValidationException(String.format("%s required", fieldName));
		}
	}

	private static void validateSupervisorAuthorizationData(AuthorizationModel authorizationModel) {
		if (authorizationModel == null) {
			throw new ValidationException("Authorisation data required");
		}
		if (ApplicationSettings.isAuthenticateRequestsInPrivacyManagerEnabled()) {
			testRequired("Session token", authorizationModel.getSessionToken());
			testRequired("Applicant hash", authorizationModel.getApplicantHash());
		}
	}
}
