package com.erigir.lamark.config;

import com.erigir.lamark.*;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkConfiguration {
    private LamarkParameters parameters;
    private LamarkComponents components;
    private List<LamarkCustomListener> customListeners  = Collections.emptyList();

    public LamarkBuilder applyToBuilder(LamarkBuilder builder)
    {
        parameters.applyToBuilder(builder);
        components.applyToBuilder(builder);
        return builder;
    }

    public LamarkConfiguration fromLamark(Lamark lamark)
    {
        return null; // TODO: impl
    }
}
