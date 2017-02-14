package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class hasFeedbackFromPeers implements Finding {
    private String mTime;

    private String mValue;

    public hasFeedbackFromPeers(String time,String value) {

        mTime = time;
        mValue = value;

    }

    @Override
    public String getNodeName() {
        return "hasFeedbackFromPeers";
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
