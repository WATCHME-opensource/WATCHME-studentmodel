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

package eu.watchme.modules.commons.staticdata.model.form.json;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.commons.enums.LanguageCode;

import java.util.List;
import java.util.Map;

public class FormRelatedData {
	@SerializedName("MAX_Active_EPAs")
	private Integer maxActiveEpas;
	@SerializedName("MAX_Inactive_EPAs")
	private Integer maxInactiveEpas;
	@SerializedName("MAX_Supervisor_Feedback")
	private Integer maxSupervisorFeedback;
	@SerializedName("SupportedForms")
	private List<Integer> formTypes;
	@SerializedName("EPAs")
	private List<EpaMappedField> mEpas;
	@SerializedName("PerformanceIndicators")
	private List<PIMappedField> mPerformanceIndicators;
	@SerializedName("PerformanceIndicatorLevels")
	private List<RankedMappedField> mPerformanceIndicatorLevels;
	@SerializedName("PerformanceIndicatorGoodLevelRank")
	private int mPerformanceIndicatorGoodLevelRank;
	@SerializedName("FeedbackIndicators")
	private List<TypeMappedField> mFeedbackIndicators;
	@SerializedName("SupervisorFeedbackFields")
	private List<SupervisorFeedbackField> supervisorFeedbackFields;
	@SerializedName("VIZStructure")
	private VizStructure mVizStructure;

	public FormRelatedData() {
	}

	public FormRelatedData(List<Integer> formTypes, List<EpaMappedField> epas, List<PIMappedField> performanceIndicators, List<RankedMappedField> performanceIndicatorLevels,
			int performanceIndicatorGoodLevelRank, List<TypeMappedField> feedbackIndicators, List<SupervisorFeedbackField> supervisorFeedbackFields, VizStructure vizStructure, int maxActiveEpas,
			int maxInactiveEpas, int maxSupervisorFeedback) {
		this.formTypes = formTypes;
		mEpas = epas;
		mPerformanceIndicators = performanceIndicators;
		mPerformanceIndicatorLevels = performanceIndicatorLevels;
		mPerformanceIndicatorGoodLevelRank = performanceIndicatorGoodLevelRank;
		mFeedbackIndicators = feedbackIndicators;
		mVizStructure = vizStructure;
		this.supervisorFeedbackFields = supervisorFeedbackFields;
		this.maxActiveEpas = maxActiveEpas;
		this.maxInactiveEpas = maxInactiveEpas;
		this.maxSupervisorFeedback = maxSupervisorFeedback;
	}

	public Integer getMaxActiveEpas() {
		return maxActiveEpas;
	}

	public Integer getMaxInactiveEpas() {
		return maxInactiveEpas;
	}

	public Integer getMaxSupervisorFeedback() {
		return maxSupervisorFeedback;
	}

	public List<Integer> getFormTypes() {
		return formTypes;
	}

	public List<EpaMappedField> getEpas() {
		return mEpas;
	}

	public List<PIMappedField> getPerformanceIndicators() {
		return mPerformanceIndicators;
	}

	public List<RankedMappedField> getPerformanceIndicatorLevels() {
		return mPerformanceIndicatorLevels;
	}

	public int getPerformanceIndicatorGoodLevelRank() {
		return mPerformanceIndicatorGoodLevelRank;
	}

	public List<TypeMappedField> getFeedbackIndicators() {
		return mFeedbackIndicators;
	}

	public List<SupervisorFeedbackField> getSupervisorFeedbackFields() {
		return supervisorFeedbackFields;
	}

	public List<VizRoleField> getRoles() {
		return mVizStructure.getRoles();
	}

	public Map<FeedbackLevel, Map<LanguageCode, String>> getFeedbackMessages() {
		return mVizStructure.getFeedbackMessages();
	}
}
