package com.erigir.lamark.gui;

import com.erigir.lamark.config.LamarkComponentDetails;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A pane for selecting and editing a specific component type
 * Created by cweiss1271 on 2/2/17.
 */
public class LamarkComponentConfigPanel<T> extends VBox {
    private static final Logger LOG = LoggerFactory.getLogger(LamarkComponentConfigPanel.class);

    // The items on creation
    private String title;
    private Map<String,String> customProperties = new TreeMap<>();

    private List<Class> availableClasses;
    private Class defaultSelection;

    // JavaFX controls
    private ComboBox<Class> comboBox;
    private Button editPropertiesButton;

    public LamarkComponentConfigPanel()
    {
        super();
    }

    public LamarkComponentDetails toComponentDetails()
    {
        return LamarkComponentDetails.createSingle(comboBox.getSelectionModel().getSelectedItem(), customProperties);
    }


    public void loadDefault(String title, LamarkComponentDetails details)
    {
        load(title,details.getConfig(),details.getClasses(),details.getDefault());
    }

    public void load(String title, Map<String,String> customProperties, List<Class> availableClasses, Class defaultSelection) {
        this.title = Objects.requireNonNull(title);
        this.customProperties = Objects.requireNonNull(customProperties);
        this.availableClasses = Objects.requireNonNull(availableClasses);
        this.defaultSelection = defaultSelection;

        if (this.availableClasses.size()==0)
        {
            throw new IllegalArgumentException("No available classes found");
        }

        if (this.defaultSelection==null)
        {
            this.defaultSelection = this.availableClasses.get(0);
        }
        else if (!this.availableClasses.contains(this.defaultSelection))
        {
            throw new IllegalArgumentException("Default selection not in available classes");
        }

        // Now initialize
        comboBox = new ComboBox();
        comboBox.getItems().addAll(availableClasses);
        comboBox.getSelectionModel().select(this.defaultSelection);
        comboBox.setConverter(ClassComboboxStringConverter.INSTANCE);

        editPropertiesButton = new Button(title+" Configuration...");
        editPropertiesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Alert(Alert.AlertType.INFORMATION,"Edit propss").show();
            }
        });

        getChildren().addAll(comboBox, editPropertiesButton);
    }

    /**
     * Generates a fully configured instance of the given type
     * @return
     */
    public T createConfiguredInstance()
    {
        T rval = (T)LamarkAvailableClasses.safeInit(comboBox.getSelectionModel().getSelectedItem());
        // TODO: Apply properties
        return rval;
    }

}
