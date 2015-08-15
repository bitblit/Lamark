package com.erigir.lamark.config;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.creator.AlphaAndSpaceStringCreator;
import com.erigir.lamark.crossover.StringSinglePoint;
import com.erigir.lamark.fitness.AllOnes;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.RouletteWheel;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cweiss on 7/17/15.
 */
public class TestLamarkBootstrapper {
    private static final Logger LOG = LoggerFactory.getLogger(TestLamarkBootstrapper.class);

    @Test
    public void testLamarkBootstrapper()
    {
        AlphaAndSpaceStringCreator creator = new AlphaAndSpaceStringCreator();
        StringSinglePoint crossover = new StringSinglePoint();
        AllOnes ones = new AllOnes();
        StringSimpleMutator mutator = new StringSimpleMutator();
        RouletteWheel wheel = new RouletteWheel();

        List<Object> holder = Arrays.asList(new Object[]{creator, crossover, ones, mutator, wheel});

        Lamark instance = LamarkBootstrapper.createLamark(holder);

        assertNotNull(instance.getCreator());
        assertEquals(instance.getCrossover().getTargetObject(), crossover);

        System.out.println("Crossover:"+instance.getCrossover());

    }
}
