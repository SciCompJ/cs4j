package net.sci.image.io;


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.image.Image;

public class TiffImageReaderTest
{
	@Test
	public void testReadImage_Gray8_2D() throws IOException
	{
		String fileName = getClass().getResource("/files/grains.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(256, data.getSize(0));
		assertEquals(256, data.getSize(1));
		
		assertEquals(193, data.getValue(150, 135), .1);
	}

	@Test
	public void testReadImage_Float_2D_grains() throws IOException
	{
		String fileName = getClass().getResource("/files/grains_float.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(256, data.getSize(0));
		assertEquals(256, data.getSize(1));
		
		assertEquals(193, data.getValue(150, 135), .1);
	}

	/**
	 * Uses a test Array<?> that generate some unknown tags.
	 * @throws IOException
	 */
	@Test
	public void testReadImage_Cameraman() throws IOException
	{
		String fileName = getClass().getResource("/files/cameraman.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(512, data.getSize(0));
		assertEquals(512, data.getSize(1));
	}

	@Test
	public void testReadImage_Gray8_3D_Stack() throws IOException
	{
		String fileName = getClass().getResource("/files/mri.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		Array<?> data = image.getData();
		assertEquals(128, data.getSize(0));
		assertEquals(128, data.getSize(1));
		assertEquals( 27, data.getSize(2));
	}

	@Test
	public void testReadImage_RGB8_2D() throws IOException
	{
		String fileName = getClass().getResource("/files/lena_color_512.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());
		assertEquals(512, image.getSize(0));
		assertEquals(512, image.getSize(1));
	}

	@Test
	public void testReadImage_UInt16_M51() throws IOException
	{
		String fileName = getClass().getResource("/files/m51.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		assertEquals(2, image.getDimension());
		assertEquals(320, image.getSize(0));
		assertEquals(510, image.getSize(1));
		
		Array<?> array = image.getData();
		assertEquals(218, array.getValue(new int[]{0, 0}), .1);
		assertEquals(275, array.getValue(new int[]{5, 0}), .1);
		assertEquals(10106, array.getValue(new int[]{80, 347}), .1);
		
		// try also with Array2D interface
		UInt16Array2D array2d = (UInt16Array2D) image.getData();
		assertEquals(218, array2d.getValue(0, 0), .1);
		assertEquals(275, array2d.getValue(5, 0), .1);
		assertEquals(10106, array2d.getValue(80, 347), .1);
		
	}
}
