package com.erigir.lamark.config;


/**
 * This is a simple wrapper object to deal with JSON not storing classnames
 * Created by chrweiss on 8/28/14.
 */
public class LamarkSavedConfiguration {
    private String configurationClassName;
    private String configurationJson;

    public String getConfigurationClassName() {
        return configurationClassName;
    }

    public void setConfigurationClassName(String configurationClassName) {
        this.configurationClassName = configurationClassName;
    }

    public String getConfigurationJson() {
        return configurationJson;
    }

    public void setConfigurationJson(String configurationJson) {
        this.configurationJson = configurationJson;
    }

}
