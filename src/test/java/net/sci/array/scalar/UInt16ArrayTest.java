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
public class UInt16ArrayTest
{

    /**
     * Test method for {@link net.sci.array.scalar.UInt16Array#convert(net.sci.array.Array)}.
     */
    @Test
    public final void testConvert()
    {
        ScalarArray2D<?> array = Float32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 1000.0 + x * 10.0);
        
        UInt16Array array16 = UInt16Array.convert(array);
        
        // array size should not change
        assertEquals(2, array16.dimensionality());
        assertEquals(8, array16.size(0));
        assertEquals(6, array16.size(1));
        
        // array content should be the same 
        assertEquals(0, array16.getInt(0, 0));
        assertEquals(70, array16.getInt(7, 0));
        assertEquals(5000, array16.getInt(0, 5));
        assertEquals(5070, array16.getInt(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.scalar.UInt16Array#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<UInt16> array = GenericArray2D.create(8, 6, new UInt16(0));
        array.fill((x,y) -> new UInt16(y * 1000 + x * 10));
        
        UInt16Array array16 = UInt16Array.wrap(array);
        
        // array size should not change
        assertEquals(2, array16.dimensionality());
        assertEquals(8, array16.size(0));
        assertEquals(6, array16.size(1));
        
        // array content should be the same 
        assertEquals(   0, array16.getInt(0, 0));
        assertEquals(  70, array16.getInt(7, 0));
        assertEquals(5000, array16.getInt(0, 5));
        assertEquals(5070, array16.getInt(7, 5));
        
        // changing the view should change original array
        array16.setInt(new int[] {4, 3}, 9999);
        assertEquals(9999, array.get(4, 3).getInt());
    }
}
