/**
 * 
 */
package net.sci.image.binary.geoddist;

import static org.junit.Assert.assertEquals;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.IntArray2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransformShort2D5x5Test
{
	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransformShort2D5x5#process(net.sci.array.data.scalar2d.BinaryArray2D, net.sci.array.data.scalar2d.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_LineSegment()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setState(1, 1, true);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setState(1, 1, true);
		mask.setState(2, 1, true);
		mask.setState(3, 1, true);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransformShort2D5x5(new short[]{1,2,12}, false);
		IntArray2D<?> res = (IntArray2D<?>) op.process(marker, mask);
		
		assertEquals(0, res.getInt(1, 1));
		assertEquals(2, res.getInt(3, 1));
	}

	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransformShort2D5x5#process(net.sci.array.data.scalar2d.BinaryArray2D, net.sci.array.data.scalar2d.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_CShape()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setState(3, 3, true);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setState(1, 1, true);
		mask.setState(2, 1, true);
		mask.setState(3, 1, true);
		mask.setState(1, 2, true);
		mask.setState(1, 3, true);
		mask.setState(2, 3, true);
		mask.setState(3, 3, true);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransformShort2D5x5(new short[]{1,2,12}, false);
		IntArray2D<?> res = (IntArray2D<?>) op.process(marker, mask);
		
		assertEquals(0, res.getInt(3, 3));
		assertEquals(2, res.getInt(1, 3));
		assertEquals(4, res.getInt(1, 1));
		assertEquals(6, res.getInt(3, 1));
	}

}
