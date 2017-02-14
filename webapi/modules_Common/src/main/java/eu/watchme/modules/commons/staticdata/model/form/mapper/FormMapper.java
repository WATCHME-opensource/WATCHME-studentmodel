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

package eu.watchme.modules.commons.staticdata.model.form.mapper;

import eu.watchme.modules.commons.data.EpassKey;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackType;
import eu.watchme.modules.commons.staticdata.model.form.json.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FormMapper {
    public static final int DEFAULT_SUPERVISOR_FEEDBACK_LIMIT = 1;
    public static final int DEFAULT_ACTIVE_LIMIT = 3;
    public static final int DEFAULT_INACTIVE_LIMIT = 0;

    private String domainId;

    private Integer maxActiveEpas;
    private Integer maxInactiveEpas;
    private Integer maxSupervisorFeedback;
    private Set<Integer> formTypes;
    private IndexMapper<EpaMappedField> epasMapper;
    private PIFieldMapper<PIMappedField> performanceIndicatorMapper;
    private RankedIndexMapper performanceIndicatorLevelMapper;
    private TypeIndexMapper feedbackIndicatorMapper;
    private PIFieldMapper<SupervisorFeedbackField> supervisorFeedbackFieldIndexMapper;
    private VizStructureMapper mVizStructureMapper;

    private FormMapper() {}

    public static FormMapper buildMapper(String domainId, FormRelatedData formRelatedData) {
        FormMapper result = new FormMapper();

        result.domainId = domainId;

        result.maxActiveEpas = formRelatedData.getMaxActiveEpas();
        result.maxInactiveEpas = formRelatedData.getMaxInactiveEpas();
        result.maxSupervisorFeedback = formRelatedData.getMaxSupervisorFeedback();

        result.formTypes = new HashSet<>(formRelatedData.getFormTypes());

        result.epasMapper = new IndexMapper<>();
        result.epasMapper.buildMapper(formRelatedData.getEpas());

        result.performanceIndicatorMapper = new PIFieldMapper<>();
        result.performanceIndicatorMapper.buildMapper(formRelatedData.getPerformanceIndicators());

        result.performanceIndicatorLevelMapper = new RankedIndexMapper();
        result.performanceIndicatorLevelMapper.buildMapper(formRelatedData.getPerformanceIndicatorLevels());

        result.feedbackIndicatorMapper = new TypeIndexMapper();
        result.feedbackIndicatorMapper.buildMapper(formRelatedData.getFeedbackIndicators());

        result.supervisorFeedbackFieldIndexMapper = new PIFieldMapper<>();
        result.supervisorFeedbackFieldIndexMapper.buildMapper(formRelatedData.getSupervisorFeedbackFields());

		result.mVizStructureMapper = new VizStructureMapper();
		result.mVizStructureMapper.buildMapper(formRelatedData.getRoles(), formRelatedData.getFeedbackMessages());

        return result;
    }

    public String getDomainId() {
        return domainId;
    }

    public Set<Integer> getFormTypes() {
        return formTypes;
    }

    public IndexMapper<EpaMappedField> getEpasMapper() {
        return epasMapper;
    }

    public PIFieldMapper<PIMappedField> getPerformanceIndicatorMapper() {
        return performanceIndicatorMapper;
    }

    public RankedIndexMapper getPerformanceIndicatorLevelMapper() {
        return performanceIndicatorLevelMapper;
    }

    public TypeIndexMapper getFeedbackIndicatorMapper() {
        return feedbackIndicatorMapper;
    }

    public PIFieldMapper<SupervisorFeedbackField> getSupervisorFeedbackFieldIndexMapper() {
        return supervisorFeedbackFieldIndexMapper;
    }

	public VizStructureMapper getVizStructureMapper() {
		return mVizStructureMapper;
	}

	public EpaMappedField getMappedEpaByUnb(String unbId) {
        return getEpasMapper().getMappedFieldFromUnb(unbId);
    }

    public EpaMappedField getMappedEpaByEpass(String epassId) {
        return getEpasMapper().getMappedFieldFromEpass(epassId);
    }

    public TypeMappedField getMappedFeedbackIdByUnb(String unbId) {
        return getFeedbackIndicatorMapper().getMappedFieldFromUnb(unbId);
    }

    public List<PIMappedField> getMappedPerformanceIndicatorByUnb(UnbKey unbId) {
        return getPerformanceIndicatorMapper().getMappedFieldFromUnb(unbId);
    }

    public List<PIMappedField> getMappedPerformanceIndicatorByUnb(String unbEpaId, String unbPiId) {
        return getMappedPerformanceIndicatorByUnb(new UnbKey(unbEpaId, unbPiId));
    }

    public List<PIMappedField> getMappedPerformanceIndicatorByEpass(EpassKey epassId) {
        return getPerformanceIndicatorMapper().getMappedFieldFromEpass(epassId);
    }

    public List<PIMappedField> getMappedPerformanceIndicatorByEpass(Integer epassFormType, String epassId) {
        return getMappedPerformanceIndicatorByEpass(new EpassKey(epassFormType, epassId));
    }

    public RankedMappedField getMappedPerformanceIndicatorLevelByUnb(String unbId) {
        return getPerformanceIndicatorLevelMapper().getMappedFieldFromUnb(unbId);
    }

    public RankedMappedField getMappedPerformanceIndicatorLevelByEpass(String epassId) {
        return getPerformanceIndicatorLevelMapper().getMappedFieldFromEpass(epassId);
    }

    public RankedMappedField getMappedPerformanceIndicatorLevelByRank(Integer rank) {
        return getPerformanceIndicatorLevelMapper().getMappedFieldByType(rank);
    }

    public List<SupervisorFeedbackField> getSupervisorFeedbackField(EpassKey epassId) {
        return getSupervisorFeedbackFieldIndexMapper().getMappedFieldFromEpass(epassId);
    }

    public List<SupervisorFeedbackField> getSupervisorFeedbackField(Integer epassFormType, String epassId) {
        return getSupervisorFeedbackField(new EpassKey(epassFormType, epassId));
    }

    public FeedbackType getFeedbackTypeByUnb(String unbId) {
        TypeMappedField mapping = getFeedbackIndicatorMapper().getMappedFieldFromUnb(unbId);
        if (mapping == null) {
            return FeedbackType.ABSURD;
        } else {
            return FeedbackType.getEnumItem(mapping.getTypeId());
        }
    }

    public Set<UnbKey> getPerformanceIndicators() {
        return getPerformanceIndicatorMapper().getKeysUnb();
    }

    public FeedbackType getFeedbackTypeByEpass(String epassId) {
        TypeMappedField mapping = getFeedbackIndicatorMapper().getMappedFieldFromEpass(epassId);
        if (mapping == null) {
            return FeedbackType.ABSURD;
        } else {
            return FeedbackType.getEnumItem(mapping.getTypeId());
        }
    }

    public Collection<EpaMappedField> getEpaList() {
        return getEpasMapper().getValues();
    }

    public int getMaxActiveEpas() {
        return maxActiveEpas == null ? DEFAULT_ACTIVE_LIMIT : maxActiveEpas;
    }

    public int getMaxInactiveEpas() {
        return maxInactiveEpas == null ? DEFAULT_INACTIVE_LIMIT : maxInactiveEpas;
    }

    public int getMaxCombinedEpas() {
        return getMaxActiveEpas() + getMaxInactiveEpas();
    }

    public Integer getMaxSupervisorFeedback() {
        return maxSupervisorFeedback == null ? DEFAULT_SUPERVISOR_FEEDBACK_LIMIT : maxSupervisorFeedback;
    }

    @Override
    public String toString() {
        return "FormMapper{domainId='" + domainId + "\'}";
    }
}
