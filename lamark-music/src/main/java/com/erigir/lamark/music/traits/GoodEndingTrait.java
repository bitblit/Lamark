package com.erigir.lamark.music.traits;

import jm.music.data.Note;

import java.util.List;
import java.util.logging.Logger;


/**
 * a song should end downward and longer
 *
 * @author cweiss
 */

public class GoodEndingTrait extends AbstractMusicTrait {
    private static Logger LOG = Logger.getLogger(GoodEndingTrait.class.getName());

    @Override
    public double guardedFitness() {
        LOG.finer("GET: " + getScoreAnalysis().getNoteDirectionChanges());
        List<Note> list = getScoreAnalysis().getAllNotes();

        Note last = list.get(list.size() - 1);
        Note second = list.get(list.size() - 1);

        if (second.getDuration() <= last.getDuration() && second.getPitch() >= last.getPitch()) {
            return 100;
        }
        return 0;
    }
}
