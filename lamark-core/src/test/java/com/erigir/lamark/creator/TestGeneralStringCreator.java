package com.erigir.lamark.creator;

import com.erigir.lamark.AnnotationUtil;
import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.Param;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by chrweiss on 8/28/14.
 */
public class TestGeneralStringCreator {
    private static final Logger LOG = LoggerFactory.getLogger(TestGeneralStringCreator.class);
    private GeneralStringCreator creator = new GeneralStringCreator();

    @Test
    public void testCreate()
    {
        Random rand = new Random(1);
        String s = creator.createString(5,rand,"0123");
        assertEquals("20110",s);
    }

    @Test
    public void testContext()
            throws Exception
    {
        Map<String,Object> context = new TreeMap<>();

        context.put("size",5);
        context.put("random", new Random(1));
        context.put("validCharacters","0123");

        Method m = GeneralStringCreator.class.getMethod("createString",new Class[]{Integer.class, Random.class, String.class});
        DynamicMethodWrapper<Creator> dmw = new DynamicMethodWrapper<>(creator, m, m.getAnnotation(Creator.class));

        LOG.info("Found param list: {}", Arrays.asList(dmw.getParameterList()));

        String s = dmw.buildAndExecute(context, String.class);
        assertEquals("20110",s);
    }



}
