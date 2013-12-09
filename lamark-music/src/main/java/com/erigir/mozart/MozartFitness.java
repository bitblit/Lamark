package com.erigir.mozart;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.erigir.mozart.traits.TraitWrapper;

import jm.music.data.Score;
import com.erigir.lamark.FitnessTypeEnum;
import com.erigir.lamark.IFitnessFunction;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Util;
import com.erigir.lamark.configure.LamarkConfig;

public class MozartFitness implements IFitnessFunction
{
	private static Logger LOG = Logger.getLogger(MozartFitness.class.getName());
    private Collection<TraitWrapper> traits;
    private LamarkConfig config;
    
    public void initTraits(Collection<TraitWrapper> pTraits)
    {
    	traits = pTraits;
    }
    
    public Collection<TraitWrapper> getTraits()
    {
    	return traits;
    }
    
    public double maximumScore()
    {
        double rval =0;
        if (null!=traits)
        {
        for (TraitWrapper tw:traits)
        {
            rval+=tw.getWeight()*100;
        }
        }
        return rval;
    }
    
    public double fitnessValue(Individual arg0)
    {
    	if (null==traits)
    	{
    		throw new IllegalStateException("Traits must be set before calculating");
    	}
    	long start = System.currentTimeMillis();
        double fullScore = 0.0;
        Score theScore = (Score)arg0.getGenome();
        ScoreAnalysis sa = new ScoreAnalysis(theScore);
        
        StringBuffer fitnessVals = new StringBuffer();
        fitnessVals.append("(");        
        for (TraitWrapper tw : traits)
        {
            tw.getTrait().setScoreAnalysis(sa);
            fullScore+=tw.getWeightedFitness();
            fitnessVals.append(tw.getTrait().getClass().getSimpleName()+" = "+Util.format(tw.getFitness())+"\n");
        }
        fitnessVals.append(")");

        arg0.setAttribute("ANALYSIS",sa);
        arg0.setAttribute("SCORES",fitnessVals.toString());
        
        return fullScore;
    }

    public FitnessTypeEnum fitnessType()
    {
        return FitnessTypeEnum.MAXIMUM_BEST;
    }

    public Class worksOn()
    {
        return Score.class;
    }

    public void configure(Properties ignored)
    {
    }
    
    public void setLamarkConfig(LamarkConfig pConfig)
    {
        config = pConfig;
    }

    
    
}
