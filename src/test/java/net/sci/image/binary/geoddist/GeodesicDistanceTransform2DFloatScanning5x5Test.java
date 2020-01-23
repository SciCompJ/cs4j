/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.ScalarArray2D;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DFloatScanning5x5Test
{
	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloatScanning5x5#process2d(net.sci.array.scalar.BinaryArray2D, net.sci.array.scalar.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_LineSegment()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setBoolean(true, 1, 1);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setBoolean(true, 1, 1);
		mask.setBoolean(true, 2, 1);
		mask.setBoolean(true, 3, 1);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloatScanning5x5(new double[]{1,2,12}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(1, 1), 1e-6);
		assertEquals(2, res.getValue(3, 1), 1e-6);
	}

	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloatScanning5x5#process2d(net.sci.array.scalar.BinaryArray2D, net.sci.array.scalar.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_CShape()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
		marker.setBoolean(true, 3, 3);
		BinaryArray2D mask = BinaryArray2D.create(5, 5);
		mask.setBoolean(true, 1, 1);
		mask.setBoolean(true, 2, 1);
		mask.setBoolean(true, 3, 1);
		mask.setBoolean(true, 1, 2);
		mask.setBoolean(true, 1, 3);
		mask.setBoolean(true, 2, 3);
		mask.setBoolean(true, 3, 3);
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloatScanning5x5(new double[]{1,2,12}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloatScanning5x5#process2d(net.sci.array.scalar.BinaryArray2D, net.sci.array.scalar.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_CShape_TwoBlobs()
    {
        BinaryArray2D marker = BinaryArray2D.create(8, 5);
        marker.setBoolean(true, 3, 3);
        BinaryArray2D mask = BinaryArray2D.create(8, 5);
        mask.setBoolean(true, 1, 1);
        mask.setBoolean(true, 2, 1);
        mask.setBoolean(true, 3, 1);
        mask.setBoolean(true, 1, 2);
        mask.setBoolean(true, 1, 3);
        mask.setBoolean(true, 2, 3);
        mask.setBoolean(true, 3, 3);
        
        mask.setBoolean(true, 6, 1);
        mask.setBoolean(true, 7, 1);
        mask.setBoolean(true, 6, 2);
        mask.setBoolean(true, 7, 2);
        mask.setBoolean(true, 6, 3);
        mask.setBoolean(true, 7, 3);

        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloatScanning5x5(new double[]{1,2,12}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(Double.isNaN(res.getValue(0, 0)));
        assertTrue(Double.isInfinite(res.getValue(7,3)));
    }
}
