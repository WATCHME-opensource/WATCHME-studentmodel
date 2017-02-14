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
package eu.watchme.modules.dataaccess.model.snapshots;

import eu.watchme.modules.dataaccess.model.DataObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.util.ArrayList;
import java.util.List;

@Entity("studentSnapshots")
public class StudentSnapshotDataObject extends DataObject {

	@Id
	private ObjectId mId;
	@Property(Properties.StudentId)
	private String mStudentId;
	@Property(Properties.ModelId)
	private String mModelId;
	@Embedded(Properties.TimelineSnapshots)
	private List<TimelineSnapshotDataObject> mTimelineSnapshots;
	@Embedded(Properties.CurrentSnapshot)
	private CurrentSnapshotDataObject mCurrentSnapshot;

	@Override
	public Object getId() {
		return mId;
	}

	public String getStudentId() {
		return mStudentId;
	}

	public void setStudentId(String studentId) {
		mStudentId = studentId;
	}

	public String getModelId() {
		return mModelId;
	}

	public void setModelId(String modelId) {
		mModelId = modelId;
	}

	public List<TimelineSnapshotDataObject> getTimelineSnapshots() {
		if (mTimelineSnapshots == null)
			mTimelineSnapshots = new ArrayList<>(0);
		return mTimelineSnapshots;
	}

	public void setTimelineSnapshots(List<TimelineSnapshotDataObject> timelineSnapshots) {
		mTimelineSnapshots = timelineSnapshots;
	}

	public CurrentSnapshotDataObject getCurrentSnapshot() {
		return mCurrentSnapshot;
	}

	public void setCurrentSnapshot(CurrentSnapshotDataObject currentSnapshot) {
		mCurrentSnapshot = currentSnapshot;
	}

	public static class Properties {
		public static final String StudentId = "studentId";
		public static final String ModelId = "modelId";
		public static final String TimelineSnapshots = "timelineSnapshots";
		public static final String CurrentSnapshot = "currentSnapshot";
	}

}
