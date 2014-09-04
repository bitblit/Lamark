package com.erigir.lamark.gui;

import com.erigir.lamark.config.ILamarkFactory;

import java.awt.*;

/**
 * Created by chrweiss on 9/1/14.
 */
public interface IGuiConfigurableLamarkFactory extends ILamarkFactory {
    public void configure(Component parent);
}
