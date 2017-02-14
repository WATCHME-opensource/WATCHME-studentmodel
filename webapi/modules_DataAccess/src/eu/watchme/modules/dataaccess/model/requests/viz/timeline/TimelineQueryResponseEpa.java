/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 27/11/2015
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
package eu.watchme.modules.dataaccess.model.requests.viz.timeline;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.List;

@Embedded
public class TimelineQueryResponseEpa {

	@Property(Properties.Name)
	private String mName;

	@Property(Properties.EpassId)
	private String mEpassId;

	@Property(Properties.Description)
	private String mDescription;

	@Embedded(Properties.Levels)
	private List<TimelineQueryResponseLevel> mLevels;

	@Embedded(Properties.PerformanceIndicators)
	private List<TimelineQueryResponsePI> mPerformanceIndicators;

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getEpassId() {
		return mEpassId;
	}

	public void setEpassId(String epassId) {
		mEpassId = epassId;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public List<TimelineQueryResponseLevel> getLevels() {
		return mLevels;
	}

	public void setLevels(List<TimelineQueryResponseLevel> levels) {
		mLevels = levels;
	}

	public List<TimelineQueryResponsePI> getPerformanceIndicators() {
		return mPerformanceIndicators;
	}

	public void setPerformanceIndicators(List<TimelineQueryResponsePI> performanceIndicators) {
		mPerformanceIndicators = performanceIndicators;
	}

	public static class Properties {

		public static final String Name = "name";
		public static final String EpassId = "epassId";
		public static final String Description = "description";
		public static final String Levels = "levels";
		public static final String PerformanceIndicators = "performanceIndicators";
	}
}
