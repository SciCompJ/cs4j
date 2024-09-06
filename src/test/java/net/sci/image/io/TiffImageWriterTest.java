/**
 * 
 */
package net.sci.image.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.Image;

/**
 * @author dlegland
 *
 */
public class TiffImageWriterTest
{
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_UInt8_2D_10x8() throws IOException
    {
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        array.fillValues((x,y) -> 10.0 * y + x);
        Image image = new Image(array);
        
        File outputFile = new File("testWriteTiff.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(2, image2.getDimension());
        assertEquals(10, image2.getSize(0));
        assertEquals(8, image2.getSize(1));
        
        ScalarArray<?> array2 = (ScalarArray<?>) image2.getData();
        assertEquals(array2.getValue(new int[] {0, 0}),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 0}),  9.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7}), 70.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}), 79.0, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_UInt8_3D_10x8x6() throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(10, 8, 6);
        array.fillValues((x,y,z) -> 20.0 * z + 10.0 * y + x);
        Image image = new Image(array);
        
        File outputFile = new File("testWriteTiff3d.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(3, image2.getDimension());
        assertEquals(10, image2.getSize(0));
        assertEquals(8, image2.getSize(1));
        assertEquals(6, image2.getSize(2));
        
        ScalarArray<?> array2 = (ScalarArray<?>) image2.getData();
        assertEquals(array2.getValue(new int[] {0, 0, 0}),   0.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 0, 0}),   9.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7, 0}),  70.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7, 0}),  79.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 0, 5}), 100.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 0, 5}), 109.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7, 5}), 170.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7, 5}), 179.0, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
}
