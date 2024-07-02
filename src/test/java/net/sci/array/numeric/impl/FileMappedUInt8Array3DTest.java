/**
 * 
 */
package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class FileMappedUInt8Array3DTest
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
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_5x4x3_uint8.raw");
        String fileName = path.toString();
        
        RandomAccessFile raf = new RandomAccessFile(fileName, "r"); 
        
        byte[] buffer = new byte[20];
        raf.read(buffer);
        
        UInt8Array2D slice0 = new BufferedUInt8Array2D(5, 4, buffer);
        
        assertEquals(  0.0, slice0.getValue(0, 0), .01);
        assertEquals( 34.0, slice0.getValue(4, 3), .01);
        
        byte[] buffer2 = new byte[20];
        raf.seek(40L);
        raf.read(buffer2);
        
        UInt8Array2D slice2 = new BufferedUInt8Array2D(5, 4, buffer2);
        assertEquals(200.0, slice2.getValue(0, 0), .01);
        assertEquals(234.0, slice2.getValue(4, 3), .01);
        
        raf.close();
    }

    @Test
    public void testGetByte_xyzRamp() throws IOException
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_5x4x3_uint8.raw");
        String fileName = path.toString();

        FileMappedUInt8Array3D array = new FileMappedUInt8Array3D(fileName, 0L, 5, 4, 3);
        
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertTrue(array instanceof UInt8Array);
        assertEquals(  0.0, array.getValue(0, 0, 0), .01);
        assertEquals(234.0, array.getValue(4, 3, 2), .01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt8Array3D#slice(int)}.
     */
    @Test
    public void testSliceInt_xyzRamp()
    {
        Path path = Paths.get("src", "test", "resources", "images", "raw", "xyzRamp_5x4x3_uint8.raw");
        String fileName = path.toString();
        
        FileMappedUInt8Array3D array = new FileMappedUInt8Array3D(fileName, 0L, 5, 4, 3);
        UInt8Array2D slice2 = array.slice(2);
        
        assertEquals(200.0, slice2.getValue(0, 0), .01);
        assertEquals(234.0, slice2.getValue(4, 3), .01);
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.impl.FileMappedUInt8Array3D#slice(int)}.
     */
    @Test
    public void testSliceInt_Rat60mhd()
    {
        Path path = Paths.get("src", "test", "resources", "images", "mhd", "rat60_LipNor552.raw");
        String fileName = path.toString();
        
        FileMappedUInt8Array3D array = new FileMappedUInt8Array3D(fileName, 0L, 100, 80, 160);
        UInt8Array2D slice2 = array.slice(80);
        
        assertEquals(100, slice2.size(0));
        assertEquals( 80, slice2.size(1));
    }
}
