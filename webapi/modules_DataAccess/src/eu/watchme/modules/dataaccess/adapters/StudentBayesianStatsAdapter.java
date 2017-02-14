/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 17/6/2016
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

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.dataaccess.model.StudentBayesianStatsDataObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static eu.watchme.modules.dataaccess.model.StudentBayesianStatsDataObject.Properties.*;

public class StudentBayesianStatsAdapter extends BaseAdapter<StudentBayesianStatsDataObject> {
	public StudentBayesianStatsAdapter() {
		super(StudentBayesianStatsDataObject.class);
	}

	private static Long parseEpassDate(String date) {
		LocalDate localDate = LocalDate.parse(date);
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return instant.toEpochMilli();
	}

	public List<StudentBayesianStatsDataObject> getStatsAll(String studentId) {
		return createQuery().field(StudentId).equal(studentId).asList();
	}

	public List<StudentBayesianStatsDataObject> getStatsEpas(String studentId) {
		return createQuery().field(StudentId).equal(studentId).field(PiId).equal("*").asList();
	}

	public List<StudentBayesianStatsDataObject> getStatsEpaPis(String studentId) {
		return createQuery().field(StudentId).equal(studentId).field(PiId).notEqual("*").asList();
	}

	public StudentBayesianStatsDataObject getStats(String studentId, String epaId, String piId) {
		return createQuery().field(StudentId).equal(studentId).field(EpaId).equal(epaId).field(PiId).equal(piId).limit(1).get();
	}

	public double[] getDataPoints(StudentBayesianStatsDataObject data) {

		int size = data.getScores().size();
		double[] res = new double[size];

		for (int i = 0; i < size; i++) {
			res[i] = data.getScores().get(i).getScore();
		}

		return res;
	}

	public int[] getDataTimePoints(StudentBayesianStatsDataObject data) {

		int size = data.getScores().size();
		int[] res = new int[size];
		for (int i = 0; i < size; i++) {
			//res[i]= (int)data.getScores().get(i).getTime();
			res[i] = i;
		}

		return res;
	}

	public int[] getRealDataTimePoints(StudentBayesianStatsDataObject data) {

		int size = data.getScores().size();
		int[] res = new int[size];
		for (int i = 0; i < size; i++) {
			res[i] = Math.round((data.getScores().get(i).getTime()) / (ApplicationSettings.getTimeScalingFactor() * 24 * 60 * 60 * 1000));
			//res[i]= i;
		}

		return res;
	}

	public int getNearestElement(String date, StudentBayesianStatsDataObject data) {
		long newDate = parseEpassDate(date);
		int size = data.getScores().size();

		long minVal = 1000000000;
		int minIndx = 0;
		for (int i = 0; i < size; i++) {
			long diff = Math.abs(data.getScores().get(i).getTime() - newDate);
			if (diff <= minVal) {
				minVal = diff;
				minIndx = i;
			}
		}
		return minIndx;
	}

	public int convertIndxFromRes(int indx, StudentBayesianStatsDataObject data, int start) {
		int min = Math.round((data.getMinTime()) / (ApplicationSettings.getTimeScalingFactor() * 24 * 60 * 60 * 1000));
		int selected = start;
		int count = 0;
		for (int i = start; i < data.getScores().size(); i++) {
			int mn = (int) Math.round(((double) data.getScores().get(i).getTime()) / ((double) ApplicationSettings.getTimeScalingFactor() * 24 * 60 * 60 * 1000));
			if (mn >= (min + indx))
				return i;

		}
		return selected;
	}

	public int convertIdToRes(int indx, int pSize, int resSize) {
		int offset = pSize - resSize;
		int newVal = indx - offset;
		if (newVal < 0)
			newVal = 0;
		else if (newVal > resSize - 1)
			newVal = resSize - 1;

		return newVal;
	}

	public long getTimePoint(int i, StudentBayesianStatsDataObject data) {
		return data.getScores().get(i).getTime();
	}

	public int deleteAllForStudent(String studentId) {
		Datastore datastore = getDataStore();
		Query query = datastore.createQuery(StudentBayesianStatsDataObject.class).field(StudentBayesianStatsDataObject.Properties.StudentId).equal(studentId);
		return datastore.delete(query).getN();
	}

}