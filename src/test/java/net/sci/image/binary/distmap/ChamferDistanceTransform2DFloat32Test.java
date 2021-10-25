/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.binary.ChamferWeights2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ChamferDistanceTransform2DFloat32Test
{

	/**
	 * Test method for {@link net.sci.image.binary.distmap.ChamferDistanceTransform2DFloat32#process2d(net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess2d()
	{
		// Create a black image with a white 8-by-6 rectangle in the middle
		BinaryArray2D image = BinaryArray2D.create(12, 10);
		for (int y = 2; y < 8; y++)
		{
			for (int x = 2; x < 10; x++)
			{
				image.setBoolean(x, y, true);
			}
		}

		ChamferWeights2D weights = ChamferWeights2D.CHESSBOARD;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, true);
		ScalarArray2D<?> result = algo.process2d(image);

//		result.print(System.out);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(3, result.getValue(4, 4), 1e-12);
	}

	@Test
	public final void testDistanceMap_UntilCorners_CityBlock() 
	{
		BinaryArray2D image = BinaryArray2D.create(7, 7);
        image.fill(Binary.TRUE);
        image.setBoolean(4, 4, false);
		
		ChamferWeights2D weights = ChamferWeights2D.CITY_BLOCK;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

//		result.print(System.out);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(8, result.getValue(0, 0), .01);
		assertEquals(6, result.getValue(6, 0), .01);
		assertEquals(6, result.getValue(0, 6), .01);
		assertEquals(4, result.getValue(6, 6), .01);
	}
	
	@Test
	public final void testDistanceMap_UntilCorners_Chessboard() 
	{
		BinaryArray2D image = BinaryArray2D.create(7, 7);
        image.fill(Binary.TRUE);
        image.setBoolean(4, 4, false);
		
		ChamferWeights2D weights = ChamferWeights2D.CHESSBOARD;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(4, result.getValue(0, 0), .01);
		assertEquals(4, result.getValue(6, 0), .01);
		assertEquals(4, result.getValue(0, 6), .01);
		assertEquals(2, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Weights_23() 
	{
		BinaryArray2D image = BinaryArray2D.create(7, 7);
        image.fill(Binary.TRUE);
        image.setBoolean(4, 4, false);
		
		ChamferWeights2D weights = ChamferWeights2D.WEIGHTS_23;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(12, result.getValue(0, 0), .01);
		assertEquals(10, result.getValue(6, 0), .01);
		assertEquals(10, result.getValue(0, 6), .01);
		assertEquals(6, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Borgefors34() 
	{
		BinaryArray2D image = BinaryArray2D.create(7, 7);
        image.fill(Binary.TRUE);
        image.setBoolean(4, 4, false);
		
		ChamferWeights2D weights = ChamferWeights2D.BORGEFORS;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(16, result.getValue(0, 0), .01);
		assertEquals(14, result.getValue(6, 0), .01);
		assertEquals(14, result.getValue(0, 6), .01);
		assertEquals(8, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Chessknight() 
	{
		BinaryArray2D image = BinaryArray2D.create(9, 9);
        image.fill(Binary.TRUE);
        image.setBoolean(6, 6, false);
		
		ChamferWeights2D weights = ChamferWeights2D.CHESSKNIGHT;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat32(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(42, result.getValue(0, 0), .01);
		assertEquals(32, result.getValue(8, 0), .01);
		assertEquals(32, result.getValue(0, 8), .01);
		assertEquals(14, result.getValue(8, 8), .01);
		
		assertEquals(30, result.getValue(0, 6), .01);
	}
}
