package com.erigir.lamark.music;

import com.erigir.lamark.AbstractLamarkComponent;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.List;
import java.util.function.Function;

public class MozartCrossover  extends AbstractLamarkComponent implements Function<List<Score>, Score> {

    @Override
    public Score apply(List<Score> scores) {
        Score rval = null;

        Score s1 = scores.get(0);
        Score s2 = scores.get(1);

        Part part1 = s1.getPart(0);
        Part part2 = s2.getPart(0);

        Phrase[] ph1a = part1.getPhraseArray(); // one phrase per bar
        Phrase[] ph2a = part2.getPhraseArray();

        if (ph1a.length != ph2a.length) {
            throw new IllegalStateException("Scores have different number of bars, 1=" + ph1a.length + " 2=" + ph2a.length);
        }

        int split1 = rand().nextInt(ph1a.length - 1);
        int split2 = (split1 + 1) + rand().nextInt(ph1a.length - (split1 + 1));

        Phrase[] np1 = new Phrase[ph1a.length];
        Phrase[] np2 = new Phrase[ph1a.length];

        for (int i = 0; i < split1; i++) {
            np1[i] = ph1a[i];
            np2[i] = ph2a[i];
        }
        for (int i = split1; i < split2; i++) {
            np1[i] = ph2a[i];
            np2[i] = ph1a[i];
        }
        for (int i = split2; i < ph1a.length; i++) {
            np1[i] = ph1a[i];
            np2[i] = ph2a[i];
        }

        Score new1 = new Score();
        Score new2 = new Score();
        Part newPart1 = new Part(np1);
        Part newPart2 = new Part(np2);
        new1.add(newPart1);
        new2.add(newPart2);

        return rval;
    }


}
