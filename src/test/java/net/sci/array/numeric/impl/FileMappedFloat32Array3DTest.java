/**
 * 
 */
package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;

/**
 * @author dlegland
 *
 */
public class FileMappedFloat32Array3DTest
{
    /**
     * Check validity of input test file...
     * 
     * @throws IOException
     *             in case of I/O problem.
     */
    @Test
    public final void test_read_from_raw_using_randomAccessFile() throws IOException
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_float32_msb.raw");
        String fileName = path.toString();
        
        RandomAccessFile raf = new RandomAccessFile(fileName, "r"); 
        
        // convert to linear float array
        // 6*5*4 -> 120
        float[] buffer = new float[120];
        for (int i = 0; i < 120; i++)
        {
            buffer[i] = raf.readFloat();
        }
        raf.close();
        
        // convert to 3D array of Float32
        Float32Array3D array = new BufferedFloat32Array3D(6, 5, 4, buffer);
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
        assertEquals(345.0, array.getValue(5, 4, 3), .01);
    }
    
    
    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedFloat32Array3D#getValue(int[])}.
     */
    @Test
    public final void testGetValue_xyzRamp654_msb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_msb();
        
        assertEquals(3, array.dimensionality());
        assertEquals(6, array.size(0));
        assertEquals(5, array.size(1));
        assertEquals(4, array.size(2));
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
        assertEquals(345.0, array.getValue(5, 4, 3), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedFloat32Array3D#getValue(int[])}.
     */
    @Test
    public final void testGetValue_xyzRamp654_lsb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_lsb();
        
        assertEquals(3, array.dimensionality());
        assertEquals(6, array.size(0));
        assertEquals(5, array.size(1));
        assertEquals(4, array.size(2));
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
        assertEquals(345.0, array.getValue(5, 4, 3), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedFloat32Array3D#slice(int)}.
     */
    @Test
    public final void testSliceInt_xyzRamp654_msb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_msb();
        
        Float32Array2D slice = array.slice(3);
        
        assertEquals(2, slice.dimensionality());
        assertEquals(6, slice.size(0));
        assertEquals(5, slice.size(1));
        
        assertEquals(300.0, slice.getValue(0, 0), .01);
        assertEquals(345.0, slice.getValue(5, 4), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedFloat32Array3D#slice(int)}.
     */
    @Test
    public final void testSliceInt_xyzRamp654_lsb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_lsb();
        
        Float32Array2D slice = array.slice(3);
        
        assertEquals(2, slice.dimensionality());
        assertEquals(6, slice.size(0));
        assertEquals(5, slice.size(1));
        
        assertEquals(300.0, slice.getValue(0, 0), .01);
        assertEquals(345.0, slice.getValue(5, 4), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.Float32Array3D#duplicate()}.
     */
    @Test
    public final void testDuplicate_msb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_msb();

        // when duplicated, it should be possible to write into the new array
        Float32Array3D dup = array.duplicate();
        dup.setValue(3, 2, 1, 45.6);
        
        assertEquals(3, dup.dimensionality());
        assertEquals(6, dup.size(0));
        assertEquals(5, dup.size(1));
        assertEquals(4, dup.size(2));
        
        assertEquals(  0.0, dup.getValue(0, 0, 0), .01);
        assertEquals( 45.0, dup.getValue(5, 4, 0), .01);
        assertEquals(345.0, dup.getValue(5, 4, 3), .01);
        assertEquals( 45.6, dup.getValue(3, 2, 1), .01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.Float32Array3D#duplicate()}.
     */
    @Test
    public final void testDuplicate_lsb()
    {
        FileMappedFloat32Array3D array = createFileMapped3D_XYZRamp654_lsb();
        
        // when duplicated, it should be possible to write into the new array
        Float32Array3D dup = array.duplicate();
        dup.setValue(3, 2, 1, 45.6);
        
        assertEquals(3, dup.dimensionality());
        assertEquals(6, dup.size(0));
        assertEquals(5, dup.size(1));
        assertEquals(4, dup.size(2));
        
        assertEquals(  0.0, dup.getValue(0, 0, 0), .01);
        assertEquals( 45.0, dup.getValue(5, 4, 0), .01);
        assertEquals(345.0, dup.getValue(5, 4, 3), .01);
        assertEquals( 45.6, dup.getValue(3, 2, 1), .01);
    }
    
    private FileMappedFloat32Array3D createFileMapped3D_XYZRamp654_msb()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_float32_msb.raw");
        String fileName = path.toString();
        
        return new FileMappedFloat32Array3D(fileName, 0, 6, 5, 4);
    }

    private FileMappedFloat32Array3D createFileMapped3D_XYZRamp654_lsb()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_float32_lsb.raw");
        String fileName = path.toString();
        
        return new FileMappedFloat32Array3D(fileName, 0, ByteOrder.LITTLE_ENDIAN, 6, 5, 4);
    }
}
