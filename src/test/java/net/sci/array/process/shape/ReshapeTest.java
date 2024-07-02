/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * 
 */
public class ReshapeTest
{
    /**
     * Test method for {@link net.sci.array.process.shape.Reshape#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_2d_to_2d()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x,y)-> 6 * y + x);
        
        Reshape op = new Reshape(new int[] {4, 6});
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(op.process(array)));
        
        assertEquals(res.size(0), 4);
        assertEquals(res.size(1), 6);
        assertEquals(res.getInt(0,0), 0);
        assertEquals(res.getInt(3,0), 3);
        assertEquals(res.getInt(0,5), 20);
        assertEquals(res.getInt(3,5), 23);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Reshape#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_2d_to_3d()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x,y)-> 6 * y + x);
        
        Reshape op = new Reshape(new int[] {4, 3, 2});
        UInt8Array3D res = UInt8Array3D.wrap(UInt8Array.wrap(op.process(array)));
        
        assertEquals(res.size(0), 4);
        assertEquals(res.size(1), 3);
        assertEquals(res.size(2), 2);
        assertEquals(res.getInt(0,0,0),  0);
        assertEquals(res.getInt(3,0,0),  3);
        assertEquals(res.getInt(0,2,0),  8);
        assertEquals(res.getInt(3,2,0), 11);
        assertEquals(res.getInt(0,0,1), 12);
        assertEquals(res.getInt(3,0,1), 15);
        assertEquals(res.getInt(0,2,1), 20);
        assertEquals(res.getInt(3,2,1), 23);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Reshape#view(net.sci.array.Array)}.
     */
    @Test
    public final void test_view_2d_to_2d()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x,y)-> 6 * y + x);
        
        Reshape op = new Reshape(new int[] {4, 6});
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(op.view(array)));
        
        assertEquals(res.size(0), 4);
        assertEquals(res.size(1), 6);
        assertEquals(res.getInt(0,0), 0);
        assertEquals(res.getInt(3,0), 3);
        assertEquals(res.getInt(0,5), 20);
        assertEquals(res.getInt(3,5), 23);
    }

    /**
     * Test method for {@link net.sci.array.process.shape.Reshape#view(net.sci.array.Array)}.
     */
    @Test
    public final void test_view_2d_to_3d()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x,y)-> 6 * y + x);
        
        Reshape op = new Reshape(new int[] {4, 3, 2});
        UInt8Array3D res = UInt8Array3D.wrap(UInt8Array.wrap(op.view(array)));
        
        assertEquals(res.size(0), 4);
        assertEquals(res.size(1), 3);
        assertEquals(res.size(2), 2);
        assertEquals(res.getInt(0,0,0),  0);
        assertEquals(res.getInt(3,0,0),  3);
        assertEquals(res.getInt(0,2,0),  8);
        assertEquals(res.getInt(3,2,0), 11);
        assertEquals(res.getInt(0,0,1), 12);
        assertEquals(res.getInt(3,0,1), 15);
        assertEquals(res.getInt(0,2,1), 20);
        assertEquals(res.getInt(3,2,1), 23);
    }

}
