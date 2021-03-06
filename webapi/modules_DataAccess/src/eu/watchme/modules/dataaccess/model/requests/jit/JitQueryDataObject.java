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

package eu.watchme.modules.dataaccess.model.requests.jit;

import eu.watchme.modules.dataaccess.model.DataObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;

@Entity("jitQueries")
public class JitQueryDataObject extends DataObject {
	@Id
	private ObjectId mId;
	@Property(Properties.Source)
	private String mSource;
	@Property(Properties.Topics)
	private String[] mTopics;
	@Property(Properties.AuthToken)
	private String mAuthorizationToken;
	@Property(Properties.StudentId)
	private String mStudentId;
	@Property(Properties.GroupId)
	private String mGroupId;
	@Property(Properties.ModelId)
	private String mModelId;
	@Embedded(Properties.Parameters)
	private JitQueryParametersDataObject mParameters;
	@Property(Properties.DateSubmitted)
	private Instant mDateSubmitted;
	@Embedded(Properties.Response)
	private JitQueryResponse mResponse;

	@Override
	public Object getId() {
		return mId;
	}

	public String getSource() {
		return mSource;
	}

	public void setSource(String source) {
		mSource = source;
	}

	public String[] getTopics() {
		return mTopics;
	}

	public void setTopics(String[] topics) {
		mTopics = topics;
	}

	public String getAuthorizationToken() {
		return mAuthorizationToken;
	}

	public void setAuthorizationToken(String authorizationToken) {
		mAuthorizationToken = authorizationToken;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public void setStudentId(String studentId) {
		mStudentId = studentId;
	}

	public String getGroupId() {
		return mGroupId;
	}

	public void setGroupId(String groupId) {
		mGroupId = groupId;
	}

	public String getModelId() {
		return mModelId;
	}

	public void setModelId(String modelId) {
		mModelId = modelId;
	}

	public JitQueryParametersDataObject getParameters() {
		return mParameters;
	}

	public void setParameters(JitQueryParametersDataObject parameters) {
		mParameters = parameters;
	}

	public Instant getDateSubmitted() {
		return mDateSubmitted;
	}

	public void setDateSubmitted(Instant dateSubmitted) {
		mDateSubmitted = dateSubmitted;
	}

	public JitQueryResponse getResponse() {
		return mResponse;
	}

	public void setResponse(JitQueryResponse response) {
		mResponse = response;
	}

	public static class Properties {
		public static final String Source = "source";
		public static final String Topics = "topics";
		public static final String AuthToken = "authToken";
		public static final String StudentId = "studentId";
		public static final String GroupId = "groupId";
		public static final String ModelId = "modelId";
		public static final String Parameters = "parameters";
		public static final String DateSubmitted = "dateSubmitted";
		public static final String Response = "response";
	}
}
