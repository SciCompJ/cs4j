package net.sci.image;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.Dimensional;

public interface Connectivity extends Dimensional
{
    public static Connectivity createOrtho(int nd)
    {
        ArrayList<int[]> offsets = new ArrayList<int[]>(2 * nd);
        for (int d = 0; d < nd; d++)
        {
            int[] minus = new int[nd];
            minus[d] = -1;
            offsets.add(minus);
            
            int[] plus = new int[nd];
            plus[d] = +1;
            offsets.add(plus);
        }
        
        return new GenericConnectivityND(offsets);
    }
    
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
    public static Connectivity convertDimensionality(Connectivity conn, int newDim)
    {
        return switch (newDim)
        {
            case 2 -> Connectivity2D.convert(conn);
            case 3 -> Connectivity3D.convert(conn);
            default -> GenericConnectivityND.convert(conn, newDim);
        };
    }
    
    
    /**
     * Returns the set of neighbors associated to a given position
     * 
     * @param pos
     *            the position of the pixel/voxel
     * @return the list of neighbors of specified pixel/voxel
     */
    public Collection<int[]> neighbors(int[] pos);
    
    /**
     * @return the list of offsets computed relative to the center pixel.
     */
    public Collection<int[]> offsets();
}
