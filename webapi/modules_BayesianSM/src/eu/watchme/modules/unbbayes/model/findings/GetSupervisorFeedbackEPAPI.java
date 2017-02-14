package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by xz905577 on 4/20/2016.
 */
public class GetSupervisorFeedbackEPAPI  implements Finding  {
    private String mEpa;
    private String mPi;
    private String mTime;
    private String mValue;

    public GetSupervisorFeedbackEPAPI(String epa,String pi, String time,String value) {
        mEpa = epa;
        mPi = pi;
        mTime = time;
        mValue = value;
    }

    @Override
    public String getNodeName() {
        return "getSupervisorFeedbackEpaPI";
    }

    @Override
    public String[] getArguments() {
        return new String[]{mEpa,mPi,mTime};
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
