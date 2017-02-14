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

import eu.watchme.modules.dataaccess.model.EpassDeleteDataObject;
import eu.watchme.modules.dataaccess.model.forms.EpassFormDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponse;
import eu.watchme.modules.dataaccess.model.requests.viz.timeline.TimelineQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.viz.timeline.TimelineQueryResponse;
import eu.watchme.modules.dataaccess.model.submissions.EpassRequestDataObject;
import eu.watchme.modules.domainmodel.TDelete;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.forms.EpassFormModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import eu.watchme.modules.domainmodel.jit.FeedbackResponseModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineQueryModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineResponseModel;

public abstract class Converter<I, R> {

	public static Converter<TPost<SubmissionModel>, EpassRequestDataObject> getEpassSubmissionConverter() {
		return new EpassRequestConverter();
	}

	public static Converter<TDelete, EpassDeleteDataObject> getEpassDeleteConverter() {
		return new EpassDeleteConverter();
	}

	public static Converter<EpassFormModel, EpassFormDataObject> getEpassFormConverter() {
		return new EpassFormConvertor();
	}

	public static Converter<FeedbackQueryModel, JitQueryDataObject> getJitQueryConverter() {
		return new JitQueryConverter();
	}

	public static Converter<FeedbackResponseModel, JitQueryResponse> getJitQueryResponseConverter() {
		return new JitQueryResponseConverter();
	}

	public static Converter<TimelineQueryModel, TimelineQueryDataObject> getVizTimelineQueryConverter() {
		return new VizTimelineQueryConverter();
	}

	public static Converter<TimelineResponseModel, TimelineQueryResponse> getVizTimelineQueryResponseConverter() {
		return new VizTimelineResponseConverter();
	}

	public abstract R toDataObject(I model);

	public abstract I fromDataObject(R dataObject);
}
