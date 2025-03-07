/**
 * 
 */
package net.sci.array.shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt16Array3D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class SubSampleTest
{

    /**
     * Test method for {@link net.sci.array.shape.SubSample#process(net.sci.array.Array)}.
     */
    @Test
    public void testProcess_2d()
    {
        UInt8Array2D array = createUInt8Array2D();
        SubSample sampler = new SubSample(2);
        
        Array<?> res = sampler.process(array);
        
        assertTrue(res instanceof UInt8Array);
        assertEquals(2, res.dimensionality());
        int[] dims = res.size();
        assertEquals(5, dims[0]);
        assertEquals(5, dims[1]);
    }

    /**
     * Test method for {@link net.sci.array.shape.SubSample#process(net.sci.array.Array)}.
     */
    @Test
    public void testProcess_3d()
    {
        UInt16Array3D array = createUInt16Array3D();
        SubSample sampler = new SubSample(2);
        
        Array<?> res = sampler.process(array);
        
        assertTrue(res instanceof UInt16Array);
        assertEquals(3, res.dimensionality());
        int[] dims = res.size();
        assertEquals(5, dims[0]);
        assertEquals(4, dims[1]);
        assertEquals(3, dims[2]);
    }

    /**
     * Test method for {@link net.sci.array.shape.SubSample#createView(net.sci.array.Array)}.
     */
    @Test
    public void testCreateView_2d()
    {
        UInt8Array2D array = createUInt8Array2D();
        SubSample sampler = new SubSample(2);
        
        Array<?> res = sampler.createView(array);
        
        assertTrue(res instanceof UInt8Array);
        assertEquals(2, res.dimensionality());
        int[] dims = res.size();
        assertEquals(5, dims[0]);
        assertEquals(5, dims[1]);
    }

    /**
     * Test method for {@link net.sci.array.shape.SubSample#createView(net.sci.array.Array)}.
     */
    @Test
    public void testCreateView_3d()
    {
        UInt16Array3D array = createUInt16Array3D();
        SubSample sampler = new SubSample(2);
        
        Array<?> res = sampler.createView(array);
        
        assertTrue(res instanceof UInt16Array);
        assertEquals(3, res.dimensionality());
        int[] dims = res.size();
        assertEquals(5, dims[0]);
        assertEquals(4, dims[1]);
        assertEquals(3, dims[2]);
    }

    private UInt8Array2D createUInt8Array2D()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);
        array.fillValues((x, y) -> (y * 10.0 + x));
        return array;
    }

    private UInt16Array3D createUInt16Array3D()
    {
        UInt16Array3D array = UInt16Array3D.create(10, 8, 6);
        array.fillValues((x, y, z) -> (z*10000.0 + y * 100 + x));
        return array;
    }
}
