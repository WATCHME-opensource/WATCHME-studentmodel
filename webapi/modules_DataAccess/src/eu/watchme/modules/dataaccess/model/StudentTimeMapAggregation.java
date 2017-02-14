package eu.watchme.modules.dataaccess.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

    @Entity("studentTimeMap")
    public class StudentTimeMapAggregation extends DataObject {

        @Override
        public ObjectId getId() {
            return id;
        }

        @Id
        private ObjectId id;



        @Property("profileId")
        private Integer profileId;

        @Property("counts")
        private Integer counts;



        @Property("lastUnb")
        private String lastUnb;


        public Integer getProfileId() {
            return profileId;
        }

        public void setProfileId(Integer profileId) {
            this.profileId = profileId;
        }

        public Integer getCounts() {
            return counts;
        }

        public void setCounts(Integer counts) {
            this.counts = counts;
        }
        public String getLastUnb() {
            return lastUnb;
        }

        public void setLastUnb(String lastUnb) {
            this.lastUnb = lastUnb;
        }
}
