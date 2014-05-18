package com.erigir.lamark.music.phrase;

import com.erigir.lamark.music.ScaleEnum;
import com.erigir.lamark.music.TimeSignatureEnum;
import jm.music.data.Phrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Eventually this class will run a GA to generate it's
 * pool.  For now, generate algorithmically
 *
 * @author cweiss
 */
public class PhrasePool {
    public static PhrasePool instance = new PhrasePool();
    public static int POOLSIZE = 100;
    private List<PhraseWrapper> pool = new ArrayList<PhraseWrapper>(POOLSIZE);
    private static int SEARCH_ITERATIONS = 2;
    private ScaleEnum scale;
    private TimeSignatureEnum signature;
    private Integer lowerBound;
    private Integer upperBound;
    private boolean initialized = false;
    private Random random;

    private PhrasePool() {
        super();
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Phrase getPhrase() {
        if (!initialized) {
            throw new IllegalStateException("Cannot use pool until initialized");
        }
        if (pool.size() < (.5 * POOLSIZE)) {
            fillPool();
        }
        return pool.remove(0).p;
    }

    private void fillPool() {
        while (pool.size() < POOLSIZE) {
            PhraseWrapper pw = new PhraseWrapper();
            pw.p = generatePhrase();
            pw.score = PhraseUtils.scorePhrase(pw.p);
            pool.add(pw);
        }
        Collections.sort(pool);
    }

    private Phrase generatePhrase() {
        Phrase first = PhraseUtils.generatePhrase(random, signature, scale, lowerBound, upperBound);

        Phrase current = first;
        double currentScore = PhraseUtils.scorePhrase(current);
        for (int i = 0; i < SEARCH_ITERATIONS; i++) {
            List<Phrase> neighbors = PhraseUtils.neighbors(current);
            for (Phrase p : neighbors) {
                double test = PhraseUtils.scorePhrase(p);
                if (test > currentScore) {
                    current = p;
                    currentScore = test;
                }
            }
        }

        return current;
    }

    public void initialize(Random pRandom, TimeSignatureEnum pSignature, ScaleEnum pScale, Integer pLowerBound, Integer pUpperBound) {
        random = pRandom;
        scale = pScale;
        signature = pSignature;
        lowerBound = pLowerBound;
        upperBound = pUpperBound;
        initialized = true;
    }

    public boolean initialized() {
        return initialized;
    }

    class PhraseWrapper implements Comparable<PhraseWrapper> {
        Phrase p;
        double score;

        public int compareTo(PhraseWrapper o) {
            if (score > o.score) {
                return -1;
            }
            if (score == o.score) {
                return 0;
            }
            return 1;
        }

        public String toString() {
            return "Pw size=" + p.size() + " score=" + score;
        }
    }


}
