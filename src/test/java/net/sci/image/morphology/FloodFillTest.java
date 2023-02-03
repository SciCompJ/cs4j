/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.data.Connectivity3D;

/**
 * @author dlegland
 *
 */
public class FloodFillTest
{
    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Square_C4()
    {
        int sizeX = 10;
        int sizeY = 10;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        fillRect(array, 3, 3, 4, 4, 8);
        
        FloodFill.floodFill(array, 3, 3, 12, 4);
        
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                assertEquals(12, array.getInt(x, y));
            }
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Square_C8()
    {
        int sizeX = 10;
        int sizeY = 10;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        fillRect(array, 3, 3, 4, 4, 8);
        
        FloodFill.floodFill(array, 3, 3, 12, 8);
        
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                assertEquals(12, array.getInt(x, y));
            }
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Concave_C4()
    {
        int sizeX = 12;
        int sizeY = 8;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        fillRect(array, 1, 1, 10, 6, 200);
        fillRect(array, 3, 1, 2, 4, 50);
        fillRect(array, 7, 3, 2, 4, 50);
//        System.out.println(array);
        
        FloodFill.floodFill(array, 9, 5, 100, 4);
        
        assertEquals(100, array.getInt( 1, 1));
        assertEquals(100, array.getInt( 1, 6));
        assertEquals(100, array.getInt(10, 1));
        assertEquals(100, array.getInt(10, 6));
        
        assertEquals( 50, array.getInt( 4, 4));
        assertEquals( 50, array.getInt( 8, 3));
    }

    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Concave_C8()
    {
        int sizeX = 12;
        int sizeY = 8;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        fillRect(array, 1, 1, 10, 6, 200);
        fillRect(array, 3, 1, 2, 4, 50);
        fillRect(array, 7, 3, 2, 4, 50);
//        System.out.println(array);
        
        FloodFill.floodFill(array, 9, 5, 100, 8);
        
        assertEquals(100, array.getInt( 1, 1));
        assertEquals(100, array.getInt( 1, 6));
        assertEquals(100, array.getInt(10, 1));
        assertEquals(100, array.getInt(10, 6));
        
        assertEquals( 50, array.getInt( 4, 4));
        assertEquals( 50, array.getInt( 8, 3));
    }

    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_FullImage_C4()
    {
        int sizeX = 10;
        int sizeY = 10;
        UInt8Array2D array = UInt8Array2D.create(sizeX, sizeY);
        array.fillInt(8);
        
        FloodFill.floodFill(array, 3, 3, 12, 4);
        
        for (int y = 0; y < 10; y++)
        {
            for (int x = 0; x < 10; x++)
            {
                assertEquals(12, array.getInt(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Cross3d_C6()
    {
        UInt8Array3D array = createCross3d();
        
        // Apply
        FloodFill.floodFill(array, 2, 2, 4, 50, 6);
        
        assertEquals( 50, array.getInt(2, 2, 0));
        assertEquals( 50, array.getInt(2, 2, 4));
        assertEquals( 50, array.getInt(2, 0, 2));
        assertEquals( 50, array.getInt(2, 4, 2));
        assertEquals( 50, array.getInt(0, 2, 2));
        assertEquals( 50, array.getInt(4, 2, 2));
    }

    /**
     * Test method for {@link net.sci.image.morphology.FloodFill#floodFill(net.sci.array.scalar.ScalarArray2D, int, int, double, int)}.
     */
    @Test
    public final void testFloodFill_InPlace_Cross3d_C26()
    {
        UInt8Array3D array = createCross3d();
        
        // Apply
        FloodFill.floodFill(array, 2, 2, 4, 50, 26);
        
        assertEquals( 50, array.getInt(2, 2, 0));
        assertEquals( 50, array.getInt(2, 2, 4));
        assertEquals( 50, array.getInt(2, 0, 2));
        assertEquals( 50, array.getInt(2, 4, 2));
        assertEquals( 50, array.getInt(0, 2, 2));
        assertEquals( 50, array.getInt(4, 2, 2));
    }


    @Test
    public final void testFloodFill_Pair_C8()
    {
        UInt8Array2D array = UInt8Array2D.fromIntArray(new int[][] {
            { 10, 10, 10, 20, 20, 20, 10, 10, 10, 10, 20, 20, 10, 10, 10 },
            { 10, 10, 20, 20, 20, 20, 20, 20, 10, 20, 20, 20, 20, 10, 10 },
            { 10, 20, 10, 10, 10, 10, 20, 20, 10, 20, 10, 10, 20, 20, 10 },
            { 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 20, 20 },
            { 20, 20, 10, 20, 10, 10, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
            { 20, 20, 10, 10, 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
            { 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 10, 10, 20, 10 },
            { 10, 20, 10, 20, 20, 20, 10, 20, 20, 20, 20, 20, 20, 20, 10 },
            { 10, 10, 20, 20, 10, 10, 10, 10, 10, 10, 10, 20, 20, 10, 10 }, 
        });
        int sizeX = array.size(0);
        int sizeY = array.size(1);
    
        // initialize empty result image fill with 255
        UInt8Array2D result = UInt8Array2D.create(sizeX, sizeY);
        result.fillInt(255);
    
        // Apply
        FloodFill.floodFillInt(array, 7, 4, result, 50, 8);
        // printImage(result);
    
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (array.getInt(x, y) == 20)
                    assertEquals(50, result.getInt(x, y));
                else
                    assertEquals(255, result.getInt(x, y));
            }
        }
    }

    @Test
    public final void testFloodFill_Pair_EmptySquaresC4()
    {
        UInt8Array2D array = UInt8Array2D.fromIntArray(new int[][] {
            {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            {10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            {10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        });

        // initialize result
        UInt8Array2D result = UInt8Array2D.create(11, 11);
        result.fillInt(255);

        // compute flood fill result
        FloodFill.floodFillInt(array, 1, 0, result, 50, 4);

        assertEquals(50, result.getInt(0, 0));
        assertEquals(50, result.getInt(10, 0));
        assertEquals(50, result.getInt(0, 10));
        assertEquals(50, result.getInt(10, 10));

        assertEquals(50, result.getInt(5, 3));
        assertEquals(50, result.getInt(5, 7));
        assertEquals(50, result.getInt(3, 5));
        assertEquals(50, result.getInt(7, 5));

        // printImage(result);
    }
    
    @Test
    public final void testFloodFill_Pair_Cross3d_C26Float()
    {
        BinaryArray3D image = createCornerCross();
        // System.out.println("input image:");
        // printStack(image);

        IntArray3D<?> result = UInt8Array3D.create(image.size(0), image.size(1), image.size(2)); 
                
        int newVal = 120;
        FloodFill.floodFill(image, 2, 4, 4, result, newVal, Connectivity3D.C26);

        // System.out.println("output image:");
        // printStack(result);

        // Test each of the branches
        assertEquals(newVal, result.getInt(0, 4, 4));
        assertEquals(newVal, result.getInt(8, 4, 4));
        assertEquals(newVal, result.getInt(4, 0, 4));
        assertEquals(newVal, result.getInt(4, 8, 4));
        assertEquals(newVal, result.getInt(4, 4, 0));
        assertEquals(newVal, result.getInt(4, 4, 8));
    }
    
    public UInt8Array3D createCross3d()
    {
        // Create test image
        int sizeX = 5;
        int sizeY = 5;
        int sizeZ = 5;
        
        UInt8Array3D array = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        int val0 = 100;
        // create three isothetic axes
        for (int i = 0; i < 5; i++)
        {
            array.setValue(i, 2, 2, val0);
            array.setValue(2, i, 2, val0);
            array.setValue(2, 2, i, val0);
        }
        return array;
    }

    /**
     * Creates a stack representing a cross with branches touching only by
     * corners.
     */
    public BinaryArray3D createCornerCross()
    {
        // Create test image
        int sizeX = 9;
        int sizeY = 9;
        int sizeZ = 9;
        BinaryArray3D image = BinaryArray3D.create(sizeX, sizeY, sizeZ);
        int val0 = 50;
        
        // Center voxel
        image.setInt(val0, 4, 4, 4);
        // eight corners
        image.setInt(3, 3, 3, val0);
        image.setInt(3, 3, 5, val0);
        image.setInt(3, 5, 3, val0);
        image.setInt(3, 5, 5, val0);
        image.setInt(5, 3, 3, val0);
        image.setInt(5, 3, 5, val0);
        image.setInt(5, 5, 3, val0);
        image.setInt(5, 5, 5, val0);
        // six branches
        for (int i = 0; i < 3; i++)
        {
            image.setInt(i, 4, 4, val0);
            image.setInt(i + 6, 4, 4, val0);
            image.setInt(4, i, 4, val0);
            image.setInt(4, i + 6, 4, val0);
            image.setInt(4, 4, i, val0);
            image.setInt(4, 4, i + 6, val0);
        }

        return image;
    }
    
    private static final void fillRect(ScalarArray2D<?> array, int x0, int y0, int w, int h, double value)
    {
        for (int y = y0; y < Math.min(y0 + h, array.size(1)); y++)
        {
            for (int x = x0; x < Math.min(x0 + w, array.size(0)); x++)
            {
                array.setValue(x, y, value);
            }
        }
    }
}
