/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package net.sci.image.label.distmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.binary.ChamferWeights2D;

public class ChamferDistanceTransform2DFloat32Test {

	@Test
	public final void testprocess2d_ChessBoard() 
	{
		UInt8Array2D image = UInt8Array2D.create(12, 10);
		image.fillInt(0);
		for (int y = 2; y < 8; y++) {
			for (int x = 2; x < 10; x++) {
				image.setInt(x, y, 255);
			}
		}
		
		float[] weights = ChamferWeights2D.CHESSBOARD.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, true);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(3, result.getValue(4, 4), 1e-12);
	}
	
	@Test
	public final void testprocess2d_UntilCorners_CityBlock() 
	{
		UInt8Array2D image = UInt8Array2D.create(7, 7);
		image.fillInt(255);
		image.setInt(4, 4, 0);
		
		float[] weights = ChamferWeights2D.CITY_BLOCK.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, false);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(8, result.getValue(0, 0), .01);
		assertEquals(6, result.getValue(6, 0), .01);
		assertEquals(6, result.getValue(0, 6), .01);
		assertEquals(4, result.getValue(6, 6), .01);
		assertEquals(5, result.getValue(0, 5), .01);
	}

	@Test
	public final void testprocess2d_UntilCorners_Chessboard()
	{
		UInt8Array2D image = UInt8Array2D.create(7, 7);
		image.fillInt(255);
		image.setInt(4, 4, 0);
		
		float[] weights = ChamferWeights2D.CHESSBOARD.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, false);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(4, result.getValue(0, 0), .01);
		assertEquals(4, result.getValue(6, 0), .01);
		assertEquals(4, result.getValue(0, 6), .01);
		assertEquals(2, result.getValue(6, 6), .01);
		
		assertEquals(4, result.getValue(0, 5), .01);
	}
	
	@Test
	public final void testprocess2d_UntilCorners_Weights23() 
	{
		UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
		image.setInt(4, 4, 0);
		
		float[] weights = ChamferWeights2D.WEIGHTS_23.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, false);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(12, result.getValue(0, 0), .01);
		assertEquals(10, result.getValue(6, 0), .01);
		assertEquals(10, result.getValue(0, 6), .01);
		assertEquals(6, result.getValue(6, 6), .01);
		
		assertEquals(9, result.getValue(0, 5), .01);
	}
	
	@Test
	public final void testprocess2d_UntilCorners_Borgefors34() 
	{
		UInt8Array2D image = UInt8Array2D.create(7, 7);
		image.fillInt(255);
		image.setInt(4, 4, 0);
		
		float[] weights = ChamferWeights2D.BORGEFORS.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, false);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(16, result.getValue(0, 0), .01);
		assertEquals(14, result.getValue(6, 0), .01);
		assertEquals(14, result.getValue(0, 6), .01);
		assertEquals(8, result.getValue(6, 6), .01);
		
		assertEquals(13, result.getValue(0, 5), .01);
	}
	
	/**
	 * Another test for chess-knight weights, to fix a bug that incorrectly
	 * checked image bounds.
	 */
	@Test
	public final void testprocess2d_UntilCorners_ChessKnight2()
	{
		UInt8Array2D image = UInt8Array2D.create(9, 9);
        image.fillInt(255);
		image.setInt(6, 6, 0);
		
		float[] weights = ChamferWeights2D.CHESSKNIGHT.getFloatWeights();
		ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, false);
		Float32Array2D result = algo.process2d(image);
		
		assertNotNull(result);
		assertEquals(image.size(0), result.size(0));
		assertEquals(image.size(1), result.size(1));
		assertEquals(42, result.getValue(0, 0), .01);
		assertEquals(32, result.getValue(8, 0), .01);
		assertEquals(32, result.getValue(0, 8), .01);
		assertEquals(14, result.getValue(8, 8), .01);
		
		assertEquals(30, result.getValue(0, 6), .01);
	}
	
	/**
	 * Test method for {@link inra.ijpb.label.distmap.LabelDistanceTransform3x3Short#process2d(ij.process.Float32Array2D)}.
	 */
	@Test
	public final void testprocess2d_TouchingLabels()
	{
		UInt8Array2D image = UInt8Array2D.create(8, 8);
		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				image.setInt(x+1, y+1, 1);
				image.setInt(x+4, y+1, 2);
				image.setInt(x+1, y+4, 3);
				image.setInt(x+4, y+4, 4);
			}
		}
		
		ChamferDistanceTransform2DFloat32 ldt = new ChamferDistanceTransform2DFloat32(ChamferWeights2D.CHESSKNIGHT, true);
		Float32Array2D distMap = ldt.process2d(image);

		// value 0 in backgrounf
		assertEquals(0, distMap.getValue(0, 0), .1);
		assertEquals(0, distMap.getValue(5, 0), .1);
		assertEquals(0, distMap.getValue(7, 7), .1);

		// value equal to 2 in the middle of the labels
		assertEquals(2, distMap.getValue(2, 2), .1);
		assertEquals(2, distMap.getValue(5, 2), .1);
		assertEquals(2, distMap.getValue(2, 5), .1);
		assertEquals(2, distMap.getValue(5, 5), .1);
		
		// value equal to 1 on the border of the labels
		assertEquals(1, distMap.getValue(1, 3), .1);
		assertEquals(1, distMap.getValue(3, 3), .1);
		assertEquals(1, distMap.getValue(4, 3), .1);
		assertEquals(1, distMap.getValue(6, 3), .1);
		assertEquals(1, distMap.getValue(1, 6), .1);
		assertEquals(1, distMap.getValue(3, 6), .1);
		assertEquals(1, distMap.getValue(4, 6), .1);
		assertEquals(1, distMap.getValue(6, 6), .1);
	}
}
