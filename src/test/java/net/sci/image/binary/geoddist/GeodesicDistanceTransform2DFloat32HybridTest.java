/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.binary.distmap.ChamferMask2D;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DFloat32HybridTest
{
	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat32Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D.CITY_BLOCK, false);
		ScalarArray2D<?> res = op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(1, 1), 1e-6);
		assertEquals(2, res.getValue(3, 1), 1e-6);
	}

	/**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat32Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
		
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D.CITY_BLOCK, false);
		ScalarArray2D<?> res = op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DFloat32Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_CShape_TwoBlobs()
    {
        BinaryArray2D marker = BinaryArray2D.create(8, 5);
        marker.setBoolean(3, 3, true);
        BinaryArray2D mask = BinaryArray2D.create(8, 5);
        mask.setBoolean(1, 1, true);
        mask.setBoolean(2, 1, true);
        mask.setBoolean(3, 1, true);
        mask.setBoolean(1, 2, true);
        mask.setBoolean(1, 3, true);
        mask.setBoolean(2, 3, true);
        mask.setBoolean(3, 3, true);
       
        mask.setBoolean(6, 1, true);
        mask.setBoolean(7, 1, true);
        mask.setBoolean(6, 2, true);
        mask.setBoolean(7, 2, true);
        mask.setBoolean(6, 3, true);
        mask.setBoolean(7, 3, true);

        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D.CITY_BLOCK, false);
        ScalarArray2D<?> res = op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(Double.isNaN(res.getValue(0, 0)));
        assertTrue(Double.isInfinite(res.getValue(7,3)));
    }
}
