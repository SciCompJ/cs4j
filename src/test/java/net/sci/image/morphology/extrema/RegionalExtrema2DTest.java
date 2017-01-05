package net.sci.image.morphology.extrema;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.type.Boolean;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.MinimaAndMaxima;

public class RegionalExtrema2DTest
{
	@Test
	public void testProcessRegionalMax_UInt8_C4()
	{
		int[] data = new int[] {
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10
		};
		UInt8Array2D image = UInt8Array2D.create(11, 11);
		
		int i = 0;
		for (int y = 0; y < 11; y++)
		{
			for (int x = 0; x < 11; x++)
			{
				image.setInt(x, y, data[i++]);
			}
		}
		
		RegionalExtrema2D algo = new RegionalExtrema2D(MinimaAndMaxima.Type.MAXIMA, Connectivity2D.C4);
		BooleanArray2D maxima = (BooleanArray2D) algo.process(image);
		
		assertEquals(Boolean.FALSE, maxima.get(0, 0));
		assertEquals(Boolean.TRUE, maxima.get(1, 1));
		assertEquals(Boolean.TRUE, maxima.get(9, 1));
		assertEquals(Boolean.TRUE, maxima.get(5, 5));
		assertEquals(Boolean.FALSE, maxima.get(10, 10));	
	}

	@Test
	public void testProcessRegionalMax_UInt8_C8()
	{
		int[] data = new int[] {
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10,
				10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10
		};
		UInt8Array2D image = UInt8Array2D.create(11, 11);
		
		int i = 0;
		for (int y = 0; y < 11; y++)
		{
			for (int x = 0; x < 11; x++)
			{
				image.setInt(x, y, data[i++]);
			}
		}
		
		RegionalExtrema2D algo = new RegionalExtrema2D(MinimaAndMaxima.Type.MAXIMA, Connectivity2D.C8);
		BooleanArray2D maxima = (BooleanArray2D) algo.process(image);
		
		assertEquals(Boolean.FALSE, maxima.get(0, 0));
		assertEquals(Boolean.FALSE, maxima.get(1, 1));
		assertEquals(Boolean.FALSE, maxima.get(9, 1));
		assertEquals(Boolean.TRUE, maxima.get(5, 5));
		assertEquals(Boolean.FALSE, maxima.get(10, 10));	
	}

	@Test
	public void testProcessRegionalMin_UInt8_C4()
	{
		int[][] data = new int[][]{
			{50, 50, 50, 50, 50},
			{50, 10, 50, 50, 50},
			{50, 50, 20, 50, 50},
			{50, 50, 50, 10, 50},
			{50, 50, 20, 10, 50},
			{50, 50, 50, 50, 50},
		};

		UInt8Array2D image = UInt8Array2D.create(5, 6);
		for (int y = 0; y < 6; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				image.setInt(x, y, data[y][x]);
			}
		}
		
		RegionalExtrema2D algo = new RegionalExtrema2D(MinimaAndMaxima.Type.MINIMA, Connectivity2D.C4);
		BooleanArray2D maxima = (BooleanArray2D) algo.process(image);
		
		assertEquals(Boolean.FALSE, maxima.get(0, 0));
		assertEquals(Boolean.TRUE, maxima.get(1, 1));
		assertEquals(Boolean.TRUE, maxima.get(2, 2));
		assertEquals(Boolean.TRUE, maxima.get(3, 3));
		assertEquals(Boolean.FALSE, maxima.get(2, 4));
	}
	
	@Test
	public void testProcessRegionalMin_UInt8_C8()
	{
		int[][] data = new int[][]{
			{50, 50, 50, 50, 50},
			{50, 10, 50, 50, 50},
			{50, 50, 20, 50, 50},
			{50, 50, 50, 10, 50},
			{50, 50, 20, 10, 50},
			{50, 50, 50, 50, 50},
		};

		UInt8Array2D image = UInt8Array2D.create(5, 6);
		for (int y = 0; y < 6; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				image.setInt(x, y, data[y][x]);
			}
		}
		
		RegionalExtrema2D algo = new RegionalExtrema2D(MinimaAndMaxima.Type.MINIMA, Connectivity2D.C8);
		BooleanArray2D maxima = (BooleanArray2D) algo.process(image);
		
		assertEquals(Boolean.FALSE, maxima.get(0, 0));
		assertEquals(Boolean.TRUE, maxima.get(1, 1));
		assertEquals(Boolean.FALSE, maxima.get(2, 2));
		assertEquals(Boolean.TRUE, maxima.get(3, 3));
		assertEquals(Boolean.FALSE, maxima.get(2, 4));
	}

}
