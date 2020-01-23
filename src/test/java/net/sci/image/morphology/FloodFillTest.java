/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.BinaryArray3D;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.image.data.Connectivity3D;

/**
 * @author dlegland
 *
 */
public class FloodFillTest
{
    @Test
    public final void testFloodFillPair_C8()
    {
        int[][] data = new int[][] {
                { 10, 10, 10, 20, 20, 20, 10, 10, 10, 10, 20, 20, 10, 10, 10 },
                { 10, 10, 20, 20, 20, 20, 20, 20, 10, 20, 20, 20, 20, 10, 10 },
                { 10, 20, 10, 10, 10, 10, 20, 20, 10, 20, 10, 10, 20, 20, 10 },
                { 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 20, 20 },
                { 20, 20, 10, 20, 10, 10, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
                { 20, 20, 10, 10, 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
                { 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 10, 10, 20, 10 },
                { 10, 20, 10, 20, 20, 20, 10, 20, 20, 20, 20, 20, 20, 20, 10 },
                { 10, 10, 20, 20, 10, 10, 10, 10, 10, 10, 10, 20, 20, 10, 10 }, };
        int sizeY = data.length;
        int sizeX = data[0].length;
        UInt8Array2D image = UInt8Array2D.create(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                image.setInt(data[y][x], x, y);
            }
        }

        // initialize empty result image fill with 255
        UInt8Array2D result = UInt8Array2D.create(sizeX, sizeY);
        result.fillInt(255);

        // Apply
        FloodFill.floodFillInt(image, 7, 4, result, 50, 8);
        // printImage(result);

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (image.getInt(x, y) == 20)
                    assertEquals(50, result.getInt(x, y));
                else
                    assertEquals(255, result.getInt(x, y));
            }
        }

    }

    @Test
    public final void testFloodFill_EmptySquaresC4()
    {
        int[][] data = new int[][] { 
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
        };
        int sizeY = data.length;
        int sizeX = data[0].length;
        UInt8Array2D image = UInt8Array2D.create(sizeX, sizeY);
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                image.setInt(data[y][x], x, y);
            }
        }

        // initialize result
        UInt8Array2D result = UInt8Array2D.create(11, 11);
        result.fillInt(255);

        // compute flood fill result
        FloodFill.floodFillInt(image, 1, 0, result, 50, 4);

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
    public final void testFloodFillPair_Cross3d_C26Float()
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
        image.setInt(val0, 3, 3, 3);
        image.setInt(val0, 3, 3, 5);
        image.setInt(val0, 3, 5, 3);
        image.setInt(val0, 3, 5, 5);
        image.setInt(val0, 5, 3, 3);
        image.setInt(val0, 5, 3, 5);
        image.setInt(val0, 5, 5, 3);
        image.setInt(val0, 5, 5, 5);
        // six branches
        for (int i = 0; i < 3; i++)
        {
            image.setInt(val0, i, 4, 4);
            image.setInt(val0, i + 6, 4, 4);
            image.setInt(val0, 4, i, 4);
            image.setInt(val0, 4, i + 6, 4);
            image.setInt(val0, 4, 4, i);
            image.setInt(val0, 4, 4, i + 6);
        }

        return image;
    }
}
