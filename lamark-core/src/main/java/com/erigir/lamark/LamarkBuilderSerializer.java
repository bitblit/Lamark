package com.erigir.lamark;

import com.erigir.lamark.selector.Selector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Created by cweiss1271 on 3/26/16.
 */
public class LamarkBuilderSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkBuilderSerializer.class);
    private static ObjectMapper OBJECT_MAPPER = createMapper();

    private static ObjectMapper createMapper()
    {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return rval;
    }


    private static void putClassName(Map<String,String> map, String key, Object value)
    {
        if (map!=null && key!=null && value!=null)
        {
            map.put(key, value.getClass().getName());
        }
    }

    public static String serialize(LamarkBuilder builder)
    {
        Objects.requireNonNull(builder);

        Map<String,String> classNames = new TreeMap<>();
        putClassName(classNames, "selector", builder.getSelector());
        putClassName(classNames, "creator", builder.getCreator());
        putClassName(classNames, "fitnessFunction", builder.getFitnessFunction());
        putClassName(classNames, "crossover", builder.getCrossover());
        putClassName(classNames, "mutator", builder.getMutator());
        putClassName(classNames, "formatter", builder.getFormatter());

        Map<String,Object> holder = new TreeMap<>();
        holder.put("builder",builder);
        holder.put("classNames", classNames);

        try {
            return OBJECT_MAPPER.writeValueAsString(holder);
        }
        catch (JsonProcessingException jpe)
        {
            throw new RuntimeException("Error writing object as string", jpe);
        }
    }

    public static LamarkBuilder deserialize(String input)
    {
        LamarkBuilder rval = null;

        if (input!=null)
        {
            try {
                Map<String, Object> holder = OBJECT_MAPPER.readValue(input, Map.class);
                rval = innerProcess(holder);
            }
            catch (IOException ioe)
            {
                throw new RuntimeException("Error processing input",ioe);
            }
        }
        return rval;
    }

    public static LamarkBuilder deserialize(InputStream input)
    {
        LamarkBuilder rval = null;

        if (input!=null)
        {
            try {
                Map<String, Object> holder = OBJECT_MAPPER.readValue(input, Map.class);
                rval = innerProcess(holder);
            }
            catch (IOException ioe)
            {
                throw new RuntimeException("Error processing input",ioe);
            }
        }
        return rval;
    }

    private static LamarkBuilder innerProcess(Map<String,Object> src)
            throws IOException
    {
        Map<String,String> classNames = (Map<String,String>)src.get("classNames");
        Map<String,Object> builder = (Map<String,Object>)src.get("builder");

        Selector selector = (Selector)createClass(classNames, "selector", builder.remove("selector"));

        Supplier creator = (Supplier)createClass(classNames, "creator", builder.remove("creator"));
        ToDoubleFunction fitness = (ToDoubleFunction)createClass(classNames, "fitnessFunction", builder.remove("fitnessFunction"));
        Function crossover = (Function)createClass(classNames, "crossover", builder.remove("crossover"));
        Function mutator = (Function)createClass(classNames, "mutator", builder.remove("mutator"));
        Function formatter = (Function)createClass(classNames, "formatter", builder.remove("formatter"));

        LamarkBuilder rval = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(builder),LamarkBuilder.class)
                .withSelector(selector)
                .withCreator(creator)
                .withFitnessFunction(fitness)
                .withCrossover(crossover)
                .withMutator(mutator)
                .withFormatter(formatter);
        return rval;

    }

    private static Object createClass(Map<String,String> classNames, String classId, Object definition)
    {
        Object rval = null;
        String className = classNames.get(classId);
        if (className!=null)
        {
            try {
                Class clazz = Class.forName(className);
                String defString = OBJECT_MAPPER.writeValueAsString(definition);
                rval = OBJECT_MAPPER.readValue(defString, clazz);
            }
            catch (ClassNotFoundException | IOException e)
            {
                throw new RuntimeException("Couldn't create item of type "+className, e);
            }
        }
        return rval;
    }



}
