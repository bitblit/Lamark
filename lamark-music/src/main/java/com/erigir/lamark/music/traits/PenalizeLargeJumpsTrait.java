package com.erigir.lamark.music.traits;

import java.util.List;
import java.util.logging.Logger;


/**
 * This trait penalizes the music if jumps too
 * far from one note to the next.
 *
 * @author cweiss
 */

public class PenalizeLargeJumpsTrait extends AbstractMusicTrait {
    private static Logger LOG = Logger.getLogger(PenalizeLargeJumpsTrait.class.getName());
    private static final int TOO_FAR = 4;

    @Override
    public double guardedFitness() {
        LOG.finer("PLJT: " + getScoreAnalysis().getNoteDirectionChanges());
        List<Integer> list = getScoreAnalysis().getNoteDeltaList();
        double pointsPerChange = 100.0 / (double) list.size();


        double jumpCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (Math.abs(list.get(i)) > TOO_FAR) {
                jumpCount++;
            }
        }
        return 100.0 - (jumpCount * pointsPerChange);
    }
}
