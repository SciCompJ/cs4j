/**
 * 
 */
package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class SliceBufferedUInt8Array3DTest
{

    /**
     * Test method for {@link net.sci.array.numeric.impl.SliceBufferedUInt8Array3D#getByte(int[])}.
     */
    @Test
    public final void testGetValue()
    {
        UInt8Array3D refArray = UInt8Array3D.create(10, 10, 10);
        refArray.fillValues((x,y,z) -> (double) (x + y * 10.0 + Math.floor(z / 5.0) * 100.0));
        
        SliceBufferedUInt8Array3D array = new SliceBufferedUInt8Array3D(refArray, 2);
        
//        System.out.println("read slice 0");
        assertEquals(  0.0, array.getValue(0, 0, 0), 0.01);
        assertEquals( 99.0, array.getValue(9, 9, 0), 0.01);
        assertEquals( 54.0, array.getValue(4, 5, 0), 0.01);
//        System.out.println("read slice 1");
        assertEquals(  0.0, array.getValue(0, 0, 1), 0.01);
        assertEquals( 99.0, array.getValue(9, 9, 1), 0.01);
        assertEquals( 54.0, array.getValue(4, 5, 1), 0.01);
//        System.out.println("read slice 0 again");
        assertEquals(  0.0, array.getValue(0, 0, 0), 0.01);
        assertEquals( 99.0, array.getValue(9, 9, 0), 0.01);
        assertEquals( 54.0, array.getValue(4, 5, 0), 0.01);
//        System.out.println("read slice 6");
        assertEquals(100.0, array.getValue(0, 0, 6), 0.01);
        assertEquals(199.0, array.getValue(9, 9, 6), 0.01);
        assertEquals(154.0, array.getValue(4, 5, 6), 0.01);
//        System.out.println("read slice 0 again");
        assertEquals(  0.0, array.getValue(0, 0, 0), 0.01);
        assertEquals( 99.0, array.getValue(9, 9, 0), 0.01);
        assertEquals( 54.0, array.getValue(4, 5, 0), 0.01);
    }
}
