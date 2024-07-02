/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class MinimalIncrementTest
{

    /**
     * Test method for {@link net.sci.array.numeric.process.MinimalIncrement#processInt(net.sci.array.numeric.IntArray)}.
     */
    @Test
    public final void testProcessInt()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 3);
        array.fillInts((x,y) -> y * 10+x);
        
        UInt8Array2D res = (UInt8Array2D) new MinimalIncrement().processInt(array);
        
        assertTrue(res.getInt(0, 0) > array.getInt(0, 0));
        assertTrue(res.getInt(3, 2) > array.getInt(3, 2));
//        res.printContent();
    }

    @Test
    public final void testIntView()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 3);
        array.fillInts((x,y) -> y * 10+x);
        
        IntArray<UInt8> res = new MinimalIncrement.IntView<UInt8>(array);
        UInt8Array2D res2 = UInt8Array2D.wrap(UInt8Array.wrap(res));

        assertTrue(res2.getInt(0, 0) > array.getInt(0, 0));
        assertTrue(res2.getInt(3, 2) > array.getInt(3, 2));
//        res2.printContent();
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MinimalIncrement#processFloat32(net.sci.array.numeric.Float32Array)}.
     */
    @Test
    public final void testProcessFloat32()
    {
        Float32Array2D array = Float32Array2D.create(4, 3);
        array.fillValues((x,y) -> (y * 0.1 + x * 0.01));
        
        Float32Array2D res = Float32Array2D.wrap(new MinimalIncrement().processFloat32(array));

        assertTrue(res.getFloat(0, 0) > array.getFloat(0, 0));
        assertTrue(res.getFloat(3, 2) > array.getFloat(3, 2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MinimalIncrement#createView(net.sci.array.numeric.Float32Array)}.
     */
    @Test
    public final void test_createView_Float32()
    {
        Float32Array2D array = Float32Array2D.create(4, 3);
        array.fillValues((x,y) -> (y * 0.1 + x * 0.01));
        
        Array<?> res = MinimalIncrement.createView(array);
        assertTrue(res instanceof Float32Array);
        
        Float32Array2D res2 = Float32Array2D.wrap((Float32Array) res);
        assertTrue(res2.getFloat(0, 0) > array.getFloat(0, 0));
        assertTrue(res2.getFloat(3, 2) > array.getFloat(3, 2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MinimalIncrement#processFloat32(net.sci.array.numeric.Float32Array)}.
     */
    @Test
    public final void testProcessFloat64()
    {
        Float64Array2D array = Float64Array2D.create(4, 3);
        array.fillValues((x,y) -> (y * 0.1 + x * 0.01));
        
        Float64Array2D res = Float64Array2D.wrap(new MinimalIncrement().processFloat64(array));
        
        assertTrue(res.getValue(0, 0) > array.getValue(0, 0));
        assertTrue(res.getValue(3, 2) > array.getValue(3, 2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MinimalIncrement#createView(net.sci.array.numeric.Float32Array)}.
     */
    @Test
    public final void test_createView_Float64()
    {
        Float64Array2D array = Float64Array2D.create(4, 3);
        array.fillValues((x,y) -> (y * 0.1 + x * 0.01));
        
        Array<?> res = MinimalIncrement.createView(array);
        assertTrue(res instanceof Float64Array);
        
        Float64Array2D res2 = Float64Array2D.wrap((Float64Array) res);
        assertTrue(res2.getValue(0, 0) > array.getValue(0, 0));
        assertTrue(res2.getValue(3, 2) > array.getValue(3, 2));
    }

}
