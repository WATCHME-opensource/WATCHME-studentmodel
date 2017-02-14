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

package eu.watchme.modules.commons;

import eu.watchme.modules.commons.enums.LanguageCode;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class ApplicationSettings {
	public static final LanguageCode DEFAULT_LANGUAGE = LanguageCode.EN;
	private static String serverVersion;

	private static String mEpassUrlLogin;
	private static String mEpassUrlPrivacy;
	private static String mEpassUrlForms;
	private static boolean mEpassAuthenticateRequests;

	private static String mMongoUrl;
	private static String mMongoDbName;

	private static File mDsmParentFilePath;
	private static String mMebnFilePath;
	private static String mDomainKnowledgeFilePath;

	private static Integer mTimelineGap;
	private static Integer mTimelineWGap;
	private static Integer mTimelineMergeGap;
	private static Integer mTimelineMaxSteps;

	private static Integer mRecentWindowSize;
	private static Integer mNowWindowSize;
	private static Double mPlight;
	private static Double mPheavy;
	private static boolean mBayesianIgnoreTime;
	private static Integer mTimeScalingFactor;

	private static File mStaticDataParentFilePath;
	private static String mStaticDataFormsPath;
	private static String mStaticDataFeedbackMessagesPath;
	private static String mStaticDataRecommendationsPath;
	private static String mStaticDataPortfolioMessagesPath;
	private static String mStaticDataSupervisorPortfolioMessagesPath;

	private static String mFeedbackMessageLevel1;
	private static String mFeedbackMessageLevel2;
	private static String mPlaceholderEpaName;

	private static String mPlaceholderPerformanceIndicator;
	private static String mPlaceholderPerformanceIndicatorLevel;
	private static String mPlaceholderRecommendation;

	private static String mPlaceholderSupervisorFeedback;
	private static String placeholderSupervisorFeedbackCreationTime;

	private static Set<String> enabledNlpProcessors;

	private static Set<String> enabledDomains;

	private static String mNLPendpoint;

	private static boolean mJitResponseCachingEnabled;
	private static boolean mVizResponseCachingEnabled;

	private static String mAdminAuthToken;

	public static void LoadSettings(Properties properties, File resourcesDir) {
		DomainCredentials.LoadCredentialsSettings(properties);

		serverVersion = properties.getProperty("sm.server.version", "none");

		mEpassUrlLogin = properties.getProperty("epass.urls.login");
		mEpassUrlPrivacy = properties.getProperty("epass.urls.privacy");
		mEpassUrlForms = properties.getProperty("epass.urls.forms");
		mEpassAuthenticateRequests = Boolean.valueOf(properties.getProperty("epass.privacymanager.authenticateRequests", "true"));

		mMongoUrl = properties.getProperty("mongo.url");
		mMongoDbName = properties.getProperty("mongo.dbname");

		mDsmParentFilePath = new File(resourcesDir, properties.getProperty("dsm.parentPath"));
		mMebnFilePath = properties.getProperty("dsm.mebnFilePath");
		mDomainKnowledgeFilePath = properties.getProperty("dsm.domainKnowledgeFilePath");
		mTimelineGap = Integer.valueOf(properties.getProperty("sm.timelineGap", "1"));
		mTimelineWGap = Integer.valueOf(properties.getProperty("sm.timelineWGap", "7"));
		mTimelineMergeGap = Integer.valueOf(properties.getProperty("sm.timelineMergeGap", "7"));
		mTimelineMaxSteps = Integer.valueOf(properties.getProperty("sm.timelineMaxSteps", "52"));

		mRecentWindowSize = Integer.valueOf(properties.getProperty("recentWindowSize", "20"));
		mNowWindowSize = Integer.valueOf(properties.getProperty("nowWindowSize", "5"));
		mPlight = Double.valueOf(properties.getProperty("pLight", "0.05"));
		mPheavy = Double.valueOf(properties.getProperty("pHeavy", "0.02"));
		mBayesianIgnoreTime = Boolean.valueOf(properties.getProperty("BayesianIgnoreTime", "false"));
		mTimeScalingFactor = Integer.valueOf(properties.getProperty("TimeScalingFactor", "7"));

		mStaticDataParentFilePath = new File(resourcesDir, properties.getProperty("staticData.parentPath"));
		mStaticDataFormsPath = properties.getProperty("staticData.formsPath");
		mStaticDataFeedbackMessagesPath = properties.getProperty("staticData.feedbackMessagesPath");
		mStaticDataRecommendationsPath = properties.getProperty("staticData.recommendationsPath");
		mStaticDataPortfolioMessagesPath = properties.getProperty("staticData.portfolioMessagesPath");
		mStaticDataSupervisorPortfolioMessagesPath = properties.getProperty("staticData.supervisorPortfolioMessagesPath");

		enabledNlpProcessors = createBarSeparatedSet(properties.getProperty("enabled.NLP.domain.list", ""));
		enabledDomains = createBarSeparatedSet(properties.getProperty("enabled.domain.list", ""));
		mNLPendpoint = properties.getProperty("NLP.endpoint", "http://localhost:8080/sm-nlp-v2/api/LanguageService/process");

		mFeedbackMessageLevel1 = properties.getProperty("feedback.message.level1");
		mFeedbackMessageLevel2 = properties.getProperty("feedback.message.level2");
		mPlaceholderEpaName = properties.getProperty("feedback.message.placeholders.epaName");
		mPlaceholderPerformanceIndicator = properties.getProperty("feedback.message.placeholders.performanceIndicator");
		mPlaceholderPerformanceIndicatorLevel = properties.getProperty("feedback.message.placeholders.performanceIndicatorLevel");
		mPlaceholderRecommendation = properties.getProperty("feedback.message.placeholders.recommendation");
		mPlaceholderSupervisorFeedback = properties.getProperty("feedback.message.placeholders.supervisorFeedback");
		placeholderSupervisorFeedbackCreationTime = properties.getProperty("feedback.message.placeholders.supervisorFeedback.creationTime");

		mJitResponseCachingEnabled = Boolean.valueOf(properties.getProperty("jit.enableCaching", "true"));
		mVizResponseCachingEnabled = Boolean.valueOf(properties.getProperty("viz.enableCaching", "true"));

		mAdminAuthToken = properties.getProperty("admin.hash", "XXXXXX");
	}

	public static Boolean getBayesianIgnoreTime() {
		return mBayesianIgnoreTime;
	}

	public static Integer getTimeScalingFactor() {
		return mTimeScalingFactor;
	}

	public static String getServerVersion() {
		return serverVersion;
	}

	public static String getDomainCredentialsUsername(String modelId) {
		return DomainCredentials.getUsernameForModelId(modelId);
	}

	public static String getDomainCredentialsSecret(String modelId) {
		return DomainCredentials.getPasswordForModelId(modelId);
	}

	public static String getEpassUrlLogin() {
		return mEpassUrlLogin;
	}

	public static String getEpassUrlPrivacy() {
		return mEpassUrlPrivacy;
	}

	public static String getEpassUrlForms() {
		return mEpassUrlForms;
	}

	public static File getStaticDataRecommendationsPath(String domain) {
		return new File(mStaticDataParentFilePath, String.format(mStaticDataRecommendationsPath, domain));
	}

	public static File getStaticDataFeedbackMessagesPath(String domain) {
		return new File(mStaticDataParentFilePath, String.format(mStaticDataFeedbackMessagesPath, domain));
	}

	public static File getStaticDataPortfolioMessagesPath(String domain) {
		return new File(mStaticDataParentFilePath, String.format(mStaticDataPortfolioMessagesPath, domain));
	}

	public static File getStaticDataSupervisorPortfolioMessagesPath(String domain) {
		return new File(mStaticDataParentFilePath, String.format(mStaticDataSupervisorPortfolioMessagesPath, domain));
	}

	public static File getStaticDataFormsPath(String domain) {
		return new File(mStaticDataParentFilePath, String.format(mStaticDataFormsPath, domain));
	}

	public static File getMebnFilePath(String domain) {
		return new File(mDsmParentFilePath, String.format(mMebnFilePath, domain));
	}

	public static File getDomainKnowledgeFilePath(String domain) {
		return new File(mDsmParentFilePath, String.format(mDomainKnowledgeFilePath, domain));
	}

	public static Integer getTimelineGap() {
		return mTimelineGap;
	}

	public static Integer getTimelineWGap() {
		return mTimelineWGap;
	}

	public static Integer getTimelineMaxSteps() {
		return mTimelineMaxSteps;
	}

	public static Integer getTimelineMergeGap() {
		return mTimelineMergeGap;
	}

	public static Integer getRecentWindowSize() {
		return mRecentWindowSize;
	}

	public static Integer getNowWindowSize() {
		return mNowWindowSize;
	}

	public static Double getPlight() {
		return mPlight;
	}

	public static Double getPheavy() {
		return mPheavy;
	}

	public static String getFeedbackMessageLevel1() {
		return mFeedbackMessageLevel1;
	}

	public static String getFeedbackMessageLevel2() {
		return mFeedbackMessageLevel2;
	}

	public static String getPlaceholderSupervisorFeedback() {
		return mPlaceholderSupervisorFeedback;
	}

	public static String getPlaceholderSupervisorFeedbackCreationTime() {
		return placeholderSupervisorFeedbackCreationTime;
	}

	public static String getPlaceholderRecommendation() {
		return mPlaceholderRecommendation;
	}

	public static String getPlaceholderPerformanceIndicatorLevel() {
		return mPlaceholderPerformanceIndicatorLevel;
	}

	public static String getPlaceholderPerformanceIndicator() {
		return mPlaceholderPerformanceIndicator;
	}

	public static String getPlaceholderEpaName() {
		return mPlaceholderEpaName;
	}

	private static Set<String> createBarSeparatedSet(String setProperty) {
		return Arrays.asList(setProperty.split("\\s*\\|\\s*")).stream().map(String::trim).filter(s -> s.length() > 0).map(String::toLowerCase).collect(toSet());
	}

	public static boolean hasNlpProcessorEnabled(String domainId) {
		return enabledNlpProcessors.contains(domainId.toLowerCase());
	}

	public static Set<String> getEnabledNlpProcessors() {
		return enabledNlpProcessors;
	}

	public static String getNLPendpoint() {
		return mNLPendpoint;
	}

	public static Set<String> getEnabledDomains() {
		return enabledDomains;
	}

	public static String getMongoUrl() {
		return mMongoUrl;
	}

	public static String getMongoDbName() {
		return mMongoDbName;
	}

	public static boolean isDomainSupported(String domainId) {
		return enabledDomains.contains(domainId.toLowerCase());
	}

	public static boolean isAuthenticateRequestsInPrivacyManagerEnabled() {
		return mEpassAuthenticateRequests;
	}

	public static boolean isJitResponseCachingEnabled() {
		return mJitResponseCachingEnabled;
	}

	public static boolean isVizResponseCachingEnabled() {
		return mVizResponseCachingEnabled;
	}

	public static String getAdminAuthToken() {
		return mAdminAuthToken;
	}
}
