package net.sci.image.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;

import org.junit.Test;

public class ImageBinaryDataReaderTest
{
    @Test
    public void testReadImage_2D_UInt8_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file);
        UInt8Array array = reader.readUInt8Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }
    
    @Test
    public void testReadImage_3D_UInt8_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file);
        UInt8Array array = reader.readUInt8Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }
    

    @Test
    public void testReadImage_2D_UInt16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        UInt16Array array = reader.readUInt16Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }


    @Test
    public void testReadImage_2D_UInt16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint16_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        UInt16Array array = reader.readUInt16Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_3D_UInt16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        UInt16Array array = reader.readUInt16Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_3D_UInt16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint16_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        UInt16Array array = reader.readUInt16Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_2D_Int16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int16_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Int16Array array = reader.readInt16Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }


    @Test
    public void testReadImage_2D_Int16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int16_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Int16Array array = reader.readInt16Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_3D_Int16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int16_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Int16Array array = reader.readInt16Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_3D_Int16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int16_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Int16Array array = reader.readInt16Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }

    
    @Test
    public void testReadImage_2D_Int32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int32_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Int32Array array = reader.readInt32Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_2D_Int32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int32_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Int32Array array = reader.readInt32Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_3D_Int32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int32_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Int32Array array = reader.readInt32Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_3D_Int32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int32_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Int32Array array = reader.readInt32Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_2D_Float32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float32_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Float32Array array = reader.readFloat32Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_2D_Float32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float32_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Float32Array array = reader.readFloat32Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_3D_Float32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float32_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Float32Array array = reader.readFloat32Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_3D_Float32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float32_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Float32Array array = reader.readFloat32Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_2D_Float64_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float64_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Float64Array array = reader.readFloat64Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_2D_Float64_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float64_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Float64Array array = reader.readFloat64Array(new int[]{4, 3});
        reader.close();

        assertEquals(2, array.dimensionality());
        assertEquals(4, array.size(0));
        assertEquals(3, array.size(1));
        
        assertEquals(230.0, array.getValue(new int[]{3, 2}), .01);
    }

    @Test
    public void testReadImage_3D_Float64_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float64_lsb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.LITTLE_ENDIAN);
        Float64Array array = reader.readFloat64Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }


    @Test
    public void testReadImage_3D_Float64_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float64_msb.raw").getFile();
        File file = new File(fileName);
        
        ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ByteOrder.BIG_ENDIAN);
        Float64Array array = reader.readFloat64Array(new int[]{5, 4, 3});
        reader.close();

        assertEquals(3, array.dimensionality());
        assertEquals(5, array.size(0));
        assertEquals(4, array.size(1));
        assertEquals(3, array.size(2));
        
        assertEquals(234.0, array.getValue(new int[]{4, 3, 2}), .01);
    }
}
