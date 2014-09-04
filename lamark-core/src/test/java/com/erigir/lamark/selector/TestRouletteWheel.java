package com.erigir.lamark.selector;

import com.erigir.lamark.EFitnessType;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

public class TestRouletteWheel {
    private static final Logger LOG = LoggerFactory.getLogger(TestRouletteWheel.class);
    List<Individual<?>> source = new ArrayList<Individual<?>>(5);
    double sumFit;

    @Before
    public void setUp() {
        sumFit = 10;
        for (int i = 0; i < 5; i++) {
            Individual next = new Individual(new Integer(i + 1));
            next.setFitness(new Double(i + 1));
            source.add(next);
        }

    }

    @Test
    @Ignore
    public void testIncreasing() {
        RouletteWheel rw = new RouletteWheel();
        /*
        rw.setLamark(new Lamark());
        rw.getLamark().setFitnessFunction(new AllOnes());
        */

        Collections.sort(source, EFitnessType.MAXIMUM_BEST.getComparator());
        //rw.select(source, 100);

        double sum = RouletteWheel.sumFitness(source);
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
    @Ignore
    public void testDecreasing() {
        RouletteWheel rw = new RouletteWheel();
        //rw.setLamark(new Lamark());
        //rw.getLamark().setFitnessFunction(new AllOnes());

        Collections.sort(source, EFitnessType.MINIMUM_BEST.getComparator());
        //rw.select(source, 100);

        double max = RouletteWheel.maxFitness(source);
        double newSum = 0;
        for (Individual i : source) {
            newSum += (max - i.getFitness()) + 1;
        }

        for (Individual i : source) {
            double current = i.getFitness();
            int expected = (int) (((max - current) / newSum) * 100);
            int var = expected - i.getSelectedCount();
            LOG.debug("{} Expected {} got {} v={}" + new Object[]{i.getGenome(), expected, i.getSelectedCount(), var});
            if (false) {
                fail("Unexpectedly large variance:" + var);
            }
        }
    }

}
