/**
 * 
 */
package net.sci.axis;

import java.util.Arrays;
import java.util.Locale;

/**
 * Categorical axis, that encapsulates a series of item names.
 * 
 * @author dlegland
 *
 */
public class CategoricalAxis implements Axis
{
    // =============================================================
    // Class fields
    
    /**
     * The name for this axis.
     */
    String name;

    /**
     * The type of axis
     */
    Type type;
    
    /**
     * The name of each item / category within this axis.
     */
    String[] itemNames;
    
    
    // =============================================================
    // Constructors
    
    /**
     * Creates a new axis by specifying its name and the name of each item.
     * 
     * @param name
     *            the name of the axis
     * @param itemNames
     *            the name of each item
     */
    public CategoricalAxis(String name, String[] itemNames)
    {
        this.name = name;
        this.itemNames = itemNames;
    }

    /**
     * Creates a new axis by specifying its name, its type, and the name of each item.
     * 
     * @param name
     *            the name of the axis
     * @param type
     *            the type of the axis
     * @param itemNames
     *            the name of each item
     */
    public CategoricalAxis(String name, Axis.Type type, String[] itemNames)
    {
        this.type = type;
        this.name = name;
        this.itemNames = itemNames;
    }

    
    // =============================================================
    // getters / setters
    
    /**
     * @return the number of items on this axis, corresponding to its length
     */
    public int length()
    {
        return this.itemNames.length;
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
        return this.itemNames[index];
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
        return Arrays.copyOf(this.itemNames, this.itemNames.length);
    }
    
    public Type type()
    {
        return this.type;
    }
    

    // =============================================================
    // Methods overriding the Axis interface
    
    /**
     * Returns the name of the axis.
     * 
     * @return the name of the axis
     */
    public String getName()
    {
        return name;
    }

    /**
     * Changes the name of the axis
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    
    // =============================================================
    // Methods overriding Object
    
    @Override
    public CategoricalAxis duplicate()
    {
        return new CategoricalAxis(this.name, this.type, Arrays.copyOf(this.itemNames, this.itemNames.length));
    }
 
    @Override
    public String toString()
    {
        int nItems = this.itemNames.length;
        String itemString = String.format("String[%d]", nItems);
        if (nItems < 6)
        {
            itemString = itemString + "{";
            if (nItems > 0)
            {
                itemString = itemString + "\"" + this.itemNames[0] + "\"";
            }
            for (int i = 1; i < nItems; i++)
            {
                itemString = itemString + ", \"" + this.itemNames[i] + "\"";
            }
            itemString = itemString + "}";
        }
        String format = "CategoricalAxis(name=\"%s\", type=%s, items=%s)";
        return String.format(Locale.ENGLISH, format, this.name, this.type, itemString);
    }
}
