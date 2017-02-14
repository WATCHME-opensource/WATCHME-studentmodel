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

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.commons.logging.LoggingUtils;
import eu.watchme.modules.dataaccess.adapters.StudentBayesianStatsAdapter;
import eu.watchme.modules.dataaccess.adapters.StudentSnapshotsAdapter;
import eu.watchme.modules.dataaccess.adapters.StudentTimeMapAdapter;
import eu.watchme.modules.dataaccess.adapters.StudentsAdapter;
import eu.watchme.modules.dataaccess.model.SMData;
import eu.watchme.modules.dataaccess.model.StudentBayesianStatsDataObject;
import eu.watchme.modules.dataaccess.model.StudentDataObject;
import eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject;
import eu.watchme.modules.dataaccess.model.snapshots.*;
import eu.watchme.modules.ndp.datatypes.StudentAssessment;
import eu.watchme.modules.ndp.datatypes.StudentRole;
import eu.watchme.modules.ndp.datatypes.StudentSentiment;
import eu.watchme.modules.ndp.functions.Auxiliaries;
import eu.watchme.modules.unbbayes.BayesianStudentModel;
import eu.watchme.modules.unbbayes.model.EntityType;
import eu.watchme.modules.unbbayes.model.findings.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unbbayes.prs.mebn.entity.exception.TypeException;

import java.time.Instant;
import java.util.*;

import static eu.watchme.modules.datamerger.StudentRepository.getInstance;

public class DataMerger {
	public static final DataMerger dataMerger = new DataMerger();
	private Logger logger = LoggerFactory.getLogger(DataMerger.class);

	private DataMerger() {
	}

	public SMData getSmData(String studentId, String domainId, int pId) {
		return getInstance().getSMData(studentId, domainId, pId);
	}

	public BayesianStudentModel addFindings(String studentId, String modelId, SMData smData, List<BayesianNumericalFinding> bayesianNumericalFindings, List<BayesianNlpFinding> bayesianNlpFindings,
			StudentTimeMapDataObject bayesTime)
			throws TypeException {
		logger.debug("Thread-{} : Start merging data...", Thread.currentThread().getId());
		List<Finding> findings = getFindings(bayesianNumericalFindings, bayesianNlpFindings, bayesTime);
		List<Finding> findingsAux = getAuxFindings(studentId, bayesTime);
		if (!findingsAux.isEmpty())
			findings.addAll(findingsAux);
		if (!findings.isEmpty()) {
			BayesianStudentModel bayesianStudentModel = getInstance().getObject(smData);
			bayesianStudentModel.addFindings(findings);
			return bayesianStudentModel;
		}
		logger.debug("Thread-{} : Data merged", Thread.currentThread().getId());
		return null;
	}

	private List<Finding> getFindings(List<BayesianNumericalFinding> bayesianNumericalFindings, List<BayesianNlpFinding> bayesianNlpFindings, StudentTimeMapDataObject bayesTime) {
		List<Finding> findingsFromNumericalData = new ArrayList<>();
		if (bayesianNumericalFindings != null) {
			for (BayesianNumericalFinding bayesianNumericalFinding : bayesianNumericalFindings) {
				findingsFromNumericalData.addAll(getFindingsFrom(bayesianNumericalFinding, bayesTime));
			}
		}
		List<Finding> findingsFromTextData = new ArrayList<>();
		if (bayesianNlpFindings != null) {
			for (BayesianNlpFinding bayesianNlpFinding : bayesianNlpFindings) {
				findingsFromTextData.addAll(getFindingsFrom(bayesianNlpFinding, bayesTime));
			}
		}
		List<Finding> findings = new ArrayList<>(findingsFromNumericalData.size() + findingsFromTextData.size());
		findings.addAll(findingsFromNumericalData);
		findings.addAll(findingsFromTextData);
		return findings;
	}

	public void saveStudentKnowledge(String studentId, String knowledge, Instant timestamp, int profileId) {
		logger.debug("Thread-{} : Saving to database...", Thread.currentThread().getId());
		StudentsAdapter studentsAdapter = new StudentsAdapter();
		StudentDataObject studentDataObject = studentsAdapter.getDataObject(studentId);
		studentDataObject.setStudentKnowledge(knowledge);
		studentDataObject.setModelImageTimestamp(timestamp);
		studentDataObject.setProfileId(profileId);
		studentsAdapter.updateDataObject(studentDataObject);
		logger.debug("Thread-{} : Student knowledge stored in the database", Thread.currentThread().getId());
	}

	private List<Finding> getFindingsFrom(BayesianNumericalFinding bayesianNumericalFinding, StudentTimeMapDataObject bayesTime) {
		logger.debug("Thread-{} : Retrieving findings from numerical data...", Thread.currentThread().getId());
		List<Finding> findingsList = new LinkedList<>();
		if (bayesianNumericalFinding.getPiUnbId() != null) {
			findingsList.add(new GetLevel(bayesianNumericalFinding.getEpaUnbId(), bayesianNumericalFinding.getPiUnbId(), bayesTime.getUnbId(), bayesianNumericalFinding.getLevelUnbId()));
			// findingsList.add(new HasLevel(bayesianNumericalFinding.getEpaUnbId(), bayesianNumericalFinding.getPiUnbId()));
		} else {
			findingsList.add(new GetLevelEpa(bayesianNumericalFinding.getEpaUnbId(), bayesTime.getUnbId(), bayesianNumericalFinding.getLevelUnbId()));
			//findingsList.add(new HasLevelEpa(bayesianNumericalFinding.getEpaUnbId()));
		}
		logger.debug("Thread-{} : Retrieved findings:\n{}", Thread.currentThread().getId(), LoggingUtils.format(findingsList));
		return findingsList;
	}

	private List<Finding> getFindingsFrom(BayesianNlpFinding bayesianNlpFinding, StudentTimeMapDataObject bayesTime) {
		/** NLP TODO : add per PI feedback , update suppervisor level, **/
		logger.debug("Thread-{} : Retrieving findings from NLP data...", Thread.currentThread().getId());
		List<Finding> findingsList = new LinkedList<>();
		String value;
		String[] tokens = bayesianNlpFinding.getEpaUnbId().split("_");
		if (bayesianNlpFinding.getFeedbackLevel().equals(FeedbackLevel.positive)) {
			value = tokens[0] + "_" + tokens[1] + "_SP";
		} else if (bayesianNlpFinding.getFeedbackLevel().equals(FeedbackLevel.negative)) {
			value = tokens[0] + "_" + tokens[1] + "_SN";
		} else {
			value = tokens[0] + "_" + tokens[1] + "_SG";
		}
		findingsList.add(new GetSupervisorFeedbackEPA(bayesianNlpFinding.getEpaUnbId(), bayesTime.getUnbId(), value));
		findingsList.add(new HasSupervisorFeedbackEPA(bayesianNlpFinding.getEpaUnbId(), bayesTime.getUnbId()));
		logger.debug("Thread-{} : Retrieved findings:\n{}", Thread.currentThread().getId(), LoggingUtils.format(findingsList));
		return findingsList;
	}

	public void processLegacy(String studentId, String modelId)
			throws TypeException {
		// TODO the new model is quite different and we no longer have the list of UnbQueryResultDataObject
		/*
		logger.debug("Thread-{} : Start merging legacy data...", Thread.currentThread().getId());
		List<Finding> findingsList = new LinkedList<>();
		StudentTimeMapAdapter timeDateAdapter = new StudentTimeMapAdapter();
		SnapshotsAdapter snaps = new SnapshotsAdapter();
		StudentTimeMapDataObject dataObject = null;
		SnapshotDataObject snapshots = snaps.getDataObject(studentId, modelId);
		if(snapshots!=null) {
			for (UnbQueryResultDataObject result : snapshots.getUnbResults()) {

				dataObject = timeDateAdapter.getNextTimeElement(studentId, timeDateAdapter.getStudentProfileId(studentId), result.getModelTimestamp().toString());
				dataObject = timeDateAdapter.addTimeStep(dataObject);
				if(dataObject!=null)
					for (FeedbackResultDataObject feedback : result.getFeedbackResults()) {
						findingsList.add(new GetLevel(feedback.getEpa(),
								feedback.getPerformanceIndicator(),
								dataObject.getUnbId(),
								feedback.getLevel()));
						//findingsList.add(new HasLevel(feedback.getEpa(),feedback.getPerformanceIndicator()));
						findingsList.add(new GetLevelEpa(feedback.getEpa(), dataObject.getUnbId(),
								feedback.getLevel()));
						//	findingsList.add(new HasLevelEpa(feedback.getEpa()));
					}
				for (NlpResultDataObject nlpfeedback : result.getNlpResults()) {
					List<NlpFindings> nlpFindings = nlpfeedback.getNlpFindings();
					if( nlpFindings!=null){
						for(NlpFindings nlpFinding:nlpFindings){
							findingsList.add(new GetSupervisorFeedbackEPA(nlpFinding.getUnbEpaId(),dataObject.getUnbId(),nlpFinding.getUnbQuestionId()));
							findingsList.add(new HasSupervisorFeedbackEPA(nlpFinding.getUnbEpaId(),dataObject.getUnbId()));
						}
					}

				}

			}
		}

		if (!findingsList.isEmpty()) {
			BayesianStudentModel bayesianStudentModel = getInstance().getObject(studentId, modelId,dataObject.getProfileId());
			bayesianStudentModel.addFindings(findingsList);
			BayesianModelSnapshot bayesianModelSnapshot = bayesianStudentModel.saveModel();
			saveStudentKnowledge(studentId, bayesianModelSnapshot,dataObject.getProfileId());
		}
		logger.debug("Thread-{} : Lagacy Data merged", Thread.currentThread().getId());
		*/
	}

	public void mergeKnowledge(String studentId, String modelId, int pId, SMData smData) throws TypeException {
		logger.debug("Thread-{} : Start merging knowledge...", Thread.currentThread().getId());
		List<Finding> findingsList = new LinkedList<>();
		StudentTimeMapAdapter timeDateAdapter = new StudentTimeMapAdapter();
		StudentSnapshotsAdapter snaps = new StudentSnapshotsAdapter();
		StudentTimeMapDataObject dataObject = null;
		StudentSnapshotDataObject snapshots = snaps.getDataObject(studentId, modelId);
		if (snapshots != null) {
			List<TimelineSnapshotDataObject> orig = snapshots.getTimelineSnapshots();
			if (orig.size() > ApplicationSettings.getTimelineMergeGap()) {
				Integer startIdx = orig.size() - ApplicationSettings.getTimelineMergeGap();

				orig = orig.subList(startIdx, orig.size());
			}
			for (TimelineSnapshotDataObject result : orig) {
				if (result.getEpas() != null) {
					dataObject = timeDateAdapter.getNextTimeElement(studentId, pId, result.getSubmissionDate().toString());
					dataObject = timeDateAdapter.addTimeStep(dataObject);
					if (dataObject != null) {
						for (TimelineSnapshotEpaDataObject timelineEpa : result.getEpas()) {
							if (timelineEpa.getActualScoreUnbId() != null) {
								findingsList.add(new GetLevelEpa(timelineEpa.getUnbId(), dataObject.getUnbId(), timelineEpa.getActualScoreUnbId()));
							}
							if (timelineEpa.getPerformanceIndicators() != null) {
								for (TimelineSnapshotNumericalPiDataObject pi : timelineEpa.getPerformanceIndicators()) {
									findingsList.add(new GetLevel(timelineEpa.getUnbId(), pi.getUnbId(), dataObject.getUnbId(), pi.getActualScoreUnbId()));
								}
							}
							if (timelineEpa.getFeedbackIndicators() != null) {
								for (TimelineSnapshotTextPiDataObject pi : timelineEpa.getFeedbackIndicators()) {
									findingsList.add(new GetSupervisorFeedbackEPA(timelineEpa.getUnbId(), dataObject.getUnbId(), pi.getUnbId()));
									findingsList.add(new HasSupervisorFeedbackEPA(timelineEpa.getUnbId(), dataObject.getUnbId()));
								}
							}
						}
					}
				}
			}
		}

		if (!findingsList.isEmpty()) {
			BayesianStudentModel bayesianStudentModel = getInstance().getObject(smData);
			bayesianStudentModel.addFindings(findingsList);
			smData.setStudentKnowledge(bayesianStudentModel.getKnowledge());
			smData.setModelImageTimestamp(bayesianStudentModel.getModelImageTimestamp());
		}
		logger.debug("Thread-{} : Lagacy Data merged", Thread.currentThread().getId());
	}

	private List<Finding> getAuxFindings(String studentId, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		StudentBayesianStatsAdapter dataAdapter = new StudentBayesianStatsAdapter();
		StudentTimeMapAdapter time_map_adapter = new StudentTimeMapAdapter();

		List<StudentBayesianStatsDataObject> studentStats = dataAdapter.getStatsEpas(studentId); // loads EPA averages

		// 1) scoreDrop - function
		List<Finding> scoreDrop = processScoreDrop(dataAdapter, studentStats, bayesTime);
		if (!scoreDrop.isEmpty())
			findings.addAll(scoreDrop);
		// 2) usagePattern - function
		List<Finding> usagePattern = processUsageChange(studentId, time_map_adapter, dataAdapter, studentStats, bayesTime);
		if (!usagePattern.isEmpty())
			findings.addAll(usagePattern);
		// 3) mostVariation - function
		List<Finding> mostVariation = processMostVariation(dataAdapter, studentStats, bayesTime);
		if (!mostVariation.isEmpty())
			findings.addAll(mostVariation);
		// 4) usageConsistancy - function
		List<Finding> usageConsistancy = processUsageConsistency(dataAdapter, studentStats, bayesTime);
		if (!usageConsistancy.isEmpty())
			findings.addAll(usageConsistancy);
		// 5) scoreConsistency  -function
		List<Finding> scoreConsistancy = processScoreConsistancy(dataAdapter, studentStats, bayesTime);
		if (!scoreConsistancy.isEmpty())
			findings.addAll(scoreConsistancy);

		// Feedback seeking strategy
		List<Finding> feedbackSeeking = processFeedbackSeeking(dataAdapter, studentStats, bayesTime);
		if (!feedbackSeeking.isEmpty())
			findings.addAll(feedbackSeeking);

		// several form types
		List<Finding> severalForms = processAssessmentTypes(dataAdapter, studentStats, bayesTime);
		if (!severalForms.isEmpty())
			findings.addAll(severalForms);

		// mood change
		List<Finding> moodChange = processSentiments(dataAdapter, studentStats, bayesTime);
		if (!moodChange.isEmpty())
			findings.addAll(moodChange);

		return findings;
	}

	/*
	1) Procedure scoreDropped computes per time step in "scores" whether the score has dropped,
	 */
	private List<Finding> processScoreDrop(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		HashMap<String, Auxiliaries.DROPLEVEL> scoreDropResEpas = new HashMap<>();
		for (StudentBayesianStatsDataObject ssDO : studentStats) {
			String key = ssDO.getEpaId();
			double[] dataPoints = dataAdapter.getDataPoints(ssDO);
			int[] timePoints = dataAdapter.getDataTimePoints(ssDO);
			List<Auxiliaries.DROPLEVEL> resList = null;
			try {

				//	logger.info(" data points size {} -  time data {} - maxTime {} - minTime {}",dataPoints.length,timePoints.length,ssDO.getMaxTime(),ssDO.getMinTime());
				//	logger.info(" timePoints {}",timePoints);
				if (timePoints.length > 2)
					resList = Arrays.asList(Auxiliaries
							.scoreDropped(dataPoints, timePoints, ApplicationSettings.getNowWindowSize(), ApplicationSettings.getRecentWindowSize(), ApplicationSettings.getPlight(),
									ApplicationSettings.getPheavy()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resList != null) {
				int indx = dataAdapter.getNearestElement(bayesTime.getSubmissionDate(), ssDO);
				int adjIndx = dataAdapter.convertIdToRes(indx, timePoints.length, resList.size());
				//	logger.info( " indx {} adjIndx {} resSize {}",indx,adjIndx,resList.size());
				if (!resList.isEmpty())
					scoreDropResEpas.put(key, resList.get(adjIndx));
				//	logger.info(" Student Stats scoreDropped : {}  ", resList.toString());
			}
		}
		if (scoreDropResEpas.values() != null && scoreDropResEpas.values().size() > 0) {
			Auxiliaries.DROPLEVEL overalDrop = maximizeDrops(new ArrayList<>(scoreDropResEpas.values()));
			findings.add(createScoreDropFinding(overalDrop, bayesTime)); // adding score drop finding
		}
		return findings;
	}

	/*
	2) Procedue usageChange computes per timestep whether the usage (in terms of number of assessments per timepoint) has changed
	 */
	private List<Finding> processUsageChange(String studentId, StudentTimeMapAdapter time_map_adapter, StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats,
			StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();

		for (StudentBayesianStatsDataObject ssDO : studentStats) {
			String key = ssDO.getEpaId();
			boolean[] resList = null;
			int[] time_data = dataAdapter.getRealDataTimePoints(ssDO);
			try {
				resList = (Auxiliaries.usageChanged(time_data, ApplicationSettings.getNowWindowSize(), ApplicationSettings.getRecentWindowSize(), ApplicationSettings.getPlight()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resList != null) {
				int start = 0;
				for (int indx = 0; indx < resList.length; indx++) {
					int adjIndex = dataAdapter.convertIndxFromRes(indx, ssDO, start);
					start = adjIndex++;
					if (adjIndex < ssDO.getScores().size()) {
						long time = dataAdapter.getTimePoint(adjIndex, ssDO);

						//		logger.info( " indx {} adjIndx {} resSize {} time points {}",indx,adjIndex,resList.length,time_data.length);
						StudentTimeMapDataObject timeObject = time_map_adapter.searchForTimeElement(studentId, time);
						if (timeObject.getProfileId().equals(bayesTime.getProfileId())) {

							if (resList[indx]) {
								findings.add(new usageChanged(timeObject.getUnbId(), EntityType.BOOLEAN_TRUE));
								findings.add(new EvenUsagePattern(timeObject.getUnbId(), key, EntityType.BOOLEAN_TRUE));
							} else {
								findings.add(new usageChanged(timeObject.getUnbId(), EntityType.BOOLEAN_FALSE));
								findings.add(new EvenUsagePattern(timeObject.getUnbId(), key, EntityType.BOOLEAN_FALSE));
							}
						}
					}
				}

			}
		}

		return findings;
	}

	/*
	 3) mostVraiation - function

      */
	private List<Finding> processMostVariation(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		List<double[]> epa_scores = new ArrayList<double[]>();
		HashMap<Integer, String> epa_keys = new HashMap<Integer, String>();

		for (int indx = 0; indx < studentStats.size(); indx++) {
			StudentBayesianStatsDataObject ssDO = studentStats.get(indx);
			String key = ssDO.getEpaId();
			epa_keys.put(indx, key);
			epa_scores.add(dataAdapter.getDataPoints(ssDO));
		}
		int index = Auxiliaries.mostVariation(epa_scores, ApplicationSettings.getPlight());
		if (index > -1) {
			findings.add(new IsVariationHotspot(bayesTime.getUnbId(), epa_keys.get(index), EntityType.BOOLEAN_TRUE));
		}
		return findings;
	}

	/*
	4) usageConsistency - function
	 */
	private List<Finding> processUsageConsistency(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		List<int[]> epa_time = new ArrayList<int[]>();

		for (int indx = 0; indx < studentStats.size(); indx++) {
			StudentBayesianStatsDataObject ssDO = studentStats.get(indx);
			epa_time.add(dataAdapter.getRealDataTimePoints(ssDO));
		}
		boolean index = false;
		try {
			index = Auxiliaries.usageConsistency(epa_time, ApplicationSettings.getPlight(), ApplicationSettings.getBayesianIgnoreTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (index) {
			findings.add(new UsageIsConsistent(bayesTime.getUnbId(), EntityType.BOOLEAN_TRUE));
		} else {
			findings.add(new UsageIsConsistent(bayesTime.getUnbId(), EntityType.BOOLEAN_FALSE));
		}
		return findings;
	}

	/*
	5) scoreConsistancy - function
	 */
	private List<Finding> processScoreConsistancy(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		List<int[]> epa_time = new ArrayList<int[]>();
		List<double[]> epa_score = new ArrayList<double[]>();

		for (int indx = 0; indx < studentStats.size(); indx++) {
			StudentBayesianStatsDataObject ssDO = studentStats.get(indx);
			epa_time.add(dataAdapter.getRealDataTimePoints(ssDO));
			epa_score.add(dataAdapter.getDataPoints(ssDO));
		}
		boolean index = false;
		try {
			index = Auxiliaries.scoreConsistency(epa_score, epa_time, ApplicationSettings.getPlight(), ApplicationSettings.getBayesianIgnoreTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (index) {
			findings.add(new ScoreIsConsistent(bayesTime.getUnbId(), EntityType.BOOLEAN_TRUE));
		} else {
			findings.add(new ScoreIsConsistent(bayesTime.getUnbId(), EntityType.BOOLEAN_FALSE));
		}
		return findings;
	}

	private HashMap<String, Integer> aggregateRoles(List<StudentRole> roles, HashMap<String, Integer> aggr) {

		for (StudentRole role : roles) {
			String key = role.getRole();
			if (key != null) {
				if (aggr.containsKey(key)) {
					aggr.put(key, aggr.get(key) + 1);
				} else {
					aggr.put(key, 1);
				}
			}
		}
		return aggr;
	}

	private List<Finding> processFeedbackSeeking(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();

		int n = 1;
		for (int indx = 0; indx < studentStats.size(); indx++) {
			List<StudentRole> roles = studentStats.get(indx).getRoles();
			HashMap<String, Integer> role_set = new HashMap<>();

			if (!roles.isEmpty()) {
				role_set = aggregateRoles(roles, role_set);
			}
			if (role_set.size() > 0) {
				// has multiple roles
				if (role_set.keySet().size() > 0)
					findings.add(new SeveralAssessorRoles(bayesTime.getUnbId(), studentStats.get(indx).getEpaId(), EntityType.BOOLEAN_TRUE));
				// Staff
				if (role_set.containsKey("Staff")) {
					double ratio = (double) role_set.get("Staff") / (double) n;
					if (ratio < 0.5)
						findings.add(new hasFeedbackFromStaff(bayesTime.getUnbId(), "SOMEFEEDBACK"));
					else
						findings.add(new hasFeedbackFromStaff(bayesTime.getUnbId(), "MUCHFEEDBACK"));

				} else {
					findings.add(new hasFeedbackFromStaff(bayesTime.getUnbId(), "NOFEEDBACK"));
				}
				// Peers
				if (role_set.containsKey("Peers")) {
					double ratio = (double) role_set.get("Peers") / (double) n;
					if (ratio < 0.5)
						findings.add(new hasFeedbackFromPeers(bayesTime.getUnbId(), "SOMEFEEDBACK"));
					else
						findings.add(new hasFeedbackFromPeers(bayesTime.getUnbId(), "MUCHFEEDBACK"));
				} else {
					findings.add(new hasFeedbackFromPeers(bayesTime.getUnbId(), "NOFEEDBACK"));
				}
				// supervisor
				if (role_set.containsKey("Supervisor")) {
					double ratio = (double) role_set.get("Supervisor") / (double) n;
					if (ratio < 0.5)
						findings.add(new hasFeedbackFromSupervisor(bayesTime.getUnbId(), "SOMEFEEDBACK"));
					else
						findings.add(new hasFeedbackFromSupervisor(bayesTime.getUnbId(), "MUCHFEEDBACK"));
				} else {
					findings.add(new hasFeedbackFromSupervisor(bayesTime.getUnbId(), "NOFEEDBACK"));
				}
			} else {
				findings.add(new SeveralAssessorRoles(bayesTime.getUnbId(), studentStats.get(indx).getEpaId()));
			}
		}
		//if (n > 0) {}

		return findings;
	}

	private HashMap<String, Integer> aggregateAssessments(List<StudentAssessment> assessments, HashMap<String, Integer> aggr) {

		for (StudentAssessment assessment : assessments) {
			String key = String.valueOf(assessment.getFormId());
			if (key != null) {
				if (aggr.containsKey(key)) {
					aggr.put(key, aggr.get(key) + 1);
				} else {
					aggr.put(key, 1);
				}
			}
		}
		return aggr;
	}

	private List<Finding> processAssessmentTypes(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();

		for (int indx = 0; indx < studentStats.size(); indx++) {
			List<StudentAssessment> assessments = studentStats.get(indx).getAssessments();
			HashMap<String, Integer> assessment_set = new HashMap<>();

			if (!assessments.isEmpty()) {
				assessment_set = aggregateAssessments(assessments, assessment_set);
			}
			if (assessment_set.size() > 0) {
				findings.add(new SeveralAssessmentTypes(bayesTime.getUnbId(), studentStats.get(indx).getEpaId(), EntityType.BOOLEAN_TRUE));
			} else {
				findings.add(new SeveralAssessmentTypes(bayesTime.getUnbId(), studentStats.get(indx).getEpaId()));
			}
		}

		return findings;

	}

	private double aggregateSentiments(List<StudentSentiment> sentiments) {
		double res = 0;
		int total = 0;
		int pop = sentiments.size();
		for (StudentSentiment sentiment : sentiments) {
			if (sentiment.getSentiment().equalsIgnoreCase("positive")) {
				total += 1;
			} else if (sentiment.getSentiment().equalsIgnoreCase("negative")) {
				total -= 1;
			} else {
				total += 0;
			}
		}
		res = Math.round((double) total / (double) pop);
		return res;
	}

	private double moodChanged(double first, double second) {

		if (first > 0 && second <= 0)
			return 1;
		if (first < 0 && second >= 0)
			return 1;
		if (first == 0 && second != 0)
			return 1;

		//	return Math.abs(second-first);
		return 0;
	}

	private List<Finding> processSentiments(StudentBayesianStatsAdapter dataAdapter, List<StudentBayesianStatsDataObject> studentStats, StudentTimeMapDataObject bayesTime) {
		List<Finding> findings = new ArrayList<>();
		int popSize = studentStats.size();
		double scoreSentiment = 0;
		for (int indx = 0; indx < studentStats.size(); indx++) {
			List<StudentSentiment> sentiments = studentStats.get(indx).getSentiments(4);

			int sampleSize = sentiments.size();
			if (sampleSize > 3) {
				int halfWay = (int) Math.round(sampleSize * .8);

				List<StudentSentiment> firstHalf = sentiments.subList(0, halfWay);
				double fIndx = aggregateSentiments(firstHalf);
				List<StudentSentiment> secondHalf = sentiments.subList(halfWay, sampleSize);
				double sIndx = aggregateSentiments(secondHalf);
				double moodIndx = moodChanged(fIndx, sIndx);
				scoreSentiment += moodIndx;
				logger.info("  first half list {} \n second half list {} \n", firstHalf, secondHalf);
				logger.info(" first index {} \n second index {} \n", fIndx, sIndx);
			}
		}
		double ratio = scoreSentiment / (double) popSize;
		logger.info(" score {} \n", ratio);
		if (ratio > 0.5) {
			findings.add(new SupervisorMoodChanged(bayesTime.getUnbId(), EntityType.BOOLEAN_TRUE));
		} else {
			findings.add(new SupervisorMoodChanged(bayesTime.getUnbId(), EntityType.BOOLEAN_FALSE));
		}
		return findings;
	}

	private Auxiliaries.DROPLEVEL maximizeDrops(List<Auxiliaries.DROPLEVEL> vals) {
		for (Auxiliaries.DROPLEVEL eD : vals) {
			if (eD.equals(Auxiliaries.DROPLEVEL.SCORESTRONGLYDROPPED))
				return Auxiliaries.DROPLEVEL.SCORESTRONGLYDROPPED;
			if (eD.equals(Auxiliaries.DROPLEVEL.SCORELITTLEDROPPED))
				return Auxiliaries.DROPLEVEL.SCORELITTLEDROPPED;
		}
		return Auxiliaries.DROPLEVEL.SCORENOTDROPPED;
	}

	private Finding createScoreDropFinding(Auxiliaries.DROPLEVEL lvl, StudentTimeMapDataObject bayesTime) {
		if (lvl.equals(Auxiliaries.DROPLEVEL.SCORELITTLEDROPPED))
			return new scoreHasDropped(bayesTime.getUnbId(), "ScoreLittleDropped");
		if (lvl.equals(Auxiliaries.DROPLEVEL.SCORESTRONGLYDROPPED))
			return new scoreHasDropped(bayesTime.getUnbId(), "ScoreStronglyDropped");
		return new scoreHasDropped(bayesTime.getUnbId(), "ScoreNotDropped");
	}
}
