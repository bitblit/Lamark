package com.erigir.lamark.music;

import jm.music.data.Note;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public enum ScaleEnum {
    A(3),
    As(-2),
    B(5),
    C(0),
    Cs(7),
    D(2),
    Ds(-3),
    E(4),
    F(-1),
    Fs(6),
    G(1),
    Gs(-4);

    private int sharpOrFlatCount;

    ScaleEnum(int sharpOrFlatCount)
    {
        this.sharpOrFlatCount = sharpOrFlatCount;
    }

    private SortedSet<Integer> cacheScale;
    private SortedSet<Integer> cacheBigFiveScale;

    public int getSharpOrFlatCount() {
        return sharpOrFlatCount;
    }

    public static ScaleEnum fromSharpOrFlatCount(int sharpOrFlatCount)
    {
        ScaleEnum rval = null;
        for (ScaleEnum e:values())
        {
            if (e.sharpOrFlatCount==sharpOrFlatCount)
            {
                rval = e;
            }
        }
        return rval;
    }

    public SortedSet<Integer> scale() {
        if (null == cacheScale) {
            switch (this) {
                case C:
                    cacheScale = scaleFromOffset(0);
                    break;
                case Cs:
                    cacheScale = scaleFromOffset(1);
                    break;
                case D:
                    cacheScale = scaleFromOffset(2);
                    break;
                case Ds:
                    cacheScale = scaleFromOffset(3);
                    break;
                case E:
                    cacheScale = scaleFromOffset(4);
                    break;
                case F:
                    cacheScale = scaleFromOffset(5);
                    break;
                case Fs:
                    cacheScale = scaleFromOffset(6);
                    break;
                case G:
                    cacheScale = scaleFromOffset(7);
                    break;
                case Gs:
                    cacheScale = scaleFromOffset(8);
                    break;
                case A:
                    cacheScale = scaleFromOffset(9);
                    break;
                case As:
                    cacheScale = scaleFromOffset(10);
                    break;
                case B:
                    cacheScale = scaleFromOffset(11);
                    break;
                default:
                    throw new IllegalStateException("Cant happen : Unknown item :" + this);
            }
        }
        return Collections.unmodifiableSortedSet(cacheScale);
    }

    public SortedSet<Integer> bigFiveScale() {
        if (null == cacheBigFiveScale) {
            switch (this) {
                case C:
                    cacheBigFiveScale = bigFiveScaleFromOffset(0);
                    break;
                case Cs:
                    cacheBigFiveScale = bigFiveScaleFromOffset(1);
                    break;
                case D:
                    cacheBigFiveScale = bigFiveScaleFromOffset(2);
                    break;
                case Ds:
                    cacheBigFiveScale = bigFiveScaleFromOffset(3);
                    break;
                case E:
                    cacheBigFiveScale = bigFiveScaleFromOffset(4);
                    break;
                case F:
                    cacheBigFiveScale = bigFiveScaleFromOffset(5);
                    break;
                case Fs:
                    cacheBigFiveScale = bigFiveScaleFromOffset(6);
                    break;
                case G:
                    cacheBigFiveScale = bigFiveScaleFromOffset(7);
                    break;
                case Gs:
                    cacheBigFiveScale = bigFiveScaleFromOffset(8);
                    break;
                case A:
                    cacheBigFiveScale = bigFiveScaleFromOffset(9);
                    break;
                case As:
                    cacheBigFiveScale = bigFiveScaleFromOffset(10);
                    break;
                case B:
                    cacheBigFiveScale = bigFiveScaleFromOffset(11);
                    break;
                default:
                    throw new IllegalStateException("Cant happen : Unknown item :" + this);
            }
        }
        return Collections.unmodifiableSortedSet(cacheBigFiveScale);
    }

    public int middleKeyValue() {
        switch (this) {
            case C:
                return 60;
            case Cs:
                return 61;
            case D:
                return 62;
            case Ds:
                return 63;
            case E:
                return 64;
            case F:
                return 65;
            case Fs:
                return 66;
            case G:
                return 67;
            case Gs:
                return 68;
            case A:
                return 69;
            case As:
                return 70;
            case B:
                return 71;
            default:
                throw new IllegalStateException("Cant happen : Unknown item :" + this);
        }
    }

    public double percentInScale(Collection<Note> p) {
        int out = 0;
        Set<Integer> scale = scale();

        for (Note n : p) {
            if (!scale.contains(n.getPitch())) {
                out++;
            }
        }
        return (double) (p.size() - out) / (double) p.size();
    }

    public double percentInScaleBigFive(Collection<Note> p) {
        int out = 0;
        Set<Integer> scale = bigFiveScale();

        for (Note n : p) {
            if (!scale.contains(n.getPitch())) {
                out++;
            }
        }
        return (double) (p.size() - out) / (double) p.size();
    }

    private SortedSet<Integer> scaleFromOffset(int offset) {
        SortedSet<Integer> rval = new TreeSet<Integer>();
        int[] scaleAdjust = new int[]{2, 2, 1, 2, 2, 2, 1};

        int current = offset;
        while (current < 128) {
            rval.add(current);
            for (int i = 0; i < scaleAdjust.length; i++) {
                current += scaleAdjust[i];
                if (current < 128) {
                    rval.add(current);
                }
            }
        }
        return rval;
    }

    private SortedSet<Integer> bigFiveScaleFromOffset(int offset) {
        SortedSet<Integer> rval = new TreeSet<Integer>();
        int[] scaleAdjust = new int[]{2, 2, 3, 2, 3};

        int current = offset;
        while (current < 128) {
            rval.add(current);
            for (int i = 0; i < scaleAdjust.length; i++) {
                current += scaleAdjust[i];
                if (current < 128) {
                    rval.add(current);
                }
            }
        }
        return rval;
    }

}
