package net.sci.image.morphology;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.type.UInt8;

public class FloodFill2DTest
{

	@Test
	public final void testFloodFillPair_C8()
	{
		int[][] data = new int[][] {
				{ 10, 10, 10, 20, 20, 20, 10, 10, 10, 10, 20, 20, 10, 10, 10 },
				{ 10, 10, 20, 20, 20, 20, 20, 20, 10, 20, 20, 20, 20, 10, 10 },
				{ 10, 20, 10, 10, 10, 10, 20, 20, 10, 20, 10, 10, 20, 20, 10 },
				{ 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 20, 20 },
				{ 20, 20, 10, 20, 10, 10, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
				{ 20, 20, 10, 10, 20, 20, 10, 20, 10, 10, 10, 20, 10, 20, 20 },
				{ 10, 20, 10, 10, 10, 20, 10, 20, 20, 10, 10, 10, 10, 20, 10 },
				{ 10, 20, 10, 20, 20, 20, 10, 20, 20, 20, 20, 20, 20, 20, 10 },
				{ 10, 10, 20, 20, 10, 10, 10, 10, 10, 10, 10, 20, 20, 10, 10 }, };
		int height = data.length;
		int width = data[0].length;
		UInt8Array2D image = UInt8Array2D.create(width, height);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				image.setInt(x, y, data[y][x]);
			}
		}

		// initialize empty result image fill with 255
		UInt8Array2D result = UInt8Array2D.create(width, height);
		result.fill(new UInt8(255));

		// Apply
		FloodFill2D.floodFillInt(image, 7, 4, result, 50, 8);
		// printImage(result);

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (image.getInt(x, y) == 20)
					assertEquals(50, result.getInt(x, y));
				else
					assertEquals(255, result.getInt(x, y));
			}
		}

	}

	@Test
	public final void testFloodFill_EmptySquaresC4()
	{
		int[][] data = new int[][] { 
				{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
				{10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
				{10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
				{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
		};
		int height = data.length;
		int width = data[0].length;
		UInt8Array2D image = UInt8Array2D.create(width, height);
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				image.setInt(x, y, data[y][x]);
			}
		}

		// initialize result
		UInt8Array2D result = UInt8Array2D.create(11, 11);
		result.fill(new UInt8(255));

		// compute flood fill result
		FloodFill2D.floodFillInt(image, 1, 0, result, 50, 4);

		assertEquals(50, result.getInt(0, 0));
		assertEquals(50, result.getInt(10, 0));
		assertEquals(50, result.getInt(0, 10));
		assertEquals(50, result.getInt(10, 10));

		assertEquals(50, result.getInt(5, 3));
		assertEquals(50, result.getInt(5, 7));
		assertEquals(50, result.getInt(3, 5));
		assertEquals(50, result.getInt(7, 5));

		// printImage(result);
	}

}
