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

package eu.watchme.modules.domainmodel.jit;

import com.google.gson.annotations.SerializedName;
import eu.watchme.modules.domainmodel.serialization.UtcDateTimeDeserializer;
import eu.watchme.modules.domainmodel.serialization.UtcDateTimeSerializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.time.Instant;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FeedbackResponseModel<T> {
	@JsonProperty("epas")
	@SerializedName("epas")
	private List<FeedbackEpaModel> mFeedbackForEpas;

	@JsonProperty("knowledge-fragments")
	@SerializedName("knowledge-fragments")
	private PortfolioModel mPortfolio;

	@JsonProperty("updatedTimestamp")
	@SerializedName("updatedTimestamp")
	@JsonSerialize(using = UtcDateTimeSerializer.class)
	@JsonDeserialize(using = UtcDateTimeDeserializer.class)
	private Instant mUpdatedInstant;

	public FeedbackResponseModel() {
		mFeedbackForEpas = null;
	}

	public FeedbackResponseModel(List<FeedbackEpaModel> feedbackForEpas, PortfolioModel portfolio, Instant updatedInstant) {
		mFeedbackForEpas = feedbackForEpas;
		mPortfolio = portfolio;
		mUpdatedInstant = updatedInstant;
	}

	@JsonIgnore
	public List<FeedbackEpaModel> getFeedbackForEpas() {
		return mFeedbackForEpas;
	}

	public void setFeedbackForEpas(List<FeedbackEpaModel> feedbackForEpas) {
		this.mFeedbackForEpas = feedbackForEpas;
	}

	@JsonIgnore
	public PortfolioModel getPortfolio() {
		return mPortfolio;
	}

	public void setPortfolio(PortfolioModel portfolio) {
		mPortfolio = portfolio;
	}

	@JsonIgnore
	public Instant getUpdatedInstant() {
		return mUpdatedInstant;
	}

	public void setUpdatedInstant(Instant updatedInstant) {
		mUpdatedInstant = updatedInstant;
	}
}
