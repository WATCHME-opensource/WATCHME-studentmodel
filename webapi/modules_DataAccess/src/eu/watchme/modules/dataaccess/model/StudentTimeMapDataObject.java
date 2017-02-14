package eu.watchme.modules.dataaccess.model;/*
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


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity("studentTimeMap")
public class StudentTimeMapDataObject extends DataObject {

    @Override
    public ObjectId getId() {
        return id;
    }

    @Id
    private ObjectId id;
    @Property(Properties.StudentId)
    private String mStudentId;

    @Property(Properties.UnbId)
    private String unbId;
    @Property(Properties.dayNumber)
    private Integer dayId;
    @Property(Properties.profileId)
    private Integer profileId;
    @Property(Properties.time)
    private String submissionDate;

    public StudentTimeMapDataObject() {
    }

    public StudentTimeMapDataObject(String studentId, String unbId, int dayNumber,int profileId,String time) {
        this.id = new ObjectId();
        this.mStudentId = studentId;
        this.unbId = unbId;
        this.dayId = dayNumber;
        this.profileId = profileId;
        this.submissionDate =time;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public String getUnbId() {
        return unbId;
    }

    public void setUnbId(String unbId) {
        this.unbId = unbId;
    }

    public Integer getDayId() {
        return dayId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }
    public Integer getProfileId() {
        return profileId;
    }

    public void setDayId(Integer dayId) {
        this.dayId = dayId;
    }
    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public static class Properties {
        public static final String StudentId = "studentId";
        public static final String UnbId = "unbId";
        public static final String dayNumber = "dayId";
        public static final String profileId = "profileId";
        public static final String time = "submissionDate";
    }

    @Override
    public String toString() {
        return "StudenTimeMapDataObject{" +
                "id=" + id +
                ", mStudentId='" + mStudentId + '\'' +
                ", unbId='" + unbId + '\'' +
                ", dayId=" + dayId +
                ",profileId=" +profileId +
                ", submissionDate='" + submissionDate + '\'' +
                '}';
    }
}
