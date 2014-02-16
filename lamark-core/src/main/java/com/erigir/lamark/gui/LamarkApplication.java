package com.erigir.lamark.gui;

import com.erigir.lamark.Util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URL;
import java.util.logging.Logger;

/**
 * A class for running a LamarkGUI instance as an application.
 * <p/>
 * This class wraps the GUI in a frame, adding a menu bar and allowing
 * loading from the local disk.
 *
 * @author cweiss
 * @since 11/2007
 */
public class LamarkApplication implements ActionListener {
    /**
     * Logger instance *
     */
    private static Logger LOG = Logger.getLogger(LamarkApplication.class.getName());

    /**
     * String label for the save action *
     */
    private static final String SAVE = "Save...";
    /**
     * String label for the open local jar file/property file action *
     */
    private static final String OPEN_LOCAL = "Open...";

    /**
     * Handle to the wrapping frame *
     */
    private JFrame frame = null;
    /**
     * Handle to the contained gui *
     */
    private LamarkGui gui;
    /**
     * Handle to the last file that was opened *
     */
    private File lastFile;

    /**
     * Standard bootstrapper from CLI.
     *
     * @param args String[] command line arguments
     */
    public static void main(String[] args) {
        LamarkApplication instance = new LamarkApplication();
        if (args.length > 0) {
            instance.lastFile = new File(args[0]);
        }
        instance.process();
    }

    /**
     * Called once the wrapping object is instantiated.
     */
    public void process() {
        if (lastFile != null && !lastFile.exists()) {
            System.out.println(lastFile + " does not exist.  Exiting");
            System.exit(-1);
        }
        frame = new JFrame();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOG.severe("Couldnt get system look and feel:" + e);
        }

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("Lamark - " + Util.getVersion());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);
        JMenuItem fileNew = new JMenuItem("New");
        JMenuItem fileOpenLocal = new JMenuItem(OPEN_LOCAL);
        JMenuItem fileOpenRemote = new JMenuItem(LamarkGui.OPEN_REMOTE);
        JMenuItem fileSave = new JMenuItem(SAVE);
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.addActionListener(this);
        fileOpenLocal.addActionListener(this);
        fileOpenRemote.addActionListener(this);
        fileSave.addActionListener(this);
        fileNew.addActionListener(this);
        file.add(fileNew);
        file.add(fileOpenLocal);
        file.add(fileOpenRemote);
        file.add(fileSave);
        file.addSeparator();
        file.add(fileExit);
        frame.setJMenuBar(menuBar);

        // Add and init the lamarkgui
        gui = new LamarkGui();
        if (lastFile != null) {
            gui.setInitialURL(toURL(lastFile));
        }

        frame.add(gui, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().getClass().equals(JMenuItem.class)) {
            JMenuItem src = (JMenuItem) e.getSource();
            if (src.getText().equals("Exit")) {
                gui.abortIfRunning();
                frame.dispose();
            } else if (src.getText().equals("New")) {
                gui.resetToNew();
            } else if (src.getText().equals(OPEN_LOCAL)) {
                JFileChooser fc = new JFileChooser(lastFile);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter(PropertiesAndJarsFilter.INSTANCE);
                int rval = fc.showOpenDialog(frame);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    if (f.exists()) {
                        try {
                            gui.appendOutput("\n\nOpening file " + f + "...\n");
                            gui.openURL(toURL(f));
                            lastFile = f;
                        } catch (Exception ioe) {
                            JOptionPane.showMessageDialog(frame, "Error reading file:" + ioe);
                        }
                    } else {
                        gui.appendOutput("\n\nERROR: File " + f + " doesn't exist\n\n");
                    }
                }
            } else if (src.getText().equals(LamarkGui.OPEN_REMOTE)) {
                gui.openUrlDialog();
            } else if (src.getText().equals(SAVE)) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int rval = fc.showSaveDialog(frame);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = fc.getSelectedFile();
                        FileWriter fos = new FileWriter(f);
                        String json = gui.configJSON();
                        fos.write(json);
                        fos.close();
                        lastFile = f;
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(frame, "Error saving file:" + ioe);
                    }
                }
            }
        }
    }

    /**
     * Converts a file handle to a url that the GUI can load.
     *
     * @param f File to convert
     * @return URL to that file
     */
    private static URL toURL(File f) {
        try {
            return f.toURI().toURL();
        } catch (Exception e) {
            throw new IllegalStateException("Cant happen:" + e);
        }
    }

    /**
     * Implements FileFilter only allowing JARs, Properties, and Directories
     *
     * @author cweiss
     * @since 11/2007
     */
    static class PropertiesAndJarsFilter extends FileFilter {
        /**
         * Static instance singleton *
         */
        public static PropertiesAndJarsFilter INSTANCE = new PropertiesAndJarsFilter();

        /**
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        @Override
        public boolean accept(File arg0) {
            if (arg0.isDirectory()) {
                return true;
            }
            String name = arg0.getName();
            int idx = name.lastIndexOf(".");
            if (idx == -1) {
                return false;
            }
            String ext = name.substring(idx).toUpperCase();
            return (ext.equals(".PROPERTIES") || ext.equals(".JAR"));
        }

        /**
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        @Override
        public String getDescription() {
            return "JAR and Properties files (*.jar, *.properties)";
        }

    }
}
