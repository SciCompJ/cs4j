/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sci.array.data.scalar2d.BooleanArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.binary.ChamferWeights2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ChamferDistanceTransform2DFloatTest
{

	/**
	 * Test method for {@link net.sci.image.binary.distmap.ChamferDistanceTransform2DFloat#process2d(net.sci.array.data.scalar2d.BooleanArray2D)}.
	 */
	@Test
	public final void testProcess2d()
	{
		// Create a black image with a white 8-by-6 rectangle in the middle
		BooleanArray2D image = BooleanArray2D.create(12, 10);
		for (int y = 2; y < 8; y++)
		{
			for (int x = 2; x < 10; x++)
			{
				image.setState(x, y, true);
			}
		}

		ChamferWeights2D weights = ChamferWeights2D.CHESSBOARD;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, true);
		ScalarArray2D<?> result = algo.process2d(image);

//		result.print(System.out);
		
		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(3, result.getValue(4, 4), 1e-12);
	}

	@Test
	public final void testDistanceMap_UntilCorners_CityBlock() 
	{
		BooleanArray2D image = BooleanArray2D.create(7, 7);
		image.fillValue(1);
		image.setValue(4, 4, 0);
		
		ChamferWeights2D weights = ChamferWeights2D.CITY_BLOCK;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

//		result.print(System.out);
		
		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(8, result.getValue(0, 0), .01);
		assertEquals(6, result.getValue(6, 0), .01);
		assertEquals(6, result.getValue(0, 6), .01);
		assertEquals(4, result.getValue(6, 6), .01);
	}
	
	@Test
	public final void testDistanceMap_UntilCorners_Chessboard() 
	{
		BooleanArray2D image = BooleanArray2D.create(7, 7);
		image.fillValue(1);
		image.setValue(4, 4, 0);
		
		ChamferWeights2D weights = ChamferWeights2D.CHESSBOARD;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(4, result.getValue(0, 0), .01);
		assertEquals(4, result.getValue(6, 0), .01);
		assertEquals(4, result.getValue(0, 6), .01);
		assertEquals(2, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Weights_23() 
	{
		BooleanArray2D image = BooleanArray2D.create(7, 7);
		image.fillValue(1);
		image.setValue(4, 4, 0);
		
		ChamferWeights2D weights = ChamferWeights2D.WEIGHTS_23;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(12, result.getValue(0, 0), .01);
		assertEquals(10, result.getValue(6, 0), .01);
		assertEquals(10, result.getValue(0, 6), .01);
		assertEquals(6, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Borgefors34() 
	{
		BooleanArray2D image = BooleanArray2D.create(7, 7);
		image.fillValue(1);
		image.setValue(4, 4, 0);
		
		ChamferWeights2D weights = ChamferWeights2D.BORGEFORS;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(16, result.getValue(0, 0), .01);
		assertEquals(14, result.getValue(6, 0), .01);
		assertEquals(14, result.getValue(0, 6), .01);
		assertEquals(8, result.getValue(6, 6), .01);
	}

	@Test
	public final void testDistanceMap_UntilCorners_Chessknight() 
	{
		BooleanArray2D image = BooleanArray2D.create(9, 9);
		image.fillValue(1);
		image.setValue(6, 6, 0);
		
		ChamferWeights2D weights = ChamferWeights2D.CHESSKNIGHT;
		DistanceTransform2D algo = new ChamferDistanceTransform2DFloat(weights, false);
		ScalarArray2D<?> result = algo.process2d(image);

		assertNotNull(result);
		assertEquals(image.getSize(0), result.getSize(0));
		assertEquals(image.getSize(1), result.getSize(1));
		assertEquals(42, result.getValue(0, 0), .01);
		assertEquals(32, result.getValue(8, 0), .01);
		assertEquals(32, result.getValue(0, 8), .01);
		assertEquals(14, result.getValue(8, 8), .01);
		
		assertEquals(30, result.getValue(0, 6), .01);
	}
}
