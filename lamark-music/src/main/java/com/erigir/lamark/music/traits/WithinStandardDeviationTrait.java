package com.erigir.lamark.music.traits;

import java.util.List;
import java.util.logging.Logger;


/**
 * This trait assumes that most music centers around a given key,
 * and in general is played around the "middle" octave of that
 * key (for middle c in midi, this is note 60).  This trait attempts
 * to give points for keeping the notes in a bell curve centered about
 * the main middle key.
 *
 * @author cweiss
 */

public class WithinStandardDeviationTrait extends AbstractMusicTrait {
    public static double ACCEPTABLE_STD_DEVIATIONS = 2.0;
    private static Logger LOG = Logger.getLogger(WithinStandardDeviationTrait.class.getName());

    @Override
    public double guardedFitness() {
        double stdDev = getScoreAnalysis().getNoteStandardDeviation();
        double mean = getScoreAnalysis().getMeanNote();

        int bottomAcceptedRange = (int) Math.max(0, mean - (ACCEPTABLE_STD_DEVIATIONS * stdDev));
        int topAcceptedRange = (int) Math.min(127, mean + (ACCEPTABLE_STD_DEVIATIONS * stdDev));

        LOG.fine("StdDev=" + stdDev + " Mean=" + mean + " Valid range=[" + bottomAcceptedRange + "," + topAcceptedRange + "]");
        int countInRange = 0;
        List<Integer> notesSorted = getScoreAnalysis().getAllNotesSorted();
        int size = notesSorted.size();
        for (Integer i : notesSorted) {
            if (i >= bottomAcceptedRange && i <= topAcceptedRange) {
                countInRange++;
            }
        }
        LOG.fine("Size=" + size + " countInRange=" + countInRange);

        // Here we give points for the percentae of notes within (2) standard deviations of the middle
        // key (not the mean)
        double rval = ((double) countInRange / (double) notesSorted.size()) * 100.0;
        return rval;
    }
}
