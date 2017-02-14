package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class SeveralAssessmentTypes implements Finding {
    private String mTime;
    private String mEpa;
    private EntityType mEntity;

    public SeveralAssessmentTypes(String time,String epa, EntityType entity) {

        mTime = time;
        mEpa = epa;
        mEntity = entity;

    }
    public SeveralAssessmentTypes(String time,String epa) {
        mTime = time;
        mEpa = epa;
        mEntity = EntityType.BOOLEAN_FALSE;
    }

    @Override
    public String getNodeName() {
        return "SeveralAssessmentTypes";
    }

    @Override
    public String[] getArguments() {
        return new String[]{mTime,mEpa};
    }

    @Override
    public EntityType getEntityType() {
        return mEntity;
    }

    @Override
    public String getValue() {
        return String.valueOf(mEntity);
    }
}
