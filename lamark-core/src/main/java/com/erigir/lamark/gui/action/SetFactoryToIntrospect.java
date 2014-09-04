package com.erigir.lamark.gui.action;

import com.erigir.lamark.builtin.AllOnesLamarkConfig;
import com.erigir.lamark.config.IntrospectLamarkFactory;
import com.erigir.lamark.gui.FactoryPanel;
import com.erigir.lamark.gui.LamarkGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by chrweiss on 9/1/14.
 */
public class SetFactoryToIntrospect implements ActionListener {

    private LamarkGui lamarkGui;

    public SetFactoryToIntrospect(LamarkGui lamarkGui) {
        this.lamarkGui = lamarkGui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        lamarkGui.getFactoryPanel().setFactory(new IntrospectLamarkFactory(new AllOnesLamarkConfig()));
        // TODO: Select a class here

    }
}
