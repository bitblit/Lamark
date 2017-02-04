package com.erigir.lamark;

import java.util.Map;

/**
 * If your component implements ContextAware, then after all the components are created
 * but just before Lamark starts calculating, it will receive a handle
 * to the context
 * Created by cweiss1271 on 2/3/17.
 */
public interface ContextAware {
    void setContext(Map<String,Object> context);
}
