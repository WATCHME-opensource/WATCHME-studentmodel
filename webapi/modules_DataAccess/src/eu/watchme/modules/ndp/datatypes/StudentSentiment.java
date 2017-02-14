package eu.watchme.modules.ndp.datatypes;

import org.mongodb.morphia.annotations.Property;

/**
 * Created by xz905577 on 6/9/2016.
 */
public class StudentSentiment {
    @Property("sentiment")
    String sentiment;
    @Property("time")
    long time;

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
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
                "sentiment=" + sentiment +
                ", time=" + time +
                '}';
    }
}
