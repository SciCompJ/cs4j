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
public class BallBinaryErosionTest
{
    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryErosion#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d()
    {
        BinaryArray2D array = BinaryArray2D.create(5, 5);
        for (int y = 1; y < 4; y++)
        {
            for (int x = 1; x < 4; x++)
            {
                array.setBoolean(x, y, true);
            }
        }
//        System.out.println(array);
        
        double radius = 1;
        BallBinaryErosion op = new BallBinaryErosion(radius);
        
        BinaryArray2D res = op.processBinary2d(array);
//        System.out.println(res);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertTrue(res.getBoolean(2, 2));
        assertFalse(res.getBoolean(2, 1));
        assertFalse(res.getBoolean(1, 2));
        assertFalse(res.getBoolean(3, 2));
        assertFalse(res.getBoolean(2, 3));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filtering.BallBinaryErosion#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testProcessBinary3d()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 5, 5);
        for (int z = 1; z < 4; z++)
        {
            for (int y = 1; y < 4; y++)
            {
                for (int x = 1; x < 4; x++)
                {
                    array.setBoolean(x, y, z, true);
                }
            }
        }
        
        double radius = 1.2;
        BallBinaryErosion op = new BallBinaryErosion(radius);
        
        BinaryArray3D res = op.processBinary3d(array);
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertTrue(res.getBoolean(2, 2, 2));
        assertFalse(res.getBoolean(2, 1, 2));
        assertFalse(res.getBoolean(1, 2, 2));
        assertFalse(res.getBoolean(3, 2, 2));
        assertFalse(res.getBoolean(2, 3, 2));
    }

}
