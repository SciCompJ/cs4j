/**
 * 
 */
package net.sci.table.impl;

import java.util.Locale;

import net.sci.axis.CategoricalAxis;
import net.sci.table.Table;

/**
 * Adapter class that mimics behavior of Categorical Axis, by referring to
 * the inner collection of columns for getter and accessors.
 * 
 * (not used anymore, as all class implementations now embed column axis).
 */
public class ColumnAxisAdapter extends CategoricalAxis
{
    Table table;
    
    public ColumnAxisAdapter(Table table)
    {
        super("", new String[table.columnCount()]);
        this.table = table;
    }
    
    /**
     * @return the number of items on this axis, corresponding to its length
     */
    public int length()
    {
        return table.columnCount();
    }
    
    /**
     * Returns the name a specific item within the axis.
     * 
     * @param index
     *            the index of the category
     * @return the name of the category for the given index
     */
    public String itemName(int index)
    {
        return table.column(index).getName();
    }
    
    public void setItemName(int index, String string)
    {
        table.column(index).setName(string);
    }
    
    /**
     * Returns an array containing all the names of the items describing the
     * axis.
     * 
     * @return an array containing all the names of the items describing the
     *         axis.
     */
    public String[] itemNames()
    {
        return table.columns().stream().map(c -> c.getName()).toArray(String[]::new);
    }
    
    @Override
    public CategoricalAxis duplicate()
    {
        return new CategoricalAxis(getName(), itemNames());
    }
 
    @Override
    public String toString()
    {
        String[] names = itemNames();
        int nItems = names.length;
        String itemString = String.format("String[%d]", nItems);
        if (nItems < 6)
        {
            itemString = itemString + "{";
            if (nItems > 0)
            {
                itemString = itemString + "\"" + names[0] + "\"";
            }
            for (int i = 1; i < nItems; i++)
            {
                itemString = itemString + ", \"" + names[i] + "\"";
            }
            itemString = itemString + "}";
        }
        String format = "CategoricalAxis(name=\"%s\", items=%s)";
        return String.format(Locale.ENGLISH, format, getName(), itemString);
    }        
}    
