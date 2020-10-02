package net.sci.array.process.shape;

import static org.junit.Assert.*;
import net.sci.array.Array;
import net.sci.array.scalar.UInt8Array3D;

import org.junit.Test;

public class OrthogonalProjectionTest
{

	@Test
	@Deprecated
	public final void testProcess_Array()
	{
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        array.populateValues((x, y, z) -> x + y * 10 + z * 100);
		
		OrthogonalProjection op = new OrthogonalProjection(2);
		
		Array<?> result = op.process(array);
		assertEquals(2, result.dimensionality());
		assertEquals(array.size(0), result.size(0));
		assertEquals(array.size(1), result.size(1));

		// max value in (3, 2) corresponds to source (3, 2, 2) 
		assertEquals(array.get(3, 2, 2), result.get(new int[]{3, 2}));
	}

}
