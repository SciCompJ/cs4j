package net.sci.image.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import org.junit.Test;

import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float64Array;
import net.sci.array.scalar.Int16Array;
import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.Image;

public class RawImageReaderTest
{
    @Test
    public void testReadImage_2D_UInt8_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT8;
        
        RawImageReader reader = new RawImageReader(file, size, type);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof UInt8Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_UInt16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof UInt16Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_UInt16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_uint16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof UInt16Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Int16_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Int16Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Int16_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Int16Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Int32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Int32Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Int32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_int32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Int32Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }


    @Test
    public void testReadImage_2D_Float32_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Float32Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Float32_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Float32Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }


    @Test
    public void testReadImage_2D_Float64_lsb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float64_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT64;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Float64Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_2D_Float64_msb_xyRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyRamp_4x3_float64_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT64;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
        assertEquals(4, data.size(0));
        assertEquals(3, data.size(1));
        
        assertTrue(data instanceof Float64Array);
        assertEquals(230.0, data.getValue(3, 2), .01);
    }

    @Test
    public void testReadImage_3D_UInt8_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint8.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT8;
        
        RawImageReader reader = new RawImageReader(file, size, type);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof UInt8Array);
        assertEquals(  0.0, data.getValue(0, 0, 0), .01);
        assertEquals(  4.0, data.getValue(4, 0, 0), .01);
        assertEquals( 30.0, data.getValue(0, 3, 0), .01);
        assertEquals(200.0, data.getValue(0, 0, 2), .01);
        assertEquals( 34.0, data.getValue(4, 3, 0), .01);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_UInt16_lsb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof UInt16Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_UInt16_msb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_uint16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof UInt16Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Int16_lsb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int16_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Int16Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Int16_msb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int16_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT16;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Int16Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Int32_lsb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Int32Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Int32_msb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_int32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.INT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Int32Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Float32_lsb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float32_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Float32Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Float32_msb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float32_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT32;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Float32Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    

    @Test
    public void testReadImage_3D_Float64_lsb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float64_lsb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT64;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.LITTLE_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Float64Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }

    @Test
    public void testReadImage_3D_Float64_msb_xyzRamp() throws IOException
    {
        String fileName = getClass().getResource("/images/raw/xyzRamp_5x4x3_float64_msb.raw").getFile();
        File file = new File(fileName);
        
        int[] size = new int[]{5, 4, 3};
        RawImageReader.DataType type = RawImageReader.DataType.FLOAT64;
        
        RawImageReader reader = new RawImageReader(file, size, type, ByteOrder.BIG_ENDIAN);
        Image image = reader.readImage();
        
        assertEquals(3, image.getDimension());

        ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
        assertEquals(5, data.size(0));
        assertEquals(4, data.size(1));
        assertEquals(3, data.size(2));
        
        assertTrue(data instanceof Float64Array);
        assertEquals(234.0, data.getValue(4, 3, 2), .01);
    }
}
