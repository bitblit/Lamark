package com.erigir.lamark.listener;

import com.erigir.lamark.events.*;

/**
 * Implementation of listener function for Lamark.
 * In this simple case, we just output events received to the
 * command line (standard out).
 *
 * @author cweiss
 * @since 11/2007
 */
public class StandardOutLoggingListener implements LamarkEventListener {

    /**
     * @see LamarkEventListener#handleEvent(LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        System.out.println("Received event: " + je);

        if (je instanceof ExceptionEvent) {
            ((ExceptionEvent) je).getException().printStackTrace();
        }

        if (je instanceof LastPopulationCompleteEvent) {
            System.out.println("Finished, best found was: " + je.getLamark().getBestSoFar());
            System.out.println("Total time spent was: " + je.getLamark().getRunTime() + " ms");
        }

    }

}
