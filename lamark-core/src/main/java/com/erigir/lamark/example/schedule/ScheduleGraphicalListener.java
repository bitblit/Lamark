package com.erigir.lamark.example.schedule;

import com.erigir.lamark.Individual;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;

import javax.swing.*;
import java.awt.*;

/**
 * A graphic listener that shows the length of a given schedule in comparison to the worst one found.
 *
 * @author cweiss
 * @since 04/2005
 */
public class ScheduleGraphicalListener implements GUIEventListener {
    /**
     * Handle to the creating window *
     */
    private Component parentComponent;

    /**
     * Handle to the frame that the schedule is displayed in *
     */
    private JFrame displayFrame;

    /**
     * Handle to the panel that actually displays the data *
     */
    private SchedulePanel displayPanel;


    /**
     * @see com.erigir.lamark.gui.GUIEventListener#setParentComponent(java.awt.Component)
     */
    public void setParentComponent(Component pComponent) {
        parentComponent = pComponent;
    }

    /**
     * Listens for betterindividual events and updates the display when they are seen.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if ((je instanceof BetterIndividualFoundEvent) && null != parentComponent) {
            je.getLamark().logFiner("Frame is " + parentComponent);
            verifyFrame();
            BetterIndividualFoundEvent bif = (BetterIndividualFoundEvent) je;
            displayPanel.div = bif.getNewBest();
            displayPanel.worstFound = (Integer) displayPanel.div.getAttribute("WORST");
            displayPanel.schedule = (DynamicScheduler) displayPanel.div.getAttribute("SCHEDULE");
            displayPanel.totalWidth = (Integer) displayPanel.div.getAttribute("TOTALWIDTH");
            displayPanel.permTime = (int[]) displayPanel.div.getAttribute("PERMTIME");
            displayPanel.permWeight = (int[]) displayPanel.div.getAttribute("PERMWEIGHT");
            displayPanel.repaint();
        }
    }

    /**
     * Makes sure that the holding frame exists and is set visible.
     */
    private void verifyFrame() {
        if (displayFrame == null) {
            displayFrame = new JFrame();
            displayPanel = new SchedulePanel();
            displayFrame.setSize(new Dimension(400, 432));
            displayFrame.getContentPane().add(displayPanel);
        }
        if (!displayFrame.isVisible()) {
            displayFrame.setVisible(true);
        }
    }


    /**
     * Class extending panel to show a schedule and improvement over time.
     *
     * @author cweiss
     * @since 05/2006
     */
    static class SchedulePanel extends JPanel {
        /**
         * Handle to the currently displayed individual *
         */
        Individual div;
        /**
         * Handle to the worst score found *
         */
        int worstFound;
        /**
         * Handle to the class that calculated the schedule *
         */
        DynamicScheduler schedule;

        /**
         * Total lenght of the schedule *
         */
        int totalWidth;
        /**
         * Time for this permutation *
         */
        int[] permTime;
        /**
         * Weight for this permutation *
         */
        int[] permWeight;

        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString("paint:" + System.currentTimeMillis(), 10, 10);
            Graphics2D gc = (Graphics2D) g;
            Dimension d = getSize();
            int fitness = div.getFitness().intValue();
            // Set up the context
            gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            gc.setBackground(Color.white);
            gc.setPaint(Color.black);

            gc.setStroke(new BasicStroke((float) worstFound / 300));
            gc.scale(d.width / (double) worstFound, d.height
                    / (double) worstFound);
            gc.clearRect(0, 0, worstFound, worstFound);

            int quarterPoint = worstFound / 4;
            int threeQuarterPoint = 3 * quarterPoint;
            int halfBoxHeight = worstFound / 10;
            int fontSize = worstFound / 30;

            // Now draw a box representing worst case
            gc.setPaint(Color.red);
            gc.fillRect(0, quarterPoint - halfBoxHeight, worstFound,
                    2 * halfBoxHeight);
            // Now draw the current case
            gc.setPaint(Color.blue);
            gc.fillRect(0, quarterPoint - (halfBoxHeight - 1), fitness,
                    2 * (halfBoxHeight - 1));

            // Now draw the schedule
            if (schedule == null) {
                throw new IllegalStateException("No fitness object for div : " + div);
            }
            int[] splitPoints = schedule.splitPoints();
            gc.setPaint(Color.black);
            gc.drawRect(0, threeQuarterPoint - halfBoxHeight, worstFound,
                    2 * halfBoxHeight);

            int startIdx = 0;
            int splitWidth = 0;
            // Draw the first split box
            gc.setPaint(Color.red);
            startIdx = scaledWidth(5, totalWidth, worstFound);
            gc.fillRect(startIdx, threeQuarterPoint - halfBoxHeight,
                    splitWidth, 2 * halfBoxHeight);
            gc.setPaint(Color.black);
            startIdx += splitWidth;
            for (int i = 0; i < permTime.length; i++) {
                int width = scaledWidth(permTime[i] * permWeight[i],
                        totalWidth, worstFound);
                gc.drawRect(startIdx, threeQuarterPoint - halfBoxHeight,
                        startIdx + width, 2 * halfBoxHeight);
                startIdx += width;
                // And if a split point, draw the box
                if (i > 0) {
                    if (arrayContains(splitPoints, i)) {
                        gc.setPaint(Color.red);
                        gc.fillRect(startIdx,
                                threeQuarterPoint - halfBoxHeight, splitWidth,
                                2 * halfBoxHeight);
                        startIdx += splitWidth;
                        gc.setPaint(Color.black);
                    }
                }
            }

            Font thisFont = new Font("Arial", Font.PLAIN, fontSize);
            gc.setFont(thisFont);
            // Draw the text specifying size
            int improvment = worstFound - fitness;
            int improvmentPct = (improvment * 100) / worstFound;

            gc.drawString("First(Random) found : " + worstFound, 0, fontSize);
            gc.drawString("Current     : " + fitness, 0, (fontSize + 1) * 2);
            gc.drawString("Improvement : " + improvment + " (" + improvmentPct
                    + "%)", 0, (fontSize + 1) * 3);

        }

        /**
         * Used by the drawing function to create the boxes.
         *
         * @param size         int size of the box to scale
         * @param totalWidth   int width of the sum of the boxes
         * @param displayWidth int widht of the screen
         * @return int with scaled width of the box
         */
        private int scaledWidth(int size, int totalWidth, int displayWidth) {
            return (int) (size * ((double) displayWidth / (double) totalWidth));
        }

        /**
         * Simple function to see if an int array contains a given int.
         *
         * @param array int[] to scan
         * @param value int to look for
         * @return true if found
         */
        private boolean arrayContains(int[] array, int value) {
            boolean rval = false;
            for (int i = 0; i < array.length && !rval; i++) {
                rval = array[i] == value;
            }
            return rval;
        }

    }
}
