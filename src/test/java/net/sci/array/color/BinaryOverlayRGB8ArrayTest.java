/**
 * 
 */
package net.sci.array.color;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class BinaryOverlayRGB8ArrayTest
{
    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#get(int[])}.
     */
    @Test
    public final void testGet_2d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_2d();
        
        // check image four corners
        assertEquals(new RGB8(  0,   0,   0), array.get(new int[] {0, 0}));
        assertEquals(new RGB8(  9,   9,   9), array.get(new int[] {9, 0}));
        assertEquals(new RGB8( 70,  70,  70), array.get(new int[] {0, 7}));
        assertEquals(new RGB8( 79,  79,  79), array.get(new int[] {9, 7}));
        // check four corners or the region in binary image
        assertEquals(RGB8.RED, array.get(new int[] {2, 2}));
        assertEquals(RGB8.RED, array.get(new int[] {7, 2}));
        assertEquals(RGB8.RED, array.get(new int[] {2, 5}));
        assertEquals(RGB8.RED, array.get(new int[] {7, 5}));
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#getValue(int[], int)}.
     */
    @Test
    public final void testGetSample_2d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_2d();
        
        // check image four corners
        assertEquals(0, array.getSample(new int[] {0, 0}, 0));
        assertEquals(0, array.getSample(new int[] {0, 0}, 1));
        assertEquals(0, array.getSample(new int[] {0, 0}, 2));
        assertEquals(9, array.getSample(new int[] {9, 0}, 0));
        assertEquals(9, array.getSample(new int[] {9, 0}, 1));
        assertEquals(9, array.getSample(new int[] {9, 0}, 2));
        assertEquals(70, array.getSample(new int[] {0, 7}, 0));
        assertEquals(70, array.getSample(new int[] {0, 7}, 1));
        assertEquals(70, array.getSample(new int[] {0, 7}, 2));
        assertEquals(79, array.getSample(new int[] {9, 7}, 0));
        assertEquals(79, array.getSample(new int[] {9, 7}, 1));
        assertEquals(79, array.getSample(new int[] {9, 7}, 2));
        
        // check four corners or the region in binary image
        assertEquals(255, array.getSample(new int[] {2, 2}, 0));
        assertEquals(  0, array.getSample(new int[] {2, 2}, 1));
        assertEquals(  0, array.getSample(new int[] {2, 2}, 2));
        assertEquals(255, array.getSample(new int[] {7, 2}, 0));
        assertEquals(  0, array.getSample(new int[] {7, 2}, 1));
        assertEquals(  0, array.getSample(new int[] {7, 2}, 2));
        assertEquals(255, array.getSample(new int[] {2, 5}, 0));
        assertEquals(  0, array.getSample(new int[] {2, 5}, 1));
        assertEquals(  0, array.getSample(new int[] {2, 5}, 2));
        assertEquals(255, array.getSample(new int[] {7, 5}, 0));
        assertEquals(  0, array.getSample(new int[] {7, 5}, 1));
        assertEquals(  0, array.getSample(new int[] {7, 5}, 2));
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#getValue(int[], int)}.
     */
    @Test
    public final void testGetValue_2d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_2d();
        
        // check image four corners
        assertEquals(0, array.getValue(new int[] {0, 0}, 0), 0.1);
        assertEquals(0, array.getValue(new int[] {0, 0}, 1), 0.1);
        assertEquals(0, array.getValue(new int[] {0, 0}, 2), 0.1);
        assertEquals(9, array.getValue(new int[] {9, 0}, 0), 0.1);
        assertEquals(9, array.getValue(new int[] {9, 0}, 1), 0.1);
        assertEquals(9, array.getValue(new int[] {9, 0}, 2), 0.1);
        assertEquals(70, array.getValue(new int[] {0, 7}, 0), 0.1);
        assertEquals(70, array.getValue(new int[] {0, 7}, 1), 0.1);
        assertEquals(70, array.getValue(new int[] {0, 7}, 2), 0.1);
        assertEquals(79, array.getValue(new int[] {9, 7}, 0), 0.1);
        assertEquals(79, array.getValue(new int[] {9, 7}, 1), 0.1);
        assertEquals(79, array.getValue(new int[] {9, 7}, 2), 0.1);
        
        // check four corners or the region in binary image
        assertEquals(255, array.getValue(new int[] {2, 2}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {2, 2}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {2, 2}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {7, 2}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {7, 2}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {7, 2}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {2, 5}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {2, 5}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {2, 5}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {7, 5}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {7, 5}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {7, 5}, 2), 0.1);
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#iterator()}.
     */
    @Test
    public final void testIterator_2d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_2d();
        int count = 0;
        for (@SuppressWarnings("unused") RGB8 rgb : array)
        {
            count++;
        }
        assertEquals(80, count);
    }
    
    /**
     * Creates a RGB8 view, from a UInt8Array with size(10,8) and a binary mask
     * with same size.
     *  
     * @return a sample 10-by-8 RGB8 overlay view
     */
    private BinaryOverlayRGB8Array createSampleArray_2d()
    {
        UInt8Array2D baseArray = UInt8Array2D.create(10, 8);
        baseArray.fillInts((x,y) -> y * 10 + x);
        BinaryArray2D binaryArray = BinaryArray2D.create(10, 8);
        binaryArray.fillBooleans((x,y) -> x >= 2 && x <= 7 && y >= 2 && y <= 5);
        
        BinaryOverlayRGB8Array res = new BinaryOverlayRGB8Array(baseArray, binaryArray, RGB8.RED);
        return res;
    }
    
    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#get(int[])}.
     */
    @Test
    public final void testGet_3d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_3d();
        
        // check image eight corners
        assertEquals(new RGB8(  0,   0,   0), array.get(new int[] {0, 0, 0}));
        assertEquals(new RGB8(  4,   4,   4), array.get(new int[] {4, 0, 0}));
        assertEquals(new RGB8( 30,  30,  30), array.get(new int[] {0, 3, 0}));
        assertEquals(new RGB8( 34,  34,  34), array.get(new int[] {4, 3, 0}));
        assertEquals(new RGB8(200, 200, 200), array.get(new int[] {0, 0, 2}));
        assertEquals(new RGB8(204, 204, 204), array.get(new int[] {4, 0, 2}));
        assertEquals(new RGB8(230, 230, 230), array.get(new int[] {0, 3, 2}));
        assertEquals(new RGB8(234, 234, 234), array.get(new int[] {4, 3, 2}));
        // check four corners of the region in binary image
        assertEquals(RGB8.RED, array.get(new int[] {1, 1, 1}));
        assertEquals(RGB8.RED, array.get(new int[] {3, 1, 1}));
        assertEquals(RGB8.RED, array.get(new int[] {1, 2, 1}));
        assertEquals(RGB8.RED, array.get(new int[] {3, 2, 1}));
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#getValue(int[], int)}.
     */
    @Test
    public final void testGetSample_3d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_3d();
        
        // check image eight corners
        assertEquals(  0, array.getSample(new int[] {0, 0, 0}, 0));
        assertEquals(  0, array.getSample(new int[] {0, 0, 0}, 1));
        assertEquals(  0, array.getSample(new int[] {0, 0, 0}, 2));
        assertEquals(  4, array.getSample(new int[] {4, 0, 0}, 0));
        assertEquals(  4, array.getSample(new int[] {4, 0, 0}, 1));
        assertEquals(  4, array.getSample(new int[] {4, 0, 0}, 2));
        assertEquals( 30, array.getSample(new int[] {0, 3, 0}, 0));
        assertEquals( 30, array.getSample(new int[] {0, 3, 0}, 1));
        assertEquals( 30, array.getSample(new int[] {0, 3, 0}, 2));
        assertEquals( 34, array.getSample(new int[] {4, 3, 0}, 0));
        assertEquals( 34, array.getSample(new int[] {4, 3, 0}, 1));
        assertEquals( 34, array.getSample(new int[] {4, 3, 0}, 2));
        assertEquals(200, array.getSample(new int[] {0, 0, 2}, 0));
        assertEquals(200, array.getSample(new int[] {0, 0, 2}, 1));
        assertEquals(200, array.getSample(new int[] {0, 0, 2}, 2));
        assertEquals(204, array.getSample(new int[] {4, 0, 2}, 0));
        assertEquals(204, array.getSample(new int[] {4, 0, 2}, 1));
        assertEquals(204, array.getSample(new int[] {4, 0, 2}, 2));
        assertEquals(230, array.getSample(new int[] {0, 3, 2}, 0));
        assertEquals(230, array.getSample(new int[] {0, 3, 2}, 1));
        assertEquals(230, array.getSample(new int[] {0, 3, 2}, 2));
        assertEquals(234, array.getSample(new int[] {4, 3, 2}, 0));
        assertEquals(234, array.getSample(new int[] {4, 3, 2}, 1));
        assertEquals(234, array.getSample(new int[] {4, 3, 2}, 2));
        
        // check four corners or the region in binary image
        assertEquals(255, array.getSample(new int[] {1, 1, 1}, 0));
        assertEquals(  0, array.getSample(new int[] {1, 1, 1}, 1));
        assertEquals(  0, array.getSample(new int[] {1, 1, 1}, 2));
        assertEquals(255, array.getSample(new int[] {3, 1, 1}, 0));
        assertEquals(  0, array.getSample(new int[] {3, 1, 1}, 1));
        assertEquals(  0, array.getSample(new int[] {3, 1, 1}, 2));
        assertEquals(255, array.getSample(new int[] {1, 2, 1}, 0));
        assertEquals(  0, array.getSample(new int[] {1, 2, 1}, 1));
        assertEquals(  0, array.getSample(new int[] {1, 2, 1}, 2));
        assertEquals(255, array.getSample(new int[] {3, 2, 1}, 0));
        assertEquals(  0, array.getSample(new int[] {3, 2, 1}, 1));
        assertEquals(  0, array.getSample(new int[] {3, 2, 1}, 2));
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#getValue(int[], int)}.
     */
    @Test
    public final void testGetValue_3d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_3d();
        
        // check image eight corners
        assertEquals(  0, array.getValue(new int[] {0, 0, 0}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {0, 0, 0}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {0, 0, 0}, 2), 0.1);
        assertEquals(  4, array.getValue(new int[] {4, 0, 0}, 0), 0.1);
        assertEquals(  4, array.getValue(new int[] {4, 0, 0}, 1), 0.1);
        assertEquals(  4, array.getValue(new int[] {4, 0, 0}, 2), 0.1);
        assertEquals( 30, array.getValue(new int[] {0, 3, 0}, 0), 0.1);
        assertEquals( 30, array.getValue(new int[] {0, 3, 0}, 1), 0.1);
        assertEquals( 30, array.getValue(new int[] {0, 3, 0}, 2), 0.1);
        assertEquals( 34, array.getValue(new int[] {4, 3, 0}, 0), 0.1);
        assertEquals( 34, array.getValue(new int[] {4, 3, 0}, 1), 0.1);
        assertEquals( 34, array.getValue(new int[] {4, 3, 0}, 2), 0.1);
        assertEquals(200, array.getValue(new int[] {0, 0, 2}, 0), 0.1);
        assertEquals(200, array.getValue(new int[] {0, 0, 2}, 1), 0.1);
        assertEquals(200, array.getValue(new int[] {0, 0, 2}, 2), 0.1);
        assertEquals(204, array.getValue(new int[] {4, 0, 2}, 0), 0.1);
        assertEquals(204, array.getValue(new int[] {4, 0, 2}, 1), 0.1);
        assertEquals(204, array.getValue(new int[] {4, 0, 2}, 2), 0.1);
        assertEquals(230, array.getValue(new int[] {0, 3, 2}, 0), 0.1);
        assertEquals(230, array.getValue(new int[] {0, 3, 2}, 1), 0.1);
        assertEquals(230, array.getValue(new int[] {0, 3, 2}, 2), 0.1);
        assertEquals(234, array.getValue(new int[] {4, 3, 2}, 0), 0.1);
        assertEquals(234, array.getValue(new int[] {4, 3, 2}, 1), 0.1);
        assertEquals(234, array.getValue(new int[] {4, 3, 2}, 2), 0.1);
        
        // check four corners or the region in binary image
        assertEquals(255, array.getValue(new int[] {1, 1, 1}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {1, 1, 1}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {1, 1, 1}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {3, 1, 1}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {3, 1, 1}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {3, 1, 1}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {1, 2, 1}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {1, 2, 1}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {1, 2, 1}, 2), 0.1);
        assertEquals(255, array.getValue(new int[] {3, 2, 1}, 0), 0.1);
        assertEquals(  0, array.getValue(new int[] {3, 2, 1}, 1), 0.1);
        assertEquals(  0, array.getValue(new int[] {3, 2, 1}, 2), 0.1);
    }

    /**
     * Test method for {@link net.sci.array.color.BinaryOverlayRGB8Array#iterator()}.
     */
    @Test
    public final void testIterator_3d()
    {
        BinaryOverlayRGB8Array array = createSampleArray_3d();
        int count = 0;
        int redCount = 0;
        
        for (RGB8 rgb : array)
        {
            count++;
            if (rgb == RGB8.RED)
            {
                redCount++;
            }
        }
        
        assertEquals(60, count);
        assertEquals(6, redCount);
    }
    
    /**
     * Creates a RGB8 view, from a UInt8Array with size(5,4,3) and a binary mask
     * with same size.
     *  
     * @return a sample 5--by-4-by-3 RGB8 overlay view
     */
    private BinaryOverlayRGB8Array createSampleArray_3d()
    {
        UInt8Array3D baseArray = UInt8Array3D.create(5, 4, 3);
        baseArray.fillInts((x,y,z) -> z * 100 + y * 10 + x);
        BinaryArray3D binaryArray = BinaryArray3D.create(5, 4, 3);
        binaryArray.fillBooleans((x,y,z) -> x >= 1 && x <= 3 && y >= 1 && y <= 2 && z == 1);
        
        BinaryOverlayRGB8Array res = new BinaryOverlayRGB8Array(baseArray, binaryArray, RGB8.RED);
        return res;
    }

}
