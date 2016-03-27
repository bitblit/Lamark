package com.erigir.lamark;

import com.erigir.lamark.creator.StringCreator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class TestLamarkBuilderSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(TestLamarkBuilderSerializer.class);

    @Test
    public void testRoundTrip()
    {
        MyFirstLamark mfl = new MyFirstLamark();
        LamarkBuilder lb = mfl.createBuilder();

        String serial = LamarkBuilderSerializer.serialize(lb);

        LOG.info("s:{}",serial);

        LamarkBuilder lb2 = LamarkBuilderSerializer.deserialize(serial);

        assertEquals(lb.getCreator().getClass(), lb2.getCreator().getClass());
        assertEquals(lb.getCrossover().getClass(), lb2.getCrossover().getClass());
        assertEquals(lb.getSelector().getClass(), lb2.getSelector().getClass());
        assertEquals(lb.getFitnessFunction().getClass(), lb2.getFitnessFunction().getClass());
        assertEquals(lb.getMutator().getClass(), lb2.getMutator().getClass());

        if (lb.getFormatter()!=null)
        {
            assertEquals(lb.getFormatter().getClass(), lb2.getFormatter().getClass());
        }

        assertEquals(lb.getCrossoverProbability(), lb2.getCrossoverProbability(),0);

        StringCreator sc = (StringCreator)lb.getCreator();
        StringCreator sc2 = (StringCreator)lb.getCreator();

        assertEquals(sc.getSize(), sc2.getSize());

    }

}
