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

import net.sci.array.scalar.UInt16Array2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.binary.ChamferWeights2D;

public class ChamferDistanceTransform2DUInt16Test
{
    @Test
    public final void testDistanceMap_ChessBoard()
    {
        UInt8Array2D image = UInt8Array2D.create(12, 10);
        image.fillInt(0);
        for (int y = 2; y < 8; y++)
        {
            for (int x = 2; x < 10; x++)
            {
                image.setInt(x, y, 255);
            }
        }

        short[] weights = ChamferWeights2D.CHESSBOARD.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, true);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(3, result.getInt(4, 4));
    }

    @Test
    public final void testDistanceMap_UntilCorners_CityBlock()
    {
        UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
        image.setInt(4, 4, 0);

        short[] weights = ChamferWeights2D.CITY_BLOCK.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(8, result.getInt(0, 0));
        assertEquals(6, result.getInt(6, 0));
        assertEquals(6, result.getInt(0, 6));
        assertEquals(4, result.getInt(6, 6));

        assertEquals(5, result.getInt(0, 5));
    }

    @Test
    public final void testDistanceMap_UntilCorners_Chessboard()
    {
        UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
        image.setInt(4, 4, 0);

        short[] weights = ChamferWeights2D.CHESSBOARD.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(4, result.getInt(0, 0));
        assertEquals(4, result.getInt(6, 0));
        assertEquals(4, result.getInt(0, 6));
        assertEquals(2, result.getInt(6, 6));

        assertEquals(4, result.getInt(0, 5));
    }

    @Test
    public final void testDistanceMap_UntilCorners_Weights23()
    {
        UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
        image.setInt(4, 4, 0);

        short[] weights = ChamferWeights2D.WEIGHTS_23.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(12, result.getInt(0, 0));
        assertEquals(10, result.getInt(6, 0));
        assertEquals(10, result.getInt(0, 6));
        assertEquals(6, result.getInt(6, 6));

        assertEquals(9, result.getInt(0, 5));
    }

    @Test
    public final void testDistanceMap_UntilCorners_Borgefors34()
    {
        UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
        image.setInt(4, 4, 0);

        short[] weights = ChamferWeights2D.BORGEFORS.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(16, result.getInt(0, 0));
        assertEquals(14, result.getInt(6, 0));
        assertEquals(14, result.getInt(0, 6));
        assertEquals(8, result.getInt(6, 6));

        assertEquals(13, result.getInt(0, 5));
    }

    @Test
    public final void testDistanceMap_UntilCorners_ChessKnight()
    {
        UInt8Array2D image = UInt8Array2D.create(7, 7);
        image.fillInt(255);
        image.setInt(4, 4, 0);

        short[] weights = ChamferWeights2D.CHESSKNIGHT.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(28, result.getInt(0, 0));
        assertEquals(22, result.getInt(6, 0));
        assertEquals(22, result.getInt(0, 6));
        assertEquals(14, result.getInt(6, 6));

        assertEquals(20, result.getInt(0, 4));
    }

    /**
     * Another test for chess-knight weights, to fix a bug that incorrectly
     * checked image bounds.
     */
    @Test
    public final void testDistanceMap_UntilCorners_ChessKnight2()
    {
        UInt8Array2D image = UInt8Array2D.create(9, 9);
        image.fillInt(255);
        image.setInt(6, 6, 0);

        short[] weights = ChamferWeights2D.CHESSKNIGHT.getShortWeights();
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(
                weights, false);
        UInt16Array2D result = algo.process2d(image);

        assertNotNull(result);
        assertEquals(image.size(0), result.size(0));
        assertEquals(image.size(1), result.size(1));
        assertEquals(42, result.getInt(0, 0));
        assertEquals(32, result.getInt(8, 0));
        assertEquals(32, result.getInt(0, 8));
        assertEquals(14, result.getInt(8, 8));

        assertEquals(30, result.getInt(0, 6));
    }

    @Test
    public final void testDistanceMap_TouchingLabels()
    {
        UInt8Array2D image = UInt8Array2D.create(8, 8);
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                image.setInt(x + 1, y + 1, 1);
                image.setInt(x + 4, y + 1, 2);
                image.setInt(x + 1, y + 4, 3);
                image.setInt(x + 4, y + 4, 4);
            }
        }

        ChamferDistanceTransform2DUInt16 dt = new ChamferDistanceTransform2DUInt16(
                ChamferWeights2D.CHESSKNIGHT, true);
        UInt16Array2D distMap = dt.process2d(image);

        // value 0 in background
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
