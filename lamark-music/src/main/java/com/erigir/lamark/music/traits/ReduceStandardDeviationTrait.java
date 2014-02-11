package com.erigir.lamark.music.traits;


/**
 * This trait assumes that most music doesnt vary wildly across
 * the scale, but rather tends to cluster in one section.
 * (Note : in future releases, this should probably be broken
 * into "windows", which would allow alternating high and
 * low sections)
 * <p/>
 * Since the range is 0-127, stddev will never be > 127.
 * Since the ideal stdev is NOT 0 (all one note) but is
 * probably less than 8 (one octave), we'll divide the
 * stddev by 2, leaving 1 of 64 blocks, and subtract from
 * 64, then normalize to 100.
 * <p/>
 * Other parts of the overall function will be responsible
 * for making sure that "all one note" doesnt become the
 * highest scorer in this
 *
 * @author cweiss
 */

public class ReduceStandardDeviationTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {

        double stdDev = getScoreAnalysis().getNoteStandardDeviation();
        return Math.max(0, 100 - (stdDev * stdDev));
        
        /*
        double rval = Math.floor(stdDev/2.0);
        rval = 64.0-rval;
        rval = rval*(100.0/64.0);
        return rval;*/
    }
}
