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

package eu.watchme.modules.commons.staticdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackMessages;
import eu.watchme.modules.commons.staticdata.model.form.json.*;
import eu.watchme.modules.commons.staticdata.model.portfolio.PortfolioMessages;
import eu.watchme.modules.commons.staticdata.model.recommendation.json.Recommendations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class StaticDataLoader {
	private static final Charset encoding = Charset.forName("UTF-8");
	private static Logger logger = LoggerFactory.getLogger(StaticDataLoader.class);

	private static Gson sGson;

	static {
		sGson = new GsonBuilder().setPrettyPrinting().create();
	}

	protected static FormRelatedData loadFormRelatedData(String domain) throws Exception {
		try {
			File filePath = ApplicationSettings.getStaticDataFormsPath(domain);
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filePath), encoding));
			FormRelatedData data = sGson.fromJson(reader, FormRelatedData.class);
			validate(data, domain);
			return data;
		} catch (Exception e) {
			logger.error("Thread-{}", Thread.currentThread().getId(), e);
			throw e;
		}
	}

	protected static FeedbackMessages loadFeedbackMessages(String domain) throws FileNotFoundException {
		try {
			File filePath = ApplicationSettings.getStaticDataFeedbackMessagesPath(domain);
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filePath), encoding));
			return sGson.fromJson(reader, FeedbackMessages.class);
		} catch (Exception e) {
			logger.error("Thread-{}", Thread.currentThread().getId(), e);
			throw e;
		}
	}

	protected static Recommendations loadRecommendations(String domain) throws FileNotFoundException {
		try {
			File filePath = ApplicationSettings.getStaticDataRecommendationsPath(domain);
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filePath), encoding));
			return sGson.fromJson(reader, Recommendations.class);
		} catch (Exception e) {
			logger.error("Thread-{}", Thread.currentThread().getId(), e);
			throw e;
		}
	}

	private static void validate(FormRelatedData data, String domain) throws ValidationException {
		validateTranslations(data, domain);
	}

	private static void validateTranslations(FormRelatedData data, String domain) throws ValidationException {
		LanguageCode languageCode = ApplicationSettings.DEFAULT_LANGUAGE;
		if (data.getEpas() != null) {
			for (EpaMappedField item : data.getEpas()) {
				if (!item.getTranslationMap().containsKey(languageCode)) {
					throw new ValidationException("Missing 'ReadableName' translation for default language - EPA " + item.getUnbId() + ", Domain " + domain);
				}
				if (item.getShortNameTranslation(languageCode) == null) {
					throw new ValidationException("Missing 'ShortName' translation for default language - EPA " + item.getUnbId() + ", Domain " + domain);
				}
			}
		}
		if (data.getPerformanceIndicators() != null) {
			for (PIMappedField item : data.getPerformanceIndicators()) {
				if (!item.getTranslationMap().containsKey(languageCode)) {
					throw new ValidationException(
							"Missing 'ReadableName' translation for default language - PI " + item.getUnbKey().getEpaId() + ", EPA " + item.getUnbKey().getUnbId() + ", Domain " + domain);
				}
				if (item.getShortNameTranslation(languageCode) == null) {
					throw new ValidationException(
							"Missing 'ShortName' translation for default language - PI " + item.getUnbKey().getEpaId() + ", EPA " + item.getUnbKey().getUnbId() + ", Domain " + domain);
				}
			}
		}
		if (data.getPerformanceIndicatorLevels() != null) {
			for (RankedMappedField item : data.getPerformanceIndicatorLevels()) {
				if (!item.getTranslationMap().containsKey(languageCode)) {
					throw new ValidationException("Missing 'ReadableName' translation for default language - PI Level " + item.getUnbId() + ", Domain " + domain);
				}
			}
		}
		if (data.getFeedbackIndicators() != null) {
			for (TypeMappedField item : data.getFeedbackIndicators()) {
				if (!item.getTranslationMap().containsKey(languageCode)) {
					throw new ValidationException("Missing 'ReadableName' translation for default language - PI Level " + item.getUnbId() + ", Domain " + domain);
				}
			}
		}
		if (data.getRoles() != null) {
			for (int i = 0; i < data.getRoles().size(); i++) {
				VizRoleField item = data.getRoles().get(i);
				if (!item.getTranslationMap().containsKey(languageCode)) {
					throw new ValidationException("Missing 'ReadableName' translation for default language - VIZ Role at index " + i + ", Domain " + domain);
				}
			}
		}
	}

	public static PortfolioMessages loadPortfolioMessages(String domainId) throws FileNotFoundException {
		try {
			File filePath = ApplicationSettings.getStaticDataPortfolioMessagesPath(domainId);
			if (!filePath.exists()) {
				logger.info("Thread-{}: WARNING: PortfolioMessages.json does not exist for {}", Thread.currentThread().getId(), domainId);
				return null;
			}
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filePath), encoding));
			return sGson.fromJson(reader, PortfolioMessages.class);
		} catch (Exception e) {
			logger.error("Thread-{}", Thread.currentThread().getId(), e);
			throw e;
		}
	}

	public static PortfolioMessages loadSupervisorPortfolioMessages(String domainId) throws FileNotFoundException {
		try {
			File filePath = ApplicationSettings.getStaticDataSupervisorPortfolioMessagesPath(domainId);
			if (!filePath.exists()) {
				logger.info("Thread-{}: WARNING: PortfolioMessages.json does not exist for {}", Thread.currentThread().getId(), domainId);
				return null;
			}
			JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filePath), encoding));
			return sGson.fromJson(reader, PortfolioMessages.class);
		} catch (Exception e) {
			logger.error("Thread-{}", Thread.currentThread().getId(), e);
			throw e;
		}
	}
}
