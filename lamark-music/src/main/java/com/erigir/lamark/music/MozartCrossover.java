package com.erigir.lamark.music;

import com.erigir.lamark.ICrossover;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.Util;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MozartCrossover implements ICrossover<Score> {
    private double pCrossover;
    private Lamark lamark;


    public Individual<Score> crossover(List<Individual<Score>> arg0) {
        //ArrayList<Individual> rval = new ArrayList<Individual>(2);
        Individual<Score> rval = null;

        Individual<Score> p1 = arg0.get(0);
        Individual<Score> p2 = arg0.get(1);

        Score s1 = p1.getGenome();
        Score s2 = p2.getGenome();
            Part part1 = s1.getPart(0);
            Part part2 = s2.getPart(0);

            Phrase[] ph1a = part1.getPhraseArray(); // one phrase per bar
            Phrase[] ph2a = part2.getPhraseArray();

            if (ph1a.length != ph2a.length) {
                throw new IllegalStateException("Scores have different number of bars, 1=" + ph1a.length + " 2=" + ph2a.length);
            }

            int split1 = lamark.getRandom().nextInt(ph1a.length - 1);
            int split2 = (split1 + 1) + lamark.getRandom().nextInt(ph1a.length - (split1 + 1));

            Phrase[] np1 = new Phrase[ph1a.length];
            Phrase[] np2 = new Phrase[ph1a.length];

            for (int i = 0; i < split1; i++) {
                np1[i] = ph1a[i];
                np2[i] = ph2a[i];
            }
            for (int i = split1; i < split2; i++) {
                np1[i] = ph2a[i];
                np2[i] = ph1a[i];
            }
            for (int i = split2; i < ph1a.length; i++) {
                np1[i] = ph1a[i];
                np2[i] = ph2a[i];
            }

            Score new1 = new Score();
            Score new2 = new Score();
            Part newPart1 = new Part(np1);
            Part newPart2 = new Part(np2);
            new1.add(newPart1);
            new2.add(newPart2);

            rval = new Individual<Score>(new1);
            //rval.add(new Individual(new1));
            //rval.add(new Individual(new2));
        // Returning a single child (could do the other)
        return rval;
    }

    public int parentCount() {
        return 2;
    }

    public int childCount() {
        return 2;
    }

    public void setCrossoverProbability(double arg0) {
        pCrossover = arg0;
    }

    public void configure(Properties ignored) {
    }

    public Class worksOn() {
        return Score.class;
    }

    public Lamark getLamark() {
        return lamark;
    }

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }

}