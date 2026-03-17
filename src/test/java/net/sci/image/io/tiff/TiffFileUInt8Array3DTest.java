/**
 * 
 */
package net.sci.image.io.tiff;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import net.sci.image.io.TiffImageReader;

/**
 * 
 */
public class TiffFileUInt8Array3DTest
{
    /**
     * Reads a 3D Tiff image as saved by Matlab.
     * 
     * @throws IOException
     */
    @Test
    public void test_UInt8_3D_uncompressed_mri_matlab() throws IOException
    {
        String fileName = getClass().getResource("/images/matlab/mri_uint8_uncompressed.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Collection<ImageFileDirectory> ifdList = reader.getImageFileDirectories();
        
        TiffFileUInt8Array3D array = TiffFileUInt8Array3D.open(fileName, ifdList);
        
        assertEquals(3, array.dimensionality());
        assertEquals(128, array.size(0));
        assertEquals(128, array.size(1));
        assertEquals( 27, array.size(2));
        
        // check values at few arbitrary positions
        assertEquals(61, array.getInt(50, 50, 10));
        assertEquals(52, array.getInt(60, 40, 8));
    }
    
    /**
     * Reads a 3D Tiff image as saved by Matlab.
     * 
     * @throws IOException
     */
    @Test
    public void test_UInt8_3D_packbits_mri_matlab() throws IOException
    {
        String fileName = getClass().getResource("/images/matlab/mri_uint8_packbits.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Collection<ImageFileDirectory> ifdList = reader.getImageFileDirectories();
        
        TiffFileUInt8Array3D array = TiffFileUInt8Array3D.open(fileName, ifdList);
        
        assertEquals(3, array.dimensionality());
        assertEquals(128, array.size(0));
        assertEquals(128, array.size(1));
        assertEquals( 27, array.size(2));
        
        // check values at few arbitrary positions
        assertEquals(61, array.getInt(50, 50, 10));
        assertEquals(52, array.getInt(60, 40, 8));
    }
}
