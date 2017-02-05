package com.erigir.lamark.example.cellular;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class for drawing/displaying CA's given a proposed solution.
 *
 * @author cweiss
 * @since 03/2005
 */
public class CARenderer {

    private int numberOfColumns;
    private int numberOfRows;
    private int tableWidth;
    private int tableHeight;

    public CARenderer(int numberOfColumns, int numberOfRows, int tableWidth, int tableHeight) {
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;
    }

    /**
     * Draws the CA onto the supplied canvas
     * @param canvas Canvas to draw the tsp onto.
     */
    public void renderCA(Canvas canvas, CellularAutomata[] automata)
    {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Calculate image width
        int imgWidth = (numberOfColumns * tableWidth) + numberOfColumns
                - 1;
        int imgHeight = (numberOfColumns * tableHeight)
                + numberOfColumns - 1;

        //gc.scale(d.width/imgWidth,d.getHeight()/imgHeight);

        gc.setStroke(Color.GREEN);
        //gc.setStroke(new BasicStroke(1.0f));

        for (int x = 0; x < numberOfColumns; x++) {
            for (int y = 0; y < numberOfRows; y++) {
                int i = x + (y * numberOfColumns);
                // Write this table into the main img
                gc.drawImage(SwingFXUtils.toFXImage(automata[i].asImage(),null), (x * tableWidth)
                        + x, (y * tableHeight) + y);
                gc.strokeLine(0, (y * tableHeight) + y,
                        imgWidth, (y * tableHeight) + y);
            }
            gc.strokeLine((x * tableWidth) + x, 0,
                    (x * tableWidth) + x, imgHeight);
        }

    }


}
