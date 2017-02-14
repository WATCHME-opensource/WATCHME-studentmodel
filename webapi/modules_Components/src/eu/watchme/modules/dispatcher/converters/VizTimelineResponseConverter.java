/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 11/2/2016
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

import eu.watchme.modules.dataaccess.model.requests.viz.timeline.*;
import eu.watchme.modules.domainmodel.viz.timeline.*;

import java.util.List;
import java.util.stream.Collectors;

public class VizTimelineResponseConverter extends Converter<TimelineResponseModel, TimelineQueryResponse> {

	@Override
	public TimelineQueryResponse toDataObject(TimelineResponseModel model) {
		if (model == null) {
			return null;
		}
		TimelineQueryResponse dataObject = new TimelineQueryResponse();
		dataObject.setUpdatedInstant(model.getUpdatedInstant());
		dataObject.setRoles(model.getRoles() == null ? null : model.getRoles().stream().map(this::toDataObject).collect(Collectors.toList()));
		return dataObject;
	}

	@Override
	public TimelineResponseModel fromDataObject(TimelineQueryResponse dataObject) {
		if (dataObject == null) {
			return null;
		}
		TimelineResponseModel model = new TimelineResponseModel();
		model.setUpdatedInstant(dataObject.getUpdatedInstant());
		model.setRoles(dataObject.getRoles() == null ? null : dataObject.getRoles().stream().map(this::fromDataObject).collect(Collectors.toList()));
		return model;
	}

	private TimelineQueryResponseRole toDataObject(ResponseRoleModel model) {
		TimelineQueryResponseRole dataObject = new TimelineQueryResponseRole();
		dataObject.setName(model.getName());
		List<TimelineQueryResponseEpa> EPAs = model.getEPAs() == null ? null : model.getEPAs().stream().map(this::toDataObject).collect(Collectors.toList());
		dataObject.setEPAs(EPAs);
		return dataObject;
	}

	private TimelineQueryResponseEpa toDataObject(ResponseEpaModel model) {
		TimelineQueryResponseEpa dataObject = new TimelineQueryResponseEpa();
		dataObject.setName(model.getName());
		dataObject.setEpassId(model.getEpassId());
		dataObject.setDescription(model.getDescription());
		dataObject.setLevels(model.getLevels() == null ? null : model.getLevels().stream().map(this::toDataObject).collect(Collectors.toList()));
		List<TimelineQueryResponsePI> performanceIndicators =
				model.getPerformanceIndicators() == null ? null : model.getPerformanceIndicators().stream().map(this::toDataObject).collect(Collectors.toList());
		dataObject.setPerformanceIndicators(performanceIndicators);
		return dataObject;
	}

	private TimelineQueryResponsePI toDataObject(ResponsePIModel model) {
		if (model == null) {
			return null;
		}
		TimelineQueryResponsePI dataObject = new TimelineQueryResponsePI();
		dataObject.setName(model.getName());
		dataObject.setDescription(model.getDescription());
		dataObject.setLevels(model.getLevels() == null ? null : model.getLevels().stream().map(this::toDataObject).collect(Collectors.toList()));
		return dataObject;
	}

	private TimelineQueryResponseLevel toDataObject(ResponseLevelModel model) {
		if (model == null) {
			return null;
		}
		TimelineQueryResponseLevel dataObject = new TimelineQueryResponseLevel();
		dataObject.setDate(model.getDate());
		dataObject.setScore(model.getScore());
		dataObject.setFormId(model.getFormId());
		dataObject.setSubmissionId(model.getSubmissionId());
		dataObject.setSentiment(model.getSentiment());
		return dataObject;
	}

	private ResponseRoleModel fromDataObject(TimelineQueryResponseRole dataObject) {
		ResponseRoleModel model = new ResponseRoleModel();
		model.setName(dataObject.getName());
		List<ResponseEpaModel> EPAs = dataObject.getEPAs() == null ? null : dataObject.getEPAs().stream().map(this::fromDataObject).collect(Collectors.toList());
		model.setEPAs(EPAs);
		return model;
	}

	private ResponseEpaModel fromDataObject(TimelineQueryResponseEpa dataObject) {
		ResponseEpaModel model = new ResponseEpaModel();
		model.setName(dataObject.getName());
		model.setEpassId(dataObject.getEpassId());
		model.setDescription(dataObject.getDescription());
		model.setLevels(dataObject.getLevels() == null ? null : dataObject.getLevels().stream().map(this::fromDataObject).collect(Collectors.toList()));
		List<ResponsePIModel> performanceIndicators =
				dataObject.getPerformanceIndicators() == null ? null : dataObject.getPerformanceIndicators().stream().map(this::fromDataObject).collect(Collectors.toList());
		model.setPerformanceIndicators(performanceIndicators);
		return model;
	}

	private ResponsePIModel fromDataObject(TimelineQueryResponsePI dataObject) {
		if (dataObject == null) {
			return null;
		}
		ResponsePIModel model = new ResponsePIModel();
		model.setName(dataObject.getName());
		model.setDescription(dataObject.getDescription());
		model.setLevels(dataObject.getLevels() == null ? null : dataObject.getLevels().stream().map(this::fromDataObject).collect(Collectors.toList()));
		return model;
	}

	private ResponseLevelModel fromDataObject(TimelineQueryResponseLevel dataObject) {
		if (dataObject == null) {
			return null;
		}
		ResponseLevelModel model = new ResponseLevelModel();
		model.setDate(dataObject.getDate());
		model.setScore(dataObject.getScore());
		model.setFormId(dataObject.getFormId());
		model.setSubmissionId(dataObject.getSubmissionId());
		model.setSentiment(dataObject.getSentiment());
		return model;
	}
}
