/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.ImageType;

/**
 * 
 */
public class SaitoToriwakiDistanceTransformTest
{
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process(net.sci.image.Image)}.
     */
    @Test
    public final void testProcessImage()
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
        
        // create an image containing the array, with a non-standard calibration 
        Image image = new Image(array);
        image.getCalibration().setSpatialCalibration(new double[] {5.0, 4.0}, "nm");

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        Image result = algo.process(image);
        
        assertEquals(ImageType.DISTANCE, result.getType());
        ScalarArray2D<?> resArray = (ScalarArray2D<?>) result.getData();
        assertEquals(12.0, resArray.getValue(5, 4), 0.001);
        assertEquals(10.0, resArray.getValue(3, 4), 0.001);
//        assertEquals(result.getCalibration().getChannelAxis().
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_process2d_centeredRectangle()
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

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(3, result.getValue(4, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_process2d_fromCorners()
    {
        // Create a white image, with only the corners missing
        BinaryArray2D array = BinaryArray2D.create(13, 9);
        array.fill(true);
        array.setBoolean(0, 0, false);
        array.setBoolean(12, 0, false);
        array.setBoolean(0, 8, false);
        array.setBoolean(12, 8, false);

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(Math.hypot(4, 6), result.getValue(6, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_process2d_fromCenter()
    {
        // Create a white image, with only the corners missing
        BinaryArray2D array = BinaryArray2D.create(13, 9);
        array.fill(true);
        array.setBoolean(6, 4, false);

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray2D<?> result = algo.process2d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(Math.hypot(4, 6), result.getValue(0, 0), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue(12, 0), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue(0, 0), 0.001);
        assertEquals(Math.hypot(4, 6), result.getValue(12, 8), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#distanceMap2d(net.sci.array.binary.BinaryArray2D, double[])}.
     */
    @Test
    public final void test_distanceMap2d()
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

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray2D<?> result = algo.distanceMap2d(array, new double[] {5.0, 4.0});
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(12.0, result.getValue(5, 4), 0.001);
        assertEquals(10.0, result.getValue(3, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_process3d_centered_rectangle()
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

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray3D<?> result = algo.process3d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        assertEquals(3, result.getValue(4, 4, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_process3d_fromCorners()
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

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray3D<?> result = algo.process3d(array);
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        
        double exp = Math.hypot(Math.hypot(6, 5), 4);
        assertEquals(exp, result.getValue(6, 5, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#process3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_process3d_fromCenter()
    {
        // Create a white image, with only the corners missing
        BinaryArray3D array = BinaryArray3D.create(13, 11, 9);
        array.fill(true);
        array.setBoolean(6, 5, 4, false);

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
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
    
    /**
     * Test method for {@link net.sci.image.binary.distmap.SaitoToriwakiDistanceTransform#distanceMap3d(net.sci.array.binary.BinaryArray3D, double[])}.
     */
    @Test
    public final void test_distanceMap3d()
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

        SaitoToriwakiDistanceTransform algo = new SaitoToriwakiDistanceTransform();
        ScalarArray3D<?> result = algo.distanceMap3d(array, new double[] {5, 4, 7});
        
        assertNotNull(result);
        assertEquals(array.size(0), result.size(0));
        assertEquals(array.size(1), result.size(1));
        assertEquals(array.size(2), result.size(2));
        assertEquals(12.0, result.getValue(5, 4, 4), 0.001);
        assertEquals(10.0, result.getValue(3, 4, 4), 0.001);
    }
    
}
