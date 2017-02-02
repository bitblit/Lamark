package com.erigir.lamark.music;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.music.phrase.PhrasePool;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.Properties;
import java.util.function.Function;

public class MozartMutator extends AbstractLamarkComponent implements Function<Score,Score> {
    private double pMutation;
    private Lamark lamark;


    @Override
    public Score apply(Score s) {
        Phrase[] pha = s.getPart(0).getPhraseArray();
        pha[rand().nextInt(pha.length)] = PhrasePool.instance.getPhrase();
        return s; // TODO: should be new copy?
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
