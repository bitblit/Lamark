package com.erigir.lamark.example.tsp;

import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A listener that redraws the supplied TSP when a new best individual is found.
 *
 * @author cweiss
 * @since 04/2006
 */
public class TSPGraphicalListener extends Stage implements GUIEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(TSPGraphicalListener.class);
    /**
     * Hold the TSPRenderer that handles drawing the TSP
     */
    private TSPRenderer renderer;
    /**
     * Handle to the currently displayed permutation
     */
    private Integer[] currentPermutation;
    /**
     * Handle to the currently displayed permutations length
     */
    private Double currentDistance;

    private Canvas canvas;

    /**
     * Sets the owner of this popup to the passed stage and starts the display
     * @see com.erigir.lamark.gui.GUIEventListener#setParentStage(Stage)
     */
    public void setParentStage(Stage parentStage) {
        initOwner(parentStage);
        canvas = new Canvas(400, 400);
        this.setScene(new Scene(new Group(canvas)));
        show();
    }

    /**
     * Redraws the TSP on new best individual found.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if (canvas==null)
        {
            LOG.info("Not rendering - not showing yet");
        }
        else
        {
            if ((je instanceof BetterIndividualFoundEvent)) {
                List<?> perm = (List<?>)(((BetterIndividualFoundEvent) je).getNewBest()).getGenome();
                currentDistance = (((BetterIndividualFoundEvent) je).getNewBest()).getFitness();

                Map<String,Object> context = je.getLamark().getContext();
                int bestKnown = ((Integer) context.get("BESTKNOWN"));

                Integer[] currentPermutation = perm.toArray(new Integer[0]);

                if (renderer==null)
                {
                    LOG.info("Creating renderer");
                    List<?> tempPoints = (List<?>) context.get("POINTS");
                    // Recasting hack
                    List<MyPoint> points = new ArrayList<MyPoint>(tempPoints.size());
                    for (Object o : tempPoints) {
                        points.add((MyPoint) o);
                    }
                    double minX = (Double) context.get("MINX");
                    double minY = (Double) context.get("MINY");
                    double maxX = (Double) context.get("MAXX");
                    double maxY = (Double) context.get("MAXY");

                    renderer = new TSPRenderer(points,bestKnown,minX,minY,maxX,maxY);
                }

                String outputString = "Length : " + LamarkUtil.format(currentDistance);
                if (bestKnown != -1) {
                    outputString += "  Error: " + LamarkUtil.format(currentDistance - bestKnown);
                }
                outputString+=" Gen : "+((BetterIndividualFoundEvent) je).getGenerationNumber();

                renderer.renderTSP(canvas,currentPermutation,outputString);
            }
        }
    }
}
