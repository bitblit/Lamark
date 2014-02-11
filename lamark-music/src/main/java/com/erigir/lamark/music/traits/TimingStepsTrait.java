package com.erigir.lamark.music.traits;

import com.erigir.lamark.music.NoteDurationEnum;
import jm.music.data.Note;

import java.util.List;

/**
 * @author cweiss
 */

public class TimingStepsTrait extends AbstractMusicTrait {
    @Override
    public double guardedFitness() {
        List<Note> notes = getScoreAnalysis().getAllNotes();
        double pointsPerChange = 100.0 / (double) notes.size();
        double rval = 0;

        Note prev = null;
        int prev32 = -1;
        int cur32 = -1;
        double mult = 0;
        for (Note cur : notes) {
            if (null != prev) {
                cur32 = NoteDurationEnum.valueFromNote(cur).getThirtySeconds();
                if (cur32 == prev32 || cur32 == prev32 / 2.0 || cur32 == prev32 * 2.0) {
                    mult = 1.0;
                } else if (cur32 == prev32 / 3.0 || cur32 == prev32 * 3.0) {
                    mult = 0.5;
                }
            }
            rval += (pointsPerChange * mult);
            prev = cur;
            prev32 = cur32;
        }

        return rval;
    }
}
