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
package eu.watchme.modules.dispatcher.jit;

import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.staticdata.model.form.json.MappedField;
import eu.watchme.modules.dataaccess.adapters.StudentTimeMapAdapter;
import eu.watchme.modules.dataaccess.model.SMData;
import eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject;
import eu.watchme.modules.dispatcher.viz.UnbSnapshotResult;
import eu.watchme.modules.unbbayes.BayesianStudentModel;
import eu.watchme.modules.unbbayes.model.queries.*;
import eu.watchme.modules.unbbayes.model.queries.results.FeedbackResult;
import eu.watchme.modules.unbbayes.model.queries.results.QueryResult;
import eu.watchme.modules.unbbayes.model.queries.results.SnapshotResult;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static eu.watchme.modules.commons.staticdata.StaticModelManager.getManager;
import static eu.watchme.modules.datamerger.StudentRepository.getInstance;
import static eu.watchme.modules.unbbayes.model.queries.results.FeedbackResultsInterpreter.feedbackInterpreter;
import static eu.watchme.modules.unbbayes.model.queries.results.MoreInformationNeededResultsInterpreter.moreInformationNeededResultsInterpreter;
import static eu.watchme.modules.unbbayes.model.queries.results.SingleValueResultsInterpreter.singleValueResultsInterpreter;
import static eu.watchme.modules.unbbayes.model.queries.results.SnapshotResultsInterpreter.snapshotInterpreter;
import static java.util.stream.Collectors.toList;

public class UnbQueryAdapter {

	private final static HashMap<String, UnbQueryAdapter> unbQueryAdapters = new HashMap<>();

	private String domainId;

	private UnbQueryAdapter(String domainId) throws FileNotFoundException {
		this.domainId = domainId;
	}

	public static UnbQueryAdapter getAdapter(String domainId) throws FileNotFoundException {
		synchronized (unbQueryAdapters) {
			if (!unbQueryAdapters.containsKey(domainId)) {
				unbQueryAdapters.put(domainId, new UnbQueryAdapter(domainId));
			}
			return unbQueryAdapters.get(domainId);
		}
	}

	public final UnbQueryResult queryAllEpasForJit(String studentId, SMData smData, HashSet<String> epasWithGeneralLevel) throws Exception {
		List<String> allEpaUnbIdsList = getManager(domainId).getFormMapper().getEpaList().stream().map(MappedField::getUnbId).collect(Collectors.toList());
		BayesianStudentModel bayesianStudentModel = getInstance().getObject(smData);
		StudentTimeMapDataObject bayesTime = new StudentTimeMapAdapter().getLastStudentTimeMap(studentId);
		List<FeedbackResult> feedbackResults = queryForFeedback(epasWithGeneralLevel, allEpaUnbIdsList, bayesianStudentModel, bayesTime);
		String feedbackSeekingStrategyResult = queryForFeedbackSeekingStrategy(bayesianStudentModel, bayesTime);
		String portfolioConsistencyResult = queryForPortfolioConsistency(bayesianStudentModel, bayesTime);
		String frustrationAlertResult = queryForFrustrationAlert(bayesianStudentModel, bayesTime);
		List<String> epaNeedingInformationUnbIds = queryForEpasNeedingInformation(bayesianStudentModel, bayesTime, allEpaUnbIdsList);
		return new UnbQueryResult(feedbackResults, feedbackSeekingStrategyResult, portfolioConsistencyResult, epaNeedingInformationUnbIds, frustrationAlertResult,
				bayesianStudentModel.getModelImageTimestamp());
	}

	private List<FeedbackResult> queryForFeedback(HashSet<String> epasWithGeneralLevel, List<String> allEpaUnbIdsList, BayesianStudentModel bayesianStudentModel, StudentTimeMapDataObject bayesTime)
			throws Exception {
		Query<FeedbackResult> query = new FeedbackForSpecificEpasAllPIAllFB(getAllPerformanceIndicatorUnbId(allEpaUnbIdsList), epasWithGeneralLevel, feedbackInterpreter, bayesTime);
		List<FeedbackResult> feedbackResults = new ArrayList<>();
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		feedbackResults.addAll(getFeedbackResultsFrom(query, queryResults));
		return feedbackResults;
	}

	private String queryForFeedbackSeekingStrategy(BayesianStudentModel bayesianStudentModel, StudentTimeMapDataObject bayesTime) throws Exception {
		Query<String> query = new FeedbackSeekingStrategy(singleValueResultsInterpreter, bayesTime);
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		return queryResults == null || queryResults.isEmpty() ? null : query.getResultsInterpreter().interpret(queryResults.get(0));
	}

	private String queryForPortfolioConsistency(BayesianStudentModel bayesianStudentModel, StudentTimeMapDataObject bayesTime) throws Exception {
		Query<String> query = new PortfolioConsistency(singleValueResultsInterpreter, bayesTime);
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		return queryResults == null || queryResults.isEmpty() ? null : query.getResultsInterpreter().interpret(queryResults.get(0));
	}

	private List<String> queryForEpasNeedingInformation(BayesianStudentModel bayesianStudentModel, StudentTimeMapDataObject bayesTime, List<String> allEpaUnbIdsList) throws Exception {
		Query<String> query = new MoreInformationNeeded(moreInformationNeededResultsInterpreter, allEpaUnbIdsList, bayesTime);
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		return queryResults == null ?
				null :
				queryResults.stream().map(queryResult -> query.getResultsInterpreter().interpret(queryResult)).filter(epaUnbId -> epaUnbId != null).collect(Collectors.toList());
	}

	private Map<String, List<String>> getAllPerformanceIndicatorUnbId(List<String> epaUnbList) {
		Collection<UnbKey> fields = getManager(domainId).getFormMapper().getPerformanceIndicators();

		Map<String, List<String>> result = new HashMap<>();

		for (String epa : epaUnbList) {
			List<String> res = fields.stream().filter(unbKey -> unbKey.getEpaId().equals(epa)).map(UnbKey::getUnbId).collect(Collectors.toList());
			result.put(epa, res);
		}
		return result;
	}

	private List<FeedbackResult> getFeedbackResultsFrom(Query<FeedbackResult> query, List<QueryResult> queryResults) {
		List<FeedbackResult> feedbackResults = new ArrayList<>(queryResults.size());
		for (QueryResult result : queryResults) {
			FeedbackResult feedback = query.getResultsInterpreter().interpret(result);
			feedbackResults.add(feedback);
		}
		return feedbackResults;
	}

	public UnbSnapshotResult queryForSnapshot(String studentId, BayesianStudentModel bayesianStudentModel, List<String> epaUnbIds) throws Exception {
		StudentTimeMapDataObject bayesTime = new StudentTimeMapAdapter().getLastStudentTimeMap(studentId);
		Query<SnapshotResult> query = new SnapshotQuery(getAllPerformanceIndicatorUnbId(epaUnbIds), snapshotInterpreter, bayesTime);
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		List<SnapshotResult> snapshotResults = getSnapshotResultsFrom(query, queryResults);
		return new UnbSnapshotResult(snapshotResults, bayesianStudentModel.getModelImageTimestamp());
	}

	private List<SnapshotResult> getSnapshotResultsFrom(Query<SnapshotResult> query, List<QueryResult> queryResults) {
		List<SnapshotResult> snapshotResults = new ArrayList<>(queryResults.size());
		for (QueryResult result : queryResults) {
			SnapshotResult snapshot = query.getResultsInterpreter().interpret(result);
			snapshotResults.add(snapshot);
		}
		return snapshotResults;
	}

	public UnbSnapshotResult queryAllEpasForViz(String studentId, SMData smData) throws Exception {
		List<String> epaUnbIds = getManager(domainId).getFormMapper().getEpaList().stream().map(MappedField::getUnbId).collect(toList());
		BayesianStudentModel bayesianStudentModel = getInstance().getObject(smData);
		return queryForSnapshot(studentId, bayesianStudentModel, epaUnbIds);
	}

	private String queryForFrustrationAlert(BayesianStudentModel bayesianStudentModel, StudentTimeMapDataObject bayesTime) throws Exception {
		Query<String> query = new FrustrationAlert(singleValueResultsInterpreter, bayesTime);
		List<QueryResult> queryResults = bayesianStudentModel.executeQueryList(query);
		return queryResults == null || queryResults.isEmpty() ? null : query.getResultsInterpreter().interpret(queryResults.get(0));
	}
}
