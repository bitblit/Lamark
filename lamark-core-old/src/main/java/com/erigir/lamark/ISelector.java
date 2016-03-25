/*
 * Copyright Erigir, Inc
 * Las Vegas NV
 * All Rights Reserved
 * 
 * Created on Aug 9, 2005
 */
package com.erigir.lamark;

import java.util.List;

/**
 * ISelector is implemented by classes serving as selectors for Lamark.
 * <p/>
 * A selector is a class which determines which individuals from a given
 * population should be "crossed over" to create the members of the next
 * generation.  Lamark provides a default "RouletteWheel" implementation,
 * which is typically the reference implementation for GAs.  It also provides
 * a very simplistic "Tournament" selector as well.  In general, it shouldn't
 * be necessary to implement another selector unless that is your specific
 * field of research.
 *
 * @author cweiss
 * @since 4-1-06
 */
public interface ISelector extends ILamarkComponent {

    /**
     * Selects <code>count</code> individuals from the given list and returns them as a list.
     *
     * @param individuals List of individuals to select from
     * @param count       int containing the number of individuals to return
     * @return List containing the selected individuals
     */
    List<Individual<?>> select(List<Individual<?>> individuals, int count);

}