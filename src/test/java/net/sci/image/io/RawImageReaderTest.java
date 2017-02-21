package net.sci.image.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.data.Int16Array;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.Image;

public class RawImageReaderTest
{
	@Test
	public void testReadImage_2D_UInt8() throws IOException
	{
		String fileName = getClass().getResource("/files/mhd/img_15x10_gray8.raw").getFile();
		File file = new File(fileName);
		
		int[] size = new int[]{15, 10};
		RawImageReader.DataType type = RawImageReader.DataType.UINT8;
		
		RawImageReader reader = new RawImageReader(file, size, type);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(15, data.getSize(0));
		assertEquals(10, data.getSize(1));
	}

	@Test
	public void testReadImage_3D_UInt16() throws IOException
	{
		String fileName = getClass().getResource("/files/mhd/img_10x15x20_gray16.raw").getFile();
        File file = new File(fileName);
		
        int[] size = new int[]{10, 15, 20};
        RawImageReader.DataType type = RawImageReader.DataType.UINT16;
        
        RawImageReader reader = new RawImageReader(file, size, type);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
		assertEquals(10, data.getSize(0));
		assertEquals(15, data.getSize(1));
		assertEquals(20, data.getSize(2));
		
		assertTrue(data instanceof UInt16Array);
	}

	@Test
	public void testReadImage_3D_Int16() throws IOException
	{
		String fileName = getClass().getResource("/files/mhd/img_10x15x20_int16.raw").getFile();
        File file = new File(fileName);
		
        int[] size = new int[]{10, 15, 20};
        RawImageReader.DataType type = RawImageReader.DataType.INT16;
        
        RawImageReader reader = new RawImageReader(file, size, type);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
		assertEquals(10, data.getSize(0));
		assertEquals(15, data.getSize(1));
		assertEquals(20, data.getSize(2));
		
		assertTrue(data instanceof Int16Array);
	}

	@Test
	public void testReadImage_3D_UInt8_RatBrain() throws IOException
	{
		String fileName = getClass().getResource("/files/mhd/rat60_LipNor552.raw").getFile();
        File file = new File(fileName);
		
        int[] size = new int[]{100, 80, 160};
        RawImageReader.DataType type = RawImageReader.DataType.UINT8;
        
        RawImageReader reader = new RawImageReader(file, size, type);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		Array<?> data = (Array<?>) image.getData();
		assertEquals(100, data.getSize(0));
		assertEquals(80, data.getSize(1));
		assertEquals(160, data.getSize(2));
	}

}
