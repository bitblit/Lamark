package com.erigir.lamark.music;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.music.phrase.PhraseUtils;
import jm.music.data.Score;
import org.junit.Test;

import java.util.*;

public class TestDrawScoreListener {
    @Test
    public void testDrawScore() {
        Lamark lamark = new Lamark();
        MozartSupplier supplier = new MozartSupplier();
        supplier.setLamark(lamark);
        supplier.setLowerBound(0);
        supplier.setScale(ScaleEnum.C);
        supplier.setSignature(TimeSignatureEnum.FOUR_FOUR);
        supplier.setSize(16);
        supplier.setUpperBound(80);

        ScoreFitness fitness = new ScoreFitness();


        Score score = supplier.get();

        System.out.println(score);

        System.out.println(fitness.applyAsDouble(score));
        DrawScoreListener dsl = new DrawScoreListener();
        BetterIndividualFoundEvent bife = new BetterIndividualFoundEvent(null,null,1L,new Individual(score));

        dsl.handleEvent(bife);

        while(true);


    }

}
