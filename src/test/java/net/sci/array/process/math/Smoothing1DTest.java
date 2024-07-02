/**
 * 
 */
package net.sci.array.process.math;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;

/**
 * @author dlegland
 *
 */
public class Smoothing1DTest
{
    /**
     * Test method for {@link net.sci.array.process.math.Smoothing1D#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Dim0()
    {
        Float32Array2D array = Float32Array2D.create(10,  10);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 8; x++)
            {
                array.setValue(x, y, 1.0);
            }
        }

        Smoothing1D op0 = new Smoothing1D(0);
        Float32Array diff0 = (Float32Array) op0.process(array);
        assertEquals(diff0.getValue(new int[] {0, 5}), 0.0, .001);
        assertEquals(diff0.getValue(new int[] {2, 5}), 2.0/3.0, .001);
        assertEquals(diff0.getValue(new int[] {5, 5}), 1.0, .001);
        assertEquals(diff0.getValue(new int[] {7, 5}), 2.0/3.0, .001);
        assertEquals(diff0.getValue(new int[] {9, 5}), 0.0, .001);
    }

    /**
     * Test method for {@link net.sci.array.process.math.Smoothing1D#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Dim1()
    {
        Float32Array2D array = Float32Array2D.create(10,  10);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 8; x++)
            {
                array.setValue(x, y, 1.0);
            }
        }
        
        Smoothing1D op1 = new Smoothing1D(1);
        Float32Array diff1 = (Float32Array) op1.process(array);
        assertEquals(diff1.getValue(new int[] {5, 0}), 0.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 2}), 2.0/3.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 5}), 1.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 7}), 2.0/3.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 9}), 0.0, .001);
    }
}
