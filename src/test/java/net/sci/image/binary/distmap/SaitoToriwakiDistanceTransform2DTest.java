/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;

/**
 * 
 */
public class SaitoToriwakiDistanceTransform2DTest
{

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform2D#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public void test_process2d_centeredRectangle()
    {
        // Create a black image with a white 8-by-6 rectangle in the middle
        BinaryArray2D array = BinaryArray2D.create(12, 10);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 10; x++)
            {
                array.setBoolean(x, y, true);
            }
        }

        DistanceTransform2D algo = new SaitoToriwakiDistanceTransform2D();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(3, result.getValue(4, 4), 0.001);
    }

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform2D#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public void test_process2d_fromCorners()
    {
        // Create a white image, with only the corners missing
        BinaryArray2D array = BinaryArray2D.create(13, 9);
        array.fill(true);
        array.setBoolean(0, 0, false);
        array.setBoolean(12, 0, false);
        array.setBoolean(0, 8, false);
        array.setBoolean(12, 8, false);

        DistanceTransform2D algo = new SaitoToriwakiDistanceTransform2D();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(Math.hypot(4, 6), result.getValue(6, 4), 0.001);
    }

    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform2D#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public void test_process2d_fromCenter()
    {
        // Create a white image, with a black pixel in the middle
        BinaryArray2D array = BinaryArray2D.create(13, 9);
        array.fill(true);
        array.setBoolean(6, 4, false);

        DistanceTransform2D algo = new SaitoToriwakiDistanceTransform2D();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(Math.hypot(4, 6), result.getValue( 0, 0), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue(12, 0), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue( 0, 8), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue(12, 8), 0.001);
    }

}
