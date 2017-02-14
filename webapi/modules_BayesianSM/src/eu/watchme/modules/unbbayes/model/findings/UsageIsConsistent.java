package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

public class UsageIsConsistent implements Finding {
    private String mTime;
    private EntityType mEntity;

    public UsageIsConsistent(String time, EntityType entity) {

        mTime = time;
        mEntity = entity;

    }
    public UsageIsConsistent(String time) {
        mTime = time;
        mEntity = EntityType.BOOLEAN_TRUE;
    }

    @Override
    public String getNodeName() {
        return "UsageIsConsistent";
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