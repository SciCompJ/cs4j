/**
 * 
 */
package net.sci.image.filtering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * 
 */
public class Convolution1DTest
{
    /**
     * Test method for {@link net.sci.image.filtering.Convolution1D#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_processScalar2d_gradX_rect()
    {
        Float32Array array = Float32Array.convert(create_array2d_rect());
        Convolution1D opGradX = new Convolution1D(0, new double[] {0.5, 0, -0.5});
        
        ScalarArray2D<?> res = ScalarArray2D.wrap(opGradX.processScalar(array));
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        // do not modify rows outside rectangle
        assertEquals(0.0, res.getValue(0, 0), 0.01);
        assertEquals(0.0, res.getValue(11, 9), 0.01);
        assertEquals(0.0, res.getValue(6, 2), 0.01);
        assertEquals(0.0, res.getValue(6, 8), 0.01);
        // no gradient within rectangle
        assertEquals(0.0, res.getValue(6, 4), 0.01);
        // positive gradients
        assertTrue(res.getValue(2, 4) > 0);
        assertTrue(res.getValue(3, 4) > 0);
        // negative gradients
        assertTrue(res.getValue(9, 4) < 0);
        assertTrue(res.getValue(10, 4) < 0);
    }

    /**
     * Test method for {@link net.sci.image.filtering.Convolution1D#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_processScalar2d_gradY_rect()
    {
        Float32Array array = Float32Array.convert(create_array2d_rect());
        Convolution1D opGradY = new Convolution1D(1, new double[] {0.5, 0, -0.5});
        
        ScalarArray2D<?> res = ScalarArray2D.wrap(opGradY.processScalar(array));
        
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        // do not modify rows outside rectangle
        assertEquals(0.0, res.getValue(0, 0), 0.01);
        assertEquals(0.0, res.getValue(11, 9), 0.01);
        assertEquals(0.0, res.getValue(2, 5), 0.01);
        assertEquals(0.0, res.getValue(8, 5), 0.01);
        // no gradient within rectangle
        assertEquals(0.0, res.getValue(6, 4), 0.01);
        // positive gradients
        assertTrue(res.getValue(4, 2) > 0);
        assertTrue(res.getValue(4, 3) > 0);
        // negative gradients
        assertTrue(res.getValue(4, 7) < 0);
        assertTrue(res.getValue(4, 8) < 0);
    }

    /**
     * Test method for {@link net.sci.image.filtering.Convolution1D#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_processScalar3d_gradX_rect()
    {
        Float32Array array = Float32Array.convert(create_array3d_rect());
        Convolution1D op = new Convolution1D(0, new double[] {0.5, 0, -0.5});
        
        ScalarArray3D<?> res = ScalarArray3D.wrap(op.processScalar(array));
        
        assertEquals(3, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        // do not modify rows outside rectangle
        assertEquals(0.0, res.getValue(0, 0, 0), 0.01);
        assertEquals(0.0, res.getValue(13, 11, 9), 0.01);
        assertEquals(0.0, res.getValue(6, 2, 6), 0.01);
        assertEquals(0.0, res.getValue(6, 10, 6), 0.01);
        assertEquals(0.0, res.getValue(6, 6, 2), 0.01);
        assertEquals(0.0, res.getValue(6, 6, 8), 0.01);
        // no gradient within rectangle
        assertEquals(0.0, res.getValue(6, 5, 5), 0.01);
        // positive gradients
        assertTrue(res.getValue(2, 5, 5) > 0);
        assertTrue(res.getValue(3, 5, 5) > 0);
        // negative gradients
        assertTrue(res.getValue(9, 5, 5) < 0);
        assertTrue(res.getValue(10, 5, 5) < 0);
    }

    /**
     * Test method for {@link net.sci.image.filtering.Convolution1D#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_processScalar3d_gradY_rect()
    {
        Float32Array array = Float32Array.convert(create_array3d_rect());
        Convolution1D op = new Convolution1D(1, new double[] {0.5, 0, -0.5});
        
        ScalarArray3D<?> res = ScalarArray3D.wrap(op.processScalar(array));
        
        assertEquals(3, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        // do not modify rows outside rectangle
        assertEquals(0.0, res.getValue(0, 0, 0), 0.01);
        assertEquals(0.0, res.getValue(13, 11, 9), 0.01);
        assertEquals(0.0, res.getValue(2, 6, 2), 0.01);
        assertEquals(0.0, res.getValue(6, 6, 2), 0.01);
        assertEquals(0.0, res.getValue(2, 6, 8), 0.01);
        assertEquals(0.0, res.getValue(6, 6, 8), 0.01);
        // no gradient within rectangle
        assertEquals(0.0, res.getValue(6, 5, 5), 0.01);
        // positive gradients
        assertTrue(res.getValue(5, 2, 5) > 0);
        assertTrue(res.getValue(5, 3, 5) > 0);
        // negative gradients
        assertTrue(res.getValue(5, 9, 5) < 0);
        assertTrue(res.getValue(5, 10, 5) < 0);
    }

    /**
     * Test method for {@link net.sci.image.filtering.Convolution1D#processScalar(net.sci.array.numeric.ScalarArray)}.
     */
    @Test
    public final void test_processScalar3d_gradZ_rect()
    {
        Float32Array array = Float32Array.convert(create_array3d_rect());
        Convolution1D op = new Convolution1D(2, new double[] {0.5, 0, -0.5});
        
        ScalarArray3D<?> res = ScalarArray3D.wrap(op.processScalar(array));
        
        assertEquals(3, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(array.size(2), res.size(2));
        
        // do not modify rows outside rectangle
        assertEquals(0.0, res.getValue(0, 0, 0), 0.01);
        assertEquals(0.0, res.getValue(13, 11, 9), 0.01);
        assertEquals(0.0, res.getValue(2, 2, 6), 0.01);
        assertEquals(0.0, res.getValue(6, 2, 6), 0.01);
        assertEquals(0.0, res.getValue(2, 8, 6), 0.01);
        assertEquals(0.0, res.getValue(6, 8, 6), 0.01);
        // no gradient within rectangle
        assertEquals(0.0, res.getValue(6, 5, 5), 0.01);
        // positive gradients
        assertTrue(res.getValue(5, 5, 2) > 0);
        assertTrue(res.getValue(5, 5, 3) > 0);
        // negative gradients
        assertTrue(res.getValue(5, 5, 7) < 0);
        assertTrue(res.getValue(5, 5, 8) < 0);
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
