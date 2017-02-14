package eu.watchme.modules.dataaccess.adapters;

import eu.watchme.modules.commons.ApplicationSettings;
import eu.watchme.modules.dataaccess.model.StudentDataObject;
import eu.watchme.modules.dataaccess.model.StudentTimeMapAggregation;
import eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.aggregation.Projection;
import org.mongodb.morphia.aggregation.Sort;
import org.mongodb.morphia.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static eu.watchme.modules.dataaccess.model.StudentTimeMapDataObject.Properties.*;

public class StudentTimeMapAdapter extends BaseAdapter<StudentTimeMapDataObject> {
    public static final StudentTimeMapAdapter supervisorFeedbackAdapter = new StudentTimeMapAdapter();


    private boolean needsMerge = false;
    public StudentTimeMapAdapter() {
        super(StudentTimeMapDataObject.class);
    }
    public int getStudentProfileId(String studentId){
        Datastore datastore = getDataStore();
       Iterator<StudentTimeMapAggregation> aggregation = datastore.createAggregation(StudentTimeMapDataObject.class).match(datastore.createQuery(StudentTimeMapDataObject.class).field(StudentTimeMapDataObject.Properties.StudentId).equal(studentId)).sort(Sort.ascending(dayNumber)).group(Group.id(Group.grouping(profileId)), Group.grouping(profileId, Group.last(profileId)), Group.grouping("counts", new Accumulator("$sum", 1)), Group.grouping("lastUnb", Group.last(UnbId))).project(Projection.projection("_id").suppress(), Projection.projection(profileId), Projection.projection("counts"), Projection.projection("lastUnb")).aggregate(StudentTimeMapAggregation.class);
       int profile = 1;
        while(aggregation.hasNext()){
            StudentTimeMapAggregation data = aggregation.next();
            if(data.getProfileId()>=profile) {
                profile = data.getProfileId();

                if(Integer.valueOf(data.getLastUnb().replace("T",""))+ ApplicationSettings.getTimelineWGap()>=ApplicationSettings.getTimelineMaxSteps())
                    profile++;
            }


        }

        return profile;
    }
    public boolean isMerge() {
        return needsMerge;
    }

    public void setMerge(boolean needsMerge) {
        this.needsMerge = needsMerge;
    }

    public StudentTimeMapDataObject getLastStudentTimeMap(String studentId){
        Datastore datastore = getDataStore();
        return datastore.createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).order("-dayId,-_id").limit(1).get();
    }
    public StudentTimeMapDataObject searchForTimeElement(String studentId,long time){
        int dayId = Math.round((time) / ( 24 * 60 * 60 * 1000));
        Datastore datastore = getDataStore();
        return datastore.createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).field(dayNumber).lessThanOrEq(dayId).order("-dayId").limit(1).get();
    }
    public StudentTimeMapDataObject getNextTimeElement(String studentId,int pId,String submissionDate){
        StudentTimeMapDataObject firstElement = null;
        StudentTimeMapDataObject lastElement = null;
        StudentTimeMapDataObject nextElement = null;
        try {

        Datastore datastore = getDataStore();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        date = format.parse(submissionDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayId = Math.round((cal.getTimeInMillis()) / ( 24 * 60 * 60 * 1000));

       firstElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).field(profileId).equal(pId).order("dayId,_id").limit(1).get();
       lastElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).field(profileId).equal(pId).order(" -dayId,-_id").limit(1).get();
        if(firstElement==null) {
            cal.add(Calendar.DAY_OF_YEAR, -(ApplicationSettings.getTimelineWGap()));
            Date firstDate = cal.getTime();
            String strDate = format.format(firstDate);
            firstElement = new StudentTimeMapDataObject(studentId, "T0", dayId - ApplicationSettings.getTimelineWGap(), pId, strDate);
            if(pId>1)
                setMerge(true);
            addTimeStep(firstElement);
        }
        if(lastElement==null){
           lastElement = firstElement;
        }


            int diffFromFirst = dayId - firstElement.getDayId();
            int lastFromFirst = lastElement.getDayId()- firstElement.getDayId(); // not used
            int diffFromLast = dayId - lastElement.getDayId();
            int UnbID = 0;
            if(diffFromFirst > 0) {
                if (diffFromLast >= ApplicationSettings.getTimelineWGap()) {
                    UnbID = Integer.valueOf(lastElement.getUnbId().replace("T","")) + ApplicationSettings.getTimelineWGap();
                } else if (diffFromLast >= 0 && diffFromLast < ApplicationSettings.getTimelineWGap()) {
                    UnbID =Integer.valueOf(lastElement.getUnbId().replace("T","")) + ApplicationSettings.getTimelineGap()+1;
                }
                if (diffFromLast < 0) {
                    StudentTimeMapDataObject betElement = null;

                    betElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).field(profileId).equal(pId).field(dayNumber).greaterThanOrEq(dayId).field(dayNumber).lessThan(lastElement.getDayId()).order("dayId, _id").limit(1).get();

                    if (betElement == null) {
                        lastElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).field(profileId).equal(pId).field(dayNumber).lessThanOrEq(dayId).order(" -dayId,-_id").limit(1).get();
                        diffFromLast = dayId - lastElement.getDayId();
                        if (diffFromLast > 1) {
                            UnbID = Integer.valueOf(lastElement.getUnbId().replace("T",""))+1;
                        } else  {
                            UnbID = Integer.valueOf(lastElement.getUnbId().replace("T","")) ;
                        }
                    } else {
                        int betFromFirst = betElement.getDayId() - firstElement.getDayId(); // not used
                        int diffFromBet = betElement.getDayId() - dayId;
                        int lastFromBet = lastElement.getDayId() - betElement.getDayId();
                        if (diffFromBet >= 0) { // cannot be false, so redundant
                            if (lastFromBet > 0) { // cannot be false, so redundant
                                UnbID = Integer.valueOf( betElement.getUnbId().replace("T","")) + 1;
                            }else{
                                UnbID = Integer.valueOf( betElement.getUnbId().replace("T","")); // not reachable
                            }
                        }
                    }
                }
            }

            nextElement = new StudentTimeMapDataObject(studentId,"T"+Integer.valueOf(UnbID),dayId,pId,submissionDate);


        } catch (ParseException e) {

           e.printStackTrace();
        }
        return nextElement ;
    }
    public StudentTimeMapDataObject addTimeStep(StudentTimeMapDataObject data){
        StudentTimeMapDataObject firstElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(data.getStudentId()).field(profileId).equal(data.getProfileId()).field(UnbId).equal(data.getUnbId()).limit(1).get();
        if(firstElement==null) {
            insertDataObject(data);
        }else{
            firstElement.setDayId(data.getDayId());
            firstElement.setSubmissionDate(data.getSubmissionDate());
            updateDataObject(firstElement);
        }
        return data;
    }
    public boolean isLegacy(String studentId){
        StudentTimeMapDataObject firstElement = getDataStore().createQuery(StudentTimeMapDataObject.class).field(StudentId).equal(studentId).limit(1).get();
        if(firstElement==null) {
            StudentDataObject secondElement = getDataStore().createQuery(StudentDataObject.class).field(StudentId).equal(studentId).limit(1).get();
            return secondElement != null;
        }
        return false;
    }
    public void deleteLegacy(String studentId){
       getDataStore().delete(getDataStore().createQuery(StudentDataObject.class).field(StudentId).equal(studentId));
    }
    public int deleteAllForStudent(String studentId) {
        Datastore datastore = getDataStore();
        Query query = datastore.createQuery(StudentTimeMapDataObject.class).field(StudentTimeMapDataObject.Properties.StudentId).equal(studentId);
        return datastore.delete(query).getN();
    }

}