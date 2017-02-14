/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 26/11/2015
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

import eu.watchme.api.exceptions.UnauthorizedException;
import eu.watchme.api.utils.HttpRequestExtras;
import eu.watchme.api.utils.VizRequestValidator;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.logging.LoggingUtils;
import eu.watchme.modules.dispatcher.APIDispatcher;
import eu.watchme.modules.domainmodel.privacymanager.AuthorizationRequestModel;
import eu.watchme.modules.domainmodel.viz.AuthorizationModel;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceQueryModel;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceResponseModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineQueryModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineResponseModel;
import eu.watchme.modules.epass.EpassPrivacyManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/viz")
public class VizResource {

	org.slf4j.Logger logger = LoggerFactory.getLogger(VizResource.class);
	Marker mMarker = MarkerFactory.getMarker("VIZ");

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/currentperformance")
	public CurrentPerformanceResponseModel handleRequest(CurrentPerformanceQueryModel queryModel) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> request data:\n{}", Thread.currentThread().getId(), queryModel == null ? null : LoggingUtils.format(queryModel));
		VizRequestValidator.validate(queryModel);
		checkAccess(queryModel.getModelId(), queryModel.getAuthorisationData());
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		CurrentPerformanceResponseModel result = apiDispatcher.processCurrentPerformanceQuery(queryModel);
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/timeline")
	public TimelineResponseModel handleTimelineRequest(TimelineQueryModel timelineQueryModel) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> request data:\n{}", Thread.currentThread().getId(), timelineQueryModel == null ? null : LoggingUtils.format(timelineQueryModel));
		VizRequestValidator.validate(timelineQueryModel);
		checkAccess(timelineQueryModel.getModelId(), timelineQueryModel.getAuthorisationData());
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		TimelineResponseModel result = apiDispatcher.processQuery(timelineQueryModel);
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/supervisor")
	public SupervisorResponseModel handleSupervisorRequest(SupervisorQueryModel supervisorQueryModel) throws Exception {
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> request data:\n{}", Thread.currentThread().getId(), supervisorQueryModel == null ? null : LoggingUtils.format(supervisorQueryModel));
		VizRequestValidator.validate(supervisorQueryModel);
		// checkAccess(supervisorQueryModel.getModelId(), supervisorQueryModel.getAuthorisationData());
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		SupervisorResponseModel result = apiDispatcher.processQuery(supervisorQueryModel);
		logger.info(mMarker, ">PUT @ Thread-{} :\n\t-> result : \n{}", Thread.currentThread().getId(), LoggingUtils.format(result));
		return result;
	}

	private void checkAccess(String modelId, AuthorizationModel authorisationData) {
		if (ApplicationSettings.isAuthenticateRequestsInPrivacyManagerEnabled()) {
			EpassPrivacyManager privacyManager = new EpassPrivacyManager();
			String accessToken = privacyManager.getAccessToken(modelId);
			if(accessToken == null) throw new UnauthorizedException();

			AuthorizationRequestModel authorizationRequestModel = HttpRequestExtras.getAuthorizationRequestModel(authorisationData, accessToken);
			if (!privacyManager.hasAccess(authorizationRequestModel)) throw new UnauthorizedException();
		} else {
			logger.info("Thread-{}: checkAccess: Skipping authentication with the EPASS PrivacyManager, modelId {}", Thread.currentThread().getId(), modelId);
		}
	}
}
