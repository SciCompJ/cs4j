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
public class ScalarArray3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray3D#wrap(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testWrapScalarArrayOfT()
    {
        UInt8Array array0 = BufferedUInt8ArrayND.create(5, 4, 3);
        ScalarArray3D<?> array = ScalarArray3D.wrap(array0);
        
        assertTrue(array instanceof ScalarArray3D);
        assertEquals(array.dimensionality(), 3);
        assertEquals(array.size(0), 5);
        assertEquals(array.size(1), 4);
        assertEquals(array.size(2), 3);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray3D#populateValues(java.util.function.Function)}.
     */
    @Test
    public final void testFillValues()
    {
        ScalarArray3D<?> array = UInt8Array3D.create(5, 4, 3);
        
        array.fillValues(pos -> pos[0] + 10.0 * pos[1] + 100.0 * pos[2]);
        
        assertEquals(array.getValue(0, 0, 0),   0, .01);
        assertEquals(array.getValue(4, 0, 0),   4, .01);
        assertEquals(array.getValue(0, 3, 0),  30, .01);
        assertEquals(array.getValue(4, 3, 0),  34, .01);
        assertEquals(array.getValue(0, 0, 2), 200, .01);
        assertEquals(array.getValue(4, 0, 2), 204, .01);
        assertEquals(array.getValue(0, 3, 2), 230, .01);
        assertEquals(array.getValue(4, 3, 2), 234, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray3D#populateValues(java.util.function.TriFunction)}.
     */
    @Test
    public final void testPopulateTriFunctionOfDoubleDoubleDoubleDouble()
    {
        ScalarArray3D<?> array = UInt8Array3D.create(5, 4, 3);
        
        array.populateValues((x,y,z) -> x + 10 * y + z * 100);
        
        assertEquals(array.getValue(0, 0, 0),   0, .01);
        assertEquals(array.getValue(4, 0, 0),   4, .01);
        assertEquals(array.getValue(0, 3, 0),  30, .01);
        assertEquals(array.getValue(4, 3, 0),  34, .01);
        assertEquals(array.getValue(0, 0, 2), 200, .01);
        assertEquals(array.getValue(4, 0, 2), 204, .01);
        assertEquals(array.getValue(0, 3, 2), 230, .01);
        assertEquals(array.getValue(4, 3, 2), 234, .01);
    }
    
}
