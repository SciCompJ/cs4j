/**
 * 
 */
package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.Vector;
import net.sci.array.numeric.VectorArray;
import net.sci.array.numeric.VectorArray2D;
import net.sci.array.numeric.VectorArray3D;

/**
 * 
 */
public class GradientFilterTest
{

    /**
     * Test method for {@link net.sci.image.filtering.GradientFilter#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.numeric.VectorArray2D)}.
     */
    @Test
    public final void testProcessScalar2d()
    {
        Float32Array array = Float32Array.convert(create_array2d_rect());
        GradientFilter op = new GradientFilter(1);
        
        Array<?> res0 = op.process(array);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        VectorArray2D<?,?> res = (VectorArray2D<?, ?>) VectorArray2D.wrap(VectorArray.wrap((Array<Vector>) res0));
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertTrue(res.getValue(5, 10, 0) > 0);
        assertEquals(0.0, res.getValue(5, 10, 1), 0.1);
        assertTrue(res.getValue(15, 10, 0) < 0);
        assertEquals(0.0, res.getValue(15, 10, 1), 0.1);
        
        assertTrue(res.getValue(10, 5, 1) > 0);
        assertEquals(0.0, res.getValue(10, 5, 0), 0.1);
        assertTrue(res.getValue(10, 15, 1) < 0);
        assertEquals(0.0, res.getValue(10, 15, 0), 0.1);
    }

    /**
     * Test method for {@link net.sci.image.filtering.GradientFilter#processScalar3d(net.sci.array.numeric.ScalarArray3D, net.sci.array.numeric.VectorArray3D)}.
     */
    @Test
    public final void testProcessScalar3d()
    {
        Float32Array array = Float32Array.convert(create_array3d_rect());
        GradientFilter op = new GradientFilter(1);
        
        Array<?> res = op.process(array);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        VectorArray3D<?,?> res3d = (VectorArray3D<?, ?>) VectorArray3D.wrap(VectorArray.wrap((Array<Vector>) res));
        
        assertEquals(3, res3d.dimensionality());
        assertEquals(array.size(0), res3d.size(0));
        assertEquals(array.size(1), res3d.size(1));
        assertEquals(array.size(2), res3d.size(2));
        
        assertTrue(res3d.getValue(5, 10, 10, 0) > 0);
        assertEquals(0.0, res3d.getValue(5, 10, 10, 1), 0.1);
        assertEquals(0.0, res3d.getValue(5, 10, 10, 2), 0.1);
        assertTrue(res3d.getValue(15, 10, 10, 0) < 0);
        assertEquals(0.0, res3d.getValue(15, 10, 10, 1), 0.1);
        assertEquals(0.0, res3d.getValue(15, 10, 10, 2), 0.1);
        
        assertTrue(res3d.getValue(10, 5, 10, 1) > 0);
        assertEquals(0.0, res3d.getValue(10, 5, 10, 0), 0.1);
        assertEquals(0.0, res3d.getValue(10, 5, 10, 2), 0.1);
        assertTrue(res3d.getValue(10, 15, 10, 1) < 0);
        assertEquals(0.0, res3d.getValue(10, 15, 10, 0), 0.1);
        assertEquals(0.0, res3d.getValue(10, 15, 10, 2), 0.1);

        assertEquals(0.0, res3d.getValue(10, 10, 5, 0), 0.1);
        assertEquals(0.0, res3d.getValue(10, 10, 5, 1), 0.1);
        assertTrue(res3d.getValue(10, 10, 5, 2) > 0);
        assertEquals(0.0, res3d.getValue(10, 10, 15, 0), 0.1);
        assertEquals(0.0, res3d.getValue(10, 10, 15, 1), 0.1);
        assertTrue(res3d.getValue(10, 10, 15, 2) < 0);
    }

    private static final UInt8Array2D create_array2d_rect()
    {
        UInt8Array2D array = UInt8Array2D.create(20, 20);
        array.fillInts((x, y) -> (x > 5 && x < 15 && y > 5 && y < 15) ? 200 : 0);
        return array;
    }

    private static final UInt8Array3D create_array3d_rect()
    {
        UInt8Array3D array = UInt8Array3D.create(20, 20, 20);
        array.fillInts((x, y, z) -> (x > 5 && x < 15 && y > 5 && y < 15 && z > 5 && z < 15) ? 200 : 0);
        return array;
    }
}
