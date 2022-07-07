package com.erigir.lamark.example.tsp;

import com.erigir.lamark.Individual;
import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A listener that redraws the supplied TSP when a new best individual is found.
 *
 * @author cweiss
 * @since 04/2006
 */
public class TSPGraphicalListener implements GUIEventListener {
    /**
     * Handle to the parent window *
     */
    private Component parentComponent;
    /**
     * Handle to the containing frame *
     */
    private JFrame displayFrame;
    /**
     * Handle to the panel that draws the TSP *
     */
    private TSPPanel displayPanel;
    /**
     * Handle to the currently displayed individual *
     */
    private Individual current;

    @Override
    public void setParentStage(Stage parent) {
        // TODO: Implement
    }

    /**
     * @see com.erigir.lamark.gui.GUIEventListener#setParentComponent(java.awt.Component)
     */
    public void setParentComponent(Component pComponent) {
        parentComponent = pComponent;
    }

    /**
     * Redraws the TSP on new best individual found.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if ((je instanceof BetterIndividualFoundEvent) && (parentComponent != null)) {
            current = ((BetterIndividualFoundEvent) je).getNewBest();
            verifyFrame();
            List<?> perm = (List<?>) current.getGenome();
            Integer[] permI = perm.toArray(new Integer[0]);

            List<?> tempPoints = (List<?>) current.getAttribute("POINTS");
            // Recasting hack
            List<MyPoint> points = new ArrayList<MyPoint>(tempPoints.size());
            for (Object o : tempPoints) {
                points.add((MyPoint) o);
            }

            displayPanel.points = points;
            displayPanel.bestKnown = ((Integer) current.getAttribute("BESTKNOWN"));
            displayPanel.minX = (Double) current.getAttribute("MINX");
            displayPanel.minY = (Double) current.getAttribute("MINY");
            displayPanel.maxX = (Double) current.getAttribute("MAXX");
            displayPanel.maxY = (Double) current.getAttribute("MAXY");
            displayPanel.permDistance = current.getFitness();
            displayPanel.perm = permI;
            displayPanel.repaint();
        }
    }

    /**
     * Guarantees that the frame has been created and is currently shown.
     */
    private void verifyFrame() {
        if (displayFrame == null) {
            displayFrame = new JFrame();
            displayPanel = new TSPPanel();
            displayFrame.setSize(new Dimension(400, 432));
            displayFrame.getContentPane().add(displayPanel);
        }
        if (!displayFrame.isVisible()) {
            displayFrame.setVisible(true);
        }
    }


    /**
     * A panel class that draws the contained TSP.
     *
     * @author cweiss
     * @since 04/2005
     */
    static class TSPPanel extends JPanel {
        /**
         * List of points in the TSP *
         */
        List<MyPoint> points;
        /**
         * Length of solution to the TSP, if known *
         */
        int bestKnown;
        /**
         * Left edge of tsp *
         */
        double minX;
        /**
         * Top edge of tsp *
         */
        double minY;
        /**
         * Right edge of tsp *
         */
        double maxX;
        /**
         * Bottom edge of tsp *
         */
        double maxY;
        /**
         * Length of this permutation *
         */
        double permDistance;
        /**
         * The current path as a permutation *
         */
        Integer[] perm;

        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            draw((Graphics2D) g);
        }

        /**
         * Draws the TSP onto the supplied Graphics2D
         *
         * @param gc Graphics2D to draw the tsp onto.
         */
        public void draw(Graphics2D gc) {
            Dimension d = getSize();

            if (perm.length != points.size()) {
                throw new IllegalArgumentException("Not a permutation.  There are "
                        + points.size() + " points and " + perm.length + " indexes");
            }

            double xRange = maxX - minX;
            double yRange = maxY - minY;

            // Calc the point size
            int pointSize = (int) xRange / 100;
            int halfPoint = pointSize / 2;
            int fontSize = (int) xRange / 25;

            // Set up the context
            gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            gc.setBackground(Color.white);
            gc.setPaint(Color.black);

            gc.setStroke(new BasicStroke((float) xRange / 300));

            Point2D minPoint = new Point2D.Double(minX, minY);
            gc.scale(d.width / xRange, d.height
                    / yRange);
            gc.translate(-1 * minPoint.getX(), -1 * minPoint.getY());
            gc.clearRect(0, 0, (int) maxX, (int) maxY);

            // Now, draw the lines
            for (int i = 0; i < perm.length; i++) {
                MyPoint p1 = points.get(perm[i].intValue());
                MyPoint p2 = points.get(perm[(i + 1) % perm.length]
                        .intValue());
                // Draw ;the first points circle
                gc.draw(new Ellipse2D.Double(p1.getX() - halfPoint, p1.getY()
                        - halfPoint, pointSize, pointSize));
                // Draw the line
                gc.draw(new Line2D.Double(p1.as2d(), p2.as2d()));
            }

            Font thisFont = new Font("Arial", Font.PLAIN, fontSize);
            gc.setFont(thisFont);

            String outputString = "Length : " + LamarkUtil.format(permDistance);
            if (bestKnown != -1) {
                outputString += "  Error: " + LamarkUtil.format(permDistance - bestKnown);
            }

            // Draw the text specifying size
            gc.drawString(outputString,
                    (int) minX, (int) minY + fontSize);

        }

    }
}
