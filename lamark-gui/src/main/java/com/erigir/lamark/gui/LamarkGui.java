/*
 * Created on Sep 28, 2004
 */
package com.erigir.lamark.gui;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.events.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
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

public class LamarkGui extends Pane {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkGui.class);
    /**
     * String containing the label of the open url button *
     */
    public static final String OPEN_REMOTE = "Open URL...";
    /**
     * Handle to the central panel *
     */
    private BorderPane mainPanel;
    /**
     * Button for starting the GA *
     */
    private Button start;
    /**
     * Button for cancelling the GA *
     */
    private Button cancel;
    /**
     * Button for showing the current properties *
     */
    private Button show;
    /**
     * Button for opening a URL to load *
     */
    private Button openUrl;
    /**
     * Button for resetting the instance to initial state *
     */
    private Button reset;
    /**
     * Output area for showing any messages *
     */
    private TextArea output;
    /**
     * Label holding current running time *
     */
    private Label currentRuntime;
    /**
     * Label showing estimated time remaining *
     */
    private Label timeRemaining;
    /**
     * Label showing current generation number *
     */
    private Label generationNumber;
    /**
     * Label showing best score to date *
     */
    private Label bestScore;
    /**
     * Label showing the current classloader *
     */
    private Label classLoaderLabel;
    /**
     * Label showing the amount of total memory taken from OS *
     */
    private Label totalMemory;
    /**
     * Label showing the amount of free memory in java currently *
     */
    private Label freeMemory;
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
        // Build mainpanel

        ToolBar toolbar = new ToolBar();
        start = new Button("Start",icon("start.png"));
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                {
                    output.setText("Starting new Lamark instance...\n\n");

                    // First, generate a new currentRunner
                    currentRunner = configPanel.getBuilder().build();

                    // Add this as a generic listener for timers
                    currentRunner.addListener(new OutputListener(output));

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
                    start.setDisable(true);
                    cancel.setDisable(false);
                    reset.setDisable(true);
                    openUrl.setDisable(true);
                    show.setDisable(true);

                    configPanel.setEnabled(false);
                    Future f = Executors.newSingleThreadExecutor().submit(currentRunner);

                    LOG.info("got : {}",f);
                }
            }
        });
        start.setDisable(false);

        cancel = new Button("Stop",icon("stop.png"));
        cancel.setDisable(true);
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Cancel");
                alert.setHeaderText("Confirm Cancel");
                alert.setContentText("Are you sure you want to stop the algorithm?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK){
                    currentRunner.stop();
                    start.setDisable(false);
                    cancel.setDisable(true);
                    reset.setDisable(false);
                    openUrl.setDisable(false);
                    show.setDisable(false);
                    configPanel.setEnabled(true);
                }
            }
        });

        show = new Button("Show",icon("show.png"));
        show.setDisable(false);
        show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                output.setText(configPanel.toGUIConfigString());
            }
        });

        reset = new Button("New",icon("new.png"));
        reset.setDisable(false);
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetToNew();
            }
        });

        openUrl = new Button("Open",icon("open.png"));
        openUrl.setDisable(false);
        openUrl.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openUrlDialog();
            }
        });

        currentRuntime = new Label("Runtime: n/a");
        timeRemaining = new Label("Remaining: n/a");
        generationNumber = new Label("Generation: n/a");
        bestScore = new Label("Best: n/a");
        classLoaderLabel = new Label("Classloader: default");

        totalMemory = new Label("");
        freeMemory = new Label("");
        updateMemoryLabels();

        toolbar.getItems().add(start);
        toolbar.getItems().add(cancel);
        toolbar.getItems().add(show);

        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(generationNumber);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(bestScore);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(currentRuntime);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(timeRemaining);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(totalMemory);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(freeMemory);
        toolbar.getItems().add(new Separator());
        toolbar.getItems().add(classLoaderLabel);
        //TODO : toolbar.setFloatable(false);

        mainPanel.setTop(toolbar);
        mainPanel.setCenter(getMainPanel());

        this.getChildren().add(mainPanel);
    }

    /**
     * Loads one of the built-in images as an icon.
     *
     * @param iconFile String containing name of the file
     * @return ImageIcon for use on buttons
     */
    private ImageView icon(String iconFile) {
        try {
            return new ImageView(new Image(getClass().getResourceAsStream(iconFile)));
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
     * Opens a dialog to type in a URL, then loads Lamark from the URL if possible.
     */
    public void openUrlDialog() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Load configuration from URL");
        dialog.setHeaderText(OPEN_REMOTE);
        dialog.setContentText("Enter a url to a config file (json file) or jar file:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
        {
            configPanel.loadFromLocation(result.get());
        }
    }

    public String configJSON() {
        return configPanel.toGUIConfigString();
    }


    /**
     * Builds the main panel of the gui.
     *
     * @return Pane containing the main controls
     */
    private Pane getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new BorderPane();
            
            output = new TextArea();
            output.setPrefRowCount(20);
            output.setPrefColumnCount(80);
            //JScrollPane outputScrollPane = new JScrollPane(output,
            //        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
             //       JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            output.setEditable(false);

            //textPane.add(outputScrollPane, BorderLayout.CENTER);
            output.setText("Lamark\nGraphical User Interface\nVersion " + LamarkUtil.getVersion() + "\n");

            mainPanel.setTop(configPanel);
            mainPanel.setCenter(output);
            /**
             TODO: Add stats panel when its built
             mainPanel.setBottom(getStatsPanel(),BorderLayout.SOUTH);
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
            start.setDisable(false);
            cancel.setDisable(true);
            reset.setDisable(false);
            openUrl.setDisable(false);
            show.setDisable(false);
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
            output.appendText(o.toString());
        }
    }

    /**
     * Adds the given object to the output pane at start
     *
     * @param o Object to add to output pane (toString)
     */
    public void prependOutput(Object o) {
        if (o != null) {
            output.insertText(0,o.toString());
        }
    }
    
    /*
    private Pane getStatsPanel()
    {
        Pane rval = new Pane();
        rval.add(new Label("TODO: Statistics Panel Will Go Here"));
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
            bestScore.setText("Best: " + LamarkUtil.format(((BetterIndividualFoundEvent) je).getNewBest().getFitness()));
        } else if (LastPopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            start.setDisable(false);
            cancel.setDisable(true);
            reset.setDisable(false);
            openUrl.setDisable(false);
            show.setDisable(false);
            configPanel.setEnabled(true);
        } else if (PopulationCompleteEvent.class.isAssignableFrom(je.getClass())) {
            generationNumber.setText("Generation: " + ((PopulationCompleteEvent) je).getGenerationNumber());
        } else if (ExceptionEvent.class.isAssignableFrom(je.getClass())) {
            start.setDisable(false);
            cancel.setDisable(true);
            reset.setDisable(false);
            openUrl.setDisable(false);
            show.setDisable(false);
            configPanel.setEnabled(true);
        }

        currentRuntime.setText("Runtime: "
                + LamarkUtil.formatISO(je.getLamark().getRunTime()));
        timeRemaining.setText("Remaining: "
                + LamarkUtil.formatISO(je.getLamark().getEstimatedRunTime()));

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
        private TextArea output;


        /**
         * Constructor that passes handle to the output pane
         *
         * @param pOutput JTextArea output pane
         */
        public OutputListener(TextArea pOutput) {
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
                output.insertText(0, msg);
                start.setDisable(false);
                cancel.setDisable(true);
            } else {
                output.insertText(0,je.toString() + " \n\n");
            }
        }
    }
}