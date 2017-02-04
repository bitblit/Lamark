package com.erigir.lamark.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by cweiss1271 on 2/3/17.
 */
public class LamarkSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkSerializer.class);
    private static final ObjectMapper OM = createMapper();

    private static final ObjectMapper createMapper()
    {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        rval.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        rval.configure(SerializationFeature.INDENT_OUTPUT, true);
        return rval;
    }

    public static MultiLamarkConfiguration readConfiguration(InputStream is)
    {
        try {
            return OM.readValue(is, MultiLamarkConfiguration.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading configuration",e);
        }
    }

    public static MultiLamarkConfiguration readConfiguration(String input)
    {
        try {
            return OM.readValue(input, MultiLamarkConfiguration.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading configuration",e);
        }
    }

    public static String configurationToString(MultiLamarkConfiguration configuration)
    {
        try {
            return OM.writeValueAsString(configuration);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading configuration",e);
        }

    }

    public static String singleConfigurationToString(String configName, LamarkConfiguration configuration)
    {
        MultiLamarkConfiguration temp = new MultiLamarkConfiguration();
        temp.getConfigurations().put(configName, configuration);
        return configurationToString(temp);
    }

}
