package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by Alina Miron on 01/08/15.
 */

public class HasSupervisorFeedbackEPAPI implements Finding {
    private String mEpa;
    private String mTime;
    private String mPerformanceIndicator;
    private EntityType mEntity;

    public HasSupervisorFeedbackEPAPI(String epa, String performanceIndicator,String time,EntityType entity) {
        mEpa = epa;
        mTime = time;
        mPerformanceIndicator = performanceIndicator;
        mEntity = entity;

    }
    public HasSupervisorFeedbackEPAPI(String epa, String performanceIndicator,String time) {
        mEpa = epa;
        mTime = time;
        mPerformanceIndicator = performanceIndicator;
        mEntity = EntityType.BOOLEAN_TRUE;
    }

    @Override
    public String getNodeName() {
        return "hasSupervisorFeedback";
    }

    @Override
    public String[] getArguments() {
        return new String[]{mEpa, mPerformanceIndicator,mTime};
    }

    @Override
    public EntityType getEntityType() {
        return mEntity;
    }

    @Override
    public String getValue() {
        return String.valueOf(true);
    }

}
