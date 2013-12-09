package com.erigir.lamark.events;

/**
 * Interface implemented by objects wanting to receive lamark-generated events.
 * 
 * @author cweiss
 * @since 03/2005
 */
public interface LamarkEventListener
{
    /**
     * Called by lamark when a lamark event occurs
     * @param je LamarkEvent sent by the lamark instance
     */
    void handleEvent(LamarkEvent je);
}
