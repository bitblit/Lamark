package com.erigir.lamark.music;

import com.erigir.lamark.Util;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.*;

/**
 * This is a wrapper object that performs analysis on a given score,
 * such that the artifacts of this analysis may be used to generate a
 * fitness score for a given piece.
 *
 * @author cweiss
 */
public class ScoreAnalysis {
    private Score score;
    private ScaleEnum cacheClosestScaleFit;
    private Double cacheClosestScaleFitPercent;
    private Double cacheClosestScaleBigFiveFitPercent;
    private TimeSignatureEnum cacheClosestTimeSignatureFit;
    private Double cacheClosestTimeSignatureFitPercent;
    private SortedMap<Integer, Integer> tonalValues;
    private SortedMap<Double, Integer> timeValues;
    private List<Integer> allNotesSorted;
    private List<Integer> noteDeltaList;
    private List<Double> timeDeltaList;
    private List<Note> cacheAllNotes;
    private Integer medianNote;
    private Double meanNote;
    private Double noteStandardDeviation;
    private Integer noteDirectionChanges;

    public ScoreAnalysis(Score s) {
        super();
        setScore(s);
    }


    public ScoreAnalysis() {
        super();
    }

    public static List<Note> partToNoteList(Part p) {
        List<Note> rval = new ArrayList<Note>();
        for (Phrase ph : p.getPhraseArray()) {
            rval.addAll(Arrays.asList(ph.getNoteArray()));
        }
        return rval;
    }

    public static List<Integer> toDeltaList(List<Integer> ints) {
        ArrayList<Integer> rval = new ArrayList<Integer>(ints.size() - 1);
        for (int i = 1; i < ints.size(); i++) {
            rval.add(ints.get(i) - ints.get(i - 1));
        }
        return rval;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (null == score) {
            return "ScoreAnalysis:Uninitialized";
        }
        sb.append("ScoreAnalysis[Score size:");
        sb.append(getAllNotes().size());
        sb.append(",Closest Scale:");
        sb.append(cacheClosestScaleFit);
        sb.append(",Percent in Closest Scale:");
        sb.append(Util.format(cacheClosestScaleFitPercent));
        sb.append(",Percent in Closest Scale Big Five:");
        sb.append(Util.format(cacheClosestScaleBigFiveFitPercent));
        sb.append(",Closest Signature:");
        sb.append(cacheClosestTimeSignatureFit);
        sb.append(",Percent in Closest Signature:");
        sb.append(Util.format(cacheClosestTimeSignatureFitPercent));
        sb.append(",Median Note:");
        sb.append(medianNote);
        sb.append(",Mean Note:");
        sb.append(Util.format(meanNote));
        sb.append(",Note Std Dev:");
        sb.append(Util.format(noteStandardDeviation));
        sb.append(",Note Direction Changes:");
        sb.append(noteDirectionChanges);
        sb.append("]");
        return sb.toString();
    }

    public Double getMeanNote() {
        return meanNote;
    }

    public Integer getMedianNote() {
        return medianNote;
    }

    public Double getNoteStandardDeviation() {
        return noteStandardDeviation;
    }

    public ScaleEnum closestScaleFit() {
        if (null == cacheClosestScaleFit) {
            ScaleEnum[] scales = ScaleEnum.values();
            cacheClosestScaleFitPercent = 0.0;
            for (int i = 0; i < scales.length; i++) {
                double test = scales[i].percentInScale(getAllNotes());
                if (test > cacheClosestScaleFitPercent) {
                    cacheClosestScaleFitPercent = test;
                    cacheClosestScaleBigFiveFitPercent = scales[i].percentInScaleBigFive(getAllNotes());
                    cacheClosestScaleFit = scales[i];
                }
            }
        }
        return cacheClosestScaleFit;
    }

    public double percentInClosestScale() {
        if (null == cacheClosestScaleFitPercent) {
            closestScaleFit(); // performs the load
        }
        return cacheClosestScaleFitPercent;
    }

    public double percentInClosestScaleBigFive() {
        if (null == cacheClosestScaleBigFiveFitPercent) {
            closestScaleFit(); // performs the load
        }
        return cacheClosestScaleBigFiveFitPercent;
    }

    public TimeSignatureEnum closestTimeSignatureFit() {
        if (null == cacheClosestTimeSignatureFit) {
            TimeSignatureEnum[] signatures = TimeSignatureEnum.values();
            cacheClosestTimeSignatureFitPercent = 0.0;
            for (int i = 0; i < signatures.length; i++) {
                double test = signatures[i].percentInTime(getAllNotes());
                if (test > cacheClosestTimeSignatureFitPercent) {
                    cacheClosestTimeSignatureFitPercent = test;
                    cacheClosestTimeSignatureFit = signatures[i];
                }
            }
        }
        return cacheClosestTimeSignatureFit;

    }

    public double percentInClosestTimeSignature() {
        if (null == cacheClosestTimeSignatureFitPercent) {
            closestTimeSignatureFit(); // performs the load
        }
        return cacheClosestTimeSignatureFitPercent;
    }

    private void buildStatistics() {
        tonalValues = new TreeMap<Integer, Integer>();// Notevalue->count
        timeValues = new TreeMap<Double, Integer>();// Notevalue->count
        List<Note> p = getAllNotes();
        int sum = 0;
        allNotesSorted = new ArrayList<Integer>(p.size());
        noteDeltaList = new ArrayList<Integer>(p.size() - 1);
        timeDeltaList = new ArrayList<Double>(p.size() - 1);
        noteDirectionChanges = 0; // initialize
        for (int i = 0; i < p.size(); i++) {
            Integer noteValue = p.get(i).getPitch();
            allNotesSorted.add(noteValue);

            if (i > 0) {
                noteDeltaList.add(noteValue - p.get(i - 1).getPitch());
                timeDeltaList.add(p.get(i).getRhythmValue() - p.get(i - 1).getRhythmValue());
            }

            // Build tonal value map
            if (null == tonalValues.get(noteValue)) {
                tonalValues.put(noteValue, new Integer(1));
            } else {
                tonalValues.put(noteValue, tonalValues.get(noteValue) + 1);
            }
            // Build time value map
            Double timeValue = p.get(i).getRhythmValue();
            if (null == timeValues.get(timeValue)) {
                timeValues.put(timeValue, new Integer(1));
            } else {
                timeValues.put(timeValue, timeValues.get(timeValue) + 1);
            }
            // Calculate sum note
            sum += noteValue;
            // check direction changes
            if (i > 1) {
                if (p.get(i - 2).getPitch() < p.get(i - 1).getPitch()) {
                    if (p.get(i - 1).getPitch() > p.get(i).getPitch()) {
                        noteDirectionChanges++;
                    }
                } else if (p.get(i - 2).getPitch() > p.get(i - 1).getPitch()) {
                    if (p.get(i - 1).getPitch() < p.get(i).getPitch()) {
                        noteDirectionChanges++;
                    }
                }
            }
        }
        Collections.sort(allNotesSorted);
        // Calc stats
        meanNote = (double) sum / (double) p.size();
        medianNote = allNotesSorted.get(p.size() / 2);
        noteStandardDeviation = calculateStandardDeviation(allNotesSorted, meanNote);

    }

    private void analyze() {
        closestScaleFit(); // Calculate closest scale
        closestTimeSignatureFit(); // Calculate closest time signature
        buildStatistics();
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score s) {
        score = s;
        analyze();
    }

    public List<Note> getAllNotes() {
        if (null == score) {
            return null;
        }
        if (cacheAllNotes == null) {
            cacheAllNotes = partToNoteList(score.getPart(0));
        }
        return cacheAllNotes;
    }

    private Double calculateStandardDeviation(List<Integer> values, Double meanValue) {
        if (values == null || meanValue == null || values.size() == 0) {
            throw new IllegalArgumentException("Cannot calculate stddev of empty list, or without mean value");
        }
        double sum = 0;
        double term = 0;
        for (int i = 0; i < values.size(); i++) {
            term = values.get(i) - meanValue;
            sum += term * term;
        }
        sum /= values.size();
        return Math.sqrt(sum);
    }

    public Integer getNoteDirectionChanges() {
        return noteDirectionChanges;
    }

    public List<Integer> getAllNotesSorted() {
        return Collections.unmodifiableList(allNotesSorted);
    }

    public List<Integer> getNoteDeltaList() {
        return Collections.unmodifiableList(noteDeltaList);
    }

    public List<Double> getTimeDeltaList() {
        return Collections.unmodifiableList(timeDeltaList);
    }

}
