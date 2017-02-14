package eu.watchme.sm.nlp;

/**
 * Created by xz905577 on 2/24/2016.
 */
public class SentimentCounter {
    final private String phrase;
    final private String tag;
    final private String identifiers;
    final private int matchedSentiment;
    final private double score;

    public String getPhrase() {
        return phrase;
    }
    public String getTag() {
        return tag;
    }
    public String getIdentifiers() {
        return identifiers;
    }
    public int getMatchedSentiment() {
        return matchedSentiment;
    }
    public double getScore() {
        return score;
    }

    public SentimentCounter(String p, String t, int m, double score) {
        this.phrase = p;
        this.tag = t;
        this.identifiers = prepareIdentifier(p, t);
        this.matchedSentiment = m;
        this.score = score;
    }

    private String prepareIdentifier(String text, String pos) {
        String[] tokens = text.split(" ");
        String[] tags = pos.split(" ");
        String identifiers = "";
        for(int i =0; i<tokens.length; i++)
            identifiers+=tokens[i]+"_"+tags[i]+" ";
        return identifiers;
    }

    @Override
    public String toString() {
        return "SentimentCounter{" +
                "phrase='" + phrase + '\'' +
                ", tag='" + tag + '\'' +
                ", matchedSentiment=" + matchedSentiment +
                ", score=" + score +
                '}';
    }
}
