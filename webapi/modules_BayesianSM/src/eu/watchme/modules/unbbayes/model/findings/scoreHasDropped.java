
package eu.watchme.modules.unbbayes.model.findings;

        import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by Alina Miron on 01/08/15.
 */
public class scoreHasDropped implements Finding {
    private String mTime;
    private String mValue;

    public scoreHasDropped(String time,String entity) {
        mTime = time;
        mValue = entity;
    }


    @Override
    public String getNodeName() {
        return "scoreHasDropped";
    }

    @Override
    public String[] getArguments() {
        return new String[]{mTime};
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.NOMINAL;
    }

    @Override
    public String getValue() {
        return mValue;
    }

}

