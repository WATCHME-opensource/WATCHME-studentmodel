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

package eu.watchme.modules.commons.staticdata.model.recommendation.mapper;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.staticdata.model.recommendation.json.RecommendationMessage;
import eu.watchme.modules.commons.staticdata.model.recommendation.json.Recommendations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RubricMatrix {
    private String domainId;
    private static String WILDCARD_STR = "*";
    private static String WILDCARD_STR_PI = "~";
    /**
     * Rubric mapping of the student recommendations
     * 1st index - represents the Performance Indicator index
     * 2nd index - represents the Performance Indicator Level index
     * 3rd index - represents the language index
     */

    private Map<LanguageCode, Map<UnbKey, Map<String, String[]>>> rubric;

    private RubricMatrix(String domainId) {
        this.rubric = new HashMap<>();
        this.domainId = domainId;
    }

    public static RubricMatrix buildRubric(String domainId, Recommendations recommendations) {
        RubricMatrix result = new RubricMatrix(domainId);

        for (RecommendationMessage message : recommendations.getImprovementRecommendationMessages()) {
            UnbKey piUnbKey = new UnbKey(message.getEpaId(), message.getPerformanceIndicator());

            for (Map.Entry<LanguageCode, String[]> entry : message.getTranslations().entrySet()) {
                result.addMapping(entry.getKey(), piUnbKey, message.getPerformanceIndicatorLevel(), entry.getValue());
            }
        }

        return result;
    }

    private void addMapping(LanguageCode lang, UnbKey piUnbKey, String piLevelUnb, String[] translation) {
        Map<UnbKey, Map<String, String[]>> piMap = rubric.get(lang);
        if (piMap == null) {
            piMap = new HashMap<>();
            rubric.put(lang, piMap);
        }

        Map<String, String[]> lvlMap = piMap.get(piUnbKey);
        if (lvlMap == null) {
            lvlMap = new HashMap<>();
            piMap.put(piUnbKey, lvlMap);
        }

        lvlMap.put(piLevelUnb, translation);
    }

    public String getDomainId() {
        return domainId;
    }

    public String getRecommendation(UnbKey piUnbKey, String piLevelUnbId, LanguageCode langCode) {
        Map<UnbKey, Map<String, String[]>> piMap = rubric.get(langCode);
        if (piMap == null) {
            piMap = rubric.get(ApplicationSettings.DEFAULT_LANGUAGE);
        }
        if (piMap == null) {
            return "";
        }
        Map<String, String[]> lvlMap = piMap.get(piUnbKey);
        if (lvlMap == null) {
            UnbKey wildcard = new UnbKey(WILDCARD_STR,piUnbKey.getUnbId());
            lvlMap = piMap.get(wildcard);
            if (lvlMap == null) {
                if(piUnbKey.getUnbId().contentEquals(WILDCARD_STR_PI)){
                    lvlMap = getPiMathcingEPA(piMap,piUnbKey.getEpaId(),piLevelUnbId);
                    if (lvlMap == null) {
                      return "";
                    }
                }else{
                    return "";
                }
            }
        }
        String[] translations = lvlMap.get(piLevelUnbId);
        if(translations == null || translations.length == 0) return "";
        int idx = new Random().nextInt(translations.length);
        String translation = translations[idx];
        return translation == null ? "" : translation;
    }
    public  Map<String, String[]> getPiMathcingEPA(Map<UnbKey, Map<String, String[]>> piMap, String EPA ,String Level){
        for (Map.Entry<UnbKey, Map<String, String[]>> entry : piMap.entrySet())
        {
            UnbKey tempKey = entry.getKey();
            if(tempKey.getEpaId().contentEquals("*") || tempKey.getEpaId().contentEquals(EPA.trim())){
                return entry.getValue();
            }

        }
        return null;
    }
    public Set<LanguageCode> getSupportedLanguages() {
        return rubric.keySet();
    }

    @Override
    public String toString() {
        return "RubricMatrix for domainId='" + domainId + '\'';
    }
}
