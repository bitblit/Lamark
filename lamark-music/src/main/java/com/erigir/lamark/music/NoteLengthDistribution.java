package com.erigir.lamark.music;

import jm.music.data.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteLengthDistribution {
    private static final Logger LOG = LoggerFactory.getLogger(NoteLengthDistribution.class);

    private Map<NoteDurationEnum, Double> counts;
    private double totalNotes;

    public NoteLengthDistribution() {
        super();
    }

    public void initialize(List<Note> notes) {
        counts = new HashMap<NoteDurationEnum, Double>();

        // Put all note types in the count table
        double sumExp = 0;
        NoteDurationEnum[] vals = NoteDurationEnum.values();
        for (NoteDurationEnum nd : vals) {
            counts.put(nd, 0.0);
        }

        // Build counts
        for (Note n : notes) {
            NoteDurationEnum nd = NoteDurationEnum.valueFromNote(n);
            if (nd==null)
            {
                LOG.info("This is weird note={}, nd=null", n);
            }
            Double i = counts.get(nd);
            if (i==null)
            {
                LOG.info("This is weird note={}, nd={}, i=null", n, nd);
            }
            counts.put(nd, i + 1);
        }
        totalNotes = notes.size();
    }

    /**
     * Uses the chi2 method to calc degree of fit to the standard note usage
     *
     * @return
     */
    public double chiCorrelation() {
        double runSum = 0;
        double sumO = 0;
        double sumE = 0;
        for (NoteDurationEnum nd : NoteDurationEnum.values()) {
            double E = nd.getExpectedFrequency() * totalNotes;
            double O = counts.get(nd);
            double OmE = O - E;
            double OmE2 = OmE * OmE;
            double OmE2dE = OmE2 / E;
            runSum += OmE2dE;
            LOG.trace("{} e= {} o= {} ome={} OmE2dE={} Ome2={}", new Object[]{ nd , E , O , OmE , OmE2dE , OmE2});
            sumE += E;
            sumO += O;
        }

        LOG.trace("SE= {} SO={}" + sumE , sumO);
        LOG.debug("rval= {}" , runSum);
        return runSum;
    }


    public double correlation() {
            NoteDurationEnum[] vals = NoteDurationEnum.values();
        try
        {

            double[] obs = new double[vals.length];
            double[] exp = new double[vals.length];

            for (int i = 0; i < vals.length; i++) {
                obs[i] = counts.get(vals[i]);
                exp[i] = vals[i].getExpectedFrequency() * totalNotes;
            }
            return Math.abs(pearson(obs, exp));
        }
        catch (NullPointerException npe)
        {
            throw npe;
        }
    }

    public double pearson(double[] x, double[] y) {
        // Calc average x and y
        if (x.length != y.length) {
            throw new IllegalStateException("x and y must have equal size");
        }
        double sumX = 0, sumY = 0, avgX = 0, avgY = 0;
        for (int i = 0; i < x.length; i++) {
            sumX += x[i];
            sumY += y[i];
        }
        avgX = sumX / (double) x.length;
        avgY = sumY / (double) y.length;

        // Calc top/bottom
        double top = 0;
        double bottomInx = 0;
        double bottomIny = 0;
        for (int i = 0; i < x.length; i++) {
            double ximx = x[i] - avgX;
            double yimy = y[i] - avgY;
            top += (ximx * yimy);
            bottomInx += (ximx * ximx);
            bottomIny += (yimy * yimy);
        }
        double bottom = Math.sqrt(bottomInx * bottomIny);
        return top / bottom;
    }
}
