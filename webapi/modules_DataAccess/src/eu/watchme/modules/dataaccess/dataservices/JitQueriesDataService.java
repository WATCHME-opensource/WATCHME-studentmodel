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

package eu.watchme.modules.dataaccess.dataservices;

import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.dataaccess.adapters.JitQueriesAdapter;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryDataObject;

public class JitQueriesDataService {

	public void storeJitQuery(JitQueryDataObject dataObject) {
		JitQueriesAdapter adapter = new JitQueriesAdapter();
		adapter.insertDataObject(dataObject);
	}

	public JitQueryDataObject getExistingQuery(String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		JitQueriesAdapter adapter = new JitQueriesAdapter();
		return adapter.getExistingQuery(studentId, modelId, epaId, feedbackType, languageCode);
	}

	public int deleteQueries(String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		JitQueriesAdapter adapter = new JitQueriesAdapter();
		return adapter.deleteQueries(studentId, modelId, epaId, feedbackType, languageCode);
	}

	public int deleteAllForStudent(String studentId) {
		JitQueriesAdapter adapter = new JitQueriesAdapter();
		return adapter.deleteAllForStudent(studentId);
	}

	public int updateQuery(JitQueryDataObject queryDataObject, String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		JitQueriesAdapter adapter = new JitQueriesAdapter();
		return adapter.update(queryDataObject, studentId, modelId, epaId, feedbackType, languageCode);
	}
}
