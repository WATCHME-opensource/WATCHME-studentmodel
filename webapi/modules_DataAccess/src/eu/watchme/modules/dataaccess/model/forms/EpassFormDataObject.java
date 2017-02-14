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

package eu.watchme.modules.dataaccess.model.forms;

import eu.watchme.modules.dataaccess.model.DataObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity("epassForms")
public class  EpassFormDataObject extends DataObject {
	@Id
	private ObjectId mId;
	@Property(Properties.FormId)
	private Integer mFormId;
	@Property(Properties.FormType)
	private Integer mFormType;
	@Property(Properties.ModelId)
	private String mModelId;
	@Property(Properties.FormVersion)
	private String mFormVersion;
	@Property(Properties.Category)
	private String mCategory;
	@Embedded(Properties.Questions)
	private QuestionDataObject[] mQuestions;

	public EpassFormDataObject() {
	}

	public EpassFormDataObject(Integer formId, Integer formType, String formVersion, String modelId, String category, QuestionDataObject[] questions) {
		super();
		mFormId = formId;
		mFormType = formType;
		mFormVersion = formVersion;
		mModelId = modelId;
		mCategory = category;
		mQuestions = questions;
	}

	public ObjectId getId() {
		return mId;
	}

	public void setId(ObjectId id) {
		mId = id;
	}

	public Integer getFormId() {
		return mFormId;
	}

	public void setFormId(Integer formId) {
		mFormId = formId;
	}

	public Integer getFormType() {
		return mFormType;
	}

	public void setFormType(Integer formType) {
		this.mFormType = formType;
	}

	public String getFormVersion() {
		return mFormVersion;
	}

	public void setFormVersion(String formVersion) {
		mFormVersion = formVersion;
	}

	public String getModelId() {
		return mModelId;
	}

	public void setModelId(String modelId) {
		mModelId = modelId;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

	public QuestionDataObject[] getQuestions() {
		return mQuestions;
	}

	public void setQuestions(QuestionDataObject[] questions) {
		mQuestions = questions;
	}

	public static class Properties {
		public static final String FormId = "formId";
		public static final String FormType = "formType";
		public static final String FormVersion = "formVersion";
		public static final String ModelId = "modelId";
		public static final String Category = "category";
		public static final String Questions = "questions";
	}

}
