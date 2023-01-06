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
public class Float64Array1DTest
{
    /**
     * Test method for {@link net.sci.array.scalar.Float64Array1D#fromIntArray(int[])}.
     */
    @Test
    public final void testFromIntArray()
    {
        double[] values = new double[] {10, 11, 12, 13, 14, 15};
        
        Float64Array1D array = Float64Array1D.fromDoubleArray(values);
        
        assertEquals(6, array.size(0));
        assertEquals(10.0, array.getValue(0), 0.1);
        assertEquals(15.0, array.getValue(5), 0.1);
    }
    

    /**
     * Test method for {@link net.sci.array.scalar.IntArray1D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        Float64Array1D array = Float64Array1D.fromDoubleArray(new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6});
        
        Float64Array.Iterator iter = array.iterator();
        float sum = 0;
        while(iter.hasNext())
        {
            iter.forward();
            sum += iter.getValue();
        }
        assertEquals(2.1, sum, 0.1);
    }
}
