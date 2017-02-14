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

package eu.watchme.modules.dataaccess.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.time.Instant;

@Entity("students")
public class StudentDataObject extends DataObject {
	@Id
	private ObjectId mId;
	@Property(Properties.StudentId)
	private String mStudentId;
	@Property(Properties.StudentKnowledge)
	private String mStudentKnowledge;



	@Property(Properties.ProfileId)
	private Integer mProfileId;
	@Property(Properties.ModelImageTimestamp)
	private Instant mModelImageTimestamp;

	public StudentDataObject() {
	}

	public StudentDataObject(String studentId) {
		this.mStudentId = studentId;
		this.mProfileId = 1;
	}
	public StudentDataObject(String studentId,Integer pId ) {
		this.mStudentId = studentId;
		this.mProfileId = pId;
	}
	@Override
	public Object getId() {
		return mId;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public String getStudentKnowledge() {
		return mStudentKnowledge;
	}

	public void setStudentKnowledge(String studentKnowledge) {
		mStudentKnowledge = studentKnowledge;
	}

	public int getProfileId() {
		return mProfileId;
	}

	public void setProfileId(int mProfileId) {
		this.mProfileId = mProfileId;
	}

	public Instant getModelImageTimestamp() {
		return mModelImageTimestamp;
	}

	public void setModelImageTimestamp(Instant modelImageTimestamp) {
		mModelImageTimestamp = modelImageTimestamp;
	}

	public static class Properties {
		public static final String StudentId = "studentId";
		public static final String StudentKnowledge = "studentKnowledge";
		public static final String ProfileId = "profileId";
		public static final String ModelImageTimestamp = "modelImageTimestamp";
	}

}
