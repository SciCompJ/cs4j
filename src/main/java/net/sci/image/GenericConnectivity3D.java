/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic class for defining connectivities for 3D images.
 * 
 * Connectivity is defined by a series of offsets, each offset being defined as
 * an array of three integers.
 */
public class GenericConnectivity3D implements Connectivity3D
{
    /**
     * The offsets that define the connectivity, as a list of int arrays with
     * three elements.
     */
    ArrayList<int[]> offsets;
    
    /**
     * Initializes a new Connectivity3D instance by locally storing the list of
     * offsets.
     * 
     * @param offsets
     *            the offsets that define the connectivity, as a list of int
     *            arrays with three elements.
     */
    public GenericConnectivity3D(Collection<int[]> offsets)
    {
        this.offsets = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            if (offset.length != 3) 
            {
                throw new RuntimeException("Offset must be three-elements int arrays");
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
