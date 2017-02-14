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

package eu.watchme.modules.dispatcher.converters;

import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryAuthorizationDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryParametersDataObject;
import eu.watchme.modules.domainmodel.jit.AuthorizationModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;

class JitQueryConverter extends Converter<FeedbackQueryModel, JitQueryDataObject> {

	@Override
	public JitQueryDataObject toDataObject(FeedbackQueryModel model) {
		if (model == null) {
			return null;
		}
		JitQueryDataObject dataObject = new JitQueryDataObject();

		JitQueryParametersDataObject paramsDataObject = new JitQueryParametersDataObject();
		paramsDataObject.setModelId(model.getModelId());
		paramsDataObject.setGroupId(model.getGroupId());
		paramsDataObject.setAuthorisationData(toDataObject(model.getAuthorisationData()));
		paramsDataObject.setEpaId(model.getEpaId());
		paramsDataObject.setFeedbackType(model.getFeedbackType());
		paramsDataObject.setLanguageCode(model.getLanguageCode());
		paramsDataObject.setSourceUrl(model.getSourceUrl());

		dataObject.setParameters(paramsDataObject);
		return dataObject;
	}

	@Override
	public FeedbackQueryModel fromDataObject(JitQueryDataObject dataObject) {
		if (dataObject == null) {
			return null;
		}
		FeedbackQueryModel queryModel = new FeedbackQueryModel();
		if (dataObject.getParameters() != null) {
			queryModel.setModelId(dataObject.getParameters().getModelId());
			queryModel.setGroupId(dataObject.getParameters().getGroupId());
			queryModel.setAuthorisationData(fromDataObject(dataObject.getParameters().getAuthorisationData()));
			queryModel.setEpaId(dataObject.getParameters().getEpaId());
			queryModel.setFeedbackType(dataObject.getParameters().getFeedbackType());
			queryModel.setLanguageCode(dataObject.getParameters().getLanguageCode());
			queryModel.setSourceUrl(dataObject.getParameters().getSourceUrl());
		}
		return queryModel;
	}

	private JitQueryAuthorizationDataObject toDataObject(AuthorizationModel authorisationData) {
		if (authorisationData == null) {
			return null;
		}
		JitQueryAuthorizationDataObject dataObject = new JitQueryAuthorizationDataObject();
		dataObject.setApplicantHash(authorisationData.getApplicantHash());
		dataObject.setSessionToken(authorisationData.getSessionToken());
		dataObject.setStudentHash(authorisationData.getStudentHash());
		return dataObject;
	}

	private AuthorizationModel fromDataObject(JitQueryAuthorizationDataObject authorisationData) {
		if (authorisationData == null) {
			return null;
		}
		AuthorizationModel model = new AuthorizationModel();
		model.setApplicantHash(authorisationData.getApplicantHash());
		model.setSessionToken(authorisationData.getSessionToken());
		model.setStudentHash(authorisationData.getStudentHash());
		return model;
	}
}
