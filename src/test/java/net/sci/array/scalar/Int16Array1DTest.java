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
public class Int16Array1DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.Int16Array1D#fromIntArray(int[])}.
     */
    @Test
    public final void testFromIntArray()
    {
        int[] values = new int[] {10, 11, 12, 13, 14, 15};
        
        Int16Array1D array = Int16Array1D.fromIntArray(values);
        
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
        Int16Array1D array = Int16Array1D.fromIntArray(new int[] {1, 2, 3, 4, 5, 6});
        
        Int16Array.Iterator iter = array.iterator();
        int sum = 0;
        while(iter.hasNext())
        {
            sum += iter.nextInt();
        }
        assertEquals(21, sum);
    }
}
