/**
 * 
 */
package net.sci.axis;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Categorical axis, that encapsulates a series of item names.
 * 
 * @author dlegland
 *
 */
public class CategoricalAxis implements Axis
{
    // =============================================================
    // Static factories
    
    /**
     * Creates a new CategoricalAxis instance with the specified name and
     * levels. This method has to be preferred over the call to constructor,
     * that may become protected in a future release.
     * 
     * @param name
     *            the name of the axis
     * @param levels
     *            the list of unique levels describing axis
     * @return a new instance of CategoricalAxis
     */
    public static final CategoricalAxis create(String name, String[] levels)
    {
        return new CategoricalAxis(name, levels);
    }
    
    
    // =============================================================
    // Class fields
    
    /**
     * The name for this axis.
     */
    String name;

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
    
    
    // =============================================================
    // getters / setters
    
    /**
     * Returns the number of items in the axis.
     * 
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
    
    public void setItemName(int index, String string)
    {
        this.itemNames[index] = string;
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
        if (this.itemNames == null) return null;
        return Arrays.copyOf(this.itemNames, this.itemNames.length);
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

    @Override
    public CategoricalAxis selectElements(int[] indices)
    {
        String[] newItems = IntStream.of(indices)
                .mapToObj(index -> (String) itemName(index))
                .toArray(String[]::new);
        return new CategoricalAxis(getName(), newItems);
    }

    @Override
    public CategoricalAxis duplicate()
    {
        return new CategoricalAxis(this.name, Arrays.copyOf(this.itemNames, this.itemNames.length));
    }
 
    
    // =============================================================
    // Methods overriding Object
    
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
        String format = "CategoricalAxis(name=\"%s\", items=%s)";
        return String.format(Locale.ENGLISH, format, this.name, itemString);
    }
}
