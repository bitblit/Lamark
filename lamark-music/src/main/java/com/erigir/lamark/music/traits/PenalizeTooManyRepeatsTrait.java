package com.erigir.lamark.music.traits;

import java.util.List;
import java.util.logging.Logger;


/**
 * This trait penalizes the music if it hits the same
 * key too many times in total.
 *
 * @author cweiss
 */

public class PenalizeTooManyRepeatsTrait extends AbstractMusicTrait {
    private static final int TOO_MANY = 3;
    private static Logger LOG = Logger.getLogger(PenalizeTooManyRepeatsTrait.class.getName());

    @Override
    public double guardedFitness() {
        List<Integer> list = getScoreAnalysis().getNoteDeltaList();

        int zerosInCurrentRun = 0;
        double penaltyCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (0 == list.get(i)) {
                zerosInCurrentRun++;
            } else // reset and check length
            {
                if (zerosInCurrentRun >= TOO_MANY) {
                    penaltyCount += zerosInCurrentRun;
                }
                zerosInCurrentRun = 0;
            }
        }
        double percentTooRepeated = penaltyCount / (double) list.size();
        double percentNotTooRepeated = 1.0 - percentTooRepeated;
        return 100.0 * percentNotTooRepeated;

    }
}
