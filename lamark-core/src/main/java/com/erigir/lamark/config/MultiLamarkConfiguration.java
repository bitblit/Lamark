package com.erigir.lamark.config;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class MultiLamarkConfiguration {
    private Map<String,LamarkConfiguration> configurations = new TreeMap<>();
    private String notes;
}
