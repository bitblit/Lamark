package com.erigir.mozart;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.erigir.mozart.phrase.PhrasePool;
import com.erigir.mozart.traits.TraitWrapper;

import jm.gui.cpn.Notate;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.data.Tempo;
import com.erigir.lamark.Individual;
import com.erigir.lamark.Util;
import com.erigir.lamark.configure.Const;
import com.erigir.lamark.configure.LamarkConfig;
import com.erigir.lamark.configure.PropertyDescriptor;

public class RandomizedSearch implements Runnable, ActionListener
{

        private JFrame frame;
        private JButton start;
        private JButton stop;
        private musicThread thread=null;
        
        /**
         * Create the GUI and show it.  For thread safety,
         * this method should be invoked from the
         * event-dispatching thread.
         */
        private void createAndShowGUI() {
            //Make sure we have nice window decorations.
            JFrame.setDefaultLookAndFeelDecorated(true);

            //Create and set up the window.
            frame = new JFrame("Randomized Search");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Create and set up the content pane.
            frame.getContentPane().setLayout(new GridLayout(4,0));
            
            start = new JButton("Start Process");
            stop = new JButton("Stop Process");
            start.addActionListener(this);
            stop.addActionListener(this);
            start.setEnabled(true);
            stop.setEnabled(false);
            frame.getContentPane().add(start);
            frame.getContentPane().add(stop);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource()==start)
            {
                thread = new musicThread();
                new Thread(thread).start();
                start.setEnabled(false);
                stop.setEnabled(true);
            }
            else if (e.getSource()==stop)
            {
                thread.abort();
                start.setEnabled(true);
                stop.setEnabled(false);
            }
        }
        
        public static void main(String[] args)
        {
            SwingUtilities.invokeLater(new RandomizedSearch());
        }

        public void run()
        {
            createAndShowGUI();
        }
        
        public String displayAnalysis(ScoreAnalysis sa, Double fitness)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Fitness : ");
            sb.append(Util.format(fitness));
            sb.append("\nScore size:");
            sb.append(sa.getAllNotes().size());
            sb.append("\nClosest Scale:");
            sb.append(sa.closestScaleFit());
            sb.append("\nPercent in Closest Scale:");
            sb.append(Util.format(sa.percentInClosestScale()));
            sb.append("\nClosest Signature:");
            sb.append(sa.closestTimeSignatureFit());
            sb.append("\nPercent in Closest Signature:");
            sb.append(Util.format(sa.percentInClosestTimeSignature()));
            sb.append("\nMedian Note:");
            sb.append(sa.getMedianNote());
            sb.append("\nMean Note:");
            sb.append(Util.format(sa.getMeanNote()));
            sb.append("\nNote Std Dev:");
            sb.append(Util.format(sa.getNoteStandardDeviation()));
            sb.append("\nNote Direction Changes:");
            sb.append(sa.getNoteDirectionChanges());
            return sb.toString();
        }

        
        static class musicThread implements Runnable
        {
            private boolean running = true;
            private static final int SONG_LENGTH=8;
            private static final double SCORE_DELTA=5;
            private static final int MOVE_RANGE=1;
            private int cycle = 0;
            private MozartFitness fitness;
            private LamarkConfig config;
            private Score currentScore;
            private int currentFitness;
            private int lastUpdate;
            private Notate musicFrame;
            private int cyclesWithoutChange=0;
            
            public musicThread()
            {
                config = new LamarkConfig();
                PropertyDescriptor size = config.descriptor(Const.INDIVIDUAL_SIZE_KEY);
                size.value = new BigDecimal(SONG_LENGTH);
                fitness = new ScoreFitness();
                fitness.setLamarkConfig(config);
                currentScore = randomScore();
                currentFitness = fitness(currentScore);
                lastUpdate=0;
            }
            
            public void run()
            {
            	try
            	{
            		PrintWriter pw = new PrintWriter(new FileWriter("test.csv"));
            		pw.print("cycle,");
            		for (TraitWrapper tw:fitness.getTraits())
            		{
            			pw.print(tw.getTrait().getClass().getSimpleName());
            			pw.print(",");
            		}
            		pw.print("\n");
                while (running)
                {
                    cyclesWithoutChange++;
                    Score[] neighbors = getNeighbors(currentScore);
                    for (int i=0;i<neighbors.length;i++)
                    {
                        int fit = fitness(neighbors[i]);
                        if (fit>=currentFitness)
                        {
                    		pw.print(cycle);
                    		pw.print(",");
                    		for (TraitWrapper tw:fitness.getTraits())
                    		{
                    			pw.print(tw.getTrait().getFitness());
                        		pw.print(",");
                    		}
                    		pw.print("\n");
                        	
                            currentFitness = fit;
                            currentScore = neighbors[i];
                            cyclesWithoutChange=0;
                        }
                    }
                    // If big enough jump, redisplay
                    if ((currentFitness-lastUpdate)>SCORE_DELTA)
                    {
                        updateScreen();
                    }
                    
                    if (cyclesWithoutChange==200)
                    {
                    	System.out.print("200 without change");
                        currentFitness = fitness(currentScore);
                        updateScreen();
                        cyclesWithoutChange=0;
                    }
                    
                    cycle++;
                    
                }
                pw.flush();
                pw.close();
            	}
            	catch (Exception e)
            	{
            		e.printStackTrace();
            	}
            }
            private void updateScreen()
            {
                if (null==musicFrame)
                {
                	musicFrame = new Notate(collapsePhrase(currentScore),0,100);
                }
                else
                {
                	musicFrame.setNewScore(collapsePhrase(currentScore));
                }
                musicFrame.setTitle("Cycle:"+cycle+"  Fitness:"+Util.format(currentFitness));
                lastUpdate = currentFitness;
            }
            public Score collapsePhrase(Score s)
            {
            	Phrase[] song = s.getPart(0).getPhraseArray();
            	Phrase newSong = new Phrase();
            	for (Phrase ph:song)
            	{
            		for (Note n:ph.getNoteArray())
            		{
            			newSong.add(n);
            		}
            	}
            	return new Score(new Part(newSong));
            }
            
            public void abort()
            {
                running = false;
            }
            
            private Score[] getNeighbors(Score s)
            {
                Score[] rval = new Score[7];

                Phrase[] data = s.getPart(0).getPhraseArray();
                // Initialzie
                for (int i=0;i<7;i++)
                {
                	rval[i]=new Score(new Part(copyPhraseArray(data)));
                }
                // Pick a bar
                int barIdx = (cycle%data.length);
                //int barIdx = Util.RAND.nextInt(data.length);
                
                // Expand
                expand(rval[0],barIdx);
                // Flatten
                flatten(rval[1],barIdx);
                // Move Up
                moveUp(rval[2],barIdx);
                // Move Down
                moveDown(rval[3],barIdx);
                // Replace
                replace(rval[4],barIdx);
                // Flatten outlier
                flatGreatestOutlier(rval[5],barIdx);
                combine(rval[6],barIdx);
                
                return rval;
            }
            
            private void expand(Score s,int barIdx)
            {
            	Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
            	Note[] notes = ph.getNoteArray();
            	int sumPitch=0;
            	for (Note n:notes)
            	{
            		sumPitch+=n.getPitch();
            	}
            	int avPitch = sumPitch/notes.length;
            	for (Note n:notes)
            	{
            		if (n.getPitch()>avPitch)
            		{
            			n.setPitch(n.getPitch()-1);
            		}
            		if (n.getPitch()<avPitch)
            		{
            			n.setPitch(n.getPitch()+1);
            		}
            	}
            }

            private void combine(Score s,int barIdx)
            {
            	Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
            	Note[] notes = ph.getNoteArray();
            	NoteDurationEnum[] ndea = NoteDurationEnum.values();
            	boolean done = false;
            	for (int i=0;i<notes.length-1 && !done;i++)
            	{
            		NoteDurationEnum nd = NoteDurationEnum.valueFromNote(notes[i]);
            		NoteDurationEnum nd1 = NoteDurationEnum.valueFromNote(notes[i+1]);

            		int tts = nd.getThirtySeconds()+nd1.getThirtySeconds();
            		
            		for (int j=0;j<ndea.length && !done;j++)
            		{
            			if (tts == ndea[j].getThirtySeconds())
            			{
            				notes[i].setRhythmValue(ndea[j].getDuration());
            				ph.removeNote(i+1);
            				done=true;
            			}
            		}
            	}
            }

            private void flatten(Score s,int barIdx)
            {
            	Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
            	Note[] notes = ph.getNoteArray();
            	int sumPitch=0;
            	for (Note n:notes)
            	{
            		sumPitch+=n.getPitch();
            	}
            	int avPitch = sumPitch/notes.length;
            	for (Note n:notes)
            	{
            		if (n.getPitch()>avPitch)
            		{
            			n.setPitch(n.getPitch()+1);
            		}
            		if (n.getPitch()<avPitch)
            		{
            			n.setPitch(n.getPitch()-1);
            		}
            	}
            	
            }
            private void flatGreatestOutlier(Score s,int barIdx)
            {
                Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
                Note[] notes = ph.getNoteArray();
                int sumPitch=0;
                int maxNoteIdx=-1,maxNoteValue=-1;
                int minNoteIdx=-1,minNoteValue=250;
                for (int i=0;i<notes.length;i++)
                {
                    Note n = notes[i];
                    sumPitch+=n.getPitch();
                    if (n.getPitch()>maxNoteValue)
                    {
                        maxNoteValue = n.getPitch();
                        maxNoteIdx = i;
                    }
                    if (n.getPitch()<minNoteValue)
                    {
                        minNoteValue = n.getPitch();
                        minNoteIdx = i;
                    }
                }
                int avPitch = sumPitch/notes.length;
                int idx = -1;
                if (Math.abs(avPitch-maxNoteValue)>Math.abs(avPitch-minNoteValue))
                {
                    idx=maxNoteIdx;
                }
                else
                {
                    idx=minNoteIdx;
                }
                
                notes[idx].setPitch(avPitch);
            }
            private void moveUp(Score s,int barIdx)
            {
            	Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
            	for (Note n:ph.getNoteArray())
            	{
            		if (n.getPitch()<127)
            		{
            			n.setPitch(n.getPitch()+1);
            		}
            	}
            }
            private void moveDown(Score s,int barIdx)
            {
            	Phrase ph = s.getPart(0).getPhraseArray()[barIdx];
            	for (Note n:ph.getNoteArray())
            	{
            		if (n.getPitch()>0)
            		{
            			n.setPitch(n.getPitch()-1);
            		}
            	}
            }
            private void replace(Score s,int barIdx)
            {
            	Part part = s.getPart(0);
            	Phrase[] ph = part.getPhraseArray();
            	part.removeAllPhrases();
            	
            	for (int i=0;i<ph.length;i++)
            	{
            		if (i!=barIdx)
            		{
            			part.add(ph[i]);
            		}
            		else
            		{
            			part.add(PhrasePool.instance.getPhrase());
            		}
            	}
            }
            
            private Phrase copyPhrase(Phrase p)
            {
            	Phrase rval = new Phrase();
            	for (Note n : p.getNoteArray())
            	{
            		rval.add(n);
            	}
            	return rval;
            }
            private Phrase[] copyPhraseArray(Phrase[] p)
            {
            	Phrase[] rval = new Phrase[p.length];
            	for (int i=0;i<rval.length;i++)
            	{
            		rval[i]=copyPhrase(p[i]);
            	}
            	return rval;
            }
            
            private Score randomScore()
            {
                PhrasePool pp = PhrasePool.instance;
                ScaleEnum scale = ScaleEnum.C;
                TimeSignatureEnum signature = TimeSignatureEnum.FOUR_FOUR;
                
                pp.initialize(signature,scale,40,81);
                
                Phrase[] song = new Phrase[SONG_LENGTH];
                for (int i=0;i<song.length;i++)
                {
                	song[i]=pp.getPhrase();
                }
                
                Score rval = new Score(new Part(song));
                rval.setTempo(Tempo.ANDANTE);
                rval.setDenominator(signature.denominator());
                rval.setNumerator(signature.numerator());
                rval.setKeySignature(scale.sharpOrFlatCount());
                
                return rval;
            }
            
            private int fitness(Score s)
            {
                Individual i = new Individual(s);
                return (int)Math.floor(fitness.fitnessValue(i));
            }
        
        }
}
