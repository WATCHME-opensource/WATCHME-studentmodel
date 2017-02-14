/*
 * Project: WATCHME Consortium, http://www.project-watchme.eu/
 * Creator: Maasticht University, NL, http://www.maastrichtunivertsity.nl/
  * Contributor: $author, $affiliation, $country, $website
 * Version: 0.1
 * Date: 20/7/2016
 * Copyright: Copyright (C) 2014-2017 WATCHME Consortium
 * License: The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.watchme.modules.ndp.functions;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jeroen.Donkers betweeb 13-5-2016 and 24-5-2016.
 *
 * This class contains Auxiliary function to help preprocessing portfolio-data before findings can
 * be added to the pedagogical knowlegde fragment of the student model
 *
 */
public class Auxiliaries {

    public enum DROPLEVEL {SCORENOTDROPPED, SCORELITTLEDROPPED, SCORESTRONGLYDROPPED}

    /**
     * Procedue scoreDropped computes per timestep in "scores" whether the score has dropped, with respect to the
     * recent past or not. It uses sliding windows with t-tests for detection.
     * Timepoints should reflect true time distances (e.g. a week, a month)
     *
     * This function can be used to fill node "ScoreHasDropped(t) in fragment "Frustration_MF".
     *  The funtion has to be called per EPA separately.
     *  in this case collect (and translate) the timepoints and average scores of all assessments concerning the EPA as input.
     *  the output values (DROPGELEVEL) of calls for all EPAs have to be combined as follows:
     *     if on a given timepoint, for any of the EPA scores a strong drop was detected: produce SCORESTRONGLYDROPPED
     *     else if on a given timepoint, for any of the EPA scores a mild drop was detected: produce SCOREMILDLYDROPPED
     *     else produce: SCORENOTDROPPED
     *  These can be translated into the corresponding MEBN levels of the node and the index of the output
     *  arrays can be translated into the corresponding bayesian time points.
     *
     *
     * @param scores:           array containing the scores, sorted ascendingly on timepoint.
     * @param timepoints:       array containing the timepoints of each score. This array needs the same amount of entries as the scores
     *                          and contains a nondescending series of numbers.
     * @param recentwindowsize: size of the window for the "recent past"
     * @param nowwindowsize:    size of the window for "now"
     * @param p_light:          boundary for the p-value between levels SCORENOTDROPPED and SCORELITTLEDROPPED.
     * @param p_heavy:          boundary for the p-value between levels SCORELITTLEDROPPED and SCORESTRONGLYDROPPED.
     * @return : array with DROPLEVELs, one per timepoint. The first array entry of the output refers to the timepoint that
     *           is at the length of the two windows after the first timepoint in the input.
     * In case of error, an exception will be thrown.
     */

    public static DROPLEVEL[] scoreDropped(double[] scores, int[] timepoints, int recentwindowsize,
            int nowwindowsize, double p_light, double p_heavy) throws Exception {
        return scoreDropped(scores, timepoints, recentwindowsize, nowwindowsize, p_light, p_heavy, false);
    }


    public static DROPLEVEL[] scoreDropped(double[] scores, int[] timepoints, int recentwindowsize,
            int nowwindowsize, double p_light, double p_heavy,
            boolean verbose) throws Exception {

        if (scores.length <= 1) throw new Exception("Score length is zero");
        if (scores.length != timepoints.length) throw new Exception("Score length unequal to timepoints length");
        if (recentwindowsize <= 0 || nowwindowsize <= 0) throw new Exception("Illegal window size");
        if (p_light <= p_heavy) throw new Exception("p-levels inconsistent");
        for (int k = 1; k < timepoints.length - 1; k++)
            if (timepoints[k] < timepoints[k - 1]) throw new Exception("Descending timepoints found.");  // check on descending timestaps

        int mintime = timepoints[0];
        int maxtime = timepoints[timepoints.length - 1];
        int timespan = maxtime - mintime + 1;
        int bothwindows = recentwindowsize+nowwindowsize-1;

        if (verbose) {
            System.out.println("Computing drop in scores...");
            System.out.println("number of scores: " + scores.length);
            System.out.println("timespan: " + timespan);
            System.out.println("start recent window - end now window - p-value - DROPLEVEL");
        }
        if (timespan < bothwindows) throw new Exception("Time windows ("+bothwindows+" too big for timespan ("+timespan+") in data");

        DROPLEVEL[] result = new DROPLEVEL[timespan - bothwindows];
        Arrays.fill(result, DROPLEVEL.SCORENOTDROPPED);
        if (scores.length <= 2) return result; // we cannot make any decision since too little data is available

        int recentwindowstart = 0; // index of start of recent window in scores array
        int nowwindowstart = 0;  // index of start of now window in scores array
        int nowwindowend = 0;  // index of end of now window in scores array

        TTest tt = new TTest();

        for (int i = 0; i < timespan - (recentwindowsize + nowwindowsize - 1); i++) {
            int trecent = i + mintime;
            int tnow = trecent + recentwindowsize;

            // determine start end endpoints of windows in scores array, based on timepoints array
            while (timepoints[recentwindowstart] < trecent && recentwindowstart < timepoints.length - 1)
                recentwindowstart++;
            while (timepoints[nowwindowstart] < tnow && nowwindowstart < timepoints.length - 1) nowwindowstart++;
            while (timepoints[nowwindowend] < (tnow + nowwindowsize - 1) && nowwindowend < timepoints.length - 1)
                nowwindowend++;

            // check if endpoints reach beyond our timespan
            if (timepoints[recentwindowstart] < trecent || timepoints[nowwindowstart] < tnow) break;

            // collect scores from windows
            double[] recent_scores = Arrays.copyOfRange(scores, recentwindowstart, nowwindowstart - 1);
            double[] now_scores = Arrays.copyOfRange(scores, nowwindowstart, nowwindowend);
            double p = tt.tTest(recent_scores, now_scores);

            double mean_recent = StatUtils.mean(recent_scores);
            double mean_now = StatUtils.mean(now_scores);

            // assign score drop level, but only if mean is lower
            if (mean_now < mean_recent) {
                if (p > p_light) result[i] = DROPLEVEL.SCORENOTDROPPED;
                else if (p > p_heavy) result[i] = DROPLEVEL.SCORELITTLEDROPPED;
                else result[i] = DROPLEVEL.SCORESTRONGLYDROPPED;
            }

            if (verbose)
                System.out.println(i + "," + (i+bothwindows-1) + ","+mean_recent+"," + mean_now+ "," + p + "," + result[i]);
        }

        return result;
    }

    /**
     * Procedue usageChange computes per timestep whether the usage (in terms of number of assessments per timepoint) has changed, with respect to the
     * recent past or not. It uses sliding windows with t-tests for detection.
     * In this procedure, timepoints need to be such that mutliple assessments are likely to map to the same timepoint.
     * All assessments available can be used per call.
     * Timepoints should reflect true time distances (e.g. a week, a month)
     *
     * This function can be used to fill node "UsageChanged(t) in fragment "Frustration_MF":
     *  in this case collect (and translate) the timepoints of all assessments as input.
     *  the output values (boolean) can be translated into the corresponding MEBN levels of the node and the index of the output
     *  array can be translated into the corresponding bayesian time points.
     *
     *  The same function can also be used to fill node "EvenUsagePattern(t,epa)" in fragment "InformationCompleteness_MF".
     *    In this case, collect (and translate) the timepoints of only those assessments which involve this EPA as input.
     *    the output values (boolean) can be translated into the corresponding MEBN level by negation.
     *    And the index of the output array can be translated into the corresponding bayesian time points.
     *
     * @param timepoints:       array containing the timepoints. (Does not have to be ordered)
     * @param recentwindowsize: size of the window for the "recent past"
     * @param nowwindowsize:    size of the window for "now"
     * @param p_level:          boundary for the p-value between levels change=true and change=false.
     * @return : array with booleans, one per timepoint. The first array entry of the output refers to the timepoint that
     *           is at the length of the two windows after the first timepoint in the input.
     *
     *           In case of error, an exception will be thrown.
     */

    public static boolean[] usageChanged(int[] timepoints, int recentwindowsize,
            int nowwindowsize, double p_level) throws Exception {
        return usageChanged(timepoints, recentwindowsize, nowwindowsize, p_level, false);
    }


    public static boolean[] usageChanged(int[] timepoints, int recentwindowsize,
            int nowwindowsize, double p_level,
            boolean verbose) throws Exception {
        if (timepoints.length <= 1) throw new Exception("Timepoints length is zero");
        if (recentwindowsize <= 0 || nowwindowsize <= 0) throw new Exception("Illegal window size");

        int mintime = -1;
        int maxtime = -1;
        for (int k = 0; k < timepoints.length-1; k++) {
            if (mintime<0 || timepoints[k]<mintime) mintime=timepoints[k];
            if (timepoints[k] > maxtime) maxtime=timepoints[k];
        }
        int timespan = maxtime - mintime + 1;
        int bothwindows = recentwindowsize+nowwindowsize-1;
        if (bothwindows > timespan) throw new Exception("Timewindows too big for timespan in data");


        if (verbose) {
            System.out.println("Computing changed in usage...");
            System.out.println("number of timepoints: " + timepoints.length);
            System.out.println("mintime: " + mintime+" maxtime: " + maxtime+ " timespan: " + timespan);
            System.out.println("Windows: " + recentwindowsize+" and " + nowwindowsize+ " both: " + bothwindows);
            System.out.println("start recent window - end now window - p-value - USAGELEVEL");
        }

        TTest tt = new TTest();


        boolean[] result = new boolean[timespan -  bothwindows];
        Arrays.fill(result, false);

        // collect usage numbers per timepoint
        int[] usage = new int[timespan+1];
        Arrays.fill(usage, 1);
        //        for (int t: timepoints)  usage[t-mintime]++;
        double[] recentwindow = new double[recentwindowsize];
        double[] nowwindow = new double[nowwindowsize];

        for (int i = 0; i < timespan - bothwindows; i++) {
            for (int j=0; j<recentwindowsize-1; j++) recentwindow[j] = usage[i+j];
            for (int j=0; j<nowwindowsize-1; j++) nowwindow[j] = usage[i+j+recentwindowsize];
            double p = tt.tTest(recentwindow, nowwindow);
            result[i] = (p <= p_level);

            if (verbose) {
                double mean_recent = StatUtils.mean(recentwindow);
                double mean_now = StatUtils.mean(nowwindow);
                System.out.println(i + "," + (i + bothwindows) + "," + mean_recent + "," + mean_now + "," + p + "," + result[i]);
            }

        }

        return result;
    }
    /**
     * This procedure detects whether one of the scores (e.g. EPAs) has significantly more variation than the other
     * scores combined. If so, it returns the index number of that score, otherwise -1.
     *
     * This function can be used to fill node "IsVariationHotspot(t,epa) in fragment "InformationCompleteness_MF".
     *   In this case, collect average scores per EPA for all assessments in a time window before t and separate scores per EPA into
     *   separate arrays. The outcome can be translated into the MEBN level "true" if the return value is equal to the index of the EPA input array, otherwise
     *   nothing has to be fed into the network (the default value of the node is "false").
     *
     * The call should be repeated for all time steps in the network.
     *          *
     * @param scores: a list of double[] arrays, each containing the subsequent values of each score (e.g. per EPA one array)
     *              The time window should not be taken too wide, otherwise natural growth in score interferes with variation.
     *              Scores with less than 2 values should be omitted from the list.
     * @param siglevel: the p-level below which signifance is accepted (i.e. H0 is rejected that no difference in variation is observed)
     * @return : -1 if no score with significantly higher variance is found
     *          otherwise: the index in the scores list of the score with maximum variance.
     */

    public static int mostVariation(List<double []> scores, double siglevel) {
        return mostVariation(scores, siglevel, false);
    }

    public static int mostVariation(List<double []> scores, double siglevel, boolean verbose) {
        if (scores.size() <=1 ) return -1; // we cannot determine a solution with too little data
        for (double[] sc: scores)
            if (sc.length <=1) return -1; // we cannot determine a solution with too little data

        ArrayList<Double> vars =  new ArrayList<Double>();
        ArrayList<Double> centered = new ArrayList<Double>();

        // get soore with maximum variance
        double maxvar = 0;
        int max = 0;
        int k=0;
        for (double[] sc: scores) {
            double v = StatUtils.variance(sc);
            if (verbose) System.out.println(k+" (n=" + sc.length + ") var=" + v);
            vars.add(v);
            if (v > maxvar) {
                maxvar = v;
                max = vars.size() - 1;
            }
            k++;
        }

        // collect and center all scores, except for the scores with maximum variance
        k=0;
        for (double[] sc: scores) {
            if (k != max) {
                double m = StatUtils.mean(sc);
                for (double s : sc)
                    centered.add(s - m);
            }
            k++;
        }
        double[] centeredarr = new double[centered.size()];
        for (int i=0; i<centered.size(); i++) centeredarr[i] = centered.get(i);
        double centeredvar = StatUtils.variance(centeredarr);

        // compute F-statistic and p-value
        int n1 = scores.get(max).length;
        int n2 = centeredarr.length;
        FDistribution f = new FDistribution(n1-1,n2-1);
        double fstat = maxvar / centeredvar;
        double tail = f.cumulativeProbability(fstat);

        if (verbose) System.out.println("max var: "+maxvar+" centered var: "+ centeredvar + " fstat: "+fstat+ " p: "+(1-tail));
        if (siglevel > (1-tail)) {
            if (verbose) System.out.println("p < : "+siglevel+", so we return score sequence: "+max);
            return max;
        } else {
            if (verbose) System.out.println("p > : "+siglevel+", so we return -1");
            return -1;
        }
    }


    /**
     * This procedure detects whether there is any inconsistency in usage of the portfolio. We define inconsistency
     * as a (by ANOVA) detectable difference between usage over EPAs and over time (if applicable).
     *
     * This function can be used to fill node "UsageIsConsistent(t) in fragment "PortfolioConsistency_MF".
     *   In this case, collect timepoints per EPA for all assessments in a time window before t in separate arrays.
     *   The outcome will be true if inconsistency is detected. Take care of taking timepoints large enough (e.g. per week
     *   or month).
     *
     * The call should be repeated for all time steps in the network.
     *          *
     * @param timepoints: a list of int[] arrays, each containing the subsequent timepoints (e.g. per EPA one array)
     *              The time window should not be taken too wide, otherwise natural growth in score interferes with variation.
     *              Scores with less than 2 values should be omitted from the list.
     * @param siglevel: the p-level below which signifance is accepted (i.e. H0 is rejected that no difference in variation is observed)
     * @param ignoretime: if true, does not involve inconsistency over time.
     * @return : false if no score with significantly higher variance is found otherwise: true.
     */

    public static boolean usageConsistency(List<int []> timepoints, double siglevel, boolean ignoretime) throws Exception {
        return usageConsistency(timepoints, siglevel, ignoretime, false);
    }

    public static boolean usageConsistency(List<int []> timepoints, double siglevel, boolean ignoretime, boolean verbose) throws Exception {
        if (timepoints.size() <=1 ) throw new Exception("No data");
        for (int[] tp: timepoints) {
            if (tp.length <= 1) throw new Exception("Missing data");
            if (verbose) System.out.println(Arrays.toString(tp));
        }
        int mintime = -1;
        int maxtime = -1;
        for (int[] tplist: timepoints) for (int tp: tplist) {
            if (mintime < 0 || tp < mintime) mintime = tp;
            if (tp > maxtime) maxtime = tp;
        }
        if (maxtime<0) throw new Exception("Missing data");
        int timespan = maxtime - mintime + 1;

        // collect usage statistics
        ArrayList<double []> usage = new ArrayList<double []>();
        for (int[] tplist: timepoints)  {
            double[] usg = new double[timespan];
            Arrays.fill(usg, 0);
            for (int tp: tplist) usg[tp-mintime]++;
            usage.add(usg);
        }
        // apply One-way Anova to detect a difference in usage per EPA
        OneWayAnova anova = new OneWayAnova();
        double p = anova.anovaPValue(usage);
        boolean EPAIncosistent = (p<siglevel);

        if (verbose) {
            System.out.println("timespan: "+timespan);
            System.out.println("Anova over EPAs p-value: "+p);
            System.out.println("Siglevel: "+siglevel);
            if (EPAIncosistent) System.out.println("Inconsistent"); else System.out.println("Consistent");
        }

        if (ignoretime) return (!EPAIncosistent);

        // reorder usage statistics - transpose time and epa/competency

        int nlayers = 2;  // we use just 2 time-layers to detect time inconsistency (so anova becomes a t-test)
        int[] startpoint = new int[nlayers+1];
        double layersize = (timespan*1.0)/nlayers;
        for (int i = 0; i<nlayers; i++) {
            startpoint[i] = (int)(i*layersize);
        }
        startpoint[nlayers]=timespan;

        int[] layercount = new int[nlayers];
        int layer=0;
        for (int i = 0; i<timespan; i++) {
            while (i>=startpoint[layer+1]) {  layer++; }
            layercount[layer]+=usage.size();
        }

        ArrayList<double []> otherusage = new ArrayList<double []>();
        for (int i = 0; i<nlayers; i++) {
            double[] u = new double[layercount[i]];
            otherusage.add(u);
            layercount[i] = 0;
        }

        layer=0;
        for (int i = 0; i<timespan; i++) {
            while (i >= startpoint[layer+1]) layer++;
            double[] u = otherusage.get(layer);
            for (double[] anUsage : usage) {
                u[layercount[layer]++] = anUsage[i];
            }
        }

        // remove empty layers if any
        for (int i = nlayers-1; i>=0; i--) if (layercount[i] == 0) otherusage.remove(i);

        p = anova.anovaPValue(otherusage);
        boolean TimeIncosistent = (p<siglevel);

        if (verbose) {
            System.out.println("Anova over time p-value: "+p);
            System.out.println("Siglevel: "+siglevel);
            if (TimeIncosistent) System.out.println("Inconsistent"); else System.out.println("Consistent");
            if (EPAIncosistent || TimeIncosistent) System.out.println("INCONSISTENT"); else System.out.println("CONSISTENT");
        }

        return (!(EPAIncosistent || TimeIncosistent));
    }



    /**
     * This procedure detects whether there is any inconsistency in scores in the portfolio. We define inconsistency
     * as a (by ANOVA) detectable difference between scores over EPAs and over time (if applicable).
     *
     * This function can be used to fill node "ScoreIsConsistent(t) in fragment "PortfolioConsistency_MF".
     *   In this case, collect scores and timepoints per EPA for all assessments in a time window before t in separate arrays.
     *   The outcome will be true if inconsistency is detected. Take care of taking timepoints large enough (e.g. per week
     *   or month).
     *
     * The call should be repeated for all time steps in the network.
     *          *
     * @param scores: a list of double[] arrays, each containing the subsequent average scores per assessment (e.g. per EPA one array)
     * @param timepoints: a list of int[] arrays, each containing the subsequent timepoints of the above scores (e.g. per EPA one array)
     *              The time window should not be taken too wide, otherwise natural growth in score interferes with variation.
     *              Scores with less than 2 values should be omitted from the list.
     * @param siglevel: the p-level below which signifance is accepted (i.e. H0 is rejected that no difference in variation is observed)
     * @param ignoretime: if true, does not involve inconsistency over time.
     * @return : false if no score with significantly higher variance is found otherwise: true.
     */

    public static boolean scoreConsistency(List<double[]> scores, List<int []> timepoints, double siglevel, boolean ignoretime) throws Exception {
        return scoreConsistency(scores, timepoints, siglevel, ignoretime, false);
    }

    public static boolean scoreConsistency(List<double[]> scores, List<int []> timepoints, double siglevel, boolean ignoretime, boolean verbose) throws Exception {
        if (timepoints.size() <=1 ) throw new Exception("No data");
        if (timepoints.size() != scores.size()) throw new Exception("No data");
        for (int[] tp: timepoints)
            if (tp.length <=1) throw new Exception("Missing data");
        for (int i = 0; i<scores.size()-1; i++)  if (scores.get(i).length != timepoints.get(i).length)
            throw new Exception("Inconsistency between scores and timepoints");
        int mintime = -1;
        int maxtime = -1;
        for (int[] tplist: timepoints) for (int tp: tplist) {
            if (mintime < 0 || tp < mintime) mintime = tp;
            if (tp > maxtime) maxtime = tp;
        }
        if (maxtime<0) throw new Exception("Missing data");
        int timespan = maxtime - mintime + 1;

        // collect score statistics - collect average scores per timepoint, per EPA
        ArrayList<double []> scoreaverages = new ArrayList<double []>();
        for (int j = 0; j<scores.size()-1; j++)  {
            int[] tplist = timepoints.get(j);
            double[] sclist = scores.get(j);

            double[] avg = new double[timespan];
            int[] cnt = new int[timespan];
            Arrays.fill(cnt, 0);
            Arrays.fill(avg, 0);
            for (int k = 0; k<sclist.length-1; k++) {
                cnt[tplist[k]-mintime]++;
                avg[tplist[k]-mintime]+=sclist[k];
            }
            for (int k = 0; k<timespan-1; k++) {
                if (cnt[k]>0) avg[k] = avg[k]/cnt[k];
            }
            scoreaverages.add(avg);
        }

        // apply One-way Anova to detect a difference in usage per EPA
        OneWayAnova anova = new OneWayAnova();
        double p = anova.anovaPValue(scoreaverages);
        boolean EPAIncosistent = (p<siglevel);

        if (verbose) {
            System.out.println("timespan: "+timespan);
            System.out.println("Anova over EPAs p-value: "+p);
            System.out.println("Siglevel: "+siglevel);
            if (EPAIncosistent) System.out.println("Inconsistent"); else System.out.println("Consistent");
        }

        if (ignoretime) return (!EPAIncosistent);

        // reorder usage statistics
        ArrayList<double []> otheravarages = new ArrayList<double []>();
        int n = timepoints.size();
        for (int i = 0; i<timespan-1; i++) {
            double[] scs = new double[n];
            for (int j=0; j<scoreaverages.size(); j++) {
                scs[j] = scoreaverages.get(j)[i];
            }
            otheravarages.add(scs);
        }
        p = anova.anovaPValue(otheravarages);
        boolean TimeIncosistent = (p<siglevel);

        if (verbose) {
            System.out.println("Anova over time p-value: "+p);
            System.out.println("Siglevel: "+siglevel);
            if (TimeIncosistent) System.out.println("Inconsistent"); else System.out.println("Consistent");

            if (EPAIncosistent || TimeIncosistent) System.out.println("INCONSISTENT"); else System.out.println("CONSISTENT");

        }

        return (!(EPAIncosistent || TimeIncosistent));
    }


}
