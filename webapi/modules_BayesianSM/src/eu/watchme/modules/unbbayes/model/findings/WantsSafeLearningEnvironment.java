package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class WantsSafeLearningEnvironment implements Finding {
    private String mTime;
    private EntityType mEntity;

    public WantsSafeLearningEnvironment(String time, EntityType entity) {

        mTime = time;
        mEntity = entity;

    }
    public WantsSafeLearningEnvironment(String time) {
        mTime = time;
        mEntity = EntityType.BOOLEAN_FALSE;
    }

    @Override
    public String getNodeName() {
        return "WantsSafeLearningEnvironment";
    }

    @Override
    public String[] getArguments() {
        return new String[]{mTime};
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
