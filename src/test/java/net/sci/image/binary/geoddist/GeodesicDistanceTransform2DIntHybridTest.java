/**
 * 
 */
package net.sci.image.binary.geoddist;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.image.binary.distmap.ChamferMask2D;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DIntHybridTest
{
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DIntHybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DIntHybrid(new short[]{1,2,10}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(1, 1), 1e-6);
        assertEquals(2, res.getValue(3, 1), 1e-6);
    }

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DIntHybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_twoBlobs_NonNormalized()
    {
        BinaryArray2D marker = BinaryArray2D.create(10, 5);
        marker.setBoolean(1, 1, true);
        BinaryArray2D mask = BinaryArray2D.create(10, 5);
        mask.fillBooleans((x,y) -> (x>=0 && x<4) || (x>6 && x < 10));
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DIntHybrid(ChamferMask2D.CHESSBOARD, false);
        UInt16Array2D res = (UInt16Array2D) op.process2d(marker, mask);
        
        // within first blob
        assertEquals(0, res.getInt(1, 1));
        assertEquals(2, res.getInt(1, 3));
        // in mask background
        assertEquals(UInt16.MAX_INT, res.getInt(5, 3));
        // in mask foreground, but unreacheable region
        assertEquals(UInt16.MAX_INT, res.getInt(8, 2));
    }

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DIntHybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_twoBlobs_Normalized()
    {
        BinaryArray2D marker = BinaryArray2D.create(10, 5);
        marker.setBoolean(1, 1, true);
        BinaryArray2D mask = BinaryArray2D.create(10, 5);
        mask.fillBooleans((x,y) -> (x>=0 && x<4) || (x>6 && x < 10));
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DIntHybrid(ChamferMask2D.CHESSBOARD, true);
        UInt16Array2D res = (UInt16Array2D) op.process2d(marker, mask);
        
        // within first blob
        assertEquals(0, res.getInt(1, 1));
        assertEquals(2, res.getInt(1, 3));
        // in mask background
        assertEquals(UInt16.MAX_INT, res.getInt(5, 3));
        // in mask foreground, but unreacheable region
        assertEquals(UInt16.MAX_INT, res.getInt(8, 2));
    }
    
    /**
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DIntHybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DIntHybrid(new short[]{1,2,10}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DIntHybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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

        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DIntHybrid(new short[]{1,2,10}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(res.getValue(7,3) > 100);
    }
}
