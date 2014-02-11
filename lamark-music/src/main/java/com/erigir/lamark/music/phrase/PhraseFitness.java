package com.erigir.lamark.music.phrase;

import com.erigir.lamark.music.MozartFitness;
import com.erigir.lamark.music.traits.CenterInTrebleCleffTrait;
import com.erigir.lamark.music.traits.MinimizeDirectionChangesTrait;
import com.erigir.lamark.music.traits.PenalizeLargeJumpsTrait;
import com.erigir.lamark.music.traits.PenalizeTooManyRepeatsTrait;
import com.erigir.lamark.music.traits.PenalizeTooMuchTotalRepetitionTrait;
import com.erigir.lamark.music.traits.PercentInScaleBigFiveTrait;
import com.erigir.lamark.music.traits.PercentInScaleTrait;
import com.erigir.lamark.music.traits.PercentInTimeSignatureTrait;
import com.erigir.lamark.music.traits.StandardTimingTrait;
import com.erigir.lamark.music.traits.TimingStepsTrait;
import com.erigir.lamark.music.traits.TraitWrapper;

import java.util.HashSet;

public class PhraseFitness extends MozartFitness {
    public PhraseFitness() {
        super();

        HashSet<TraitWrapper> traits = new HashSet<TraitWrapper>();
        // Initialize traits
        traits.add(new TraitWrapper(new PercentInScaleTrait(), 1.0));
        traits.add(new TraitWrapper(new PercentInTimeSignatureTrait(), 1.0));
        traits.add(new TraitWrapper(new CenterInTrebleCleffTrait(), 2.5));
        //traits.add(new TraitWrapper(new WithinStandardDeviationTrait(), 1.0)); //1.5
        //traits.add(new TraitWrapper(new ReduceStandardDeviationTrait(), 1.0)); //4.5
        traits.add(new TraitWrapper(new MinimizeDirectionChangesTrait(), 1.0));
        traits.add(new TraitWrapper(new PenalizeTooMuchTotalRepetitionTrait(), 20.5)); //1.5
        traits.add(new TraitWrapper(new PenalizeTooManyRepeatsTrait(), 2.5)); //1.5
        traits.add(new TraitWrapper(new PenalizeLargeJumpsTrait(), 1.5)); //1.5
        traits.add(new TraitWrapper(new PercentInScaleBigFiveTrait(), 1.0));
        traits.add(new TraitWrapper(new StandardTimingTrait(), 2.0)); //2.5
        traits.add(new TraitWrapper(new TimingStepsTrait(), 2.0)); //2.0
        initTraits(traits);
    }
}
