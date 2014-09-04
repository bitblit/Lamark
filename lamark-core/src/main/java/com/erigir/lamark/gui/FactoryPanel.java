package com.erigir.lamark.gui;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.config.ILamarkFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A panel that describes the LamarkFactory and allows it to be configured
 * Created by chrweiss on 9/1/14.
 */
public class FactoryPanel extends JPanel implements ILamarkFactory{
    private ILamarkFactory factory;
    private JLabel shortDescription = new JLabel("--TBD--");
    private JButton configure = new JButton("Configure...");
    private JButton describe = new JButton("Describe...");

    public FactoryPanel(final ILamarkFactory factory) {
        this.factory = factory;

        add(shortDescription);
        add(configure);
        add(describe);

        final FactoryPanel parent = this;

        configure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((IGuiConfigurableLamarkFactory)factory).configure(parent);
            }
        });

        describe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        updateInterface();
    }

    @Override
    public String getShortDescription() {
        return factory.getShortDescription();
    }

    @Override
    public Lamark createConfiguredLamarkInstance()
    {
        return factory.createConfiguredLamarkInstance();
    }

    public void setFactory(ILamarkFactory factory) {
        if (factory==null)
        {
            throw new IllegalStateException("Cant set a null factory");
        }

        this.factory = factory;
        updateInterface();
    }

    public void updateInterface()
    {
        shortDescription.setText(factory.getShortDescription());
        configure.setEnabled(IGuiConfigurableLamarkFactory.class.isAssignableFrom(factory.getClass()));
    }

}
