package net.sci.image.morphology.reconstruct;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.MorphologicalReconstruction;

public class MorphologicalReconstruction2DHybridTest
{

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByDilation()}.
	 */
	@Test
	public void testReconstructByDilation_C4()
	{
		int BG = 0;
		int FG = 255;
		int[][] data = new int[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		UInt8Array2D mask = UInt8Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(data[y][x], x, y);
			}
		}
		UInt8Array2D marker = UInt8Array2D.create(sizeX, sizeY);
		marker.setInt(255, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C4);

		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
		// printImage(result);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(255, result.getInt(2, 8));
		assertEquals(255, result.getInt(8, 8));
		assertEquals(255, result.getInt(8, 5));
		assertEquals(255, result.getInt(14, 8));
	}

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByDilation()}.
	 */
	@Test
	public void testReconstructByDilation_C8()
	{
		int BG = 0;
		int FG = 255;
		int[][] data = new int[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, BG, BG, FG, FG, FG, FG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, BG, BG, FG, FG, FG, FG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, FG, FG, FG, FG, BG, BG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, FG, FG, FG, FG, BG, BG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		UInt8Array2D mask = UInt8Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(data[y][x], x, y);
			}
		}
		UInt8Array2D marker = UInt8Array2D.create(sizeX, sizeY);
		marker.setInt(255, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C8);
		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(255, result.getInt(2, 6));
		assertEquals(255, result.getInt(4, 8));
		assertEquals(255, result.getInt(8, 4));
		assertEquals(255, result.getInt(10, 2));
		assertEquals(255, result.getInt(14, 8));
	}

	@Test
	public void testReconstructByDilationGrayscaleC4()
	{
		// size of images
		int sizeX = 16;
		int sizeY = 10;

		UInt8Array2D mask = UInt8Array2D.create(16, 10);
		UInt8Array2D marker = UInt8Array2D.create(16, 10);
		UInt8Array2D expected = UInt8Array2D.create(16, 10);

		// initialize mask, marker, and expected images
		int[] maskProfile = { 10, 10, 40, 40, 40, 40, 20, 20, 30, 30, 10, 10, 30, 30, 0, 0 };
		int[] markerProfile = { 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] expectedProfile = { 10, 10, 30, 30, 30, 30, 20, 20, 20, 20, 10, 10, 10, 10, 0, 0 };
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(maskProfile[x], x, y);
				marker.setInt(markerProfile[x], x, y);
				expected.setInt(expectedProfile[x], x, y);
			}
		}

		// Compute geodesic reconstruction by dilation
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C4);
		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
		// printImage(result);

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				assertEquals(expectedProfile[x], result.getInt(x, y));
			}
		}

	}

	@Test
	public void testReconstructByDilationGrayscaleC8()
	{
		// size of images
		int sizeX = 16;
		int sizeY = 10;

		UInt8Array2D mask = UInt8Array2D.create(16, 10);
		UInt8Array2D marker = UInt8Array2D.create(16, 10);
		UInt8Array2D expected = UInt8Array2D.create(16, 10);

		// initialize mask, marker, and expected images
		int[] maskProfile = { 10, 10, 40, 40, 40, 40, 20, 20, 30, 30, 10, 10, 30, 30, 0, 0 };
		int[] markerProfile = { 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] expectedProfile = { 10, 10, 30, 30, 30, 30, 20, 20, 20, 20, 10, 10, 10, 10, 0, 0 };
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(maskProfile[x], x, y);
				marker.setInt(markerProfile[x], x, y);
				expected.setInt(expectedProfile[x], x, y);
			}
		}

		// Compute geodesic reconstruction by dilation
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C8);
		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);
		// printImage(result);

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				assertEquals(expectedProfile[x], result.getInt(x, y));
			}
		}

	}

	@Test
	public void testReconstructByDilationFloatC4()
	{
		// size of images
		int sizeX = 16;
		int sizeY = 10;

		Float32Array2D mask = Float32Array2D.create(16, 10);
		Float32Array2D marker = Float32Array2D.create(16, 10);
		Float32Array2D expected = Float32Array2D.create(16, 10);

		// initialize mask, marker, and expected images
		int[] maskProfile = { 10, 10, 40, 40, 40, 40, 20, 20, 30, 30, 10, 10, 30, 30, 0, 0 };
		int[] markerProfile = { 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] expectedProfile = { 10, 10, 30, 30, 30, 30, 20, 20, 20, 20, 10, 10, 10, 10, 0, 0 };
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setValue(maskProfile[x], x, y);
				marker.setValue(markerProfile[x], x, y);
				expected.setValue(expectedProfile[x], x, y);
			}
		}

		// Compute geodesic reconstruction by dilation
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C4);
		Float32Array2D result = (Float32Array2D) algo.process(marker, mask);
		// printImage(result);

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				assertEquals(expectedProfile[x], result.getValue(x, y), .01);
			}
		}
	}

	@Test
	public void testReconstructByDilationFloatC8()
	{
		// size of images
		int sizeX = 16;
		int sizeY = 10;

		Float32Array2D mask = Float32Array2D.create(16, 10);
		Float32Array2D marker = Float32Array2D.create(16, 10);
		Float32Array2D expected = Float32Array2D.create(16, 10);

		// initialize mask, marker, and expected images
		int[] maskProfile = { 10, 10, 40, 40, 40, 40, 20, 20, 30, 30, 10, 10, 30, 30, 0, 0 };
		int[] markerProfile = { 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] expectedProfile = { 10, 10, 30, 30, 30, 30, 20, 20, 20, 20, 10, 10, 10, 10, 0, 0 };
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setValue(maskProfile[x], x, y);
				marker.setValue(markerProfile[x], x, y);
				expected.setValue(expectedProfile[x], x, y);
			}
		}

		// Compute geodesic reconstruction by dilation
		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_DILATION, Connectivity2D.C8);
		Float32Array2D result = (Float32Array2D) algo.process(marker, mask);
		// printImage(result);

		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				assertEquals(expectedProfile[x], result.getValue(x, y), .01);
			}
		}

	}

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByErosion()}.
	 */
	@Test
	public void testReconstructByErosion_C4()
	{
		int BG = 0;
		int FG = 255;
		int[][] data = new int[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		UInt8Array2D mask = UInt8Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(data[y][x], x, y);
			}
		}
		mask = invert(mask);

		UInt8Array2D marker = UInt8Array2D.create(sizeX, sizeY);
		marker.fillValue(255);
		marker.setInt(0, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C4);
		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(0, result.getInt(2, 8));
		assertEquals(0, result.getInt(8, 8));
		assertEquals(0, result.getInt(8, 5));
		assertEquals(0, result.getInt(14, 8));
		assertEquals(FG, result.getInt(15, 9));
		assertEquals(FG, result.getInt(0, 0));
		assertEquals(FG, result.getInt(5, 3));
		assertEquals(FG, result.getInt(11, 5));
	}

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByErosion()}.
	 */
	@Test
	public void testReconstructByErosion_C8()
	{
		int BG = 0;
		int FG = 255;
		int[][] data = new int[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, BG, BG, FG, FG, FG, FG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, BG, BG, FG, FG, FG, FG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, FG, FG, FG, FG, BG, BG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, FG, FG, FG, FG, BG, BG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		UInt8Array2D mask = UInt8Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				mask.setInt(data[y][x], x, y);
			}
		}
		mask = invert(mask);

		UInt8Array2D marker = UInt8Array2D.create(sizeX, sizeY);
		marker.fillValue(255);
		marker.setInt(0, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C8);
		UInt8Array2D result = (UInt8Array2D) algo.process(marker, mask);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(0, result.getInt(2, 6));
		assertEquals(0, result.getInt(4, 8));
		assertEquals(0, result.getInt(8, 5));
		assertEquals(0, result.getInt(14, 8));
		assertEquals(255, result.getInt(15, 9));
		assertEquals(255, result.getInt(0, 0));
		assertEquals(255, result.getInt(5, 3));
		assertEquals(255, result.getInt(11, 5));
	}

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByErosion()}.
	 */
	@Test
	public void testReconstructByErosion_FloatC4()
	{
		float BG = -42;
		float FG = 2500;
		float[][] data = new float[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		Float32Array2D mask = Float32Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				if (data[y][x] == FG)
					mask.setValue(BG, x, y);
				else
					mask.setValue(FG, x, y);
			}
		}

		Float32Array2D marker = Float32Array2D.create(sizeX, sizeY);
		marker.fillValue(FG);
		marker.setValue(BG, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C4);
		Float32Array2D result = (Float32Array2D) algo.process(marker, mask);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(BG, result.getValue(2, 8), .01);
		assertEquals(BG, result.getValue(8, 8), .01);
		assertEquals(BG, result.getValue(8, 5), .01);
		assertEquals(BG, result.getValue(14, 8), .01);
		assertEquals(FG, result.getValue(15, 9), .01);
		assertEquals(FG, result.getValue(0, 0), .01);
		assertEquals(FG, result.getValue(5, 3), .01);
		assertEquals(FG, result.getValue(11, 5), .01);
	}

	/**
	 * Test method for
	 * {@link ijt.filter.morphology.GeodesicReconstruction#reconstructByErosion()}.
	 */
	@Test
	public void testReconstructByErosion_FloatC8()
	{
		float BG = -42;
		float FG = 2500;
		float[][] data = new float[][] {
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, FG, FG, FG, FG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, BG, BG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, BG, BG, BG, BG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, FG, FG, FG, FG, FG, FG, FG, FG, BG, FG, FG, BG, FG, FG, BG },
				{ BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG, BG }, };
		int sizeY = data.length;
		int sizeX = data[0].length;
		Float32Array2D mask = (Float32Array2D) Float32Array2D.create(sizeX, sizeY);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				if (data[y][x] == FG)
					mask.setValue(BG, x, y);
				else
					mask.setValue(FG, x, y);
			}
		}

		Float32Array2D marker = Float32Array2D.create(sizeX, sizeY);
		marker.fillValue(FG);
		marker.setValue(BG, 2, 3);

		MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(
				MorphologicalReconstruction.Type.BY_EROSION, Connectivity2D.C8);
		Float32Array2D result = (Float32Array2D) algo.process(marker, mask);

		assertEquals(16, result.size(0));
		assertEquals(10, result.size(1));
		assertEquals(BG, result.getValue(2, 8), .01);
		assertEquals(BG, result.getValue(8, 8), .01);
		assertEquals(BG, result.getValue(8, 5), .01);
		assertEquals(BG, result.getValue(14, 8), .01);
		assertEquals(FG, result.getValue(15, 9), .01);
		assertEquals(FG, result.getValue(0, 0), .01);
		assertEquals(FG, result.getValue(5, 3), .01);
		assertEquals(FG, result.getValue(11, 5), .01);
	}

	private UInt8Array2D invert(UInt8Array2D array)
	{
		UInt8Array2D result = array.duplicate();
		int size0 = array.size(0);
		int size1 = array.size(1);
		for (int y = 0; y < size1; y++)
		{
			for (int x = 0; x < size0; x++)
			{
				result.setInt(255 - array.getInt(x, y), x, y);
			}
		}
		return result;
	}
	
	public void printImage(Array2D<?> image)
	{
		int sizeX = image.size(0);
		int sizeY = image.size(1);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				System.out.printf(" %3d", image.get(x, y));
			}
			System.out.println("");
		}
	}

	public void printImageFloat(ScalarArray2D<?> image)
	{
		int sizeX = image.size(0);
		int sizeY = image.size(1);
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				System.out.printf(" %7.2f", image.getValue(x, y));
			}
			System.out.println("");
		}
	}
}
