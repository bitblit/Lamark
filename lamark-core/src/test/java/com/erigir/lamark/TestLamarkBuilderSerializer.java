package com.erigir.lamark;

import com.erigir.lamark.crossover.StringSinglePoint;
import com.erigir.lamark.fitness.StringFinderFitness;
import com.erigir.lamark.mutator.StringSimpleMutator;
import com.erigir.lamark.selector.TournamentSelector;
import com.erigir.lamark.supplier.AlphaStringSupplier;
import com.erigir.lamark.supplier.StringSupplier;
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
        LamarkBuilder lb = createBuilder();

        String serial = LamarkBuilderSerializer.serialize(lb);

        LOG.info("s:{}",serial);

        LamarkBuilder lb2 = LamarkBuilderSerializer.deserialize(serial);

        assertEquals(lb.getSupplier().getClass(), lb2.getSupplier().getClass());
        assertEquals(lb.getCrossover().getClass(), lb2.getCrossover().getClass());
        assertEquals(lb.getSelector().getClass(), lb2.getSelector().getClass());
        assertEquals(lb.getFitnessFunction().getClass(), lb2.getFitnessFunction().getClass());
        assertEquals(lb.getMutator().getClass(), lb2.getMutator().getClass());

        if (lb.getFormatter()!=null)
        {
            assertEquals(lb.getFormatter().getClass(), lb2.getFormatter().getClass());
        }

        assertEquals(lb.getCrossoverProbability(), lb2.getCrossoverProbability(),0);

        StringSupplier sc = (StringSupplier)lb.getSupplier();
        StringSupplier sc2 = (StringSupplier)lb.getSupplier();

        assertEquals(sc.getSize(), sc2.getSize());

    }

    public LamarkBuilder<String> createBuilder()
    {
        return new LamarkBuilder<String>()
                .withSupplier(new AlphaStringSupplier(6))
                .withCrossover(new StringSinglePoint())
                .withFitnessFunction(new StringFinderFitness("LAMARK"))
                .withMutator(new StringSimpleMutator())
                .withSelector(new TournamentSelector<>())
                .withPopulationSize(50)
                .withMutationProbability(.01)
                .withCrossoverProbability(1.0)
                .withUpperElitism(.1)
                .withLowerElitism(.1)
                .withTargetScore(1.0);
    }


}
