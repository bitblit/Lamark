package com.erigir.lamark.gui;

import com.erigir.lamark.LamarkBuilder;
import com.erigir.lamark.LamarkBuilderSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

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
    /**
    * The shared ObjectMapper instance
     */
    private static final ObjectMapper MAPPER = createObjectMapper();

    private LamarkAvailableClasses availableClasses = loadAvailableClasses();

    private LamarkBuilder builder = new LamarkBuilder();

    /**
     * Custom properties for supplier *
     */
    private Properties supplierProperties = new Properties();
    /**
     * Custom properties for crossover *
     */
    private Properties crossoverProperties = new Properties();
    /**
     * Custom properties for fitness *
     */
    private Properties fitnessProperties = new Properties();
    /**
     * Custom properties for mutator *
     */
    private Properties mutatorProperties = new Properties();
    /**
     * Custom properties for selector *
     */
    private Properties selectorProperties = new Properties();

    /**
     * List of strings to convert into preload individuals *
     */
    private List<String> preloads = new ArrayList<String>();
    /**
     * List of custom listeners to instantiate *
     */
    private List<String> customListener = new ArrayList<String>();

    /**
     * Button/Label for supplier *
     */
    private Button supplierLabel = new Button("Supplier");
    /**
     * Button/Label for crossover *
     */
    private Button crossoverLabel = new Button("Crossover");
    /**
     * Button/Label for fitness *
     */
    private Button fitnessLabel = new Button("Fitness Function");
    /**
     * Button/Label for mutator *
     */
    private Button mutatorLabel = new Button("Mutator");
    /**
     * Button/Label for selector *
     */
    private Button selectorLabel = new Button("Selector");
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
    private Label crossoverProbabilityLabel = new Label("Crossover Prob (0-1)");
    /**
     * Label for mutation prob control *
     */
    private Label mutationProbabilityLabel = new Label("Mutation Prob (0-1)");
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
     * Log listener enabler *
     */
    private CheckBox lLog = new CheckBox("Log");
    /**
     * Population Done listener enabler *
     */
    private CheckBox lPopulationComplete = new CheckBox("Population Done");
    /**
     * Population Plan listener enabler *
     */
    private CheckBox lPopPlanDone = new CheckBox("Population Plan Done");
    /**
     * Uniform Population listener enabler *
     */
    private CheckBox lUniformPop = new CheckBox("Uniform Population");

    /**
     * Combobox holding supplier name *
     */
    private ComboBox supplier = new ComboBox();
    /**
     * Combobox holding crossover name *
     */
    private ComboBox crossover = new ComboBox();
    /**
     * Combobox holding fitness name *
     */
    private ComboBox fitness = new ComboBox();
    /**
     * Combobox holding mutator name *
     */
    private ComboBox mutator = new ComboBox();
    /**
     * Combobox holding selector name *
     */
    private ComboBox selector = new ComboBox();
    /**
     * Edit box holding upper elitism *
     */
    private TextField upperElitism = new TextField();
    /**
     * Edit box holding lower elitism *
     */
    private TextField lowerElitism = new TextField();
    /**
     * Edit box holding maximum populations *
     */
    private TextField maximumPopulations = new TextField();
    /**
     * Edit box holding population size *
     */
    private TextField populationSize = new TextField();
    /**
     * Edit box holding crossover probability *
     */
    private TextField crossoverProbability = new TextField();
    /**
     * Edit box holding mutation probability *
     */
    private TextField mutationProbability = new TextField();
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

    private static ObjectMapper createObjectMapper()
    {
        ObjectMapper rval = new ObjectMapper();
        rval.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return rval;
    }

    public static Map<String, Object> propertiesToMap(Properties p) {
        Map<String, Object> rval = null;
        if (p != null) {
            rval = new TreeMap<String, Object>();
            for (Map.Entry<Object, Object> e : p.entrySet()) {
                rval.put((String) e.getKey(), (String) e.getValue());
            }
        }
        return rval;
    }

    public static Properties mapToProperties(Map<String, Object> m) {
        Properties rval = null;
        if (m != null) {
            rval = new Properties();
            for (Map.Entry<String, Object> e : m.entrySet()) {
                rval.setProperty(e.getKey(), String.valueOf(e.getValue()));
            }
        }
        return rval;

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
            String json = null;

            if (location != null) {
                if (location.startsWith("classpath:")) {
                    // Load as a resource - can only be a json file (no jars on classpath)
                    String readLoc = location.substring("classpath:".length());
                    InputStream ios = Thread.currentThread().getContextClassLoader().getResourceAsStream(readLoc);
                    json = readStreamToString(ios);
                } else {
                    // Load as a url
                    URL u = safeURL(location);
                    if (location.endsWith(".json")) {
                        // Just need to read the json
                        json = readStreamToString(u.openStream());
                    } else {
                        // Need to massage the classpath and load default
                        URLClassLoader newClassLoader = new URLClassLoader(new URL[]{u}, Thread.currentThread().getContextClassLoader());
                        json = readStreamToString(newClassLoader.getResourceAsStream("/lamark.json"));
                    }
                }

                if (json != null) {
                    builder = LamarkBuilderSerializer.deserialize(json);
                    availableClasses.addBuilderClasses(builder);
                    fillPanelValues();
                    // TODO: set the classes to be the same
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
    private Pane panel1() {
        Pane rval = new Pane();
        rval.getChildren().add(populationSizeLabel);
        rval.getChildren().add(crossoverProbabilityLabel);
        rval.getChildren().add(mutationProbabilityLabel);
        rval.getChildren().add(maximumPopulationsLabel);
        rval.getChildren().add(targetScoreLabel);
        rval.getChildren().add(customListenerButton);
        rval.getChildren().add(populationSize);
        rval.getChildren().add(crossoverProbability);
        rval.getChildren().add(mutationProbability);
        rval.getChildren().add(maximumPopulations);
        rval.getChildren().add(targetScore);
        rval.getChildren().add(preloadButton);

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

        Pane north = new Pane();
        //north.setLayout(new GridLayout(0, 4));
        north.getChildren().add(upperElitismLabel);
        north.getChildren().add(lowerElitismLabel);
        north.getChildren().add(randomSeedLabel);
        north.getChildren().add(upperElitism);
        north.getChildren().add(lowerElitism);
        north.getChildren().add(randomSeed);

        Pane south = new Pane();
        south.getChildren().add(new Label("Listeners"));

        lBetterIndividualFound.setSelected(true);
        lException.setSelected(true);
        lLastPopDone.setSelected(true);
        lAbort.setSelected(true);

        lLog.setSelected(false);
        lPopulationComplete.setSelected(false);
        lPopPlanDone.setSelected(false);
        lUniformPop.setSelected(false);

        south.getChildren().add(lBetterIndividualFound);
        south.getChildren().add(lException);
        south.getChildren().add(lLastPopDone);
        south.getChildren().add(lAbort);

        south.getChildren().add(lLog);
        south.getChildren().add(lPopulationComplete);
        south.getChildren().add(lPopPlanDone);
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
    private Pane classPanel() {
        Pane rval = new Pane();
        rval.getChildren().add(new Label("Classes"));
        rval.getChildren().add(supplierLabel);
        rval.getChildren().add(crossoverLabel);
        rval.getChildren().add(fitnessLabel);
        rval.getChildren().add(mutatorLabel);
        rval.getChildren().add(selectorLabel);
        rval.getChildren().add(supplier);
        rval.getChildren().add(crossover);
        rval.getChildren().add(fitness);
        rval.getChildren().add(mutator);
        rval.getChildren().add(selector);

        supplierLabel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editProperties(supplierProperties, supplierLabel.getText());
            }
        });

        crossoverLabel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editProperties(crossoverProperties, crossoverLabel.getText());
            }
        });

        fitnessLabel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editProperties(fitnessProperties, fitnessLabel.getText());
            }
        });

        mutatorLabel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editProperties(mutatorProperties, mutatorLabel.getText());
            }
        });

        selectorLabel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editProperties(selectorProperties, selectorLabel.getText());
            }
        });

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
     * Sets the values in the frop for a combo box from a properties object.
     * All properties whos key starts with the given prefix will be loaded as
     * options into the combo box.
     *
     * @param box     ComboBox to load from the properties object
     * @param classes List of class objects to load from
     * @param def     Class to default select
     */
    private void setComboBoxValues(ComboBox box, List<? extends Class> classes, Object def) {
        box.setEditable(true);

        box.getItems().retainAll(Collections.EMPTY_LIST);
        if (classes != null) {
            for (Class c : classes) {
                box.getItems().add(formatClassName(c));
            }
        } else if (def != null) {
            box.getItems().add(formatClassName(def.getClass()));
        }

        box.getSelectionModel().select(formatClassName(def.getClass()));
    }

    /**
     * Sets all values in the panel from the AvailableClasses and Builder objects
     */
    public void fillPanelValues() {

        // Init the drop boxes
        setComboBoxValues(selector, availableClasses.toClasses(availableClasses.getSelectorClassNames()), builder.getSelector());
        setComboBoxValues(supplier, availableClasses.toClasses(availableClasses.getSupplierClassNames()), builder.getSupplier());
        setComboBoxValues(crossover, availableClasses.toClasses(availableClasses.getCrossoverClassNames()), builder.getCrossover());
        setComboBoxValues(fitness, availableClasses.toClasses(availableClasses.getFitnessFunctionClassNames()), builder.getFitnessFunction());
        setComboBoxValues(mutator, availableClasses.toClasses(availableClasses.getMutatorClassNames()), builder.getMutator());

        /*supplier.setSelectedItem(formatClassName(lc.defaultSupplier()));
        crossover.setSelectedItem(formatClassName(lc.defaultCrossover()));
        fitness.setSelectedItem(formatClassName(lc.defaultFitnessFunction()));
        mutator.setSelectedItem(formatClassName(lc.defaultMutator()));
        selector.setSelectedItem(formatClassName(lc.defaultSelector()));*/
        upperElitism.setText(doubleToPercent(builder.getUpperElitismPercentage()));
        lowerElitism.setText(doubleToPercent(builder.getLowerElitismPercentage()));
        maximumPopulations.setText(ein(builder.getMaxGenerations()));
        populationSize.setText(ein(builder.getPopulationSize()));
        crossoverProbability.setText(ein(builder.getCrossoverProbability()));
        mutationProbability.setText(ein(builder.getMutationProbability()));
        targetScore.setText(ein(builder.getTargetScore()));
        /*
        randomSeed.setText(ein(builder.getRandomSeed()));

        supplierProperties = mapToProperties(lc.getSupplierConfiguration());
        crossoverProperties = mapToProperties(lc.getCrossoverConfiguration());
        fitnessProperties = mapToProperties(lc.getFitnessFunctionConfiguration());
        mutatorProperties = mapToProperties(lc.getMutatorConfiguration());
        selectorProperties = mapToProperties(lc.getSelectorConfiguration());

        preloads = new ArrayList<String>(lc.getPreCreatedIndividuals());

        customListener = new ArrayList<String>();
        for (Class c : lc.getCustomListeners()) {
            customListener.add(c.getName());
        }
        TODO: Reimpl
        */


    }

    public String toGUIConfigString() {
        return LamarkBuilderSerializer.serialize(builder);
    }

    /**
     * Converts a double 0-1 value to a percent 0-100, both in strings.
     *
     * @param value String to parse and convert.
     * @return String containing the percent value.
     */
    private String doubleToPercent(Double value) {
        Double d = new Double(value);
        d *= 100;
        return NumberFormat.getIntegerInstance().format(d);
    }

    /**
     * Converts a percent value 0-100 to double 0-1, both in strings.
     *
     * @param value String containing the percent
     * @return String containing the double
     */
    private Double percentToDouble(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        Double d = new Double(value);
        d /= 100;
        return d;
    }

    private Double nsDouble(String value) {
        return (value == null || value.trim().length() == 0) ? null : new Double(value);
    }

    private Integer nsInteger(String value) {
        return (value == null || value.trim().length() == 0) ? null : new Integer(value);
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
     * Creates a classname from the contents of the combobox.
     *
     * @param cb ComboBox to read classname from
     * @return String containing the class name
     */
    private Class classFromCombo(ComboBox cb) {
        // First, try reading it as a class name.  If it works, use that
        try {
            String test = (String) cb.getSelectionModel().getSelectedItem();
            return Thread.currentThread().getContextClassLoader().loadClass(test);
        } catch (Exception e) {
            String start = (String) cb.getSelectionModel().getSelectedItem();

            int pi = start.indexOf("[");
            String classN = start.substring(0, pi);
            String packageN = start.substring(pi + 1, start.length() - 1);
            String fullN = packageN + "." + classN;
            return availableClasses.safeLoadClass(fullN);
        }

    }

    /**
     * Converts a classname to combobox format (CLASS[PACKAGE])
     *
     * @param className String containing the name
     * @return String containing name in combo format
     */
    private String formatClassName(String className) {

        if (className == null) {
            return null;
        }
        int idx = className.lastIndexOf(".");
        if (idx == -1) {
            return className;
        }
        return className.substring(idx + 1) + "[" + className.substring(0, idx) + "]";
    }

    /**
     * Converts a classname to combobox format (CLASS[PACKAGE])
     *
     * @param clazz Class to convert
     * @return String containing name in combo format
     */
    private String formatClassName(Class clazz) {
        return (clazz == null) ? null : formatClassName(clazz.getName());
    }

    /**
     * Creates a properties object matching the state of the panel.
     *
     * @return Properties object matching the panel.
     *
    public LamarkBuilder toBuilder() {
        LamarkBuilder p = new LamarkBuilder();
        p.withSupplier((Supplier)availableClasses.safeInit(classFromCombo(supplier)));
        p.withCrossover((Function) availableClasses.safeInit(classFromCombo(crossover)));
        p.withMutator((Function)availableClasses.safeInit(classFromCombo(mutator)));
        p.withFitnessFunction((ToDoubleFunction) availableClasses.safeInit(classFromCombo(fitness)));
        p.withSelector((Selector)availableClasses.safeInit(classFromCombo(selector)));

        * TODO: Settings
        p.setSupplierConfiguration(propertiesToMap(supplierProperties));
        p.setCrossoverConfiguration(propertiesToMap(crossoverProperties));
        p.setFitnessFunctionConfiguration(propertiesToMap(fitnessProperties));
        p.setMutatorConfiguration(propertiesToMap(mutatorProperties));
        p.setSelectorConfiguration(propertiesToMap(selectorProperties));

        p.setPreCreatedIndividuals(new LinkedList<String>(preloads));
        *

        p.withUpperElitism(percentToDouble(upperElitism.getText()));
        p.withLowerElitism(percentToDouble(lowerElitism.getText()));
        p.withMaxGenerations(nsLong(maximumPopulations.getText()));
        p.withPopulationSize(nsInteger(populationSize.getText()));
        p.withCrossoverProbability(nsDouble(crossoverProbability.getText()));
        p.withMutationProbability(nsDouble(mutationProbability.getText()));
        p.withTargetScore(nsDouble(targetScore.getText()));
        //p.setRandomSeed(nsLong(randomSeed.getText()));

        *
        for (String s : customListener) {
            Class c = lamarkFactory.safeLoadClass(s);
            if (c != null) {
                p.getCustomListeners().add(c);
            }
        }
        *

        return p;
    }*/

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenAbort() {
        return lAbort.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenBetterIndividualFound() {
        return lBetterIndividualFound.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenException() {
        return lException.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenLastPopDone() {
        return lLastPopDone.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenLog() {
        return lLog.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenPopPlanDone() {
        return lPopPlanDone.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenPopulationComplete() {
        return lPopulationComplete.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     *
     * @return true if the given listener should be used
     */
    public boolean listenUniformPop() {
        return lUniformPop.isSelected();
    }

    /**
     * Accessor method.
     *
     * @return List containing the property
     */
    public List<String> getCustomListener() {
        return customListener;
    }

    /**
     * Accessor method.
     *
     * @return List containing the property
     */
    public List<String> getPreloads() {
        return preloads;
    }

    private LamarkAvailableClasses loadAvailableClasses()
    {
        LamarkAvailableClasses rval = loadAvailableClasses(System.getProperty("availableClassesConfig"));
        if (rval==null)
        {
            rval = loadAvailableClasses("/override-available-classes.json");
        }
        if (rval==null)
        {
            rval = loadAvailableClasses("default-available-classes.json");
        }
        if (rval==null)
        {
            throw new IllegalStateException("Couldn't find any available classes file");
        }
        return rval;
    }

    private LamarkAvailableClasses loadAvailableClasses(String path)
    {
        LamarkAvailableClasses rval = null;
        if (path!=null) {
            try {
                InputStream is = getClass().getResourceAsStream(path);
                if (is!=null)
                {
                    rval = MAPPER.readValue(is, LamarkAvailableClasses.class);
                }
            }
            catch (IOException ioe)
            {
                LOG.trace("Couldnt find : {}",path);
            }
        }
        return rval;
    }

    public LamarkBuilder getBuilder() {
        return builder;
    }
}
