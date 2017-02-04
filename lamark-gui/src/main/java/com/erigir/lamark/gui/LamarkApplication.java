package com.erigir.lamark.gui;

import com.erigir.lamark.LamarkUtil;
import com.erigir.lamark.config.LamarkSerializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;


/**
 * A class for running a LamarkGUI instance as an application.
 * &lt;p /&gt;
 * This class wraps the GUI in a frame, adding a menu bar and allowing
 * loading from the local disk.
 *
 * @author cweiss
 * @since 11/2007
 */
public class LamarkApplication extends Application {
    /**
     * String label for the save action *
     */
    private static final String SAVE = "Save...";
    /**
     * String label for the open local jar file/property file action *
     */
    private static final String OPEN_LOCAL = "Open...";
    /**
     * Logger instance *
     */
    private static Logger LOG = LoggerFactory.getLogger(LamarkApplication.class);
    /**
     * Handle to the Primary stage
     */
    private Stage primaryStage;
    /**
     * Handle to the contained gui *
     */
    private LamarkGui gui;
    /**
     * Handle to the last file that was opened *
     */
    private File lastFile;

    /**
     * Bootstrapper
     * @param args CLI args
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // If a file was passed in parameters, set last file to that
        if (getParameters().getRaw().size()==1)
        {
            LOG.info("Treating first parameter as file to load");
            lastFile = new File(getParameters().getRaw().get(0));
        }

        if (lastFile != null && !lastFile.exists()) {
            System.out.println(lastFile + " does not exist.  Exiting");
            System.exit(-1);
        }
        this.primaryStage = primaryStage;

        //Create and set up the window.
        primaryStage.setTitle("Lamark - " + LamarkUtil.getVersion());
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                LOG.info("Closing the main window");
                // TODO: Exit?
            }
        });

        // Create the menu bar
        MenuBar menuBar = new MenuBar();
        Menu file = new Menu("File");
        file.setMnemonicParsing(true);
        menuBar.getMenus().add(file);
        MenuItem fileNew = new MenuItem("New");
        fileNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.resetToNew();
            }
        });
        MenuItem fileOpenLocal = new MenuItem(OPEN_LOCAL);
        fileOpenLocal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Select local file to open");
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JAR Files","*.jar"),
                        new FileChooser.ExtensionFilter("JSON Files","*.json")
                );
                // TODO: initial directory?
                //fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

                File f = fc.showOpenDialog(primaryStage);
                if (f!=null)
                {
                    if (f.exists() && f.isFile()) {
                        try {
                            gui.appendOutput("\n\nOpening file " + f + "...\n");
                            gui.getConfigPanel().loadFromLocation(f.toURI().toString());
                            lastFile = f;
                        } catch (Exception ioe) {
                            LOG.warn("Error opening file", ioe);
                            gui.clearOutput();
                            gui.appendOutput(ioe);
                            new Alert(Alert.AlertType.ERROR,"Error reading file:" + ioe).show();
                        }
                    } else {
                        gui.appendOutput("\n\nERROR: File " + f + " doesn't exist\n\n");
                    }

                }
                }
        });
        MenuItem fileOpenRemote = new MenuItem(LamarkGui.OPEN_REMOTE);
        fileOpenRemote.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.openUrlDialog();
            }
        });
        MenuItem fileSave = new MenuItem(SAVE);
        fileSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Save configuration");
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JSON Files","*.json")
                );

                File f = fc.showSaveDialog(primaryStage);
                if (f!=null) {
                    try {
                        FileWriter fos = new FileWriter(f);
                        String json = LamarkSerializer.singleConfigurationToString(f.getName(),gui.getConfigPanel().convertToConfiguration());
                        fos.write(json);
                        fos.close();
                        lastFile = f;
                    } catch (Exception ioe) {
                        new Alert(Alert.AlertType.ERROR,"Error saving file:" + ioe).show();
                    }
                }
            }
        });
        MenuItem fileExit = new MenuItem("Exit");
        fileExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gui.abortIfRunning();
                Platform.exit();
            }
        });

        file.getItems().add(fileNew);
        file.getItems().add(fileOpenLocal);
        file.getItems().add(fileOpenRemote);
        file.getItems().add(fileSave);
        file.getItems().add(new SeparatorMenuItem());
        file.getItems().add(fileExit);

        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);

        // Add and init the lamarkgui
        String initialLocation = (lastFile == null) ? null : lastFile.toURI().toString();
        String initialSelection = null; // TODO: implement

        gui = new LamarkGui(initialLocation, initialSelection);
        layout.setCenter(gui);

        primaryStage.setScene(new Scene(layout));
        primaryStage.show();
    }

}
