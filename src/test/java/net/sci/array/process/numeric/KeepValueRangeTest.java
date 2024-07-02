/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class KeepValueRangeTest
{

    /**
     * Test method for {@link net.sci.array.process.numeric.KeepValueRange#processScalar(net.sci.array.numeric.ScalarArray, net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_2d()
    {
        UInt8Array2D array = UInt8Array2D.create(20, 6);
        array.fillValues((x,y) -> x * 10.0);
        
        KeepValueRange algo = new KeepValueRange(50, 150); 
        ScalarArray<?> res = algo.processScalar(array);
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertEquals(  0.0, res.getValue(new int[] { 0, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] { 4, 0}), 0.0);
        assertEquals( 50.0, res.getValue(new int[] { 5, 0}), 0.0);
        assertEquals(150.0, res.getValue(new int[] {15, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] {16, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] {19, 0}), 0.0);
    }


    /**
     * Test method for {@link net.sci.array.process.numeric.KeepValueRange#processScalar(net.sci.array.numeric.ScalarArray, net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_3d()
    {
        UInt8Array3D array = UInt8Array3D.create(20, 6, 4);
        array.fillValues((x,y,z) -> x * 10.0);
        
        KeepValueRange algo = new KeepValueRange(50, 150); 
        ScalarArray<?> res = algo.processScalar(array);
        
        assertEquals(3, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        assertEquals(  0.0, res.getValue(new int[] { 0, 0, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] { 4, 0, 0}), 0.0);
        assertEquals( 50.0, res.getValue(new int[] { 5, 0, 0}), 0.0);
        assertEquals(150.0, res.getValue(new int[] {15, 0, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] {16, 0, 0}), 0.0);
        assertEquals(  0.0, res.getValue(new int[] {19, 0, 0}), 0.0);
    }

}
