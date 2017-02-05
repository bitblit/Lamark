package com.erigir.lamark.example.tsp;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

/**
 * A class that draws the contained TSP.
 *
 * @author cweiss
 * @since 04/2005
 */
public class TSPRenderer {
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
     * Width of the provided TSP
     */
    double xRange;
    /**
     * Height of the provided TSP
     */
    double yRange;

    private CachedStats stats = new CachedStats();


    public TSPRenderer(List<MyPoint> points, int bestKnown, double minX, double minY, double maxX, double maxY) {
        super();
        this.points = points;
        this.bestKnown = bestKnown;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.xRange = maxX-minX;
        this.yRange = maxY-minY;
    }

    /**
     * Draws the TSP onto the supplied canvas
     * @param canvas Canvas to draw the tsp onto.
     */
    public void renderTSP(Canvas canvas, Integer[] perm, String headerText)
    {
        if (perm.length != points.size()) {
            throw new IllegalArgumentException("Not a permutation.  There are "
                    + points.size() + " points and " + perm.length + " indexes");
        }

        GraphicsContext gc = stats.updateStats(canvas);
        gc.clearRect(0, 0, (int) maxX, (int) maxY);

        // Now, draw the lines
        for (int i = 0; i < perm.length; i++) {
            MyPoint p1 = points.get(perm[i].intValue());
            MyPoint p2 = points.get(perm[(i + 1) % perm.length]
                    .intValue());
            // Draw ;the first points circle
            gc.strokeOval(p1.getX() - stats.halfPoint, p1.getY()
                    - stats.halfPoint, stats.pointSize, stats.pointSize);
            // Draw the line
            gc.strokeLine(p1.getX(),p1.getY(), p2.getX(), p2.getY());
            //gc.moveTo(p1.x,p1.y);
            //gc.lineTo(p2.x,p2.y);
        }

        Font thisFont = new Font("Arial", stats.fontSize);
        gc.setFont(thisFont);

        // Draw the text specifying size
        gc.fillText(headerText,
                (int) minX, (int) minY + stats.fontSize);

    }

    class CachedStats
    {
        Canvas canvas;
        double width;
        double height;

        // Calc the point size
        int pointSize;
        int halfPoint;
        int fontSize;

        public GraphicsContext updateStats(Canvas canvas)
        {
            // So we only do the setup once
            GraphicsContext gc = canvas.getGraphicsContext2D();
            if (this.canvas==canvas) // same canvas object
            {
                // Do nothing
            }
            else
            {
                this.canvas = canvas;
                width = canvas.getWidth();
                height = canvas.getHeight();

                // Calc the point size
                pointSize = (int) xRange / 100;
                halfPoint = pointSize / 2;
                fontSize = (int) xRange / 25;

                // Set up the context

                // TODO: gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                //        RenderingHints.VALUE_ANTIALIAS_ON);

                gc.setFill(Color.BLACK);
                gc.setStroke(Color.BLACK);
                // TODO: gc.setBackground(Color.white);
                gc.scale(width / xRange, height
                        / yRange);
                gc.translate(-1 * minX, -1 * minY);
                gc.setLineWidth(1);

                //gc.setStroke(new BasicStroke((float) xRange / 300));
            }

            return gc;
        }
    }

}
