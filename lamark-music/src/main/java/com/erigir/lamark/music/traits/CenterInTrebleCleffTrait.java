package com.erigir.lamark.music.traits;

import jm.music.data.Note;

import java.util.List;


/**
 * Since most western music is written primarily in notes
 * found within the treble cleff, this trait calculates the
 * percentage of notes in the treble cleff and returns
 * that number as its score
 * &lt;p /&gt;
 * Trebel cleff is notes 60 (middle c) to  81 (c above cleff)
 *
 * @author cweiss
 */

public class CenterInTrebleCleffTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {
        List<Note> all = getScoreAnalysis().getAllNotes();

        double count = 0;
        double size = all.size();
        for (Note n : all) {
            if (n.getPitch() >= 60 && n.getPitch() <= 81) {
                count++;
            }
        }

        return (count / size) * 100.0;
    }
}
