/**
 * 
 */
package net.sci.image.binary.geoddist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.IntArray2D;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DUInt16Scanning5x5Test
{
	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Scanning5x5#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_LineSegment()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setBoolean(1, 1, true);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setBoolean(1, 1, true);
		mask.setBoolean(2, 1, true);
		mask.setBoolean(3, 1, true);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Scanning5x5(new short[]{1,2,12}, false);
		IntArray2D<?> res = (IntArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getInt(1, 1));
		assertEquals(2, res.getInt(3, 1));
	}

	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Scanning5x5#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_CShape()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setBoolean(3, 3, true);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setBoolean(1, 1, true);
		mask.setBoolean(2, 1, true);
		mask.setBoolean(3, 1, true);
		mask.setBoolean(1, 2, true);
		mask.setBoolean(1, 3, true);
		mask.setBoolean(2, 3, true);
		mask.setBoolean(3, 3, true);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Scanning5x5(new short[]{1,2,12}, false);
		IntArray2D<?> res = (IntArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getInt(3, 3));
		assertEquals(2, res.getInt(1, 3));
		assertEquals(4, res.getInt(1, 1));
		assertEquals(6, res.getInt(3, 1));
	}

}
