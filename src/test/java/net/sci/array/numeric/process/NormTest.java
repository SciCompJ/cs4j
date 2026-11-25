/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.ScalarArray;

/**
 * 
 */
public class NormTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.Norm#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process()
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(5, 4, 2);
        array.fill((x, y) -> new Float32Vector(new float[] { x, y }));
        
        Norm algo = new Norm();
        ScalarArray<?> res = algo.process(array);
        
        assertEquals(2, res.dimensionality());
        assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
        assertEquals(0.0, res.getValue(new int[] {0, 0}), 0.01);
        assertEquals(4.0, res.getValue(new int[] {4, 0}), 0.01);
        assertEquals(3.0, res.getValue(new int[] {0, 3}), 0.01);
        assertEquals(5.0, res.getValue(new int[] {4, 3}), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.process.Norm#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_Array_Array()
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(5, 4, 2);
        array.fill((x, y) -> new Float32Vector(new float[] { x, y }));
        ScalarArray<?> output = Float32Array.create(5, 4);     
        Norm algo = new Norm();
        
        algo.processVectorArray(array, output);
        
        assertEquals(2, output.dimensionality());
        assertEquals(5, output.size(0));
        assertEquals(4, output.size(1));
        assertEquals(0.0, output.getValue(new int[] {0, 0}), 0.01);
        assertEquals(4.0, output.getValue(new int[] {4, 0}), 0.01);
        assertEquals(3.0, output.getValue(new int[] {0, 3}), 0.01);
        assertEquals(5.0, output.getValue(new int[] {4, 3}), 0.01);
    }
    
}
