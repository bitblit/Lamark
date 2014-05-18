package com.erigir.lamark.music;

import jm.JMC;
import jm.music.data.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum NoteDurationEnum {
    THIRTYSECOND_NOTE_TRIPLET,
    THIRTYSECOND_NOTE,
    SIXTEENTH_NOTE_TRIPLET,
    SIXTEENTH_NOTE,
    EIGHTH_NOTE_TRIPLET,
    DOTTED_SIXTEENTH_NOTE,
    EIGHTH_NOTE,
    QUARTER_NOTE_TRIPLET,
    DOTTED_EIGHTH_NOTE,
    DOUBLE_DOTTED_EIGHTH_NOTE,
    QUARTER_NOTE,
    HALF_NOTE_TRIPLET,
    DOTTED_QUARTER_NOTE,
    DOUBLE_DOTTED_QUARTER_NOTE,
    HALF_NOTE,
    DOTTED_HALF_NOTE,
    DOUBLE_DOTTED_HALF_NOTE,
    WHOLE_NOTE;

    // An array from 1-32.  Each list contains all durations whose value in 32s
    // is less then or equal to the array index
    private static List<List> notesLessThanOrEqualTo;

    private static List<NoteDurationEnum> cacheCreationExcludedNotes;

    public static List<NoteDurationEnum> creationExcludedNotes() {
        if (null == cacheCreationExcludedNotes) {
            cacheCreationExcludedNotes = new ArrayList<NoteDurationEnum>();
            cacheCreationExcludedNotes.add(NoteDurationEnum.THIRTYSECOND_NOTE_TRIPLET);
            cacheCreationExcludedNotes.add(NoteDurationEnum.SIXTEENTH_NOTE_TRIPLET);
            cacheCreationExcludedNotes.add(NoteDurationEnum.EIGHTH_NOTE_TRIPLET);
            cacheCreationExcludedNotes.add(NoteDurationEnum.QUARTER_NOTE_TRIPLET);
        }
        return cacheCreationExcludedNotes;

    }

    private static synchronized void initializeNotesLessList() {
        if (null == notesLessThanOrEqualTo) {
            // init the valuesLessThan array
            notesLessThanOrEqualTo = new ArrayList<List>(33);

            for (int i = 0; i < 33; i++) {
                notesLessThanOrEqualTo.add(new ArrayList<NoteDurationEnum>());
            }

            for (NoteDurationEnum nd : values()) {
                int count = nd.getThirtySeconds();
                for (int i = count; i <= 32; i++) {
                    List<NoteDurationEnum> list = notesLessThanOrEqualTo.get(i);
                    list.add(nd);
                }
            }
        }
        // remove any creation excluded notes
        for (int i = 0; i < 33; i++) {
            notesLessThanOrEqualTo.get(0).removeAll(creationExcludedNotes());
        }
    }

    public static boolean isShortNote(Note n) {
        NoteDurationEnum nde = valueFromNote(n);
        switch (nde) {
            case THIRTYSECOND_NOTE_TRIPLET:
                return true;
            case THIRTYSECOND_NOTE:
                return true;
            case SIXTEENTH_NOTE_TRIPLET:
                return true;
            case SIXTEENTH_NOTE:
                return true;
            case EIGHTH_NOTE_TRIPLET:
                return true;
            case DOTTED_SIXTEENTH_NOTE:
                return true;
            case EIGHTH_NOTE:
                return true;
            case QUARTER_NOTE_TRIPLET:
                return true;
            case DOTTED_EIGHTH_NOTE:
                return true;
            case DOUBLE_DOTTED_EIGHTH_NOTE:
                return true;
            case QUARTER_NOTE:
                return false;
            case HALF_NOTE_TRIPLET:
                return false;
            case DOTTED_QUARTER_NOTE:
                return false;
            case DOUBLE_DOTTED_QUARTER_NOTE:
                return false;
            case HALF_NOTE:
                return false;
            case DOTTED_HALF_NOTE:
                return false;
            case DOUBLE_DOTTED_HALF_NOTE:
                return false;
            case WHOLE_NOTE:
                return false;
            default:
                throw new IllegalStateException("Cant happen : not a member of this enum: " + n);
        }
    }

    public static boolean isWholeNote(Note n) {
        NoteDurationEnum nde = valueFromNote(n);
        switch (nde) {
            case WHOLE_NOTE:
                return true;
            default:
                return false;
        }
    }

    public static NoteDurationEnum valueFromNote(Note n) {
        double d = n.getRhythmValue();
        if (JMC.THIRTYSECOND_NOTE_TRIPLET == d) {
            return THIRTYSECOND_NOTE_TRIPLET;
        } else if (JMC.THIRTYSECOND_NOTE == d) {
            return THIRTYSECOND_NOTE;
        } else if (JMC.SIXTEENTH_NOTE_TRIPLET == d) {
            return SIXTEENTH_NOTE_TRIPLET;
        } else if (JMC.SIXTEENTH_NOTE == d) {
            return SIXTEENTH_NOTE;
        } else if (JMC.EIGHTH_NOTE_TRIPLET == d) {
            return EIGHTH_NOTE_TRIPLET;
        } else if (JMC.DOTTED_SIXTEENTH_NOTE == d) {
            return DOTTED_SIXTEENTH_NOTE;
        } else if (JMC.EIGHTH_NOTE == d) {
            return EIGHTH_NOTE;
        } else if (JMC.QUARTER_NOTE_TRIPLET == d) {
            return QUARTER_NOTE_TRIPLET;
        } else if (JMC.DOTTED_EIGHTH_NOTE == d) {
            return DOTTED_EIGHTH_NOTE;
        } else if (JMC.DOUBLE_DOTTED_EIGHTH_NOTE == d) {
            return DOUBLE_DOTTED_EIGHTH_NOTE;
        } else if (JMC.QUARTER_NOTE == d) {
            return QUARTER_NOTE;
        } else if (JMC.HALF_NOTE_TRIPLET == d) {
            return HALF_NOTE_TRIPLET;
        } else if (JMC.DOTTED_QUARTER_NOTE == d) {
            return DOTTED_QUARTER_NOTE;
        } else if (JMC.DOUBLE_DOTTED_QUARTER_NOTE == d) {
            return DOUBLE_DOTTED_QUARTER_NOTE;
        } else if (JMC.HALF_NOTE == d) {
            return HALF_NOTE;
        } else if (JMC.DOTTED_HALF_NOTE == d) {
            return DOTTED_HALF_NOTE;
        } else if (JMC.DOUBLE_DOTTED_HALF_NOTE == d) {
            return DOUBLE_DOTTED_HALF_NOTE;
        } else if (JMC.WHOLE_NOTE == d) {
            return WHOLE_NOTE;
        }
        throw new IllegalArgumentException("Illegal note rythm value:" + n.getRhythmValue() + " Note Was :" + n + " quarter is " + QUARTER_NOTE.getDuration());

    }

    public static NoteDurationEnum newNoteValue(int upperBoundIn32s, Random rand) {
        initializeNotesLessList();
        List<NoteDurationEnum> list = notesLessThanOrEqualTo.get(upperBoundIn32s);
        return list.get(rand.nextInt(list.size()));
    }

    public static NoteDurationEnum randomDuration(Random rand) {
        NoteDurationEnum[] vals = values();
        return vals[rand.nextInt(vals.length)];
    }

    public double getDuration() {
        switch (this) {
            case THIRTYSECOND_NOTE_TRIPLET:
                return JMC.THIRTYSECOND_NOTE_TRIPLET;
            case THIRTYSECOND_NOTE:
                return JMC.THIRTYSECOND_NOTE;
            case SIXTEENTH_NOTE_TRIPLET:
                return JMC.SIXTEENTH_NOTE_TRIPLET;
            case SIXTEENTH_NOTE:
                return JMC.SIXTEENTH_NOTE;
            case EIGHTH_NOTE_TRIPLET:
                return JMC.EIGHTH_NOTE_TRIPLET;
            case DOTTED_SIXTEENTH_NOTE:
                return JMC.DOTTED_SIXTEENTH_NOTE;
            case EIGHTH_NOTE:
                return JMC.EIGHTH_NOTE;
            case QUARTER_NOTE_TRIPLET:
                return JMC.QUARTER_NOTE_TRIPLET;
            case DOTTED_EIGHTH_NOTE:
                return JMC.DOTTED_EIGHTH_NOTE;
            case DOUBLE_DOTTED_EIGHTH_NOTE:
                return JMC.DOUBLE_DOTTED_EIGHTH_NOTE;
            case QUARTER_NOTE:
                return JMC.QUARTER_NOTE;
            case HALF_NOTE_TRIPLET:
                return JMC.HALF_NOTE_TRIPLET;
            case DOTTED_QUARTER_NOTE:
                return JMC.DOTTED_QUARTER_NOTE;
            case DOUBLE_DOTTED_QUARTER_NOTE:
                return JMC.DOUBLE_DOTTED_QUARTER_NOTE;
            case HALF_NOTE:
                return JMC.HALF_NOTE;
            case DOTTED_HALF_NOTE:
                return JMC.DOTTED_HALF_NOTE;
            case DOUBLE_DOTTED_HALF_NOTE:
                return JMC.DOUBLE_DOTTED_HALF_NOTE;
            case WHOLE_NOTE:
                return JMC.WHOLE_NOTE;
            default:
                throw new IllegalStateException("Cant happen : not a member of this enum: " + this);
        }
    }

    public double getExpectedFrequency() {
        switch (this) {
            case THIRTYSECOND_NOTE_TRIPLET:
                return 0.001;
            case THIRTYSECOND_NOTE:
                return 0.001;
            case SIXTEENTH_NOTE_TRIPLET:
                return 0.001;
            case SIXTEENTH_NOTE:
                return 0.001;
            case EIGHTH_NOTE_TRIPLET:
                return 0.001;
            case DOTTED_SIXTEENTH_NOTE:
                return .06;
            case EIGHTH_NOTE:
                return .25;
            case QUARTER_NOTE_TRIPLET:
                return 0.001;
            case DOTTED_EIGHTH_NOTE:
                return .06;
            case DOUBLE_DOTTED_EIGHTH_NOTE:
                return 0.001;
            case QUARTER_NOTE:
                return .46;
            case HALF_NOTE_TRIPLET:
                return 0.001;
            case DOTTED_QUARTER_NOTE:
                return .02;
            case DOUBLE_DOTTED_QUARTER_NOTE:
                return 0.001;
            case HALF_NOTE:
                return .1;
            case DOTTED_HALF_NOTE:
                return .01;
            case DOUBLE_DOTTED_HALF_NOTE:
                return 0.001;
            case WHOLE_NOTE:
                return .03;
            default:
                throw new IllegalStateException("Cant happen : not a member of this enum: " + this);
        }
    }

    public boolean isTriplet() {
        return (this == THIRTYSECOND_NOTE_TRIPLET || this == SIXTEENTH_NOTE_TRIPLET ||
                this == EIGHTH_NOTE_TRIPLET || this == QUARTER_NOTE_TRIPLET);
    }

    public int getThirtySeconds() {
        switch (this) {
            case THIRTYSECOND_NOTE_TRIPLET:
                return 1;
            case THIRTYSECOND_NOTE:
                return 1;
            case SIXTEENTH_NOTE_TRIPLET:
                return 2;
            case SIXTEENTH_NOTE:
                return 2;
            case EIGHTH_NOTE_TRIPLET:
                return 4;
            case DOTTED_SIXTEENTH_NOTE:
                return 3;
            case EIGHTH_NOTE:
                return 4;
            case QUARTER_NOTE_TRIPLET:
                return 8;
            case DOTTED_EIGHTH_NOTE:
                return 6;
            case DOUBLE_DOTTED_EIGHTH_NOTE:
                return 7;
            case QUARTER_NOTE:
                return 8;
            case HALF_NOTE_TRIPLET:
                return 16;
            case DOTTED_QUARTER_NOTE:
                return 12;
            case DOUBLE_DOTTED_QUARTER_NOTE:
                return 14;
            case HALF_NOTE:
                return 16;
            case DOTTED_HALF_NOTE:
                return 24;
            case DOUBLE_DOTTED_HALF_NOTE:
                return 28;
            case WHOLE_NOTE:
                return 32;
            default:
                throw new IllegalStateException("Cant happen : not a member of this enum: " + this);
        }
    }
}
