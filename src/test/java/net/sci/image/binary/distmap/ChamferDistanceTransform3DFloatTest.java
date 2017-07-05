/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import net.sci.array.data.Float32Array;
import net.sci.array.data.scalar3d.BooleanArray3D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.binary.ChamferWeights3D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ChamferDistanceTransform3DFloatTest
{

	/**
	 * Test method for {@link net.sci.image.binary.distmap.ChamferDistanceTransform3DFloat#process3d(net.sci.array.data.scalar3d.BooleanArray3D)}.
	 */
	@Test
	public final void testProcess3d_Cuboid()
	{
		// create 3D image containing a cube 
		BooleanArray3D image = BooleanArray3D.create(20, 20, 20);
		for (int z = 2; z < 19; z++)
		{
			for (int y = 2; y < 19; y++)
			{
				for (int x = 2; x < 19; x++)
				{
					image.setValue(x, y, z, 1);
				}
			}
		}

		ChamferWeights3D weights = ChamferWeights3D.BORGEFORS;
		DistanceTransform3D algo = new ChamferDistanceTransform3DFloat(weights, true);
		
		ScalarArray3D<?> result = algo.process3d(image);
		assertNotNull(result);
		assertTrue(result instanceof Float32Array);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(image.getSize(2), result.getSize(2));
		
//		System.out.println("result:");
//		for (int x = 0; x < 100; x++)
//		{
//			System.out.print(((int)result.getValue(x, 50, 50)) + " ");
//		}
		double middle = result.getValue(10, 10, 10);
		assertEquals(9, middle, .1);
	}

	@Test
	public void testDistanceMap_FromCenter()
	{
		// create 3D image filled with white containing a black dot in the middle
		BooleanArray3D image = BooleanArray3D.create(21, 21, 21);
		image.fillValue(1);
		image.setValue(10, 10, 10, 0);

		ChamferWeights3D weights = ChamferWeights3D.BORGEFORS;
		DistanceTransform3D algo = new ChamferDistanceTransform3DFloat(weights, true);
		
		ScalarArray3D<?> result = algo.process3d(image);
		assertNotNull(result);
		assertTrue(result instanceof Float32Array);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(image.getSize(2), result.getSize(2));
		
		// Test some voxels in the neighborhood of center
		assertEquals(1.0, result.getValue( 9, 10, 10), .1);
		assertEquals(1.0, result.getValue(11, 10, 10), .1);
		assertEquals(4.0/3.0, result.getValue( 9,  9, 10), .1);
		assertEquals(5.0/3.0, result.getValue( 9,  9,  9), .1);
		
		// Test some voxels at the cube corners
		double exp = 10.0 * 5.0 / 3.0;
		assertEquals(exp, result.getValue( 0,  0,  0), .01);
		assertEquals(exp, result.getValue(20,  0,  0), .01);
		assertEquals(exp, result.getValue( 0, 20,  0), .01);
		assertEquals(exp, result.getValue(20, 20,  0), .01);
		assertEquals(exp, result.getValue( 0,  0, 20), .01);
		assertEquals(exp, result.getValue(20,  0, 20), .01);
		assertEquals(exp, result.getValue( 0, 20, 20), .01);
		assertEquals(exp, result.getValue(20, 20, 20), .01);
	}
}
