/**
 * 
 */
package net.sci.array.numeric.interp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float32Array3D;

/**
 * @author dlegland
 *
 */
public class NearestNeighborInterpolatedArray3DTest
{
    /**
     * Test method for {@link net.sci.array.numeric.interp.LinearInterpolatedArray2D#evaluate(double, double)}.
     */
    @Test
    public final void testEvaluate_simple()
    {
        // Create a sample array with a single value at position (5,5)
        Float32Array3D array = Float32Array3D.create(10, 10, 10);
        array.setValue(5, 5, 5, 100.0);
        // Create interpolator for input array
        NearestNeighborInterpolatedArray3D interp = new NearestNeighborInterpolatedArray3D(array);
        
        // evaluate value close to the defined value
        double value = interp.evaluate(4.6, 4.6, 4.6);
        assertEquals(100.0, value, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.interp.NearestNeighborInterpolatedArray3D#evaluate(double, double, double)}.
     */
    @Test
    public final void testEvaluate_bounds()
    {
        // Create a demo image
        Float32Array3D array = Float32Array3D.create(10, 10, 10);
        for (int z = 3; z < 7; z++)
        {
            for (int y = 3; y < 7; y++)
            {
                for (int x = 3; x < 7; x++)
                {
                    array.setValue(x, y, z, 100.0);
                }
            }
        }

        // Create interpolator for input array
        NearestNeighborInterpolatedArray3D interp = new NearestNeighborInterpolatedArray3D(array);
        
        // check in the middle of array
        assertEquals(100.0, interp.evaluate(5.0, 5.0, 5.0), 0.01);
        
        // check array bounds
        assertEquals(0.0, interp.evaluate(0.0, 0.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 0.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(0.0, 9.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 9.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(0.0, 0.0, 9.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 0.0, 9.0), 0.01);
        assertEquals(0.0, interp.evaluate(0.0, 9.0, 9.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 9.0, 9.0), 0.01);

        // check out of bounds
        assertEquals(0.0, interp.evaluate(-5.0, 5.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(15.0, 5.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, -5.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, 15.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, 5.0, -5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, 5.0, 15.0), 0.01);
    }

}
