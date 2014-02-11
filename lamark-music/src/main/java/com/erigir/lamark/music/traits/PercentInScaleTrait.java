package com.erigir.lamark.music.traits;

/**
 * A trait that calculates which musical scale the score comes
 * closest to being in, and then allocates 1 point for each percent
 * of the song in that key.
 *
 * @author cweiss
 */

public class PercentInScaleTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {
        return this.getScoreAnalysis().percentInClosestScale() * 100.0;
    }
}
