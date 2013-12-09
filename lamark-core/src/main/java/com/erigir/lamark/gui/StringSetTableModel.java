package com.erigir.lamark.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Simple class implementing a table model that holds a list of strings.
 * 
 * @author cweiss
 * @since 10/2007
 */
public class StringSetTableModel extends AbstractTableModel
{
    /** Backing list of data for the table **/
    private List<String> data;
    
    
    /**
     * Constructor that gets passed the initial list of strings.
     * @param initial List of strings to initialize with
     */
    public StringSetTableModel(List<String> initial)
    {
        super();
        data = new ArrayList<String>();
        data.addAll(initial);
    }

    
    /**
     * Accessor method
     * @return List containing the property
     */
    public List<String> getData()
    {
        return data;
    }
    
    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return 1;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return data.size();
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col)
    {
        if (row>=0 && row<data.size())
        {
            return data.get(row);
        }
        // in update
        return null;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int arg0)
    {
        return "Item";
    }

    /**
     * Adds a value to the list of strings
     * @param val String to add to the list
     */
    public void addValue(String val)
    {
        if (!data.contains(val))
        {
            data.add(val);
            fireTableDataChanged();
        }
    }
    
    /**
     * Removes a string from the backing list
     * @param val String to remove
     */
    public void removeValue(String val)
    {
        while (data.contains(val))
        {
            data.remove(val);
            fireTableDataChanged();
        }
    }
}
