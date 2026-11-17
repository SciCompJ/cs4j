/**
 * 
 */
package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * 
 */
public class GaussianFilterTest
{

    /**
     * Test method for {@link net.sci.image.filtering.GaussianFilter#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_process_UInt8Array2D()
    {
        UInt8Array2D array = create_array2d_rect();
        GaussianFilter op = new GaussianFilter(3, 1);

        Array<?> res = op.process(array);
        
        assertEquals(array.dimensionality(), res.dimensionality());
        assertEquals(UInt8.class, res.elementClass());
        
        UInt8Array2D res2d = UInt8Array2D.wrap(UInt8Array.wrap(res));
        
        assertEquals(array.getInt(6, 6), res2d.getInt(6, 6));
        assertEquals(array.getInt(8, 6), res2d.getInt(6, 6));
        assertEquals(array.getInt(0, 0), res2d.getInt(0, 0));
        assertEquals(array.getInt(11, 0), res2d.getInt(11, 0));
        assertEquals(array.getInt(0, 9), res2d.getInt(0, 9));
        assertEquals(array.getInt(11, 9), res2d.getInt(11, 9));
        
        assertTrue(array.getInt(6, 2) < res2d.getInt(6, 2));
        assertTrue(array.getInt(6, 3) > res2d.getInt(6, 3));
    }

    /**
     * Test method for {@link net.sci.image.filtering.GaussianFilter#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_process_UInt8Array3D()
    {
        UInt8Array3D array = create_array3d_rect();
        GaussianFilter op = new GaussianFilter(3, 1);

        Array<?> res = op.process(array);
        
        assertEquals(array.dimensionality(), res.dimensionality());
        assertEquals(UInt8.class, res.elementClass());
        
        UInt8Array3D res2d = UInt8Array3D.wrap(UInt8Array.wrap(res));
        
        assertEquals(array.getInt(6, 6, 6), res2d.getInt(6, 6, 6));
        assertEquals(array.getInt(8, 8, 6), res2d.getInt(6, 6, 6));
        assertEquals(array.getInt(0, 0, 0), res2d.getInt(0, 0, 0));
        assertEquals(array.getInt(11, 0, 0), res2d.getInt(11, 0, 0));
        assertEquals(array.getInt(0, 9, 0), res2d.getInt(0, 9, 0));
        assertEquals(array.getInt(11, 9, 0), res2d.getInt(11, 9, 0));
        
        assertTrue(array.getInt(6, 6, 2) < res2d.getInt(6, 6, 2));
        assertTrue(array.getInt(6, 6, 3) > res2d.getInt(6, 6, 3));
    }

    private static final UInt8Array2D create_array2d_rect()
    {
        UInt8Array2D array = UInt8Array2D.create(12, 10);
        array.fillInts((x, y) -> (x > 2 && x < 10 && y > 2 && y < 8) ? 200 : 0);
        return array;
    }

    private static final UInt8Array3D create_array3d_rect()
    {
        UInt8Array3D array = UInt8Array3D.create(14, 12, 10);
        array.fillInts((x, y, z) -> (x > 2 && x < 10 && y > 2 && y < 10 && z > 2 && z < 8) ? 200 : 0);
        return array;
    }
}
