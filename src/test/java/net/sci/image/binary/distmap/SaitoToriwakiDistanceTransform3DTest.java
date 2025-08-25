/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray3D;

/**
 * 
 */
public class SaitoToriwakiDistanceTransform3DTest
{

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform3D#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void test_process3d_centered_rectangle()
    {
        // Create a black image with a white 8-by-6 rectangle in the middle
        BinaryArray3D array = BinaryArray3D.create(14, 12, 10);
        for (int z = 2; z < 8; z++)
        {
            for (int y = 2; y < 10; y++)
            {
                for (int x = 2; x < 12; x++)
                {
                    array.setBoolean(x, y, z, true);
                }
            }
        }

        DistanceTransform3D algo = new SaitoToriwakiDistanceTransform3D();
        ScalarArray3D<?> result = algo.process3d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        assertEquals(3, result.getValue(4, 4, 4), 0.001);
    }

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform3D#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void test_process3d_fromCorners()
    {
        // Create a white image, with only the corners missing
        BinaryArray3D array = BinaryArray3D.create(13, 11, 9);
        array.fill(true);
        array.setBoolean(0, 0, 0, false);
        array.setBoolean(12, 0, 0, false);
        array.setBoolean(0, 10, 0, false);
        array.setBoolean(12, 10, 0, false);
        array.setBoolean(0, 0, 8, false);
        array.setBoolean(12, 0, 8, false);
        array.setBoolean(0, 10, 8, false);
        array.setBoolean(12, 10, 8, false);

        DistanceTransform3D algo = new SaitoToriwakiDistanceTransform3D();
        ScalarArray3D<?> result = algo.process3d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        
        double exp = Math.hypot(Math.hypot(6, 5), 4);
        assertEquals(exp, result.getValue(6, 5, 4), 0.001);
    }

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform3D#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void test_process3d_fromCenter()
    {
        // Create a white image, with only the corners missing
        BinaryArray3D array = BinaryArray3D.create(13, 11, 9);
        array.fill(true);
        array.setBoolean(6, 5, 4, false);

        DistanceTransform3D algo = new SaitoToriwakiDistanceTransform3D();
        ScalarArray3D<?> result = algo.process3d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        
        double exp = Math.hypot(Math.hypot(6, 5), 4);
        assertEquals(exp, result.getValue(0, 0, 0), 0.001);
        assertEquals(exp, result.getValue(12, 0, 0), 0.001);
        assertEquals(exp, result.getValue(0, 10, 0), 0.001);
        assertEquals(exp, result.getValue(12, 10, 0), 0.001);
        assertEquals(exp, result.getValue(0, 0, 8), 0.001);
        assertEquals(exp, result.getValue(12, 0, 8), 0.001);
        assertEquals(exp, result.getValue(0, 10, 8), 0.001);
        assertEquals(exp, result.getValue(12, 10, 8), 0.001);
    }

}
