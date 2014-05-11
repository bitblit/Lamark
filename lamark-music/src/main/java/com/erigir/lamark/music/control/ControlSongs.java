package com.erigir.lamark.music.control;

import com.erigir.lamark.music.NoteDurationEnum;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.data.Tempo;

public enum ControlSongs {
    MARY_HAD_A_LITTLE_LAMB, NEW_WORLD_SYMPHONY, MINUET_IN_G, TWINKLE_TWINKLE, PACHABEL_CANON_IN_D;//GRAND_STAFF_SCALE, 

    public Score getSong() {
        switch (this) {
            case MARY_HAD_A_LITTLE_LAMB:
                return maryHadALittleLamb();
            //case GRAND_STAFF_SCALE : return grandStaffScale();
            case NEW_WORLD_SYMPHONY:
                return newWorldSymphony();
            case MINUET_IN_G:
                return minuetInG();
            case TWINKLE_TWINKLE:
                return twinkleTwinkle();
            case PACHABEL_CANON_IN_D:
                return pachabelCanonInD();
            default:
                throw new IllegalStateException("Cant happen : Unknown item :" + this);
        }
    }

    public static Score[] getSongs() {
        ControlSongs[] songs = ControlSongs.values();
        Score[] rval = new Score[songs.length];
        for (int i = 0; i < songs.length; i++) {
            rval[i] = songs[i].getSong();
        }
        return rval;
    }

    public int getLength() {
        return getSong().getPart(0).getPhrase(0).getSize();
    }

    private Score newWorldSymphony() {
        Phrase p = new Phrase();

        p.addNote(new Note(65, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(68, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(68, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        p.addNote(new Note(64, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(68, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.HALF_NOTE.getDuration()));

        p.addNote(new Note(65, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(68, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(68, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.DOTTED_EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.SIXTEENTH_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        p.addNote(new Note(64, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.HALF_NOTE.getDuration()));

        Score s = new Score(new Part(p));
        s.setKeySignature(-5);
        s.setNumerator(4);
        s.setDenominator(4);
        s.setTempo(Tempo.ANDANTE);
        s.setTitle("NEW WORLD SYMPHONY");
        return s;
    }

    private Score minuetInG() {
        Phrase p = new Phrase();

        p.addNote(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(71, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(72, NoteDurationEnum.EIGHTH_NOTE.getDuration()));

        p.addNote(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        p.addNote(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(72, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(74, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(76, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(78, NoteDurationEnum.EIGHTH_NOTE.getDuration()));

        p.addNote(new Note(79, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        p.addNote(new Note(72, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(74, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(72, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(71, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.EIGHTH_NOTE.getDuration()));

        p.addNote(new Note(71, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(72, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(71, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.EIGHTH_NOTE.getDuration()));

        p.addNote(new Note(69, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(71, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.EIGHTH_NOTE.getDuration()));
        p.addNote(new Note(66, NoteDurationEnum.EIGHTH_NOTE.getDuration()));

        p.addNote(new Note(67, NoteDurationEnum.DOTTED_HALF_NOTE.getDuration()));

        Score s = new Score(new Part(p));
        s.setKeySignature(1);
        s.setNumerator(3);
        s.setDenominator(4);
        s.setTempo(Tempo.ANDANTE);
        s.setTitle("MINUET IN G");
        return s;
    }

    private Score twinkleTwinkle() {
        Phrase p = new Phrase();

        // tt
        p.addNote(new Note(60, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(60, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        //ls
        p.addNote(new Note(69, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.HALF_NOTE.getDuration()));

        //hiw
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        //wya
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(60, NoteDurationEnum.HALF_NOTE.getDuration()));

        // uat
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        // wsh
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.HALF_NOTE.getDuration()));

        // lad
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        // its
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.HALF_NOTE.getDuration()));

        // tt
        p.addNote(new Note(60, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(60, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        //ls
        p.addNote(new Note(69, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(67, NoteDurationEnum.HALF_NOTE.getDuration()));

        //hiw
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(65, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(64, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        //wya
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(62, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        p.addNote(new Note(60, NoteDurationEnum.HALF_NOTE.getDuration()));

        Score s = new Score(new Part(p));
        s.setKeySignature(0);
        s.setNumerator(4);
        s.setDenominator(4);
        s.setTempo(Tempo.ANDANTE);
        s.setTitle("TWINKLE TWINKLE");

        return s;
    }

    private Score pachabelCanonInD() {
        Phrase p = new Phrase();

        p.addNote(new Note(78, NoteDurationEnum.HALF_NOTE.getDuration()));
        p.addNote(new Note(76, NoteDurationEnum.HALF_NOTE.getDuration()));

        p.addNote(new Note(74, NoteDurationEnum.HALF_NOTE.getDuration()));
        p.addNote(new Note(73, NoteDurationEnum.HALF_NOTE.getDuration()));

        p.addNote(new Note(71, NoteDurationEnum.HALF_NOTE.getDuration()));
        p.addNote(new Note(69, NoteDurationEnum.HALF_NOTE.getDuration()));

        p.addNote(new Note(71, NoteDurationEnum.HALF_NOTE.getDuration()));
        p.addNote(new Note(73, NoteDurationEnum.HALF_NOTE.getDuration()));

        Score s = new Score(new Part(p));
        s.setKeySignature(2);
        s.setNumerator(4);
        s.setDenominator(4);
        s.setTempo(Tempo.ANDANTE);
        s.setTitle("CANON IN D");

        return s;
    }
    /*
    private Score grandStaffScale()
    {
        Score s = new Score();
        s.setDenominator(4);
        s.setNumerator(4);
        s.setKeySignature(0);
        s.setTempo(Tempo.ANDANTE);
        Part p = new Part();
        Phrase ph = new Phrase();
        s.add(p);
        p.add(ph);
        
        for (int i=40;i<81;i++)
        {
            ph.add(new Note(i,NoteDurationEnum.QUARTER_NOTE.getDuration()));
        }
        return s;
    }*/

    private Score maryHadALittleLamb() {
        Score s = new Score();
        s.setDenominator(4);
        s.setNumerator(4);
        s.setKeySignature(0);
        s.setTempo(Tempo.ANDANTE);
        Part p = new Part();
        Phrase ph = new Phrase();
        s.add(p);
        p.add(ph);

        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(72, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.HALF_NOTE.getDuration()));

        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.HALF_NOTE.getDuration()));

        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(79, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(79, NoteDurationEnum.HALF_NOTE.getDuration()));

        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(72, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(76, NoteDurationEnum.QUARTER_NOTE.getDuration()));
        ph.add(new Note(74, NoteDurationEnum.QUARTER_NOTE.getDuration()));

        ph.add(new Note(72, NoteDurationEnum.WHOLE_NOTE.getDuration()));

        s.setTitle("MARY HAD A LITTLE LAMB");

        return s;
    }

}
