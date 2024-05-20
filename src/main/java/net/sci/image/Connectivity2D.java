/**
 * 
 */
package net.sci.image;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines the connectivity for planar images.
 * 
 * Contains static classes for classical C4 and C8 connectivities, and C6
 * connectivity (orthogonal neighbors plus lower-left and upper right
 * neighbors).
 * 
 * @author dlegland
 *
 */
public interface Connectivity2D extends Connectivity
{
    // =============================================================
    // Constants

    /**
     * Planar connectivity that considers the four orthogonal neighbors of a
     * pixel.
     */
    public static final Connectivity2D C4 = new Connectivity2D()
    {
        @Override
        public Collection<int[]> neighbors(int x, int y)
        {
            ArrayList<int[]> array = new ArrayList<int[]>(4);
            array.add(new int[] { x, y - 1 });
            array.add(new int[] { x - 1, y });
            array.add(new int[] { x + 1, y });
            array.add(new int[] { x, y + 1 });
            return array;
        }

        @Override
        public Collection<int[]> offsets()
        {
            ArrayList<int[]> array = new ArrayList<int[]>(4);
            array.add(new int[] { 0, -1 });
            array.add(new int[] { -1, 0 });
            array.add(new int[] { +1, 0 });
            array.add(new int[] { 0, +1 });
            return array;
        }
    };

    /**
     * Defines the C6_1 connectivity, corresponding to orthogonal neighbors plus
     * lower-left and upper right neighbors.
     */
    public static final Connectivity2D C6_1 = new Connectivity2D()
    {
        @Override
        public Collection<int[]> neighbors(int x, int y)
        {
            ArrayList<int[]> array = new ArrayList<int[]>(6);
            array.add(new int[] { x, y - 1 });
            array.add(new int[] { x + 1, y - 1 });
            array.add(new int[] { x - 1, y });
            array.add(new int[] { x + 1, y });
            array.add(new int[] { x - 1, y + 1 });
            array.add(new int[] { x, y + 1 });
            return array;
        }

        @Override
        public Collection<int[]> offsets()
        {
            ArrayList<int[]> array = new ArrayList<int[]>(6);
            array.add(new int[] { 0, -1 });
            array.add(new int[] { +1, -1 });
            array.add(new int[] { -1, 0 });
            array.add(new int[] { +1, 0 });
            array.add(new int[] { -1, +1 });
            array.add(new int[] { 0, +1 });
            return array;
        }
    };

    /**
     * Planar connectivity that considers the eight neighbors (orthogonal plus
     * diagonal) of a pixel.
     */
    public static final Connectivity2D C8 = new Connectivity2D()
    {
        @Override
        public Collection<int[]> neighbors(int x, int y)
        {
            ArrayList<int[]> array = new ArrayList<int[]>(8);
            array.add(new int[] { x - 1, y - 1 });
            array.add(new int[] { x, y - 1 });
            array.add(new int[] { x + 1, y - 1 });
            array.add(new int[] { x - 1, y });
            array.add(new int[] { x + 1, y });
            array.add(new int[] { x - 1, y + 1 });
            array.add(new int[] { x, y + 1 });
            array.add(new int[] { x + 1, y + 1 });
            return array;
        }

        @Override
        public Collection<int[]> offsets()
        {
            ArrayList<int[]> array = new ArrayList<int[]>(8);
            array.add(new int[] { -1, -1 });
            array.add(new int[] { 0, -1 });
            array.add(new int[] { +1, -1 });
            array.add(new int[] { -1, 0 });
            array.add(new int[] { +1, 0 });
            array.add(new int[] { -1, +1 });
            array.add(new int[] { 0, +1 });
            array.add(new int[] { +1, +1 });
            return array;
        }
    };

    
    // =============================================================
    // Static methods

    /**
     * Converts a connectivity from any dimensionality into an instance of
     * Connectivity2D. If the specified connectivity is already an instance of
     * Connectivity2D, it is simply returned.
     * 
     * @param conn
     *            the connectivity to convert or to cast
     * @return an instance of Connectivity2D
     */
    public static Connectivity2D convert(Connectivity conn)
    {
        if (conn instanceof Connectivity2D)
        {
            return (Connectivity2D) conn;
        }
        
        int[] offset0 = new int[] {0, 0};
        
        ArrayList<int[]> offsets = new ArrayList<int[]>(4);
        int nd = Math.min(conn.dimensionality(), 2);
        for (int[] offset : conn.offsets())
        {
            // create new 2D offset
            int[] offset2d = new int[2];
            System.arraycopy(offset, 0, offset2d, 0, nd);
            
            // add offset only if it does not already exist
            if (!contains(offsets, offset2d) && !equals(offset2d, offset0))
            {
                offsets.add(offset2d);
            }
        }
        
        return new GenericConnectivity2D(offsets);
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
     *            the connectivity value, either 4 or 8
     * @return a Connectivity object
     */
    public static Connectivity2D fromValue(int conn)
    {
        if (conn == 4)
            return C4;
        else if (conn == 8)
            return C8;
        else
            throw new IllegalArgumentException("Connectivity value should be either 4 or 8");
    }

    // =============================================================
    // New methods

    /**
     * Returns the set of neighbors associated to a given position
     * 
     * @param x
     *            the x position of the pixel
     * @param y
     *            the y position of the pixel
     * @return the list of neighbors of specified pixel
     */
    public default Collection<int[]> neighbors(int x, int y)
    {
        Collection<int[]> offsets = offsets();
        ArrayList<int[]> res = new ArrayList<int[]>(offsets.size());
        for (int[] offset : offsets)
        {
            res.add(new int[] { x + offset[0], y + offset[1] });
        }
        return res;
    }
    

    // =============================================================
    // Default implementation of the Connectivity interface

    public default Collection<int[]> neighbors(int[] pos)
    {
        return neighbors(pos[0], pos[1]);
    }

    
    // =============================================================
    // Default implementation of the Dimensional interface

    @Override
    public default int dimensionality()
    {
        return 2;
    }
}
