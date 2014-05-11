package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.NoteDurationEnum;
import jm.music.data.Note;

import java.util.List;
import java.util.logging.Logger;


/**
 * This trait penalizes any triplet used without a pair of
 * other triplet notes
 *
 * @author cweiss
 */

public class PenalizeInvalidTripletTrait extends AbstractMusicTrait {
    private static Logger LOG = Logger.getLogger(PenalizeInvalidTripletTrait.class.getName());

    @Override
    public double guardedFitness() {
        LOG.finer("PITT: " + getScoreAnalysis().getNoteDirectionChanges());
        List<Note> list = getScoreAnalysis().getAllNotes();
        double pointsPerChange = 100.0 / (double) list.size();

        double invalidCount = 0;
        int i = 0;

        while (i < list.size() - 2) {
            Note n = list.get(i);
            NoteDurationEnum nd = NoteDurationEnum.valueFromNote(n);
            if (nd.isTriplet()) {
                NoteDurationEnum n2 = NoteDurationEnum.valueFromNote(list.get(i + 1));
                NoteDurationEnum n3 = NoteDurationEnum.valueFromNote(list.get(i + 2));
                if (nd != n2 || n2 != n3) {
                    invalidCount++;
                }
            }
            i++;
        }
        return 100.0 - (invalidCount * pointsPerChange);
    }
}
