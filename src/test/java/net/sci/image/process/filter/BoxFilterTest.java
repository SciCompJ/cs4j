package net.sci.image.process.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.color.RGB8Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.array.type.RGB8;
import net.sci.image.process.filter.BoxFilter;

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
	public void testProcessColor2D()
	{
		RGB8Array2D array = RGB8Array2D.create(6, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 6; x++)
			{
				array.set(x, y, new RGB8(x * 5, y * 5, 0));
			}
		}
		
		int[] radiusList = new int[]{1,1};
		BoxFilter filter = new BoxFilter(radiusList);
		
		Array<?> result = filter.process(array);
		
		assertTrue(result instanceof RGB8Array2D);
		assertEquals(2, result.dimensionality());
	}

}