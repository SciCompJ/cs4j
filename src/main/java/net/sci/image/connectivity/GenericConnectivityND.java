/**
 * 
 */
package net.sci.image.connectivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic class for defining connectivities for ND images.
 * 
 * Connectivity is defined by a series of offsets, each offset being defined as
 * an array of N integers.
 */
public class GenericConnectivityND implements Connectivity
{
    /**
     * Converts a connectivity defined for a given dimensionality to a
     * connectivity defined for the specified dimensionality.
     *
     * If the new dimension is lower than the connectivity dimension, the
     * offsets are projected to the lower dimension subspace. The new number of
     * offsets may be smaller than initial, due to projections overlap.
     * 
     * If the new dimension is greater than the connectivity dimension, the
     * offsets are embedded into the larger dimension space by adding trailing 0
     * coordinates.
     * 
     * @param conn
     *            the connectivity to convert
     * @param newDim
     *            the target dimensionality
     * @return a new Connectivity defined for dimension <code>newDim</code>
     */
    public static final Connectivity convert(Connectivity conn, int newDim)
    {
        int[] offset0 = new int[newDim];

        ArrayList<int[]> offsets = new ArrayList<int[]>();
        int nd = Math.min(conn.dimensionality(), newDim);
        for (int[] offset : conn.offsets())
        {
            // create new 3D offset
            int[] offset2 = new int[newDim];
            System.arraycopy(offset, 0, offset2, 0, nd);
            
            // add offset only if it does not already exist
            if (!contains(offsets, offset2) && !equals(offset2, offset0))
            {
                offsets.add(offset2);
            }
        }
        
        return new GenericConnectivityND(offsets);
        
    }
    
    private static final boolean contains(Collection<int[]> list, int[] item)
    {
        for(int[] listItem : list)
        {
            if (equals(listItem, item))
            {                
                return true;
            }
        }
        return false;
    }
    
    private static final boolean equals(int[] item1, int[] item2)
    {
        for (int d = 0; d < item1.length; d++)
        {
            if (item1[d] != item2[d])
            {
                return false;
            }
        }
        
        // all int elements are equal -> items are equal
        return true;
    }

    /**
     * The dimensionality of the connectivity. Corresponds to the length of each
     * offset.
     */
    int nd;
    
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
    public GenericConnectivityND(Collection<int[]> offsets)
    {
        this.nd = offsets.iterator().next().length;
        this.offsets = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            if (offset.length != nd) 
            {
                throw new RuntimeException("All offsets must have same number of elements");
            }
            this.offsets.add(offset);
        }
    }

    @Override
    public Collection<int[]> offsets()
    {
        return this.offsets;
    }

    @Override
    public int dimensionality()
    {
        return this.nd;
    }

    @Override
    public Collection<int[]> neighbors(int[] pos)
    {
        ArrayList<int[]> res = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            int[] neigh = new int[nd];
            for (int d = 0; d < nd; d++)
            {
                neigh[d] = pos[d] + offset[d];
            }
            res.add(neigh);
        }
        return res;
    }
}
