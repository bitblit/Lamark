package com.erigir.lamark.music.control;

import com.erigir.lamark.Individual;
import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.music.ScaleEnum;
import com.erigir.lamark.music.ScoreAnalysis;
import com.erigir.lamark.music.ScoreFitness;
import com.erigir.lamark.music.TimeSignatureEnum;
import jm.gui.cpn.Notate;
import jm.music.data.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayControl implements Runnable, ActionListener {
    private JFrame frame;
    private ControlSongs song;
    private JLabel currentSong;
    private JButton choose;
    private JButton show;
    private JButton analyze;
    private JButton timeAnalyze;
    private JButton noteAnalyze;
    private ScoreFitness fitness = new ScoreFitness();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new DisplayControl());
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("Control Music");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        frame.getContentPane().setLayout(new GridLayout(4, 0));

        currentSong = new JLabel("Current Song: NONE");
        frame.getContentPane().add(currentSong);
        choose = new JButton("Change song");
        show = new JButton("Show song");
        analyze = new JButton("Analyze song");
        timeAnalyze = new JButton("Analyze song's timing");
        noteAnalyze = new JButton("Analyze song's notes");
        choose.addActionListener(this);
        show.addActionListener(this);
        show.setEnabled(false);
        analyze.addActionListener(this);
        analyze.setEnabled(false);
        timeAnalyze.addActionListener(this);
        timeAnalyze.setEnabled(false);
        noteAnalyze.addActionListener(this);
        noteAnalyze.setEnabled(false);
        frame.getContentPane().add(choose);
        frame.getContentPane().add(show);
        frame.getContentPane().add(analyze);
        frame.getContentPane().add(timeAnalyze);
        frame.getContentPane().add(noteAnalyze);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == choose) {
            song = ((ControlSongs) JOptionPane.showInputDialog(
                    frame,
                    "Please select an control song:",
                    "Song Chooser",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    ControlSongs.values(),
                    song));
            currentSong.setText("Current song:" + song);
            analyze.setEnabled(song != null);
            show.setEnabled(song != null);
            noteAnalyze.setEnabled(song != null);
            timeAnalyze.setEnabled(song != null);

        } else if (e.getSource() == show) {
            Frame scoreFrame = new Notate(song.getSong(), 0, 100);
            scoreFrame.setTitle("Song:" + song.name());
        } else if (e.getSource() == analyze) {
            Score score = song.getSong();
            // TODO: fix time sig lookup here
            //Lamark lamark = Mozart.mozartInstance(ScaleEnum.fromSharpOrFlatCount(score.getKeySignature()).toString(),TimeSignatureEnum.FOUR_FOUR.toString(), false, song.getLength());
            Individual<Score> i = new Individual<Score>();
            i.setGenome(song.getSong());

            double fit = fitness.applyAsDouble(i.getGenome());
            double maxFit = fitness.maximumScore();
            double percent = (fit / maxFit) * 100;
            JOptionPane.showMessageDialog(frame, "Fitness:" + LamarkUtil.format(fit) + " out of " + LamarkUtil.format(maxFit) + " (" + LamarkUtil.format(percent) + "%)\n\n" + i.getAttribute("SCORES").toString());
        } else if (e.getSource() == timeAnalyze) {
            JOptionPane.showMessageDialog(frame, timeAnalyze(song.getSong()));
        } else if (e.getSource() == noteAnalyze) {
            JOptionPane.showMessageDialog(frame, noteAnalyze(song.getSong()));
        }
    }

    public void run() {
        createAndShowGUI();
    }

    public String displayAnalysis(ScoreAnalysis sa, Double fitness) {
        StringBuffer sb = new StringBuffer();
        sb.append("Fitness : ");
        sb.append(LamarkUtil.format(fitness));
        sb.append("\nScore size:");
        sb.append(sa.getAllNotes().size());
        sb.append("\nClosest Scale:");
        sb.append(sa.closestScaleFit());
        sb.append("\nPercent in Closest Scale:");
        sb.append(LamarkUtil.format(sa.percentInClosestScale()));
        sb.append("\nClosest Signature:");
        sb.append(sa.closestTimeSignatureFit());
        sb.append("\nPercent in Closest Signature:");
        sb.append(LamarkUtil.format(sa.percentInClosestTimeSignature()));
        sb.append("\nMedian Note:");
        sb.append(sa.getMedianNote());
        sb.append("\nMean Note:");
        sb.append(LamarkUtil.format(sa.getMeanNote()));
        sb.append("\nNote Std Dev:");
        sb.append(LamarkUtil.format(sa.getNoteStandardDeviation()));
        sb.append("\nNote Direction Changes:");
        sb.append(sa.getNoteDirectionChanges());
        return sb.toString();
    }

    public String timeAnalyze(Score s) {
        StringBuffer sb = new StringBuffer();
        sb.append("Time Analysis\n");
        TimeSignatureEnum[] sigs = TimeSignatureEnum.values();
        for (int i = 0; i < sigs.length; i++) {
            sb.append(sigs[i].name() + " EIGHTHS = ");
            sb.append(LamarkUtil.format(sigs[i].percentInTime(ScoreAnalysis.partToNoteList(s.getPart(0)))));
            sb.append("\n");
        }
        return sb.toString();
    }

    public String noteAnalyze(Score s) {
        StringBuffer sb = new StringBuffer();
        sb.append("Note Analysis\n");
        ScaleEnum[] scales = ScaleEnum.values();
        for (int i = 0; i < scales.length; i++) {
            sb.append(scales[i].name() + " = ");
            sb.append(LamarkUtil.format(scales[i].percentInScale(ScoreAnalysis.partToNoteList(s.getPart(0)))));
            sb.append("\n");
        }
        return sb.toString();
    }
}
