package com.erigir.lamark.music.traits;

import java.util.List;
import java.util.logging.Logger;


/**
 * This trait penalizes the music if it hits the same
 * key too many times in a row.
 *
 * @author cweiss
 */

public class PenalizeTooMuchTotalRepetitionTrait extends AbstractMusicTrait {
    private static Logger LOG = Logger.getLogger(PenalizeTooMuchTotalRepetitionTrait.class.getName());

    @Override
    public double guardedFitness() {
        List<Integer> list = getScoreAnalysis().getNoteDeltaList();

        double totalZeroCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (0 == list.get(i)) {
                totalZeroCount++;
            }
        }
        double zeroPercent = totalZeroCount / (double) list.size();
        double nonZeroPercent = 1.0 - zeroPercent;
        return 100.0 * nonZeroPercent;
    }
}
