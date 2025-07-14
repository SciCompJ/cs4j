/**
 * 
 */
package net.sci.axis;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class GenericNumericalAxisTest
{

    /**
     * Test method for {@link net.sci.axis.GenericNumericalAxis#physicalRange()}.
     */
    @Test
    public final void testPhysicalRange()
    {
        GenericNumericalAxis axis = createDefaultAxis();
        double[] range = axis.physicalRange();
        
        assertEquals(3.0, range[0], 0.01);
        assertEquals(15.0, range[1], 0.01);
    }

    /**
     * Test method for {@link net.sci.axis.GenericNumericalAxis#indexToValue(int)}.
     */
    @Test
    public final void testIndexToValue()
    {
        GenericNumericalAxis axis = createDefaultAxis();
        
        assertEquals(10.0, axis.indexToValue(0), 0.01);
        assertEquals(7.0, axis.indexToValue(2), 0.01);
        assertEquals(3.0, axis.indexToValue(5), 0.01);
    }

    private static final GenericNumericalAxis createDefaultAxis()
    {
        double[] values = new double[] {10, 15, 7, 8, 9, 3};
        return new GenericNumericalAxis("Values", values);
    }
}
