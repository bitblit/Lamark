package com.erigir.lamark.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dialog displaying an editable list of strings in a table with add/remove buttons.
 * 
 * Lamark uses this class to display a list of pre-load individuals.
 * 
 * @author cweiss
 * @since 11/2007
 */
public class StringListDialog extends JDialog implements ActionListener, ListSelectionListener, PropertyChangeListener 
{
        /** Table to show the list **/
        private JTable listTable;
                
        /** Handle to the dialog box **/
        private JOptionPane optionPane;
        /** Label for ok button **/
        private String btnString1 = "Ok";
        /** Label for cancel button **/
        private String btnString2 = "Cancel";
        
        /** Field for editing strings **/
        private JTextField editValue;
        /** Button to add a string **/
        private JButton add;
        /** Button tot remove a string **/
        private JButton remove;
        
        /** List of strings to return when the dialog closes **/
        private List<String> returnData;
        
        
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent arg0)
        {
            StringSetTableModel ptm = (StringSetTableModel)listTable.getModel();
            int row = listTable.getSelectedRow();
            editValue.setText((String)ptm.getValueAt(row, 0));
        }

        /**
         * Accessor method.
         * @return List of strings from the dialog
         */
        public List<String> getData()
        {
            return returnData;
        }
        
        /**
         * Constructs the dialog box with the given title and initial list of strings.
         * @param title String containing the title for the box
         * @param initial List to initialize with
         */
        public StringListDialog(String title,List<String> initial) {
            super((Frame)null, true);

            setTitle(title);
            
            if (null!=initial)
            {
                listTable = new JTable(new StringSetTableModel(initial));
            }
            else
            {
                listTable = new JTable(new StringSetTableModel(new ArrayList<String>()));
            }
            listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listTable.getSelectionModel().addListSelectionListener(this);
            
            listTable.getColumnModel().getColumn(0).setMinWidth(60);
            listTable.setPreferredScrollableViewportSize(new Dimension(200,200));
            
            JScrollPane testScrollPane = new JScrollPane(listTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            Object[] array = {testScrollPane,editPanel()};
            
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
                    listTable.requestFocusInWindow();
                }
            });

            //Register an event handler that reacts to option pane state changes.
            optionPane.addPropertyChangeListener(this);
            
            // Default the returned properties to the same as initial
            returnData = initial;
            
            pack();
        }
        
        /**
         * Constructs a panel to display in the box
         * @return JPanel containing the edit controls
         */
        private JPanel editPanel()
        {
            JPanel rval = new JPanel(new GridLayout(0,3));
            editValue = new JTextField();
            add = new JButton("Add");
            add.addActionListener(this);
            remove = new JButton("Remove");
            remove.addActionListener(this);

            rval.add(editValue);
            rval.add(add);
            rval.add(remove);
            
            return rval;
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            if (arg0.getSource()==add)
            {
                performAddValue();
            }
            else if (arg0.getSource()==remove)
            {
                ((StringSetTableModel)listTable.getModel()).removeValue(editValue.getText());
            }
        }
        
        /**
         * Puts the value in the edit box into the table.
         */
        private void performAddValue()
        {
            ((StringSetTableModel)listTable.getModel()).addValue(editValue.getText());
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
                    try
                    {
                        // Make sure that if there is anything in the boxes, it gets set
                        performAddValue();
                        returnData = ((StringSetTableModel)listTable.getModel()).getData();
                        //we're done; clear and dismiss the dialog
                        setVisible(false);
                    }
                    catch (Exception err)
                    {
                        JOptionPane.showMessageDialog(
                            StringListDialog.this,
                            "Sorry, there's an error in the properties:"+err,
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                        listTable.requestFocusInWindow();
                             
                    }
                    } 
                 else { 
                    
                    //user closed dialog or clicked cancel, do nothing
                     setVisible(false);
                }
            }
            
        }

}
