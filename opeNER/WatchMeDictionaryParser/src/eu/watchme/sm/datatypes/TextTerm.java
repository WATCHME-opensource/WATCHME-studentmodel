package eu.watchme.sm.datatypes;
import java.util.ArrayList;

public class TextTerm {
    private String lemma;
    private String morphofeat;
    private String pos;
    private ArrayList<TargetToken> targets = new ArrayList<TargetToken>();
    private String tid;
    private String type;

    public void addTarget(TargetToken target) {
	targets.add(target);
    }

    public String getLemma() {
	return lemma;
    }

    public String getMorphofeat() {
	return morphofeat;
    }

    public String getPos() {
	return pos;
    }

    public ArrayList<TargetToken> getTargets() {
	return targets;
    }

    public String getTid() {
	return tid;
    }

    public String getType() {
	return type;
    }

    public void setLemma(String lemma) {
	this.lemma = lemma;
    }

    public void setMorphofeat(String morphofeat) {
	this.morphofeat = morphofeat;
    }

    public void setPos(String pos) {
	this.pos = pos;
    }

    public void setTid(String tid) {
	this.tid = tid;
    }

    public void setType(String type) {
	this.type = type;
    }

    @Override
    public String toString() {
	return "{\"lemma\":\"" + lemma + "\", \"morphofeat\":\"" + morphofeat //$NON-NLS-1$ //$NON-NLS-2$
		+ "\", \"pos\":\"" + pos + "\", \"tid\":\"" + tid + "\", \"type\":\"" + type //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		+ "\", \"targets\":" + targets.toString() + "}"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
