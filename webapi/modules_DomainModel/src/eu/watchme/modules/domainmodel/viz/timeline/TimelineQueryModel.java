/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 26/11/2015
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
package eu.watchme.modules.domainmodel.viz.timeline;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.domainmodel.viz.AuthorizationModel;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimelineQueryModel {

	@JsonProperty("authorisationData")
	@SerializedName("authorisationData")
	private AuthorizationModel mAuthorisationData;

	@JsonProperty("modelId")
	@SerializedName("modelId")
	private String mModelId;

	@JsonProperty("languageCode")
	@SerializedName("languageCode")
	private LanguageCode mLanguageCode;

	@JsonProperty("epaId")
	@SerializedName("epaId")
	private String mEpaId;

	private Set<String> mEpaIds;

	public AuthorizationModel getAuthorisationData() {
		return mAuthorisationData;
	}

	public void setAuthorisationData(AuthorizationModel authorisationData) {
		mAuthorisationData = authorisationData;
	}

	public String getModelId() {
		return mModelId;
	}

	public void setModelId(String modelId) {
		mModelId = modelId;
	}

	public LanguageCode getLanguageCode() {
		return mLanguageCode;
	}

	public void setLanguageCode(LanguageCode languageCode) {
		mLanguageCode = languageCode;
	}

	public Set<String> getEpaIdsForFiltering() {
		if (mEpaIds == null) {
			if (mEpaId == null || mEpaId.trim().isEmpty() || mEpaId.trim().equals("*")) {
				mEpaIds = Collections.emptySet();
			} else {
				mEpaIds = createComaSeparatedSet(mEpaId);
			}
		}
		return mEpaIds;
	}

	public String getEpaId() {
		return mEpaId;
	}

	public void setEpaId(String epaId) {
		this.mEpaIds = null;
		this.mEpaId = epaId;
	}

	private static Set<String> createComaSeparatedSet(String setProperty) {
		return Arrays.asList(setProperty.split("\\s*,\\s*")).stream().map(String::trim).filter(s -> s.length() > 0).map(String::toLowerCase).collect(toSet());
	}
}
