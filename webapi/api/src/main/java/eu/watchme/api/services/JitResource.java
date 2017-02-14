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

package eu.watchme.api.services;

import com.sun.jersey.api.view.Viewable;
import eu.watchme.api.exceptions.UnauthorizedException;
import eu.watchme.api.utils.HttpRequestExtras;
import eu.watchme.api.utils.JitRequestValidator;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.logging.LoggingUtils;
import eu.watchme.modules.dispatcher.APIDispatcher;
import eu.watchme.modules.domainmodel.jit.AuthorizationModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import eu.watchme.modules.domainmodel.jit.FeedbackResponseModel;
import eu.watchme.modules.domainmodel.jit.supervisor.*;
import eu.watchme.modules.domainmodel.privacymanager.AuthorizationRequestModel;
import eu.watchme.modules.epass.EpassPrivacyManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Path("/api/jit")
public class JitResource {
	org.slf4j.Logger logger = LoggerFactory.getLogger(JitResource.class);
	Marker mMarker = MarkerFactory.getMarker("JIT");

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FeedbackResponseModel handleJitRequest(FeedbackQueryModel data) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> request data:\n{}", Thread.currentThread().getId(), data == null ? null : LoggingUtils.format(data));
		JitRequestValidator.validate(data);
		checkAccess(data.getModelId(), data.getAuthorisationData());
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		FeedbackResponseModel result = apiDispatcher.processQuery(data);
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/supervisor")
	public SupervisorResponseModel handleJitSupervisorRequest(SupervisorQueryModel data) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> request data:\n{}", Thread.currentThread().getId(), data == null ? null : LoggingUtils.format(data));
		JitRequestValidator.validate(data);
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();

		List<SupervisedStudentModel> supervisedStudentsWithAlerts = new ArrayList<>(data.getStudents() == null ? 0 : data.getStudents().size());
		if (data.getStudents() != null) {
			for (SupervisorQueryStudentModel student : data.getStudents()) {
				SupervisedStudentPortfolioModel portfolio = apiDispatcher.processJitSupervisorQuery(student.getStudentHash(), data.getModelId(), data.getLanguageCode());
				if (portfolio != null) {
					supervisedStudentsWithAlerts.add(new SupervisedStudentModel(student.getStudentHash(), student.getStudentName(), portfolio));
				}
			}
		}
		String message = supervisedStudentsWithAlerts.isEmpty() ? "You have no students with alerts" : "Alerts: These students require your attention";
		SupervisorResponseModel result = new SupervisorResponseModel(message, supervisedStudentsWithAlerts);

		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}

	/*
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/supervisor/mock")
	public SupervisorResponseModel generateMockSupervisorData(SupervisorQueryModel data) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> MOCK request data:\n{}", Thread.currentThread().getId(), data == null ? null : LoggingUtils.format(data));
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();

		Random random = new Random();
		int noStudents = random.nextInt(5);
		List<SupervisedStudentModel> supervisedStudentsWithAlerts = new ArrayList<>(noStudents);
		for (int index = 1; index <= noStudents; index++) {
			SupervisedStudentPortfolioModel portfolio = apiDispatcher.generateMockData(random, data.getModelId(), data.getLanguageCode());
			if (portfolio != null) {
				supervisedStudentsWithAlerts.add(new SupervisedStudentModel("Hash" + index, "Mock student " + index, portfolio));
			}
		}
		String message = supervisedStudentsWithAlerts.isEmpty() ? "You have no students with alerts" : "Alerts: These students require your attention";
		SupervisorResponseModel result = new SupervisorResponseModel(message, supervisedStudentsWithAlerts);

		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> MOCK result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}
	*/

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable handleJitHelpRequest() {
		logger.info(mMarker, ">GET @ Thread-{}", Thread.currentThread().getId());
		return new Viewable("/jitIndex", new eu.watchme.api.help.JitHelpOutputModel());
	}

	private void checkAccess(String modelId, AuthorizationModel authorisationData) {
		if (ApplicationSettings.isAuthenticateRequestsInPrivacyManagerEnabled()) {
			EpassPrivacyManager privacyManager = new EpassPrivacyManager();
			String accessToken = privacyManager.getAccessToken(modelId);
			if (accessToken == null)
				throw new UnauthorizedException();

			AuthorizationRequestModel authorizationRequestModel = HttpRequestExtras.getAuthorizationRequestModel(authorisationData, accessToken);
			if (!privacyManager.hasAccess(authorizationRequestModel))
				throw new UnauthorizedException();
		} else {
			logger.info("Thread-{}: checkAccess: Skipping authentication with the EPASS PrivacyManager, modelId {}", Thread.currentThread().getId(), modelId);
		}
	}
}
