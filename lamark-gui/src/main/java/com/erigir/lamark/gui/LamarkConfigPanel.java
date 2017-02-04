package com.erigir.lamark.gui;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkBuilder;
import com.erigir.lamark.config.*;
import com.erigir.lamark.events.*;
import com.erigir.lamark.selector.Selector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * A panel for holding and organizing the various properties controls for LamarkGUI.
 * &lt;p /&gt;
 * Also wraps business logic for converting these controls into properties object
 * that can configure a lamark instance or be saved to disk.
 *
 * @author cweiss
 * @since 10/2007
 */
public class LamarkConfigPanel extends BorderPane {
    /**
     * Logging instance *
     */
    private static final Logger LOG = LoggerFactory.getLogger(LamarkConfigPanel.class.getName());
    /**
     * Location of the default file
     */
    private static final String DEFAULT_CONFIG_LOCATION = "classpath:com/erigir/lamark/gui/default-lamark.json";

    // These hold the selections as well as the custom properties
    private LamarkComponentConfigPanel<Supplier> supplierConfigPane=new LamarkComponentConfigPanel();
    private LamarkComponentConfigPanel<Function> crossoverConfigPane=new LamarkComponentConfigPanel();
    private LamarkComponentConfigPanel<ToDoubleFunction> fitnessConfigPane=new LamarkComponentConfigPanel();
    private LamarkComponentConfigPanel<Function> mutatorConfigPane=new LamarkComponentConfigPanel();
    private LamarkComponentConfigPanel<Selector> selectorConfigPane=new LamarkComponentConfigPanel();

    /**
     * List of strings to convert into preload individuals *
     */
    private List<String> preloads = new ArrayList<String>();
    /**
     * List of custom listeners to instantiate *
     */
    private List<String> customListener = new ArrayList<String>();

    /**
     * Button/Label for preloads *
     */
    private Button preloadButton = new Button("Preloads...");
    /**
     * Button/Label for custom listeners *
     */
    private Button customListenerButton = new Button("Custom Listeners...");
    /**
     * Label for upper elitism control *
     */
    private Label upperElitismLabel = new Label("Upper Elitism (%)");
    /**
     * Label for lower elitism control *
     */
    private Label lowerElitismLabel = new Label("Lower Elitism (%)");
    /**
     * Label for max pop control *
     */
    private Label maximumPopulationsLabel = new Label("Max. Populations (Blank for cont.)");
    /**
     * Label for pop size control *
     */
    private Label populationSizeLabel = new Label("Population Size");
    /**
     * Label for crossover prob control *
     */
    private Label crossoverProbabilityLabel = new Label("Crossover Prob (%)");
    /**
     * Label for mutation prob control *
     */
    private Label mutationProbabilityLabel = new Label("Mutation Prob (%)");
    /**
     * Label for target score control *
     */
    private Label targetScoreLabel = new Label("Target Score (Blank for cont.)");
    /**
     * Label for random seed control *
     */
    private Label randomSeedLabel = new Label("Random Seed");

    /**
     * Better individual listener enabler *
     */
    private CheckBox lBetterIndividualFound = new CheckBox("Better Individual Found");
    /**
     * Exception listener enabler *
     */
    private CheckBox lException = new CheckBox("Exception");
    /**
     * Last Population listener enabler *
     */
    private CheckBox lLastPopDone = new CheckBox("Last Population Done");
    /**
     * Abort listener enabler *
     */
    private CheckBox lAbort = new CheckBox("Aborted");
    /**
     * Population Done listener enabler *
     */
    private CheckBox lPopulationComplete = new CheckBox("Population Done");
    /**
     * Uniform Population listener enabler *
     */
    private CheckBox lUniformPop = new CheckBox("Uniform Population");

    /**
     * Edit box holding upper elitism *
     */
    private Spinner<Integer> upperElitism = new Spinner<Integer>(0,100,10);
    /**
     * Edit box holding lower elitism *
     */
    private Spinner<Integer>  lowerElitism = new Spinner<Integer>(0,100,10);
    /**
     * Edit box holding maximum populations *
     */
    private TextField maximumPopulations = new TextField();
    /**
     * Edit box holding population size *
     */
    private TextField  populationSize = new TextField();
    /**
     * Edit box holding crossover probability *
     */
    private Spinner<Integer>  crossoverProbability = new Spinner<Integer>(0,100,10);
    /**
     * Edit box holding mutation probability *
     */
    private Spinner<Integer>  mutationProbability = new Spinner<Integer>(0,100,10);
    /**
     * Edit box holding target score *
     */
    private TextField targetScore = new TextField();
    /**
     * Edit box holding random seed *
     */
    private TextField randomSeed = new TextField();

    /**
     * Default constructor.
     * &lt;p /&gt;
     * Builds the layout.
     * @param inConfigResource String containing the config to load
     */
    public LamarkConfigPanel(final String inConfigResource) {
        super();

        setTop(classPanel());
        setCenter(panel1());
        setBottom(panel2());

        // Finally, initialize
        String configResource = (inConfigResource == null) ? DEFAULT_CONFIG_LOCATION : inConfigResource;

        loadFromLocation(configResource);
    }

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enable) {
        this.setDisable(!enable);
        /*
        supplierLabel.setEnabled(enable);
        crossoverLabel.setEnabled(enable);
        fitnessLabel.setEnabled(enable);
        mutatorLabel.setEnabled(enable);
        selectorLabel.setEnabled(enable);
        preloadButton.setEnabled(enable);
        customListenerButton.setEnabled(enable);

        lBetterIndividualFound.setEnabled(enable);
        lException.setEnabled(enable);
        lLastPopDone.setEnabled(enable);
        lAbort.setEnabled(enable);

        lLog.setEnabled(enable);
        lPopulationComplete.setEnabled(enable);
        lPopPlanDone.setEnabled(enable);
        lUniformPop.setEnabled(enable);

        supplier.setEnabled(enable);
        crossover.setEnabled(enable);
        fitness.setEnabled(enable);
        mutator.setEnabled(enable);
        selector.setEnabled(enable);
        upperElitism.setEnabled(enable);
        lowerElitism.setEnabled(enable);
        maximumPopulations.setEnabled(enable);
        populationSize.setEnabled(enable);
        crossoverProbability.setEnabled(enable);
        mutationProbability.setEnabled(enable);
        targetScore.setEnabled(enable);
        randomSeed.setEnabled(enable);
        */
    }

    public void loadFromConfiguration(LamarkConfiguration configuration)
    {
        LOG.info("Loading from configuration {}",configuration);

        Platform.runLater(()-> {
                    // Init the drop boxes
                    supplierConfigPane.loadDefault("Supplier", configuration.getComponents().getSupplier());
                    crossoverConfigPane.loadDefault("Crossover", configuration.getComponents().getCrossover());
                    fitnessConfigPane.loadDefault("Fitness", configuration.getComponents().getFitnessFunction());
                    mutatorConfigPane.loadDefault("Mutator", configuration.getComponents().getMutator());
                    selectorConfigPane.loadDefault("Selector", configuration.getComponents().getSelector());

                    LamarkParameters p = configuration.getParameters();
                    upperElitism.getValueFactory().setValue((int) (p.getUpperElitismPercentage() * 100));
                    lowerElitism.getValueFactory().setValue((int) (p.getLowerElitismPercentage() * 100));
                    maximumPopulations.setText(ein(p.getMaxGenerations()));
                    populationSize.setText(String.valueOf(p.getPopulationSize()));
                    crossoverProbability.getValueFactory().setValue((int) (p.getCrossoverProbability() * 100));
                    mutationProbability.getValueFactory().setValue((int) (p.getMutationProbability() * 100));
                    targetScore.setText(ein(p.getTargetScore()));
                    randomSeed.setText(ein(p.getRandomSeed()));
                });

        /*
        preloads = new ArrayList<String>(lc.getPreCreatedIndividuals());

        customListener = new ArrayList<String>();
        for (Class c : lc.getCustomListeners()) {
            customListener.add(c.getName());
        }
        TODO: Reimpl
        */
    }

    public LamarkConfiguration convertToConfiguration()
    {
        LamarkParameters parameters = new LamarkParameters();
        LamarkComponents components = new LamarkComponents();
        LamarkConfiguration rval = new LamarkConfiguration();

        rval.setComponents(components);
        rval.setParameters(parameters);

        components.setSupplier(supplierConfigPane.toComponentDetails());
        components.setCrossover(crossoverConfigPane.toComponentDetails());
        components.setMutator(mutatorConfigPane.toComponentDetails());
        components.setFitnessFunction(fitnessConfigPane.toComponentDetails());
        components.setSelector(selectorConfigPane.toComponentDetails());

        parameters.setInitialValues(new LinkedList<String>(preloads));
        parameters.setUpperElitismPercentage(upperElitism.getValue()/100.0);
        parameters.setLowerElitismPercentage(lowerElitism.getValue()/100.0);
        parameters.setMaxGenerations(nsLong(maximumPopulations.getText()));
        parameters.setPopulationSize(Integer.parseInt(populationSize.getText()));
        parameters.setCrossoverProbability(crossoverProbability.getValue()/100.0);
        parameters.setMutationProbability(mutationProbability.getValue()/100.0);
        parameters.setTargetScore(nsDouble(targetScore.getText()));

        /*
        for (String s : customListener) {
            Class c = LamarkAvailableClasses.safeLoadClass(s);
            if (c != null) {
                p.getCustomListeners().add(c);
            }
        }
        TODO: listeners get added to the lamark instance?
        */

        return rval;
    }



    public void reset() {
        loadFromLocation(DEFAULT_CONFIG_LOCATION);
    }

    private String readStreamToString(InputStream ios)
            throws IOException {
        String rval = null;
        if (ios != null) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(ios));
            String next = br.readLine();
            while (next != null) {
                sb.append(next);
                next = br.readLine();
            }
            br.close();
            rval = sb.toString();

        }
        return rval;
    }

    /**
     * Given a location, load the config panel from it
     * A location can be either of the form "classpath:xxx" or a url
     * If its a URL, it can point either to a xxx.json file or a xxx.jar file
     * If its a jar file, its assumed to have a file named "lamark.json" at the root level (along with
     * all classes referred to in that file)
     * After the json is read, it may have multiple entries - if one is specified, it will be used.
     * If only one entry is found, it will be used
     * Otherwise, a dialog box for choices will be shown
     *
     * @param location          String containing a path to a resource
     */

    public void loadFromLocation(String location) {
        try {
            InputStream is = null;

            if (location != null) {
                if (location.startsWith("classpath:")) {
                    // Load as a resource - can only be a json file (no jars on classpath)
                    String readLoc = location.substring("classpath:".length());
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(readLoc);
                } else {
                    // Load as a url
                    URL u = safeURL(location);
                    if (location.endsWith(".json")) {
                        // Just need to read the json
                        is = u.openStream();
                    } else {
                        // Need to massage the classpath and load default
                        URLClassLoader newClassLoader = new URLClassLoader(new URL[]{u}, Thread.currentThread().getContextClassLoader());
                        is = newClassLoader.getResourceAsStream("/lamark.json");
                    }
                }

                if (is!=null)
                {
                    String jsonData = readStreamToString(is);
                    MultiLamarkConfiguration mlc = LamarkSerializer.readConfiguration(jsonData);
                    if (mlc.getConfigurations().size()==1)
                    {
                        loadFromConfiguration(mlc.getConfigurations().values().iterator().next());
                    }
                    else
                    {
                        // TODO: impl
                        LOG.warn("Cant process multi yet");
                    }
                } else {
                    throw new IllegalStateException("Failed to load configuration JSON file");
                }
            }
        } catch (IOException ioe) {
            LOG.error("Error reading location {}", location, ioe);
        }

    }

    private URL safeURL(String input) {
        try {
            return new URL(input);
        } catch (MalformedURLException mue) {
            throw new RuntimeException("Unable to read url", mue);
        }
    }

    /**
     * Lays out and returns the first panel
     *
     * @return Pane containing the first row.
     */
    private GridPane panel1() {

        populationSize.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        targetScore.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        GridPane rval = new GridPane();
        GridPane.setConstraints(populationSizeLabel,0,0);
        GridPane.setConstraints(crossoverProbabilityLabel,1,0);
        GridPane.setConstraints(mutationProbabilityLabel,2,0);
        GridPane.setConstraints(maximumPopulationsLabel,3,0);
        GridPane.setConstraints(targetScoreLabel,4,0);
        GridPane.setConstraints(customListenerButton,5,0);
        
        GridPane.setConstraints(populationSize,0,1);
        GridPane.setConstraints(crossoverProbability,1,1);
        GridPane.setConstraints(mutationProbability,2,1);
        GridPane.setConstraints(maximumPopulations,3,1);
        GridPane.setConstraints(targetScore,4,1);
        GridPane.setConstraints(preloadButton,5,1);

        rval.getChildren().addAll(populationSizeLabel
        ,crossoverProbabilityLabel
        ,mutationProbabilityLabel
        ,maximumPopulationsLabel
        ,targetScoreLabel
        ,customListenerButton

        ,populationSize
        ,crossoverProbability
        ,mutationProbability
        ,maximumPopulations
        ,targetScore
        ,preloadButton);

        
        customListenerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                customListener = editStringList(customListener, customListenerButton.getText());
            }
        });

        preloadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 preloads = editStringList(preloads, preloadButton.getText());
            }
        });

        return rval;

    }

    /**
     * Lays out and returns the second panel.
     *
     * @return Pane containing the second row.
     */
    private BorderPane panel2() {
        BorderPane rval = new BorderPane();

        GridPane north = new GridPane();
        GridPane.setConstraints(upperElitismLabel,0,0);
        GridPane.setConstraints(lowerElitismLabel,1,0);
        GridPane.setConstraints(randomSeedLabel,2,0);
        GridPane.setConstraints(upperElitism,0,1);
        GridPane.setConstraints(lowerElitism,1,1);
        GridPane.setConstraints(randomSeed,2,1);
        north.getChildren().addAll(upperElitismLabel,lowerElitismLabel,randomSeedLabel,
                upperElitism,lowerElitism,randomSeed);

        HBox south = new HBox();
        south.setSpacing(5.0);

        lBetterIndividualFound.setSelected(true);
        lException.setSelected(true);
        lLastPopDone.setSelected(true);
        lAbort.setSelected(true);

        lPopulationComplete.setSelected(false);
        lUniformPop.setSelected(false);

        south.getChildren().add(lBetterIndividualFound);
        south.getChildren().add(lException);
        south.getChildren().add(lLastPopDone);
        south.getChildren().add(lAbort);
        south.getChildren().add(lPopulationComplete);
        south.getChildren().add(lUniformPop);

        rval.setTop(north);
        rval.setBottom(south);

        return rval;
    }

    /**
     * Builds and lays out the component class panel.
     *
     * @return Pane containing the component class edit boxes.
     */
    private GridPane classPanel() {
        GridPane rval = new GridPane();

        GridPane.setConstraints(supplierConfigPane,0,0);
        GridPane.setConstraints(crossoverConfigPane,1,0);
        GridPane.setConstraints(fitnessConfigPane,2,0);
        GridPane.setConstraints(mutatorConfigPane,3,0);
        GridPane.setConstraints(selectorConfigPane,4,0);

        rval.getChildren().addAll(supplierConfigPane,crossoverConfigPane,fitnessConfigPane,mutatorConfigPane,selectorConfigPane);

        return rval;
    }


    /**
     * Opens a dialog box to edit a string list
     *
     * @param p     List of strings to edit
     * @param label String containing label for dialog box
     * @return List of strings after edit
     */
    private List<String> editStringList(List<String> p, String label) {
        StringListDialog pd = new StringListDialog(label, p);
        pd.setVisible(true);
        return pd.getData();
    }

    /**
     * Opens a dialog box to edit a properties object
     *
     * @param p     Properties object to edit
     * @param label String label for the dialog box
     * @return Properties object after edit
     */
    private Properties editProperties(Properties p, String label) {
        PropertiesDialog pd = new PropertiesDialog(label, p);
        pd.setVisible(true);
        return pd.getProperties();
    }

    /**
     * Determines whether to output an event.
     * Note - in real life, you'd just listen to the correct events, but in here we allow filtering for easy output, plus
     * this allows it to be modified in real time (otherwise it'd be created just before the run)
     * to the GUI.
     * @param event
     * @return true if the event should be logged
     */
    public boolean eventOutputEnabled(LamarkEvent event)
    {
        return
                checkEventFilterMatch(lBetterIndividualFound, BetterIndividualFoundEvent.class, event)
                        || checkEventFilterMatch(lException, ExceptionEvent.class, event)
                        || checkEventFilterMatch(lLastPopDone, LastPopulationCompleteEvent.class, event)
                        || checkEventFilterMatch(lAbort, AbortedEvent.class, event)
                        || checkEventFilterMatch(lPopulationComplete, PopulationCompleteEvent.class, event)
                        || checkEventFilterMatch(lUniformPop, UniformPopulationEvent.class, event);
   }

    private boolean checkEventFilterMatch(CheckBox box, Class eventClass, Object event)
    {
        return box.isSelected() && event!=null && event.getClass().equals(eventClass);
    }

    private Double nsDouble(String value) {
        return (value == null || value.trim().length() == 0) ? null : new Double(value);
    }

    private Long nsLong(String value) {
        return (value == null || value.trim().length()==0) ? null : new Long(value);
    }

    /**
     * Converts null to empty string, otherwise does nothing.
     *
     * @param s String to convert.
     * @return String that was converted
     */
    private String ein(Object s) {
        return (s == null) ? "" : String.valueOf(s);
    }

    /**
     * Creates a properties object matching the state of the panel.
     *
     * @return Properties object matching the panel.
     */
    public LamarkBuilder toBuilder() {
        LamarkBuilder builder = new LamarkBuilder();
        return convertToConfiguration().applyToBuilder(builder);

    }

    /**
     * Accessor method.
     *
     * @return List containing the property
     */
    public List<String> getCustomListener() {
        return customListener;
    }


}
