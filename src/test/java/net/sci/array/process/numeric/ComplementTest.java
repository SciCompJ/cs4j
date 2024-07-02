/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class ComplementTest
{
    /**
     * Test method for {@link net.sci.array.process.numeric.Complement#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Scalar2D()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 6);
        array.fillInts((x,y) -> x);
        
        Complement op = new Complement(10);
        Array<?> res = op.process(array);
        
        assertTrue(res instanceof UInt8Array);
        ScalarArray2D<?> res2 = ScalarArray2D.wrapScalar2d((ScalarArray<?>) res);
        assertEquals(res2.getValue(0,0), 10.0, 0.01);
        assertEquals(res2.getValue(9,5),  1.0, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.array.process.numeric.Complement#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Float32Vector()
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(10, 6, 2);
        array.fill((x,y) -> new Float32Vector(new float[] {x, y}));
        
        Complement op = new Complement(new Float32Vector(new float[] {10, 6}));
        Array<?> res = op.process(array);
        
        assertTrue(res instanceof Float32VectorArray);
        Float32VectorArray res2 = (Float32VectorArray) res;
        assertEquals(res2.getValue(new int[] {0,0}, 0), 10.0, 0.01);
        assertEquals(res2.getValue(new int[] {0,0}, 1),  6.0, 0.01);
        assertEquals(res2.getValue(new int[] {9,5}, 0),  1.0, 0.01);
        assertEquals(res2.getValue(new int[] {9,5}, 1),  1.0, 0.01);
    }
}
