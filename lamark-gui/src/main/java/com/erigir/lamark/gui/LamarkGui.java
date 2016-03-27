/*
 * Created on Sep 28, 2004
 */
package com.erigir.lamark.gui;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.Util;
import com.erigir.lamark.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A class implementing a simple GUI for creating and running Lamark instances.
 * &lt;p /&gt;
 * This panel is typically wrapped by either an applet (LamarkApplet) or application
 * (LamarkApplication), which only differ in whether they allow the file menu (the
 * applet has no menus since they would be disabled anyway).  The GUI has controls
 * for setting the various components and properties of the Lamark instance, and
 * for passing in custom properties and pre-load individuals.  It also can run
 * a Lamark instance defined via classpath (ie, a JAR file containing all needed
 * component classes + a lamark.json file).
 *
 * @author cweiss
 * @since 02-2005
 */

public class LamarkGui extends JPanel implements LamarkEventListener, ActionListener {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkGui.class);
    /**
     * String containing the label of the open url button *
     */
    public static final String OPEN_REMOTE = "Open URL...";
    /**
     * Handle to the central panel *
     */
    private JPanel mainPanel;
    /**
     * Button for starting the GA *
     */
    private JButton start;
    /**
     * Button for cancelling the GA *
     */
    private JButton cancel;
    /**
     * Button for showing the current properties *
     */
    private JButton show;
    /**
     * Button for opening a URL to load *
     */
    private JButton openUrl;
    /**
     * Button for resetting the instance to initial state *
     */
    private JButton reset;
    /**
     * Output area for showing any messages *
     */
    private JTextArea output;
    /**
     * Label holding current running time *
     */
    private JLabel currentRuntime;
    /**
     * Label showing estimated time remaining *
     */
    private JLabel timeRemaining;
    /**
     * Label showing current generation number *
     */
    private JLabel generationNumber;
    /**
     * Label showing best score to date *
     */
    private JLabel bestScore;
    /**
     * Label showing the current classloader *
     */
    private JLabel classLoaderLabel;
    /**
     * Label showing the amount of total memory taken from OS *
     */
    private JLabel totalMemory;
    /**
     * Label showing the amount of free memory in java currently *
     */
    private JLabel freeMemory;
    /**
     * Handle to the current instnace of Lamark, if any *
     */
    private Lamark currentRunner;
    /**
     * Panel holding the current configuration *
     */
    private LamarkConfigPanel configPanel;


    /**
     * Default constructor.
     * Lays out all the controls.
     * @param initialLocation String containing the initial location to load
     * @param initialSelection String containing the initial item to select
     */
    public LamarkGui(String initialLocation, String initialSelection) {

        configPanel = new LamarkConfigPanel(initialLocation);

        setLayout(new BorderLayout());

        JToolBar toolbar = new JToolBar();
        start = new JButton(icon("start.png"));
        start.addActionListener(this);
        cancel = new JButton(icon("stop.png"));
        cancel.setEnabled(false);
        cancel.addActionListener(this);
        show = new JButton(icon("show.png"));
        show.setEnabled(true);
        show.addActionListener(this);
        reset = new JButton(icon("new.png"));
        reset.setEnabled(true);
        reset.addActionListener(this);
        openUrl = new JButton(icon("open.png"));
        openUrl.setEnabled(true);
        openUrl.addActionListener(this);

        currentRuntime = new JLabel("Runtime: n/a");
        timeRemaining = new JLabel("Remaining: n/a");
        generationNumber = new JLabel("Generation: n/a");
        bestScore = new JLabel("Best: n/a");
        classLoaderLabel = new JLabel("Classloader: default");

        totalMemory = new JLabel("");
        freeMemory = new JLabel("");
        updateMemoryLabels();

        toolbar.add(start);
        toolbar.add(cancel);
        toolbar.add(show);
        toolbar.setFloatable(false);

        toolbar.addSeparator();
        toolbar.add(generationNumber);
        toolbar.addSeparator();
        toolbar.add(bestScore);
        toolbar.addSeparator();
        toolbar.add(currentRuntime);
        toolbar.addSeparator();
        toolbar.add(timeRemaining);
        toolbar.addSeparator();
        toolbar.add(totalMemory);
        toolbar.addSeparator();
        toolbar.add(freeMemory);
        toolbar.addSeparator();
        toolbar.add(classLoaderLabel);
        toolbar.setFloatable(false);

        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(1, 8));
        toolPanel.add(toolbar);

        add(toolPanel, BorderLayout.NORTH);
        add(getMainPanel(), BorderLayout.CENTER);

    }

    /**
     * Loads one of the built-in images as an icon.
     *
     * @param iconFile String containing name of the file
     * @return ImageIcon for use on buttons
     */
    private ImageIcon icon(String iconFile) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getResourceAsStream(iconFile)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load icon " + iconFile + " :" + e);
        }
    }

    /**
     * Make the classloader label match the current class loader.
     */
    private void updateClassLoaderLabel() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null && URLClassLoader.class.isAssignableFrom(cl.getClass())) {
            classLoaderLabel.setText("Classloader: " + Arrays.asList(((URLClassLoader) cl).getURLs()));
        } else {
            classLoaderLabel.setText("Classloader: default");
        }
    }

    /**
     * Empty the current runner from memory and reset labels.
     */
    private void clearCurrent() {
        currentRunner = null;
        updateClassLoaderLabel();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            output.setText("Starting new Lamark instance...\n\n");

                // First, generate a new currentRunner
                currentRunner = configPanel.getBuilder().build();

                // Add this as a generic listener for timers
                currentRunner.addListener(this);

                // Add any defined listeners
                OutputListener ol = new OutputListener(output);
                Set<Class> outputListenerClasses = new HashSet<>();
                if (configPanel.listenAbort()) {
                    outputListenerClasses.add(AbortedEvent.class);
                }
                if (configPanel.listenBetterIndividualFound()) {
                    outputListenerClasses.add(BetterIndividualFoundEvent.class);
                }
                if (configPanel.listenException()) {
                    outputListenerClasses.add(ExceptionEvent.class);
                }
                if (configPanel.listenLastPopDone()) {
                    outputListenerClasses.add(LastPopulationCompleteEvent.class);
                }
                if (configPanel.listenPopulationComplete()) {
                    outputListenerClasses.add(PopulationCompleteEvent.class);
                }
                if (configPanel.listenUniformPop()) {
                    outputListenerClasses.add(UniformPopulationEvent.class);
                }
                currentRunner.addListener(ol,outputListenerClasses);


                output.setText("");
                start.setEnabled(false);
                cancel.setEnabled(true);
                reset.setEnabled(false);
                openUrl.setEnabled(false);
                show.setEnabled(false);
                configPanel.setEnabled(false);
                Future f = Executors.newSingleThreadExecutor().submit(currentRunner);

            LOG.info("got : {}",f);

        } else if (e.getSource() == show) {
            output.setText(configPanel.toGUIConfigString());


        } else if (e.getSource() == cancel) {
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to stop the algorithm?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                currentRunner.stop();
                start.setEnabled(true);
                cancel.setEnabled(false);
                reset.setEnabled(true);
                openUrl.setEnabled(true);
                show.setEnabled(true);
                configPanel.setEnabled(true);
            }
        } else if (e.getSource() == openUrl) {
            openUrlDialog();
        } else if (e.getSource() == reset) {
            resetToNew();
        }
    }

    /**
     * Opens a dialog to type in a URL, then loads Lamark from the URL if possible.
     */
    public void openUrlDialog() {
        String url = (String) JOptionPane.showInputDialog(
                this,
                "Enter a url to a config file (json file) or jar file",
                OPEN_REMOTE,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
        configPanel.loadFromLocation(url);
    }

    public String configJSON() {
        return configPanel.toGUIConfigString();
    }


    /**
     * Builds the main panel of the gui.
     *
     * @return JPanel containing the main controls
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            JPanel textPane = new JPanel(new BorderLayout());

            output = new JTextArea();
            output.setRows(20);
            output.setColumns(80);
            JScrollPane outputScrollPane = new JScrollPane(output,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            output.setEditable(false);

            textPane.add(outputScrollPane, BorderLayout.CENTER);
            output.setText("Lamark\nGraphical User Interface\nVersion " + Util.getVersion() + "\n");

            mainPanel.add(configPanel, BorderLayout.NORTH);
            mainPanel.add(textPane, BorderLayout.CENTER);
            /**
             TODO: Add stats panel when its built
             mainPanel.add(getStatsPanel(),BorderLayout.SOUTH);
             */

        }
        return mainPanel;
    }

    /**
     * Used by the container classes to shut down the runner if needed.
     */
    public void abortIfRunning() {
        if (currentRunner != null && currentRunner.isRunning()) {
            currentRunner.stop();
            start.setEnabled(true);
            cancel.setEnabled(false);
            reset.setEnabled(true);
            openUrl.setEnabled(true);
            show.setEnabled(true);
            configPanel.setEnabled(true);
        }
    }

    /**
     * Called by containers to reset the GUI to it's initial state.
     */
    public void resetToNew() {
        abortIfRunning();
        clearCurrent();
        configPanel.reset();
        updateClassLoaderLabel();
    }

    /**
     * Empties contents of the output pane
     */
    public void clearOutput() {
        output.setText("");
    }

    /**
     * Adds the given object to the output pane at end
     *
     * @param o Object to add to output pane (toString)
     */
    public void appendOutput(Object o) {
        if (o != null) {
            output.append(o.toString());
        }
    }

    /**
     * Adds the given object to the output pane at start
     *
     * @param o Object to add to output pane (toString)
     */
    public void prependOutput(Object o) {
        if (o != null) {
            output.insert(o.toString(), 0);
        }
    }
    
    /*
    private JPanel getStatsPanel()
    {
        JPanel rval = new JPanel();
        rval.add(new JLabel("TODO: Statistics Panel Will Go Here"));
        return rval;
    }
    */

    /**
     * This handleEvent updates the toolbar, not the output pane, which is handled by the custom handler below.
     *
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je) {
        if (BetterIndividualFoundEvent.class.isAssignableFrom(je.getClass())) {
            bestScore.setText("Best: " + Util.format(((BetterIndividualFoundEvent) je).getNewBest().getFitness()));
        } else if (LastPopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            start.setEnabled(true);
            cancel.setEnabled(false);
            reset.setEnabled(true);
            openUrl.setEnabled(true);
            show.setEnabled(true);
            configPanel.setEnabled(true);
        } else if (PopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            generationNumber.setText("Generation: " + ((PopulationCompleteEvent) je).getGenerationNumber());
        } else if (ExceptionEvent.class.isAssignableFrom(je.getClass())) {
            start.setEnabled(true);
            cancel.setEnabled(false);
            reset.setEnabled(true);
            openUrl.setEnabled(true);
            show.setEnabled(true);
            configPanel.setEnabled(true);
        }

        currentRuntime.setText("Runtime: "
                + Util.formatISO(je.getLamark().getRunTime()));
        timeRemaining.setText("Remaining: "
                + Util.formatISO(je.getLamark().getEstimatedRunTime()));

        updateMemoryLabels();
    }

    /**
     * Updates the contents of the memory labels.
     */
    private void updateMemoryLabels() {
        totalMemory.setText("Total Memory: " +
                (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + " Mb");
        freeMemory.setText("Free Memory :" +
                (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + " Mb");
    }

    /**
     * Return a handle to the configuration panel
     * The config panel can be used to force loading of a new configuration
     *
     * @return LamarkConfigPanel handle to the config panel
     */
    public LamarkConfigPanel getConfigPanel() {
        return configPanel;
    }

    /**
     * A class to wrap a lamarklistener around the output panel of the gui.
     * &lt;p /&gt;
     * This class catches the selected events and outputs their
     * string representation into the output panel.
     *
     * @author cweiss
     * @since 10/2007
     */
    class OutputListener implements LamarkEventListener {
        /**
         * Handle to the output area *
         */
        private JTextArea output;


        /**
         * Constructor that passes handle to the output pane
         *
         * @param pOutput JTextArea output pane
         */
        public OutputListener(JTextArea pOutput) {
            super();
            output = pOutput;
        }

        /**
         * Writes appropriate events to the output pane.
         *
         * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
         */
        public void handleEvent(LamarkEvent je) {
            if (ExceptionEvent.class.isAssignableFrom(je.getClass())) {
                Throwable t = ((ExceptionEvent) je).getException();
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                String msg = "\nAn error occurred while attempting to run the algorithm:\n\n" + t + "\n\n" + sw.toString() + "\n";
                output.insert(msg, 0);
                start.setEnabled(true);
                cancel.setEnabled(false);
            } else {
                output.insert(je.toString() + " \n\n", 0);
            }
        }
    }
}