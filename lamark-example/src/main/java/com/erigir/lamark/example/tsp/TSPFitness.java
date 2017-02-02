/*
 * Created on Apr 1, 2005
 */
package com.erigir.lamark.example.tsp;

import com.erigir.lamark.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.ToDoubleFunction;

/**
 * A fitness function for TSPs, where the fitness value is the length of the path.
 * &lt;p /&gt;
 * This class implements the IFitnessFunction interface for TSPs, when passed a
 * TSP to solve, and a given solution, returns the length of that path as the
 * fitness value for the individual.  Note that this class cannot be used for
 * non-symmetrical TSPs.
 *
 * @author cweiss
 * @since 04/2005
 */
public class TSPFitness  implements ToDoubleFunction<List<Integer>>, SelfValidating {
    private static final Logger LOG = LoggerFactory.getLogger(TSPFitness.class);
    /**
     * String handle to the original tsp file read *
     */
    private String tspFile;
    /**
     * If the solution for this tsp is known, it can be set here *
     */
    private Integer bestKnown = null; // not known for arbitrary TSP
    /**
     * Set of points in the tsp *
     */
    private List<MyPoint> points;
    /**
     * Left edge of the tsp *
     */
    private double minX = Double.MAX_VALUE;
    /**
     * Right edge of the tsp *
     */
    private double maxX = Double.MIN_VALUE;
    /**
     * Top edge of the tsp *
     */
    private double minY = Double.MAX_VALUE;
    /**
     * Bottow edge of the tsp *
     */
    private double maxY = Double.MIN_VALUE;

    /**
     * Accessor method.
     *
     * @return Integer containing the property
     */
    public Integer getBestKnown() {
        return bestKnown;
    }


    /**
     * Mutator method
     *
     * @param bestKnown Integer containing the new value
     */
    public void setBestKnown(Integer bestKnown) {
        this.bestKnown = bestKnown;
    }

    /**
     * If tspFile is set, this function loads the file into memory and creates
     * the point set.
     */
    private void loadPoints() {
        try {
            if (tspFile != null) {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(tspFile);
                if (is == null) {
                    LOG.trace("Couldnt find resource:" + tspFile + " ... trying as file");
                    is = new FileInputStream(new File(tspFile));
                }
                if (is != null) {
                    InputStreamReader isr = new InputStreamReader(is);
                    initFromTSPReader(isr);
                } else {
                    LOG.error("Unable to load tsp as resource or file:" + tspFile);
                }
            }
        } catch (Exception e) {
            LOG.error("Error on TSP load points from {}", tspFile, e);
            LOG.error("Error loading tspFile:" + e);
        }
    }

    /**
     * Calculates the length of a given permutation.
     *
     * @param permutation int[] containing the permutation to calculate
     * @return double containing the length of the perm on this tsp.
     */
    public double permutationDistance(int[] permutation) {
        double rval = 0;

        if (permutation.length != points.size()) {
            throw new IllegalArgumentException("Not a permutation.  There are "
                    + points.size() + " points and " + permutation.length
                    + " indexes");
        }
        for (int i = 0; i < permutation.length; i++) {
            rval += distance(permutation[i],
                    permutation[((i + 1) % permutation.length)]);
        }
        return rval;
    }

    /**
     * Converts Integer objects to ints to call the other permDistance function.
     *
     * @param permutation Integer[] containing the permutation
     * @return double containing the permutation length
     */
    public double permutationDistance(Integer[] permutation) {
        LOG.trace("in perm distance, perm = " + Arrays.asList(permutation));
        double rval = 0;

        if (permutation.length != points.size()) {
            throw new IllegalArgumentException("Not a permutation.  There are "
                    + points.size() + " points and " + permutation.length
                    + " indexes");
        }
        for (int i = 0; i < permutation.length; i++) {
            rval += distance(permutation[i].intValue(),
                    permutation[((i + 1) % permutation.length)].intValue());
        }
        LOG.trace("returning " + rval);
        return rval;
    }

    /**
     * Calculates the distance between points at the two indexes.
     *
     * @param idx1 int containing the first index
     * @param idx2 int containing the second index
     * @return double containing the distance between the points.
     */
    public double distance(int idx1, int idx2) {
        MyPoint p1 = (MyPoint) points.get(idx1);
        MyPoint p2 = (MyPoint) points.get(idx2);
        return p1.distance(p2);
    }

    /**
     * Loads the TSP from the supplied Reader.
     *
     * @param data Reader object containing the TSP
     */
    private void initFromTSPReader(Reader data) {
        try {
            points = new ArrayList<MyPoint>();

            BufferedReader br = new BufferedReader(data);
            String next = br.readLine();

            // Skip the header
            while (next != null && !next.startsWith("NODE_COORD_SECTION")) {
                next = br.readLine();
            }

            next = br.readLine(); // Move next line
            // Read the points
            while (next != null) {
                addPoint(pointFromLine(next));
                next = br.readLine();
                if (next.startsWith("EOF") || next.trim().endsWith("EOF")) {
                    next = null;
                }
            }

            LOG.info("Finished processing read.  Found " + points.size()
                    + " points");
        } catch (IOException ioe) {
            throw new RuntimeException("Error reading TSP Stream: " + ioe);
        }
    }

    /**
     * Adds a point to the TSP
     *
     * @param point MyPoint to add to the tsp.
     */
    private void addPoint(MyPoint point) {
        if (point != null) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            }
            points.add(point);
        }
    }

    /**
     * Given a line in TSP file format, create a point from it.
     *
     * @param line String containing the line.
     * @return MyPoint object from that line.
     */
    private MyPoint pointFromLine(String line) {
        LOG.info("Parsing point from '" + line + "'");
        Double x = null;
        Double y = null;
        Integer point = null;

        StringTokenizer st = new StringTokenizer(line, " \t");

        while (st.hasMoreTokens()) {
            String next = st.nextToken().trim();
            if (next.length() > 0) {
                if (point == null) {
                    point = new Integer(Integer.parseInt(next));
                } else if (x == null) {
                    x = new Double(Double.parseDouble(next));
                } else {
                    y = new Double(Double.parseDouble(next));
                }
            }
        }

        MyPoint rval = new MyPoint(x.doubleValue(), y.doubleValue());
        LOG.info("returning " + rval);
        return rval;
    }

    // TODO: Minimum best

    @Override
    public double applyAsDouble(List<Integer> l) {
        Integer[] arr = l.toArray(new Integer[0]);
        /*
        i.setAttribute("POINTS", points);
        i.setAttribute("MINX", minX);
        i.setAttribute("MINY", minY);
        i.setAttribute("MAXX", maxX);
        i.setAttribute("MAXY", maxY);
        i.setAttribute("BESTKNOWN", getBestKnown()); */
        return permutationDistance(arr);
    }

    /**
     * Guarantees that a set of points can be loaded.
     *
     * see com.erigir.lamark.SelfValidating
     **/
    public void selfValidate() {
        // try to load points
        loadPoints();
        if (points == null) {
            throw new IllegalArgumentException("No TSP file loaded.  Set tspFile property to a filename or one of the included resources.");
        }
    }

    public String getTspFile() {
        return tspFile;
    }

    /**
     * Mutator method.
     * NOTE: Calling this method will reset all cache values.
     *
     * @param filename String containing the new value
     */
    public void setTspFile(String filename) {
        if (filename != null) {
            if (!filename.equals(tspFile)) {
                tspFile = filename;
                points = null;
                minX = Double.MAX_VALUE;
                maxX = Double.MIN_VALUE;
                minY = Double.MAX_VALUE;
                maxY = Double.MIN_VALUE;
            }
        } else {
            tspFile = null;
        }


    }
}