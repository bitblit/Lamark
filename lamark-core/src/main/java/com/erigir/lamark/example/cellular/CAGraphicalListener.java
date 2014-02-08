package com.erigir.lamark.example.cellular;

import com.erigir.lamark.Individual;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;


/**
 * A class for displaying a given cellular automata plan as a picture.
 * <p/>
 * This class implements GUIEventListener, and every time a new best
 * individual object is found, it repaints the drawn GA in the display
 * panel with the output of the new best CA.
 *
 * @author cweiss
 * @since 03/2005
 */
public class CAGraphicalListener implements GUIEventListener {
    /**
     * Handle to parent component for close messages *
     */
    private Component parentComponent;

    /**
     * Handle to the frame this panel goes in *
     */
    private JFrame displayFrame;

    /**
     * Handle to the CAPanel that draws the CA *
     */
    private CAPanel displayPanel;


    /**
     * @see com.erigir.lamark.gui.GUIEventListener#setParentComponent(java.awt.Component)
     */
    public void setParentComponent(Component pComponent) {
        parentComponent = pComponent;
    }


    /**
     * Updates the CA on a new better solution found.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if ((je instanceof BetterIndividualFoundEvent) && null != parentComponent) {
            verifyFrame();
            BetterIndividualFoundEvent bif = (BetterIndividualFoundEvent) je;
            displayPanel.div = bif.getNewBest();
            displayPanel.NUMBER_OF_CA_COLS = (Integer) displayPanel.div.getAttribute("NUMBER_OF_CA_COLS");
            displayPanel.NUMBER_OF_CA_ROWS = (Integer) displayPanel.div.getAttribute("NUMBER_OF_CA_ROWS");
            displayPanel.tableWidth = (Integer) displayPanel.div.getAttribute("TABLE_WIDTH");
            displayPanel.tableHeight = (Integer) displayPanel.div.getAttribute("TABLE_HEIGHT");
            displayPanel.automata = (CellularAutomata[]) displayPanel.div.getAttribute("CADATA");

            Dimension preferred = new Dimension(displayPanel.preferredWidth(), displayPanel.preferredHeight() + 32);
            displayFrame.setSize(preferred);
            displayPanel.repaint();
        }
    }


    /**
     * Makes sure the internal frame object exists and is visible.
     */
    private void verifyFrame() {
        if (displayFrame == null) {
            displayFrame = new JFrame();
            displayPanel = new CAPanel();
            displayFrame.getContentPane().add(displayPanel);
        }
        if (!displayFrame.isVisible()) {
            displayFrame.setVisible(true);
        }
    }

    /**
     * Class for drawing/displaying CA's given a proposed solution.
     *
     * @author cweiss
     * @since 03/2005
     */
    static class CAPanel extends JPanel {
        /**
         * Current individual to display *
         */
        Individual div;
        /**
         * Number of columns in the ca *
         */
        int NUMBER_OF_CA_COLS;
        /**
         * Number of rows in the ca *
         */
        int NUMBER_OF_CA_ROWS;
        /**
         * Number of CA instances in the output frame (width) *
         */
        int tableWidth;
        /**
         * Number of CA isntances in the output frame (height) *
         */
        int tableHeight;
        /**
         * Array of CA's to display *
         */
        CellularAutomata[] automata;


        /**
         * Cals the preferred width of the frame
         *
         * @return int containing the value
         */
        public int preferredWidth() {
            return (NUMBER_OF_CA_COLS * tableWidth) + NUMBER_OF_CA_COLS - 1;
        }

        /**
         * Cals the preferred height of the frame
         *
         * @return int containing the value
         */
        public int preferredHeight() {
            return (NUMBER_OF_CA_COLS * tableWidth) + NUMBER_OF_CA_COLS - 1;
        }


        /**
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D gc = (Graphics2D) g;
            // Calculate image width
            int imgWidth = (NUMBER_OF_CA_COLS * tableWidth) + NUMBER_OF_CA_COLS
                    - 1;
            int imgHeight = (NUMBER_OF_CA_COLS * tableHeight)
                    + NUMBER_OF_CA_COLS - 1;

            //gc.scale(d.width/imgWidth,d.getHeight()/imgHeight);

            gc.setPaint(Color.green);
            gc.setStroke(new BasicStroke(1.0f));

            for (int x = 0; x < NUMBER_OF_CA_COLS; x++) {
                for (int y = 0; y < NUMBER_OF_CA_ROWS; y++) {
                    int i = x + (y * NUMBER_OF_CA_COLS);
                    // Write this table into the main img
                    gc.drawImage(automata[i].asImage(), null, (x * tableWidth)
                            + x, (y * tableHeight) + y);
                    gc.draw(new Line2D.Double(0, (y * tableHeight) + y,
                            imgWidth, (y * tableHeight) + y));
                }
                gc.draw(new Line2D.Double((x * tableWidth) + x, 0,
                        (x * tableWidth) + x, imgHeight));
            }
        }
    }

}
