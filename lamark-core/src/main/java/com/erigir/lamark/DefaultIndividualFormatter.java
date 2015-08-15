package com.erigir.lamark;

import com.erigir.lamark.annotation.IndividualFormatter;

import java.util.Collection;

/**
 * Default formatter for individuals.
 * <p/>
 * Just calls the toString method for the contained genome.
 *
 * @author cweiss
 */
public class DefaultIndividualFormatter implements IIndividualFormatter<Object> {

    /**
     * @see com.erigir.lamark.IIndividualFormatter#format(Individual)
     */
    public String format(Individual<Object> toFormat) {
        return toFormat.getGenome().toString();
    }

    /**
     * @see com.erigir.lamark.IIndividualFormatter#format(Collection)
     */
    @IndividualFormatter
    public String format(Collection<Individual<Object>> toFormat) {
        StringBuffer sb = new StringBuffer();

        boolean first = true;
        for (Individual i : toFormat) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(format(i));
            first = false;
        }
        return sb.toString();
    }

}
