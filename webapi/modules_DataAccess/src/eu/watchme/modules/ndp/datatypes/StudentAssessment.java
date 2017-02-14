package eu.watchme.modules.ndp.datatypes;

import org.mongodb.morphia.annotations.Property;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class StudentAssessment {

    @Property("formId")
    Integer formId;
    @Property("time")
    long time;

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StudentAssessment{" +
                "formId=" + formId +
                ", time=" + time +
                '}';
    }
}
