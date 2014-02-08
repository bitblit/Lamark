package com.erigir.mozart;

import jm.music.data.Note;

import java.util.List;

/**
 * All values in eighths
 *
 * @author cweiss
 */

public enum TimeSignatureEnum {
    ONE_EIGHT, ONE_FOUR, THREE_EIGHT, ONE_TWO, FIVE_EIGHT, THREE_FOUR, SEVEN_EIGHT, FOUR_FOUR, NINE_EIGHT, FIVE_FOUR, THREE_TWO, SEVEN_FOUR, EIGHT_FOUR, NINE_FOUR;

    public int numerator() {
        switch (this) {
            case ONE_EIGHT:
                return 1;// 1/8
            case ONE_FOUR:
                return 1;// 1/4
            case THREE_EIGHT:
                return 3;// 3/8
            case ONE_TWO:
                return 1;// 1/2
            case FIVE_EIGHT:
                return 5;// 5/8
            case THREE_FOUR:
                return 3;// 3/4
            case SEVEN_EIGHT:
                return 7; // 7/8
            case FOUR_FOUR:
                return 4;// 4/4
            case NINE_EIGHT:
                return 9;// 9/8
            case FIVE_FOUR:
                return 5;// 5/4
            case THREE_TWO:
                return 3;// 3/2
            case SEVEN_FOUR:
                return 7;// 7/4
            case EIGHT_FOUR:
                return 8;// 8/4
            case NINE_FOUR:
                return 9;// 9/4
            default:
                throw new IllegalStateException("Cant happen: value not in enum: " + this);
        }
    }

    public int denominator() {
        switch (this) {
            case ONE_EIGHT:
                return 8;// 1/8
            case ONE_FOUR:
                return 4;// 1/4
            case THREE_EIGHT:
                return 8;// 3/8
            case ONE_TWO:
                return 2;// 1/2
            case FIVE_EIGHT:
                return 8;// 5/8
            case THREE_FOUR:
                return 4;// 3/4
            case SEVEN_EIGHT:
                return 8; // 7/8
            case FOUR_FOUR:
                return 4;// 4/4
            case NINE_EIGHT:
                return 8;// 9/8
            case FIVE_FOUR:
                return 4;// 5/4
            case THREE_TWO:
                return 2;// 3/2
            case SEVEN_FOUR:
                return 4;// 7/4
            case EIGHT_FOUR:
                return 4;// 8/4
            case NINE_FOUR:
                return 4;// 9/4
            default:
                throw new IllegalStateException("Cant happen: value not in enum: " + this);
        }
    }

    public int barValueInThirtySeconds() {
        switch (this) {
            case ONE_EIGHT:
                return 4;
            case ONE_FOUR:
                return 8;
            case THREE_EIGHT:
                return 12;
            case ONE_TWO:
                return 16;
            case FIVE_EIGHT:
                return 20;
            case THREE_FOUR:
                return 24;
            case SEVEN_EIGHT:
                return 28;
            case FOUR_FOUR:
                return 32;
            case NINE_EIGHT:
                return 36;
            case FIVE_FOUR:
                return 40;
            case THREE_TWO:
                return 48;
            case SEVEN_FOUR:
                return 56;
            case EIGHT_FOUR:
                return 64;
            case NINE_FOUR:
                return 72;
            default:
                throw new IllegalStateException("Cant happen: value not in enum: " + this);
        }
    }

    public double percentInTime(List<Note> p) {
        int valid = 0;
        int runningCount = 0;
        int value = barValueInThirtySeconds();
        boolean broke = false;
        int noteVal;
        for (int i = 0; i < p.size() && !broke; i++) {
            noteVal = NoteDurationEnum.valueFromNote(p.get(i)).getThirtySeconds();
            runningCount += noteVal;
            if (runningCount == value) {
                valid = i; // Good this far
                runningCount = 0;
            } else if (runningCount > value) // broken
            {
                broke = true;
            }
        }
        if (!broke) {
            valid = p.size(); // all notes valid
        }
        return (double) valid / (double) p.size();
    }

}
