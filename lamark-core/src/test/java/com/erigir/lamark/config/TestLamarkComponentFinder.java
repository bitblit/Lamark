package com.erigir.lamark.config;

import com.erigir.lamark.DefaultIndividualFormatter;
import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.builtin.StringCreator;
import com.erigir.lamark.builtin.ListCrossover;
import com.erigir.lamark.builtin.ListSimpleMutator;
import com.erigir.lamark.selector.Tournament;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chrweiss on 8/24/14.
 */
public class TestLamarkComponentFinder {
    private static final Logger LOG = LoggerFactory.getLogger(TestLamarkComponentFinder.class);

    private LamarkComponentFinder finder;

    @Before
    public void setup()
    {
        finder = new LamarkComponentFinder(Arrays.asList("com.erigir"));
    }

    @Test
    public void testFindComponents()
    {
        LOG.info("Found components {}\ncreators {}",finder.getComponentClasses(), finder.getCreators());

        assertTrue(finder.getComponentClasses().contains(StringCreator.class));
        assertTrue(finder.getComponentClasses().contains(ListSimpleMutator.class));
        assertTrue(finder.getComponentClasses().contains(DefaultIndividualFormatter.class));
        assertTrue(finder.getComponentClasses().contains(ListCrossover.class));
    }

    @Test
    public void testListAsWrappers()
    {
        LOG.info("Found components {}\ncreators {}",finder.getComponentClasses(), finder.getCreators());

        List<DynamicMethodWrapper<Creator>> l = finder.listAsWrappers(Creator.class);

        LOG.info("Found : {} : {}",l.size(),l);

    }

}
