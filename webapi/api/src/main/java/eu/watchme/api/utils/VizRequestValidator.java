/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 27/11/2015
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
import eu.watchme.modules.domainmodel.viz.AuthorizationModel;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceQueryModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryStudentModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineQueryModel;

import java.util.List;
import java.util.Set;

public class VizRequestValidator {

	public static void validate(CurrentPerformanceQueryModel query) {
		if (query == null) {
			throw new ValidationException("Missing query data");
		}
		validateModelId(query.getModelId());
		validateAuthorizationData(query.getAuthorisationData());
		validateEpas(query.getModelId(), query.getEpaIdsForFiltering());
	}

	public static void validate(TimelineQueryModel query) {
		if (query == null) {
			throw new ValidationException("Missing query data");
		}
		validateModelId(query.getModelId());
		validateAuthorizationData(query.getAuthorisationData());
		validateEpas(query.getModelId(), query.getEpaIdsForFiltering());
	}

	public static void validate(SupervisorQueryModel query) {
		if (query == null) {
			throw new ValidationException("Missing query data");
		}
		validateModelId(query.getModelId());
		validateSupervisorAuthorizationData(query.getAuthorisationData());
		validateStudents(query.getStudents());
	}

	private static void validateModelId(String modelId) {
		testRequired("Model ID", modelId);
		if (!ApplicationSettings.isDomainSupported(modelId)) {
			throw new ValidationException("Invalid Model ID");
		}
	}

	private static void validateAuthorizationData(AuthorizationModel authorizationModel) {
		if (authorizationModel == null) {
			throw new ValidationException("Authorisation data required");
		}
		testRequired("Student ID", authorizationModel.getStudentHash());
		if (ApplicationSettings.isAuthenticateRequestsInPrivacyManagerEnabled()) {
			testRequired("Session token", authorizationModel.getSessionToken());
			testRequired("Applicant hash", authorizationModel.getApplicantHash());
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

	private static void validateStudents(List<SupervisorQueryStudentModel> students) {
		if (students != null) {
			for (SupervisorQueryStudentModel studentModel : students) {
				testRequired("Student hash", studentModel.getStudentHash());
			}
		}

	}

	private static void validateEpas(String modelId, Set<String> epaIds) {
		for (String epaId : epaIds) {
			if (StaticModelManager.getManager(modelId).getFormMapper().getEpasMapper().getMappedFieldFromEpass(epaId) == null) {
				throw new ValidationException(String.format("Invalid epa ( %s )", epaId));
			}
		}
	}

	private static void testRequired(String fieldName, String fieldValue) {
		if (fieldValue == null || fieldValue.trim().isEmpty()) {
			throw new ValidationException(String.format("%s required", fieldName));
		}
	}
}
