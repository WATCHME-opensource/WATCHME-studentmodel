package eu.watchme.modules.unbbayes.model.findings;

import eu.watchme.modules.unbbayes.model.EntityType;

/**
 * Created by Alina Miron on 01/08/15.
 */
public class HasSupervisorFeedbackEPA implements Finding {
	public static final HasSupervisorFeedbackEPA emptyLevel = new HasSupervisorFeedbackEPA(null, null, null);
	private String mEpa;
	private String mTime;
	private EntityType mEntity;

	public HasSupervisorFeedbackEPA(String epa, String time, EntityType entity) {
		mEpa = epa;
		mTime = time;
		mEntity = entity;
	}

	public HasSupervisorFeedbackEPA(String epa, String time) {
		mEpa = epa;
		mTime = time;
		mEntity = EntityType.BOOLEAN_TRUE;
	}

	@Override
	public String getNodeName() {
		return "hasSupervisorFeedbackEPA";
	}

	@Override
	public String[] getArguments() {
		return new String[] { mEpa, mTime };
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

