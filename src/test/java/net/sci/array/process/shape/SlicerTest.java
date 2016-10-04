/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.assertEquals;
import net.sci.array.Array;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.data.scalar3d.UInt8Array3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class SlicerTest
{
	/**
	 * Test static method in slicer.
	 * 
	 * Test method for {@link net.sci.array.process.shape.Slicer#getSlice(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testGetSlice()
	{
		UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
		for (int z = 0; z < 3; z++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int x = 0; x < 5; x++)
				{
					array.setInt(x, y, z, z * 100 + y * 10 + x);
				}
			}
		}
		
		UInt8Array2D result = UInt8Array2D.create(5, 4);
		
		Slicer.getSlice(array, result, 2, 1);

		assertEquals(array.getValue(3, 2, 1), result.getValue(new int[]{3, 2}), .1);
	}

	/**
	 * Test method for {@link net.sci.array.process.shape.Slicer#process(net.sci.array.Array, net.sci.array.Array)}.
	 */
	@Test
	public final void testProcess_Array()
	{
		UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
		for (int z = 0; z < 3; z++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int x = 0; x < 5; x++)
				{
					array.setInt(x, y, z, z * 100 + y * 10 + x);
				}
			}
		}
		Slicer slicer = new Slicer(2, 1);
		
		Array<?> result = slicer.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.getSize(0), result.getSize(0));
		assertEquals(array.getSize(1), result.getSize(1));

		assertEquals(array.getValue(3, 2, 1), result.getValue(new int[]{3, 2}), .1);
	}

}
