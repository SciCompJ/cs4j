/**
 * 
 */
package net.sci.image.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Vector;
import net.sci.array.numeric.Float64VectorArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.VectorArray;
import net.sci.image.Image;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.TiffTag;

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
    public void testWriteImage_UInt8_10x8() throws IOException
    {
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        array.fillInts((x,y) -> 10 * y + x);
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
    public void testWriteImage_UInt8_10x8_customTag() throws IOException
    {
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        array.fillInts((x,y) -> 10 * y + x);
        Image image = new Image(array);
        
        File outputFile = new File("testWriteTiff.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        String softwareString = "CS4J Test Suite";
        writer.addCustomTag(new BaselineTags.Software().setValue(softwareString));
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
        
        TiffTag softwareTag = image2.tiffTags.get(BaselineTags.Software.CODE);
        assertNotNull(softwareTag);
        assertEquals(softwareString, (String) softwareTag.content);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_UInt16_10x8() throws IOException
    {
        UInt16Array2D array = UInt16Array2D.create(10, 8);
        array.fillInts((x,y) -> 1000 * y + 10 * x);
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_uint16_10x8.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(2, image2.getDimension());
        assertEquals(10, image2.getSize(0));
        assertEquals(8, image2.getSize(1));
        
        ScalarArray<?> array2 = (ScalarArray<?>) image2.getData();
        assertEquals(array2.getValue(new int[] {0, 0}),    0.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 0}),   90.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7}), 7000.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}), 7090.0, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_Float32_10x8() throws IOException
    {
        Float32Array2D array = Float32Array2D.create(10, 8);
        array.fillValues((x,y) -> 10.0 * y + 0.1 * x);
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_float32_10x8.tif");
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
        assertEquals(array2.getValue(new int[] {9, 0}),  0.9, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7}), 70.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}), 70.9, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_Float64_10x8() throws IOException
    {
        Float64Array2D array = Float64Array2D.create(10, 8);
        array.fillValues((x,y) -> 10.0 * y + 0.1 * x);
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_float64_10x8.tif");
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
        assertEquals(array2.getValue(new int[] {9, 0}),  0.9, 0.01);
        assertEquals(array2.getValue(new int[] {0, 7}), 70.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}), 70.9, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_Float32Vector_3_10x8() throws IOException
    {
        Float32VectorArray2D array = Float32VectorArray2D.create(10, 8, 3);
        array.fill((x,y) -> new Float32Vector(new double[] {x, y, y + x * 0.1}));
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_float32Vector_10x8.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(2, image2.getDimension());
        assertEquals(10, image2.getSize(0));
        assertEquals(8, image2.getSize(1));
        
        VectorArray<?,?> array2 = (VectorArray<?,?>) image2.getData();
        assertEquals(3, array2.channelCount());
        assertEquals(array2.getValue(new int[] {0, 0}, 0),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 0}, 1),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 0}, 2),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 0),  9.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 1),  7.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 2),  7.9, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_Float64Vector_3_10x8() throws IOException
    {
        Float64VectorArray2D array = Float64VectorArray2D.create(10, 8, 3);
        array.fill((x,y) -> new Float64Vector(new double[] {x, y, y + x * 0.1}));
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_float64Vector_10x8.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(2, image2.getDimension());
        assertEquals(10, image2.getSize(0));
        assertEquals(8, image2.getSize(1));
        
        VectorArray<?,?> array2 = (VectorArray<?,?>) image2.getData();
        assertEquals(3, array2.channelCount());
        assertEquals(array2.getValue(new int[] {0, 0}, 0),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 0}, 1),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {0, 0}, 2),  0.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 0),  9.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 1),  7.0, 0.01);
        assertEquals(array2.getValue(new int[] {9, 7}, 2),  7.9, 0.01);
        
        boolean b = outputFile.delete();
        assertTrue(b);
    }
    
    /**
     * Test method for {@link net.sci.image.io.TiffImageWriter#writeImage(net.sci.image.Image)}.
     * @throws IOException 
     */
    @Test
    public void testWriteImage_RGB8_20x10() throws IOException
    {
        RGB8Array2D array = RGB8Array2D.create(20, 10);
        array.fill((x,y) -> new RGB8(x * 10, y * 20, x * 10));
        Image image = new Image(array);
        
        File outputFile = new File("test_writeTiff_rgb8_20x10.tif");
        TiffImageWriter writer = new TiffImageWriter(outputFile);
        writer.writeImage(image);
        
        assertTrue(outputFile.exists());
        
        TiffImageReader reader = new TiffImageReader(outputFile);
        Image image2 = reader.readImage();
        
        assertEquals(2, image2.getDimension());
        assertEquals(image.getSize(0), image2.getSize(0));
        assertEquals(image.getSize(1), image2.getSize(1));
        
        RGB8Array array2 = (RGB8Array) image2.getData();
        assertEquals(new RGB8(  0,   0,   0), array2.get(new int[] { 0, 0}));
        assertEquals(new RGB8(190,   0, 190), array2.get(new int[] {19, 0}));
        assertEquals(new RGB8(  0, 180,   0), array2.get(new int[] { 0, 9}));
        assertEquals(new RGB8(190, 180, 190), array2.get(new int[] {19, 9}));
        
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
