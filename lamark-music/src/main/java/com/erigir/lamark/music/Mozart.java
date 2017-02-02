package com.erigir.lamark.music;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkBuilder;
import com.erigir.lamark.LamarkBuilderSerializer;
import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.events.*;
import com.erigir.lamark.music.phrase.PhrasePool;
import jm.music.data.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Mozart implements ActionListener, LamarkEventListener, Runnable {
    private static Logger LOG = Logger.getLogger(Mozart.class.getName());
    private JFrame frame = null;
    private JPanel mainPanel;
    private JButton start;
    private JButton cancel;
    private JTextArea output;
    private JLabel currentRuntime;
    private JLabel timeRemaining;
    private JLabel generationNumber;
    private JLabel bestScore;
    private Lamark currentRunner;

    private MyNotate notate;

    private JComboBox keySelect;
    private JComboBox signatureSelect;
    private JComboBox rangeSelect;
    private JTextField barCountEntry;


    public Mozart() {
    }

    public static void main(String[] args) {
        Mozart instance = new Mozart();
        SwingUtilities.invokeLater(instance);
    }

    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.severe("Couldnt get system look and feel:" + e);
        }
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("Mozart - v1.0.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.getContentPane().add(tabbedPane);
        frame.getContentPane().setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        start = new JButton("Start");
        start.setEnabled(true);
        start.addActionListener(this);
        cancel = new JButton("Cancel");
        cancel.setEnabled(false);
        cancel.addActionListener(this);

        JToolBar infoBar = new JToolBar();

        currentRuntime = new JLabel("Runtime: n/a");
        timeRemaining = new JLabel("Remaining: n/a");
        generationNumber = new JLabel("Generation: n/a");
        bestScore = new JLabel("Best: n/a");

        toolbar.add(start);
        toolbar.add(cancel);
        toolbar.setFloatable(false);

        infoBar.add(generationNumber);
        infoBar.addSeparator();
        infoBar.add(bestScore);
        infoBar.addSeparator();
        infoBar.add(currentRuntime);
        infoBar.addSeparator();
        infoBar.add(timeRemaining);
        infoBar.addSeparator();
        infoBar.setFloatable(false);

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(2, 1));
        toolPanel.add(toolbar);
        toolPanel.add(infoBar);

        frame.getContentPane().add(toolPanel, BorderLayout.NORTH);
        frame.getContentPane().add(getMainPanel(), BorderLayout.CENTER);
        notate = new MyNotate(new Score(),0,0);
        frame.getContentPane().add(notate,BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.addActionListener(this);
        file.addSeparator();
        file.add(fileExit);

        frame.setJMenuBar(menuBar);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass().equals(JMenuItem.class)) {
            JMenuItem src = (JMenuItem) e.getSource();
            if (src.getText().equals("Exit")) {
                if (currentRunner != null) {
                    currentRunner.stop();
                }
                frame.dispose();
            }
        } else if (e.getSource() == start) {
            // Start the engine
            currentRunner = mozartInstance();
            currentRunner.addListener(this);
            currentRunner.addListener(new DrawScoreListener(), BetterIndividualFoundEvent.class);

            output.setText("");
            Executors.newSingleThreadExecutor().submit(currentRunner);
            start.setEnabled(false);
            cancel.setEnabled(true);
        } else if (e.getSource() == cancel) {
            if (JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to stop the algorithm?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                currentRunner.stop();
                start.setEnabled(true);
                cancel.setEnabled(false);
            }
        }

    }

    private Lamark mozartInstance() {
        return mozartInstance((String) keySelect.getSelectedItem()
                , (String) signatureSelect.getSelectedItem()
                , "ALL".equals(rangeSelect.getSelectedItem()),
                Integer.valueOf(barCountEntry.getText()));
    }

    public Lamark mozartInstance(String keyS, String timeSigS, boolean fullRange, int barCount) {
        try {

            LamarkBuilder<Score> builder = LamarkBuilderSerializer.deserialize(getClass().getResourceAsStream("/mozart.json"));
            MozartSupplier supplier = (MozartSupplier) builder.getSupplier();

            if (!keyS.equals("ANY")) {
                supplier.setScale(ScaleEnum.valueOf(keyS));
            }

            supplier.setSignature(TimeSignatureEnum.valueOf(timeSigS));

            if (fullRange) {
                supplier.setLowerBound(0);
                supplier.setUpperBound(127);
            } else {
                supplier.setLowerBound(40);
                supplier.setUpperBound(81);
            }
            supplier.setSize(barCount);
            PhrasePool.instance.initialize(builder.getRandom(), supplier.getSignature(), supplier.getScale(), supplier.getLowerBound(), supplier.getUpperBound());

            return builder.build();

        } catch (Exception e) {
            IllegalArgumentException e2 = new IllegalArgumentException("Couldnt load defaults");
            e2.initCause(e);
            throw e2;
        }
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            List<String> keyNameList = new ArrayList<String>();
            //keyNameList.add("ANY");
            for (ScaleEnum e : ScaleEnum.values()) {
                keyNameList.add(e.name());
            }
            List<String> signatureNameList = new ArrayList<String>();
            //signatureNameList.add("ANY");
            for (TimeSignatureEnum e : TimeSignatureEnum.values()) {
                signatureNameList.add(e.name());
            }


            keySelect = new JComboBox(keyNameList.toArray());
            keySelect.setSelectedItem(ScaleEnum.C.name());
            signatureSelect = new JComboBox(signatureNameList.toArray());
            signatureSelect.setSelectedItem(TimeSignatureEnum.FOUR_FOUR.name());
            rangeSelect = new JComboBox(new String[]{"GRAND STAFF", "ANY"});
            barCountEntry = new JTextField("8");

            mainPanel = new JPanel(new BorderLayout());
            JPanel controls = new JPanel(new GridLayout(4, 2));
            controls.add(new JLabel("Key"));
            controls.add(keySelect);
            controls.add(new JLabel("Time Signature"));
            controls.add(signatureSelect);
            controls.add(new JLabel("Note Range"));
            controls.add(rangeSelect);
            controls.add(new JLabel("# of bars:"));
            controls.add(barCountEntry);

            JPanel textPane = new JPanel(new BorderLayout());

            output = new JTextArea();
            output.setRows(20);
            output.setColumns(80);
            JScrollPane outputScrollPane = new JScrollPane(output,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            textPane.add(new JLabel("Mozart Information:"));
            textPane.add(outputScrollPane, BorderLayout.CENTER);
            output.setText("Mozart\nVersion 1.0.0\nLexikos, Inc.");


            JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    controls, textPane);
            sp.setOneTouchExpandable(true);
            sp.setDividerLocation(150);
            Dimension minimumSize = new Dimension(200, 100);
            controls.setMinimumSize(minimumSize);
            textPane.setMinimumSize(minimumSize);
            mainPanel.add(sp, BorderLayout.CENTER);

        }
        return mainPanel;
    }

    public void run() {
        createAndShowGUI();
    }

    public void handleEvent(LamarkEvent je) {
        if (AbortedEvent.class.isAssignableFrom(je.getClass())) {
            output.insert(je.toString() + "\n\n", 0);
        } else if (BetterIndividualFoundEvent.class.isAssignableFrom(je.getClass())) {
            output.insert(je.toString() + "\n", 0);
            bestScore.setText("Best: " + LamarkUtil.format(((BetterIndividualFoundEvent) je).getNewBest().getFitness()));
        } else if (LastPopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            output.insert(je.toString() + "\n", 0);
            start.setEnabled(true);
            cancel.setEnabled(false);
        } else if (PopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            //output.append("\n"+je.toString());
            generationNumber.setText("Generation: " + ((PopulationCompleteEvent) je).getGenerationNumber());
        } else if (UniformPopulationEvent.class.isAssignableFrom(je.getClass())) {
            output.insert(je.toString() + "\n", 0);
        } else if (ExceptionEvent.class.isAssignableFrom(je.getClass())) {
            Throwable t = ((ExceptionEvent) je).getException();
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            String msg = "\nAn error occurred while attempting to run the algorithm:\n\n" + t + "\n\n" + sw.toString() + "\n";
            output.insert(msg, 0);
            start.setEnabled(true);
            cancel.setEnabled(false);
        }

        currentRuntime.setText("Runtime: "
                + LamarkUtil.formatISO(je.getLamark().getRunTime()));
        timeRemaining.setText("Remaining: "
                + LamarkUtil.formatISO(je.getLamark().getEstimatedRunTime()));

    }

    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getOutput() {
        return output;
    }

}
