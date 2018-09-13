package net.sci.array.process.shape;

import static org.junit.Assert.*;
import net.sci.array.Array;
import net.sci.array.scalar.UInt8Array3D;

import org.junit.Test;

public class OrthogonalProjectionTest
{

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
		
		OrthogonalProjection op = new OrthogonalProjection(2);
		
		Array<?> result = op.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.getSize(0), result.getSize(0));
		assertEquals(array.getSize(1), result.getSize(1));

		// max value in (3, 2) corresponds to source (3, 2, 2) 
		assertEquals(array.get(3, 2, 2), result.get(new int[]{3, 2}));
	}

}
