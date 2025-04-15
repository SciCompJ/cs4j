/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.ScalarArray;

/**
 * 
 */
public class VectorArrayMaxNormTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.process.VectorArrayMaxNorm#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(5, 4, 2);
        array.fill((x, y) -> new Float32Vector(new float[] { x, y }));
        
        VectorArrayMaxNorm algo = new VectorArrayMaxNorm();
        ScalarArray<?> res = algo.process(array);
        
        assertEquals(2, res.dimensionality());
        assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
        assertEquals(0.0, res.getValue(new int[] {0, 0}), 0.01);
        assertEquals(4.0, res.getValue(new int[] {4, 0}), 0.01);
        assertEquals(3.0, res.getValue(new int[] {0, 3}), 0.01);
        assertEquals(4.0, res.getValue(new int[] {4, 3}), 0.01);
    }
    
}
