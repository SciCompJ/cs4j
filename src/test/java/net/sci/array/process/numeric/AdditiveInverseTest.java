/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.scalar.Int;
import net.sci.array.scalar.Int32Array2D;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.vector.Float32Vector;
import net.sci.array.vector.Float32VectorArray;
import net.sci.array.vector.Float32VectorArray2D;
import net.sci.array.vector.VectorArray2D;

/**
 * 
 */
public class AdditiveInverseTest
{
    /**
     * Test method for {@link net.sci.array.process.numeric.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Int32()
    {
        Int32Array2D array = Int32Array2D.create(8, 6);
        array.fillInts((x,y) -> y * 10 + x);
        
        AdditiveInverse op = new AdditiveInverse();
        Array<?> res = op.process(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertTrue(res.sampleElement() instanceof Int);
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray2D<?> res2 = IntArray2D.wrap(IntArray.wrap((Array<Int>) res));
        assertEquals(res2.getInt(0, 0),   0);
        assertEquals(res2.getInt(7, 0),  -7);
        assertEquals(res2.getInt(0, 5), -50);
        assertEquals(res2.getInt(7, 5), -57);
    }
    
    /**
     * Test method for {@link net.sci.array.process.numeric.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Float32Vector()
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(8, 6, 2);
        array.fill((x,y) -> new Float32Vector(new float[] {(float) x, (float) y}));
        
        AdditiveInverse op = new AdditiveInverse();
        Array<?> res = op.process(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertTrue(res.sampleElement() instanceof Float32Vector);
        
        @SuppressWarnings({ "unchecked" })
        VectorArray2D<?,?> res2 = VectorArray2D.wrap(Float32VectorArray.wrap((Array<Float32Vector>) res));
        assertEquals(res2.getValue(0, 0, 0),   0.0, 0.01);
        assertEquals(res2.getValue(0, 0, 1),   0.0, 0.01);
        assertEquals(res2.getValue(7, 0, 0),  -7.0, 0.01);
        assertEquals(res2.getValue(7, 0, 1),   0.0, 0.01);
        assertEquals(res2.getValue(0, 5, 0),   0.0, 0.01);
        assertEquals(res2.getValue(0, 5, 1),  -5.0, 0.01);
        assertEquals(res2.getValue(7, 5, 0),  -7.0, 0.01);
        assertEquals(res2.getValue(7, 5, 1),  -5.0, 0.01);
    }
}
