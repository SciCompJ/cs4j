/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DFloat5x5ScanningTest
{
	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat5x5Scanning#process(net.sci.array.data.scalar2d.BinaryArray2D, net.sci.array.data.scalar2d.BinaryArray2D)}.
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
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat5x5Scanning(new double[]{1,2,12}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process(marker, mask);
		
		assertEquals(0, res.getValue(1, 1), 1e-6);
		assertEquals(2, res.getValue(3, 1), 1e-6);
	}

	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat5x5Scanning#process(net.sci.array.data.scalar2d.BinaryArray2D, net.sci.array.data.scalar2d.BinaryArray2D)}.
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
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat5x5Scanning(new double[]{1,2,12}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process(marker, mask);
		
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat5x5Scanning#process(net.sci.array.data.scalar2d.BinaryArray2D, net.sci.array.data.scalar2d.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_CShape_TwoBlobs()
    {
        BinaryArray2D marker = BinaryArray2D.create(8, 5);
        marker.setState(3, 3, true);
        BinaryArray2D mask = BinaryArray2D.create(8, 5);
        mask.setState(1, 1, true);
        mask.setState(2, 1, true);
        mask.setState(3, 1, true);
        mask.setState(1, 2, true);
        mask.setState(1, 3, true);
        mask.setState(2, 3, true);
        mask.setState(3, 3, true);
        
        mask.setState(6, 1, true);
        mask.setState(7, 1, true);
        mask.setState(6, 2, true);
        mask.setState(7, 2, true);
        mask.setState(6, 3, true);
        mask.setState(7, 3, true);

        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat5x5Scanning(new double[]{1,2,12}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process(marker, mask);
        
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(Double.isNaN(res.getValue(0, 0)));
        assertTrue(Double.isInfinite(res.getValue(7,3)));
    }
}
