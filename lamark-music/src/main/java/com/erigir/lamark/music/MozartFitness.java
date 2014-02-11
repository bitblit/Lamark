package com.erigir.lamark.music;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.Util;
import com.erigir.lamark.music.traits.TraitWrapper;
import jm.music.data.Score;

import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

public class MozartFitness implements IFitnessFunction<Score> {
    private static Logger LOG = Logger.getLogger(MozartFitness.class.getName());
    private Collection<TraitWrapper> traits;
    private Lamark lamark;

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

    public double fitnessValue(Individual<Score> arg0) {
        if (null == traits) {
            throw new IllegalStateException("Traits must be set before calculating");
        }
        long start = System.currentTimeMillis();
        double fullScore = 0.0;
        Score theScore = arg0.getGenome();
        ScoreAnalysis sa = new ScoreAnalysis(theScore);

        StringBuffer fitnessVals = new StringBuffer();
        fitnessVals.append("(");
        for (TraitWrapper tw : traits) {
            tw.getTrait().setScoreAnalysis(sa);
            fullScore += tw.getWeightedFitness();
            fitnessVals.append(tw.getTrait().getClass().getSimpleName() + " = " + Util.format(tw.getFitness()) + "\n");
        }
        fitnessVals.append(")");

        arg0.setAttribute("ANALYSIS", sa);
        arg0.setAttribute("SCORES", fitnessVals.toString());

        return fullScore;
    }

    public EFitnessType fitnessType() {
        return EFitnessType.MAXIMUM_BEST;
    }

    public Class worksOn() {
        return Score.class;
    }

    public void configure(Properties ignored) {
    }

    public Lamark getLamark() {
        return lamark;
    }

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }


}
