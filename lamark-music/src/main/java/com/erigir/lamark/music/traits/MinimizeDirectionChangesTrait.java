package com.erigir.lamark.music.traits;

import java.util.logging.Logger;


/**
 * This trait tries to minimize the number of times the
 * notes change direction... that is, switching from heading
 * up to heading down or vice versa.  Normalized to 100
 * points
 *
 * @author cweiss
 */

public class MinimizeDirectionChangesTrait extends AbstractMusicTrait {
    private static Logger LOG = Logger.getLogger(MinimizeDirectionChangesTrait.class.getName());

    @Override
    public double guardedFitness() {
        LOG.finer("MDCT: " + getScoreAnalysis().getNoteDirectionChanges());
        double pointsPerChange = 100.0 / (double) getScoreAnalysis().getAllNotes().size();
        return 100.0 - (getScoreAnalysis().getNoteDirectionChanges() * pointsPerChange);
    }
}
