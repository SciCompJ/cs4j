package net.sci.image.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

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

//    @Test
//    public void testReadImage_2D_UInt16_lsb_xyRamp() throws IOException
//    {
//        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint16_lsb.raw").getFile();
//        File file = new File(fileName);
//        
//        int[] size = new int[]{4, 3};
//        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
//        
//        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
//        Image image = reader.readImage();
//        
//        assertEquals(2, image.getDimension());
//
//        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
//        assertEquals(4, data.getSize(0));
//        assertEquals(3, data.getSize(1));
//        
//        assertTrue(data instanceof UInt16Array);
//        assertEquals(230.0, data.getValue(3, 2), .01);
//    }


}
