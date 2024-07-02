/**
 * 
 */
package net.sci.image.label;

import java.util.Set;
import java.util.TreeSet;

import net.sci.array.Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.IntArray3D;
import net.sci.image.Image;

/**
 * Computes the list of region adjacencies within an integer array of region 
 * labels.
 * 
 * <p>Example: <pre>{@code
 * byte[] data = new byte[]{
 *     1, 1, 1, 0, 2, 2, 2,
 *     1, 1, 0, 5, 0, 2, 2,
 *     1, 0, 5, 5, 5, 0, 2,
 *     0, 5, 5, 5, 5, 5, 0,
 *     3, 0, 5, 5, 5, 0, 4,
 *     3, 3, 0, 5, 0, 4, 4,
 *     3, 3, 3, 0, 4, 4, 4 };
 * UInt8Array2D array = new BufferedUInt8Array2D(7, 7, data);
 * Set<LabelPair> adjacencies = RegionAdjacencies.computeAdjacencies(array);
 * }</pre>
 * 
 * @author dlegland
 *
 */
public class RegionAdjacencies
{
    /**
     * Private constructor to prevent class instantiation.
     */
    private RegionAdjacencies()
    {
    }

    /**
     * Returns the set of region adjacencies in an Image, either 2D or 3D.
     * 
     * @param image
     *            an Image containing a 2D or 3D integer array
     * @return the set of adjacencies within the image
     */
    public static final Set<LabelPair> computeAdjacencies(Image image)
    {
        Array<?> array = image.getData();
        if (!(array instanceof IntArray)) 
        {
            throw new IllegalArgumentException("Requires an image containing an instance of IntArray");
        }
         
        if (array.dimensionality() == 2)
        {
            return computeAdjacencies(IntArray2D.wrap((IntArray<?>) array));
        }
        else if (array.dimensionality() == 3)
        {
            return computeAdjacencies(IntArray3D.wrap((IntArray<?>) array));
        }
        throw new IllegalArgumentException("Can only process arrays with dimensionality 2 or 3");
    }

    /**
     * Returns the set of region adjacencies in an integer array of labels.
     * 
     * @param array
     *            an integer array containing labels
     * @return the set of adjacencies within the image
     */
    public static final Set<LabelPair> computeAdjacencies(IntArray2D<?> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        TreeSet<LabelPair> list = new TreeSet<LabelPair>();
        
        // transitions in x direction
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX - 2; x++)
            {
                int label = array.getInt(x, y);
                if (label == 0)
                    continue;
                int label2 = array.getInt(x + 2, y);
                if (label2 == 0 || label2 == label)
                    continue;
                
                LabelPair pair = new LabelPair(label, label2);
                list.add(pair);
            }
        }
        
        // transitions in y direction
        for (int y = 0; y < sizeY - 2; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = array.getInt(x, y);
                if (label == 0)
                    continue;
                int label2 = array.getInt(x, y + 2);
                if (label2 == 0 || label2 == label)
                    continue;
                
                LabelPair pair = new LabelPair(label, label2);
                list.add(pair);
            }
        }
        
        return list;
    }
    
    /**
    * Returns the set of region adjacencies in an integer array of labels.
    * 
    * @param array
    *            an integer array containing labels
     * @return the set of adjacencies within the image
     */
    public static final Set<LabelPair> computeAdjacencies(IntArray3D<?> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        TreeSet<LabelPair> list = new TreeSet<LabelPair>();
        
        // transitions in x direction
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX - 2; x++)
                {
                    int label = array.getInt(x, y, z);
                    if (label == 0)
                        continue;
                    int label2 = array.getInt(x + 2, y, z);
                    if (label2 == 0 || label2 == label)
                        continue;

                    LabelPair pair = new LabelPair(label, label2);
                    list.add(pair);
                }
            }
        }

        // transitions in y direction
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY - 2; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = array.getInt(x, y, z);
                    if (label == 0)
                        continue;
                    int label2 = array.getInt(x, y + 2, z);
                    if (label2 == 0 || label2 == label)
                        continue;

                    LabelPair pair = new LabelPair(label, label2);
                    list.add(pair);
                }
            }
        }

        // transitions in z direction
        for (int z = 0; z < sizeZ - 2; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = array.getInt(x, y, z);
                    if (label == 0)
                        continue;
                    int label2 = array.getInt(x, y, z + 2);
                    if (label2 == 0 || label2 == label)
                        continue;

                    LabelPair pair = new LabelPair(label, label2);
                    list.add(pair);
                }
            }
        }
    
        return list;
    }

    /**
     * Used to stores the adjacency information between two regions. In order to
     * ensure symmetry of the relation, the value of label1 field always
     * contains the lower label, while the value of label2 always contains the
     * highest label.
     */
    public static final class LabelPair implements Comparable <LabelPair>
    {
        public int label1;
        public int label2;
        
        public LabelPair(int label1, int label2)
        {
            if (label1 < label2) 
            {
                this.label1 = label1;
                this.label2 = label2;
            }
            else
            {
                this.label1 = label2;
                this.label2 = label1;
            }

        }

        @Override
        public int compareTo(LabelPair pair) {
            if (this.label1 < pair.label1)
                return -1;
            if (this.label1 > pair.label1)
                return +1;
            if (this.label2 < pair.label2)
                return -1;
            if (this.label2 > pair.label2)
                return +1;
            return 0;
        }
    }

}
