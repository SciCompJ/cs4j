/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.VectorArray2D;

/**
 * 
 */
public class AdditiveInverseTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
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
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_Float32()
    {
        Float32Array2D array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 10.0 + x);
        
        AdditiveInverse op = new AdditiveInverse();
        Array<?> res = op.process(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertTrue(res.sampleElement() instanceof Float32);
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ScalarArray2D<?> res2 = ScalarArray2D.wrap(ScalarArray.wrap((Array<Scalar>) res));
        assertEquals(res2.getValue(0, 0),   0, .01);
        assertEquals(res2.getValue(7, 0),  -7, .01);
        assertEquals(res2.getValue(0, 5), -50, .01);
        assertEquals(res2.getValue(7, 5), -57, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
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
    
    
    /**
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_View_Float32()
    {
        Float32Array2D array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 10.0 + x);
        
        ScalarArray<Float32> res = new AdditiveInverse.ScalarView<Float32>(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertTrue(res.sampleElement() instanceof Float32);
        
        ScalarArray2D<?> res2 = ScalarArray2D.wrap(res);
        assertEquals(res2.getValue(0, 0),   0, .01);
        assertEquals(res2.getValue(7, 0),  -7, .01);
        assertEquals(res2.getValue(0, 5), -50, .01);
        assertEquals(res2.getValue(7, 5), -57, .01);
        
        res2.setValue(4, 3, -30);
        assertEquals(array.getValue(4, 3), 30.0, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_View_Int32()
    {
        Int32Array2D array = Int32Array2D.create(8, 6);
        array.fillInts((x,y) -> y * 10 + x);
        
        IntArray<Int32> res = new AdditiveInverse.IntView<Int32>(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertTrue(res.sampleElement() instanceof Int32);
        
        IntArray2D<?> res2 = IntArray2D.wrap(res);
        assertEquals(res2.getInt(0, 0),   0);
        assertEquals(res2.getInt(7, 0),  -7);
        assertEquals(res2.getInt(0, 5), -50);
        assertEquals(res2.getInt(7, 5), -57);
        
        res2.setInt(4, 3, -30);
        assertEquals(array.getInt(4, 3), 30);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.process.AdditiveInverse#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_View_UInt8()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 6);
        array.fillInts((x,y) -> y * 10 + x);
        
        IntArray<?> res = new AdditiveInverse.IntView<UInt8>(array);
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        
        IntArray2D<?> res2 = IntArray2D.wrap(res);
        assertEquals(res2.getInt(0, 0),   0);
        assertEquals(res2.getInt(7, 0),  -7);
        assertEquals(res2.getInt(0, 5), -50);
        assertEquals(res2.getInt(7, 5), -57);
        
        res2.setInt(4, 3, -30);
        assertEquals(array.getInt(4, 3), 30);
        res2.setInt(3, 2, 20);
        assertEquals(array.getInt(3, 2), 0);
        
        assertEquals(res.minInt(), -57);
        assertEquals(res.maxInt(), 0);
    }
    
}
