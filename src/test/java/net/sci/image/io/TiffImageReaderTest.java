package net.sci.image.io;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import net.sci.array.Array;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.Image;

import org.junit.Test;

public class TiffImageReaderTest
{
	@Test
	public void testReadImage_Gray8_2D() throws IOException
	{
		File file = new File("files/grains.tif");
		
		TiffImageReader reader = new TiffImageReader(file);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(256, data.getSize(0));
		assertEquals(256, data.getSize(1));
	}

	/**
	 * Uses a test Array<?> that generate some unknown tags.
	 * @throws IOException
	 */
	@Test
	public void testReadImage_Cameraman() throws IOException
	{
		File file = new File("files/cameraman.tif");
		
		TiffImageReader reader = new TiffImageReader(file);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(512, data.getSize(0));
		assertEquals(512, data.getSize(1));
	}

	@Test
	public void testReadImage_Gray8_3D_Stack() throws IOException
	{
		File file = new File("files/mri.tif");
		
		TiffImageReader reader = new TiffImageReader(file);
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
		File file = new File("files/lena_color_512.tif");
		
		TiffImageReader reader = new TiffImageReader(file);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());
		assertEquals(512, image.getSize(0));
		assertEquals(512, image.getSize(1));
	}

}
