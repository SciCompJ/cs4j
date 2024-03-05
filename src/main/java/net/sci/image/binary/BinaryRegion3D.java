/**
 * 
 */
package net.sci.image.binary;

import java.util.HashMap;

/**
 * A 3D region within an array, without explicit specification of array bounds.
 * 
 * @see BinaryRegion2D
 */
public class BinaryRegion3D
{
    // =============================================================
    // Class fields
    
    /**
     * The collection of 2D binary regions that compose this 3D region.
     */
    HashMap<Integer, BinaryRegion2D> slices;
    
    
    // =============================================================
    // Constructors

    /**
     * Empty constructor
     */
    public BinaryRegion3D()
    {
        this.slices = new HashMap<Integer, BinaryRegion2D>();
    }
    
    
    // =============================================================
    // Methods
    
    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount()
    {
        long count = 0;
        for (BinaryRegion2D slice : slices.values())
        {
            count += slice.elementCount();
        }
        return count;
    }
    
    public boolean get(int x, int y, int z)
    {
        BinaryRegion2D slice = slices.get(z);
        if (slice == null) return false;
        return slice.get(x, y);
    }
    
    public void set(int x, int y, int z, boolean state)
    {
        if (slices.containsKey(z))
        {
            // update existing row
            BinaryRegion2D slice = slices.get(z);
            slice.set(x, y, state);
            
            // in case slice becomes empty, removes it from row array
            if (slice.isEmpty())
            {
                slices.remove(y);
            }
        }
        else
        {
            // if state is false and slice is empty, nothing changes
            if (!state) return;
            
            // create a new slice containing only the specified element
            BinaryRegion2D row = new BinaryRegion2D();
            row.set(x, y, true);
            slices.put(z, row);
        }
    }
}
