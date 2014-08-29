package com.erigir.lamark;

/**
 * Created by chrweiss on 8/27/14.
 */
public interface IParamProvider {
    Object getParameter(String name);
    <T> T getParameter(String name, Class<T> clazz);
}
