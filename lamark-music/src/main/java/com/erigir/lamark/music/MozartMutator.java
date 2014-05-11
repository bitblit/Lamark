package com.erigir.lamark.music;

import com.erigir.lamark.IMutator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.music.phrase.PhrasePool;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.Properties;

public class MozartMutator implements IMutator<Score> {
    private double pMutation;
    private Lamark lamark;

    public void mutate(Individual<Score> arg0) {
        Score s =arg0.getGenome();
        Phrase[] pha = s.getPart(0).getPhraseArray();
        pha[lamark.getRandom().nextInt(pha.length)] = PhrasePool.instance.getPhrase();
    }

    public void setMutationProbability(double arg0) {
        pMutation = arg0;
    }

    public void configure(Properties ignored) {
    }

    public Class worksOn() {
        return Score.class;
    }

    public Lamark getLamark() {
        return lamark;
    }

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }

}
