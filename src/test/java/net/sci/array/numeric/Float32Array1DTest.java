/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Float32Array1DTest
{
    /**
     * Test method for {@link net.sci.array.numeric.Float32Array1D#fromIntArray(int[])}.
     */
    @Test
    public final void testFromIntArray()
    {
        float[] values = new float[] {10, 11, 12, 13, 14, 15};
        
        Float32Array1D array = Float32Array1D.fromFloatArray(values);
        
        assertEquals(6, array.size(0));
        assertEquals(10f, array.getFloat(0), 0.1f);
        assertEquals(15f, array.getFloat(5), 0.1f);
    }
    

    /**
     * Test method for {@link net.sci.array.numeric.IntArray1D#iterator()}.
     */
    @Test
    public final void testIterator()
    {
        Float32Array1D array = Float32Array1D.fromFloatArray(new float[] {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f});
        
        Float32Array.Iterator iter = array.iterator();
        float sum = 0;
        while(iter.hasNext())
        {
            iter.forward();
            sum += iter.getFloat();
        }
        assertEquals(2.1f, sum, 0.1f);
    }
}
