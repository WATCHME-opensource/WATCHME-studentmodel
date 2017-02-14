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

package eu.watchme.modules.nlp.model;

import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.enums.FeedbackLevel;
import eu.watchme.modules.nlp.datatypes.NLPFindingSet;

import java.util.List;
import java.util.Set;

public class NlpOutputField {
    private String unbEpaId;
    private UnbKey unbQuestionId;
    private String feedback;
    private FeedbackLevel feedbackLevel;
    private Set<String> linkedKeywords;
    private List<NLPFindingSet> findingList;
    private long creationDate;

    public NlpOutputField(String unbEpaId, UnbKey unbQuestionId, String feedback,
                          FeedbackLevel feedbackLevel, Set<String> linkedEpaNames, long creationDate, List<NLPFindingSet> findingList) {
        this.unbEpaId = unbEpaId;
        this.unbQuestionId = unbQuestionId;
        this.feedback = feedback;
        this.feedbackLevel = feedbackLevel;
        this.linkedKeywords = linkedEpaNames;
        this.creationDate = creationDate;
        this.findingList = findingList;
    }

    public String getUnbEpaId() {
        return unbEpaId;
    }

    public UnbKey getUnbQuestionId() {
        return unbQuestionId;
    }


    public String getFeedback() {
        return feedback;
    }

    public FeedbackLevel getFeedbackLevel() {
        return feedbackLevel;
    }

    public Set<String> getLinkedKeywords() {

        return linkedKeywords;
    }
    public  List<NLPFindingSet> getFindingList(){
        return this.findingList;
    }

    public long getCreationDate() {return creationDate;}
}
