package com.erigir.lamark.teacher;

import org.codehaus.janino.ClassBodyEvaluator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

public class CodeDialog extends JDialog implements PropertyChangeListener
{
        private JTextArea textField;
        private String header;
        private String footer;
        private String defaultCode;
        private Class[] paramTypes;
        private String functionName;
        private Method method;
        
        private JOptionPane optionPane;

        private String btnString1 = "Compile";
        private String btnString2 = "Cancel";

        public Method getMethod()
        {
            return method;
        }
        

        /** Creates the reusable dialog. */
        public CodeDialog(String pHeader,String pFooter,String pDefaultCode,String pFunctionName,Class[] pParamTypes) {
            super((Frame)null, true);

            setTitle("Code Dialog");
            
            header = pHeader;
            footer = pFooter;
            defaultCode = pDefaultCode;
            functionName = pFunctionName;
            paramTypes = pParamTypes;
         
            textField = new JTextArea(defaultCode);
            textField.setRows(20);
            textField.setColumns(80);
            JScrollPane testScrollPane = new JScrollPane(textField,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            Object[] array = {header,testScrollPane,footer};
            
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
                    textField.requestFocusInWindow();
                }
            });

            //Register an event handler that reacts to option pane state changes.
            optionPane.addPropertyChangeListener(this);
        }

        public String getContent()
        {
            return textField.getText();
        }

        /** This method reacts to state changes in the option pane. */
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
                    // Compile selected
                    try
                    {
                        String code = header+textField.getText()+footer;
                        Class c = new ClassBodyEvaluator(code).evaluate();
                        method = c.getMethod(functionName,paramTypes);
                        //we're done; dismiss the dialog
                        setVisible(false);
                    }
                    catch (Exception err)
                    {
                        JOptionPane.showMessageDialog(
                            CodeDialog.this,
                            "Sorry, that doesnt compile because:"+err,
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                        textField.requestFocusInWindow();
                             
                    }
                    } 
                 else { 
                    
                    //user closed dialog or clicked cancel
                    clearAndHide();
                }
            }
            
        }

        /** This method clears the dialog and hides it. */
        public void clearAndHide() {
            textField.setText(defaultCode);
            setVisible(false);
        }
    

}
