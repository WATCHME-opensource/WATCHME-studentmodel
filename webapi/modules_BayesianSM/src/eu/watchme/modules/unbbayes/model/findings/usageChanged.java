package eu.watchme.modules.unbbayes.model.findings;


import eu.watchme.modules.unbbayes.model.EntityType;

public class usageChanged implements Finding {
    private String mTime;
    private EntityType mEntity;

    public usageChanged(String time, EntityType entity) {

        mTime = time;
        mEntity = entity;

    }
    public usageChanged(String time) {
        mTime = time;
        mEntity = EntityType.BOOLEAN_TRUE;
    }

    @Override
    public String getNodeName() {
        return "usageChanged";
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