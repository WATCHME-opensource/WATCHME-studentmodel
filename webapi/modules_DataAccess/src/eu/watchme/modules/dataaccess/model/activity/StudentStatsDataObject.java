/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 15/02/2016
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

package eu.watchme.modules.dataaccess.model.activity;

import eu.watchme.modules.dataaccess.model.DataObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity("studentStats")
public class StudentStatsDataObject extends DataObject {

	@Override
	public ObjectId getId() {
		return id;
	}

	@Id
	private ObjectId id;

	@Property(Properties.ModelId)
	private String mModelId;

	@Property(Properties.StudentId)
	private String mStudentId;

	@Embedded(Properties.EpaStats)
	private List<StudentEpaStatsDataObject> mEpaStats;

	public StudentStatsDataObject() {
	}

	public StudentStatsDataObject(String studentId, String modelId) {
		mStudentId = studentId;
		mModelId = modelId;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public String getModelId() {
		return mModelId;
	}

	@Override
	public String toString() {
		return "StudentStatsDataObject{" +
				"id=" + id +
				", mModelId='" + mModelId + '\'' +
				", mStudentId='" + mStudentId + '\'' +
				", mEpaStats=" + mEpaStats +
				'}';
	}

	public void addNewEpaSubmission(String epaUnbId, Integer submissionId, Integer formId, String cohort, String submissionReferenceDate, Instant dateReceived, Collection<String> piUnbIds) {
		StudentEpaStatsDataObject epaStatsDataObject = findEpaStats(epaUnbId);
		epaStatsDataObject.newSubmission(submissionId, formId, cohort, submissionReferenceDate, dateReceived, piUnbIds);
	}

	public StudentEpaStatsDataObject findEpaStats(String epaUnbId) {
		StudentEpaStatsDataObject result = null;
		if (mEpaStats == null) {
			mEpaStats = new ArrayList<>(1);
		} else {
			for (StudentEpaStatsDataObject epaStats : mEpaStats) {
				if (epaStats.getEpaUnbId().equals(epaUnbId)) {
					result = epaStats;
					break;
				}
			}
		}
		if (result == null) {
			result = new StudentEpaStatsDataObject();
			result.setEpaUnbId(epaUnbId);
			mEpaStats.add(result);
		}
		return result;
	}

	public int getSubmissionsCount(String epaUnbId) {
		if (mEpaStats == null)
			return 0;
		Optional<StudentEpaStatsDataObject> first = mEpaStats.stream().filter(epaStats -> epaStats.getEpaUnbId().equals(epaUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getSubmissionsCount();
		}
		return 0;
	}

	public int getSubmissionsCount(String epaUnbId, String piUnbId) {
		if (mEpaStats == null)
			return 0;
		Optional<StudentEpaStatsDataObject> first = mEpaStats.stream().filter(epaStats -> epaStats.getEpaUnbId().equals(epaUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getPiSubmissionsCount(piUnbId);
		}
		return 0;
	}

	public String getCohort(String epaUnbId) {
		if (mEpaStats == null)
			return null;
		Optional<StudentEpaStatsDataObject> first = mEpaStats.stream().filter(epaStats -> epaStats.getEpaUnbId().equals(epaUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getCohort();
		}
		return null;
	}

	public boolean hasEpaWithCohort(String epaUnbId, String cohort) {
		return mEpaStats != null && mEpaStats.stream().filter(epaStats -> epaStats.getEpaUnbId().equals(epaUnbId) && epaStats.getCohort().equals(cohort)).findFirst().isPresent();
	}

	public Instant getLastUpdatedInstant(String epaUnbId) {
		if (mEpaStats == null)
			return null;
		Optional<StudentEpaStatsDataObject> first = mEpaStats.stream().filter(epaStats -> epaStats.getEpaUnbId().equals(epaUnbId)).findFirst();
		if (first.isPresent()) {
			return first.get().getLastSubmissionDate();
		}
		return null;
	}

	public static class Properties {
		public static final String ModelId = "modelId";
		public static final String StudentId = "studentId";
		public static final String EpaStats = "epaStats";
	}

}
