package com.erigir.lamark.fitness;

import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.FitnessFunction;
import com.erigir.lamark.creator.GeneralStringCreator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by chrweiss on 8/28/14.
 */
public class TestStringFinderFitness {
    private static final Logger LOG = LoggerFactory.getLogger(TestStringFinderFitness.class);
    private StringFinderFitness fitness = new StringFinderFitness();

    @Test
    public void testCreate()
    {
        double t = fitness.fitnessValue("TEST", "TEST");
        assertEquals((Double)1.0,(Double)t);
    }

    @Test
    public void testContext()
            throws Exception
    {
        Map<String,Object> context = new TreeMap<>();
        context.put("target","TEST");

        Method m = StringFinderFitness.class.getMethod("fitnessValue",new Class[]{ String.class, String.class});
        DynamicMethodWrapper<FitnessFunction> dmw = new DynamicMethodWrapper<>(fitness, m, m.getAnnotation(FitnessFunction.class));

        LOG.info("Found param list: {}", Arrays.asList(dmw.getParameterList()));

        Double t = dmw.buildAndExecute(context, "TEST", Double.class);
        assertEquals((Double)1.0,(Double)t);
    }



}
