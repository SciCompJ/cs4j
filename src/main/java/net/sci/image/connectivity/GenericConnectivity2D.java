/**
 * 
 */
package net.sci.image.connectivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic class for defining connectivities for 2D images.
 * 
 * Connectivity is defined by a series of offsets, each offset being defined as
 * an array of two integers.
 */
public class GenericConnectivity2D implements Connectivity2D
{
    /**
     * The offsets that define the connectivity, as a list of int arrays with
     * two elements.
     */
    ArrayList<int[]> offsets;
    
    /**
     * Initializes a new Connectivity2D instance by locally storing the list of
     * offsets.
     * 
     * @param offsets
     *            the offsets that define the connectivity, as a list of int
     *            arrays with two elements.
     */
    public GenericConnectivity2D(Collection<int[]> offsets)
    {
        this.offsets = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            if (offset.length != 2) 
            {
                throw new RuntimeException("Offset must be two-elements int arrays");
            }
            this.offsets.add(offset);
        }
    }

    @Override
    public Collection<int[]> offsets()
    {
        return this.offsets;
    }
}
