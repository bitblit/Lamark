package com.erigir.lamark.music;

import com.erigir.lamark.Individual;
import com.erigir.lamark.Util;
import com.erigir.lamark.music.control.ControlSongs;
import com.erigir.lamark.music.traits.*;
import jm.music.data.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Given a set of traits, and a set of known songs (The controlsongs class)
 * this class finds the weights for those traits that maximizes the
 * score for those songs.  All songs in the set are treated equally
 *
 * @author cweiss
 */
public class WeightFinder {
    private static final Logger LOG = LoggerFactory.getLogger(WeightFinder.class);
    public static double MINIMUM_WEIGHT = 1.0;
    public static double MAXIMUM_WEIGHT = 3.0;
    public static double STEP_VALUE = 1.0;

    public static void main(String[] args) {
        List<IMusicTrait> traits = new ArrayList<IMusicTrait>();
        // Initialize traits
        //traits.add(new PercentInScaleTrait());
        //traits.add(new PercentInTimeSignatureTrait());
        traits.add(new CenterInTrebleCleffTrait());
        traits.add(new WithinStandardDeviationTrait()); //1.5
        traits.add(new ReduceStandardDeviationTrait()); //4.5
        traits.add(new RepeatingThemeTrait()); //2
        //traits.add(new RepeatingTimingTrait()); //2
        traits.add(new MinimizeDirectionChangesTrait());
        //traits.add(new PenalizeTooMuchTotalRepetitionTrait());
        traits.add(new PenalizeTooManyRepeatsTrait()); //1.5
        traits.add(new PenalizeLargeJumpsTrait()); //1.5
        traits.add(new PercentInScaleBigFiveTrait());
        traits.add(new StandardTimingTrait()); //2.5
        //traits.add(new PenalizeInvalidTripletTrait());
        //traits.add(new GoodEndingTrait());
        traits.add(new TimingStepsTrait()); //2.0
        double[] best = calculateBest(traits);
        LOG.info("Best found=" + Arrays.asList(best));

        // double[] tester = new double[]{3,3,3,3,3,3,3,1,3,1,3,1,1,3,3,1};
        //  LOG.info("Tester="+percentScoreForWeights(traits,tester));

    }

    public static double percentScoreForWeights(List<IMusicTrait> traits, double[] weights) {
        if (traits.size() != weights.length) {
            throw new IllegalArgumentException("Trait size must equal weight size");
        }
        double[] ass = averageSongScores(traits);
        double sumScore = 0;
        double maxScore = 0;
        for (int i = 0; i < ass.length; i++) {
            sumScore += ass[i] * weights[i];
            maxScore += 100 * weights[i];
        }
        return sumScore / maxScore;
    }

    public static double[] averageSongScores(List<IMusicTrait> traits) {
        // Calculate the scores
        MozartFitness fitness = new MozartFitness();
        Score[] songs = ControlSongs.getSongs();
        double[][] scores = new double[songs.length][traits.size()];
        double[] songAverageScore = new double[traits.size()];
        Individual div = new Individual();
        List<TraitWrapper> test = new ArrayList<TraitWrapper>();
        for (IMusicTrait t : traits) {
            test.add(new TraitWrapper(t, 1.0));
        }
        fitness.initTraits(test);
        for (int i = 0; i < songs.length; i++) {
            div.setGenome(songs[i]);
            fitness.fitnessValue(div);
            for (int j = 0; j < test.size(); j++) {
                scores[i][j] = test.get(j).getFitness();
                songAverageScore[j] += scores[i][j];
            }
            LOG.info("For song {} scores was {}", songs[i].getTitle(), Arrays.asList(scores[i]));
        }
        for (int i = 0; i < test.size(); i++) {
            songAverageScore[i] /= (double) songs.length;
        }
        LOG.info("Song average score: {}" + Arrays.asList(songAverageScore));
        return songAverageScore;
    }

    public static double[] calculateBest(List<IMusicTrait> traits) {
        if (null == traits) {
            throw new IllegalArgumentException("Traits cannot be null");
        }
        double[] rval = new double[traits.size()];
        double[] songAverageScore = averageSongScores(traits);

        double bestPercentage = 0.0;
        double[] weights = new double[traits.size()];

        // Init weights to min
        for (int i = 0; i < weights.length; i++) {
            weights[i] = MINIMUM_WEIGHT;
        }

        boolean allWeightsMaximum = false;

        double sumFit = 0;
        double maxFit = 0;
        double currentPercent;
        double stepsPerIndex = ((MAXIMUM_WEIGHT - MINIMUM_WEIGHT) + 1) / STEP_VALUE;
        double iterationCount = Math.pow(stepsPerIndex, weights.length);
        LOG.info("Trying {} per index, {} indexs, total of {} iterations", new Object[]{stepsPerIndex, weights.length, iterationCount});

        double iteration = 0;
        long startTime = System.currentTimeMillis();
        int avail;
        try {
            while (!allWeightsMaximum) {
                iteration++;

                avail = System.in.available();
                if (avail > 0) {
                    for (int i = 0; i < avail; i++) {
                        System.in.read();
                    }

                    LOG.info("Iteration #" + iteration);
                    double pctDone = iteration / iterationCount;
                    LOG.info(Util.format(pctDone) + " percent done");
                    long runtime = System.currentTimeMillis() - startTime;
                    long perIt = (long) ((iteration * 1000.0) / (double) runtime);
                    LOG.info(perIt + " iterations per second");
                    LOG.info("Trying " + Arrays.asList(weights));
                    LOG.info("Current runtime:" + Util.formatISO(runtime));
                    long estTime = (long) (runtime / pctDone);
                    LOG.info("Est total time:" + Util.formatISO(estTime));
                    LOG.info("Est rem time:" + Util.formatISO(estTime - runtime));
                    LOG.info("\n\n");
                }

                sumFit = 0;
                maxFit = 0;
                for (int j = 0; j < songAverageScore.length; j++) {
                    maxFit += 100 * weights[j];
                    sumFit += songAverageScore[j] * weights[j];
                }

                currentPercent = sumFit / maxFit;

                //LOG.info("Trying "+Arrays.asList(weights)+" pct = "+Util.format(currentPercent*100));

                if (currentPercent > bestPercentage) {
                    bestPercentage = currentPercent;
                    LOG.info("New Best found %=" + bestPercentage);
                    LOG.info("Weights:" + Arrays.asList(weights));
                    System.arraycopy(weights, 0, rval, 0, weights.length);
                }


                // Find the last contiguous max (+1)
                int lastMax = 0;
                while (lastMax < weights.length && weights[lastMax] >= MAXIMUM_WEIGHT) {
                    lastMax++;
                }
                // Now back up one
                lastMax--;

                //LOG.info("w="+Arrays.asList(weights)+" lm="+lastMax);

                if (lastMax == (weights.length - 1)) {
                    // The whole thing is at max
                    LOG.info("len=" + weights.length + " lastmax=" + lastMax);
                    allWeightsMaximum = true;
                } else {
                    // Increment the one after the last max
                    weights[lastMax + 1] += STEP_VALUE;
                    // Reset all values from 0 to lastMax
                    for (int i = 0; i <= lastMax; i++) {
                        weights[i] = MINIMUM_WEIGHT;
                    }
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        LOG.info("Last tried: {}", Arrays.asList(weights));

        return rval;
    }


}
