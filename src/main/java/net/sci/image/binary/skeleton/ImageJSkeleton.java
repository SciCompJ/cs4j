/**
 * 
 */
package net.sci.image.binary.skeleton;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.BinaryArray2D;

/**
 * Adaptation of the skeletonzation code from ImageJ.
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
    private static int[] table  =
            //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1
             {0,0,0,0,0,0,1,3,0,0,3,1,1,0,1,3,0,0,0,0,0,0,0,0,0,0,2,0,3,0,3,3,
              0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,3,0,2,2,
              0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
              2,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,3,0,0,0,0,0,0,0,3,0,0,0,3,0,2,0,
              0,0,3,1,0,0,1,3,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
              3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
              2,3,1,3,0,0,1,3,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
              2,3,0,1,0,0,0,1,0,0,0,0,0,0,0,0,3,3,0,1,0,0,0,0,2,2,0,0,2,0,0,0};

    /**
     * Another table for removing additional (spurious ?) pixels.
     */
    private static int[] table2  =
            //0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1
           {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,2,2,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    /**
     * Creates a new skeletonizer.
     */
    public ImageJSkeleton()
    {
    }

    public BinaryArray2D process2d(BinaryArray2D array)
    {
       
        BinaryArray2D result = array.duplicate();
        
        // original IJ algo clears pixels on the boundary
        clearBorders(result);
        
        int removedPixels;
        do
        {
            removedPixels = thin(result, 1, table);
            removedPixels += thin(result, 2, table);
        } while (removedPixels > 0);
        
        // use a second table to remove "stuck" pixels
        do
        {
            removedPixels = thin(result, 1, table2);
            removedPixels += thin(result, 2, table2);
        } while (removedPixels > 0);

        return result;
        
    }
    
    private void clearBorders(BinaryArray2D array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // clear left and right borders
        for (int y = 0; y < sizeY; y++)
        {
            array.setBoolean(0, y, false);
            array.setBoolean(sizeX-1, y, false);
        }

        // clear top and bottom borders
        for (int x = 0; x < sizeX; x++)
        {
            array.setBoolean(x, 0, false);
            array.setBoolean(x, sizeY-1, false);
        }
    }
    
    private int thin(BinaryArray2D array, int pass, int[] table)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        BinaryArray2D array2 = array.duplicate();
        
        
        // Original IJ algorithm does not process image borders
        int removedPixels = 0;
        for (int  y = 1; y < sizeY-1; y++)
        {
            for (int  x = 1; x < sizeX-1; x++)
            {
                // do not process background pixels
                if (!array2.getBoolean(x, y))
                {
                    continue;
                }

                // determine index of current X-by-3 configuration
                int index = 0;
                if (array2.getBoolean(x-1, y-1)) index |=  1;
                if (array2.getBoolean(  x, y-1)) index |=  2;
                if (array2.getBoolean(x+1, y-1)) index |=  4;
                if (array2.getBoolean(x-1,   y)) index |= 128;
                if (array2.getBoolean(x+1,   y)) index |= 8;
                if (array2.getBoolean(x-1, y+1)) index |= 64;
                if (array2.getBoolean(  x, y+1)) index |= 32;
                if (array2.getBoolean(x+1, y+1)) index |= 16;
                
                int code = table[index];
                if ((code & pass) > 0)
                {
                    array.setBoolean(x, y, false);
                    removedPixels++;
                }
            }
        }
        
        return removedPixels;
    }
}
