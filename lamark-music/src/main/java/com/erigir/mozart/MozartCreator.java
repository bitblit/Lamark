package com.erigir.mozart;

import java.util.Properties;

import com.erigir.mozart.phrase.PhrasePool;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.data.Tempo;
import com.erigir.lamark.ICreator;
import com.erigir.lamark.Individual;
import com.erigir.lamark.configure.LamarkConfig;

public class MozartCreator implements ICreator
{
    private Properties properties;
    private PhrasePool pool;
    private int size;
	private ScaleEnum scale;
	private TimeSignatureEnum signature;
    private Integer lowerBound;
    private Integer upperBound;
	private boolean validated = false;
	
    public Properties getProperties()
    {
    	return properties;
    }
    
    public Individual create()
    {
    	Score s = new Score();
    	if (null!=signature)
    	{
    		s.setDenominator(signature.denominator());
    		s.setNumerator(signature.numerator());
    	}
    	if (null!=scale)
    	{
    		s.setKeySignature(scale.sharpOrFlatCount());
    	}
    	s.setTempo(Tempo.ANDANTE);
    	Part p = new Part();
    	for (int i=0;i<size;i++)
    	{
    		p.appendPhrase(pool.getPhrase());
    	}
    	s.add(p);
    	
        Individual i = new Individual(s);
        return i;
    }

    public String translate(Individual arg0)
    {
        ScoreAnalysis sa = (ScoreAnalysis)arg0.getAttribute("ANALYSIS");
        String scores = (String)arg0.getAttribute("SCORES");
        if (null!=sa)
        {
            return sa.toString()+" SCORES:"+scores;
        }
        else
        {
            Score s = (Score)arg0.getGenome();
            return "Score, Size="+s.getPart(0).getPhrase(0).getSize()+" SCORES:"+scores;
        }
    }
	private void validate()
	{
		if (!validated)
		{
		if (null==properties)
		{
			throw new IllegalStateException("Properties object not set");
		}
		String keyS = properties.getProperty("key");
		String signatureS = properties.getProperty("signature");
		
		if (keyS==null)
		{
			throw new IllegalStateException("Property 'key' object not set");
		}
		if (!keyS.equals("ANY"))
		{
			scale = ScaleEnum.valueOf(keyS);
		}
		if (signatureS==null)
		{
			throw new IllegalStateException("Property 'signature' object not set");
		}
		if (!signatureS.equals("ANY"))
		{
			signature = TimeSignatureEnum.valueOf(signatureS);
		}
        if (properties.getProperty("lower.bound")!=null)
        {
            lowerBound = Integer.parseInt(properties.getProperty("lower.bound"));
        }
        else
        {
            lowerBound=0;
        }
        if (properties.getProperty("upper.bound")!=null)
        {
            upperBound = Integer.parseInt(properties.getProperty("upper.bound"));
        }
        else
        {
            upperBound=128;
        }
		pool = PhrasePool.instance;
        pool.initialize(signature,scale,lowerBound,upperBound);
		
		validated=true;
		}
	}

    public Class worksOn()
    {
        return Score.class;
    }

    public void setSize(int pSize)
    {
    	size = pSize;
    }
    public void configure(Properties props)
    {
    	properties = props;
    	validate();
    }

	public void setLamarkConfig(LamarkConfig ignored)
	{
	}
    
}
