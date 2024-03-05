/**
 * 
 */
package net.sci.image.binary;

import java.util.HashMap;

import net.sci.array.binary.BinaryRow;

/**
 * A region within an array, without explicit specification of array bounds.
 * 
 * @see BinaryRow
 */
public class BinaryRegion2D
{
    // =============================================================
    // Class fields
    
    /**
     * The collection of binary rows that compose the region.
     */
    HashMap<Integer, BinaryRow> rows;
    
    
    // =============================================================
    // Constructors

    /**
     * Empty constructor
     */
    public BinaryRegion2D()
    {
        this.rows = new HashMap<Integer, BinaryRow>();
    }
    
    
    // =============================================================
    // Methods
    
    public boolean isEmpty()
    {
        return this.rows.isEmpty();
    }

    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount()
    {
        long count = 0;
        for (BinaryRow row : rows.values())
        {
            count += row.elementCount();
        }
        return count;
    }
    
    public boolean get(int x, int y)
    {
        BinaryRow row = rows.get(y);
        if (row == null) return false;
        return row.get(x);
    }
    
    public void set(int x, int y, boolean state)
    {
        if (rows.containsKey(y))
        {
            // update existing row
            BinaryRow row = rows.get(y);
            row.set(x, state);
            
            // in case row becomes empty, removes it from row array
            if (row.isEmpty())
            {
                rows.remove(y);
            }
        }
        else
        {
            // if state is false and row is empty, nothing changes
            if (!state) return;
            
            // create a new row containing only the specified element
            BinaryRow row = new BinaryRow();
            row.set(x, true);
            rows.put(y, row);
        }
    }
    
}
