package com.erigir.lamark.music.traits;

/**
 * A trait that calculates which time signature the score comes
 * closest to being in, and then allocates 1 point for each percent
 * of the song in that key (calculated from start to end... ie, if
 * the music gets 65% of the way through the piece before breaking
 * time signature (in the closest fit), this will give it 65 points)
 *
 * @author cweiss
 */

public class PercentInTimeSignatureTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {
        return this.getScoreAnalysis().percentInClosestTimeSignature() * 100.0;
    }
}
