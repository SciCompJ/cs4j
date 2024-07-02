/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class SquareStrelTest
{
    @Test
    public void testGetSize()
    {
        Strel2D strel = new SquareStrel(5);
        int[] size = strel.size();
        assertEquals(size[0], 5);
        assertEquals(size[1], 5);
    }
    
    @Test
    public void testReverse()
    {
        Strel2D strel = new SquareStrel(5);
        int[] size = strel.size();
        Strel2D strel2 = strel.reverse();
        int[] size2 = strel2.size();
        assertEquals(size[0], size2[0]);
        assertEquals(size[1], size2[1]);
    }
    
    @Test
    public void testGetMask()
    {
        Strel2D strel = new SquareStrel(5);
        BinaryArray2D mask = strel.binaryMask();
        
        assertEquals(mask.size(0), 5);
        assertEquals(mask.size(1), 5);
    }
    
    @Test
    public void testGetShifts()
    {
        Strel2D strel = new SquareStrel(5);
        int[][] shifts = strel.shifts();
        
        assertEquals(shifts.length, 5 * 5);
        assertEquals(shifts[1].length, 2);
    }
    
    @Test
    public void testErosion_Square4x4()
    {
        ScalarArray2D<?> array = createImage_Square4x4();
        Strel2D strel = new SquareStrel(3);
        
        ScalarArray2D<?> result = strel.erosion(array);
        
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                assertEquals(0, result.getValue(x, y), .01);
            }
        }
        for (int y = 4; y < 6; y++)
        {
            assertEquals(  0, result.getValue(3, y), .01);
            assertEquals(255, result.getValue(4, y), .01);
            assertEquals(255, result.getValue(5, y), .01);
            assertEquals(  0, result.getValue(6, y), .01);
        }
        for (int y = 6; y < 10; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                assertEquals(0, result.getValue(x, y), .01);
            }
        }
    }
    
    @Test
    public void testDilation_Square4x4()
    {
        ScalarArray2D<?> array = createImage_Square4x4();
        Strel2D strel = new SquareStrel(3);
        
        ScalarArray2D<?> result = strel.dilation(array);
        
        for (int y = 0; y < 2; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                assertEquals(0, result.getValue(x, y), 0.01);
            }
        }
        for (int y = 2; y < 8; y++)
        {
            assertEquals(  0, result.getValue(1, y), 0.01);
            assertEquals(255, result.getValue(2, y), 0.01);
            assertEquals(255, result.getValue(7, y), 0.01);
            assertEquals(  0, result.getValue(8, y), 0.01);
        }
        for (int y = 8; y < 10; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                assertEquals(0, result.getValue(x, y), 0.01);
            }
        }
    }
    
    @Test
    public void testClosing()
    {
        ScalarArray2D<?> array = createImage_Square10x10();
        Strel2D strel = new SquareStrel(5);
        
        ScalarArray2D<?> result = strel.closing(array);
        
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertEquals(array.getValue(x, y), result.getValue(x, y), 0.01);
            }
        }
    }
    
    /**
     * Try to compute morphological closing when there is edge effect: the
     * result is completely white.
     */
    @Test
    public void testClosing_EdgeEffect()
    {
        ScalarArray2D<?> array = createImage_Square4x4();
        Strel2D strel = new SquareStrel(15);
        
        ScalarArray2D<?> result = strel.closing(array);
        
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertEquals(255, result.getValue(x, y), 0.01);
            }
        }
    }
    
    /**
     * Try to compute morphological closing with a strel larger than the
     * original array.
     */
    @Test
    public void testClosing_StrelLargerThanImage()
    {
        ScalarArray2D<?> array = createImage_Square4x4();
        Strel2D strel = new SquareStrel(30);
        
        ScalarArray2D<?> result = strel.closing(array);
        
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertEquals(255, result.getValue(x, y), 0.01);
            }
        }
    }
    
    @Test
    public void testOpening()
    {
        ScalarArray2D<?> array = createImage_Square10x10();
        Strel2D strel = new SquareStrel(5);
        
        ScalarArray2D<?> result = strel.opening(array);
        
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertEquals(array.getValue(x, y), result.getValue(x, y), 0.01);
            }
        }
    }
    
//    @Test
//    public void testErosion_Square4x4_short()
//    {
//        ScalarArray2D<?> array = createImage_Square4x4();
//        array = array.convertToShort(false);
//        Strel2D strel = new SquareStrel(3);
//        
//        ScalarArray2D<?> result = strel.erosion(array);
//        
//        for (int y = 0; y < 4; y++)
//        {
//            for (int x = 0; x < 10; x++)
//            {
//                assertEquals(0, result.get(x, y));
//            }
//        }
//        for (int y = 4; y < 6; y++)
//        {
//            assertEquals(0, result.get(3, y));
//            assertEquals(255, result.get(4, y));
//            assertEquals(255, result.get(5, y));
//            assertEquals(0, result.get(6, y));
//        }
//        for (int y = 6; y < 10; y++)
//        {
//            for (int x = 0; x < 10; x++)
//            {
//                assertEquals(0, result.get(x, y));
//            }
//        }
//    }
    
//    @Test
//    public void testDilation_Square4x4_short()
//    {
//        ScalarArray2D<?> array = createImage_Square4x4();
//        array = array.convertToShort(false);
//        Strel2D strel = new SquareStrel(3);
//        
//        ScalarArray2D<?> result = strel.dilation(array);
//        
//        for (int y = 0; y < 2; y++)
//        {
//            for (int x = 0; x < 10; x++)
//            {
//                assertEquals(0, result.get(x, y));
//            }
//        }
//        for (int y = 2; y < 8; y++)
//        {
//            assertEquals(0, result.get(1, y));
//            assertEquals(255, result.get(2, y));
//            assertEquals(255, result.get(7, y));
//            assertEquals(0, result.get(8, y));
//        }
//        for (int y = 8; y < 10; y++)
//        {
//            for (int x = 0; x < 10; x++)
//            {
//                assertEquals(0, result.get(x, y));
//            }
//        }
//    }
    
    /**
     * Creates a 10-by-10 array with a 4-by-4 square in the middle.
     */
    private ScalarArray2D<?> createImage_Square4x4()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(10, 10);
        array.fillValue(0);
        
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                array.setValue(x, y, 255);
            }
        }
        
        return array;
    }
    
    /**
     * Creates a 30-by-30 array with a 10-by-10 square in the middle.
     */
    private ScalarArray2D<?> createImage_Square10x10()
    {
        ScalarArray2D<?> array = UInt8Array2D.create(30, 30);
        array.fillValue(0);
        
        for (int y = 10; y < 20; y++)
        {
            for (int x = 10; x < 20; x++)
            {
                array.setValue(x, y, 255);
            }
        }
        
        return array;
    }

}
