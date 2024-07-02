/**
 * 
 */
package net.sci.array.numeric;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;

/**
 * @author dlegland
 *
 */
public class Float64ArrayTest
{

    /**
     * Test method for {@link net.sci.array.numeric.Float64Array#convert(net.sci.array.Array)}.
     */
    @Test
    public final void testConvert()
    {
        ScalarArray2D<?> array = Int32Array2D.create(8, 6);
        array.fillValues((x,y) -> y * 1000.0 + x * 10.0);
        
        Float64Array array64 = Float64Array.convert(array);
        
        // array size should not change
        assertEquals(2, array64.dimensionality());
        assertEquals(8, array64.size(0));
        assertEquals(6, array64.size(1));
        
        // array content should be the same 
        assertEquals(   0.0, array64.getValue(new int[] {0, 0}), 0.1);
        assertEquals(  70.0, array64.getValue(new int[] {7, 0}), 0.1);
        assertEquals(5000.0, array64.getValue(new int[] {0, 5}), 0.1);
        assertEquals(5070.0, array64.getValue(new int[] {7, 5}), 0.1);
    }

    /**
     * Test method for {@link net.sci.array.numeric.Float64Array#wrap(net.sci.array.Array)}.
     */
    @Test
    public final void testWrap()
    {
        Array2D<Float64> array = GenericArray2D.create(8, 6, new Float64(0.0));
        array.fill((x,y) -> new Float64(y * 1000.0 + x * 10.0));
        
        Float64Array array64 = Float64Array.wrap(array);
        
        // array size should not change
        assertEquals(2, array64.dimensionality());
        assertEquals(8, array64.size(0));
        assertEquals(6, array64.size(1));
        
        // array content should be the same 
        assertEquals(   0.0, array64.getValue(new int[] {0, 0}), 0.1);
        assertEquals(  70.0, array64.getValue(new int[] {7, 0}), 0.1);
        assertEquals(5000.0, array64.getValue(new int[] {0, 5}), 0.1);
        assertEquals(5070.0, array64.getValue(new int[] {7, 5}), 0.1);
        
        // changing the view should change original array
        array64.setValue(new int[] {4, 3}, 9999.9);
        assertEquals(9999.9, array.get(4, 3).getValue(), 0.01);
    }

}
