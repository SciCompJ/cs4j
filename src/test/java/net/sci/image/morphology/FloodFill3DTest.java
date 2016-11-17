package net.sci.image.morphology;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.data.scalar3d.IntArray3D;
import net.sci.array.data.scalar3d.UInt8Array3D;

public class FloodFill3DTest
{
	@Test
	public final void testFloodFillPair_Cross3d_C26Float()
	{
		BooleanArray3D image = createCornerCross();
		// System.out.println("input image:");
		// printStack(image);

		IntArray3D<?> result = UInt8Array3D.create(image.getSize(0), image.getSize(1), image.getSize(2)); 
				
		int newVal = 120;
		FloodFill3D.floodFillFloat(image, 2, 4, 4, result, newVal, 26);

		// System.out.println("output image:");
		// printStack(result);

		// Test each of the branches
		assertEquals(newVal, result.getInt(0, 4, 4));
		assertEquals(newVal, result.getInt(8, 4, 4));
		assertEquals(newVal, result.getInt(4, 0, 4));
		assertEquals(newVal, result.getInt(4, 8, 4));
		assertEquals(newVal, result.getInt(4, 4, 0));
		assertEquals(newVal, result.getInt(4, 4, 8));
	}

	/**
	 * Creates a stack representing a cross with branches touching only by
	 * corners.
	 */
	public BooleanArray3D createCornerCross()
	{
		// Create test image
		int sizeX = 9;
		int sizeY = 9;
		int sizeZ = 9;
		BooleanArray3D image = BooleanArray3D.create(sizeX, sizeY, sizeZ);
		int val0 = 50;
		
		// Center voxel
		image.setInt(4, 4, 4, val0);
		// eight corners
		image.setInt(3, 3, 3, val0);
		image.setInt(3, 3, 5, val0);
		image.setInt(3, 5, 3, val0);
		image.setInt(3, 5, 5, val0);
		image.setInt(5, 3, 3, val0);
		image.setInt(5, 3, 5, val0);
		image.setInt(5, 5, 3, val0);
		image.setInt(5, 5, 5, val0);
		// six branches
		for (int i = 0; i < 3; i++)
		{
			image.setInt(i, 4, 4, val0);
			image.setInt(i + 6, 4, 4, val0);
			image.setInt(4, i, 4, val0);
			image.setInt(4, i + 6, 4, val0);
			image.setInt(4, 4, i, val0);
			image.setInt(4, 4, i + 6, val0);
		}

		return image;
	}
}
