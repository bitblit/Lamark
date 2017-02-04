package com.erigir.lamark.config;

import com.erigir.lamark.events.LamarkEventListener;
import lombok.Data;

import java.util.Map;

/**
 * Created by cweiss1271 on 2/3/17.
 */
@Data
public class LamarkCustomListener {
    private Class listenerClass;
    private Map<String,String> config;

    public LamarkEventListener createConfiguredObject() {
        return (LamarkEventListener)LamarkComponentDetails.createConfiguredObject(listenerClass, config);
    }
}
