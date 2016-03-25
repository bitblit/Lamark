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
    private static final int RUNCOUNT=100000;
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

        List<Individual> selected = rw.select(source, new Random(), RUNCOUNT, false);
        selected.stream().forEach((p)->p.incrementSelected());

        int totalVariance = 0;

        double sum = rw.sumFitness(source);
        for (Individual i : source) {
            double current = i.getFitness();
            int expected = (int) ((current / sum) * RUNCOUNT);
            int var = expected - i.getSelectedCount();
            totalVariance += Math.abs(var);

            LOG.debug(i.getGenome() + "{} Expected {} got {} v= {}", new Object[]{i.getGenome(), expected, i.getSelectedCount(), var});
        }

        double pctVar = (double)totalVariance/RUNCOUNT;
        LOG.info("Variance : {} total {} pct", totalVariance, pctVar*100);
        if (pctVar>.01)
        {
            fail("Unexpectedly large variance of "+totalVariance+" over "+RUNCOUNT+" selections ("+pctVar*100+" %)");
        }

    }

    @Test
    public void testDecreasing() {
        RouletteWheelSelector rw = new RouletteWheelSelector();

        List<Individual> selected = rw.select(source, new Random(), RUNCOUNT, true);
        selected.stream().forEach((p)->p.incrementSelected());
        double min = source.get(0).getFitness();
        double invSum = rw.invertedSumFitness(source);

        int totalVariance = 0;

        for (Individual i : source) {
            double current = i.getFitness();

            int expected = (int) ((min/current)/invSum * RUNCOUNT);
            int selCount = i.getSelectedCount();
            int var = expected - selCount;
            LOG.debug("{} Expected {} got {} v={}" , new Object[]{i.getGenome(), expected, i.getSelectedCount(), var});
            totalVariance += Math.abs(var);
        }

        double pctVar = (double)totalVariance/RUNCOUNT;
        LOG.info("Variance : {} total {} pct", totalVariance, pctVar*100);
        if (pctVar>.01)
        {
            fail("Unexpectedly large variance of "+totalVariance+" over "+RUNCOUNT+" selections ("+pctVar*100+" %)");
        }

    }

}
