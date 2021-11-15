/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.scalar.ScalarArray;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;
import net.sci.image.morphology.strel.Cross3x3Strel;
import net.sci.image.morphology.strel.SquareStrel;
import net.sci.image.morphology.strel.Strel2D;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArray2DTest
{
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#convert(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testConvert()
    {
        BinaryArray2D array = new BufferedBinaryArray2D(12, 12);
        fillRect(array, 2, 10, 2, 10, true);
        fillRect(array, 4, 8, 4, 8, false);
        array.setBoolean(6, 6, true);
        
        RunLengthBinaryArray2D converted = RunLengthBinaryArray2D.convert(array);
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(array.getBoolean(x, y) == converted.getBoolean(x, y));  
            }
        }
    }
    
    
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     * @throws IOException 
     */
    @Test
    public final void testDilation_circles_square5x5() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();

        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        BinaryArray2D array = BinaryArray2D.wrap(BinaryArray.convert((ScalarArray<?>) image.getData()));
        
        Strel2D strel = Strel2D.Shape.SQUARE.fromDiameter(5);
        RunLengthBinaryArray2D strelArray = RunLengthBinaryArray2D.convert(strel.getMask());

        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.dilation(rleArray, strelArray, new int[] {2, 2});
        
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
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     * @throws IOException 
     */
    @Test
    public final void testDilation_circles_disk7x7() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/circles.tif").getFile();

        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        BinaryArray2D array = BinaryArray2D.wrap(BinaryArray.convert((ScalarArray<?>) image.getData()));
        
        Strel2D strel = Strel2D.Shape.DISK.fromDiameter(7);
        RunLengthBinaryArray2D strelArray = RunLengthBinaryArray2D.convert(strel.getMask());

        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.dilation(rleArray, strelArray, new int[] {3, 3});
        
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
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testDilation_twoSquares_Square3x3()
    {
        BufferedBinaryArray2D array = new BufferedBinaryArray2D(12, 8);
        
        // a ring-like structure
        fillRect(array, 2, 3, 2, 3, true);
        fillRect(array, 8, 9, 2, 3, true);
        
        // create a square-shaped structuring element
        BufferedBinaryArray2D strel = new BufferedBinaryArray2D(3, 3);
        strel.fill(new Binary(true));
        
        RunLengthBinaryArray2D array2 = RunLengthBinaryArray2D.convert(array);
        RunLengthBinaryArray2D strel2 = RunLengthBinaryArray2D.convert(strel);
        
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.dilation(array2, strel2, new int[] {1, 1});
        
        Strel2D strel2d = new SquareStrel(3);
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
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testDilation_basicShapes_Cross3x3()
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
        
        // create a cross-shaped structuring element
        RunLengthBinaryArray2D strel = new RunLengthBinaryArray2D(3, 3);
        strel.setBoolean(1, 0, true);
        strel.setBoolean(0, 1, true);
        strel.setBoolean(1, 1, true);
        strel.setBoolean(2, 1, true);
        strel.setBoolean(1, 2, true);
        
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.dilation(array, strel, new int[] {1, 1});
        
        Strel2D strel2d = new Cross3x3Strel();
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.dilation(array)));
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testErosion_cross()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(12, 12);
        
        // a thick ring-like structure
        fillRect(array, 1, 9, 1, 9, true);
        array.setBoolean(5, 5, false);
        
        // create a cross-shaped structuring element
        RunLengthBinaryArray2D strel = new RunLengthBinaryArray2D(3, 3);
        strel.setBoolean(1, 0, true);
        strel.setBoolean(0, 1, true);
        strel.setBoolean(1, 1, true);
        strel.setBoolean(2, 1, true);
        strel.setBoolean(1, 2, true);
        
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.erosion(array, strel, new int[] {1, 1});
        
        Strel2D strel2d = new Cross3x3Strel();
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.erosion(array)));
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(res.getBoolean(x, y) == expected.getBoolean(x, y));
            }
        }
    }

    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testErosion_square5x5()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(14, 14);
        
        // a thick ring-like structure
        fillRect(array, 1, 12, 1, 12, true);
        fillRect(array, 6, 8, 7, 7, false);
        
        // create a cross-shaped structuring element
        RunLengthBinaryArray2D strel = new RunLengthBinaryArray2D(5, 5);
        strel.fill(Binary.TRUE);
        
        RunLengthBinaryArray2D res = RunLengthBinaryArray2D.erosion(array, strel, new int[] {2, 2});
        
        Strel2D strel2d = Strel2D.Shape.SQUARE.fromDiameter(5);
        BinaryArray2D expected = BinaryArray2D.wrap(BinaryArray.wrap(strel2d.erosion(array)));
        
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
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testIterator()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(5, 5);
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                array.setBoolean(x, y, true);
            }
        }
        
        int count = 0;
        for (Binary binary : array)
        {
            if (binary.getBoolean())
            {
                count++;
            }
        }
        
        assertEquals(25, count);
    }
}
