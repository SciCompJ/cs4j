package net.sci.image.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import net.sci.array.scalar.BufferedUInt16Array3D;
import net.sci.array.scalar.UInt16Array3D;

import org.junit.Test;

public class ImageBinaryDataReaderTest
{
    @Test
    public void testReadImage_2D_UInt8_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(new RandomAccessFile(file, "r"));
        int numel = 12;
        byte[] array = new byte[numel];
        reader.readByteArray(array, 0, numel);
        reader.close();
        
        assertEquals(230.0, array[numel - 1] & 0x00FF, .01);
    }

    @Test
    public void testReadImage_2D_UInt16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        ImageBinaryDataReader reader = new ImageBinaryDataReader(raf, ByteOrder.LITTLE_ENDIAN);
        int numel = 12;
        short[] array = new short[numel];
        reader.readShortArray(array, 0, numel);
        reader.close();
        
        assertEquals(230.0, array[numel - 1] & 0x00FFFF, .01);
    }

    @Test
    public void testReadMHDImage_3D_UInt16() throws IOException
    {
        String fileName = getClass().getResource("/files/mhd/img_10x15x20_gray16.raw").getFile();
        File file = new File(fileName);
        
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        ImageBinaryDataReader reader = new ImageBinaryDataReader(raf, ByteOrder.LITTLE_ENDIAN);
        int numel = 20*15*10;
        short[] buffer = new short[numel];
        reader.readShortArray(buffer, 0, numel);
        reader.close();
        
        UInt16Array3D array = new BufferedUInt16Array3D(10, 15, 20, buffer);
        assertEquals(250.0, array.getValue(4, 4, 10), .01);
        
        assertEquals(3, array.dimensionality());

        assertEquals(10, array.size(0));
        assertEquals(15, array.size(1));
        assertEquals(20, array.size(2));
        
    }
}
