package com.erigir.lamark.gui;

import com.erigir.lamark.DynamicMethodWrapper;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.annotation.Creator;
import com.erigir.lamark.annotation.Crossover;
import com.erigir.lamark.annotation.FitnessFunction;
import com.erigir.lamark.annotation.Mutator;
import com.erigir.lamark.config.ILamarkFactory;
import com.erigir.lamark.config.LamarkComponentFinder;
import com.erigir.lamark.selector.ISelector;
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
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Allows selection of either:
 * 1) A self-contained jar file with a defined LamarkConfiguration
 * 2) A class annotated LamarkConfiguration that can be instantiated and introspected
 * 3) Selection of all the appropriate classes and entry of runtime params to all a lamark to be instantiated
 *
 * Source: xxx
 * cr  cr  sl  ff  mt
 *
 *
 *
 * Classes annotated with Lamark Components should NOT require
 *
 * A panel for holding and organizing the various properties controls for LamarkGUI.
 * <p/>
 * Also wraps business logic for converting these controls into properties object
 * that can configure a lamark instance or be saved to disk.
 *
 * @author cweiss
 * @since 10/2007
 */
public class LamarkConfigPanel extends JPanel implements ActionListener,IGuiConfigurableLamarkFactory {
    /**
     * Logging instance *
     */
    private static final Logger LOG = LoggerFactory.getLogger(LamarkConfigPanel.class.getName());

    /**
     * This can either be a loaded or dynamically built configuration
     */
    private Lamark lamark = new Lamark();

    private Map<String,Object> runtimeParameters = new TreeMap<>();


    private List<String> preloads = new LinkedList<>();

    /**
     * Scans the classpath for options for the drop-boxes
     */
    private LamarkComponentFinder componentFinder = new LamarkComponentFinder(Arrays.asList("com.erigir"));

    /**
     * Button/Label for creator *
     */
    private JButton customPropertiesButton = new JButton("Custom Properties...");
    /**
     * Button/Label for creator *
     */
    private JLabel creatorLabel = new JLabel("Creator");
    /**
     * Button/Label for crossover *
     */
    private JLabel crossoverLabel = new JLabel("Crossover");
    /**
     * Button/Label for fitness *
     */
    private JLabel fitnessLabel = new JLabel("Fitness Function");
    /**
     * Button/Label for mutator *
     */
    private JLabel mutatorLabel = new JLabel("Mutator");
    /**
     * Button/Label for selector *
     */
    private JLabel selectorLabel = new JLabel("Selector");
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
     * Default constructor.
     * <p/>
     * Builds the layout.
     */
    public LamarkConfigPanel() {//final String configResource) {
        super();

        setLayout(new BorderLayout());
        add(classPanel(), BorderLayout.NORTH);
        add(panel1(), BorderLayout.CENTER);
        add(panel2(), BorderLayout.SOUTH);

        // Finally, initialize
       // loadFromLocation(configResource);
    }

    @Override
    public Lamark createConfiguredLamarkInstance() {
        Lamark rval = new Lamark();


        rval.setRuntimeParameters(runtimeParameters);
        rval.setCreator((DynamicMethodWrapper) creator.getSelectedItem());
        rval.setCrossover((DynamicMethodWrapper) crossover.getSelectedItem());
        rval.setFitnessFunction((DynamicMethodWrapper) fitness.getSelectedItem());
        rval.setMutator((DynamicMethodWrapper) mutator.getSelectedItem());
        rval.setSelector((ISelector) selector.getSelectedItem());
        //rval.setFormatter((DynamicMethodWrapper).getSelectedItem());
        //rval.setPreloader((DynamicMethodWrapper)preloader);
        //rval.setListeners((DynamicMethodWrapper)listeners);

        return rval;
    }

    @Override
    public String getShortDescription() {
        return "ExplicitLamarkFactory";
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
        customPropertiesButton.setEnabled(enable);
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

    public void reset() {
        lamark = new Lamark();
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

    @Override
    public void configure(Component parent) {
        JOptionPane.showMessageDialog(parent,"CONFIG!");
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
     * @return boolean true if the config panel could be loaded from resource at that location
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
                        //lamarkFactory = new LamarkFactory(newClassLoader);
                        //json = readStreamToString(lamarkFactory.getClassLoader().getResourceAsStream("lamark.json"));
                    }
                }

                if (json != null) {

                    /*
                    Map<String, LamarkGUIConfig> configs = lamarkFactory.jsonToConfig(json);

                    // If more than one and not specified, then pop the selection box
                    LamarkGUIConfig selected = null;
                    if (optionalEntryName != null) {
                        selected = configs.get(optionalEntryName);
                    } else if (configs.size() == 1) {
                        selected = configs.values().iterator().next();
                    } else {
                        Set<String> options = configs.keySet();

                        String s = (String) JOptionPane.showInputDialog(
                                null,
                                "Options:",
                                "Select Algorithm",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                options.toArray(),
                                null);

                        if (s != null && s.length() > 0) {
                            selected = configs.get(s);
                        }
                    }

                    if (selected != null) {
                        fromGUIConfig(selected);
                    } else {
                        JOptionPane.showMessageDialog(null, "No config selected.  Nothing happened.");
                    }
                    */

                } else {
                    throw new IllegalStateException("Failed to load configuration JSON file");
                }
            }
        } catch (IOException ioe) {
            LOG.error("Error reading location {}", location, ioe);
        }

        initializeConfig();

    }

    private void initializeConfig()
    {
        List<DynamicMethodWrapper<Creator>> creators = componentFinder.listAsWrappers(Creator.class);
        setComboBoxValues(creator, creators, creators.get(0), Creator.class);

        List<DynamicMethodWrapper<Crossover>> crossovers = componentFinder.listAsWrappers(Crossover.class);
        setComboBoxValues(crossover, crossovers, crossovers.get(0), Crossover.class);

        List<DynamicMethodWrapper<FitnessFunction>> fitnessFunctions = componentFinder.listAsWrappers(FitnessFunction.class);
        setComboBoxValues(fitness, fitnessFunctions, fitnessFunctions.get(0), FitnessFunction.class);

        List<DynamicMethodWrapper<Mutator>> mutators = componentFinder.listAsWrappers(Mutator.class);
        setComboBoxValues(mutator, mutators, mutators.get(0), Mutator.class);

        selector.setEditable(true);

        selector.removeAllItems();
        for (Class<? extends ISelector> c:componentFinder.getSelectors())
        {
            selector.addItem(c.getSimpleName());
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
     * @return JPanel containing the first row.
     */
    private JPanel panel1() {
        JPanel rval = new JPanel();
        rval.setLayout(new GridLayout(0, 5));
        rval.add(populationSizeLabel);
        rval.add(crossoverProbabilityLabel);
        rval.add(mutationProbabilityLabel);
        rval.add(maximumPopulationsLabel);
        rval.add(targetScoreLabel);
        rval.add(populationSize);
        rval.add(crossoverProbability);
        rval.add(mutationProbability);
        rval.add(maximumPopulations);
        rval.add(targetScore);

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

        north.add(customListenerButton);
        north.add(customPropertiesButton);
        north.add(preloadButton);

        customPropertiesButton.addActionListener(this);
        customListenerButton.addActionListener(this);
        preloadButton.addActionListener(this);


        JPanel south = new JPanel();
        south.setBorder(BorderFactory.createTitledBorder("Listeners"));

        lBetterIndividualFound.setSelected(true);
        lException.setSelected(true);
        lLastPopDone.setSelected(true);
        lAbort.setSelected(true);

        lLog.setSelected(false);
        lPopulationComplete.setSelected(false);
        lPopPlanDone.setSelected(false);
        lUniformPop.setSelected(false);

        south.add(lBetterIndividualFound);
        south.add(lException);
        south.add(lLastPopDone);
        south.add(lAbort);

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
        rval.setBorder(BorderFactory.createTitledBorder("Components"));
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

        return rval;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        JButton src = (JButton) arg0.getSource();
        if (src == customPropertiesButton) {
            //runtimeParameters = editProperties(mapToProperties(runtimeParameters), src.getText());
        } else if (src == preloadButton) {
            preloads = editStringList(preloads, src.getText());
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
     * Sets the values in the drop for a combo box.
     *
     * @param box     JComboBox to load from the properties object
     * @param wrappers List of DynamicMethodWrapper objects to load from
     * @param def     DynamicMethodWrapper to default select
     */
    private <T> void setComboBoxValues(JComboBox box, List<DynamicMethodWrapper<T>> wrappers, DynamicMethodWrapper def, Class<T> annotationClass) {
        box.setEditable(true);

        box.removeAllItems();
        if (wrappers != null) {
            for (DynamicMethodWrapper c : wrappers) {
                box.addItem(formatDynamicMethodWrapper(c));
            }
        } else if (def != null) {
            box.addItem(formatDynamicMethodWrapper(def));
        }

        box.setSelectedItem(formatDynamicMethodWrapper(def));
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
            String fullN = packageN + "." + classN;
            return null;//lamarkFactory.safeLoadClass(fullN);
        }

    }

    /**
     * Converts a classname to combobox format (CLASS[PACKAGE])
     *
     * @param dmw String containing DynamicMethodWrapper to convert
     * @return String containing name in combo format
     */
    private String formatDynamicMethodWrapper(DynamicMethodWrapper dmw) {
        if (dmw == null) {
            return null;
        }

        return dmw.getObject().getClass().getSimpleName()+" : "+dmw.getMethod().getName();

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
    public List<String> getPreloads() {
        return preloads;
    }

    public Lamark createLamarkInstance(LamarkGui gui)
    {
        return lamark;
    }


}
