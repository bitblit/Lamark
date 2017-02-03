package com.erigir.lamark.gui;

import com.erigir.lamark.events.LamarkEventListener;
import javafx.stage.Stage;

import java.awt.*;


/**
 * This interface is implemented by any class that wants to
 * act as a listener for LamarkEvents, by also needs a
 * handle to the GUI.  For example, a TSP solver might
 * want to graphically display the current TSP solution in
 * a new window, but would need a handle to the parent component
 * to open said window.  This parent would
 * be provided by the system here.
 *
 * @author cweiss
 */
public interface GUIEventListener extends LamarkEventListener {
    /**
     * Called by LamarkGUI to give the listener a handle to the parent window.
     *
     * @param parent Component reference to the parent window
     */
    void setParentStage(Stage parent);
}
