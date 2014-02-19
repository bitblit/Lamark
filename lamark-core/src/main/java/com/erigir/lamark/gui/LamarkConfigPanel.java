package com.erigir.lamark.gui;

import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkFactory;
import com.erigir.lamark.config.LamarkGUIConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * A panel for holding and organizing the various properties controls for LamarkGUI.
 * <p/>
 * Also wraps business logic for converting these controls into properties object
 * that can configure a lamark instance or be saved to disk.
 *
 * @author cweiss
 * @since 10/2007
 */
public class LamarkConfigPanel extends JPanel implements ActionListener {
    /**
     * Logging instance *
     */
    private static final Logger LOG = LoggerFactory.getLogger(LamarkConfigPanel.class.getName());
    /**
     * Location of the default file
     */
    private static final String DEFAULT_CONFIG_LOCATION = "classpath:com/erigir/lamark/gui/default-lamark.json";

    /**
     * Handle to the default classloader, for resets
     */
    private static final ClassLoader DEFAULT_CLASS_LOADER = LamarkConfigPanel.class.getClassLoader();

    /**
     * Factory is used for serialization
     */
    private LamarkFactory lamarkFactory = new LamarkFactory();

    /**
     * Custom properties for creator *
     */
    private Properties creatorProperties = new Properties();
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
     * Button/Label for creator *
     */
    private JButton creatorLabel = new JButton("Creator");
    /**
     * Button/Label for crossover *
     */
    private JButton crossoverLabel = new JButton("Crossover");
    /**
     * Button/Label for fitness *
     */
    private JButton fitnessLabel = new JButton("Fitness Function");
    /**
     * Button/Label for mutator *
     */
    private JButton mutatorLabel = new JButton("Mutator");
    /**
     * Button/Label for selector *
     */
    private JButton selectorLabel = new JButton("Selector");
    /**
     * Button/Label for preloads *
     */
    private JButton preloadButton = new JButton("Preloads...");
    /**
     * Button/Label for custom listeners *
     */
    private JButton customListenerButton = new JButton("Custom Listeners...");
    /**
     * Label for upper elitism control *
     */
    private JLabel upperElitismLabel = new JLabel("Upper Elitism (%)");
    /**
     * Label for lower elitism control *
     */
    private JLabel lowerElitismLabel = new JLabel("Lower Elitism (%)");
    /**
     * Label for max pop control *
     */
    private JLabel maximumPopulationsLabel = new JLabel("Max. Populations (Blank for cont.)");
    /**
     * Label for pop size control *
     */
    private JLabel populationSizeLabel = new JLabel("Population Size");
    /**
     * Label for crossover prob control *
     */
    private JLabel crossoverProbabilityLabel = new JLabel("Crossover Prob (0-1)");
    /**
     * Label for mutation prob control *
     */
    private JLabel mutationProbabilityLabel = new JLabel("Mutation Prob (0-1)");
    /**
     * Label for no. of threads control *
     */
    private JLabel numberOfWorkerThreadsLabel = new JLabel("Number of worker threads");
    /**
     * Label for target score control *
     */
    private JLabel targetScoreLabel = new JLabel("Target Score (Blank for cont.)");
    /**
     * Label for random seed control *
     */
    private JLabel randomSeedLabel = new JLabel("Random Seed");

    /**
     * Better individual listener enabler *
     */
    private JCheckBox lBetterIndividualFound = new JCheckBox("Better Individual Found");
    /**
     * Exception listener enabler *
     */
    private JCheckBox lException = new JCheckBox("Exception");
    /**
     * Last Population listener enabler *
     */
    private JCheckBox lLastPopDone = new JCheckBox("Last Population Done");
    /**
     * Abort listener enabler *
     */
    private JCheckBox lAbort = new JCheckBox("Aborted");
    /**
     * Configuration listener enabler *
     */
    private JCheckBox lConfiguration = new JCheckBox("Configuration");
    /**
     * Log listener enabler *
     */
    private JCheckBox lLog = new JCheckBox("Log");
    /**
     * Population Done listener enabler *
     */
    private JCheckBox lPopulationComplete = new JCheckBox("Population Done");
    /**
     * Population Plan listener enabler *
     */
    private JCheckBox lPopPlanDone = new JCheckBox("Population Plan Done");
    /**
     * Uniform Population listener enabler *
     */
    private JCheckBox lUniformPop = new JCheckBox("Uniform Population");

    /**
     * Combobox holding creator name *
     */
    private JComboBox creator = new JComboBox();
    /**
     * Combobox holding crossover name *
     */
    private JComboBox crossover = new JComboBox();
    /**
     * Combobox holding fitness name *
     */
    private JComboBox fitness = new JComboBox();
    /**
     * Combobox holding mutator name *
     */
    private JComboBox mutator = new JComboBox();
    /**
     * Combobox holding selector name *
     */
    private JComboBox selector = new JComboBox();
    /**
     * Edit box holding upper elitism *
     */
    private JTextField upperElitism = new JTextField();
    /**
     * Edit box holding lower elitism *
     */
    private JTextField lowerElitism = new JTextField();
    /**
     * Edit box holding maximum populations *
     */
    private JTextField maximumPopulations = new JTextField();
    /**
     * Edit box holding population size *
     */
    private JTextField populationSize = new JTextField();
    /**
     * Edit box holding crossover probability *
     */
    private JTextField crossoverProbability = new JTextField();
    /**
     * Edit box holding mutation probability *
     */
    private JTextField mutationProbability = new JTextField();
    /**
     * Edit box holding no. of worker threads *
     */
    private JTextField numberOfWorkerThreads = new JTextField();
    /**
     * Edit box holding target score *
     */
    private JTextField targetScore = new JTextField();
    /**
     * Edit box holding random seed *
     */
    private JTextField randomSeed = new JTextField();

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enable) {
        creatorLabel.setEnabled(enable);
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

        lConfiguration.setEnabled(enable);
        lLog.setEnabled(enable);
        lPopulationComplete.setEnabled(enable);
        lPopPlanDone.setEnabled(enable);
        lUniformPop.setEnabled(enable);

        creator.setEnabled(enable);
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
        numberOfWorkerThreads.setEnabled(enable);
        targetScore.setEnabled(enable);
        randomSeed.setEnabled(enable);
    }

    public void reset()
    {
        loadFromLocation(DEFAULT_CONFIG_LOCATION,null);
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
     * @param optionalEntryName String optionally containing which entry in the json file to use
     * @return boolean true if the config panel could be loaded from resource at that location
     */

    public boolean loadFromLocation(String location, String optionalEntryName) {
        boolean rval = false;
        try {
            String json = null;

            if (location != null) {
                if (location.startsWith("classpath:")) {
                    // If its on the classpath, reset to the default class loader
                    Thread.currentThread().setContextClassLoader(DEFAULT_CLASS_LOADER);

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
                        Thread.currentThread().setContextClassLoader(new URLClassLoader(new URL[]{u}, Thread.currentThread().getContextClassLoader()));
                        json = readStreamToString(Thread.currentThread().getContextClassLoader().getResourceAsStream("lamark.json"));
                    }
                }

                if (json != null) {

                    Map<String, LamarkGUIConfig> configs = lamarkFactory.jsonToConfig(json);

                    // If more than one and not specified, then pop the selection box
                    LamarkGUIConfig selected = null;
                    if (optionalEntryName != null) {
                        selected = configs.get(optionalEntryName);
                    } else if (configs.size() == 1) {
                        selected = configs.values().iterator().next();
                    } else {
                        Set<String> options = configs.keySet();

                        String s = (String)JOptionPane.showInputDialog(
                                null,
                                "Options:",
                                "Select Algorithm",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                options.toArray(),
                                null);

                        if (s!=null && s.length()>0)
                        {
                            selected = configs.get(s);
                        }
                    }
                    fromGUIConfig(selected);

                    rval = true;
                }
            }
        } catch (IOException ioe) {
            LOG.error("Error reading location {}", location, ioe);
        }

        if (!rval) {
            JOptionPane.showMessageDialog(null, "Error trying to load config");
        }

        return rval;

    }

    private URL safeURL(String input) {
        try {
            return new URL(input);
        } catch (MalformedURLException mue) {
            throw new RuntimeException("Unable to read url", mue);
        }
    }

    /**
     * Default constructor.
     * <p/>
     * Builds the layout.
     */
    public LamarkConfigPanel(final String inConfigResource, final String inSelectedItem) {
        super();

        setLayout(new BorderLayout());
        add(classPanel(), BorderLayout.NORTH);
        add(panel1(), BorderLayout.CENTER);
        add(panel2(), BorderLayout.SOUTH);

        // Finally, initialize
        String configResource = (inConfigResource == null) ? DEFAULT_CONFIG_LOCATION : inConfigResource;

        loadFromLocation(configResource, inSelectedItem);
    }

    /**
     * Lays out and returns the first panel
     *
     * @return JPanel containing the first row.
     */
    private JPanel panel1() {
        JPanel rval = new JPanel();
        rval.setLayout(new GridLayout(0, 6));
        rval.add(populationSizeLabel);
        rval.add(crossoverProbabilityLabel);
        rval.add(mutationProbabilityLabel);
        rval.add(maximumPopulationsLabel);
        rval.add(targetScoreLabel);
        rval.add(customListenerButton);
        rval.add(populationSize);
        rval.add(crossoverProbability);
        rval.add(mutationProbability);
        rval.add(maximumPopulations);
        rval.add(targetScore);
        rval.add(preloadButton);
        customListenerButton.addActionListener(this);
        preloadButton.addActionListener(this);
        return rval;

    }

    /**
     * Lays out and returns the second panel.
     *
     * @return JPanel containing the second row.
     */
    private JPanel panel2() {
        JPanel rval = new JPanel();
        rval.setLayout(new BorderLayout());

        JPanel north = new JPanel();
        north.setLayout(new GridLayout(0, 4));
        north.add(upperElitismLabel);
        north.add(lowerElitismLabel);
        north.add(numberOfWorkerThreadsLabel);
        north.add(randomSeedLabel);
        north.add(upperElitism);
        north.add(lowerElitism);
        north.add(numberOfWorkerThreads);
        north.add(randomSeed);

        JPanel south = new JPanel();
        south.setBorder(BorderFactory.createTitledBorder("Listeners"));

        lBetterIndividualFound.setSelected(true);
        lException.setSelected(true);
        lLastPopDone.setSelected(true);
        lAbort.setSelected(true);

        lConfiguration.setSelected(false);
        lLog.setSelected(false);
        lPopulationComplete.setSelected(false);
        lPopPlanDone.setSelected(false);
        lUniformPop.setSelected(false);

        south.add(lBetterIndividualFound);
        south.add(lException);
        south.add(lLastPopDone);
        south.add(lAbort);

        south.add(lConfiguration);
        south.add(lLog);
        south.add(lPopulationComplete);
        south.add(lPopPlanDone);
        south.add(lUniformPop);

        rval.add(north, BorderLayout.NORTH);
        rval.add(south, BorderLayout.SOUTH);

        return rval;
    }

    /**
     * Builds and lays out the compoennt class panel.
     *
     * @return JPanel containing the component class edit boxes.
     */
    private JPanel classPanel() {
        JPanel rval = new JPanel();
        rval.setBorder(BorderFactory.createTitledBorder("Classes"));
        rval.setLayout(new GridLayout(0, 5));
        rval.add(creatorLabel);
        rval.add(crossoverLabel);
        rval.add(fitnessLabel);
        rval.add(mutatorLabel);
        rval.add(selectorLabel);
        rval.add(creator);
        rval.add(crossover);
        rval.add(fitness);
        rval.add(mutator);
        rval.add(selector);

        creatorLabel.addActionListener(this);
        crossoverLabel.addActionListener(this);
        fitnessLabel.addActionListener(this);
        mutatorLabel.addActionListener(this);
        selectorLabel.addActionListener(this);

        return rval;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        JButton src = (JButton) arg0.getSource();
        if (src == creatorLabel) {
            creatorProperties = editProperties(creatorProperties, src.getText());
        } else if (src == crossoverLabel) {
            crossoverProperties = editProperties(crossoverProperties, src.getText());
        } else if (src == fitnessLabel) {
            fitnessProperties = editProperties(fitnessProperties, src.getText());
        } else if (src == mutatorLabel) {
            mutatorProperties = editProperties(mutatorProperties, src.getText());
        } else if (src == selectorLabel) {
            selectorProperties = editProperties(selectorProperties, src.getText());
        } else if (src == preloadButton) {
            preloads = editStringList(preloads, src.getText());
        } else if (src == customListenerButton) {
            customListener = editStringList(customListener, src.getText());
        }
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
     * @param box     JComboBox to load from the properties object
     * @param classes List of class objects to load from
     * @param def     Class to default select
     */
    private void setComboBoxValues(JComboBox box, List<? extends Class> classes, Class def) {
        box.setEditable(true);

        box.removeAllItems();
        if (classes!=null)
        {
            for (Class c : classes) {
                box.addItem(formatClassName(c));
            }
        }
        else if (def!=null)
        {
            box.addItem(formatClassName(def));
        }

        box.setSelectedItem(formatClassName(def));
    }

    /**
     * Sets all values in the panel from the config object.
     *
     * @param lc Config object to load from.
     */
    public void fromGUIConfig(LamarkGUIConfig lc) {

        // Init the drop boxes
        setComboBoxValues(selector, lc.getSelectorClasses(), lc.defaultSelector());
        setComboBoxValues(creator, lc.getCreatorClasses(), lc.defaultCreator());
        setComboBoxValues(crossover, lc.getCrossoverClasses(), lc.defaultCrossover());
        setComboBoxValues(fitness, lc.getFitnessFunctionClasses(), lc.defaultFitnessFunction());
        setComboBoxValues(mutator, lc.getMutatorClasses(), lc.defaultMutator());


        creator.setSelectedItem(formatClassName(lc.defaultCreator()));
        crossover.setSelectedItem(formatClassName(lc.defaultCrossover()));
        fitness.setSelectedItem(formatClassName(lc.defaultFitnessFunction()));
        mutator.setSelectedItem(formatClassName(lc.defaultMutator()));
        selector.setSelectedItem(formatClassName(lc.defaultSelector()));
        upperElitism.setText(doubleToPercent(lc.getUpperElitism()));
        lowerElitism.setText(doubleToPercent(lc.getLowerElitism()));
        maximumPopulations.setText(ein(lc.getMaximumPopulations()));
        populationSize.setText(ein(lc.getPopulationSize()));
        crossoverProbability.setText(ein(lc.getCrossoverProbability()));
        mutationProbability.setText(ein(lc.getMutationProbability()));
        numberOfWorkerThreads.setText(ein(lc.getNumberOfWorkerThreads()));
        targetScore.setText(ein(lc.getTargetScore()));
        randomSeed.setText(ein(lc.getRandomSeed()));

        creatorProperties = mapToProperties(lc.getCreatorConfiguration());
        crossoverProperties = mapToProperties(lc.getCrossoverConfiguration());
        fitnessProperties = mapToProperties(lc.getFitnessFunctionConfiguration());
        mutatorProperties = mapToProperties(lc.getMutatorConfiguration());
        selectorProperties = mapToProperties(lc.getSelectorConfiguration());

        preloads = new ArrayList<String>(lc.getPreCreatedIndividuals());

        customListener = new ArrayList<String>();
        for (Class c : lc.getCustomListeners()) {
            customListener.add(c.getName());
        }


    }

    public String toGUIConfigString() {
        return lamarkFactory.convertToJson(toGUIConfigObject());
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
        return (value == null) ? null : new Long(value);
    }

    private Class safeForName(String value) {
        Class rval = null;
        try {
            rval = Thread.currentThread().getContextClassLoader().loadClass(value);
        } catch (ClassNotFoundException cnf) {
            LOG.warn("Couldnt find class {} - returning null", value);
            rval = null;
        }
        return rval;
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
    private Class classFromCombo(JComboBox cb) {
        // First, try reading it as a class name.  If it works, use that
        try {
            String test = (String) cb.getSelectedItem();
            return Thread.currentThread().getContextClassLoader().loadClass(test);
        } catch (Exception e) {
            String start = (String) cb.getSelectedItem();

            int pi = start.indexOf("[");
            String classN = start.substring(0, pi);
            String packageN = start.substring(pi + 1, start.length() - 1);
            String fullN = packageN+"."+classN;
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(fullN);
            } catch (ClassNotFoundException cnf) {
                throw new IllegalArgumentException("Cant find class " + cb.getSelectedItem());
            }
        }

    }

    /**
     * Converts a classname to combobox format (CLASS[PACKAGE])
     *
     * @param className String containing classname to convert
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
     */
    public LamarkGUIConfig toGUIConfigObject() {
        LamarkGUIConfig p = new LamarkGUIConfig();
        p.setCreatorClass(classFromCombo(creator));
        p.setCrossoverClass(classFromCombo(crossover));
        p.setFitnessFunctionClass(classFromCombo(fitness));
        p.setMutatorClass(classFromCombo(mutator));
        p.setSelectorClass(classFromCombo(selector));

        p.setUpperElitism(percentToDouble(upperElitism.getText()));
        p.setLowerElitism(percentToDouble(lowerElitism.getText()));
        p.setMaximumPopulations(nsInteger(maximumPopulations.getText()));
        p.setPopulationSize(nsInteger(populationSize.getText()));
        p.setCrossoverProbability(nsDouble(crossoverProbability.getText()));
        p.setMutationProbability(nsDouble(mutationProbability.getText()));
        p.setNumberOfWorkerThreads(nsInteger(numberOfWorkerThreads.getText()));
        p.setTargetScore(nsDouble(targetScore.getText()));
        p.setRandomSeed(nsLong(randomSeed.getText()));

        p.setCreatorConfiguration(propertiesToMap(creatorProperties));
        p.setCrossoverConfiguration(propertiesToMap(crossoverProperties));
        p.setFitnessFunctionConfiguration(propertiesToMap(fitnessProperties));
        p.setMutatorConfiguration(propertiesToMap(mutatorProperties));
        p.setSelectorConfiguration(propertiesToMap(selectorProperties));

        p.setPreCreatedIndividuals(new LinkedList<String>(preloads));

        for (String s : customListener) {
            Class c = safeForName(s);
            if (c != null) {
                p.getCustomListeners().add(c);
            }
        }

        return p;
    }

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
    public boolean listenConfiguration() {
        return lConfiguration.isSelected();
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

    public LamarkFactory getLamarkFactory() {
        return lamarkFactory;
    }

    public void setLamarkFactory(LamarkFactory lamarkFactory) {
        this.lamarkFactory = lamarkFactory;
    }

    public Lamark createLamarkInstance(Component optionalParentComponent) {
        return lamarkFactory.createLamarkFromConfig(toGUIConfigObject(), optionalParentComponent);
    }

}
