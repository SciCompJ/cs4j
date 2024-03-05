/**
 * 
 */
package net.sci.image.morphology.filter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.BufferedBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;
import net.sci.image.morphology.strel.Cross3x3Strel;
import net.sci.image.morphology.strel.SquareStrel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * @author dlegland
 *
 */
public class BinaryDilationTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#dilation(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_dilationRows_singleRun_singleRun()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 5; i < 10; i++)
        {
            row1.set(i, true);
        }
        // 
        BinaryRow row2 = new BinaryRow();
        for (int i = -1; i <= 1; i++)
        {
            row2.set(i, true);
        }
        
        BinaryRow res = BinaryDilation.dilation(row1, row2);
        
        assertEquals(1, res.runCount());
        assertFalse(res.get(3));
        assertTrue(res.get(4));
        assertTrue(res.get(10));
        assertFalse(res.get(11));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#dilation(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void test_dilation_MergeRuns()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 0; i <= 5; i++)
        {
            row.set(i + 10, true);
            row.set(i + 20, true);
        }
        assertEquals(2, row.runCount());
        assertFalse(row.get(16));
       
        // dilation should fill indices 16 and 17 from the left, 
        // and indices 18 and 19 from the right
        BinaryRow row2 = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            row2.set(i, true);
        }
        BinaryRow res = BinaryDilation.dilation(row, row2);
        
        assertEquals(1, res.runCount());
        assertFalse(res.get(7));
        assertTrue(res.get(8));
        assertTrue(res.get(27));
        assertFalse(res.get(28));
    }

    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcessBinary2d_circles_square5x5() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();

        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        BinaryArray2D array = BinaryArray2D.wrap(BinaryArray.convert((ScalarArray<?>) image.getData()));
        
        // create a dilation using a square structuring element
        Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
        BinaryDilation op = new BinaryDilation(strel);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.convert(strel.dilation(array)));
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertTrue(String.format("x=%d, y=%d", x, y), expected.getBoolean(x, y) == res.getBoolean(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcessBinary2d_circles_disk7x7() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();

        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        BinaryArray2D array = BinaryArray2D.wrap(BinaryArray.convert((ScalarArray<?>) image.getData()));
        
        // create a dilation using a disk structuring element
        Strel2D strel = Strel2D.Shape.DISK.fromDiameter(7);
        BinaryDilation op = new BinaryDilation(strel);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.convert(strel.dilation(array)));
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertTrue(String.format("x=%d, y=%d", x, y), expected.getBoolean(x, y) == res.getBoolean(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_twoSquares_Square3x3()
    {
        BufferedBinaryArray2D array = new BufferedBinaryArray2D(12, 8);
        
        // a ring-like structure
        fillRect(array, 2, 3, 2, 3, true);
        fillRect(array, 8, 9, 2, 3, true);
        
        // create a dilation using a square structuring element
        Strel2D strel2d = new SquareStrel(3);
        BinaryDilation op = new BinaryDilation(strel2d);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.dilation(array)));
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                assertTrue(String.format("x=%d, y=%d", x, y), res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryDilation#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcessBinary2d_basicShapes_Cross3x3()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(12, 12);
        // an isolated pixel
        array.setBoolean(2, 2, true);
        
        // a ring-like structure
        fillRect(array, 6, 9, 2, 5, true);
        fillRect(array, 7, 8, 3, 4, false);
        
        // diagonal pixels
        array.setBoolean(2, 6, true);
        array.setBoolean(3, 7, true);
        array.setBoolean(4, 8, true);
        array.setBoolean(5, 9, true);
        
        // create a dilation using cross-shaped structuring element
        Strel2D strel2d = new Cross3x3Strel();
        BinaryDilation op = new BinaryDilation(strel2d);
        
        // run operator
        BinaryArray2D res = op.processBinary2d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.dilation(array)));
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }


    private static final void fillRect(BinaryArray2D array, int xmin, int xmax, int ymin, int ymax, boolean state)
    {
        for (int y = ymin; y <= ymax; y++)
        {
            for (int x = xmin; x <= xmax; x++)
            {
                array.setBoolean(x, y, state);
            }
        }
    }
    
    
    /**
     * Test method for {@link net.sci.image.morphology.filter.BinaryDilation#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testProcessBinary3d_Cubes_Cube3x3x3()
    {
        // init sample array
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(12, 6, 6);
        fillRect(array, 2, 3, 2, 3, 2, 3, true);
        fillRect(array, 8, 9, 2, 3, 2, 3, true);
        
        // create operator
        Strel3D strel = Strel3D.Shape.CUBE.fromDiameter(3);
        BinaryDilation op = new BinaryDilation(strel);
        
        // run operator
        BinaryArray3D res = op.processBinary3d(array);
        
        // compare with the result obtained using "classical" algorithm
        BinaryArray3D expected = BinaryArray3D.wrap(BinaryArray.convert(strel.dilation(array)));
        for (int z = 0; z < array.size(2); z++)
        {
            for (int y = 0; y < array.size(1); y++)
            {
                for (int x = 0; x < array.size(0); x++)
                {
                    assertTrue(String.format("x=%d, y=%d, z=%d", x, y, z), expected.getBoolean(x, y, z) == res.getBoolean(x, y, z));
                }
            }
        }
    }
    
    private static final void fillRect(BinaryArray3D array, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, boolean state)
    {
        for (int z = zmin; z <= zmax; z++)
        {
            for (int y = ymin; y <= ymax; y++)
            {
                for (int x = xmin; x <= xmax; x++)
                {
                    array.setBoolean(x, y, z, state);
                }
            }
        }
    }
}
