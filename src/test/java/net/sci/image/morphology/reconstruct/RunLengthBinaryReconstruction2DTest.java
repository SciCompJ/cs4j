/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray2D;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryReconstruction2DTest
{

    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.RunLengthBinaryReconstruction2D#reconstructRegion2d(net.sci.array.binary.BinaryArray2D, int, int)}.
     */
    @Test
    public final void testProcessBinary2d_Square()
    {
        RunLengthBinaryArray2D marker = new RunLengthBinaryArray2D(5, 5);
        marker.setBoolean(1, 1, true);
        RunLengthBinaryArray2D mask = new RunLengthBinaryArray2D(5, 5);
        fillRect(mask, 1, 3, 1, 3, true);
        
        RunLengthBinaryReconstruction2D algo = new RunLengthBinaryReconstruction2D();
        BinaryArray2D res = algo.processBinary2d(marker, mask);
        
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(1, 3));
        assertTrue(res.getBoolean(3, 1));
        assertTrue(res.getBoolean(3, 3));
        assertFalse(res.getBoolean(0, 1));
        assertFalse(res.getBoolean(4, 1));
        assertFalse(res.getBoolean(0, 3));
        assertFalse(res.getBoolean(4, 3));
        assertFalse(res.getBoolean(2, 0));
        assertFalse(res.getBoolean(2, 4));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.RunLengthBinaryReconstruction2D#reconstructRegion2d(net.sci.array.binary.BinaryArray2D, int, int)}.
     */
    @Test
    public final void testProcessBinary2d_SquareRing()
    {
        RunLengthBinaryArray2D marker = new RunLengthBinaryArray2D(5, 5);
        marker.setBoolean(1, 1, true);
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(10, 10);
        fillRect(array, 1, 8, 1, 8, true);
        fillRect(array, 3, 6, 3, 6, false);
        
        RunLengthBinaryReconstruction2D algo = new RunLengthBinaryReconstruction2D();
        BinaryArray2D res = algo.processBinary2d(marker, array);
        
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(1, 8));
        assertTrue(res.getBoolean(8, 1));
        assertTrue(res.getBoolean(8, 8));
        assertTrue(res.getBoolean(5, 8));
        assertFalse(res.getBoolean(0, 1));
        assertFalse(res.getBoolean(1, 0));
        assertFalse(res.getBoolean(3, 3));
        assertFalse(res.getBoolean(6, 3));
        assertFalse(res.getBoolean(3, 6));
        assertFalse(res.getBoolean(6, 6));
        assertFalse(res.getBoolean(9, 9));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.reconstruct.RunLengthBinaryReconstruction2D#reconstructRegion2d(net.sci.array.binary.BinaryArray2D, int, int)}.
     */
    @Test
    public final void test_processBinary2d_Snake()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(16, 10);
        fillRect(array,  1, 14, 1, 8, true);
        fillRect(array,  3,  6, 1, 6, false);
        fillRect(array,  9, 12, 3, 8, false);
        fillRect(array,  4,  5, 1, 4, true);
        fillRect(array, 10, 11, 5, 8, true);
        
        RunLengthBinaryArray2D marker = new RunLengthBinaryArray2D(16, 10);
        marker.setBoolean(1, 1, true);
        RunLengthBinaryReconstruction2D algo = new RunLengthBinaryReconstruction2D();
        BinaryArray2D res = algo.processBinary2d(marker, array);
        
        assertTrue(res.getBoolean( 1, 1));
        assertTrue(res.getBoolean(14, 8));
        assertFalse(res.getBoolean( 3, 1));
        assertFalse(res.getBoolean( 9, 3));
        assertFalse(res.getBoolean( 4, 5));
        assertFalse(res.getBoolean(10, 5));
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
