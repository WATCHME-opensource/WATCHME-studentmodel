package eu.watchme.modules.unbbayes.model.findings;


import eu.watchme.modules.unbbayes.model.EntityType;

public class IsVariationHotspot implements Finding {
    private String mTime;
    private String mEpa;
    private EntityType mEntity;

    public IsVariationHotspot(String time,String epa, EntityType entity) {

        mTime = time;
        mEpa = epa;
        mEntity = entity;

    }
    public IsVariationHotspot(String time,String epa) {
        mTime = time;
        mEpa =epa;
        mEntity = EntityType.BOOLEAN_TRUE;
    }

    @Override
    public String getNodeName() {
        return "IsVariationHotspot";
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