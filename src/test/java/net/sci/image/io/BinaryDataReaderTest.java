package net.sci.image.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Test;

public class BinaryDataReaderTest
{
    @Test
    public void testReadImage_2D_UInt8_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file);
        byte[] buffer = new byte[numel];
        int nRead = reader.readByteArray(buffer);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1] & 0x00FF, .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_UInt16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        short[] buffer = new short[numel];
        int nRead = reader.readShortArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1] & 0x00FFFF, .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_UInt16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_uint16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        short[] buffer = new short[numel];
        int nRead = reader.readShortArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1] & 0x00FFFF, .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Int16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_int16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        short[] buffer = new short[numel];
        int nRead = reader.readShortArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1] & 0x00FFFF, .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Int16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_int16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        short[] buffer = new short[numel];
        int nRead = reader.readShortArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1] & 0x00FFFF, .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Int32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_int32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        int[] buffer = new int[numel];
        int nRead = reader.readIntArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Int32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_int32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        int[] buffer = new int[numel];
        int nRead = reader.readIntArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }


    @Test
    public void testReadImage_2D_Float32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_float32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        float[] buffer = new float[numel];
        int nRead = reader.readFloatArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Float32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_float32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        float[] buffer = new float[numel];
        int nRead = reader.readFloatArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }


    @Test
    public void testReadImage_2D_Float64_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_float64_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        double[] buffer = new double[numel];
        int nRead = reader.readDoubleArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }

    @Test
    public void testReadImage_2D_Float64_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/files/raw/xyRamp_4x3_float64_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        int numel = prod(size);
        
        BinaryDataReader reader = new BinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        double[] buffer = new double[numel];
        int nRead = reader.readDoubleArray(buffer, 0, numel);
        
        assertEquals(numel, nRead);
        assertEquals(230.0, buffer[numel - 1], .01);
        
        reader.close();
    }
    
    private static final int prod(int[] dims)
    {
        int numel = 1;
        for (int n : dims)
        {
            numel *= n;
        }
        return numel;
    }
}
