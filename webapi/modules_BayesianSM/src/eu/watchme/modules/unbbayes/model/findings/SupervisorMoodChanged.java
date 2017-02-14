package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by xz905577 on 6/14/2016.
 */
public class SupervisorMoodChanged implements Finding {
    private String mTime;
    private EntityType mEntity;

    public SupervisorMoodChanged(String time, EntityType entity) {

        mTime = time;
        mEntity = entity;

    }
    public SupervisorMoodChanged(String time) {
        mTime = time;
        mEntity = EntityType.BOOLEAN_FALSE;
    }

    @Override
    public String getNodeName() {
        return "SupervisorMoodChanged";
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
