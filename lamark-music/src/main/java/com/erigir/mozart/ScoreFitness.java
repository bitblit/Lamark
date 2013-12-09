package com.erigir.mozart;

import java.util.HashSet;

import com.erigir.mozart.traits.CenterInTrebleCleffTrait;
import com.erigir.mozart.traits.GoodEndingTrait;
import com.erigir.mozart.traits.MinimizeDirectionChangesTrait;
import com.erigir.mozart.traits.PenalizeInvalidTripletTrait;
import com.erigir.mozart.traits.PenalizeLargeJumpsTrait;
import com.erigir.mozart.traits.PenalizeTooManyRepeatsTrait;
import com.erigir.mozart.traits.PenalizeTooMuchTotalRepetitionTrait;
import com.erigir.mozart.traits.PercentInScaleBigFiveTrait;
import com.erigir.mozart.traits.PercentInScaleTrait;
import com.erigir.mozart.traits.PercentInTimeSignatureTrait;
import com.erigir.mozart.traits.ReduceStandardDeviationTrait;
import com.erigir.mozart.traits.RepeatingThemeTrait;
import com.erigir.mozart.traits.RepeatingTimingTrait;
import com.erigir.mozart.traits.StandardTimingTrait;
import com.erigir.mozart.traits.TimingStepsTrait;
import com.erigir.mozart.traits.TraitWrapper;
import com.erigir.mozart.traits.WithinStandardDeviationTrait;


public class ScoreFitness extends MozartFitness
{
	public ScoreFitness()
	{
		super();

		HashSet<TraitWrapper> traits = new HashSet<TraitWrapper>();
		// Initialize traits
		traits.add(new TraitWrapper(new PercentInScaleTrait(), 1.0));
		traits.add(new TraitWrapper(new PercentInTimeSignatureTrait(), 1.0));
		traits.add(new TraitWrapper(new CenterInTrebleCleffTrait(), 2.5));
		traits.add(new TraitWrapper(new WithinStandardDeviationTrait(), 1.0)); //1.5
		traits.add(new TraitWrapper(new ReduceStandardDeviationTrait(), 1.0)); //4.5
		traits.add(new TraitWrapper(new RepeatingThemeTrait(), 4.5)); //2
		traits.add(new TraitWrapper(new RepeatingTimingTrait(), 4.5)); //2
		traits.add(new TraitWrapper(new MinimizeDirectionChangesTrait(), 1.0));
		traits.add(new TraitWrapper(new PenalizeTooMuchTotalRepetitionTrait(),
			8.5)); //1.5
		traits.add(new TraitWrapper(new PenalizeTooManyRepeatsTrait(), 8.5)); //1.5
		traits.add(new TraitWrapper(new PenalizeLargeJumpsTrait(), 1.5)); //1.5
		traits.add(new TraitWrapper(new PercentInScaleBigFiveTrait(), 1.0));
		traits.add(new TraitWrapper(new StandardTimingTrait(), 3.0)); //2.5
		//traits.add(new TraitWrapper(new PenalizeInvalidTripletTrait(), 1.0));
		traits.add(new TraitWrapper(new GoodEndingTrait(), 1.0));
        traits.add(new TraitWrapper(new TimingStepsTrait(), 2.0)); //2.0
		initTraits(traits);
	}
}
