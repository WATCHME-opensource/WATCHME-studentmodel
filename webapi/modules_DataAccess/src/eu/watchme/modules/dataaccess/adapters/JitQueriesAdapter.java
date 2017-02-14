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

package eu.watchme.modules.dataaccess.adapters;

import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryAuthorizationDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryParametersDataObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

public class JitQueriesAdapter extends BaseAdapter<JitQueryDataObject> {
	public JitQueriesAdapter() {
		super(JitQueryDataObject.class);
	}

	public JitQueryDataObject getExistingQuery(String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		Datastore datastore = getDataStore();
		return datastore.createQuery(JitQueryDataObject.class)
				.field(parametersField(authorizationField(JitQueryAuthorizationDataObject.Properties.StudentHash))).equal(studentId)
				.field(parametersField(JitQueryParametersDataObject.Properties.ModelId)).equal(modelId)
				.field(parametersField(JitQueryParametersDataObject.Properties.EpaId)).equal(epaId)
				.field(parametersField(JitQueryParametersDataObject.Properties.FeedbackType)).equal(feedbackType)
				.field(parametersField(JitQueryParametersDataObject.Properties.LanguageCode)).equal(languageCode)
				.get();
	}

	public int deleteQueries(String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		Datastore datastore = getDataStore();
		Query query = datastore.createQuery(JitQueryDataObject.class)
				.field(parametersField(authorizationField(JitQueryAuthorizationDataObject.Properties.StudentHash))).equal(studentId)
				.field(parametersField(JitQueryParametersDataObject.Properties.ModelId)).equal(modelId)
				.field(parametersField(JitQueryParametersDataObject.Properties.EpaId)).equal(epaId)
				.field(parametersField(JitQueryParametersDataObject.Properties.FeedbackType)).equal(feedbackType)
				.field(parametersField(JitQueryParametersDataObject.Properties.LanguageCode)).equal(languageCode);
		return datastore.delete(query).getN();
	}

	public int deleteAllForStudent(String studentId) {
		Datastore datastore = getDataStore();
		Query query = datastore.createQuery(JitQueryDataObject.class)
				.field(parametersField(authorizationField(JitQueryAuthorizationDataObject.Properties.StudentHash))).equal(studentId);
		return datastore.delete(query).getN();
	}

	public int update(JitQueryDataObject queryDataObject, String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		Datastore datastore = getDataStore();
		Query query = datastore.createQuery(JitQueryDataObject.class)
				.field(parametersField(authorizationField(JitQueryAuthorizationDataObject.Properties.StudentHash))).equal(studentId)
				.field(parametersField(JitQueryParametersDataObject.Properties.ModelId)).equal(modelId)
				.field(parametersField(JitQueryParametersDataObject.Properties.EpaId)).equal(epaId)
				.field(parametersField(JitQueryParametersDataObject.Properties.FeedbackType)).equal(feedbackType)
				.field(parametersField(JitQueryParametersDataObject.Properties.LanguageCode)).equal(languageCode);
		UpdateResults updateResults = datastore.updateFirst(query, queryDataObject, true);
		return updateResults.getUpdatedCount() > 0 ? updateResults.getUpdatedCount() : updateResults.getInsertedCount();
	}

	private String parametersField(String parametersFieldName) {
		return String.format("%s.%s", JitQueryDataObject.Properties.Parameters, parametersFieldName);
	}

	private String authorizationField(String authorizationFieldName) {
		return String.format("%s.%s", JitQueryParametersDataObject.Properties.AuthorisationData, authorizationFieldName);
	}
}
