/**
 * 
 */
package net.sci.array.process.math;

import static org.junit.Assert.assertEquals;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GradientTest
{
    /**
     * Test method for {@link net.sci.array.process.math.Gradient#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        Float32Array2D array = Float32Array2D.create(10,  10);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 8; x++)
            {
                array.setValue(x, y, 1.0);
            }
        }

        Gradient op0 = new Gradient();
        VectorArray<?,?> grad = (VectorArray<?,?>) op0.process(array);

        ScalarArray<?> diff0 = grad.channel(0);
        assertEquals(diff0.getValue(new int[] {0, 5}),  0.0, .001);
        assertEquals(diff0.getValue(new int[] {2, 5}),  0.5, .001);
        assertEquals(diff0.getValue(new int[] {5, 5}),  0.0, .001);
        assertEquals(diff0.getValue(new int[] {7, 5}), -0.5, .001);
        assertEquals(diff0.getValue(new int[] {9, 5}),  0.0, .001);

        ScalarArray<?> diff1 = grad.channel(1);
        assertEquals(diff1.getValue(new int[] {5, 0}),  0.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 2}),  0.5, .001);
        assertEquals(diff1.getValue(new int[] {5, 5}),  0.0, .001);
        assertEquals(diff1.getValue(new int[] {5, 7}), -0.5, .001);
        assertEquals(diff1.getValue(new int[] {5, 9}),  0.0, .001);
    }
}
