package eu.watchme.modules.ndp.datatypes;

import org.mongodb.morphia.annotations.Property;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class StudentRole {


    @Property("sentiment")
    String role;
    @Property("time")
    long time;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StudentRole{" +
                "sentiment=" + role +
                ", time=" + time +
                '}';
    }
}
