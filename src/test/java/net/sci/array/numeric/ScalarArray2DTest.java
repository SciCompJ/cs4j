/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.impl.BufferedUInt8ArrayND;

/**
 * @author dlegland
 *
 */
public class ScalarArray2DTest
{
    
    /**
     * Test method for {@link net.sci.array.numeric.ScalarArray2D#wrap(net.sci.array.scalar.ScalarArray)}.
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
     * Test method for {@link net.sci.array.numeric.ScalarArray2D#fillValues(java.util.function.Function)}.
     */
    @Test
    public final void testFillValuesFunction()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(10, 10);
        
        array.fillValues(pos -> (double) pos[0] + pos[1]);
        
        assertEquals(array.getValue(0, 0), 0, .01);
        assertEquals(array.getValue(9, 0), 9, .01);
        assertEquals(array.getValue(0, 9), 9, .01);
        assertEquals(array.getValue(9, 9), 18, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.ScalarArray2D#fillValues(java.util.function.BiFunction)}.
     */
    @Test
    public final void testFillValuesBiFunction()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(10, 10);
        
        array.fillValues((x,y) -> (double) x + y);
        
        assertEquals(array.getValue(0, 0), 0, .01);
        assertEquals(array.getValue(9, 0), 9, .01);
        assertEquals(array.getValue(0, 9), 9, .01);
        assertEquals(array.getValue(9, 9), 18, .01);
    }
    
}
