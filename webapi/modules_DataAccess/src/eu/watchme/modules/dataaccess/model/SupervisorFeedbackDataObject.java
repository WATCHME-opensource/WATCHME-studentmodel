/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: Ovidiu Serban, University of Reading, UK, http://www.reading.ac.uk/
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

package eu.watchme.modules.dataaccess.model;

import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.nlp.datatypes.NLPFindingSet;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity("supervisorFeedback")
public class SupervisorFeedbackDataObject extends DataObject {
	@Id
	private ObjectId id;
	@Property(Properties.StudentId)
	private String studentId;
	@Property(Properties.EpaId)
	private String unbEpaId;
	@Property(Properties.PerformanceIndicators)
	private Set<String> performanceIndicators;
	@Embedded(Properties.FindingList)
	private List<NLPFindingSet> findList;
	@Property(Properties.FeedbackLevel)
	private FeedbackLevel feedbackLevel;
	@Property(Properties.SupervisorFeedback)
	private String supervisorFeedback;
	@Property(Properties.CreationDate)
	private Instant creationDate;
	@Property(Properties.FormId)
	private Integer mFormId;
	@Property(Properties.SubmissionId)
	private Integer mSubmissionId;

	public SupervisorFeedbackDataObject() {
	}

	public SupervisorFeedbackDataObject(String studentId, String unbEpaId, Set<String> performanceIndicators,
			FeedbackLevel feedbackLevel, String supervisorFeedback, Instant creationDate, List<NLPFindingSet> findList, Integer formId, Integer submissionId) {
		this();
		this.studentId = studentId;
		this.unbEpaId = unbEpaId;
		this.performanceIndicators = Collections.unmodifiableSet(performanceIndicators);
		this.findList = findList;
		this.feedbackLevel = feedbackLevel;
		this.supervisorFeedback = supervisorFeedback;
		this.creationDate = creationDate;
		this.findList = findList;
		this.mFormId = formId;
		this.mSubmissionId = submissionId;
	}

	@Override
	public Object getId() {
		return id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getUnbEpaId() {
		return unbEpaId;
	}

	public void setUnbEpaId(String unbEpaId) {
		this.unbEpaId = unbEpaId;
	}

	public Set<String> getPerformanceIndicators() {
		return performanceIndicators;
	}

	public void setFindingList(List<NLPFindingSet> flist) {
		this.findList = flist;
	}

	public List<NLPFindingSet> getFindingList() {
		return findList;
	}

	public void setPerformanceIndicators(Set<String> performanceIndicators) {
		this.performanceIndicators = performanceIndicators;
	}

	public FeedbackLevel getFeedbackLevel() {
		return feedbackLevel;
	}

	public void setFeedbackLevel(FeedbackLevel feedbackLevel) {
		this.feedbackLevel = feedbackLevel;
	}

	public String getSupervisorFeedback() {
		return supervisorFeedback;
	}

	public void setSupervisorFeedback(String supervisorFeedback) {
		this.supervisorFeedback = supervisorFeedback;
	}

	public Instant getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Instant creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getFormId() {
		return mFormId;
	}

	public void setFormId(Integer formId) {
		mFormId = formId;
	}

	public Integer getSubmissionId() {
		return mSubmissionId;
	}

	public void setSubmissionId(Integer submissionId) {
		mSubmissionId = submissionId;
	}

	@Override
	public String toString() {
		return "SupervisorFeedbackDataObject{" +
				"id=" + id +
				", studentId='" + studentId + '\'' +
				", unbEpaId='" + unbEpaId + '\'' +
				", performanceIndicators=" + performanceIndicators +
				", findList=" + findList +
				", feedbackLevel=" + feedbackLevel +
				", supervisorFeedback='" + supervisorFeedback + '\'' +
				", creationDate=" + creationDate +
				", mFormId=" + mFormId +
				", mSubmissionId=" + mSubmissionId +
				'}';
	}

	public static class Properties {
		public static final String StudentId = "studentId";
		public static final String EpaId = "unbEpaId";
		public static final String PerformanceIndicators = "performanceIndicators";
		public static final String FindingList = "NLPFindingList";
		public static final String FeedbackLevel = "feedbackLevel";
		public static final String SupervisorFeedback = "supervisorFeedback";
		public static final String CreationDate = "creationDate";
		public static final String Updated = "updated";
		public static final String FormId = "formId";
		public static final String SubmissionId = "submissionId";
	}

	public static class OrderKey {
		public static final String AscendingCreationDate = Properties.CreationDate;
		public static final String DescendingCreationDate = "-" + Properties.CreationDate;
		public static final String AscendingUpdated = Properties.Updated;
		public static final String DescendingUpdated = "-" + Properties.Updated;
	}
}
