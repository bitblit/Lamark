package com.erigir.lamark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: chrweiss
 * Date: 2/15/14
 * Time: 2:31 PM
 */
public class LamarkConfigurationFailedException extends RuntimeException{
    private List<String> reasons = new LinkedList<String>();

    public LamarkConfigurationFailedException(List<String> reasons) {
        this.reasons = reasons;
    }

    public List<String> getReasons() {
        return Collections.unmodifiableList(reasons);
    }

    public void addReason(String reason)
    {
        if (reason!=null && reason.length()>0)
        {
            reasons.add(reason);
        }
    }


}
