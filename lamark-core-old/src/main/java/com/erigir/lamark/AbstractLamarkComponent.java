package com.erigir.lamark;

/**
 * Provides a default implementation of ILamarkComponent.
 * <p/>
 * This class provides a simple implementation of the requirements
 * of ILamarkComponent, so that descendant classes don't have to
 * deal with the details.
 *
 * @author cweiss
 * @version 2007-04-28
 */
public abstract class AbstractLamarkComponent implements ILamarkComponent {
    /**
     * Handle to the instance of lamark using this component
     */
    private Lamark lamark;

    /**
     * Returns a handle to the stored lamark instance.
     * Exposes the private Lamark instance
     * for use by any subclasses.
     *
     * @return Lamark instance for use.
     */
    public Lamark getLamark() {
        return lamark;
    }

    /**
     * @see com.erigir.lamark.ILamarkComponent#setLamark(Lamark)
     */
    public void setLamark(Lamark pLamark) {
        lamark = pLamark;
    }

}
