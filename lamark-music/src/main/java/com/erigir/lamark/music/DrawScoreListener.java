package com.erigir.lamark.music;

import com.erigir.lamark.Individual;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.gui.GUIEventListener;
import javafx.stage.Stage;
import jm.gui.cpn.Notate;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.View;

import java.awt.*;
import java.awt.event.WindowEvent;

public class DrawScoreListener implements GUIEventListener {
    private Stage parentStage;
    private Notate scoreFrame;

    public DrawScoreListener() {
        //scoreFrame = new Notate((Score)null,50,50);
    }

    @Override
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    public void handleEvent(LamarkEvent arg0) {
        if (arg0 instanceof BetterIndividualFoundEvent) {
            BetterIndividualFoundEvent bife = (BetterIndividualFoundEvent) arg0;
            Individual i = bife.getNewBest();

            Score s = (Score) i.getGenome();

            if (scoreFrame==null)
            {
                scoreFrame = new Notate(s,250,250);
            }

            /*
            if (scoreFrame!=null)
            {
                scoreFrame.windowClosing(new WindowEvent(null,0));
            }*/

            scoreFrame.setVisible(true);


            //View.notate(s);
        }



            /*
            // Build new score object to get around display bug
            ScoreAnalysis sa = (ScoreAnalysis) i.getAttribute("ANALYSIS");
            Phrase newPhrase = new Phrase();
            for (Note n : sa.getAllNotes()) {
                newPhrase.add(n);
            }
            Score newS = new Score(new Part(newPhrase));


            if (scoreFrame != null) {
                // Close the existing window
                scoreFrame.dispose();
            }

            scoreFrame = new Notate(newS, 0, 100);
            scoreFrame.setMinimumSize(new Dimension(100, 100));
            scoreFrame.setTitle("Best Individual:" + i.getFitness());
            //View.notate(newS);


            //scoreFrame = new Notate(s,50,50);

            //View.notate(s);
            //View.histogram(s);
            //scoreFrame=new ShowScore(s.getPart(0).getPhrase(0));
        }
        */
    }

    public void setParentComponent(Component ignored) {
    }

}
