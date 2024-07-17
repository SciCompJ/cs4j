/**
 * 
 */
package net.sci.image.morphology.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.strel.Cross3x3Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * @author dlegland
 *
 */
public class BinaryErosionTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#erosion(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_erosion_singleRun_singleRun()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 5; i <= 15; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow strel = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            strel.set(i, true);
        }
        
        BinaryRow res = BinaryErosion.erosion(row, strel);
        
        assertEquals(1, res.runCount());
        assertFalse(res.get(6));
        assertTrue(res.get(7));
        assertTrue(res.get(13));
        assertFalse(res.get(14));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#erosion(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_erosion_TwoRuns_singleRun()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 5; i <= 10; i++)
        {
            row.set(i, true);
            row.set(i + 10, true);
        }
        
        BinaryRow strel = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            strel.set(i, true);
        }
        
        BinaryRow res = BinaryErosion.erosion(row, strel);
        
        assertEquals(2, res.runCount());
        assertFalse(res.get(6));
        assertTrue(res.get(7));
        assertTrue(res.get(8));
        assertFalse(res.get(9));
        assertFalse(res.get(16));
        assertTrue(res.get(17));
        assertTrue(res.get(18));
        assertFalse(res.get(19));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filtering.BinaryErosion#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
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
     * Test method for {@link net.sci.image.morphology.filtering.BinaryErosion#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
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
    
    /**
     * Test method for {@link net.sci.image.morphology.filtering.BinaryErosion#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void erosion_of_black_dot_by_Cube_should_result_in_black_cube()
    {
        // create a 3D array containing a black dot within a large (side=30) white cube
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(40, 40, 40);
        fillRect3d(array, 6, 34, 6, 34, 6, 34, true);
        array.setBoolean(20, 20, 20, false);
        
        // create the structuring element, and the operator
        Strel3D strel = Strel3D.Shape.CUBE.fromRadius(4);
        BinaryErosion op = new BinaryErosion(strel);
        
        // run the operator
        BinaryArray3D res = op.processBinary3d(array);
        
        // check some specific locations
        
        // center
        assertFalse(res.getBoolean(20, 20, 20));
        assertFalse(res.getBoolean(19, 20, 20));
        assertFalse(res.getBoolean(21, 20, 20));
        assertFalse(res.getBoolean(20, 19, 20));
        assertFalse(res.getBoolean(20, 21, 20));
        assertFalse(res.getBoolean(20, 20, 19));
        assertFalse(res.getBoolean(20, 20, 21));

        // image corners
        assertFalse(res.getBoolean( 0,  0,  0));
        assertFalse(res.getBoolean(39,  0,  0));
        assertFalse(res.getBoolean( 0, 39,  0));
        assertFalse(res.getBoolean(39, 39,  0));
        assertFalse(res.getBoolean( 0,  0, 39));
        assertFalse(res.getBoolean(39,  0, 39));
        assertFalse(res.getBoolean( 0, 39, 39));
        assertFalse(res.getBoolean(39, 39, 39));

        // new corners of enclosing cube
        assertTrue(res.getBoolean(10, 10, 10));
        assertTrue(res.getBoolean(30, 10, 10));
        assertTrue(res.getBoolean(10, 30, 10));
        assertTrue(res.getBoolean(30, 30, 10));
        assertTrue(res.getBoolean(10, 10, 30));
        assertTrue(res.getBoolean(30, 10, 30));
        assertTrue(res.getBoolean(10, 30, 30));
        assertTrue(res.getBoolean(30, 30, 30));

        // extremity of the inner cube in each direction
        assertFalse(res.getBoolean(16, 20, 20));
        assertFalse(res.getBoolean(24, 20, 20));
        assertFalse(res.getBoolean(20, 16, 20));
        assertFalse(res.getBoolean(20, 24, 20));
        assertFalse(res.getBoolean(20, 20, 16));
        assertFalse(res.getBoolean(20, 20, 24));
        assertTrue(res.getBoolean(15, 20, 20));
        assertTrue(res.getBoolean(25, 20, 20));
        assertTrue(res.getBoolean(20, 15, 20));
        assertTrue(res.getBoolean(20, 25, 20));
        assertTrue(res.getBoolean(20, 20, 15));
        assertTrue(res.getBoolean(20, 20, 25));

        // corners of the inner cube
        assertFalse(res.getBoolean(16, 16, 16));
        assertFalse(res.getBoolean(24, 16, 16));
        assertFalse(res.getBoolean(16, 24, 16));
        assertFalse(res.getBoolean(24, 24, 16));
        assertFalse(res.getBoolean(16, 16, 24));
        assertFalse(res.getBoolean(24, 16, 24));
        assertFalse(res.getBoolean(16, 24, 24));
        assertFalse(res.getBoolean(24, 24, 24));

        // middle of edges of inner cube (12 positions)
        assertFalse(res.getBoolean(20, 16, 16));
        assertFalse(res.getBoolean(16, 20, 16));
        assertFalse(res.getBoolean(16, 24, 16));
        assertFalse(res.getBoolean(24, 16, 16));
        assertFalse(res.getBoolean(16, 16, 20));
        assertFalse(res.getBoolean(24, 16, 20));
        assertFalse(res.getBoolean(16, 24, 20));
        assertFalse(res.getBoolean(24, 24, 20));
        assertFalse(res.getBoolean(20, 16, 24));
        assertFalse(res.getBoolean(16, 20, 24));
        assertFalse(res.getBoolean(16, 24, 24));
        assertFalse(res.getBoolean(24, 16, 24));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.filtering.BinaryErosion#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void erosion_of_black_dot_by_Ball_should_result_in_black_ball()
    {
        // create a 3D array containing a black dot within a large (side=30) white cube
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(40, 40, 40);
        fillRect3d(array, 5, 34, 5, 34, 5, 34, true);
        array.setBoolean(20, 20, 20, false);
        
        // create the structuring element, and the operator
        Strel3D strel = Strel3D.Shape.BALL.fromRadius(4);
        BinaryErosion op = new BinaryErosion(strel);
        
        // run the operator
        BinaryArray3D res = op.processBinary3d(array);
        
        // check some specific locations
        
        // center, and just around center
        assertFalse(res.getBoolean(20, 20, 20));
        assertFalse(res.getBoolean(19, 20, 20));
        assertFalse(res.getBoolean(21, 20, 20));
        assertFalse(res.getBoolean(20, 19, 20));
        assertFalse(res.getBoolean(20, 21, 20));
        assertFalse(res.getBoolean(20, 20, 19));
        assertFalse(res.getBoolean(20, 20, 21));

        // image corners
        assertFalse(res.getBoolean( 0,  0,  0));
        assertFalse(res.getBoolean(39,  0,  0));
        assertFalse(res.getBoolean( 0, 39,  0));
        assertFalse(res.getBoolean(39, 39,  0));
        assertFalse(res.getBoolean( 0,  0, 39));
        assertFalse(res.getBoolean(39,  0, 39));
        assertFalse(res.getBoolean( 0, 39, 39));
        assertFalse(res.getBoolean(39, 39, 39));

        // new corners of enclosing cube
        assertTrue(res.getBoolean(10, 10, 10));
        assertTrue(res.getBoolean(30, 10, 10));
        assertTrue(res.getBoolean(10, 30, 10));
        assertTrue(res.getBoolean(30, 30, 10));
        assertTrue(res.getBoolean(10, 10, 30));
        assertTrue(res.getBoolean(30, 10, 30));
        assertTrue(res.getBoolean(10, 30, 30));
        assertTrue(res.getBoolean(30, 30, 30));

        // extremity of the inner ball in each direction
        assertFalse(res.getBoolean(16, 20, 20));
        assertFalse(res.getBoolean(24, 20, 20));
        assertFalse(res.getBoolean(20, 16, 20));
        assertFalse(res.getBoolean(20, 24, 20));
        assertFalse(res.getBoolean(20, 20, 16));
        assertFalse(res.getBoolean(20, 20, 24));
        assertTrue(res.getBoolean(15, 20, 20));
        assertTrue(res.getBoolean(25, 20, 20));
        assertTrue(res.getBoolean(20, 15, 20));
        assertTrue(res.getBoolean(20, 25, 20));
        assertTrue(res.getBoolean(20, 20, 15));
        assertTrue(res.getBoolean(20, 20, 25));

        // middle of edges of inner cube (12 positions)
        // (here, result is different from cube)
        assertTrue(res.getBoolean(20, 16, 16));
        assertTrue(res.getBoolean(16, 20, 16));
        assertTrue(res.getBoolean(16, 24, 16));
        assertTrue(res.getBoolean(24, 16, 16));
        assertTrue(res.getBoolean(16, 16, 20));
        assertTrue(res.getBoolean(24, 16, 20));
        assertTrue(res.getBoolean(16, 24, 20));
        assertTrue(res.getBoolean(24, 24, 20));
        assertTrue(res.getBoolean(20, 16, 24));
        assertTrue(res.getBoolean(16, 20, 24));
        assertTrue(res.getBoolean(16, 24, 24));
        assertTrue(res.getBoolean(24, 16, 24));
    }
    
    private static final void fillRect3d(BinaryArray3D array, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, boolean state)
    {
        for (int z = zmin; z <= zmax; z++)
        {
            for (int y = ymin; y <= ymax; y++)
            {
                for (int x = xmin; x <= xmax; x++)
                {
                    array.setBoolean(x, y, z, state);
                }
            }
        }
    }
}
