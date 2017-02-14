/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: NetRom Software, RO, http://www.netromsoftware.ro/
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

package eu.watchme.modules.dispatcher;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.logging.LoggingUtils;
import eu.watchme.modules.commons.staticdata.StaticModelException;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.commons.staticdata.model.feedback.FeedbackType;
import eu.watchme.modules.commons.staticdata.model.form.json.EpaMappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.MappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.PIMappedField;
import eu.watchme.modules.commons.staticdata.model.form.json.SupervisorFeedbackField;
import eu.watchme.modules.dataaccess.adapters.*;
import eu.watchme.modules.dataaccess.dataservices.EpassDeletionsDataService;
import eu.watchme.modules.dataaccess.dataservices.EpassFormsDataService;
import eu.watchme.modules.dataaccess.dataservices.JitQueriesDataService;
import eu.watchme.modules.dataaccess.dataservices.VizQueriesDataService;
import eu.watchme.modules.dataaccess.model.*;
import eu.watchme.modules.dataaccess.model.activity.StudentStatsDataObject;
import eu.watchme.modules.dataaccess.model.forms.EpassFormDataObject;
import eu.watchme.modules.dataaccess.model.forms.QuestionDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.jit.JitQueryResponse;
import eu.watchme.modules.dataaccess.model.requests.viz.timeline.TimelineQueryDataObject;
import eu.watchme.modules.dataaccess.model.requests.viz.timeline.TimelineQueryResponse;
import eu.watchme.modules.dataaccess.model.snapshots.*;
import eu.watchme.modules.dataaccess.model.submissions.EpassRequestDataObject;
import eu.watchme.modules.dataaccess.model.submissions.SubmissionItemDataObject;
import eu.watchme.modules.dispatcher.converters.Converter;
import eu.watchme.modules.dispatcher.epass.ProcessedEpa;
import eu.watchme.modules.dispatcher.epass.ProcessedPi;
import eu.watchme.modules.dispatcher.epass.ProcessedSubmission;
import eu.watchme.modules.dispatcher.jit.JitResponseMapper;
import eu.watchme.modules.dispatcher.jit.UnbQueryAdapter;
import eu.watchme.modules.dispatcher.jit.UnbQueryResult;
import eu.watchme.modules.dispatcher.viz.UnbSnapshotResult;
import eu.watchme.modules.dispatcher.viz.VizResponseMapper;
import eu.watchme.modules.domainmodel.TDelete;
import eu.watchme.modules.domainmodel.TPost;
import eu.watchme.modules.domainmodel.epass.forms.EpassFormModel;
import eu.watchme.modules.domainmodel.epass.submissions.SubmissionModel;
import eu.watchme.modules.domainmodel.exceptions.*;
import eu.watchme.modules.domainmodel.jit.FeedbackQueryModel;
import eu.watchme.modules.domainmodel.jit.FeedbackResponseModel;
import eu.watchme.modules.domainmodel.jit.supervisor.SupervisedStudentPortfolioModel;
import eu.watchme.modules.domainmodel.serialization.Constants;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceQueryModel;
import eu.watchme.modules.domainmodel.viz.current_performance.CurrentPerformanceResponseModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorQueryStudentModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseModel;
import eu.watchme.modules.domainmodel.viz.supervisor.SupervisorResponseStudentModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineQueryModel;
import eu.watchme.modules.domainmodel.viz.timeline.TimelineResponseModel;
import eu.watchme.modules.epass.EpassFormsServiceClient;
import eu.watchme.modules.ndp.NumericDataProcessor;
import eu.watchme.modules.ndp.datatypes.StudentAssessment;
import eu.watchme.modules.ndp.datatypes.StudentRole;
import eu.watchme.modules.ndp.datatypes.StudentScore;
import eu.watchme.modules.ndp.datatypes.StudentSentiment;
import eu.watchme.modules.ndp.model.NumericOutputData;
import eu.watchme.modules.nlp.NaturalLanguageProcessor;
import eu.watchme.modules.unbbayes.BayesianStudentModel;
import eu.watchme.modules.unbbayes.model.queries.results.FeedbackResult;
import eu.watchme.modules.unbbayes.model.queries.results.SnapshotResult;
import eu.watchme.modules.unbbayes.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unbbayes.prs.mebn.entity.exception.TypeException;

import java.io.FileNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;
import static eu.watchme.modules.dataaccess.adapters.StudentStatsAdapter.studentStatsAdapter;
import static eu.watchme.modules.dataaccess.adapters.SupervisorFeedbackAdapter.supervisorFeedbackAdapter;
import static eu.watchme.modules.datamerger.DataMerger.dataMerger;
import static eu.watchme.modules.dispatcher.converters.Converter.*;

public class APIDispatcher {
	private Logger logger = LoggerFactory.getLogger(APIDispatcher.class);
	private static final String EPA_ONLY = "~";

	private APIDispatcher() {
	}

	public static APIDispatcher newInstance() {
		return new APIDispatcher();
	}

	@SuppressWarnings("unchecked")
	public void dispatch(TPost<SubmissionModel> data) throws Exception {
		logger.debug("Thread-{} : Start dispatching submissions...", Thread.currentThread().getId());
		validate(data);

		Converter<TPost<SubmissionModel>, EpassRequestDataObject> converter = getEpassSubmissionConverter();
		EpassRequestDataObject dataObject = converter.toDataObject(data);
		StudentTimeMapAdapter bayesTimeAdapter = new StudentTimeMapAdapter();
		if (bayesTimeAdapter.isLegacy(dataObject.getStudentId())) {
			logger.debug("Thread-{} : bayesian model is legacy setting needs to be transferred ", Thread.currentThread().getId());
			bayesTimeAdapter.deleteLegacy(dataObject.getStudentId());
			logger.debug("Thread-{} : Loading latest snapshot ", Thread.currentThread().getId());
			if (transferLegacyModel(dataObject.getStudentId(), dataObject.getModelId())) {
				logger.debug("Thread-{} : Model loaded from legacy version ", Thread.currentThread().getId());
			}
		}
		processEpassRequest(dataObject);
		logger.debug("Thread-{} : Dispatch finished", Thread.currentThread().getId());
	}

	public void delete(TDelete data) {
		logger.debug("Thread-{} : Start deleting...", Thread.currentThread().getId());

		validate(data);

		EpassDeletionsDataService dataService = new EpassDeletionsDataService();
		Converter<TDelete, EpassDeleteDataObject> converter = getEpassDeleteConverter();
		EpassDeleteDataObject dataObject = converter.toDataObject(data);
		deleteData(dataObject);
		dataService.storeEpassDelete(dataObject);
		logger.debug("Thread-{} : Deleted", Thread.currentThread().getId());
	}

	public boolean transferLegacyModel(String studentId, String modelId) {
		boolean rs = true;
		try {
			dataMerger.processLegacy(studentId, modelId);
		} catch (TypeException e) {
			logger.error(" Failed to process legacy data");
			rs = false;
		}
		return rs;
	}

	private void deleteData(EpassDeleteDataObject dataObject) {
		logger.debug("Thread-{} : Deleting data for student {}; modelId {}", Thread.currentThread().getId(), dataObject.getStudentId(), dataObject.getModelId());

		deleteSupervisorFeedback(dataObject.getStudentId());
		deleteStudentFromDatabase(dataObject.getStudentId());
		deleteStudentStatsFromDatabase(dataObject.getStudentId());
		deleteJitQueriesFromDatabase(dataObject.getStudentId());
		deleteVizQueriesFromDatabase(dataObject.getStudentId());
		deleteSnapshots(dataObject.getStudentId());
		deleteStudentStats(dataObject.getStudentId());
	}

	private void deleteSupervisorFeedback(String studentId) {
		logger.debug("Thread-{} : Deleting supervisor feedback", Thread.currentThread().getId());
		SupervisorFeedbackAdapter feedbackAdapter = new SupervisorFeedbackAdapter();
		List<SupervisorFeedbackDataObject> feedbackDataObjects = feedbackAdapter.getSupervisorFeedback(studentId);
		if (feedbackDataObjects != null && !feedbackDataObjects.isEmpty()) {
			feedbackDataObjects.forEach(feedbackAdapter::deleteDataObject);
			logger.debug("Thread-{} : Supervisor feedback deleted", Thread.currentThread().getId());
		} else {
			logger.debug("Thread-{} : No supervisor feedback available", Thread.currentThread().getId());
		}
	}

	private void deleteStudentFromDatabase(String studentId) {
		logger.debug("Thread-{} : Deleting student from database", Thread.currentThread().getId());
		StudentsAdapter studentsAdapter = new StudentsAdapter();
		StudentDataObject studentDataObject = studentsAdapter.getDataObject(studentId);
		if (studentDataObject != null) {
			studentsAdapter.deleteDataObject(studentDataObject);
			logger.debug("Thread-{} : Student deleted", Thread.currentThread().getId());
		} else {
			logger.debug("Thread-{} : Student not found", Thread.currentThread().getId());
		}
	}

	private void deleteStudentStatsFromDatabase(String studentId) {
		logger.debug("Thread-{} : Deleting student stats from database", Thread.currentThread().getId());
		int count = studentStatsAdapter.deleteAllForStudent(studentId);
		if (count > 0) {
			logger.debug("Thread-{} : Student Stats deleted", Thread.currentThread().getId());
		} else {
			logger.debug("Thread-{} : Student stats not found", Thread.currentThread().getId());
		}
	}

	private void deleteJitQueriesFromDatabase(String studentId) {
		logger.debug("Thread-{} : Deleting JIT queries from database", Thread.currentThread().getId());
		JitQueriesDataService dataService = new JitQueriesDataService();
		int noQueriesDeleted = dataService.deleteAllForStudent(studentId);
		logger.debug("Thread-{} : {} JIT queries deleted", Thread.currentThread().getId(), noQueriesDeleted);
	}

	private void deleteVizQueriesFromDatabase(String studentId) {
		logger.debug("Thread-{} : Deleting VIZ queries from database", Thread.currentThread().getId());
		VizQueriesDataService dataService = new VizQueriesDataService();
		int noQueriesDeleted = dataService.deleteAllForStudent(studentId);
		logger.debug("Thread-{} : {} VIZ queries deleted", Thread.currentThread().getId(), noQueriesDeleted);
	}

	private void deleteSnapshots(String studentId) {
		logger.debug("Thread-{} : Deleting snapshots from database", Thread.currentThread().getId());
		StudentSnapshotsAdapter adapter = new StudentSnapshotsAdapter();
		int deletedCount = adapter.deleteAllForStudent(studentId);
		logger.debug("Thread-{} : {} snapshots deleted", Thread.currentThread().getId(), deletedCount);
	}

	private void deleteStudentStats(String studentId) {
		logger.debug("Thread-{} : Deleting student stats from database", Thread.currentThread().getId());
		StudentStatsAdapter adapter = new StudentStatsAdapter();
		int deletedCount = adapter.deleteAllForStudent(studentId);
		logger.debug("Thread-{} : {} stats deleted", Thread.currentThread().getId(), deletedCount);
	}

	private void processEpassRequest(EpassRequestDataObject dataObject) throws Exception {
		if (dataObject.getContent() != null && dataObject.getContent().length > 0) {
			HashSet<String> epasWithGeneralLevel = new HashSet<>();
			String modelId = dataObject.getModelId();
			List<ProcessedSubmission> processedSubmissions = processSubmissions(modelId, dataObject.getContent(), epasWithGeneralLevel);
			if (processedSubmissions != null && !processedSubmissions.isEmpty()) {
				String studentId = dataObject.getStudentId();
				SMData smData = dataMerger.getSmData(studentId, modelId, new StudentTimeMapAdapter().getStudentProfileId(studentId));

				List<TimelineSnapshotDataObject> timelineSnapshots = processSubmissions(studentId, modelId, processedSubmissions, smData);
				CurrentSnapshotDataObject currentSnapshot = getCurrentSnapshot(modelId, studentId, smData, epasWithGeneralLevel);
				List<SupervisorFeedbackDataObject> supervisorFeedbackList = getSupervisorFeedback(studentId, processedSubmissions);
				StudentStatsDataObject studentStats = getStudentStats(modelId, studentId, processedSubmissions);

				storeStudentKnowledge(studentId, smData);
				storeSupervisorFeedback(supervisorFeedbackList);
				storeStudentStats(studentStats);
				storeSnapshot(studentId, modelId, timelineSnapshots, currentSnapshot);
				deleteJitQueriesFromDatabase(studentId);
				deleteVizQueriesFromDatabase(studentId);
				FileUtils.deleteTemps(); /* Attempt to remove temp copies of domain files after garbage collection (not guaranteed) */
			}
		} else {
			logger.debug("Thread-{} : processEpassRequest -> No content available", Thread.currentThread().getId());
		}
	}

	public boolean recomputeCurrentSnapshot(String studentId, String modelId) throws Exception {
		StudentSnapshotsAdapter adapter = new StudentSnapshotsAdapter();
		StudentSnapshotDataObject studentSnapshotDataObject = adapter.getDataObject(studentId, modelId);
		if (studentSnapshotDataObject != null) {
			SMData smData = dataMerger.getSmData(studentId, modelId, new StudentTimeMapAdapter().getStudentProfileId(studentId));
			CurrentSnapshotDataObject currentSnapshot = getCurrentSnapshot(modelId, studentId, smData, new HashSet<>());
			studentSnapshotDataObject.setCurrentSnapshot(currentSnapshot);
			adapter.updateDataObject(studentSnapshotDataObject);
			return true;
		}
		return false;
	}

	private List<SupervisorFeedbackDataObject> getSupervisorFeedback(String studentId, List<ProcessedSubmission> processedSubmissions) {
		List<SupervisorFeedbackDataObject> result = new ArrayList<>(processedSubmissions.size());
		for (ProcessedSubmission processedSubmission : processedSubmissions) {
			for (ProcessedEpa processedEpa : processedSubmission.getProcessedEpas()) {
				processedEpa.getProcessedPis().stream().filter(processedPi -> processedPi.getNlpOutputData() != null).forEach(processedPi -> {
					SupervisorFeedbackDataObject supervisorFeedbackDataObject = new SupervisorFeedbackDataObject(
							studentId,
							processedEpa.getUnbId(),
							processedPi.getNlpOutputData().getLinkedKeywords(),
							processedPi.getNlpOutputData().getFeedbackLevel(),
							processedPi.getNlpOutputData().getFeedback(),
							processedSubmission.getDateReceived(),
							processedPi.getNlpOutputData().getFindingList(),
							processedSubmission.getFormId(),
							processedSubmission.getSubmissionId());
					result.add(supervisorFeedbackDataObject);
				});
			}
		}
		return result;
	}

	private StudentStatsDataObject getStudentStats(String modelId, String studentId, List<ProcessedSubmission> processedSubmissions) {
		logger.debug("Thread-{} : Computing Student Stats", Thread.currentThread().getId());
		StudentStatsDataObject studentStats = studentStatsAdapter.getStats(studentId, modelId);

		if (studentStats == null) {
			studentStats = new StudentStatsDataObject(studentId, modelId);
		}
		for (ProcessedSubmission processedSubmission : processedSubmissions) {
			for (ProcessedEpa processedEpa : processedSubmission.getProcessedEpas()) {
				Collection<String> piUnbIds = processedEpa.getProcessedPis().stream().map(ProcessedPi::getUnbId).collect(Collectors.toList());
				studentStats.addNewEpaSubmission(processedEpa.getUnbId(), processedSubmission.getSubmissionId(), processedSubmission.getFormId(), processedSubmission.getCohort(),
						processedSubmission.getSubmissionReferenceDate(), processedSubmission.getDateReceived(), piUnbIds);
			}
		}
		return studentStats;
	}

	private void storeStudentKnowledge(String studentId, SMData smData) {
		dataMerger.saveStudentKnowledge(studentId, smData.getStudentKnowledge(), smData.getModelImageTimestamp(), smData.getPId());
	}

	private void storeSnapshot(String studentId, String modelId, List<TimelineSnapshotDataObject> timelineSnapshots, CurrentSnapshotDataObject currentSnapshot) {
		StudentSnapshotsAdapter adapter = new StudentSnapshotsAdapter();
		StudentSnapshotDataObject studentSnapshotDataObject = adapter.getDataObject(studentId, modelId);
		if (studentSnapshotDataObject != null) {
			studentSnapshotDataObject.getTimelineSnapshots().addAll(timelineSnapshots);
			studentSnapshotDataObject.setCurrentSnapshot(currentSnapshot);
			adapter.updateDataObject(studentSnapshotDataObject);
		} else {
			studentSnapshotDataObject = new StudentSnapshotDataObject();
			studentSnapshotDataObject.setModelId(modelId);
			studentSnapshotDataObject.setStudentId(studentId);
			studentSnapshotDataObject.getTimelineSnapshots().addAll(timelineSnapshots);
			studentSnapshotDataObject.setCurrentSnapshot(currentSnapshot);
			adapter.insertDataObject(studentSnapshotDataObject);
		}
	}

	private CurrentSnapshotDataObject getCurrentSnapshot(String modelId, String studentId, SMData smData, HashSet<String> epasWithGeneralLevel) throws Exception {
		JitQueryResultDataObject jitQueryResultDataObject = queryStudentModelForJit(modelId, studentId, smData, epasWithGeneralLevel);
		VizQueryResultDataObject vizQueryResultDataObject = queryStudentModelForViz(modelId, studentId, smData);
		return new CurrentSnapshotDataObject(jitQueryResultDataObject, vizQueryResultDataObject);
	}

	private List<ProcessedSubmission> processSubmissions(String modelId, SubmissionItemDataObject[] submissions, HashSet<String> epasWithGeneralLevel) throws FileNotFoundException {
		if (submissions == null) {
			return null;
		}
		List<ProcessedSubmission> processedSubmissions = new ArrayList<>(submissions.length);
		for (SubmissionItemDataObject submission : submissions) {
			ProcessedSubmission processedSubmission = processSubmission(modelId, submission, epasWithGeneralLevel);
			if (processedSubmission != null) {
				processedSubmissions.add(processedSubmission);
			}
		}
		return processedSubmissions;
	}

	private ProcessedSubmission processSubmission(String modelId, SubmissionItemDataObject submission, HashSet<String> epasWithGeneralLevel) throws FileNotFoundException {
		if (submission.getEPAs() != null && !submission.getEPAs().isEmpty()) {
			EpassFormDataObject form = validateForm(modelId, submission.getForm().getId(), submission.getForm().getVersion());
			List<ProcessedEpa> processedEpas = processQuestions(modelId, form, submission, epasWithGeneralLevel);
			return new ProcessedSubmission(form.getFormId(), submission.getId(), submission.getSubmissionDate(), submission.getReferenceDate(), submission.getCohort(),
					submission.getAuthority() == null ? null : submission.getAuthority().getRole(), processedEpas);
		}
		return null;
	}

	private List<TimelineSnapshotDataObject> processSubmissions(String studentId, String modelId, List<ProcessedSubmission> processedSubmissions, SMData smData) throws Exception {
		List<TimelineSnapshotDataObject> snapshots = new ArrayList<>();
		for (ProcessedSubmission submission : processedSubmissions) {
			TimelineSnapshotDataObject snapshotDataObject = processSubmission(studentId, modelId, submission, smData);
			if (snapshotDataObject != null) {
				snapshots.add(snapshotDataObject);
				storeBayesianStudentStats(studentId, submission);
			}
		}
		return snapshots;
	}

	private JitQueryResultDataObject queryStudentModelForJit(String modelId, String studentId, SMData smData, HashSet<String> epasWithGeneralLevel) throws Exception {
		UnbQueryResult queryResult = queryAllEpasForJit(modelId, studentId, smData, epasWithGeneralLevel);
		List<JitFeedbackResultDataObject> feedbackResults = queryResult.getFeedbackResults().stream().map(this::mapToDataObject).collect(Collectors.toList());
		JitPortfolioResultDataObject portfolioResult = new JitPortfolioResultDataObject(queryResult.getFeedbackSeekingStrategyResult(), queryResult.getPortfolioConsistencyResult(),
				queryResult.getEpaNeedingInformationUnbIds(), queryResult.getFrustrationAlertResult());
		return new JitQueryResultDataObject(feedbackResults, portfolioResult);
	}

	private UnbQueryResult queryAllEpasForJit(String modelId, String studentId, SMData smData, HashSet<String> epasWithGeneralLevel) throws Exception {
		logger.debug("Thread-{} : Querying UnBBayes...", Thread.currentThread().getId());
		UnbQueryAdapter queryAdapter = UnbQueryAdapter.getAdapter(modelId);
		if (queryAdapter == null) {
			throw new NoMappingAvailableException("domain", modelId);
		}
		UnbQueryResult unbQueryResult = queryAdapter.queryAllEpasForJit(studentId, smData, epasWithGeneralLevel);
		logger.debug("Thread-{} : Query finished with result:\n{}", Thread.currentThread().getId(), LoggingUtils.format(unbQueryResult));
		return unbQueryResult;
	}

	private JitFeedbackResultDataObject mapToDataObject(FeedbackResult feedback) {
		return new JitFeedbackResultDataObject(feedback.getFeedbackType(), feedback.getEpa(), feedback.getPerformanceIndicator(), feedback.getLevel());
	}

	private VizQueryResultDataObject queryStudentModelForViz(String modelId, String studentId, SMData smData) throws Exception {
		UnbSnapshotResult unbSnapshotResult = queryAllEpasForViz(modelId, studentId, smData);
		List<VizPredictedLevelDataObject> predictedLevels =
				unbSnapshotResult.getResults() == null ? new ArrayList<>(0) : unbSnapshotResult.getResults().stream().map(this::mapToDataObject).collect(Collectors.toList());
		return new VizQueryResultDataObject(predictedLevels);
	}

	private UnbSnapshotResult queryAllEpasForViz(String modelId, String studentId, SMData smData) throws Exception {
		logger.debug("Thread-{} : Querying UnBBayes...", Thread.currentThread().getId());
		UnbQueryAdapter queryAdapter = UnbQueryAdapter.getAdapter(modelId);
		if (queryAdapter == null) {
			throw new NoMappingAvailableException("domain", modelId);
		}
		UnbSnapshotResult unbSnapshotResult = queryAdapter.queryAllEpasForViz(studentId, smData);
		logger.debug("Thread-{} : Query finished with result:\n{}", Thread.currentThread().getId(), LoggingUtils.format(unbSnapshotResult));
		return unbSnapshotResult;
	}

	private VizPredictedLevelDataObject mapToDataObject(SnapshotResult feedback) {
		return new VizPredictedLevelDataObject(feedback.getEpa(), feedback.getPerformanceIndicator(), feedback.getLevel());
	}

	private TimelineSnapshotDataObject processSubmission(String studentId, String modelId, ProcessedSubmission processedSubmission, SMData smData) throws Exception {
		if (processedSubmission.getProcessedEpas() == null || processedSubmission.getProcessedEpas().isEmpty()) {
			return null;
		}
		StudentTimeMapDataObject bayesTime = processBayesianTime(studentId, modelId, processedSubmission.getSubmissionDate(), smData);

		BayesianStudentModel bayesianModel = dataMerger
				.addFindings(studentId, modelId, smData, processedSubmission.getBayesianNumericalFindings(), processedSubmission.getBayesianNlpFindings(), bayesTime);
		if (bayesianModel == null) {
			logger.debug("Thread-{} : No findings were added for submission id {}", Thread.currentThread().getId(), processedSubmission.getSubmissionId());
			return null;
		}
		smData.setStudentKnowledge(bayesianModel.getKnowledge());
		smData.setModelImageTimestamp(bayesianModel.getModelImageTimestamp());
		smData.setPId(bayesTime.getProfileId());
		Integer formId = processedSubmission.getFormId();
		Integer submissionId = processedSubmission.getSubmissionId();
		Instant submissionDate = parseServerDate(processedSubmission.getSubmissionReferenceDate());
		String cohort = processedSubmission.getCohort();
		Instant receivedDate = processedSubmission.getDateReceived();
		List<TimelineSnapshotEpaDataObject> epas = getSnapshotEpaList(processedSubmission);

		return new TimelineSnapshotDataObject(formId, submissionId, submissionDate, cohort, receivedDate, epas);
	}

	private StudentTimeMapDataObject processBayesianTime(String studentId, String modelId, String submissionDate, SMData smData) {
		StudentTimeMapAdapter adapter = new StudentTimeMapAdapter();
		int profileId = adapter.getStudentProfileId(studentId);

		StudentTimeMapDataObject dataObject = adapter.getNextTimeElement(studentId, profileId, submissionDate);
		dataObject = adapter.addTimeStep(dataObject);
		if (adapter.isMerge()) {
			try {
				dataMerger.mergeKnowledge(studentId, modelId, dataObject.getProfileId(), smData);
				dataObject = adapter.getNextTimeElement(studentId, profileId, submissionDate);
				dataObject = adapter.addTimeStep(dataObject);
			} catch (TypeException e) {
				e.printStackTrace();
			}
		}

		logger.info("Thread-{} : Bayesian Student profile id : {}", Thread.currentThread().getId(), dataObject.toString());
		return dataObject;
	}

	private Instant parseServerDate(String dateString) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
			LocalDate date = LocalDate.parse(dateString, formatter);
			return date.atStartOfDay(ZoneId.of("UTC")).toInstant();
		} catch (Exception e) {
			logger.debug("Thread-{} : Could not parse date: {}", Thread.currentThread().getId(), dateString);
		}
		return null;
	}

	private List<TimelineSnapshotEpaDataObject> getSnapshotEpaList(ProcessedSubmission processedSubmission) {
		if (processedSubmission.getProcessedEpas() == null) {
			return new ArrayList<>(0);
		}

		ArrayList<TimelineSnapshotEpaDataObject> epaList = new ArrayList<>(processedSubmission.getProcessedEpas().size());
		for (ProcessedEpa processedEpa : processedSubmission.getProcessedEpas()) {
			String unbId = processedEpa.getUnbId();
			String actualScoreUnbId = processedEpa.getMeanRankNumericOutputData() == null ? null : processedEpa.getMeanRankNumericOutputData().getLevelUnbId();
			String sentiment = getSentimentFromPis(processedEpa.getProcessedPis());
			List<TimelineSnapshotNumericalPiDataObject> performanceIndicators = getSnapshotNumericalPiList(processedEpa.getProcessedPis());
			List<TimelineSnapshotTextPiDataObject> feedbackIndicators = getSnapshotTextPiList(processedEpa.getProcessedPis());
			epaList.add(new TimelineSnapshotEpaDataObject(unbId, actualScoreUnbId, sentiment, performanceIndicators, feedbackIndicators));
		}

		return epaList;
	}

	private String getSentimentFromPis(List<ProcessedPi> processedPiList) {
		Optional<ProcessedPi> first = processedPiList.stream().filter(pi -> pi.getNlpOutputData() != null && pi.getNlpOutputData().getSentiment() != null).findFirst();
		if (first.isPresent()) {
			return first.get().getNlpOutputData().getSentiment();
		}
		return null;
	}

	private List<TimelineSnapshotNumericalPiDataObject> getSnapshotNumericalPiList(List<ProcessedPi> processedPis) {
		List<TimelineSnapshotNumericalPiDataObject> resultList = new ArrayList<>(processedPis.size());
		resultList.addAll(processedPis.stream().filter(pi -> pi.getNumericOutputData() != null)
				.map(processedPi -> new TimelineSnapshotNumericalPiDataObject(processedPi.getUnbId(), processedPi.getNumericOutputData().getLevelUnbId())).collect(Collectors.toList()));
		return resultList;
	}

	private List<TimelineSnapshotTextPiDataObject> getSnapshotTextPiList(List<ProcessedPi> processedPis) {
		List<TimelineSnapshotTextPiDataObject> resultList = new ArrayList<>(processedPis.size());
		resultList.addAll(processedPis.stream().filter(pi -> pi.getNlpOutputData() != null)
				.map(processedPi -> new TimelineSnapshotTextPiDataObject(processedPi.getUnbId(), processedPi.getNlpOutputData().getFeedback(), processedPi.getNlpOutputData().getSentiment(),
						processedPi.getNlpOutputData().getFeedbackLevel(), processedPi.getNlpOutputData().getLinkedKeywords(), processedPi.getNlpOutputData().getFindingList()))
				.collect(Collectors.toList()));
		return resultList;
	}

	private EpassFormDataObject validateForm(String domainId, Integer formId, String formVersion) {
		ensureFormAvailable(domainId, formId, formVersion);

		EpassFormDataObject form = new EpassFormsDataService().getForm(formId, formVersion, domainId);
		validateFormType(domainId, formId, form.getFormType());

		return form;
	}

	private List<ProcessedEpa> processQuestions(String domainId, EpassFormDataObject form, SubmissionItemDataObject submission, HashSet<String> epasWithGeneralLevel) throws FileNotFoundException {
		List<ProcessedEpa> processedEpas = new ArrayList<>();

		HashMap<String, QuestionAnswerType> questionTypesMap = getQuestionTypesMap(form, submission);
		NumericDataProcessor numericDataProcessor = NumericDataProcessor.getProcessor(domainId);
		NaturalLanguageProcessor naturalLanguageProcessor = NaturalLanguageProcessor.getProcessor(domainId);

		for (String epaEpassId : submission.getEPAs()) {
			String epaUnbId = getUnbEpaId(domainId, epaEpassId);

			ProcessedEpa processedEpa = new ProcessedEpa();
			processedEpa.setEpassId(epaEpassId);
			processedEpa.setUnbId(epaUnbId);

			List<ProcessedPi> processedPis = new ArrayList<>(submission.getAnswers().size());
			for (HashMap.Entry<String, Object> entry : submission.getAnswers().entrySet()) {
				String piEpassId = entry.getKey();

				QuestionAnswerType answerType = questionTypesMap.get(entry.getKey());
				if (answerType == QuestionAnswerType.NUMERIC) {
					String piUnbId = getUnbPerformanceIndicatorId(domainId, form.getFormType(), piEpassId, epaUnbId);
					if (piUnbId != null) {
						if (EPA_ONLY.equals(piUnbId)) {
							processedEpa.setMeanRankNumericOutputData(numericDataProcessor.process(String.valueOf(entry.getValue())));
							epasWithGeneralLevel.add(epaEpassId);
						} else {
							ProcessedPi processedPi = new ProcessedPi();
							processedPi.setEpassId(piEpassId);
							processedPi.setUnbId(piUnbId);
							processedPi.setNumericOutputData(numericDataProcessor.process(String.valueOf(entry.getValue())));
							processedPis.add(processedPi);
						}
					} else {
						reportMissingMapping(piEpassId);
					}
				} else if (answerType == QuestionAnswerType.NARRATIVE) {
					String piUnbId = getUnbSupervisorFieldId(domainId, form.getFormType(), piEpassId, epaUnbId);
					if (piUnbId != null) {
						ProcessedPi processedPi = new ProcessedPi();
						processedPi.setEpassId(piEpassId);
						processedPi.setUnbId(piUnbId);
						processedPi.setNlpOutputData(naturalLanguageProcessor.process(form.getFormType(), piEpassId, epaUnbId, String.valueOf(entry.getValue())));
						processedPis.add(processedPi);
					} else {
						reportMissingMapping(piEpassId);
					}
				}
			}
			processedEpa.setProcessedPis(processedPis);
			if (processedEpa.getMeanRankNumericOutputData() == null) {
				List<Integer> piLevels = processedPis.stream().filter(processedPi -> processedPi.getNumericOutputData() != null).map(pi -> pi.getNumericOutputData().getLevelRank())
						.collect(Collectors.toList());
				NumericOutputData meanRank = numericDataProcessor.getMeanRank(epaUnbId, piLevels);
				processedEpa.setMeanRankNumericOutputData(meanRank);
			}
			processedEpas.add(processedEpa);
		}

		return processedEpas;
	}

	private String getUnbSupervisorFieldId(String domainId, Integer formType, String piEpassId, String epaUnbId) {
		try {
			List<SupervisorFeedbackField> supervisorFeedbackFields = getManager(domainId).getFormMapper().getSupervisorFeedbackField(formType, piEpassId);
			Optional<SupervisorFeedbackField> optionalSupervisorFeedbackField = supervisorFeedbackFields.stream().filter(pi -> pi.getUnbKey().getEpaId().equals(epaUnbId)).findFirst();

			if (optionalSupervisorFeedbackField.isPresent() && optionalSupervisorFeedbackField.get().getUnbKey() != null) {
				return optionalSupervisorFeedbackField.get().getUnbKey().getUnbId();
			} else {
				return null;
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("supervisor field id", piEpassId, e);
		}
	}

	private void reportMissingMapping(String epassPI) {
		logger.info("Thread-{} : Ignored field Id due to missing mapping = {}", Thread.currentThread().getId(), epassPI);
	}

	private HashMap<String, QuestionAnswerType> getQuestionTypesMap(EpassFormDataObject form, SubmissionItemDataObject submission) {
		HashMap<String, QuestionAnswerType> questionTypesMap = new HashMap<>();
		for (HashMap.Entry<String, Object> entry : submission.getAnswers().entrySet()) {
			QuestionAnswerType answerType = getAnswerTypeForQuestion(form, entry.getKey());
			questionTypesMap.put(entry.getKey(), answerType);
		}
		return questionTypesMap;
	}

	protected String getUnbEpaId(String domainId, String epassEpaId) {
		try {
			MappedField unbEpaId = getManager(domainId).getFormMapper().getMappedEpaByEpass(epassEpaId);

			if (unbEpaId != null && unbEpaId.getUnbId() != null) {
				return unbEpaId.getUnbId();
			} else {
				throw new NoMappingAvailableException("unb epa id", epassEpaId);
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("unb epa id", epassEpaId, e);
		}
	}

	protected String getUnbPerformanceIndicatorId(String domainId, Integer epassFormType, String epassPiId, String unbEPAid) {
		try {
			List<PIMappedField> unbPerformanceIndicatorIds =
					getManager(domainId).getFormMapper().getMappedPerformanceIndicatorByEpass(epassFormType, epassPiId);
			Optional<PIMappedField> unbPerformanceIndicatorId = unbPerformanceIndicatorIds.stream().filter(pi -> pi.getUnbKey().getEpaId().equals(unbEPAid)).findFirst();

			if (unbPerformanceIndicatorId != null && unbPerformanceIndicatorId.isPresent() &&
					unbPerformanceIndicatorId.get().getUnbKey() != null) {
				return unbPerformanceIndicatorId.get().getUnbKey().getUnbId();
			} else {
				return null;
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("performance indicator id", epassPiId, e);
		}
	}

	private void validateFormType(String domainId, Integer formId, Integer formType) {
		if (formType == null) {
			throw new ValidationException("The form type is not available for form id=" + formId);
		}

		if (!isFormTypeSupported(domainId, formType)) {
			throw new FormTypeNotSupportedException(formType);
		}
	}

	private void ensureFormAvailable(String modelId, Integer formId, String formVersion) {
		EpassFormsDataService dataService = new EpassFormsDataService();
		ensureFormAvailability(dataService, formId, formVersion, modelId);
	}

	private void ensureFormAvailability(EpassFormsDataService controller, Integer formId, String formVersion, String modelId) {
		logger.debug("Thread-{} : Checking form availability - formId {}, formVersion {}, modelId {}", Thread.currentThread().getId(), formId, formVersion, modelId);
		boolean isFormAvailable = controller.isFormAvailable(formId, formVersion, modelId);
		if (!isFormAvailable) {
			EpassFormModel epassFormModel = retrieveFormDataFromEpass(formId, modelId);
			Converter<EpassFormModel, EpassFormDataObject> converter = getEpassFormConverter();
			EpassFormDataObject form = converter.toDataObject(epassFormModel);
			form.setFormId(formId);
			form.setFormVersion(formVersion);
			form.setModelId(modelId);
			controller.storeForm(form);
		} else {
			logger.debug("Thread-{} : Form is available", Thread.currentThread().getId());
		}
	}

	private EpassFormModel retrieveFormDataFromEpass(Integer formId, String modelId) {
		logger.debug("Thread-{} : Requesting form with id {}, modelId {}", Thread.currentThread().getId(), formId, modelId);
		EpassFormsServiceClient service = new EpassFormsServiceClient();
		EpassFormModel epassFormModel = service.downloadFormData(formId, modelId);
		logger.debug("Thread-{} : Form downloaded\n{}", Thread.currentThread().getId(), LoggingUtils.format(epassFormModel));
		return epassFormModel;
	}

	public QuestionAnswerType getAnswerTypeForQuestion(EpassFormDataObject form, String questionId) {
		QuestionAnswerType result = QuestionAnswerType.NUMERIC;
		if (form.getQuestions() != null) {
			for (QuestionDataObject question : form.getQuestions()) {
				if (question.getQuestionId() != null && question.getQuestionId().equals(questionId)) {
					if (question.getNarrativefeedback() != null) {
						result = QuestionAnswerType.NARRATIVE;
					}
					break;
				}
			}
		}
		return result;
	}

	protected boolean isFormTypeSupported(String modelId, Integer formType) {
		try {
			return getManager(modelId).getFormMapper().getFormTypes().contains(formType);
		} catch (StaticModelException e) {
			throw new FormIdNotFoundException(formType, e);
		}
	}

	public FeedbackResponseModel processQuery(FeedbackQueryModel data) throws Exception {
		logger.debug("Thread-{} : Start processing query...", Thread.currentThread().getId());
		FeedbackResponseModel responseModel;
		Converter<FeedbackQueryModel, JitQueryDataObject> jitQueryConverter = getJitQueryConverter();
		Converter<FeedbackResponseModel, JitQueryResponse> jitQueryResponseConverter = getJitQueryResponseConverter();

		JitQueryDataObject queryDataObject = jitQueryConverter.toDataObject(data);
		JitQueryResponse cachedResponse = getCachedResponse(data);
		if (cachedResponse == null) {
			StudentSnapshotDataObject studentSnapshotDataObject = new StudentSnapshotsAdapter().getDataObject(data.getAuthorisationData().getStudentHash(), data.getModelId());
			List<JitFeedbackResultDataObject> jitFeedbackResults = getJitFeedbackResults(data, studentSnapshotDataObject);
			JitPortfolioResultDataObject jitPortfolioResult = getJitPortfolioResult(data, studentSnapshotDataObject);
			responseModel = mapResult(data, jitFeedbackResults, jitPortfolioResult);
			queryDataObject.setResponse(jitQueryResponseConverter.toDataObject(responseModel));
		} else {
			responseModel = jitQueryResponseConverter.fromDataObject(cachedResponse);
			queryDataObject.setResponse(cachedResponse);
		}
		if (ApplicationSettings.isJitResponseCachingEnabled()) {
			queryDataObject.setDateSubmitted(Instant.now(Clock.systemUTC()));
			updateQuery(queryDataObject, data.getAuthorisationData().getStudentHash(), data.getModelId(), data.getEpaId(), data.getFeedbackType(), data.getLanguageCode());
		}
		return responseModel;
	}

	private JitQueryResponse getCachedResponse(FeedbackQueryModel data) {
		JitQueryResponse cachedResponse = null;
		if (ApplicationSettings.isJitResponseCachingEnabled()) {
			JitQueryDataObject existingQueryDataObject = getExistingQuery(data.getAuthorisationData().getStudentHash(), data.getModelId(), data.getEpaId(), data.getFeedbackType(),
					data.getLanguageCode());
			if (existingQueryDataObject != null && existingQueryDataObject.getDateSubmitted() != null && existingQueryDataObject.getResponse() != null) {
				Instant lastModelImageTimestamp = new StudentsAdapter().getModelImageTimestamp(data.getAuthorisationData().getStudentHash());
				if (lastModelImageTimestamp != null && lastModelImageTimestamp.isBefore(existingQueryDataObject.getDateSubmitted())) {
					cachedResponse = existingQueryDataObject.getResponse();
				}
			}
		}
		return cachedResponse;
	}

	private JitQueryDataObject getExistingQuery(String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		JitQueriesDataService dataService = new JitQueriesDataService();
		return dataService.getExistingQuery(studentId, modelId, epaId, feedbackType, languageCode);
	}

	private void updateQuery(JitQueryDataObject queryDataObject, String studentId, String modelId, String epaId, String feedbackType, LanguageCode languageCode) {
		logger.debug("Thread-{} : Saving query...", Thread.currentThread().getId());
		JitQueriesDataService dataService = new JitQueriesDataService();
		int noAffectedRecords = dataService.updateQuery(queryDataObject, studentId, modelId, epaId, feedbackType, languageCode);
		logger.debug("Thread-{} : Query stored in the database (noAffectedRecords = {})", Thread.currentThread().getId(), noAffectedRecords);
	}

	private List<JitFeedbackResultDataObject> getJitFeedbackResults(FeedbackQueryModel data, StudentSnapshotDataObject studentSnapshotDataObject) throws Exception {
		if (studentSnapshotDataObject == null || studentSnapshotDataObject.getCurrentSnapshot() == null || studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult() == null
				|| studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getFeedbackResults() == null)
			return new ArrayList<>(0);
		List<JitFeedbackResultDataObject> feedbackResults = studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getFeedbackResults();
		if (data.isFilterEpas()) {
			Set<String> epaUnbIdsForFiltering = getEpaUnbIds(data.getModelId(), data.getEpaIdsForFiltering());
			feedbackResults = feedbackResults.stream()
					.filter(feedbackResult -> isEpaRequested(epaUnbIdsForFiltering, feedbackResult.getEpaUnbId()) && isFeedbackTypeRequested(data.getModelId(), feedbackResult.getFeedbackType(),
							data.getFeedbackType()))
					.collect(Collectors.toList());
		}
		return feedbackResults;
	}

	private JitPortfolioResultDataObject getJitPortfolioResult(FeedbackQueryModel data, StudentSnapshotDataObject studentSnapshotDataObject) {
		if (studentSnapshotDataObject == null || studentSnapshotDataObject.getCurrentSnapshot() == null || studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult() == null
				|| studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getPortfolioResult() == null)
			return null;
		JitPortfolioResultDataObject portfolioResult = studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getPortfolioResult();
		if (data.isFilterEpas()) {
			Set<String> epaUnbIdsForFiltering = getEpaUnbIds(data.getModelId(), data.getEpaIdsForFiltering());
			List<String> epasNeedingMoreInformation = portfolioResult.getEpaNeedingInformationUnbIds().stream()
					.filter(epaUnbId -> isEpaRequested(epaUnbIdsForFiltering, epaUnbId))
					.collect(Collectors.toList());
			portfolioResult.setEpaNeedingInformationUnbIds(epasNeedingMoreInformation);
		}
		return portfolioResult;
	}

	private boolean isFeedbackTypeRequested(String modelId, String actualFeedbackTypeUnbId, String requestedFeedbackTypeEpassId) {
		return requestedFeedbackTypeEpassId == null || requestedFeedbackTypeEpassId.trim().isEmpty() || requestedFeedbackTypeEpassId.equals("*") || FeedbackType
				.isCompatible(getFeedbackTypeByEpassId(modelId, requestedFeedbackTypeEpassId), getFeedbackTypeByUnbId(modelId, actualFeedbackTypeUnbId));
	}

	private FeedbackType getFeedbackTypeByEpassId(String modelId, String epassFeedbackIndicatorId) {
		try {
			FeedbackType unbFeedbackId = getManager(modelId).getFormMapper().getFeedbackTypeByEpass(epassFeedbackIndicatorId);

			if (unbFeedbackId != null && unbFeedbackId != FeedbackType.ABSURD) {
				return unbFeedbackId;
			} else {
				throw new NoMappingAvailableException("epass feedback id", epassFeedbackIndicatorId);
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("epass feedback id", epassFeedbackIndicatorId, e);
		}
	}

	private FeedbackType getFeedbackTypeByUnbId(String modelId, String unbFeedbackIndicatorId) {
		try {
			FeedbackType unbFeedbackId = getManager(modelId).getFormMapper().getFeedbackTypeByUnb(unbFeedbackIndicatorId);

			if (unbFeedbackId != null) {
				return unbFeedbackId;
			} else {
				throw new NoMappingAvailableException("unb feedback id", unbFeedbackIndicatorId);
			}
		} catch (StaticModelException e) {
			throw new NoMappingAvailableException("unb feedback id", unbFeedbackIndicatorId, e);
		}
	}

	private Set<String> getEpaUnbIds(String modelId, Set<String> epaEpassIds) {
		return epaEpassIds.stream().map(epaEpassId -> getUnbEpaId(modelId, epaEpassId)).collect(Collectors.toSet());
	}

	private boolean isEpaRequested(Set<String> epaUnbIdsForFiltering, String epaUnbId) {
		return epaUnbIdsForFiltering.stream().filter(unbId -> unbId.equals(epaUnbId)).findFirst().isPresent();
	}

	private FeedbackResponseModel mapResult(FeedbackQueryModel data, List<JitFeedbackResultDataObject> jitFeedbackResults, JitPortfolioResultDataObject jitPortfolioResult)
			throws FileNotFoundException {
		logger.debug("Thread-{} : Mapping the results...", Thread.currentThread().getId());
		JitResponseMapper responseMapper = JitResponseMapper.getMapper(data.getModelId());
		if (responseMapper == null) {
			throw new NoMappingAvailableException("JitResponseMapper in domain", data.getModelId());
		}

		FeedbackResponseModel feedbackResponseModel = responseMapper
				.processFeedbackResults(data.getAuthorisationData().getStudentHash(), data.getModelId(), jitFeedbackResults, jitPortfolioResult, data.getLanguageCode());
		logger.debug("Thread-{} : Results mapped", Thread.currentThread().getId());
		return feedbackResponseModel;
	}

	private void storeSupervisorFeedback(List<SupervisorFeedbackDataObject> supervisorFeedbackList) {
		logger.debug("Thread-{} : Saving supervisor feedback", Thread.currentThread().getId());
		supervisorFeedbackList.stream().forEach(supervisorFeedbackAdapter::insertDataObject);
		logger.debug("Thread-{} : Supervisor feedback saved", Thread.currentThread().getId());
	}

	private void storeStudentStats(StudentStatsDataObject studentStats) {
		logger.debug("Thread-{} : Saving student stats", Thread.currentThread().getId());
		if (studentStats.getId() == null) {
			studentStatsAdapter.insertDataObject(studentStats);
		} else {
			studentStatsAdapter.updateDataObject(studentStats);
		}
		logger.debug("Thread-{} : Student stats saved", Thread.currentThread().getId());
	}

	/*
		Validation
	 */

	private void validate(TPost<SubmissionModel> data) {
		validateDomain(data.getModelId());
		if (data.getContent() != null) {
			for (SubmissionModel submissionModel : data.getContent()) {
				validate(submissionModel);
			}
		}
	}

	private void validate(SubmissionModel submissionModel) {
		if (submissionModel.getForm() == null || submissionModel.getForm().getId() == null) {
			throw new ValidationException("The form id is required");
		}
		if (submissionModel.getEPAs() == null || submissionModel.getEPAs().size() == 0) {
			throw new ValidationException("No EPAs supplied");
		}
		if (submissionModel.getAnswers() == null || submissionModel.getAnswers().size() == 0) {
			throw new ValidationException("No answers supplied");
		}
	}

	private void validate(TDelete data) {
		validateDomain(data.getModelId());
	}

	private void validateDomain(String domainId) {
		if (!ApplicationSettings.isDomainSupported(domainId)) {
			throw new DomainNotFoundException(domainId);
		}
	}

	public CurrentPerformanceResponseModel processCurrentPerformanceQuery(CurrentPerformanceQueryModel queryModel) throws Exception {
		logger.debug("Thread-{} : Start processing query...", Thread.currentThread().getId());

		VizQueryResultDataObject vizQueryResultDataObject = getVizCurrentPerformanceResult(queryModel.getModelId(), queryModel.getAuthorisationData().getStudentHash());
		HashMap<String, Double> epaCohortLevel = new HashMap<>();
		if (vizQueryResultDataObject != null && vizQueryResultDataObject.getPredictedLevels() != null) {
			List<String> epaUnbIds = vizQueryResultDataObject.getPredictedLevels().stream().map(VizPredictedLevelDataObject::getEpaUnbId).distinct().collect(Collectors.toList());
			StudentSnapshotsAdapter studentSnapshotsAdapter = new StudentSnapshotsAdapter();
			List<StudentStatsDataObject> stats = studentStatsAdapter.getStatsExcept(queryModel.getModelId(), queryModel.getAuthorisationData().getStudentHash());
			epaUnbIds.stream().forEach(epaUnbId -> {
				String cohort = studentStatsAdapter.getCohort(queryModel.getModelId(), queryModel.getAuthorisationData().getStudentHash(), epaUnbId);
				if (cohort == null) {
					logger.info("Thread-{} : No cohort found for model {}, student {} and epa {}", Thread.currentThread().getId(), queryModel.getModelId(),
							queryModel.getAuthorisationData().getStudentHash(), epaUnbId);
				} else {
					List<String> studentsInCohort = stats.stream().map(stat -> stat.hasEpaWithCohort(epaUnbId, cohort) ? stat.getStudentId() : null).filter(studentHash -> studentHash != null)
							.collect(Collectors.toList());
					if (studentsInCohort.isEmpty()) {
						logger.info("Thread-{} : No students in cohort {} for epa {}", Thread.currentThread().getId(), cohort, epaUnbId);
					} else {
						OptionalDouble optionalCohortAverage = studentsInCohort.stream().mapToDouble(studentId -> {
							StudentSnapshotDataObject snapshot = studentSnapshotsAdapter.getDataObject(studentId, queryModel.getModelId());
							if (snapshot != null && snapshot.getCurrentSnapshot() != null && snapshot.getCurrentSnapshot().getVizQueryResult() != null
									&& snapshot.getCurrentSnapshot().getVizQueryResult().getPredictedLevels() != null) {
								OptionalDouble optionalAverage = snapshot.getCurrentSnapshot().getVizQueryResult().getPredictedLevels().stream().filter(level -> level.getEpaUnbId().equals(epaUnbId))
										.mapToInt(
												level -> getManager(queryModel.getModelId()).getFormMapper().getPerformanceIndicatorLevelMapper().getMappedFieldFromUnb(level.getLevelUnbId())
														.getRank()).average();
								return optionalAverage.isPresent() ? optionalAverage.getAsDouble() : 0;
							}
							return 0;
						}).average();
						if (optionalCohortAverage.isPresent()) {
							epaCohortLevel.put(epaUnbId, optionalCohortAverage.getAsDouble());
						}
					}
				}
			});
		}
		return mapResult(queryModel, vizQueryResultDataObject, epaCohortLevel);
	}

	public TimelineResponseModel processQuery(TimelineQueryModel timelineQueryModel) throws Exception {
		logger.debug("Thread-{} : Start processing query...", Thread.currentThread().getId());
		TimelineResponseModel responseModel;
		Converter<TimelineQueryModel, TimelineQueryDataObject> vizQueryConverter = getVizTimelineQueryConverter();
		Converter<TimelineResponseModel, TimelineQueryResponse> vizQueryResponseConverter = getVizTimelineQueryResponseConverter();

		TimelineQueryDataObject queryDataObject = vizQueryConverter.toDataObject(timelineQueryModel);
		TimelineQueryResponse cachedResponse = getCachedResponse(timelineQueryModel);
		if (cachedResponse == null) {
			StudentSnapshotDataObject studentSnapshotDataObject = new StudentSnapshotsAdapter()
					.getDataObject(timelineQueryModel.getAuthorisationData().getStudentHash(), timelineQueryModel.getModelId());
			responseModel = mapTimelineResponse(timelineQueryModel, studentSnapshotDataObject == null ? null : studentSnapshotDataObject.getTimelineSnapshots());
			queryDataObject.setResponse(vizQueryResponseConverter.toDataObject(responseModel));
		} else {
			responseModel = vizQueryResponseConverter.fromDataObject(cachedResponse);
			queryDataObject.setResponse(cachedResponse);
		}
		queryDataObject.setDateSubmitted(Instant.now(Clock.systemUTC()));
		updateQuery(queryDataObject, timelineQueryModel.getAuthorisationData().getStudentHash(), timelineQueryModel.getModelId(), timelineQueryModel.getEpaId(), timelineQueryModel.getLanguageCode());
		return responseModel;
	}

	public SupervisorResponseModel processQuery(SupervisorQueryModel supervisorQueryModel) throws Exception {
		logger.debug("Thread-{} : Start processing query...", Thread.currentThread().getId());

		SupervisorResponseModel responseModel = new SupervisorResponseModel();
		responseModel.setUpdatedInstant(Instant.now(Clock.systemUTC()));
		List<SupervisorResponseStudentModel> students = getStudentsData(supervisorQueryModel.getStudents(), supervisorQueryModel.getModelId());
		responseModel.setStudents(students);
		return responseModel;
	}

	private List<SupervisorResponseStudentModel> getStudentsData(List<SupervisorQueryStudentModel> students, String modelId) {
		if (students == null) {
			return null;
		}
		List<SupervisorResponseStudentModel> result = new ArrayList<>(students.size());
		for (SupervisorQueryStudentModel queryStudent : students) {
			StudentSnapshotDataObject studentSnapshotDataObject = new StudentSnapshotsAdapter().getDataObject(queryStudent.getStudentHash(), modelId);
			SupervisorResponseStudentModel student = mapResult(queryStudent, modelId, studentSnapshotDataObject);
			result.add(student);
		}
		return result;
	}

	private SupervisorResponseStudentModel mapResult(SupervisorQueryStudentModel queryStudent, String modelId, StudentSnapshotDataObject studentSnapshotDataObject) {
		logger.debug("Thread-{} : Mapping the results...", Thread.currentThread().getId());
		SupervisorResponseStudentModel responseModel = VizResponseMapper.map(queryStudent, modelId, studentSnapshotDataObject);
		logger.debug("Thread-{} : Results mapped", Thread.currentThread().getId());
		return responseModel;
	}

	private TimelineResponseModel mapTimelineResponse(TimelineQueryModel timelineQueryModel, List<TimelineSnapshotDataObject> timelineSnapshots) {
		logger.debug("Thread-{} : Mapping the results...", Thread.currentThread().getId());
		TimelineResponseModel responseModel = VizResponseMapper
				.mapToTimelineResponse(timelineQueryModel.getModelId(), timelineQueryModel.getEpaIdsForFiltering(), timelineQueryModel.getLanguageCode(), timelineSnapshots);
		logger.debug("Thread-{} : Results mapped", Thread.currentThread().getId());
		return responseModel;
	}

	private TimelineQueryResponse getCachedResponse(TimelineQueryModel queryModel) {
		TimelineQueryResponse cachedResponse = null;
		if (ApplicationSettings.isVizResponseCachingEnabled()) {
			TimelineQueryDataObject existingQueryDataObject = getExistingTimelineQuery(queryModel.getAuthorisationData().getStudentHash(), queryModel.getModelId(), queryModel.getEpaId(),
					queryModel.getLanguageCode());
			if (existingQueryDataObject != null && existingQueryDataObject.getDateSubmitted() != null && existingQueryDataObject.getResponse() != null) {
				Instant lastModelImageTimestamp = new StudentsAdapter().getModelImageTimestamp(queryModel.getAuthorisationData().getStudentHash());
				if (lastModelImageTimestamp != null && lastModelImageTimestamp.isBefore(existingQueryDataObject.getDateSubmitted())) {
					cachedResponse = existingQueryDataObject.getResponse();
				}
			}
		}
		return cachedResponse;
	}

	private TimelineQueryDataObject getExistingTimelineQuery(String studentId, String modelId, String epaId, LanguageCode languageCode) {
		VizQueriesDataService dataService = new VizQueriesDataService();
		return dataService.getExistingTimelineQuery(studentId, modelId, epaId, languageCode);
	}

	private VizQueryResultDataObject getVizCurrentPerformanceResult(String modelId, String studentId) throws Exception {
		StudentSnapshotDataObject studentSnapshot = new StudentSnapshotsAdapter().getDataObject(studentId, modelId);
		if (studentSnapshot == null || studentSnapshot.getCurrentSnapshot() == null)
			return null;
		return studentSnapshot.getCurrentSnapshot().getVizQueryResult();
	}

	private CurrentPerformanceResponseModel mapResult(CurrentPerformanceQueryModel queryModel, VizQueryResultDataObject vizQueryResultDataObject, HashMap<String, Double> epaCohortLevel) {
		logger.debug("Thread-{} : Mapping the results...", Thread.currentThread().getId());
		CurrentPerformanceResponseModel responseModel = VizResponseMapper.mapToCurrentPerformanceResult(queryModel, vizQueryResultDataObject, epaCohortLevel);
		logger.debug("Thread-{} : Results mapped", Thread.currentThread().getId());
		return responseModel;
	}

	private void updateQuery(TimelineQueryDataObject queryDataObject, String studentId, String modelId, String epaId, LanguageCode languageCode) {
		logger.debug("Thread-{} : Saving query...", Thread.currentThread().getId());
		VizQueriesDataService dataService = new VizQueriesDataService();
		int noAffectedRecords = dataService.updateTimelineQuery(queryDataObject, studentId, modelId, epaId, languageCode);
		logger.debug("Thread-{} : Query stored in the database (noAffectedRecords = {})", Thread.currentThread().getId(), noAffectedRecords);
	}

	private static Long parseEpassDate(String date) {
		LocalDate localDate = LocalDate.parse(date);
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return instant.toEpochMilli();
	}

	private void storeBayesianStudentStats(String studentId, ProcessedSubmission submission) {
		logger.debug("Thread-{} : Saving Student Stats", Thread.currentThread().getId());

		HashMap<String, StudentScore> epaPIscores = new HashMap<>();
		long submissionDate = parseEpassDate(submission.getSubmissionDate());
		if (submission.getProcessedEpas() != null) {
			for (ProcessedEpa processedEpa : submission.getProcessedEpas()) {
				if (processedEpa.getProcessedPis() != null) {
					processedEpa.getProcessedPis().stream().filter(processedQuestion -> processedQuestion.getNumericOutputData() != null).forEach(processedQuestion -> {
						String key = processedEpa.getUnbId() + processedQuestion.getUnbId();
						addNewScore(epaPIscores, key, (double) processedQuestion.getNumericOutputData().getLevelRank(), submissionDate);
					});
				}
				if (processedEpa.getMeanRankNumericOutputData() != null) {
					addNewScore(epaPIscores, processedEpa.getUnbId(), (double) processedEpa.getMeanRankNumericOutputData().getLevelRank(), submissionDate);
				}
			}
		}

		Set<String> Epas = new HashSet<>();
		Set<String> EpaPIs = new HashSet<>();
		if (submission.getProcessedEpas() != null) {
			for (ProcessedEpa processedEpa : submission.getProcessedEpas()) {
				if (processedEpa.getProcessedPis() != null) {
					for (ProcessedPi processedQuestion : processedEpa.getProcessedPis()) {
						StudentBayesianStatsAdapter studentAdapter = new StudentBayesianStatsAdapter();
						String key = processedEpa.getUnbId();
						if (!Epas.contains(key)) {
							Epas.add(key);
							StudentBayesianStatsDataObject studentEpaStats = studentAdapter.getStats(studentId, processedEpa.getUnbId(), "*");
							if (studentEpaStats == null) {
								studentEpaStats = new StudentBayesianStatsDataObject(studentId, processedEpa.getUnbId(), "*");
							}
							StudentScore tmp = epaPIscores.get(key);

							if (tmp != null) {
								studentEpaStats.addScore(tmp);
								StudentRole role = new StudentRole();

								role.setRole(submission.getRole());
								role.setTime(tmp.getTime());
								StudentAssessment assessment = new StudentAssessment();
								assessment.setFormId(submission.getFormId());
								assessment.setTime(tmp.getTime());
								studentEpaStats.addAssessment(assessment);
								studentEpaStats.addRole(role);

								if (processedQuestion.getNlpOutputData() != null) {
									if (processedQuestion.getNlpOutputData().getFeedbackLevel() != null) {
										StudentSentiment sentiment = new StudentSentiment();
										sentiment.setSentiment(processedQuestion.getNlpOutputData().getFeedbackLevel().toValue());
										sentiment.setTime(tmp.getTime());
										studentEpaStats.addSentiment(sentiment);
									}
								}

							}

							studentEpaStats.newData();
							studentEpaStats.sortLists();
							studentAdapter.updateDataObject(studentEpaStats);
						}
						String keyEpaPi = processedEpa.getUnbId() + processedQuestion.getUnbId();
						if (!EpaPIs.contains(keyEpaPi)) {

							StudentBayesianStatsDataObject studentStats = studentAdapter.getStats(studentId, processedEpa.getUnbId(), processedQuestion.getUnbId());
							if (studentStats == null) {
								studentStats = new StudentBayesianStatsDataObject(studentId, processedEpa.getUnbId(), processedQuestion.getUnbId());
							}
							EpaPIs.add(keyEpaPi);
							StudentScore tmp = epaPIscores.get(keyEpaPi);

							if (tmp != null) {
								studentStats.addScore(tmp);
								StudentRole role = new StudentRole();
								role.setRole(submission.getRole());
								role.setTime(tmp.getTime());
								StudentAssessment assessment = new StudentAssessment();
								assessment.setFormId(submission.getFormId());
								assessment.setTime(tmp.getTime());
								studentStats.addAssessment(assessment);
								studentStats.addRole(role);
							}
							studentStats.newData();
							studentStats.sortLists();
							studentAdapter.updateDataObject(studentStats);
						}
					}
				}
			}
		}
	}

	private void addNewScore(HashMap<String, StudentScore> epaPIscores, String key, double score, long time) {
		StudentScore tmpScore = new StudentScore();
		tmpScore.setTime(time);
		tmpScore.setScore(score);
		epaPIscores.put(key, tmpScore);
	}

	public SupervisedStudentPortfolioModel processJitSupervisorQuery(String studentId, String modelId, LanguageCode languageCode) throws FileNotFoundException {
		StudentSnapshotDataObject studentSnapshotDataObject = new StudentSnapshotsAdapter().getDataObject(studentId, modelId);
		JitPortfolioResultDataObject jitPortfolioResult = getJitPortfolioResult(studentSnapshotDataObject);
		if (jitPortfolioResult == null) {
			return null;
		}
		return mapResult(jitPortfolioResult, modelId, languageCode);
	}

	private JitPortfolioResultDataObject getJitPortfolioResult(StudentSnapshotDataObject studentSnapshotDataObject) {
		if (studentSnapshotDataObject == null || studentSnapshotDataObject.getCurrentSnapshot() == null || studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult() == null
				|| studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getPortfolioResult() == null)
			return null;
		return studentSnapshotDataObject.getCurrentSnapshot().getJitQueryResult().getPortfolioResult();
	}

	private SupervisedStudentPortfolioModel mapResult(JitPortfolioResultDataObject jitPortfolioResult, String modelId, LanguageCode languageCode)
			throws FileNotFoundException {
		logger.debug("Thread-{} : Mapping the results...", Thread.currentThread().getId());
		JitResponseMapper responseMapper = JitResponseMapper.getMapper(modelId);
		if (responseMapper == null) {
			throw new NoMappingAvailableException("JitResponseMapper in domain", modelId);
		}

		SupervisedStudentPortfolioModel portfolioModel = responseMapper.processPortfolioResults(jitPortfolioResult, languageCode);
		logger.debug("Thread-{} : Results mapped", Thread.currentThread().getId());
		return portfolioModel;
	}

	public SupervisedStudentPortfolioModel generateMockData(Random random, String modelId, LanguageCode languageCode) throws FileNotFoundException {
		String feedbackSeekingStrategyUnbId = random.nextBoolean() ? "fbseeking_poor" : null;
		String portfolioConsistencyUnbId = random.nextBoolean() ? "inconsistencyhigh" : null;
		List<String> epaNeedingInformationUnbIds = random.nextBoolean() ? randomEpas(random, StaticModelManager.getManager(modelId).getFormMapper().getEpaList()) : null;
		String frustrationUnbId = random.nextBoolean() ? "frustrationhigh" : null;
		JitPortfolioResultDataObject jitPortfolioResult = new JitPortfolioResultDataObject(feedbackSeekingStrategyUnbId, portfolioConsistencyUnbId, epaNeedingInformationUnbIds, frustrationUnbId);
		return mapResult(jitPortfolioResult, modelId, languageCode);
	}

	private List<String> randomEpas(Random random, Collection<EpaMappedField> epaList) {
		if (epaList == null)
			return null;
		List<String> result = new ArrayList<>(epaList.size());
		for (EpaMappedField epa : epaList) {
			if (random.nextBoolean()) {
				result.add(epa.getUnbId());
			}
		}
		if (result.isEmpty())
			return null;
		return result;
	}
}
