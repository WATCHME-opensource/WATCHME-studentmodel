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
import com.sun.jersey.multipart.FormDataParam;
import eu.watchme.api.exceptions.UnauthorizedException;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.dispatcher.APIDispatcher;
import eu.watchme.modules.domainmodel.TDelete;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/epass")
public class EpassResource {

	private ObjectMapper objectMapper = new ObjectMapper();
	org.slf4j.Logger logger = LoggerFactory.getLogger(EpassResource.class);
	Marker mMarker = MarkerFactory.getMarker("EPASS");

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Object handleEpassRequest(@FormDataParam("Source") String source,
			@FormDataParam("Topic") String topic,
			@FormDataParam("Student ID") String studentId,
			@FormDataParam("Model ID") String modelId,
			@FormDataParam("Content") String content) throws Exception {
		logger.info(mMarker, ">POST @ Thread-{} :\n\t-> source {};\n\t-> topic {};\n\t-> studentId {};\n\t-> modelId {};\n\t-> content {}",
				Thread.currentThread().getId(), source, topic, studentId, modelId, content);
		TPost<SubmissionModel> data = new TPost<>();
		data.setSource(source);
		data.setTopics(objectMapper.readValue(topic, String[].class));
		data.setStudentId(studentId);
		data.setModelId(modelId);
		data.setContent(objectMapper.readValue(content, SubmissionModel[].class));

		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		apiDispatcher.dispatch(data);

		Object result = new String[] { "OK!" };
		logger.info(mMarker, ">POST @ Thread-{} :\n\t-> result {}", Thread.currentThread().getId(), "OK");
		return result;
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/snapshot")
	public Object recomputeCurrentSnapshot(
			@FormDataParam("Auth") String authToken,
			@FormDataParam("Student ID") String studentId,
			@FormDataParam("Model ID") String modelId) throws Exception {
		logger.info(mMarker, ">POST @ Thread-{} :\n\t-> studentId {};\n\t-> modelId {}", Thread.currentThread().getId(), studentId, modelId);
		if (authToken == null || !ApplicationSettings.getAdminAuthToken().equals(authToken)) {
			throw new UnauthorizedException();
		}
		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		boolean done = apiDispatcher.recomputeCurrentSnapshot(studentId, modelId);
		return new String[] { Boolean.toString(done) };
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable handleEpassGetRequest() {
		logger.info(mMarker, ">GET @ Thread-{}", Thread.currentThread().getId());
		return new Viewable("/epassIndex", new eu.watchme.api.help.EpassHelpOutputModel());
	}

	@DELETE
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response handleDeleteRequest(@FormDataParam("Source") String source,
			@FormDataParam("Student ID") String studentId,
			@FormDataParam("Group ID") String groupId,
			@FormDataParam("Model ID") String modelId,
			@FormDataParam("Invalidate") String invalidate) {
		logger.info(mMarker, ">DELETE @ Thread-{} :\n\t-> source {};\n\t-> studentId {};\n\t-> groupId {};\n\t-> modelId {};\n\t-> invalidate {};",
				Thread.currentThread().getId(), source, studentId, groupId, modelId, invalidate);
		TDelete data = new TDelete();
		data.setSource(source);
		data.setStudentId(studentId);
		data.setGroupId(groupId);
		data.setModelId(modelId);
		data.setInvalidate(Boolean.valueOf(invalidate));

		APIDispatcher apiDispatcher = APIDispatcher.newInstance();
		apiDispatcher.delete(data);

		Response response = Response.ok().build();
		logger.info(mMarker, ">DELETE @ Thread-{} :\n\t-> result {}", Thread.currentThread().getId(), "OK");
		return response;
	}
}
