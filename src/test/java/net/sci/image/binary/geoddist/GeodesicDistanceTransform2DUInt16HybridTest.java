/**
 * 
 */
package net.sci.image.binary.geoddist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.image.Image;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.io.ImageIOImageReader;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DUInt16HybridTest
{
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(new short[]{1,2,10}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(1, 1), 1e-6);
        assertEquals(2, res.getValue(3, 1), 1e-6);
    }

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_twoBlobs_NonNormalized()
    {
        BinaryArray2D marker = BinaryArray2D.create(10, 5);
        marker.setBoolean(1, 1, true);
        BinaryArray2D mask = BinaryArray2D.create(10, 5);
        mask.fillBooleans((x,y) -> (x>=0 && x<4) || (x>6 && x < 10));
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D.CHESSBOARD, false);
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
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_twoBlobs_Normalized()
    {
        BinaryArray2D marker = BinaryArray2D.create(10, 5);
        marker.setBoolean(1, 1, true);
        BinaryArray2D mask = BinaryArray2D.create(10, 5);
        mask.fillBooleans((x,y) -> (x>=0 && x<4) || (x>6 && x < 10));
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D.CHESSBOARD, true);
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
	 * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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
		GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(new short[]{1,2,10}, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
		
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
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

        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(new short[]{1,2,10}, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, mask);
        
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(res.getValue(7,3) > 100);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_spiral_BorgeforsWeights_mustFail() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/tortuousPath_512x512.png").getFile();
        ImageIOImageReader reader = new ImageIOImageReader(fileName);
        Image image = reader.readImage();
        ScalarArray2D<?> array = ScalarArray2D.wrapScalar2d((ScalarArray<?>) image.getData());
        BinaryArray2D mask = BinaryArray2D.create(array.size(0), array.size(1));
        mask.fillBooleans((x,y) -> array.getValue(x, y) > 0);
        
        BinaryArray2D marker = BinaryArray2D.create(array.size(0), array.size(1));
        marker.setBoolean(0, marker.size(1)/2-2, true);
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D.BORGEFORS, false);
        boolean failed = false;
        try
        {
            op.process2d(marker, mask);
        }
        catch(Exception ex)
        {
            failed = true;
        }
        assertTrue(failed);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_spiral_ChessboardWeights_mustSucceed() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/tortuousPath_512x512.png").getFile();
        ImageIOImageReader reader = new ImageIOImageReader(fileName);
        Image image = reader.readImage();
        ScalarArray2D<?> array = ScalarArray2D.wrapScalar2d((ScalarArray<?>) image.getData());
        BinaryArray2D mask = BinaryArray2D.create(array.size(0), array.size(1));
        mask.fillBooleans((x,y) -> array.getValue(x, y) > 0);
        
        BinaryArray2D marker = BinaryArray2D.create(array.size(0), array.size(1));
        marker.setBoolean(0, marker.size(1)/2-2, true);
        
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D.CHESSBOARD, false);
        boolean failed = false;
        try
        {
            op.process2d(marker, mask);
        }
        catch(Exception ex)
        {
            failed = true;
        }
        assertFalse(failed);
    }
}
