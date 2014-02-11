package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.ScoreAnalysis;
import jm.music.data.Score;

public abstract class AbstractMusicTrait implements IMusicTrait {
    private ScoreAnalysis analysis;

    public void setScoreAnalysis(ScoreAnalysis sa) {
        analysis = sa;
    }

    public ScoreAnalysis getScoreAnalysis() {
        return analysis;
    }

    public Score getScore() {
        if (null == analysis) {
            return null;
        }
        return analysis.getScore();
    }

    public double getFitness() {
        if (null == analysis) {
            throw new IllegalStateException("Score Analysis may not be null - must set prior to fitness call");
        }
        if (null == analysis.getScore()) {
            throw new IllegalStateException("Score may not be null - must set prior to fitness call");
        }
        return guardedFitness();
    }

    public abstract double guardedFitness();

}
