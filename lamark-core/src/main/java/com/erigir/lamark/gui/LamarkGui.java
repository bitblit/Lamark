/*
 * Created on Sep 28, 2004
 */
package com.erigir.lamark.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.erigir.lamark.IPreloadableCreator;
import com.erigir.lamark.Lamark;
import com.erigir.lamark.LamarkFactory;
import com.erigir.lamark.Util;
import com.erigir.lamark.events.BetterIndividualFoundEvent;
import com.erigir.lamark.events.ExceptionEvent;
import com.erigir.lamark.events.LamarkEvent;
import com.erigir.lamark.events.LamarkEventListener;
import com.erigir.lamark.events.LastPopulationCompleteEvent;
import com.erigir.lamark.events.PopulationCompleteEvent;

/**
 * A class implementing a simple GUI for creating and running Lamark instances.
 * 
 * This panel is typically wrapped by either an applet (LamarkApplet) or application
 * (LamarkApplication), which only differ in whether they allow the file menu (the
 * applet has no menus since they would be disabled anyway).  The GUI has controls 
 * for setting the various components and properties of the Lamark instance, and
 * for passing in custom properties and pre-load individuals.  It also can run
 * a Lamark instance defined via classpath (ie, a JAR file containing all needed
 * component classes + a Lamark.properties file).
 * 
 * @since 02-2005
 * @author cweiss
 */

public class LamarkGui extends JPanel implements LamarkEventListener, ActionListener
{
    /** Handle to the central panel **/
	private JPanel mainPanel;
    /** Button for starting the GA **/
	private JButton start;
    /** Button for cancelling the GA **/
    private JButton cancel;
    /** Button for showing the current properties **/
    private JButton show;
    /** Button for opening a URL to load **/
    private JButton openUrl;
    /** Button for resetting the instance to initial state **/
    private JButton reset;
    /** Output area for showing any messages **/
	private JTextArea output;
    /** Label holding current running time **/
    private JLabel currentRuntime;
    /** Label showing estimated time remaining **/
    private JLabel timeRemaining;
    /** Label showing current generation number **/
    private JLabel generationNumber;
    /** Label showing best score to date **/
    private JLabel bestScore;
    /** Label showing the current classloader **/
    private JLabel classLoaderLabel;
    /** Label showing the amount of total memory taken from OS **/
    private JLabel totalMemory;
    /** Label showing the amount of free memory in java currently **/
    private JLabel freeMemory;
    /** Handle to the current instnace of Lamark, if any **/
    private Lamark currentRunner;
    /** Handle to the current configuration of Lamark **/
    private Properties currentProperties=LamarkFactory.defaultProperties();
    /** Handle to the current classloader, if not default **/
    private URLClassLoader currentClassloader;
    /** Handle to the url loaded at startup, if any **/
    private URL initialURL;    
    /** Panel holding the current configuration **/
    private LamarkConfigPanel configPanel;
    /** String containing the lable of the open url button **/
    public static final String OPEN_REMOTE="Open URL..."; 

    
	/**
	 * Default constructor.  
     * Lays out all the controls.
	 */
	public LamarkGui()
	{
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
        toolPanel.setLayout(new GridLayout(1,8));
        toolPanel.add(toolbar);
        
		add(toolPanel,BorderLayout.NORTH);
		add(getMainPanel(),BorderLayout.CENTER);
        
        // If a url was given, load it
        if (initialURL!=null)
        {
            openURL(initialURL);
        }
	}
	
    /**
     * Loads one of the built-in images as an icon.
     * @param iconFile String containing name of the file
     * @return ImageIcon for use on buttons
     */
    private ImageIcon icon(String iconFile)
    {
        try
        {
            return new ImageIcon(ImageIO.read(getClass().getResourceAsStream(iconFile)));
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Unable to load icon "+iconFile+" :"+e);
        }
    }
    
    /**
     * Converts contents of config panel to lamark properties.
     * @return Proeprties object containing the contents
     */
    public Properties getConfigPanelProperties()
    {
        return configPanel.toProperties();
    }

    /**
     * Loads config panel from a resource path containing a properties object
     * @param resourcePath String containing a path to a resource
     * @return boolean true if the config panel could be loaded from properties at that location
     */
    public boolean openPropertyResource(String resourcePath)
    {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is!=null)
        {
            output.append("Loading "+resourcePath+"...");
            return openPropertyStream(is);
        }
        else
        {
            output.append("Couldn't read resource path:"+resourcePath);
            return false;
        }
    }
    
    /**
     * Loads config panel from an input stream containing a properties object
     * @param is InputStream to read properties from
     * @return boolean true if the config panel could be loaded from properties at that location
     */
    public boolean openPropertyStream(InputStream is)
    {
        if (is==null)
        {
            throw new IllegalArgumentException("Passed stream cannot be null");
        }
        try
        {
            clearCurrent();
            currentProperties=new Properties();
            currentProperties.load(is);
            is.close();
            configPanel.fromProperties(currentProperties);
            output.append("\n\nLamark loaded from properties");
            return true;
        }
        catch (Exception e)
        {
            output.append("Error reading stream :"+e);
            clearCurrent();
            return false;
        }
    }
    
    /**
     * Loads either a property file or entire classpath from a URL
     * @param u URL to open and read
     * @return boolean true if the URL could be read, false otherwise
     */
    public boolean openURL(URL u)
    {
        try
        {
            if (u.toString().endsWith(".properties"))
            {
                clearCurrent();
                InputStream is = u.openStream();
                if (is!=null)
                {
                    return openPropertyStream(is);
                }
                else
                {
                    return false;
                }
            }
            else if (u.toString().endsWith(".jar"))
            {
                output.append("Trying URL : "+u);
                URLClassLoader ucl = new URLClassLoader(new URL[]{u});
                
                InputStream is = ucl.getResourceAsStream("lamark.properties");
                
                if (null!=is)
                {
                    currentProperties=new Properties();
                    currentProperties.load(is);
                    configPanel.fromProperties(currentProperties);
                    currentClassloader = ucl;
                    is.close();
                    updateClassLoaderLabel();
                    output.append("\n\nLamark loaded from url");
                    return true;
                }
                else
                {
                    output.append("Classpath didn't contain a lamark.properties file.  Ignoring");
                }
            }
            else
            {
                output.append("Resource: "+u+" doesn't end with .properties or .jar ... Ignoring.");
            }
            clearCurrent();
            return false;
        }
        catch (Exception e)
        {
            output.append("Error reading url : "+u+" was :"+e);
            clearCurrent();
            return false;
        }
    }
    
    /**
     * Make the classloader label match the current class loader.
     */
    private void updateClassLoaderLabel()
    {
        if (currentClassloader==null)
        {
            classLoaderLabel.setText("Classloader: default");
        }
        else
        {
            classLoaderLabel.setText("Classloader: "+Arrays.asList(currentClassloader.getURLs()));
        }
    }
        
    /**
     * Empty the current runner from memory and reset labels.
     */
    private void clearCurrent()
    {
        currentRunner = null;
        currentClassloader = null;
        currentProperties = LamarkFactory.defaultProperties();
        updateClassLoaderLabel();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource()==start)
        {
            output.setText("Starting new Lamark instance...\n\n");
            
            // First, generate a new currentRunner
            
            currentRunner = new Lamark();

            // Add this as a generic listener for timers
            currentRunner.addGenericListener(this);

            // Add any defined listeners
            OutputListener ol = new OutputListener(output);
            if (configPanel.listenAbort())
            {
                currentRunner.addAbortListener(ol);
            }
            if (configPanel.listenBetterIndividualFound())
            {
                currentRunner.addBetterIndividualFoundListener(ol);
            }
            if (configPanel.listenConfiguration())
            {
                currentRunner.addConfigurationListener(ol);
            }
            if (configPanel.listenException())
            {
                currentRunner.addExceptionListener(ol);
            }
            if (configPanel.listenLastPopDone())
            {
                currentRunner.addLastPopulationCompleteListener(ol);
            }
            if (configPanel.listenLog())
            {
                currentRunner.addLogListener(ol);
            }
            if (configPanel.listenPopPlanDone())
            {
                currentRunner.addPopulationPlanCompleteListener(ol);
            }
            if (configPanel.listenPopulationComplete())
            {
                currentRunner.addPopulationCompleteListener(ol);
            }
            if (configPanel.listenUniformPop())
            {
                currentRunner.addUniformPopulationListener(ol);
            }

            // Init the running
            LamarkFactory.initLamarkFromProperties(currentRunner, configPanel.toProperties(), currentClassloader);
            
            // Check for errors
            List<String> errors = currentRunner.getConfigurationErrors();
            
            if (errors.size()>0)
            {
                for (String s:errors)
                {
                    output.append(s);
                    output.append("\n");
                }
                output.append("\n\nStopping Lamark instance... errors found.\n");
            }
            else
            {
                // Create any custom listeners and attach them
                List<LamarkEventListener> customList = configPanel.customListeners(currentClassloader, currentRunner);
                for (LamarkEventListener l:customList)
                {
                    currentRunner.addGenericListener(l);
                    if (GUIEventListener.class.isAssignableFrom(l.getClass()))
                    {
                        ((GUIEventListener)l).setParentComponent(this);
                    }
                }
                
                // If any preloads are there, process them
                if (configPanel.getPreloads().size()>0)
                {
                    if (IPreloadableCreator.class.isAssignableFrom(currentRunner.getCreator().getClass()))
                    {
                        IPreloadableCreator pc = (IPreloadableCreator)currentRunner.getCreator();
                        currentRunner.clearInsertQueue();
                        for (String s:configPanel.getPreloads())
                        {
                            currentRunner.enqueueForInsert(pc.createFromPreload(s));
                        }
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Creator doesnt extend IPreloadableCreator.  Ignoring preloads");
                    }
                }
                
                output.setText("");
                start.setEnabled(false);
                cancel.setEnabled(true);
                reset.setEnabled(false);
                openUrl.setEnabled(false);
                show.setEnabled(false);
                configPanel.setEnabled(false);
                new Thread(currentRunner).start();
            }
            
        }
        else if (e.getSource()==show)
        {
            output.setText("Properties...\n\n");
            Properties p = configPanel.toProperties();
            for (Object key:p.keySet())
            {
                output.append(key+" = "+p.get(key)+"\n");
            }
            
        }
        else if (e.getSource()==cancel)
        {
			if (JOptionPane.showConfirmDialog(this,
					"Are you sure you want to stop the algorithm?",
					"Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					currentRunner.abort();
					start.setEnabled(true);
					cancel.setEnabled(false);
                    reset.setEnabled(true);
                    openUrl.setEnabled(true);
                    show.setEnabled(true);
                    configPanel.setEnabled(true);
				}
        }
        else if (e.getSource()==openUrl)
        {
            openUrlDialog();
        }
        else if (e.getSource()==reset)
        {
            resetToNew();
        }
    }
    
    /**
     * Opens a dialog to type in a URL, then loads Lamark from the URL if possible.
     */
    public void openUrlDialog()
    {
        String url=(String)JOptionPane.showInputDialog(
            this,
            "Enter a url to a config file (properties file) or jar file",
            OPEN_REMOTE,
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            null);
    
    if (null!=url && url.trim().length()>0)
    {
        try
        {
            openURL(new URL(url));
        }
        catch (Exception ex)
        {
            appendOutput("\n\nError opening remote site:"+ex);
        }
    }
    else
    {   
        appendOutput("\n\nRemote classpath open cancelled.\n\n");
    }

    }
	
	/**
     * Builds the main panel of the gui.
	 * @return JPanel containing the main controls
	 */
	private JPanel getMainPanel()
	{
		if (mainPanel == null)
		{
			mainPanel = new JPanel(new BorderLayout());
			JPanel textPane = new JPanel(new BorderLayout());
			
			output = new JTextArea();
			output.setRows(20);
			output.setColumns(80);
			JScrollPane outputScrollPane = new JScrollPane(output,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            output.setEditable(false);

			textPane.add(outputScrollPane,BorderLayout.CENTER);
            output.setText("Lamark\nGraphical User Interface\nVersion "+Util.getVersion()+"\n");
            
            configPanel = new LamarkConfigPanel();
            currentRunner = new Lamark();
            // Init stuff to default lamark
            configPanel.fromProperties(LamarkFactory.lamarkToProperties(currentRunner));
            // Init drop lists
            configPanel.initializeLists();
            
			mainPanel.add(configPanel,BorderLayout.NORTH);
            mainPanel.add(textPane,BorderLayout.CENTER);
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
    public void abortIfRunning()
    {
        if (currentRunner!=null && currentRunner.isRunning())
        {
            currentRunner.abort();
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
    public void resetToNew()
    {
        abortIfRunning();
        clearCurrent();
        configPanel.fromProperties(LamarkFactory.defaultProperties());
        configPanel.initializeLists();
    }
    
    /**
     * Empties contents of the output pane
     */
    public void clearOutput()
    {
        output.setText("");
    }
    
    /**
     * Adds the given object to the output pane at end
     * @param o Object to add to output pane (toString)
     */
    public void appendOutput(Object o)
    {
        if (o!=null)
        {
            output.append(o.toString());
        }
    }
    
    /**
     * Adds the given object to the output pane at start
     * @param o Object to add to output pane (toString)
     */
    public void prependOutput(Object o)
    {
        if (o!=null)
        {
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
     * This handleEvent updates the toobar, not the output pane, which is handled by the custom handler below.
     * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
     */
    public void handleEvent(LamarkEvent je)
    {
        if (BetterIndividualFoundEvent.class.isAssignableFrom(je.getClass()))
        {
            bestScore.setText("Best: "+Util.format(((BetterIndividualFoundEvent)je).getNewBest().getFitness()));
        }
        else if (LastPopulationCompleteEvent.class.isAssignableFrom(je.getClass()))
        {
            start.setEnabled(true);
            cancel.setEnabled(false);
            reset.setEnabled(true);
            openUrl.setEnabled(true);
            show.setEnabled(true);
        configPanel.setEnabled(true);
        }
        else if (PopulationCompleteEvent.class.isAssignableFrom(je.getClass()))
        {
            generationNumber.setText("Generation: "+((PopulationCompleteEvent)je).getPopulation().getNumber());
        }
        else if (ExceptionEvent.class.isAssignableFrom(je.getClass()))
        {
            start.setEnabled(true);
            cancel.setEnabled(false);
            reset.setEnabled(true);
            openUrl.setEnabled(true);
            show.setEnabled(true);
        configPanel.setEnabled(true);
        }
        currentRuntime.setText("Runtime: "
            + Util.formatISO(je.getLamark().currentRuntimeMS()));
        timeRemaining.setText("Remaining: "
            + Util.formatISO(je.getLamark().estimatedRuntimeMS()));
        
        updateMemoryLabels();
    }
    
    /**
     * Updates the contents of the memory labels. 
     */
    private void updateMemoryLabels()
    {
        totalMemory.setText("Total Memory: "+
            (Runtime.getRuntime().totalMemory()/(1024*1024))+" Mb");
        freeMemory.setText("Free Memory :"+
            (Runtime.getRuntime().freeMemory()/(1024*1024))+" Mb");
    }

    /**
     * A class to wrap a lamarklistener around the output panel of the gui.
     * 
     * This class catches the selected events and outputs their
     * string representation into the output panel.
     * 
     * @author cweiss
     * @since 10/2007 
     */
    class OutputListener implements LamarkEventListener
    {
        /** Handle to the output area **/
        private JTextArea output;
        
        
        /**
         * Constructor that passes handle to the output pane
         * @param pOutput JTextArea output pane
         */
        public OutputListener(JTextArea pOutput)
        {
            super();
            output = pOutput;
        }

        /**
         * Writes appropriate events to the output pane.
         * @see com.erigir.lamark.events.LamarkEventListener#handleEvent(com.erigir.lamark.events.LamarkEvent)
         */
        public void handleEvent(LamarkEvent je)
        {
            if (ExceptionEvent.class.isAssignableFrom(je.getClass()))
            {
                Throwable t = ((ExceptionEvent)je).getException();
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                String msg = "\nAn error occurred while attempting to run the algorithm:\n\n"+t+"\n\n"+sw.toString()+"\n";
                output.insert(msg,0);
                start.setEnabled(true);
                cancel.setEnabled(false);
            }
            else
            {
                output.insert(je.toString()+" \n\n",0);
            }
        }
    }

    /**
     * Called the wrapper classes to start the GUI in a given state by opening a url
     * @param initialURL URL object to initialize to
     */
    public void setInitialURL(URL initialURL)
    {
        this.initialURL = initialURL;
    }

}