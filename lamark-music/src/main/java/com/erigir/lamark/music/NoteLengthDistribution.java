package com.erigir.lamark.music;

import jm.music.data.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteLengthDistribution {
    private Map<NoteDurationEnum, Double> counts;
    private double totalNotes;

    public NoteLengthDistribution() {
        super();
    }

    public void initialize(List<Note> notes) {
        counts = new HashMap<NoteDurationEnum, Double>();

        // Put all note types in the count table
        double sumExp = 0;
        for (NoteDurationEnum nd : NoteDurationEnum.values()) {
            counts.put(nd, 0.0);
        }

        // Build counts
        for (Note n : notes) {
            NoteDurationEnum nd = NoteDurationEnum.valueFromNote(n);
            Double i = counts.get(nd);
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
            System.out.println(nd + " e=" + E + " o=" + O + " ome=" + OmE + " OmE2dE=" + OmE2dE + " Ome2=" + OmE2);
            sumE += E;
            sumO += O;
        }
        System.out.println("SE=" + sumE + "  SO=" + sumO);
        System.out.println("rval:" + runSum);
        return runSum;
    }


    public double correlation() {
        NoteDurationEnum[] vals = NoteDurationEnum.values();

        double[] obs = new double[vals.length];
        double[] exp = new double[vals.length];

        for (int i = 0; i < vals.length; i++) {
            obs[i] = counts.get(vals[i]);
            exp[i] = vals[i].getExpectedFrequency() * totalNotes;
        }
        return Math.abs(pearson(obs, exp));
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
