package com.erigir.lamark.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.erigir.lamark.EComponent;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkFactory;
import com.erigir.lamark.events.LamarkEventListener;

/**
 * A panel for holding and organizing the various properties controls for LamarkGUI.
 * 
 * Also wraps business logic for converting these controls into properties object
 * that can configure a lamark instance or be saved to disk.
 * 
 * @author cweiss
 * @since 10/2007
 */
public class LamarkConfigPanel extends JPanel implements ActionListener
{
    /** Logging instance **/
    private static final Logger LOG = Logger.getLogger(LamarkConfigPanel.class.getName());
    
    /** Custom properties for creator **/
    private Properties creatorProperties = new Properties();
    /** Custom properties for crossover **/
    private Properties crossoverProperties = new Properties();
    /** Custom properties for fitness **/
    private Properties fitnessProperties = new Properties();
    /** Custom properties for mutator **/
    private Properties mutatorProperties = new Properties();
    /** Custom properties for selector **/
    private Properties selectorProperties = new Properties();
    
    /** List of strings to convert into preload individuals **/
    private List<String> preloads = new ArrayList<String>();
    /** List of custom listeners to instantiate **/
    private List<String> customListener = new ArrayList<String>();
    
    /** Button/Label for creator **/
    private JButton creatorLabel = new JButton("Creator");
    /** Button/Label for crossover **/
    private JButton crossoverLabel = new JButton("Crossover");
    /** Button/Label for fitness **/
    private JButton fitnessLabel = new JButton("Fitness Function");
    /** Button/Label for mutator **/
    private JButton mutatorLabel = new JButton("Mutator");
    /** Button/Label for selector **/
    private JButton selectorLabel = new JButton("Selector");
    /** Button/Label for preloads **/
    private JButton preloadButton = new JButton("Preloads...");
    /** Button/Label for custom listeners **/
    private JButton customListenerButton = new JButton("Custom Listeners...");
    /** Label for upper elitism control **/
    private JLabel upperElitismLabel = new JLabel("Upper Elitism (%)");
    /** Label for lower elitism control **/
    private JLabel lowerElitismLabel = new JLabel("Lower Elitism (%)");
    /** Label for max pop control **/
    private JLabel maximumPopulationsLabel = new JLabel("Max. Populations (Blank for cont.)");
    /** Label for pop size control **/
    private JLabel populationSizeLabel = new JLabel("Population Size");
    /** Label for crossover prob control **/
    private JLabel crossoverProbabilityLabel = new JLabel("Crossover Prob (0-1)");
    /** Label for mutation prob control **/
    private JLabel mutationProbabilityLabel = new JLabel("Mutation Prob (0-1)");
    /** Label for no. of threads control **/
    private JLabel numberOfWorkerThreadsLabel = new JLabel("Number of worker threads");
    /** Label for target score control **/
    private JLabel targetScoreLabel = new JLabel("Target Score (Blank for cont.)");
    /** Label for random seed control **/
    private JLabel randomSeedLabel = new JLabel("Random Seed");
    
    /** Better individual listener enabler **/
    private JCheckBox lBetterIndividualFound = new JCheckBox("Better Individual Found");
    /** Exception listener enabler **/
    private JCheckBox lException = new JCheckBox("Exception");
    /** Last Population listener enabler **/
    private JCheckBox lLastPopDone = new JCheckBox("Last Population Done");
    /** Abort listener enabler **/
    private JCheckBox lAbort = new JCheckBox("Aborted");
    /** Configuration listener enabler **/
    private JCheckBox lConfiguration = new JCheckBox("Configuration");
    /** Log listener enabler **/
    private JCheckBox lLog = new JCheckBox("Log");
    /** Population Done listener enabler **/
    private JCheckBox lPopulationComplete = new JCheckBox("Population Done");
    /** Population Plan listener enabler **/
    private JCheckBox lPopPlanDone = new JCheckBox("Population Plan Done");
    /** Uniform Population listener enabler **/
    private JCheckBox lUniformPop = new JCheckBox("Uniform Population");

    /** Combobox holding creator name **/
    private JComboBox creator = new JComboBox();
    /** Combobox holding crossover name **/
    private JComboBox crossover = new JComboBox();
    /** Combobox holding fitness name **/
    private JComboBox fitness = new JComboBox();
    /** Combobox holding mutator name **/
    private JComboBox mutator = new JComboBox();
    /** Combobox holding selector name **/
    private JComboBox selector = new JComboBox();
    /** Edit box holding upper elitism **/
    private JTextField upperElitism = new JTextField();
    /** Edit box holding lower elitism **/
    private JTextField lowerElitism = new JTextField();
    /** Edit box holding maximum populations **/
    private JTextField maximumPopulations = new JTextField();
    /** Edit box holding population size **/
    private JTextField populationSize = new JTextField();
    /** Edit box holding crossover probability **/
    private JTextField crossoverProbability = new JTextField();
    /** Edit box holding mutation probability **/
    private JTextField mutationProbability = new JTextField();
    /** Edit box holding no. of worker threads **/
    private JTextField numberOfWorkerThreads = new JTextField();
    /** Edit box holding target score **/
    private JTextField targetScore = new JTextField();
    /** Edit box holding random seed **/
    private JTextField randomSeed = new JTextField();

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enable)
    {
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
    
    /**
     * Default constructor.
     * 
     * Builds the layout.
     */
    public LamarkConfigPanel()
    {
        super();
        
        setLayout(new BorderLayout());
        add(classPanel(),BorderLayout.NORTH);
        add(panel1(),BorderLayout.CENTER);
        add(panel2(),BorderLayout.SOUTH);
        
    }
    
    /**
     * Lays out and returns the first panel
     * @return JPanel containing the first row.
     */
    private JPanel panel1()
    {
        JPanel rval = new JPanel();
        rval.setLayout(new GridLayout(0,6));
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
     * @return JPanel containing the second row.
     */
    private JPanel panel2()
    {
        JPanel rval = new JPanel();
        rval.setLayout(new BorderLayout());
        
        JPanel north = new JPanel();
        north.setLayout(new GridLayout(0,4));
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

        rval.add(north,BorderLayout.NORTH);
        rval.add(south,BorderLayout.SOUTH);
        
        return rval;
    }
    
    /**
     * Builds and lays out the compoennt class panel.
     * @return JPanel containing the component class edit boxes.
     */
    private JPanel classPanel()
    {
        JPanel rval = new JPanel();
        rval.setBorder(BorderFactory.createTitledBorder("Classes"));
        rval.setLayout(new GridLayout(0,5));
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
    public void actionPerformed(ActionEvent arg0)
    {
        JButton src = (JButton)arg0.getSource();
        if (src==creatorLabel)
        {
            creatorProperties=editProperties(creatorProperties,src.getText());
        }
        else if (src==crossoverLabel)
        {
            crossoverProperties=editProperties(crossoverProperties,src.getText());
        }
        else if (src==fitnessLabel)
        {
            fitnessProperties=editProperties(fitnessProperties,src.getText());
        }
        else if (src==mutatorLabel)
        {
            mutatorProperties=editProperties(mutatorProperties,src.getText());
        }
        else if (src==selectorLabel)
        {
            selectorProperties=editProperties(selectorProperties,src.getText());
        }
        else if (src==preloadButton)
        {
            preloads = editStringList(preloads,src.getText());
        }
        else if (src==customListenerButton)
        {
            customListener = editStringList(customListener,src.getText());
        }
    }

    /** 
     * Opens a dialog box to edit a string list
     * @param p List of strings to edit
     * @param label String containing label for dialog box
     * @return List of strings after edit
     */
    private List<String> editStringList(List<String> p,String label)
    {
        StringListDialog pd = new StringListDialog(label,p);
        pd.setVisible(true);
        return pd.getData();
    }

    /**
     * Opens a dialog box to edit a properties object
     * @param p Properties object to edit
     * @param label String label for the dialog box
     * @return Properties object after edit
     */
    private Properties editProperties(Properties p,String label)
    {
        PropertiesDialog pd = new PropertiesDialog(label,p);
        pd.setVisible(true);
        return pd.getProperties();
    }

    /**
     * Searches the classpath for config files, and if found uses them to load the boxes.
     */
    public void initializeLists()
    {
        try
        {
            InputStream is = getClass().getResourceAsStream("/LamarkGuiConfig.properties");
            if (is==null)
            {
                is = getClass().getResourceAsStream("DefaultLamarkGuiConfig.properties");
            }
            if (is!=null)
            {
                Properties p = new Properties();
                p.load(is);

                // If any standard properties were specified, set them
                fromProperties(p);

                setComboBoxValues(selector,p,"selectorClass");
                setComboBoxValues(creator,p,"creatorClass");
                setComboBoxValues(crossover,p,"crossoverClass");
                setComboBoxValues(fitness,p,"fitnessClass");
                setComboBoxValues(mutator,p,"mutatorClass");
                
                
            }
        }
        catch (Exception e)
        {
            LOG.warning("Error while trying to read config panel properties:"+e);
        }
    }
    
    /**
     * Sets the values in the frop for a combo box from a properties object.
     * All properties whos key starts with the given prefix will be loaded as
     * options into the combo box.
     * @param box JComboBox to load from the properties object
     * @param p Properties object to load from
     * @param prefix String containing the prefix of properties to load into the combo box
     */
    private void setComboBoxValues(JComboBox box,Properties p, String prefix)
    {
        box.setEditable(true);
        
        box.removeAllItems();
        for (String s:loadStringsWithPrefix(p,prefix))
        {
            box.addItem(formatClassName(s));
        }
        
        
        String def = p.getProperty(prefix+".default");
        if (def!=null)
        {
            box.setSelectedItem(formatClassName(def));
        }
    }
    
    /**
     * Creates a string[] of all properties with the given key prefix.
     * @param p Properties object to extract values from
     * @param prefix String containing prefix to select on
     * @return String[] extracted from properties
     */
    private String[] loadStringsWithPrefix(Properties p, String prefix)
    {
        // of the form prefix.number
        int i=0;
        List<String> rval = new LinkedList<String>();
        String data=p.getProperty(prefix+"."+i);
        while (data!=null)
        {
            rval.add(data);
            i++;
            data=p.getProperty(prefix+"."+i);
        }
        return rval.toArray(new String[0]);
    }
    
    /**
     * Generates and instantiates the list of custom listeners defined.
     * @param cl Classloader to load any listeners from
     * @param logTo Lamark instance to log any messages to
     * @return List of LamarkEventListeners generated
     */
    public List<LamarkEventListener> customListeners(ClassLoader cl,Lamark logTo)
    {
        LinkedList<LamarkEventListener> rval = new LinkedList<LamarkEventListener>();
        for (String s:customListener)
        {
            Object o = LamarkFactory.badClassNameToNull(s, cl);
            if (o!=null)
            {
                if (LamarkEventListener.class.isAssignableFrom(o.getClass()))
                {
                    rval.add((LamarkEventListener)o);
                }
                else
                {
                    logTo.logWarning(s+" exists but isnt a lamarkeventlistener");
                }
            }
            else
            {
                logTo.logWarning("Couldnt create custom listener : "+s);
            }
        }
        
        return rval;
    }
    
    /**
     * Sets all values in the panel from the properties object.
     * @param p Properties object to load from.
     */
    public void fromProperties(Properties p)
    {
        creator.setSelectedItem(formatClassName(nie(p.getProperty(EComponent.CREATOR.getClassProperty()))));
        crossover.setSelectedItem(formatClassName(nie(p.getProperty(EComponent.CROSSOVER.getClassProperty()))));
        fitness.setSelectedItem(formatClassName(nie(p.getProperty(EComponent.FITNESSFUNCTION.getClassProperty()))));
        mutator.setSelectedItem(formatClassName(nie(p.getProperty(EComponent.MUTATOR.getClassProperty()))));
        selector.setSelectedItem(formatClassName(nie(p.getProperty(EComponent.SELECTOR.getClassProperty()))));
        upperElitism.setText(doubleToPercent(nie(p.getProperty(LamarkFactory.UPPER_ELITISM_KEY))));
        lowerElitism.setText(doubleToPercent(nie(p.getProperty(LamarkFactory.LOWER_ELITISM_KEY))));
        maximumPopulations.setText(nie(p.getProperty(LamarkFactory.MAXIMUM_POPULATION_KEY)));
        populationSize.setText(nie(p.getProperty(LamarkFactory.POPULATION_SIZE_KEY)));
        crossoverProbability.setText(nie(p.getProperty(LamarkFactory.CROSSOVER_PROBABILITY_KEY)));
        mutationProbability.setText(nie(p.getProperty(LamarkFactory.MUTATION_PROBABILITY_KEY)));
        numberOfWorkerThreads.setText(nie(p.getProperty(LamarkFactory.NUMBER_OF_WORKER_THREADS_KEY)));
        targetScore.setText(nie(p.getProperty(LamarkFactory.TARGET_SCORE_KEY)));
        randomSeed.setText(nie(p.getProperty(LamarkFactory.RANDOM_SEED_KEY)));
        
        creatorProperties = EComponent.CREATOR.extractComponentProperties(p);
        crossoverProperties = EComponent.CROSSOVER.extractComponentProperties(p);
        fitnessProperties = EComponent.FITNESSFUNCTION.extractComponentProperties(p);
        mutatorProperties = EComponent.MUTATOR.extractComponentProperties(p);
        selectorProperties = EComponent.SELECTOR.extractComponentProperties(p);
        
        
        preloads = new ArrayList<String>();
        customListener = new ArrayList<String>();
        for (Object key:p.keySet())
        {
            String k = (String)key;
            if (k.startsWith(LamarkFactory.PRELOAD_PREFIX))
            {
                preloads.add(p.getProperty(k));
            }
            else if (k.startsWith(LamarkFactory.CUSTOM_LISTENER_PREFIX))
            {
                customListener.add(p.getProperty(k));
            }
        }
        
    }
    
    /**
     * Converts a double 0-1 value to a percent 0-100, both in strings.
     * @param value String to parse and convert.
     * @return String containing the percent value.
     */
    private String doubleToPercent(String value)
    {
        if (value==null || value.trim().length()==0)
        {
            return value;
        }
        Double d = new Double(value);
        d*=100;
        return NumberFormat.getIntegerInstance().format(d);
    }
    
    /**
     * Converts a percent value 0-100 to double 0-1, both in strings.
     * 
     * @param value String containing the percent
     * @return String containing the double
     */
    private String percentToDouble(String value)
    {
        if (value==null || value.trim().length()==0)
        {
            return value;
        }
        Double d = new Double(value);
        d/=100;
        return d.toString();
    }
    
    /**
     * Converts empty strings to null, otherwise does nothing.
     * @param s String to convert.
     * @return String that was converted
     */
    private String nie(String s)
    {
        if (s==null)
        {
            return "";
        }
        return s;
    }
    
    /**
     * If the value and key are non-empty and non-null, sets them in the properties object.
     * @param p Properties object to receive the key
     * @param key String key to set in properties
     * @param value String value to set in properties
     */
    private void setIfNonEmpty(Properties p,String key,Object value)
    {
        if (value!=null && p!=null && key!=null)
        {
            if (!String.class.isInstance(value) || ((String)value).trim().length()>0)
            {
                p.setProperty(key, value.toString());
            }
        }
    }
    
    /** Creates a classname from the contents of the combobox.
     * @param cb ComboBox to read classname from
     * @return String containing the class name
     */
    private String classFromCombo(JComboBox cb)
    {
        // First, try reading it as a class name.  If it works, use that
        try
        {
            String test = (String)cb.getSelectedItem();
            Class.forName(test);
            return test; // If we reached here, just use the name
        }
        catch (Exception e)
        {
            String start = (String)cb.getSelectedItem();
            
            int pi = start.indexOf("[");
            String classN = start.substring(0,pi);
            String packageN = start.substring(pi+1,start.length()-1);
            return packageN+"."+classN;
        }
        
    }
    
    /**
     * Converts a classname to combobox format (CLASS[PACKAGE])
     * @param className String containing classname to convert
     * @return String containing name in combo format
     */
    private String formatClassName(String className)
    {
        if (className==null)
        {
            return null;
        }
        int idx = className.lastIndexOf(".");
        if (idx==-1)
        {
            return className;
        }
        return className.substring(idx+1)+"["+className.substring(0,idx)+"]";
    }
    
    /**
     * Creates a properties object matching the state of the panel.
     * @return Properties object matching the panel.
     */
    public Properties toProperties()
    {
        Properties p = new Properties();
        
        setIfNonEmpty(p,EComponent.CREATOR.getClassProperty(),classFromCombo(creator));
        setIfNonEmpty(p,EComponent.CROSSOVER.getClassProperty(),classFromCombo(crossover));
        setIfNonEmpty(p,EComponent.FITNESSFUNCTION.getClassProperty(),classFromCombo(fitness));
        setIfNonEmpty(p,EComponent.MUTATOR.getClassProperty(),classFromCombo(mutator));
        setIfNonEmpty(p,EComponent.SELECTOR.getClassProperty(),classFromCombo(selector));
        setIfNonEmpty(p,LamarkFactory.UPPER_ELITISM_KEY,percentToDouble(upperElitism.getText()));
        setIfNonEmpty(p,LamarkFactory.LOWER_ELITISM_KEY,percentToDouble(lowerElitism.getText()));
        setIfNonEmpty(p,LamarkFactory.MAXIMUM_POPULATION_KEY,maximumPopulations.getText());
        setIfNonEmpty(p,LamarkFactory.POPULATION_SIZE_KEY,populationSize.getText());
        setIfNonEmpty(p,LamarkFactory.CROSSOVER_PROBABILITY_KEY,crossoverProbability.getText());
        setIfNonEmpty(p,LamarkFactory.MUTATION_PROBABILITY_KEY,mutationProbability.getText());
        setIfNonEmpty(p,LamarkFactory.NUMBER_OF_WORKER_THREADS_KEY,numberOfWorkerThreads.getText());
        setIfNonEmpty(p,LamarkFactory.TARGET_SCORE_KEY,targetScore.getText());
        setIfNonEmpty(p,LamarkFactory.RANDOM_SEED_KEY,randomSeed.getText());
        
        // now write all properties to props
        addComponentProperties(EComponent.CREATOR.getPropertyPrefix(),creatorProperties,p);
        addComponentProperties(EComponent.CROSSOVER.getPropertyPrefix(),crossoverProperties,p);
        addComponentProperties(EComponent.FITNESSFUNCTION.getPropertyPrefix(),fitnessProperties,p);
        addComponentProperties(EComponent.MUTATOR.getPropertyPrefix(),mutatorProperties,p);
        addComponentProperties(EComponent.SELECTOR.getPropertyPrefix(),selectorProperties,p);

        int count=0;
        for (String s:preloads)
        {
            p.setProperty(LamarkFactory.PRELOAD_PREFIX+count, s);
            count++;
        }
        count=0;
        for (String s:customListener)
        {
            p.setProperty(LamarkFactory.CUSTOM_LISTENER_PREFIX+count, s);
            count++;
        }
        
        return p;
    }
    
    /**
     * Adds component properties to a properties object, using the given prefix.
     * @param componentPrefix String containing the prefix for the given component.
     * @param componentProperties Properties object containing the component properties
     * @param targetProps Properties object to stick the component properties into
     */
    private void addComponentProperties(String componentPrefix,Properties componentProperties,Properties targetProps)
    {
        for (Object key:componentProperties.keySet())
        {
            String s = (String)key;
            targetProps.setProperty(componentPrefix+s, componentProperties.getProperty(s));
        }
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenAbort()
    {
        return lAbort.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenBetterIndividualFound()
    {
        return lBetterIndividualFound.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenConfiguration()
    {
        return lConfiguration.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenException()
    {
        return lException.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenLastPopDone()
    {
        return lLastPopDone.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenLog()
    {
        return lLog.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenPopPlanDone()
    {
        return lPopPlanDone.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenPopulationComplete()
    {
        return lPopulationComplete.isSelected();
    }

    /**
     * Returns true if the given listener should be used.
     * @return true if the given listener should be used
     */
    public boolean listenUniformPop()
    {
        return lUniformPop.isSelected();
    }

    /**
     * Accessor method.
     * @return List containing the property
     */
    public List < String > getCustomListener()
    {
        return customListener;
    }

    /**
     * Accessor method.
     * @return List containing the property
     */
    public List < String > getPreloads()
    {
        return preloads;
    }
    
}
