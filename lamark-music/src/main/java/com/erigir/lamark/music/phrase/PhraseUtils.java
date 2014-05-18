package com.erigir.lamark.music.phrase;

import com.erigir.lamark.Individual;
import com.erigir.lamark.music.NoteDurationEnum;
import com.erigir.lamark.music.ScaleEnum;
import com.erigir.lamark.music.TimeSignatureEnum;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class has a bunch of static methods dealing with the
 * creation and modification of phrases.  Generally used
 * for the creation of parts of a score
 *
 * @author cweiss
 */

public class PhraseUtils {

    private static PhraseFitness FITNESS_CALCULATOR = new PhraseFitness();


    public static double scorePhrase(Phrase p) {
        Individual i = new Individual(new Score(new Part(p)));
        return FITNESS_CALCULATOR.fitnessValue(i);
    }

    /**
     * Generates a new phrase object matching the input parameters.  If signature is not null,
     * then the phrase will match the supplied time signature.  Otherwise the phrase will
     * contain a random number of notes (but not less than one or more than one whole note).  If the
     * scale is not null, the notes will all be in that scale, otherwise they may be any valid note.
     * If lowerBound is not null, all notes will be equal or greater to that value.  If upperbound
     * is not null, all notes will be less than upperbound
     *
     * @param signature  TimeSignatureEnum to constrain the generated phrase.
     * @param scale      ScaleEnum to constrain the generated phrase.
     * @param lowerBound Integer lower bound of generated notes.
     * @param upperBound Integer upper bound of generated notes.
     * @return
     */
    public static Phrase generatePhrase(Random random, TimeSignatureEnum signature, ScaleEnum scale, Integer lowerBound, Integer upperBound) {
        if (lowerBound == null || lowerBound < 0) {
            lowerBound = 0;
        }
        if (upperBound == null || upperBound > 128) {
            upperBound = 128;
        }
        Phrase rval = new Phrase();

        // Build list of valid note numbers
        List<Integer> validNotes = new ArrayList<Integer>();
        for (int i = lowerBound; i < upperBound; i++) {
            validNotes.add(i);
        }
        if (null != scale) {
            validNotes.retainAll(scale.scale());
        }
        int remainder;
        if (signature != null) {
            remainder = signature.barValueInThirtySeconds();
        } else {
            remainder = 1 + random.nextInt(32);
        }
        while (remainder > 0) // add a note
        {
            NoteDurationEnum nd = NoteDurationEnum.newNoteValue(remainder, random);
            Integer noteTone = validNotes.get(random.nextInt(validNotes.size()));
            rval.add(new Note(noteTone, nd.getDuration()));
            remainder -= nd.getThirtySeconds();
        }
        return rval;
    }

    /**
     * Given a phrase, generates a list of "neighbor" phrases to that phrase, as
     * understood in a "hill climbing" context.
     *
     * @param phrase Phrase to generate neighbors to
     * @return
     */
    public static List<Phrase> neighbors(Phrase phrase) {
        List<Phrase> rval = new ArrayList<Phrase>();

        rval.add(expand(phrase));
        rval.add(combine(phrase));
        rval.add(flatGreatestOutlier(phrase));
        rval.add(flatten(phrase));
        rval.add(moveDown(phrase));
        rval.add(moveUp(phrase));

        for (int i = 0; i < phrase.size(); i++) {
            rval.add(moveNoteUp(phrase, i));
            rval.add(moveNoteDown(phrase, i));
        }

        return rval;
    }

    /**
     * Generates a new clone of a phrase
     *
     * @param p
     * @return
     */
    public static Phrase copyPhrase(Phrase p) {
        Phrase rval = new Phrase();
        for (Note n : p.getNoteArray()) {
            rval.add(n);
        }
        return rval;
    }

    public static Phrase expand(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);
        Note[] notes = ph.getNoteArray();
        int sumPitch = 0;
        for (Note n : notes) {
            sumPitch += n.getPitch();
        }
        int avPitch = sumPitch / notes.length;
        for (Note n : notes) {
            if (n.getPitch() > avPitch) {
                n.setPitch(n.getPitch() - 1);
            }
            if (n.getPitch() < avPitch) {
                n.setPitch(n.getPitch() + 1);
            }
        }
        return ph;
    }

    public static Phrase combine(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);

        Note[] notes = ph.getNoteArray();
        NoteDurationEnum[] ndea = NoteDurationEnum.values();
        boolean done = false;
        for (int i = 0; i < notes.length - 1 && !done; i++) {
            NoteDurationEnum nd = NoteDurationEnum.valueFromNote(notes[i]);
            NoteDurationEnum nd1 = NoteDurationEnum.valueFromNote(notes[i + 1]);

            int tts = nd.getThirtySeconds() + nd1.getThirtySeconds();

            for (int j = 0; j < ndea.length && !done; j++) {
                if (tts == ndea[j].getThirtySeconds()) {
                    notes[i].setRhythmValue(ndea[j].getDuration());
                    ph.removeNote(i + 1);
                    done = true;
                }
            }
        }
        return ph;
    }

    public static Phrase flatten(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);

        Note[] notes = ph.getNoteArray();
        int sumPitch = 0;
        for (Note n : notes) {
            sumPitch += n.getPitch();
        }
        int avPitch = sumPitch / notes.length;
        for (Note n : notes) {
            if (n.getPitch() > avPitch) {
                n.setPitch(n.getPitch() + 1);
            }
            if (n.getPitch() < avPitch) {
                n.setPitch(n.getPitch() - 1);
            }
        }
        return ph;

    }

    public static Phrase flatGreatestOutlier(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);

        Note[] notes = ph.getNoteArray();
        int sumPitch = 0;
        int maxNoteIdx = -1, maxNoteValue = -1;
        int minNoteIdx = -1, minNoteValue = 250;
        for (int i = 0; i < notes.length; i++) {
            Note n = notes[i];
            sumPitch += n.getPitch();
            if (n.getPitch() > maxNoteValue) {
                maxNoteValue = n.getPitch();
                maxNoteIdx = i;
            }
            if (n.getPitch() < minNoteValue) {
                minNoteValue = n.getPitch();
                minNoteIdx = i;
            }
        }
        int avPitch = sumPitch / notes.length;
        int idx = -1;
        if (Math.abs(avPitch - maxNoteValue) > Math.abs(avPitch - minNoteValue)) {
            idx = maxNoteIdx;
        } else {
            idx = minNoteIdx;
        }

        notes[idx].setPitch(avPitch);
        return ph;
    }

    public static Phrase moveUp(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);

        for (Note n : ph.getNoteArray()) {
            if (n.getPitch() < 127) {
                n.setPitch(n.getPitch() + 1);
            }
        }
        return ph;
    }

    public static Phrase moveDown(Phrase phrase) {
        Phrase ph = copyPhrase(phrase);

        for (Note n : ph.getNoteArray()) {
            if (n.getPitch() > 0) {
                n.setPitch(n.getPitch() - 1);
            }
        }
        return ph;
    }

    public static Phrase moveNoteDown(Phrase phrase, int noteIdx) {
        Phrase ph = copyPhrase(phrase);
        if (ph.size() > noteIdx) {
            Note n = ph.getNote(noteIdx);
            if (n.getPitch() > 0) {
                n.setPitch(n.getPitch() - 1);
            }
        }
        return ph;
    }

    public static Phrase moveNoteUp(Phrase phrase, int noteIdx) {
        Phrase ph = copyPhrase(phrase);
        if (ph.size() > noteIdx) {
            Note n = ph.getNote(noteIdx);
            if (n.getPitch() < 127) {
                n.setPitch(n.getPitch() + 1);
            }
        }
        return ph;
    }
}
