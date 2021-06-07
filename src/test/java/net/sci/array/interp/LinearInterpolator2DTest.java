/**
 * 
 */
package net.sci.array.interp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.sci.array.scalar.Float32Array2D;

/**
 * @author dlegland
 *
 */
class LinearInterpolator2DTest
{

    /**
     * Test method for {@link net.sci.array.interp.LinearInterpolator2D#evaluate(double, double)}.
     */
    @Test
    final void testEvaluate_simple()
    {
        // Create a sample array with a single value at position (5,5)
        Float32Array2D array = Float32Array2D.create(10, 10);
        array.setValue(5, 5, 100.0);
        // Create interpolator for input array
        LinearInterpolator2D interp = new LinearInterpolator2D(array);
        // evaluate value close to the defined value
        double value = interp.evaluate(4.6, 4.6);
        assertEquals(36.0, value, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.array.interp.LinearInterpolator2D#evaluate(double, double)}.
     */
    @Test
    final void testEvaluate_bounds()
    {
        // Create a demo image
        Float32Array2D array = Float32Array2D.create(10, 10);
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                array.setValue(x, y, 100.0);
            }
        }
        
        // Create interpolator for input array
        LinearInterpolator2D interp = new LinearInterpolator2D(array);
        
        // check in the middle of array
        assertEquals(100.0, interp.evaluate(5.0, 5.0), 0.01);
        
        // check array bounds
        assertEquals(0.0, interp.evaluate(0.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 0.0), 0.01);
        assertEquals(0.0, interp.evaluate(0.0, 9.0), 0.01);
        assertEquals(0.0, interp.evaluate(9.0, 9.0), 0.01);

        // check out of bounds
        assertEquals(0.0, interp.evaluate(-5.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(15.0, 5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, -5.0), 0.01);
        assertEquals(0.0, interp.evaluate(5.0, 15.0), 0.01);
    }

}
