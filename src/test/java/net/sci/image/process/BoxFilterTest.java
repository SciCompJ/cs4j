package net.sci.image.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.data.scalar3d.Float32Array3D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;

public class BoxFilterTest
{

	@Test
	public void testProcessScalar2D()
	{
		UInt8Array2D array = UInt8Array2D.create(6, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 6; x++)
			{
				array.setInt(x, y, 10);
			}
		}
		
		int[] radiusList = new int[]{1,1};
		BoxFilter filter = new BoxFilter(radiusList);
		
		Array<?> result = filter.process(array);
		
		assertTrue(result instanceof ScalarArray);
		assertEquals(2, result.dimensionality());
	}

	@Test
	public void testProcessScalar3D()
	{
		UInt8Array3D array = UInt8Array3D.create(6, 5, 4);
		for (int z = 0; z < 4; z++)
		{
			for (int y = 0; y < 5; y++)
			{
				for (int x = 0; x < 6; x++)
				{
					array.setInt(x, y, z, 10);
				}
			}
		}
		
		int[] radiusList = new int[]{2, 2, 1};
		BoxFilter filter = new BoxFilter(radiusList);
		
		Array<?> result = filter.process(array);
		
		assertTrue(result instanceof ScalarArray);
		assertEquals(3, result.dimensionality());
	}

	@Test
	public void compareTiming_Scalar3D() throws IOException
	{
		String fileName = getClass().getResource("/files/mri.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		ScalarArray3D<?> array = (ScalarArray3D<?>) image.getData();
		assertEquals(128, array.getSize(0));
		assertEquals(128, array.getSize(1));
		assertEquals( 27, array.getSize(2));

		BoxFilter filter = new BoxFilter(new int[]{3, 3, 1});
		long t0, t1;
		double dt1, dt2;
		
		System.out.println("Start comparing timing");

		Float32Array3D target1 = Float32Array3D.create(128, 128, 27);
		t0 = System.nanoTime();
		filter.processScalar3d(array, target1);
		t1 = System.nanoTime();
		dt1 = (t1 - t0) / 1000000;
		System.out.println("Scalar 3D, elapsed time: " + dt1);
		
		t0 = System.nanoTime();
		filter.processScalar(array, target1);
		t1 = System.nanoTime();
		dt2 = (t1 - t0) / 1000000;
		System.out.println("Scalar,    elapsed time: " + dt2);
		
		System.out.println("Timing ratio: " + String.format("%7.3f", dt2 / dt1));
	}
	
}
