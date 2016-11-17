package net.sci.image.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.Array3D;
import net.sci.array.data.scalar3d.BooleanArray3D;

public class FloodFillComponentsLabeling3DTest
{

	@Test
	public void testProcess3d()
	{
		// create the reference 3D image, that contains height cubes with size 2x2x2
		BooleanArray3D image = BooleanArray3D.create(8, 8, 8);
		for (int z = 0; z < 2; z++)
		{
			for (int y = 0; y < 2; y++)
			{
				for (int x = 0; x < 2; x++)
				{
					image.setState(x + 1, y + 1, z + 1, true);
					image.setState(x + 5, y + 1, z + 1, true);
					image.setState(x + 1, y + 5, z + 1, true);
					image.setState(x + 5, y + 5, z + 1, true);
					image.setState(x + 1, y + 1, z + 5, true);
					image.setState(x + 5, y + 1, z + 5, true);
					image.setState(x + 1, y + 5, z + 5, true);
					image.setState(x + 5, y + 5, z + 5, true);
				}
			}
		}
		
		// compute labels of the binary image
		FloodFillComponentsLabeling3D algo = new FloodFillComponentsLabeling3D(6, 8); 
		Array3D<?> labels = (Array3D<?>) algo.process(image);
		
		// check labels and empty regions
		assertEquals(0, (int) labels.getValue(0, 0, 0));
		assertEquals(1, (int) labels.getValue(2, 2, 2));
		assertEquals(0, (int) labels.getValue(4, 4, 4));
		assertEquals(8, (int) labels.getValue(6, 6, 6));
		assertEquals(0, (int) labels.getValue(7, 7, 7));
	}
}
