/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: Ovidiu Serban, University of Reading, UK, http://www.reading.ac.uk/
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

package eu.watchme.modules.dataaccess.adapters;

import eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject;

import java.util.List;

import static eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject.OrderKey.DescendingCreationDate;
import static eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject.OrderKey.DescendingUpdated;
import static eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject.Properties.EpaId;
import static eu.watchme.modules.dataaccess.model.SupervisorFeedbackDataObject.Properties.StudentId;

public class SupervisorFeedbackAdapter extends BaseAdapter<SupervisorFeedbackDataObject> {
    public static final SupervisorFeedbackAdapter supervisorFeedbackAdapter = new SupervisorFeedbackAdapter();

    public SupervisorFeedbackAdapter() {
        super(SupervisorFeedbackDataObject.class);
    }

    public List<SupervisorFeedbackDataObject> getSupervisorFeedback(String studentId) {
        return createQuery().field(StudentId).equal(studentId).asList();
    }

    public List<SupervisorFeedbackDataObject> getSupervisorFeedback(String studentId, String unbEpaId) {
        return createQuery().field(StudentId).equal(studentId).field(EpaId).equal(unbEpaId).asList();
    }

    public SupervisorFeedbackDataObject getLastSupervisorFeedback(String studentId) {
        return createQuery().field(StudentId).equal(studentId).order(DescendingCreationDate+","+ DescendingUpdated).limit(1).get();
    }

    public List<SupervisorFeedbackDataObject> getLastSupervisorFeedback(String studentId, String unbEpaId, int limit) {
        return createQuery().field(StudentId).equal(studentId)
                .field(EpaId).equal(unbEpaId)
                .order(DescendingCreationDate+","+ DescendingUpdated).limit(limit).asList();
    }
}
