package com.erigir.lamark.music;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.music.phrase.PhraseUtils;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.data.Tempo;

import java.util.List;
import java.util.function.Supplier;

public class MozartSupplier extends AbstractLamarkComponent implements Supplier<Score>  {
    private static int SEARCH_ITERATIONS = 2;

    private int size;
    private ScaleEnum scale;
    private TimeSignatureEnum signature;
    private Integer lowerBound;
    private Integer upperBound;
    private Lamark lamark;

    @Override
    public Score get() {
        Score s = new Score();
        if (null != signature) {
            s.setDenominator(signature.denominator());
            s.setNumerator(signature.numerator());
        }
        if (null != scale) {
            s.setKeySignature(scale.getSharpOrFlatCount());
        }
        s.setTempo(Tempo.ANDANTE);
        Part p = new Part();
        for (int i = 0; i < size; i++) {
            p.appendPhrase(generatePhrase());
        }
        s.add(p);
        return s;
    }

    private Phrase generatePhrase() {
        Phrase first = PhraseUtils.generatePhrase(rand(), signature, scale, lowerBound, upperBound);

        // Does a quick search for nearby improved phrases
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


    public String translate(Individual arg0) {
        ScoreAnalysis sa = (ScoreAnalysis) arg0.getAttribute("ANALYSIS");
        String scores = (String) arg0.getAttribute("SCORES");
        if (null != sa) {
            return sa.toString() + " SCORES:" + scores;
        } else {
            Score s = (Score) arg0.getGenome();
            return "Score, Size=" + s.getPart(0).getPhrase(0).getSize() + " SCORES:" + scores;
        }
    }

    public Lamark getLamark() {
        return lamark;
    }

    public void setLamark(Lamark lamark) {
        this.lamark = lamark;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int pSize) {
        size = pSize;
    }

    public TimeSignatureEnum getSignature() {
        return signature;
    }

    public void setSignature(TimeSignatureEnum signature) {
        this.signature = signature;
    }

    public ScaleEnum getScale() {
        return scale;
    }

    public void setScale(ScaleEnum scale) {
        this.scale = scale;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }
}
