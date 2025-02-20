/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.impl.GenericArray2D;

/**
 * @author dlegland
 *
 */
public class Int32ArrayTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Int32Array#convert(net.sci.array.Array)}.
     */
    @Test
    public final void testConvert()
    {
        ScalarArray2D<?> array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 1000.0 + x * 10.0);
        
        Int32Array array32 = Int32Array.convert(array);
        
        // array size should not change
        assertEquals(2, array32.dimensionality());
        assertEquals(8, array32.size(0));
        assertEquals(6, array32.size(1));
        
        // array content should be the same 
        assertEquals(   0, array32.getInt(new int[] {0, 0}));
        assertEquals(  70, array32.getInt(new int[] {7, 0}));
        assertEquals(5000, array32.getInt(new int[] {0, 5}));
        assertEquals(5070, array32.getInt(new int[] {7, 5}));
    }

    /**
     * Test method for {@link net.sci.array.numeric.Int32Array#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<Int32> array = GenericArray2D.create(8, 6, new Int32(0));
        array.fill((x,y) -> new Int32(y * 1000 + x * 10));
        
        Int32Array array32 = Int32Array.wrap(array);
        
        // array size should not change
        assertEquals(2, array32.dimensionality());
        assertEquals(8, array32.size(0));
        assertEquals(6, array32.size(1));
        
        // array content should be the same 
        assertEquals(   0, array32.getInt(new int[] {0, 0}));
        assertEquals(  70, array32.getInt(new int[] {7, 0}));
        assertEquals(5000, array32.getInt(new int[] {0, 5}));
        assertEquals(5070, array32.getInt(new int[] {7, 5}));
        
        // changing the view should change original array
        array32.setInt(new int[] {4, 3}, 9999);
        assertEquals(9999, array.get(4, 3).intValue());
    }

}
