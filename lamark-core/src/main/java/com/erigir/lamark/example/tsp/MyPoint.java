package com.erigir.lamark.example.tsp;

import java.awt.geom.Point2D;
import java.io.Serializable;


/**
 * A simple wrapper around a AWT Point2D object that adds a distance function.
 *
 * @author cweiss
 * @since 04/2006
 */
public class MyPoint implements Serializable {
    /**
     * Here to implement serializable *
     */
    public static final long serialVersionUID = 0;
    /**
     * X coord *
     */
    public double x;
    /**
     * Y coord *
     */
    public double y;


    /**
     * Simple setting constructor.
     *
     * @param pX double containing initial x value
     * @param pY double containing initial y value
     */
    public MyPoint(double pX, double pY) {
        x = pX;
        y = pY;
    }


    /**
     * Default constructor.
     */
    public MyPoint() {
        super();
    }

    /**
     * Calculates the distance from this point to the passed point.
     *
     * @param p2 MyPoint to calc distance to.
     * @return double containing the distance
     */
    public double distance(MyPoint p2) {
        return as2d().distance(p2.as2d());
    }


    /**
     * Convert this point to a AWT Point2D
     *
     * @return Point2D containing the conversion
     */
    public Point2D as2d() {
        return new Point2D.Double(x, y);
    }

    /**
     * Accessor method.
     *
     * @return double contianing the property
     */
    public double getX() {
        return x;
    }

    /**
     * Mutator method
     *
     * @param x double new value for property
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Accessor method.
     *
     * @return double contianing the property
     */
    public double getY() {
        return y;
    }

    /**
     * Mutator method
     *
     * @param y double new value for property
     */
    public void setY(double y) {
        this.y = y;
    }

}
