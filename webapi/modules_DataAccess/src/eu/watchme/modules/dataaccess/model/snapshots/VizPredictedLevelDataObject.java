/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 10/6/2016
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

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

@Embedded
public class VizPredictedLevelDataObject {

	@Property(Properties.EpaUnbId)
	private String mEpaUnbId;

	@Property(Properties.PerformanceIndicatorUnbId)
	private String mPerformanceIndicatorUnbId;

	@Property(Properties.LevelUnbId)
	private String mLevelUnbId;

	public VizPredictedLevelDataObject() {
	}

	public VizPredictedLevelDataObject(String epaUnbId, String performanceIndicatorUnbId, String levelUnbId) {
		mEpaUnbId = epaUnbId;
		mPerformanceIndicatorUnbId = performanceIndicatorUnbId;
		mLevelUnbId = levelUnbId;
	}

	public String getEpaUnbId() {
		return mEpaUnbId;
	}

	public void setEpaUnbId(String epaUnbId) {
		mEpaUnbId = epaUnbId;
	}

	public String getPerformanceIndicatorUnbId() {
		return mPerformanceIndicatorUnbId;
	}

	public void setPerformanceIndicatorUnbId(String performanceIndicatorUnbId) {
		mPerformanceIndicatorUnbId = performanceIndicatorUnbId;
	}

	public String getLevelUnbId() {
		return mLevelUnbId;
	}

	public void setLevelUnbId(String levelUnbId) {
		mLevelUnbId = levelUnbId;
	}

	public static class Properties {
		public static final String EpaUnbId = "epaUnbId";
		public static final String PerformanceIndicatorUnbId = "piUnbId";
		public static final String LevelUnbId = "levelUnbId";
	}
}
