/**
 * 
 */
package net.sci.image.process.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class MedianFilterBoxSlidingTest
{
    /**
     * Test method for {@link net.sci.image.process.filter.MedianFilterBoxSliding#processScalar2d(net.sci.array.scalar.ScalarArray2D, net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcessScalar2d_UInt8_square6x6_diams3x3()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        array.fillInts((x,y) -> x > 1 && x < 6 && y > 1 && y < 6 ? 200 : 10);
//        System.out.println(array);
        
        MedianFilterBoxSliding algo = new MedianFilterBoxSliding(new int[] {3,3});
        
        UInt8Array2D result = array.duplicate();
        algo.processScalar2d(array, result);
//        System.out.println(result);
        
        assertEquals(10, result.getInt(1, 1));
        assertEquals(10, result.getInt(2, 2));
        assertEquals(200, result.getInt(3, 2));
        assertEquals(200, result.getInt(3, 3));

        assertEquals(10, result.getInt(6, 6));
        assertEquals(10, result.getInt(5, 5));
        assertEquals(200, result.getInt(5, 4));
        assertEquals(200, result.getInt(4, 5));
    }

    /**
     * Test method for {@link net.sci.image.process.filter.MedianFilterBoxSliding#processScalar3d(net.sci.array.scalar.ScalarArray3D, net.sci.array.scalar.ScalarArray3D)}.
     */
    @Test
    public final void testProcessScalar3d_UInt8_cube3x3x3_diams3x3x3()
    {
        UInt8Array3D array = UInt8Array3D.create(6, 6, 6);
        array.fillInts((x,y,z) -> x > 1 && x < 5 && y > 1 && y < 5 && z > 1 && z < 5 ? 200 : 10);
//        System.out.println(array);
        
        MedianFilterBoxSliding algo = new MedianFilterBoxSliding(new int[] {3,3,3});
        
        UInt8Array3D result = array.duplicate();
        algo.processScalar3d(array, result);
//        System.out.println(result);
        
        // test corners (8 cases)
        assertEquals(10, result.getInt(2, 2, 2));
        assertEquals(10, result.getInt(4, 2, 2));
        assertEquals(10, result.getInt(2, 4, 2));
        assertEquals(10, result.getInt(4, 4, 2));
        assertEquals(10, result.getInt(2, 2, 4));
        assertEquals(10, result.getInt(4, 2, 4));
        assertEquals(10, result.getInt(2, 4, 4));
        assertEquals(10, result.getInt(4, 4, 4));
        
        // test edges (12 cases)
        assertEquals(10, result.getInt(3, 2, 2));
        assertEquals(10, result.getInt(2, 3, 2));
        assertEquals(10, result.getInt(4, 3, 2));
        assertEquals(10, result.getInt(3, 4, 2));
        assertEquals(10, result.getInt(2, 2, 3));
        assertEquals(10, result.getInt(4, 2, 3));
        assertEquals(10, result.getInt(2, 4, 3));
        assertEquals(10, result.getInt(4, 4, 3));
        assertEquals(10, result.getInt(3, 2, 4));
        assertEquals(10, result.getInt(2, 3, 4));
        assertEquals(10, result.getInt(4, 3, 4));
        assertEquals(10, result.getInt(3, 4, 4));
        
        // test faces (6 cases)
        assertEquals(200, result.getInt(3, 3, 2));
        assertEquals(200, result.getInt(3, 2, 3));
        assertEquals(200, result.getInt(2, 3, 3));
        assertEquals(200, result.getInt(4, 3, 3));
        assertEquals(200, result.getInt(3, 4, 3));
        assertEquals(200, result.getInt(3, 3, 4));
        
        // test middle (1 case)
        assertEquals(200, result.getInt(3, 3, 3));
    }
}
