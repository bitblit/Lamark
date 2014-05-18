package com.erigir.lamark.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

/**
 * Dialog box that displays a table with name/value pairs allowing editing/adding/removing.
 * <p/>
 * Used to modify a set of properties, typically custom properties for a component.
 *
 * @author cweiss
 * @since 10/2007
 */
public class PropertiesDialog extends JDialog implements ActionListener, ListSelectionListener, PropertyChangeListener {
    /**
     * Backing property table *
     */
    private JTable propTable;

    /**
     * Handle to the dialog box *
     */
    private JOptionPane optionPane;
    /**
     * String lable for ok button *
     */
    private String btnString1 = "Ok";
    /**
     * String label for cancel button *
     */
    private String btnString2 = "Cancel";

    /**
     * Field for holding the key being edited *
     */
    private JTextField editKey;
    /**
     * Field for holding the value being edited *
     */
    private JTextField editValue;
    /**
     * Button to commit changes *
     */
    private JButton editCommit;

    /**
     * Properties object to return on dialog close *
     */
    private Properties returnProps;

    /**
     * Constructor initialized with the given title and initial properties.
     *
     * @param title   String containing the title to use on the dialog box
     * @param initial Properties to init the dialog with
     */
    public PropertiesDialog(String title, Properties initial) {
        super((Frame) null, true);

        setTitle("Custom properties for " + title);

        if (null != initial) {
            propTable = new JTable(new PropertiesTableModel(initial));
        } else {
            propTable = new JTable(new PropertiesTableModel(new Properties()));
        }
        propTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propTable.getSelectionModel().addListSelectionListener(this);

        propTable.getColumnModel().getColumn(0).setMinWidth(60);
        propTable.getColumnModel().getColumn(1).setMinWidth(60);
        propTable.setPreferredScrollableViewportSize(new Dimension(400, 200));

        JScrollPane testScrollPane = new JScrollPane(propTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        Object[] array = {testScrollPane, editPanel()};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                propTable.requestFocusInWindow();
            }
        });

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);

        // Default the returned properties to the same as initial
        returnProps = initial;

        pack();
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent arg0) {
        PropertiesTableModel ptm = (PropertiesTableModel) propTable.getModel();
        int row = propTable.getSelectedRow();
        editKey.setText((String) ptm.getValueAt(row, 0));
        editValue.setText((String) ptm.getValueAt(row, 1));

    }

    /**
     * Generates a properties object matching the backed data.
     *
     * @return Properties object matching the table
     */
    public Properties getProperties() {
        return returnProps;
    }

    /**
     * Constructs the central panel of the dialog.
     *
     * @return JPanel with controls of the dialog
     */
    private JPanel editPanel() {
        JPanel rval = new JPanel(new GridLayout(0, 3));
        editKey = new JTextField();
        editValue = new JTextField();
        editCommit = new JButton("Commit");
        editCommit.addActionListener(this);

        rval.add(editKey);
        rval.add(editValue);
        rval.add(editCommit);

        return rval;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == editCommit) {
            performSetProperties();
        }
    }

    /**
     * Commits the contents of the edit boxes to the table
     */
    private void performSetProperties() {
        ((PropertiesTableModel) propTable.getModel()).setProperty(editKey.getText(), editValue.getText());
    }


    /**
     * Gets contents of table as properties object
     *
     * @return Properties object matching table content
     */
    public Properties getContent() {
        return ((PropertiesTableModel) propTable.getModel()).getProperties();
    }


    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                // Extract properties from selected
                try {
                    // Make sure that if there is anything in the boxes, it gets set
                    performSetProperties();
                    returnProps = ((PropertiesTableModel) propTable.getModel()).getProperties();
                    //we're done; clear and dismiss the dialog
                    setVisible(false);
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(
                            PropertiesDialog.this,
                            "Sorry, there's an error in the properties:" + err,
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                    propTable.requestFocusInWindow();

                }
            } else {

                //user closed dialog or clicked cancel, do nothing
                setVisible(false);
            }
        }

    }

}
