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

package eu.watchme.modules.datamerger;

import eu.watchme.modules.dataaccess.dataservices.StudentsDataService;
import eu.watchme.modules.dataaccess.model.SMData;
import eu.watchme.modules.domainmodel.exceptions.BayesianModelException;
import eu.watchme.modules.unbbayes.BayesianStudentModel;

import java.io.IOException;

public class StudentRepository {
	private static StudentRepository sInstance;
	private StudentsDataService mDataService;

	private StudentRepository() {
		mDataService = new StudentsDataService();
	}

	public static StudentRepository getInstance() {
		if (sInstance == null) {
			sInstance = new StudentRepository();
		}
		return sInstance;
	}

	public SMData getSMData(String studentId, String domainId, int pId) {
		return mDataService.getStudentModelData(studentId, domainId, pId);
	}

	public BayesianStudentModel getObject(SMData smData) {
		try {
			return BayesianStudentModel.load(smData.getMebnFilePath(),
					smData.getDomainKnowledgeFilePath(),
					smData.getStudentKnowledge(),
					smData.getModelImageTimestamp());
		} catch (IOException e) {
			throw new BayesianModelException(e);
		}
	}
}
