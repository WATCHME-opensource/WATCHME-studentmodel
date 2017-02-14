/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: University of Reading, UK, http://www.reading.ac.uk/
 * Creator: NetRom Software, RO, http://www.netromsoftware.ro/
 * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 11/8/2015
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

package eu.watchme.modules.tests.junit.consistency;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.commons.data.EpassKey;
import eu.watchme.modules.commons.data.UnbKey;
import eu.watchme.modules.commons.enums.LanguageCode;
import eu.watchme.modules.commons.staticdata.StaticModelManager;
import eu.watchme.modules.commons.staticdata.model.form.json.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class TestModelConsistency {
    private String domainId;
    private List<LanguageCode> domainLanguages = new LinkedList<>();
    private List<Integer> supportedForms = new LinkedList<>();
    private Map<EpassKey,UnbKey> alreadyMapped = new HashMap<>();
    private static StaticModelManager modelManager;
    private Map<LanguageCode,Map<UnbKey,List<String>>> ignoreList = new HashMap<>(new HashMap<>());
    public TestModelConsistency(String domainId, List<LanguageCode> domainLanguages,List<Integer> forms, Map<LanguageCode,Map<UnbKey,List<String>>> ignore) {
        this.domainId = domainId;
        this.domainLanguages.addAll(domainLanguages);
        this.supportedForms.addAll(forms);
        this.ignoreList.putAll(ignore);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("appsettings.test.properties"));
            String resDirRelativePath = servletContextEvent.getServletContext().getInitParameter("ResourcesRelativePath");
            String resDirAbsolutePath = servletContextEvent.getServletContext().getRealPath(resDirRelativePath);
            ApplicationSettings.LoadSettings(properties, new File(resDirAbsolutePath));
            modelManager = StaticModelManager.getManager(domainId);
            assertNotNull(String.format("Model not supported for %s", domainId), modelManager);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    public void testAll() {
        testSupportedLanguages();
        testSupportedForms();
        testSupportedEPAs();
        testSupportedLvls();
        testSupportedPIs();
        testSupportedFI();
        testSupportedSFF();
        testSupportedRubric();
    }

    public void testSupportedLanguages() {
        for (LanguageCode languageCode : domainLanguages) {
            assertTrue(message("Language %s not supported for %s", languageCode.toString(), domainId),
                    modelManager.isLanguageSupported(languageCode));
        }
    }
    public void testSupportedForms() {
        for (Integer formType : supportedForms) {
            assertTrue(message("Form Type %d not supported for %s", formType, domainId),
                    modelManager.getFormMapper().getFormTypes().contains(formType));

        }
    }

    public void testSupportedEPAs() {
        for (MappedField item : modelManager.getFormMapper().getEpaList()) {
            checkField("EPA EpassId", item.getUnbId(), item.getEpassId());
            checkField("EPA UnbId", item.getEpassId(), item.getUnbId());
            checkField("EPA Translations", item.getEpassId(), item.getTranslationMap());
            for (LanguageCode languageCode : domainLanguages) {
                checkTranslation(languageCode, item.getEpassId(), item.getTranslation(languageCode));
            }
        }
    }


    public void testSupportedLvls() {

        for (RankedMappedField item : modelManager.getFormMapper().getPerformanceIndicatorLevelMapper()
                                                  .getRankedValues()) {
            checkField("PI Level EpassId", item.getUnbId(), item.getEpassId());
            checkField("PI Level UnbId", item.getEpassId(), item.getUnbId());
            checkField("PI Level Translations", item.getEpassId(), item.getTranslationMap());
            for (LanguageCode languageCode : domainLanguages) {
                checkTranslation(languageCode, item.getEpassId(), item.getTranslation(languageCode));
            }
        }
    }
    public void testSupportedPIs() {
        clearAlreadyMapped();

        for(UnbKey unbId : modelManager.getFormMapper().getPerformanceIndicators()) {

            for ( PIMappedField item : modelManager.getFormMapper().getMappedPerformanceIndicatorByUnb(unbId)) {

                for (EpassKey ePassItem : item.getEpassKey()) {
                    checkField("PI EpassId", item.getUnbKey().getUnbId(), ePassItem.getEpassId());
                    checkField("PI UnbId",  ePassItem.getEpassId(),item.getUnbKey().getUnbId());
                    checkField("PI Translations",  ePassItem.getEpassId(), item.getTranslationMap());
                    if(item.getEpassKey()!=null && item.getUnbKey()!=null)
                    {
//                        assertFalse(String.format(" PI==== Epass key mapped to multiple UnbKeys for Form Type : %d Question ID :  %s for domain %s", ePassItem.getEpassFormType(),ePassItem.getEpassId(),domainId), alreadyMapped.containsKey(ePassItem));
                        alreadyMapped.putIfAbsent(ePassItem,item.getUnbKey());
                    }
                for (LanguageCode languageCode : domainLanguages) {
                    checkTranslation(languageCode, ePassItem.getEpassId(), item.getTranslation(languageCode));
                }
                    assertTrue(modelManager.getFormMapper().getFormTypes().contains(ePassItem.getEpassFormType()));
                }
                testSupportedEPAbyUnb(item.getUnbKey().getEpaId());

            }
        }
    }
    public void testSupportedFI() {
        clearAlreadyMapped();
        for(TypeMappedField item : modelManager.getFormMapper().getFeedbackIndicatorMapper().getTypedValues()) {

            checkField("FI TypeId", item.getUnbId(), String.valueOf(item.getTypeId()));
            checkField("FI EpassId", item.getUnbId(), item.getEpassId());
            checkField("FI UnbId", item.getEpassId(), item.getUnbId());
            checkField("FI Translations", item.getEpassId(), item.getTranslationMap());
            for (LanguageCode languageCode : domainLanguages) {
                checkTranslation(languageCode, item.getEpassId(), item.getTranslation(languageCode));
            }
        }
    }
    public void testSupportedSFF() {
        clearAlreadyMapped();
        for(SupervisorFeedbackField item : modelManager.getFormMapper().getSupervisorFeedbackFieldIndexMapper().getValues()) {

            for (EpassKey ePassItem : item.getEpassKey()) {
                checkField("SFF EpassId", item.getUnbKey().getUnbId(), ePassItem.getEpassId());
                checkField("SFF UnbId",  ePassItem.getEpassId(),item.getUnbKey().getUnbId());
                checkField("SFF Translations",  ePassItem.getEpassId(), String.valueOf(item.getFeedbackLevel()));
                if(item.getEpassKey()!=null && item.getUnbKey()!=null)
                {
//                    assertFalse(String.format("SFF Epass key mapped to multiple UnbKeys for Form Type : %d Question ID :  %s for domain %s", ePassItem.getEpassFormType(), ePassItem.getEpassId(), domainId), alreadyMapped.containsKey(ePassItem));
                    alreadyMapped.putIfAbsent(ePassItem,item.getUnbKey());
                }
                assertTrue(modelManager.getFormMapper().getFormTypes().contains(ePassItem.getEpassFormType()));
            }
            testSupportedEPAbyUnb(item.getUnbKey().getEpaId());
        }
    }
    public void testSupportedRubric() {
        for(UnbKey unbId : modelManager.getFormMapper().getPerformanceIndicators()) {
            for (RankedMappedField itemLvl : modelManager.getFormMapper().getPerformanceIndicatorLevelMapper()
                    .getRankedValues()) {
                for (LanguageCode languageCode : domainLanguages) {
                    boolean ignore = false;
                    if(ignoreList.containsKey(languageCode)) {
                        Map<UnbKey, List<String>> exceptionList = ignoreList.get(languageCode);

                        if(exceptionList.containsKey(unbId) ) {
                            if (exceptionList.get(unbId).contains(itemLvl.getUnbId()) || exceptionList.get(unbId).contains("*")) {
                                ignore = true;
                            }
                        }

                    }
                    if(!ignore)
                        assertNotSame(String.format("No matching rubric for key : %s , level : %s , language : %s  , domain : %s", unbId.toString(), itemLvl.getUnbId(), languageCode.toString(), domainId), modelManager.getRubricMatrix().getRecommendation(unbId, itemLvl.getUnbId(), languageCode), "");

                }
                }

            }

        }



    private void checkTranslation(LanguageCode languageCode, String identifier, String value) {
        String message = message("[%s] Missing translation (lang: %s) for %s", domainId, languageCode, identifier);
        assertNotNull(message, value);
        assertTrue(message, value.trim().length() > 0);
    }

    private void checkField(String fieldName, String identifier, String value) {
        assertNotNull(message("[%s] %s missing for %s", domainId, fieldName, identifier), value);
        assertTrue(message("[%s] %s missing for %s", domainId, fieldName, identifier), value.trim().length() > 0);
    }

    private void checkField(String fieldName, String identifier, Map value) {
        assertNotNull(message("[%s] %s missing for %s", domainId, fieldName, identifier), value);
        assertFalse(message("[%s] %s missing for %s", domainId, fieldName, identifier), value.isEmpty());
    }

    private void checkField(String fieldName, String identifier, Collection value) {
        assertNotNull(message("[%s] %s missing for %s", domainId, fieldName, identifier), value);
        assertFalse(message("[%s] %s missing for %s", domainId, fieldName, identifier), value.isEmpty());
    }
    private void testSupportedEPAbyUnb(String unbkey) {
            MappedField item = modelManager.getFormMapper().getMappedEpaByUnb(unbkey);
            checkField("EPA EpassId", item.getUnbId(), item.getEpassId());
            checkField("EPA UnbId", item.getEpassId(), item.getUnbId());
            checkField("EPA Translations", item.getEpassId(), item.getTranslationMap());
            for (LanguageCode languageCode : domainLanguages) {
                checkTranslation(languageCode, item.getEpassId(), item.getTranslation(languageCode));
            }

    }
    private void clearAlreadyMapped(){
        alreadyMapped.clear();
    }
    private String message(String pattern, Object... params) {
        return String.format(pattern, params);
    }
}
