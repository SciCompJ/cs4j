/**
 * 
 */
package net.sci.image.morphology.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;

/**
 * @author dlegland
 *
 */
public class BallBinaryDilationTest
{
    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_singlePixel_dilatedBy_square()
    {
        BinaryArray2D array = BinaryArray2D.create(5, 5);
        array.setBoolean(2, 2, true);
        
        double radius = 1;
        BallBinaryDilation op = new BallBinaryDilation(radius);
        
        BinaryArray2D res = op.processBinary2d(array);
//        System.out.println(res);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(3, 1));
        assertTrue(res.getBoolean(1, 3));
        assertTrue(res.getBoolean(3, 3));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(4, 0));
        assertFalse(res.getBoolean(0, 4));
        assertFalse(res.getBoolean(4, 4));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_PixelsAtCorners_dilatedBy_square()
    {
        BinaryArray2D array = BinaryArray2D.create(9, 9);
        array.setBoolean(0, 0, true);
        array.setBoolean(8, 0, true);
        array.setBoolean(0, 8, true);
        array.setBoolean(8, 8, true);
       
        double radius = 2;
        BallBinaryDilation op = new BallBinaryDilation(radius);
        
        BinaryArray2D res = op.processBinary2d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertTrue(res.getBoolean(2, 1));
        assertTrue(res.getBoolean(6, 1));
        assertTrue(res.getBoolean(1, 2));
        assertTrue(res.getBoolean(7, 2));
        assertTrue(res.getBoolean(1, 6));
        assertTrue(res.getBoolean(7, 6));
        assertTrue(res.getBoolean(2, 7));
        assertTrue(res.getBoolean(6, 7));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryDilation#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testProcessBinary3d_singleVoxel_BallR1_3()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 5, 5);
        array.setBoolean(2, 2, 2, true);
        
        double radius = 1.3;
        BallBinaryDilation op = new BallBinaryDilation(radius);
        
        BinaryArray3D res = op.processBinary3d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertTrue(res.getBoolean(1, 1, 1));
        assertTrue(res.getBoolean(3, 1, 1));
        assertTrue(res.getBoolean(1, 3, 1));
        assertTrue(res.getBoolean(3, 3, 1));
        assertTrue(res.getBoolean(1, 1, 3));
        assertTrue(res.getBoolean(3, 1, 3));
        assertTrue(res.getBoolean(1, 3, 3));
        assertTrue(res.getBoolean(3, 3, 3));
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(4, 0, 0));
        assertFalse(res.getBoolean(0, 4, 0));
        assertFalse(res.getBoolean(4, 4, 0));
        assertFalse(res.getBoolean(0, 0, 4));
        assertFalse(res.getBoolean(4, 0, 4));
        assertFalse(res.getBoolean(0, 4, 4));
        assertFalse(res.getBoolean(4, 4, 4));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryDilation#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testProcessBinary3d_cubeCorners_BallR1()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 5, 5);
        array.setBoolean(0, 0, 0, true);
        array.setBoolean(4, 0, 0, true);
        array.setBoolean(0, 4, 0, true);
        array.setBoolean(4, 4, 0, true);
        array.setBoolean(0, 0, 4, true);
        array.setBoolean(4, 0, 4, true);
        array.setBoolean(0, 4, 4, true);
        array.setBoolean(4, 4, 4, true);
        
        double radius = 1.0;
        BallBinaryDilation op = new BallBinaryDilation(radius);
        
        BinaryArray3D res = op.processBinary3d(array);
//        res.print(System.out);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertTrue(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(1, 1, 0));
        assertTrue(res.getBoolean(1, 0, 1));
        assertTrue(res.getBoolean(0, 1, 1));

        assertTrue(res.getBoolean(4, 0, 0));
        assertTrue(res.getBoolean(3, 1, 0));
        assertTrue(res.getBoolean(3, 0, 1));
        assertTrue(res.getBoolean(4, 1, 1));
        
        assertTrue(res.getBoolean(0, 4, 0));
        assertTrue(res.getBoolean(1, 3, 0));
        assertTrue(res.getBoolean(1, 4, 1));
        assertTrue(res.getBoolean(0, 3, 1));

        assertTrue(res.getBoolean(4, 4, 0));
        assertTrue(res.getBoolean(3, 3, 0));
        assertTrue(res.getBoolean(3, 4, 1));
        assertTrue(res.getBoolean(4, 3, 1));
        
        assertTrue(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(1, 1, 0));
        assertTrue(res.getBoolean(1, 0, 1));
        assertTrue(res.getBoolean(0, 1, 1));

        assertTrue(res.getBoolean(4, 0, 4));
        assertTrue(res.getBoolean(3, 1, 4));
        assertTrue(res.getBoolean(3, 0, 3));
        assertTrue(res.getBoolean(4, 1, 3));
        
        assertTrue(res.getBoolean(0, 4, 4));
        assertTrue(res.getBoolean(1, 3, 4));
        assertTrue(res.getBoolean(1, 4, 3));
        assertTrue(res.getBoolean(0, 3, 3));

        assertTrue(res.getBoolean(4, 4, 4));
        assertTrue(res.getBoolean(3, 3, 4));
        assertTrue(res.getBoolean(3, 4, 3));
        assertTrue(res.getBoolean(4, 3, 3));
    }

}
