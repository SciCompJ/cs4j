/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;

/**
 * 
 */
public class IntArrayTest
{

    /**
     * Test method for {@link net.sci.array.numeric.IntArray#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrapArrayOfInt()
    {
        Array2D<UInt16> array = Array2D.create(10, 5, UInt16.ZERO);
        array.fill((x,y) -> new UInt16(x + y*10));
        
        IntArray<UInt16> result = IntArray.wrap(array);
        
        assertEquals(10, result.size(0));
        assertEquals( 5, result.size(1));
        assertEquals( 0, result.getInt(new int[] {0, 0}));
        assertEquals( 9, result.getInt(new int[] {9, 0}));
        assertEquals(40, result.getInt(new int[] {0, 4}));
        assertEquals(49, result.getInt(new int[] {9, 4}));
    }
}
