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
public class Float64Array2DTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Float64Array2D#fromDoubleArray(double[][])}.
     */
    @Test
    public final void testFromDoubleArray()
    {
        double[][] values = new double[][] {{1.0, 1.1, 1.2, 1.3}, {2.0, 2.1, 2.2, 2.3}, {3.0, 3.1, 3.2, 3.3}};
        
        Float64Array2D array = Float64Array2D.fromDoubleArray(values);
        
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        assertEquals(1.0, array.getValue(0, 0), 0.01);
        assertEquals(3.3, array.getValue(3, 2), 0.01);
    }
}
