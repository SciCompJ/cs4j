/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;

/**
 * @author dlegland
 *
 */
public class Float32ArrayTest
{

    /**
     * Test method for {@link net.sci.array.scalar.Float32Array#convert(net.sci.array.Array)}.
     */
    @Test
    public final void testConvert()
    {
        ScalarArray2D<?> array = Int32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 1000.0 + x * 10.0);
        
        Float32Array array32 = Float32Array.convert(array);
        
        // array size should not change
        assertEquals(2, array32.dimensionality());
        assertEquals(8, array32.size(0));
        assertEquals(6, array32.size(1));
        
        // array content should be the same 
        assertEquals(   0.0f, array32.getFloat(new int[] {0, 0}), 0.1f);
        assertEquals(  70.0f, array32.getFloat(new int[] {7, 0}), 0.1f);
        assertEquals(5000.0f, array32.getFloat(new int[] {0, 5}), 0.1f);
        assertEquals(5070.0f, array32.getFloat(new int[] {7, 5}), 0.1f);
    }

    /**
     * Test method for {@link net.sci.array.scalar.Float32Array#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<Float32> array = GenericArray2D.create(8, 6, new Float32(0));
        array.fill((x,y) -> new Float32(y * 1000 + x * 10));
        
        Float32Array array32 = Float32Array.wrap(array);
        
        // array size should not change
        assertEquals(2, array32.dimensionality());
        assertEquals(8, array32.size(0));
        assertEquals(6, array32.size(1));
        
        // array content should be the same 
        assertEquals(   0.0f, array32.getFloat(new int[] {0, 0}), 0.1f);
        assertEquals(  70.0f, array32.getFloat(new int[] {7, 0}), 0.1f);
        assertEquals(5000.0f, array32.getFloat(new int[] {0, 5}), 0.1f);
        assertEquals(5070.0f, array32.getFloat(new int[] {7, 5}), 0.1f);
        
        // changing the view should change original array
        array32.setFloat(new int[] {4, 3}, 9999.9f);
        assertEquals(9999.9f, array.get(4, 3).getFloat(), 0.01f);
    }

}
