package com.erigir.lamark;

import com.erigir.lamark.annotation.IndividualFormatter;
import com.erigir.lamark.annotation.LamarkComponent;

/**
 * Default formatter for individuals.
 * <p/>
 * Just calls the toString method for the contained genome.
 *
 * @author cweiss
 */
@LamarkComponent
public class DefaultIndividualFormatter {

    @IndividualFormatter
    public String format(Individual<Object> toFormat) {
        return String.valueOf(toFormat.getGenome());
    }

}
