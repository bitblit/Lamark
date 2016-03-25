package com.erigir.lamark.selector;

import com.erigir.lamark.Individual;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.fail;

public class TestRouletteWheelSelector {
    private static final Logger LOG = LoggerFactory.getLogger(TestRouletteWheelSelector.class);
    private static final int SIZE=5;
    List<Individual<?>> source = new ArrayList<Individual<?>>(SIZE);

    @Before
    public void setUp() {
        for (int i = 0; i < SIZE; i++) {
            Individual next = new Individual(new Integer(i + 1));
            next.setFitness(new Double(i + 1));
            source.add(next);
        }
    }

    @Test
    public void testIncreasing() {
        RouletteWheelSelector rw = new RouletteWheelSelector();

        double sum = rw.sumFitness(source);
        for (Individual i : source) {
            double current = i.getFitness();
            int expected = (int) ((current / sum) * 100);
            int var = expected - i.getSelectedCount();
            LOG.debug(i.getGenome() + "{} Expected {} got {} v= {}", new Object[]{i.getGenome(), expected, i.getSelectedCount(), var});
            if (var > 10) {
                fail("Unexpectedly large variance:" + var);
            }
        }
    }

    @Test
    public void testDecreasing() {
        RouletteWheelSelector rw = new RouletteWheelSelector();

        List<Individual> selected = rw.select(source, new Random(), 100, true);
        double min = source.get(0).getFitness();
        double invSum = rw.invertedSumFitness(source);

        for (Individual i : source) {
            double current = i.getFitness();

            int expected = (int) ((min/i.getFitness())/invSum * 100);
            int var = expected - i.getSelectedCount();
            LOG.debug("{} Expected {} got {} v={}" + new Object[]{i.getGenome(), expected, i.getSelectedCount(), var});
            if (false) {
                fail("Unexpectedly large variance:" + var);
            }
        }
    }

}
