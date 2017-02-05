package com.erigir.lamark.example.cellular;

import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * A class for displaying a given cellular automata plan as a picture.
 * &lt;p /&gt;
 * This class implements GUIEventListener, and every time a new best
 * individual object is found, it repaints the drawn GA in the display
 * panel with the output of the new best CA.
 *
 * @author cweiss
 * @since 03/2005
 */
public class CAGraphicalListener extends Stage implements GUIEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(CAGraphicalListener.class);

    /**
     * Handle to the CAPanel that draws the CA *
     */
    private CARenderer renderer;
    private javafx.scene.canvas.Canvas canvas;

    /**
     * Sets the owner of this popup to the passed stage and starts the display
     * @see com.erigir.lamark.gui.GUIEventListener#setParentStage(Stage)
     */
    public void setParentStage(Stage parentStage) {
        initOwner(parentStage);
        canvas = new javafx.scene.canvas.Canvas(400, 400);
        this.setScene(new Scene(new Group(canvas)));
        show();
    }

    /**
     * Updates the CA on a new better solution found.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if ((je instanceof BetterIndividualFoundEvent) && null != canvas) {
            BetterIndividualFoundEvent bif = (BetterIndividualFoundEvent) je;

            Map<String,Object> context = je.getLamark().getContext();

            if (renderer==null)
            {
                LOG.info("Creating the renderer");

                int numCols = (Integer) context.get("NUMBER_OF_CA_COLS");
                int numRows = (Integer) context.get("NUMBER_OF_CA_ROWS");
                int tableWidth = (Integer) context.get("TABLE_WIDTH");
                int tableHeight = (Integer) context.get("TABLE_HEIGHT");
                renderer = new CARenderer(numCols,numRows,tableWidth,tableHeight);
            }

            CellularAutomata[] data = (CellularAutomata[]) bif.getNewBest().getAttribute("CADATA");
            renderer.renderCA(canvas, data);
        }
    }



}
