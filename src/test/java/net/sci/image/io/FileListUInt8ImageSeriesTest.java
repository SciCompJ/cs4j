/**
 * 
 */
package net.sci.image.io;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

/**
 * 
 */
public class FileListUInt8ImageSeriesTest
{
    /**
     * Test method for {@link net.sci.image.io.FileListUInt8ImageSeries#getByte(int, int, int)}.
     */
    @Test
    public final void testGetByte()
    {
        File dirFile = new File(getClass().getResource("/images/slices").getFile());
//        System.out.println(dirFile.isDirectory());
        
        File[] fileList = dirFile.listFiles(file -> file.getName().endsWith(".tif"));
//        System.out.println(fileList.length);
        
        FileListUInt8ImageSeries array = new FileListUInt8ImageSeries(fileList, 512, 512);
        
        assertEquals(172, array.getInt(200, 89, 0));
        assertEquals( 81, array.getInt(150, 100, 24));
    }
    
}
