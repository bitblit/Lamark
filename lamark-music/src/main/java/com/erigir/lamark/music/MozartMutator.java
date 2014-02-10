package com.erigir.lamark.music;

import com.erigir.lamark.IMutator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Util;
import com.erigir.lamark.configure.LamarkConfig;
import com.erigir.mozart.phrase.PhrasePool;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.Properties;

public class MozartMutator implements IMutator {
    private double pMutation;

    public boolean mutate(Individual arg0) {
        if (Util.flip(pMutation)) {
            Score s = (Score) arg0.getGenome();
            Phrase[] pha = s.getPart(0).getPhraseArray();
            pha[Util.RAND.nextInt(pha.length)] = PhrasePool.instance.getPhrase();
            return true;
        } else {
            return false;
        }
    }

    public void setMutationProbability(double arg0) {
        pMutation = arg0;
    }

    public void configure(Properties ignored) {
    }

    public Class worksOn() {
        return Score.class;
    }

    public void setLamarkConfig(LamarkConfig ignored) {
    }

}
