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
public class ScalarArray2DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray2D#wrap(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testWrapScalarArrayOfT()
    {
        UInt8Array array0 = BufferedUInt8ArrayND.create(10, 10);
        ScalarArray2D<?> array = ScalarArray2D.wrap(array0);
        
        assertTrue(array instanceof ScalarArray2D);
        assertEquals(array.dimensionality(), 2);
        assertEquals(array.size(0), 10);
        assertEquals(array.size(1), 10);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray2D#populateValues(java.util.function.Function)}.
     */
    @Test
    public final void testPopulateFunctionOfDoubleDouble()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(10, 10);
        
        array.fillValues(pos -> (double) pos[0] + pos[1]);
        
        assertEquals(array.getValue(0, 0), 0, .01);
        assertEquals(array.getValue(9, 0), 9, .01);
        assertEquals(array.getValue(0, 9), 9, .01);
        assertEquals(array.getValue(9, 9), 18, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray2D#populateValues(java.util.function.BiFunction)}.
     */
    @Test
    public final void testPopulateBiFunctionOfDoubleDoubleDouble()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(10, 10);
        
        array.populateValues((x,y) -> (double) x + y);
        
        assertEquals(array.getValue(0, 0), 0, .01);
        assertEquals(array.getValue(9, 0), 9, .01);
        assertEquals(array.getValue(0, 9), 9, .01);
        assertEquals(array.getValue(9, 9), 18, .01);
    }
    
}
