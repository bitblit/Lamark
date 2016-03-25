package com.erigir.lamark;

import java.util.Objects;
import java.util.Random;

/**
 * Any class implementing the right interface can be a Lamark component, but
 * good ones need to support having an instance of a "Random" object passed
 * in, to support repeatable runs (with a set seed).  This class simplifies
 * construction of general Lamark components with this and other support
 * Created by cweiss1271 on 3/25/16.
 */
public abstract class AbstractLamarkComponent {
    private Random random = new Random(); // Yeah, it might get thrown away.  I'm ok with that, perf wise.

    public AbstractLamarkComponent()
    {
        super();
    }

    public AbstractLamarkComponent(Random srcRandom)
    {
        super();
        Objects.requireNonNull(srcRandom);
        this.random = srcRandom;
    }

    public Random rand()
    {
        return random;
    }

}
