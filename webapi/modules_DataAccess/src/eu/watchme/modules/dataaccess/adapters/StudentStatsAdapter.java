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

package eu.watchme.modules.dataaccess.adapters;

import eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;

import static eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject.Properties.ModelId;
import static eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject.Properties.StudentId;

public class StudentStatsAdapter extends BaseAdapter<StudentStatsDataObject> {
	public static final StudentStatsAdapter studentStatsAdapter = new StudentStatsAdapter();

	public StudentStatsAdapter() {
		super(StudentStatsDataObject.class);
	}

	public StudentStatsDataObject getStats(String studentId, String modelId) {
		return createQuery().field(StudentId).equal(studentId).field(ModelId).equal(modelId).limit(1).get();
	}

	public int deleteAllForStudent(String studentId) {
		Datastore datastore = getDataStore();
		Query query = datastore.createQuery(StudentStatsDataObject.class).field(StudentStatsDataObject.Properties.StudentId).equal(studentId);
		return datastore.delete(query).getN();
	}

	public String getCohort(String modelId, String studentId, String epaUnbId) {
		StudentStatsDataObject statsDataObject = getStats(studentId, modelId);
		if (statsDataObject == null) {
			return null;
		}
		return statsDataObject.getCohort(epaUnbId);
	}

	public List<StudentStatsDataObject> getStatsExcept(String modelId, String exceptStudentId) {
		return createQuery().field(ModelId).equal(modelId).field(StudentId).notEqual(exceptStudentId).asList();
	}
}