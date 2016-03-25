package com.erigir.lamark.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * A table model that holds a set of name/value pairs (2 columns) for a dialog box.
 * <p/>
 * Used to hold custom properties for the various GA components.
 *
 * @author cweiss
 * @since 10/2007
 */
public class PropertiesTableModel extends AbstractTableModel {
    /**
     * List of Prop objects backing the table *
     */
    private List<Prop> properties = new ArrayList<Prop>();

    /**
     * Constructor receiving the initial properties object
     *
     * @param initial Properties to load the table with
     */
    public PropertiesTableModel(Properties initial) {
        for (Object key : initial.keySet()) {
            String s = (String) key;
            properties.add(new Prop(s, initial.getProperty(s)));
        }
    }

    /**
     * Convert table contents to a properties object
     *
     * @return Properties object with table contents
     */
    public Properties getProperties() {
        Properties rval = new Properties();
        for (Prop p : properties) {
            rval.setProperty(p.key, p.value);
        }
        return rval;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return properties.size();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if (row >= 0 && row < properties.size()) {
            Prop p = properties.get(row);
            switch (col) {
                case 0:
                    return p.key;
                case 1:
                    return p.value;
                default:
                    throw new IllegalStateException("Cant happen");
            }
        }
        return null; // In middle of update
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int arg0) {
        switch (arg0) {
            case 0:
                return "Key";
            case 1:
                return "Value";
            default:
                throw new IllegalArgumentException("Cant happen");
        }
    }

    /**
     * Sets a value in the backing model
     *
     * @param key   String key of property to set
     * @param value String value of property to set
     */
    public void setProperty(String key, String value) {
        if (key != null && key.trim().length() > 0) {
            int idx = -1;
            for (int i = 0; i < properties.size() && idx == -1; i++) {
                if (properties.get(i).key.equals(key)) {
                    idx = i;
                }
            }

            if (idx != -1 && value == null || value.trim().length() == 0) //remove
            {
                properties.remove(idx);
            } else if (idx != -1) // update
            {
                properties.get(idx).value = value;
            } else // insert
            {
                properties.add(new Prop(key, value));
            }

            fireTableDataChanged();
        }
    }

    /**
     * Wrapper class to simplify table handling.
     * <p/>
     * Needed because properties objects are hashed and have no set order.
     *
     * @author cweiss
     * @since 10/2007
     */
    class Prop {
        /**
         * Key of property *
         */
        String key;
        /**
         * Value of property *
         */
        String value;

        /**
         * Simple constructor.
         *
         * @param k String containing key
         * @param v String containing value
         */
        public Prop(String k, String v) {
            super();
            key = k;
            value = v;
        }
    }

}
