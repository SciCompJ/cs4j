/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines the connectivity for 3D images.
 */
public interface Connectivity3D extends Connectivity
{
    // =============================================================
    // Constants

    /**
     * 3D connectivity that considers the six orthogonal neighbors of a voxel.
     */
    public static final Connectivity3D C6 = new Connectivity3D()
    {
        @Override
        public Collection<int[]> neighbors(int x, int y, int z)
        {
            ArrayList<int[]> array = new ArrayList<int[]>(6);
            array.add(new int[] { x, y, z - 1 });
            array.add(new int[] { x, y - 1, z });
            array.add(new int[] { x - 1, y, z });
            array.add(new int[] { x + 1, y, z });
            array.add(new int[] { x, y + 1, z });
            array.add(new int[] { x, y, z + 1 });
            return array;
        }

        @Override
        public Collection<int[]> offsets()
        {
            ArrayList<int[]> array = new ArrayList<int[]>(6);
            array.add(new int[] { 0, 0, -1 });
            array.add(new int[] { 0, -1, 0 });
            array.add(new int[] { -1, 0, 0 });
            array.add(new int[] { +1, 0, 0 });
            array.add(new int[] { 0, +1, 0 });
            array.add(new int[] { 0, 0, +1 });
            return array;
        }

    };

    /**
     * 3D connectivity that considers all the 26 neighbors of a voxel.
     */
    public static final Connectivity3D C26 = new Connectivity3D()
    {
        @Override
        public Collection<int[]> offsets()
        {
            ArrayList<int[]> array = new ArrayList<int[]>(26);
            for (int dz = -1; dz <= 1; dz++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    for (int dx = -1; dx <= 1; dx++)
                    {
                        if (dx != 0 || dy != 0 || dz != 0)
                        {
                            array.add(new int[] { dx, dy, dz });
                        }
                    }
                }
            }
            return array;
        }
    };

    
    // =============================================================
    // Static methods

    /**
     * Converts a connectivity from any dimensionality into an instance of
     * Connectivity3D. If the specified connectivity is already an instance of
     * Connectivity3D, it is simply returned.
     * 
     * @param conn
     *            the connectivity to convert or to cast
     * @return an instance of Connectivity2D
     */
    public static Connectivity3D convert(Connectivity conn)
    {
        if (conn instanceof Connectivity3D)
        {
            return (Connectivity3D) conn;
        }
        
        int[] offset0 = new int[] {0, 0, 0};

        ArrayList<int[]> offsets = new ArrayList<int[]>();
        int nd = Math.min(conn.dimensionality(), 3);
        for (int[] offset : conn.offsets())
        {
            // create new 3D offset
            int[] offset3d = new int[3];
            System.arraycopy(offset, 0, offset3d, 0, nd);
            
            // add offset only if it does not already exist
            if (!contains(offsets, offset3d) && !equals(offset3d, offset0))
            {
                offsets.add(offset3d);
            }
        }
        
        return new GenericConnectivity3D(offsets);
    }
    
    private static boolean contains(Collection<int[]> list, int[] item)
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
    
    private static boolean equals(int[] item1, int[] item2)
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
     * Returns a new connectivity object from a connectivity value.
     * 
     * @param conn
     *            the connectivity value, either 6 or 26
     * @return a Connectivity3D object
     */
    public static Connectivity3D fromValue(int conn)
    {
        if (conn == 6)
            return C6;
        else if (conn == 26)
            return C26;
        else
            throw new IllegalArgumentException("Connectivity value should be either 6 or 26");
    }
    

    // =============================================================
    // New methods

    /**
     * Returns the set of neighbors associated to a given position
     * 
     * @param x
     *            the x position of the voxel
     * @param y
     *            the y position of the voxel
     * @param z
     *            the z position of the voxel
     * @return the list of neighbors of specified voxel
     */
    public default Collection<int[]> neighbors(int x, int y, int z)
    {
        Collection<int[]> offsets = offsets();
        ArrayList<int[]> res = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            res.add(new int[] { x + offset[0], y + offset[1], z + offset[2] });
        }
        return res;
    }

    
    // =============================================================
    // Default implementation of the Connectivity interface

    public default Collection<int[]> neighbors(int[] pos)
    {
        return neighbors(pos[0], pos[1], pos[2]);
    }

    
    // =============================================================
    // Default implementation of the Dimensional interface

    @Override
    public default int dimensionality()
    {
        return 3;
    }
}
