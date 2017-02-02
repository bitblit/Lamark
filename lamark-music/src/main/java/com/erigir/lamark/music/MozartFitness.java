package com.erigir.lamark.music;

import com.erigir.lamark.*;
import com.erigir.lamark.music.traits.TraitWrapper;
import jm.music.data.Score;

import java.util.Collection;
import java.util.Properties;
import java.util.function.ToDoubleFunction;
import java.util.logging.Logger;

public class MozartFitness implements ToDoubleFunction<Score>{
    private static Logger LOG = Logger.getLogger(MozartFitness.class.getName());
    private Collection<TraitWrapper> traits;

    public void initTraits(Collection<TraitWrapper> pTraits) {
        traits = pTraits;
    }

    public Collection<TraitWrapper> getTraits() {
        return traits;
    }

    public double maximumScore() {
        double rval = 0;
        if (null != traits) {
            for (TraitWrapper tw : traits) {
                rval += tw.getWeight() * 100;
            }
        }
        return rval;
    }

    @Override
    public double applyAsDouble(Score theScore) {
        if (null == traits) {
            throw new IllegalStateException("Traits must be set before calculating");
        }
        long start = System.currentTimeMillis();
        double fullScore = 0.0;
        ScoreAnalysis sa = new ScoreAnalysis(theScore);

        StringBuffer fitnessVals = new StringBuffer();
        fitnessVals.append("(");
        for (TraitWrapper tw : traits) {
            tw.getTrait().setScoreAnalysis(sa);
            fullScore += tw.getWeightedFitness();
            fitnessVals.append(tw.getTrait().getClass().getSimpleName() + " = " + LamarkUtil.format(tw.getFitness()) + "\n");
        }
        fitnessVals.append(")");

        /*
        arg0.setAttribute("ANALYSIS", sa);
        arg0.setAttribute("SCORES", fitnessVals.toString());
        */

        return fullScore;
    }


}
