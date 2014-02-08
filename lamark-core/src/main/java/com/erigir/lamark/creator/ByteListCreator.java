/*
 * Created on Mar 29, 2005
 */
package com.erigir.lamark.creator;

import com.erigir.lamark.AbstractLamarkComponent;
import com.erigir.lamark.EConfigResult;
import com.erigir.lamark.IConfigurable;
import com.erigir.lamark.ICreator;
import com.erigir.lamark.IValidatable;
import com.erigir.lamark.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Creator that creates individuals with a genome of Lists of bytes.
 * Size must be set, or the validator method will add an error.
 *
 * @author cweiss
 * @since 09/2007
 */
public class ByteListCreator extends AbstractLamarkComponent implements ICreator<List<Byte>>, IValidatable, IConfigurable {
    /**
     * Size of byte list to create *
     */
    private Integer size;

    /**
     * Accessor method.
     *
     * @return Integer containing the property
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Mutator method.
     *
     * @param size Integer containing the new value
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @see com.erigir.lamark.ICreator#create()
     */
    public Individual<List<Byte>> create() {
        if (size == null) {
            throw new IllegalStateException("Cannot process, 'size' not set");
        }
        byte[] temp = new byte[size];
        getLamark().getRandom().nextBytes(temp);

        List<Byte> rval = new ArrayList<Byte>(size);
        for (byte b : temp) {
            rval.add(b);
        }
        Individual<List<Byte>> i = new Individual<List<Byte>>();
        i.setGenome(rval);
        return i;
    }

    /**
     * @see com.erigir.lamark.IValidatable#validate(List)
     */
    public void validate(List<String> errors) {
        if (size == null) {
            errors.add("No 'size' set for the creator");
        }
    }

    /**
     * @see com.erigir.lamark.IConfigurable#setProperty(String, String)
     */
    public EConfigResult setProperty(String name, String value) {
        if (name.equalsIgnoreCase("size")) {
            try {
                size = new Integer(value);
                return EConfigResult.OK;
            } catch (Exception e) {
                return EConfigResult.INVALID_VALUE;
            }
        } else {
            return EConfigResult.NO_SUCH_PROPERTY;
        }
    }

    /**
     * @see com.erigir.lamark.IConfigurable#getProperties()
     */
    public Properties getProperties() {
        Properties p = new Properties();
        if (size != null) {
            p.setProperty("size", size.toString());
        }
        return p;
    }
}