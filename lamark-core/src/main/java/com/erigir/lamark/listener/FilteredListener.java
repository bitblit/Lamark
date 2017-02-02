package com.erigir.lamark.listener;

import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;

import java.util.HashSet;
import java.util.Set;

/**
 *  A wrapper for a LamarkEventListener that holds the set of events of which it wishes to be informed
 * Created by cweiss1271 on 3/24/16.
 */
public class FilteredListener {
    private LamarkEventListener listener;
    private Set<Class<? extends LamarkEvent>> typeFilter = new HashSet<>();

    public FilteredListener(LamarkEventListener listener, Set<Class<? extends LamarkEvent>> typeFilter) {
        this.listener = listener;
        if (typeFilter!=null)
        {
            this.typeFilter.addAll(typeFilter);
        }
    }

    public void applyEvent(LamarkEvent event)
    {
        if (applies(event))
        {
            listener.handleEvent(event);
        }
    }

    public boolean applies(LamarkEvent event)
    {
        boolean rval = false;
        if (event!=null)
        {
            rval = (typeFilter.size()==0 || typeFilter.contains(event.getClass()));
        }
        return rval;
    }

    public void addFilter(Set<Class<? extends LamarkEvent>> clazz)
    {
        if (clazz!=null) {
            typeFilter.addAll(clazz);
        }
    }

    public void removeFilter(Class<? extends LamarkEvent> clazz)
    {
        if (clazz!=null)
        {
            typeFilter.remove(clazz);
        }
    }

    public LamarkEventListener getListener() {
        return listener;
    }

    public void setListener(LamarkEventListener listener) {
        this.listener = listener;
    }

    public Set<Class<? extends LamarkEvent>> getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(Set<Class<? extends LamarkEvent>> typeFilter) {
        this.typeFilter = typeFilter;
    }
}
