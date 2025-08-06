/**
 * 
 */
package net.sci.image.label.skeleton;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.IntArray2D;

/**
 * Apply skeletonization on a binary image or to a label map.
 * 
 * Adaptation of the skeletonization code from ImageJ. In the case of a label
 * map, all regions are skeletonized during the same process.
 * 
 * 
 * Note: original IJ algo clears pixels on the boundary. This is not the case
 * here.
 * 
 * @author dlegland
 *
 */
public class ImageJSkeleton extends AlgoStub
{
    /**
     * The look-up-table for converting between index of 3-by-3 configuration
     * (256 indices, without central pixel) and a deletion flag for central
     * vertex.
     * 
     * 1 -> delete in first pass 
     * 2 -> delete in second pass 
     * 3 -> delete in either pass
     */
    private static final int[] table1  =
        {
                // 0->3  4        8        12       16       20       24       28
                0,0,0,0, 0,0,1,3, 0,0,3,1, 1,0,1,3, 0,0,0,0, 0,0,0,0, 0,0,2,0, 3,0,3,3,
                0,0,0,0, 0,0,0,0, 3,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 2,0,0,0, 3,0,2,2,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                2,0,0,0, 0,0,0,0, 2,0,0,0, 2,0,0,0, 3,0,0,0, 0,0,0,0, 3,0,0,0, 3,0,2,0,
                0,0,3,1, 0,0,1,3, 0,0,0,0, 0,0,0,1, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,1,
                3,1,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 2,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                2,3,1,3, 0,0,1,3, 0,0,0,0, 0,0,0,1, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                2,3,0,1, 0,0,0,1, 0,0,0,0, 0,0,0,0, 3,3,0,1, 0,0,0,0, 2,2,0,0, 2,0,0,0
        };

    /**
     * Another table for removing additional (spurious ?) pixels.
     */
    private static final int[] table2  =
        {
                // 0->3  4        8        12       16       20       24       28
                0,0,0,1, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 2,0,2,2, 0,0,0,0,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,2,0, 2,0,0,0, 0,0,0,0, 0,0,2,0, 0,0,0,0,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                0,1,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,2,0, 0,0,0,0,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 2,0,0,0, 0,0,0,0,
                0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
                0,0,1,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0
        };

    /**
     * Creates a new skeletonizer.
     */
    public ImageJSkeleton()
    {
    }

    public IntArray2D<?> process2d(IntArray2D<?> array)
    {
        // create result image
        IntArray2D<?> result = array.duplicate();
        
        int removedPixels;
        do
        {
            removedPixels = thin(result, table1, 1);
            removedPixels += thin(result, table1, 2);
        } while (removedPixels > 0);
        
        // use a second table to remove "stuck" pixels
        do
        {
            removedPixels = thin(result, table2, 1);
            removedPixels += thin(result, table2, 2);
        } while (removedPixels > 0);

        return result;
    }
    
    /**
     * Applies a two-passes thinning operation on the input array, using the
     * pass number and the specified look-up table.
     * 
     * @param array
     *            the binary array to process
     * @param pass
     *            the number of the pass (1 or 2)
     * @param table
     *            the look-up-table indicating for each configuration whether it
     *            should be removed or not
     * @return the number of removed pixels.
     */
    private int thin(IntArray2D<?> array, int[] table, int pass)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // keep information about original pixels
        IntArray2D<?> copy = array.duplicate();
        
        // count the number of removed pixels
        int removedPixels = 0;
        
        // Iterate over image pixels
        // Note iterate over *all* pixels, whereas original ImageJ algorithm
        // does not consider border pixels.
        for (int  y = 0; y < sizeY; y++)
        {
            for (int  x = 0; x < sizeX; x++)
            {
                // retrieve label of current pixel
                int label = copy.getInt(x, y);
                
                // do not process background pixels
                if (label == 0)
                {
                    continue;
                }
                
                // determine index of current 3-by-3 configuration
                int index = 0;
                // Process neighbor pixels on previous line
                if (y > 0)
                {
                    if (x > 0)
                    {
                        if (copy.getInt(x-1, y-1) == label) index |=  1;
                    }
                    if (copy.getInt(x, y-1) == label) index |=  2;
                    if (x < sizeX - 1)
                    {
                        if (copy.getInt(x+1, y-1) == label) index |=  4;
                    }
                }
                // Process neighbor pixels on current line
                if (x > 0)
                {
                    if (copy.getInt(x-1, y) == label) index |= 128;
                }
                if (x < sizeX - 1)
                {
                    if (copy.getInt(x+1, y) == label) index |= 8;
                }
                // Process neighbor pixels on next line
                if (y < sizeY-1)
                {
                    if (x > 0)
                    {
                        if (copy.getInt(x-1, y+1) == label) index |= 64;
                    }
                    if (copy.getInt(x, y+1) == label) index |= 32;
                    if (x < sizeX - 1)
                    {
                        if (copy.getInt(x+1, y+1) == label) index |= 16;
                    }
                }
                
                int code = table[index];
                if ((code & pass) > 0)
                {
                    array.setInt(x, y, 0);
                    removedPixels++;
                }
            }
        }
        
        return removedPixels;
    }
}
