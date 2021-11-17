/**
 * 
 */
package net.sci.image.morphology.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.image.morphology.strel.Cross3x3Strel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * @author dlegland
 *
 */
public class BinaryErosionTest
{
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryErosion#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_cross3x3()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(12, 12);
        
        // a thick ring-like structure
        fillRect(array, 1, 9, 1, 9, true);
        array.setBoolean(5, 5, false);
                
        // create an erosion using a cross structuring element
        Strel2D strel = new Cross3x3Strel();
        BinaryErosion op = new BinaryErosion(strel);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel.erosion(array)));
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryErosion#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_square5x5()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(14, 14);
        
        // a thick ring-like structure
        fillRect(array, 1, 12, 1, 12, true);
        fillRect(array, 6, 8, 7, 7, false);
        
        // create an erosion using a square structuring element
        Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
        BinaryErosion op = new BinaryErosion(strel);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel.erosion(array)));
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }
    
    
    private static final void fillRect(BinaryArray2D array, int xmin, int xmax, int ymin, int ymax, boolean state)
    {
        for (int y = ymin; y <= ymax; y++)
        {
            for (int x = xmin; x <= xmax; x++)
            {
                array.setBoolean(x, y, state);
            }
        }
    }
    
}
