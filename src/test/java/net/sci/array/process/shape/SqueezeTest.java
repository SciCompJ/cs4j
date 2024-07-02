/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;

/**
 * @author dlegland
 *
 */
public class SqueezeTest
{
    /**
     * Test method for {@link net.sci.array.process.shape.Squeeze#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_2D_H()
    {
        int[] dims = new int[]{10, 1};
        UInt8Array array = UInt8Array.create(dims);
        
        Squeeze op = new Squeeze();
        UInt8Array res = (UInt8Array) op.process(array);
        
        assertEquals(1, res.dimensionality());
        assertEquals(10, res.size(0));
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Squeeze#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_2D_V()
    {
        int[] dims = new int[]{1, 10};
        UInt8Array array = UInt8Array.create(dims);
        
        Squeeze op = new Squeeze();
        UInt8Array res = (UInt8Array) op.process(array);
        
        assertEquals(1, res.dimensionality());
        assertEquals(10, res.size(0));
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Squeeze#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_3D()
    {
        int[] dims = new int[]{1, 10, 1};
        UInt8Array array = UInt8Array.create(dims);
        
        Squeeze op = new Squeeze();
        UInt8Array res = (UInt8Array) op.process(array);
        
        assertEquals(1, res.dimensionality());
        assertEquals(10, res.size(0));
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Squeeze#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess_3D_NoSqueeze()
    {
        int[] dims = new int[]{5, 4, 3};
        UInt8Array array = UInt8Array.create(dims);
        
        Squeeze op = new Squeeze();
        UInt8Array res = (UInt8Array) op.process(array);
        
        assertEquals(3, res.dimensionality());
        assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
        assertEquals(3, res.size(2));
    }

}
