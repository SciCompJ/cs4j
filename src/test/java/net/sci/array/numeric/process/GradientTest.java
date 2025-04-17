/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float64Vector;
import net.sci.array.numeric.Float64VectorArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;

/**
 * @author dlegland
 *
 */
public class GradientTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.Gradient#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        Float32Array2D array = createInputArray();

        Gradient op = new Gradient();
        VectorArray<?,?> grad = (VectorArray<?,?>) op.process(array);

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
    
    /**
     * Test method for {@link net.sci.array.numeric.process.Gradient#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Float64VectorOutput()
    {
        Float32Array2D array = createInputArray();

        Gradient op = new Gradient();
        op.setFactory(Float64VectorArray.defaultFactory);
        VectorArray<?,?> grad = (VectorArray<?,?>) op.process(array);
        
        assertTrue(grad.sampleElement() instanceof Float64Vector);
        assertTrue(grad instanceof Float64VectorArray);

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
    
    private static final Float32Array2D createInputArray()
    {
        Float32Array2D array = Float32Array2D.create(10,  10);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 8; x++)
            {
                array.setValue(x, y, 1.0);
            }
        }
        return array;
    }
}
