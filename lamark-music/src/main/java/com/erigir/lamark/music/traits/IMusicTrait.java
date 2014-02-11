package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.ScoreAnalysis;

public interface IMusicTrait {
    public void setScoreAnalysis(ScoreAnalysis sa);

    public double getFitness();
}
