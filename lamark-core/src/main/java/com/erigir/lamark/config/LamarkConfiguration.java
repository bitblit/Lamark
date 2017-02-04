package com.erigir.lamark.config;

import com.erigir.lamark.*;
import lombok.Data;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkConfiguration {
    private LamarkParameters parameters;
    private LamarkComponents components;

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
