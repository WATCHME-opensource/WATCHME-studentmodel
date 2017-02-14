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

import eu.watchme.modules.domainmodel.exceptions.BayesianModelException;
import eu.watchme.modules.unbbayes.entities.LabelTaskEntity;
import eu.watchme.modules.unbbayes.model.queries.QueryItem;
import eu.watchme.modules.unbbayes.utils.FileUtils;
import unbbayes.TextModeRunner;
import unbbayes.TextModeRunner.QueryNodeNameAndArguments;
import unbbayes.io.mebn.UbfIO2;
import unbbayes.io.mebn.owlapi.OWLAPICompatiblePROWL2IO;
import unbbayes.prs.Node;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.RandomVariableFinding;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.entity.BooleanStatesEntityContainer;
import unbbayes.prs.mebn.entity.Entity;
import unbbayes.prs.mebn.entity.ObjectEntityInstance;
import unbbayes.prs.mebn.entity.exception.TypeException;
import unbbayes.prs.mebn.kb.KnowledgeBase;
import unbbayes.prs.mebn.kb.powerloom.PowerLoomKB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnBBayesWrapper {
    private MultiEntityBayesianNetwork mNetwork;
    private KnowledgeBase mKnowledgeBase;
    private TextModeRunner mTxtUnbbayes;

    public UnBBayesWrapper(File mebnFilePath, File knowledgeFilePath) throws IOException {
        mTxtUnbbayes = new TextModeRunner();
        UbfIO2 ubf = UbfIO2.getInstance();
        ubf.setProwlIO(OWLAPICompatiblePROWL2IO.newInstance());
        mNetwork = ubf.loadMebn(mebnFilePath);

        PowerLoomKB knowledgeBase = PowerLoomKB.getNewInstanceKB();
        mKnowledgeBase = mTxtUnbbayes.createKnowledgeBase(knowledgeBase, mNetwork);
        mKnowledgeBase.loadModule(knowledgeFilePath, true); /* Error in library code - buffered reader opened but not closed causing knowledge file to be locked */
        mKnowledgeBase = mTxtUnbbayes.fillFindings(mNetwork, mKnowledgeBase);
    }

    private void addEntity(String nodeName, String[] nodeArguments, Entity entity) {
        ResidentNode residentNode = mNetwork.getDomainResidentNode(nodeName);
        ObjectEntityInstance[] arguments = getArgumentsFrom(nodeArguments);

        boolean stateUpdateHandled = updateStateIfRequired(nodeArguments, entity, residentNode, arguments);
        if (!stateUpdateHandled) {
            insertNewFinding(entity, residentNode, arguments);
        }

        //TODO Not sure what is the purpose of the method fillFindings. I've removed this because it
        // causes the knowledge base not to be updated correctly
        // mKnowledgeBase = mTxtUnbbayes.fillFindings(mNetwork, mKnowledgeBase);
    }

    private ObjectEntityInstance[] getArgumentsFrom(String[] nodeArguments) {
        ObjectEntityInstance[] arguments = new ObjectEntityInstance[nodeArguments.length];
        for (int i = 0; i < nodeArguments.length; i++) {
            arguments[i] = mNetwork.getObjectEntityContainer().getEntityInstanceByName(nodeArguments[i]);
            if (arguments[i] == null) {
                //todo; report invalid mapping in the FormMapping data
                return null;
            }
        }
        return arguments;
    }

    private void insertNewFinding(Entity entity, ResidentNode residentNode, ObjectEntityInstance[] arguments) {
        residentNode.addRandomVariableFinding(new RandomVariableFinding(residentNode, arguments, entity, mNetwork));
        RandomVariableFinding randomVariableFinding = new RandomVariableFinding(residentNode, arguments, entity,
                                                                                mNetwork);
        mKnowledgeBase.insertRandomVariableFinding(randomVariableFinding);
    }

    private boolean updateStateIfRequired(String[] nodeArguments, Entity entity, ResidentNode residentNode,
                                          ObjectEntityInstance[] arguments) {
        boolean foundOldValue = false;
        for (RandomVariableFinding randomVariableFinding : residentNode.getRandomVariableFindingList()) {

            int index = 0;
            boolean matched = true;

            for (ObjectEntityInstance objectEntityInstance : randomVariableFinding.getArguments()) {
                if (!objectEntityInstance.getName().equals(nodeArguments[index])) {
                    matched = false;
                    break;
                }

                index++;
            }

            if (matched) {
                foundOldValue = true;
                residentNode.getRandomVariableFindingList().remove(randomVariableFinding);
                residentNode
                        .addRandomVariableFinding(new RandomVariableFinding(residentNode, arguments, entity, mNetwork));
                break;
            }
        }
        return foundOldValue;
    }
    public List<QueryItem> validateArgs(List<QueryItem> queryItems){
        List<QueryItem> q = new ArrayList<>();
        for(QueryItem el:queryItems){
            List<String> args = el.getNodeArguments();
            boolean valid =true;
                for(String arg:args){
                    if(arg.equalsIgnoreCase("*") || arg.equalsIgnoreCase("~"))
                        valid =false;
                }
            if(valid)
            {
                q.add(el);
            }
        }
        return q;
    }
    public ProbabilisticNetwork query(List<QueryItem> queryItems) throws Exception {
        queryItems = validateArgs(queryItems);
        List<QueryNodeNameAndArguments> queries =
                queryItems.stream().map(item -> mTxtUnbbayes.new QueryNodeNameAndArguments(item.getNodeName(), item.getNodeArgumentArray()))
                          .collect(Collectors.toList());

        return mTxtUnbbayes.callLaskeyAlgorithm(mNetwork, mKnowledgeBase, queries);
    }

    public boolean isTheQueriedNodeFullName(QueryItem item, Node node) {
        return node.getName().equalsIgnoreCase(item.getNodeNameOutFull());
    }

    public String getNodeName(Node node) {
        return node.getName().substring(0, node.getName().indexOf('_'));
    }
    public String getTime(Node node) {
        String[] tokens = node.getName().split("_");
        String result = "*";
        if(tokens.length>1)
            if(tokens[tokens.length-1].matches("[a-zA-z]{1}[0-9]+"))
               result = tokens[tokens.length-1];

        return result;
    }

    public void addBooleanEntityFinding(String nodeName, String[] nodeArguments, boolean value) throws TypeException {
        Entity entity = value ? new BooleanStatesEntityContainer()
                                        .getTrueStateEntity() : new BooleanStatesEntityContainer()
                                                                        .getFalseStateEntity();
        addEntity(nodeName, nodeArguments, entity);
    }

    public void addNominalEntityFinding(String nodeName, String[] nodeArguments, String value) throws TypeException {
        Entity entity = new LabelTaskEntity(value);
        addEntity(nodeName, nodeArguments, entity);
    }

    public String saveModel() throws BayesianModelException, IOException {
        UbfIO2 ubf = UbfIO2.getInstance();
        ubf.setProwlIO(OWLAPICompatiblePROWL2IO.newInstance());
        File tempFile = File.createTempFile("tempKnowledgeFile", ".tmp");
        mTxtUnbbayes.saveKnowledgeBase(tempFile, mKnowledgeBase, mNetwork);
        String knowledge = FileUtils.readFileToString(tempFile.getAbsolutePath());
        FileUtils.deleteFile(tempFile.getAbsolutePath());
        return knowledge;
    }
}
