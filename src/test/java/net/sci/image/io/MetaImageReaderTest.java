package net.sci.image.io;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.scalar.Int16Array;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt16Array;
import net.sci.image.Image;

public class MetaImageReaderTest
{
	@Test
	public void testReadImage_2D_UInt8() throws IOException
	{
		String fileName = getClass().getResource("/images/mhd/img_15x10_gray8.mhd").getFile();
		
		MetaImageReader reader = new MetaImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(15, data.size(0));
		assertEquals(10, data.size(1));
	}

	@Test
	public void testReadImage_3D_UInt16() throws IOException
	{
		String fileName = getClass().getResource("/images/mhd/img_10x15x20_gray16.mhd").getFile();
		
		MetaImageReader reader = new MetaImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
		assertEquals(10, data.size(0));
		assertEquals(15, data.size(1));
		assertEquals(20, data.size(2));
		
		assertTrue(data instanceof UInt16Array);
	}

	@Test
	public void testReadImage_3D_Int16() throws IOException
	{
		String fileName = getClass().getResource("/images/mhd/img_10x15x20_int16.mhd").getFile();
		
		MetaImageReader reader = new MetaImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> data = (ScalarArray3D<?>) image.getData();
		assertEquals(10, data.size(0));
		assertEquals(15, data.size(1));
		assertEquals(20, data.size(2));
		
		assertTrue(data instanceof Int16Array);
	}

	@Test
	public void testReadImage_3D_UInt8_RatBrain() throws IOException
	{
		String fileName = getClass().getResource("/images/mhd/rat60_LipNor552.mhd").getFile();
		
		MetaImageReader reader = new MetaImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		Array<?> data = (Array<?>) image.getData();
		assertEquals(100, data.size(0));
		assertEquals(80, data.size(1));
		assertEquals(160, data.size(2));
	}

}
