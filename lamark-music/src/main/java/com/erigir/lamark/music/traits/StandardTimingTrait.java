package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.NoteLengthDistribution;

/**
 * This trait rewards music that conforms to the standard usage of
 * note durations based on analysis of existing music
 *
 * @author cweiss
 */

public class StandardTimingTrait extends AbstractMusicTrait {

    @Override
    public double guardedFitness() {
        NoteLengthDistribution nld = new NoteLengthDistribution();
        nld.initialize(getScoreAnalysis().getAllNotes());
        return nld.correlation() * 100.0;
    }
}