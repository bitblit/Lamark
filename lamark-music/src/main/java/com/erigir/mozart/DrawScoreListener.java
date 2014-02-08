package com.erigir.mozart;

import com.erigir.lamark.GUIEventListener;
import com.erigir.lamark.Individual;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import jm.gui.cpn.Notate;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import javax.swing.*;

public class DrawScoreListener implements GUIEventListener {
    private Notate scoreFrame;

    public void handleEvent(LamarkEvent arg0) {
        if (arg0 instanceof BetterIndividualFoundEvent) {
            BetterIndividualFoundEvent bife = (BetterIndividualFoundEvent) arg0;
            Individual i = bife.getNewBest();
            Score s = (Score) i.getGenome();

            // Build new score object to get aroud display bug
            ScoreAnalysis sa = (ScoreAnalysis) i.getAttribute("ANALYSIS");
            Phrase newPhrase = new Phrase();
            for (Note n : sa.getAllNotes()) {
                newPhrase.add(n);
            }
            Score newS = new Score(new Part(newPhrase));

            if (null == scoreFrame) {
                scoreFrame = new Notate(newS, 0, 100);
            } else {
                scoreFrame.setNewScore(newS);
            }


            //scoreFrame = new Notate(s,50,50);

            //View.notate(s);
            //View.histogram(s);
            //scoreFrame=new ShowScore(s.getPart(0).getPhrase(0));
            scoreFrame.setTitle("Best Individual:" + i.getFitness());
        }
    }

    public void setFrame(JFrame ignored) {
    }

}
