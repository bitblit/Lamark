package com.erigir.lamark.config;

import com.erigir.lamark.DefaultIndividualFormatter;
import com.erigir.lamark.creator.AlphaStringCreator;
import com.erigir.lamark.crossover.ListSinglePoint;
import com.erigir.lamark.fitness.AllOnes;
import com.erigir.lamark.mutator.ListSimpleMutator;
import com.erigir.lamark.selector.Tournament;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * Created by chrweiss on 8/24/14.
 */
public class TestLamarkComponentFinder {
    private static final Logger LOG = LoggerFactory.getLogger(TestLamarkComponentFinder.class);

    private LamarkComponentFinder finder;

    @Before
    public void setup()
    {
        finder = new LamarkComponentFinder(Arrays.asList("com.erigir.lamark"));
    }

    @Test
    public void testFindComponents()
    {
        LOG.info("Found components {}\ncreators {}",finder.getComponentClasses(), finder.getCreators());

        assertTrue(finder.getComponentClasses().contains(AlphaStringCreator.class));
        assertTrue(finder.getComponentClasses().contains(ListSinglePoint.class));
        assertTrue(finder.getComponentClasses().contains(AllOnes.class));
        assertTrue(finder.getComponentClasses().contains(ListSimpleMutator.class));
        assertTrue(finder.getComponentClasses().contains(Tournament.class));
        assertTrue(finder.getComponentClasses().contains(DefaultIndividualFormatter.class));
        assertTrue(finder.getComponentClasses().contains(AlphaStringCreator.class));
    }
}
