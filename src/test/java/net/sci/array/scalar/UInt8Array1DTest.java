/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class UInt8Array1DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.UInt8Array1D#fromIntArray(int[])}.
     */
    @Test
    public final void testFromIntArray()
    {
        int[] values = new int[] {10, 11, 12, 13, 14, 15};
        
        UInt8Array1D array = UInt8Array1D.fromIntArray(values);
        
        assertEquals(6, array.size(0));
        assertEquals(10, array.getInt(0));
        assertEquals(15, array.getInt(5));
    }
    

    /**
     * Test method for {@link net.sci.array.scalar.IntArray1D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        UInt8Array1D array = UInt8Array1D.fromIntArray(new int[] {1, 2, 3, 4, 5, 6});
        
        UInt8Array.Iterator iter = array.iterator();
        int sum = 0;
        while(iter.hasNext())
        {
            sum += iter.nextInt();
        }
        assertEquals(21, sum);
    }
}
