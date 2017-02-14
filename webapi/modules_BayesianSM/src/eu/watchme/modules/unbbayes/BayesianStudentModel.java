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

package eu.watchme.modules.unbbayes;

import com.google.gson.Gson;
import eu.watchme.modules.domainmodel.exceptions.BayesianModelException;
import eu.watchme.modules.unbbayes.model.findings.Finding;
import eu.watchme.modules.unbbayes.model.queries.Query;
import eu.watchme.modules.unbbayes.model.queries.QueryItem;
import eu.watchme.modules.unbbayes.model.queries.results.Evidence;
import eu.watchme.modules.unbbayes.model.queries.results.ProbableState;
import eu.watchme.modules.unbbayes.model.queries.results.QueryResult;
import eu.watchme.modules.unbbayes.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unbbayes.prs.Node;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.bn.TreeVariable;
import unbbayes.prs.mebn.entity.exception.TypeException;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BayesianStudentModel {
	private UnBBayesWrapper mUnBBayesWrapper;
	private Instant mModelImageTimestamp;
	private Logger logger = LoggerFactory.getLogger(BayesianStudentModel.class);

	private BayesianStudentModel(File mebnFilePath, File domainModelFilePath, String studentModel, Instant modelImageTimestamp) throws IOException {
		logger.debug("Thread-{} : Loading BayesianStudentModel...", Thread.currentThread().getId());
		File studentModelFile = File.createTempFile("student_model", ".tmp");
		if (studentModel == null) {
			logger.debug("Thread-{} : No knowledge available. Creating a copy of the domain model", Thread.currentThread().getId());
			FileUtils.createCopy(domainModelFilePath, studentModelFile);
			logger.debug("Thread-{} : Copy created", Thread.currentThread().getId());
		} else {
			FileUtils.writeStringToFile(studentModel, studentModelFile);
		}
		mModelImageTimestamp = modelImageTimestamp;
		mUnBBayesWrapper = new UnBBayesWrapper(mebnFilePath, studentModelFile);
		// FileUtils.deleteFile(studentModelFile.getAbsolutePath()); /* Copy of domain file is never successfully released here as is held by UnBBayesWrapper object */
		FileUtils.temps.computeIfPresent(studentModelFile.getAbsolutePath(), (k,v) -> !v);											  /* Mark temp file for deletion */
		logger.debug("Thread-{} : BayesianStudentModel loaded", Thread.currentThread().getId());
	}

	public static BayesianStudentModel load(File mebnPath, File domainKnowledgePath, String studentKnowledge, Instant modelImageTimestamp)
			throws IOException {
		return new BayesianStudentModel(mebnPath, domainKnowledgePath, studentKnowledge, modelImageTimestamp);
	}

	private List<QueryResult> getResults(List<QueryItem> queryItemList) throws Exception {
		logger.debug("Thread-{} : Query: {}", Thread.currentThread().getId(), new Gson().toJson(queryItemList));
		ProbabilisticNetwork probabilisticNetwork = mUnBBayesWrapper.query(queryItemList);

		List<Node> nodes = probabilisticNetwork.getNodes();
		List<QueryResult> list = new LinkedList<>();

		for (QueryItem queryItem : queryItemList) {
			nodes.stream().filter(node -> node instanceof ProbabilisticNode)
					.filter(node -> mUnBBayesWrapper.isTheQueriedNodeFullName(queryItem, node))
					.forEach(node -> {
						ProbableState state = getState((ProbabilisticNode) node);
						if (state != null) {
							list.add(new QueryResult(queryItem, state, node.getDescription()));
						}
					});
		}
		return list;
	}

	private ProbableState getState(ProbabilisticNode node) {
		ProbableState probableState = null;
		List<Evidence> evidences = getEvidences(node);
		if (!evidences.isEmpty()) {
			float maxStateProb = getMaxStateProbability(node);
			int count = 0;
			String state = null;
			for (int i = 0; i < node.getStatesSize(); i++) {
				if (node.getMarginalAt(i) == maxStateProb) {
					count++;
					state = node.getStateAt(i);
				}
			}
			if (count == 1) {
				probableState = new ProbableState(mUnBBayesWrapper.getNodeName(node), state, evidences);
			}
		}
		return probableState;
	}

	private float getMaxStateProbability(ProbabilisticNode node) {
		float maxStateProb = 0f;
		for (int i = 0; i < node.getStatesSize(); i++) {
			float prob = node.getMarginalAt(i);
			if (prob > maxStateProb) {
				maxStateProb = prob;
			}
		}
		return maxStateProb;
	}

	private List<Evidence> getEvidences(TreeVariable node) {
		List<Evidence> evidences = new ArrayList<>();
		if (node.hasEvidence()) {
			Evidence evidence = new Evidence(mUnBBayesWrapper.getNodeName(node),mUnBBayesWrapper.getTime(node), node.getStateAt(node.getEvidence()));
			evidences.add(evidence);
		}
		List<Node> parents = node.getParents();
		if (parents != null && !parents.isEmpty()) {
			for (Node parent : parents) {
				evidences.addAll(getEvidences((TreeVariable) parent));
			}
		}
		return evidences;
	}

	public synchronized List<QueryResult> executeQueryList(Query query) throws Exception {
		List<QueryItem> queryItems = query.getQueriesList();
		List<QueryResult> results = getResults(queryItems);

		return query.filterResults(results);
	}

	private synchronized void addFinding(Finding finding) throws TypeException {
		switch (finding.getEntityType()) {
		case BOOLEAN_TRUE:
			mUnBBayesWrapper.addBooleanEntityFinding(finding.getNodeName(), finding.getArguments(), true);
			break;
		case BOOLEAN_FALSE:
			mUnBBayesWrapper.addBooleanEntityFinding(finding.getNodeName(), finding.getArguments(), false);
			break;
		case NOMINAL:
		default:
			mUnBBayesWrapper
					.addNominalEntityFinding(finding.getNodeName(), finding.getArguments(), finding.getValue());
			break;
		}
	}

	public synchronized void addFindings(List<Finding> findings) throws TypeException {
		for (Finding finding : findings) {
			addFinding(finding);
		}
		mModelImageTimestamp = Instant.now(Clock.systemUTC());
	}

	/*
	public synchronized BayesianModelSnapshot saveModel() {
		logger.debug("Thread-{} : Saving knowledge base...", Thread.currentThread().getId());
		String model;
		try {
			model = mUnBBayesWrapper.saveModel();
		} catch (IOException e) {
			throw new BayesianModelException(e);
		}
		logger.debug("Thread-{} : Knowledge base saved", Thread.currentThread().getId());
		return new BayesianModelSnapshot(mModelImageTimestamp, model);
	}
	*/

	public String getKnowledge() {
		try {
			return mUnBBayesWrapper.saveModel();
		} catch (IOException e) {
			throw new BayesianModelException(e);
		}
	}

	public Instant getModelImageTimestamp() {
		return mModelImageTimestamp;
	}
}
