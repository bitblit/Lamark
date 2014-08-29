package com.erigir.lamark;

import com.erigir.lamark.annotation.IndividualFormatter;
import com.erigir.lamark.annotation.LamarkComponent;

import java.util.Collection;

/**
 * Default formatter for individuals.
 * <p/>
 * Just calls the toString method for the contained genome.
 *
 * @author cweiss
 */
public class DefaultIndividualFormatter {

    /**
     * @see com.erigir.lamark.IIndividualFormatter#format(Individual)
     */
    @IndividualFormatter
    public String format(Individual<Object> toFormat) {
        return toFormat.getGenome().toString();
    }

}
