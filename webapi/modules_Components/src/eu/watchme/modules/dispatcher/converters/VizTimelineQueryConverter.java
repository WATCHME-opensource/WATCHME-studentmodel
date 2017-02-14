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
package eu.watchme.modules.dispatcher.converters;

import eu.watchme.modules.dataaccess.model.requests.viz.VizQueryAuthorizationDataObject;
import eu.watchme.modules.dataaccess.model.requests.viz.timeline.TimelineQueryDataObject;
import eu.watchme.modules.domainmodel.viz.AuthorizationModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineQueryModel;

public class VizTimelineQueryConverter extends Converter<TimelineQueryModel, TimelineQueryDataObject> {

	@Override
	public TimelineQueryDataObject toDataObject(TimelineQueryModel model) {
		if (model == null) {
			return null;
		}
		TimelineQueryDataObject dataObject = new TimelineQueryDataObject();
		dataObject.setModelId(model.getModelId());
		dataObject.setEpaId(model.getEpaId());
		dataObject.setLanguageCode(model.getLanguageCode());
		dataObject.setAuthorisationData(toDataObject(model.getAuthorisationData()));

		return dataObject;
	}

	@Override
	public TimelineQueryModel fromDataObject(TimelineQueryDataObject dataObject) {
		if (dataObject == null) {
			return null;
		}

		TimelineQueryModel model = new TimelineQueryModel();
		model.setModelId(dataObject.getModelId());
		model.setEpaId(dataObject.getEpaId());
		model.setLanguageCode(dataObject.getLanguageCode());
		model.setAuthorisationData(fromDataObject(dataObject.getAuthorisationData()));

		return model;
	}

	private VizQueryAuthorizationDataObject toDataObject(AuthorizationModel authorisationData) {
		if (authorisationData == null) {
			return null;
		}
		VizQueryAuthorizationDataObject dataObject = new VizQueryAuthorizationDataObject();
		dataObject.setApplicantHash(authorisationData.getApplicantHash());
		dataObject.setSessionToken(authorisationData.getSessionToken());
		dataObject.setStudentHash(authorisationData.getStudentHash());
		return dataObject;
	}

	private AuthorizationModel fromDataObject(VizQueryAuthorizationDataObject authorisationData) {
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
