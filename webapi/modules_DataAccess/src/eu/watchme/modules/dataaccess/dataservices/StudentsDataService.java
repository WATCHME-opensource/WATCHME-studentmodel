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

import eu.watchme.modules.dataaccess.adapters.StudentsAdapter;
import eu.watchme.modules.dataaccess.model.SMData;
import eu.watchme.modules.dataaccess.model.StudentDataObject;
import eu.watchme.modules.dataaccess.providers.FilePathProviderFactory;

import java.io.File;
import java.time.Instant;

public class StudentsDataService {

	public StudentsDataService() {
	}

	public SMData getStudentModelData(String studentId, String domainId, int pId) {
		StudentDataObject studentDataObject = getStudentDataObjectInternal(studentId, pId);

		File mebnFilePath = FilePathProviderFactory.newMebnPathProvider().providePath(domainId);
		String studentKnowledge = studentDataObject.getStudentKnowledge();
		Instant modelImageTimestamp = studentDataObject.getModelImageTimestamp();
		File domainFilePath = FilePathProviderFactory.newDomainKnowledgePathProvider().providePath(domainId);
		return new SMData(mebnFilePath, studentKnowledge, modelImageTimestamp, domainFilePath, pId);
	}

	protected StudentDataObject getStudentDataObjectInternal(String studentId, int pId) {
		StudentsAdapter studentsAdapter = new StudentsAdapter();
		StudentDataObject studentDataObject = studentsAdapter.getDataObject(studentId, pId);
		if (studentDataObject == null) {
			studentDataObject = new StudentDataObject(studentId, pId);
			studentsAdapter.insertDataObject(studentDataObject);
		}
		return studentDataObject;
	}

}
