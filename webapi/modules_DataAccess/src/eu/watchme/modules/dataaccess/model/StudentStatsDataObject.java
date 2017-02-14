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

package eu.watchme.modules.dataaccess.model;

import eu.watchme.modules.ndp.datatypes.StudentAssessment;
import eu.watchme.modules.ndp.datatypes.StudentRole;
import eu.watchme.modules.ndp.datatypes.StudentScore;
import eu.watchme.modules.ndp.datatypes.StudentSentiment;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.ArrayList;
import java.util.List;

@Entity("studentStats")
public class StudentStatsDataObject extends DataObject {

	@Override
	public ObjectId getId() {
		return id;
	}

	@Id
	private ObjectId id;
	@Property(Properties.StudentId)
	private String mStudentId;
	@Property(Properties.EpaId)
	private String mEpaId;
	@Property(Properties.PiId)
	private String mPiId;
	@Property(Properties.Count)
	private Integer mCount;
	@Embedded(Properties.Scores)
	private List<StudentScore> mScores;
	@Embedded(Properties.Sentiment)
	private List<StudentSentiment> mSentiments;
	@Embedded(Properties.Roles)
	private List<StudentRole> mRoles;
	@Embedded(Properties.Assessments)
	private List<StudentAssessment> mAssessments;
	@Property(Properties.MaxTime)
	private long mMaxTime;
	@Property(Properties.MinTime)
	private long mMinTime;
	@Property(Properties.Sorted)
	private Boolean sorted;
	public StudentStatsDataObject() {
	}

	public StudentStatsDataObject(String studentId, String EpaId, String PiId) {
		this.id = new ObjectId();
		this.mStudentId = studentId;
		this.mEpaId = EpaId;
		this.mPiId = PiId;
		this.mCount = 0;
		this.mScores=new ArrayList<>();
		this.mAssessments=new ArrayList<>();
		this.mRoles=new ArrayList<>();
		this.mSentiments=new ArrayList<>();
		this.sorted = false;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public String getEpaId() {
		return mEpaId;
	}

	public void setEpaId(String mEpaId) {
		this.mEpaId = mEpaId;
	}

	public String getPiId() {
		return mPiId;
	}

	public void setPiId(String mPiId) {
		this.mPiId = mPiId;
	}

	public Integer getCount() {
		return mCount;
	}
	public long getMaxTime() { return mMaxTime; }
	public long getMinTime() { return mMinTime; }
	public void setScores(List<StudentScore> elems) {
		this.mScores = elems;
	}
	public void addScore(StudentScore elem) {
		this.mScores.add(elem);
	}
	public void addRole(StudentRole elem){
		this.mRoles.add(elem);
	}
	public void addAssessment(StudentAssessment elem){
		this.mAssessments.add(elem);
	}
	public void addSentiment(StudentSentiment elem){
		this.mSentiments.add(elem);
	}

	private void sortScores(){
		int n = this.mScores.size();
		StudentScore temp;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {

				if (this.mScores.get(j - 1).getTime() > this.mScores.get(j).getTime()) {
					//swap the elements!
					temp = this.mScores.get(j - 1);

					this.mScores.set(j - 1, this.mScores.get(j));
					this.mScores.set(j, temp);

				}

			}
		}
		if(!this.mScores.isEmpty()){
			int lastElm = (this.mScores.size()<2) ? 0 : this.mScores.size()-1;

			mMaxTime = this.mScores.get(lastElm).getTime();
			mMinTime = this.mScores.get(0).getTime();
		}
	}
	private void sortRoles(){
		int n = this.mRoles.size();
		StudentRole temp;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {

				if (this.mRoles.get(j - 1).getTime() > this.mRoles.get(j).getTime()) {
					//swap the elements!
					temp = this.mRoles.get(j - 1);

					this.mRoles.set(j - 1, this.mRoles.get(j));
					this.mRoles.set(j, temp);

				}

			}
		}

	}
	private void sortAssessments(){
		int n = this.mAssessments.size();
		StudentAssessment temp;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {

				if (this.mAssessments.get(j - 1).getTime() > this.mAssessments.get(j).getTime()) {
					//swap the elements!
					temp = this.mAssessments.get(j - 1);

					this.mAssessments.set(j - 1, this.mAssessments.get(j));
					this.mAssessments.set(j, temp);

				}

			}
		}

	}
	private void sortSentiments(){
		int n = this.mSentiments.size();
		StudentSentiment temp;
		for (int i = 0; i < n; i++) {
			for (int j = 1; j < (n - i); j++) {

				if (this.mSentiments.get(j - 1).getTime() > this.mSentiments.get(j).getTime()) {
					//swap the elements!
					temp = this.mSentiments.get(j - 1);

					this.mSentiments.set(j - 1, this.mSentiments.get(j));
					this.mSentiments.set(j, temp);

				}

			}
		}

	}
	public void sortLists(){
		if(!this.sorted) {
			sortScores();
			sortRoles();
			sortAssessments();
			sortSentiments();
			this.sorted = true;
		}
	}
	public List<StudentScore> getScores() {
		if(!this.sorted) {
			sortLists();
			this.sorted=true;
		}
		return this.mScores;
	}
	public List<StudentRole> getRoles(){
		if(!this.sorted){
			sortLists();
			this.sorted=true;
		}
		return this.mRoles;
	}
	public List<StudentAssessment> getAssessments(){
		if(!this.sorted){
			sortLists();
			this.sorted = true;
		}
		return this.mAssessments;
	}
	public List<StudentSentiment> getSentiments(){
		if(!this.sorted){
			sortLists();
			this.sorted = true;
		}
		return this.mSentiments;
	}
	public List<StudentSentiment> getSentiments(int lastRec){
		if(!this.sorted){
			sortLists();
			this.sorted = true;
		}
		int s = this.mSentiments.size();
		if(s>lastRec)
			return this.mSentiments.subList(s-lastRec,s);
		return this.mSentiments;
	}
	public void setCount(Integer mCount) {
		this.mCount = mCount;
	}

	public void newData() {
		this.mCount++;
	}


	public static class Properties {
		public static final String StudentId = "studentId";
		public static final String EpaId = "epaId";
		public static final String PiId = "piId";
		public static final String Count = "count";
		public static final String Scores = "scores";
		public static final String Roles = "roles";
		public static final String Assessments = "assessments";
		public static final String MaxTime = "maxTime";
		public static final String MinTime = "minTime";
		public static final String Sorted = "sorted";
		public static final String Sentiment = "sentiments";

	}



}
