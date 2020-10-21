/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;
import net.sci.array.Array2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ScalarArrayUInt8ViewTest
{
    @Test
    public final void testConvertFloat32Array()
    {
        ScalarArray2D<?> array = Float32Array2D.create(100, 100);
        array.populateValues((x, y) -> (x + y + 0.0));
        
        UInt8Array res = new ScalarArrayUInt8View(array, 0, 200-2);
        
        assertEquals(100, res.size(0));
        assertEquals(100, res.size(1));
        
        double[] range = res.valueRange();
        assertEquals(0, range[0], .01);
        assertEquals(255, range[1], .01);
    }
    
    @Test
    public final void testConvertFloat32Array2D_Is2D()
    {
        ScalarArray2D<?> array = Float32Array2D.create(100, 100);
        array.populateValues((x, y) -> (x * 5.0 + y * 2.0));
        
        double maxi = array.getValue(99,  99);
        UInt8Array res = new ScalarArrayUInt8View(array, 0, maxi);
        
        UInt8Array2D res2d = UInt8Array2D.wrap(res);
        assertEquals(100, res2d.size(0));
        assertEquals(100, res2d.size(1));
        assertEquals(255, res2d.getInt(99, 99));
    }

    @Test
    public final void testConvertFloat32Array2D_IsScalar2D()
    {
        ScalarArray2D<?> array = Float32Array2D.create(100, 100);
        array.populateValues((x, y) -> (x * 5.0 + y * 2.0));
        
        double maxi = array.getValue(99,  99);
        UInt8Array res = new ScalarArrayUInt8View(array, 0, maxi);
        
        ScalarArray2D<?> res2d = ScalarArray2D.wrapScalar2d(res);
        assertTrue(res2d instanceof Array2D);
        assertEquals(100, res2d.size(0));
        assertEquals(100, res2d.size(1));
        assertEquals(255, res2d.getValue(99, 99), .01);
    }
}
