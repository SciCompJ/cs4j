/**
 * 
 */
package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt16Array3D;

/**
 * @author dlegland
 *
 */
public class FileMappedUInt16Array3DTest
{
    /**
     * Check validity of input test file...
     * 
     * @throws IOException
     *             in case of I/O problem.
     */
    @Test
    public void test_checkData_xyzRamp() throws IOException
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_UInt16_msb.raw");
        String fileName = path.toString();
        
        RandomAccessFile raf = new RandomAccessFile(fileName, "r"); 
        
        // convert to linear float array
        short[] buffer = new short[120];
        for (int i = 0; i < 120; i++)
        {
            buffer[i] = raf.readShort();
        }
        raf.close();
        
        // convert to 3D array of UInt16
        UInt16Array3D array = new BufferedUInt16Array3D(6, 5, 4, buffer);
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
        assertEquals(345.0, array.getValue(5, 4, 3), .01);
    }
    
    
    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt16Array3D#slice(int)}.
     */
    @Test
    public final void testSliceInt_slice0()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_UInt16_msb.raw");
        String fileName = path.toString();

        FileMappedUInt16Array3D array = new FileMappedUInt16Array3D(fileName, 0, 6, 5, 4);
        UInt16Array2D slice = array.slice(0);
        
        assertEquals(2, slice.dimensionality());
        assertEquals(6, slice.size(0));
        assertEquals(5, slice.size(1));
        
        assertEquals(  0.0, slice.getValue(0, 0), .01);
        assertEquals( 45.0, slice.getValue(5, 4), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt16Array3D#slice(int)}.
     */
    @Test
    public final void testSliceInt_slice2()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_UInt16_msb.raw");
        String fileName = path.toString();

        FileMappedUInt16Array3D array = new FileMappedUInt16Array3D(fileName, 0, 6, 5, 4);
        UInt16Array2D slice = array.slice(3);
        
        assertEquals(2, slice.dimensionality());
        assertEquals(6, slice.size(0));
        assertEquals(5, slice.size(1));
        
        assertEquals(300.0, slice.getValue(0, 0), .01);
        assertEquals(345.0, slice.getValue(5, 4), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt16Array3D#getValue(int[])}.
     */
    @Test
    public final void testGetValue_slice0()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_UInt16_msb.raw");
        String fileName = path.toString();

        FileMappedUInt16Array3D array = new FileMappedUInt16Array3D(fileName, 0, 6, 5, 4);
        
        assertEquals(3, array.dimensionality());
        assertEquals(6, array.size(0));
        assertEquals(5, array.size(1));
        assertEquals(4, array.size(2));
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt16Array3D#getValue(int[])}.
     */
    @Test
    public final void testGetValue()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_6x5x4_UInt16_msb.raw");
        String fileName = path.toString();

        FileMappedUInt16Array3D array = new FileMappedUInt16Array3D(fileName, 0, 6, 5, 4);
        
        assertEquals(3, array.dimensionality());
        assertEquals(6, array.size(0));
        assertEquals(5, array.size(1));
        assertEquals(4, array.size(2));
        
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals( 45.0, array.getValue(5, 4, 0), .01);
        assertEquals(345.0, array.getValue(5, 4, 3), .01);
    }
}
