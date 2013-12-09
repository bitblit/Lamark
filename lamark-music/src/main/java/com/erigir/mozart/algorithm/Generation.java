package com.erigir.mozart.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jm.gui.cpn.Notate;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import com.erigir.mozart.NoteDurationEnum;

public class Generation
{
    public static void main(String[] a)
    {
        List<Integer> times = createMeasureTimes();
        Phrase ph = new Phrase();
        
        for (Integer i:times)
        {
            Note n = new Note();
            n.setPitch(60);
            System.out.println("i="+i+" dur="+NoteDurationEnum.fromThirtySeconds(i).getDuration());
            n.setRhythmValue(NoteDurationEnum.fromThirtySeconds(i).getDuration());
            ph.add(n);
        }
        
        Score s = new Score(new Part(ph));
        new Notate(s,0,100);
        
    }
    
    public static List<Integer> createMeasureTimes()
    {
        ArrayList<Node> times = new ArrayList<Node>();
        times.add(new Node(32,.8));
        Random rand = new Random();

        int idx = 0;
        
        while (idx<times.size())
        {
            //System.out.println("Iterating, i="+idx+" val="+times);
            Node n = times.get(idx);
            if (!n.visited)
            {
                if (n.value==1)
                {
                    n.visited=true;
                }
                else
                {
                    if (rand.nextDouble()<n.splitProb)
                    {
                        times.remove(idx);
                        times.add(idx, new Node(n.value/2,n.splitProb*.8));
                        times.add(idx, new Node(n.value/2,n.splitProb*.8));
                    }
                    else
                    {
                        n.visited=true;
                    }
                }
            }
            else
            {
                idx++;
            }
        }
        
        ArrayList<Integer> rval = new ArrayList<Integer>(times.size());
        for (Node n:times)
        {
            rval.add(n.value);
        }
        //System.out.println("Returning:"+rval);
        return rval;
    }
    
    
    
    static class Node
    {
        Integer value;
        boolean visited=false;
        Double splitProb;
        
        public Node(Integer value,Double splitProb)
        {
            super();
            this.value = value;
            this.splitProb = splitProb;
        }
        public String toString()
        {
            return "["+value+","+splitProb+"]";
        }
    }
}
