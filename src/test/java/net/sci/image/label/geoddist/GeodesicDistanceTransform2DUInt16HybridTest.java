/**
 * 
 */
package net.sci.image.label.geoddist;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.*;
import net.sci.image.Image;
import net.sci.image.binary.distmap.ChamferMask2D;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform2DUInt16HybridTest
{
	/**
	 * Test method for {@link net.sci.image.label.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_LineSegment()
	{
		BinaryArray2D marker = BinaryArray2D.create(5, 5);
        marker.setBoolean(1, 1, true);
        UInt8Array2D labelMap = UInt8Array2D.create(5, 5);
        labelMap.setInt(1, 1, 12);
        labelMap.setInt(2, 1, 12);
        labelMap.setInt(3, 1, 12);
		
        ChamferMask2D mask = ChamferMask2D.CITY_BLOCK;
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(mask, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, labelMap);
		
        assertTrue(res instanceof UInt16Array);
		assertEquals(0, res.getValue(1, 1), 1e-6);
		assertEquals(2, res.getValue(3, 1), 1e-6);
	}

	/**
	 * Test method for {@link net.sci.image.label.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
	 */
	@Test
	public final void testProcess_CShape()
	{
        BinaryArray2D marker = BinaryArray2D.create(5, 5);
        marker.setBoolean(3, 3, true);
        UInt8Array2D labelMap = UInt8Array2D.create(5, 5);
        labelMap.setInt(1, 1, 12);
        labelMap.setInt(2, 1, 12);
        labelMap.setInt(3, 1, 12);
        labelMap.setInt(1, 2, 12);
        labelMap.setInt(1, 3, 12);
        labelMap.setInt(2, 3, 12);
        labelMap.setInt(3, 3, 12);
        ChamferMask2D mask = ChamferMask2D.CITY_BLOCK;
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(mask, false);
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, labelMap);
		
        assertTrue(res instanceof UInt16Array);
		assertEquals(0, res.getValue(3, 3), 1e-6);
		assertEquals(2, res.getValue(1, 3), 1e-6);
		assertEquals(4, res.getValue(1, 1), 1e-6);
		assertEquals(6, res.getValue(3, 1), 1e-6);
	}

    /**
     * Test method for {@link net.sci.image.label.geoddist.GeodesicDistanceTransform2DUInt16Hybrid#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_CShape_TwoBlobs()
    {
        BinaryArray2D marker = BinaryArray2D.create(8, 5);
        marker.setBoolean(3, 3, true);
        UInt8Array2D labelMap = UInt8Array2D.create(8, 5);
        labelMap.setInt(1, 1, 12);
        labelMap.setInt(2, 1, 12);
        labelMap.setInt(3, 1, 12);
        labelMap.setInt(1, 2, 12);
        labelMap.setInt(1, 3, 12);
        labelMap.setInt(2, 3, 12);
        labelMap.setInt(3, 3, 12);
       
        labelMap.setInt(6, 1, 12);
        labelMap.setInt(7, 1, 12);
        labelMap.setInt(6, 2, 12);
        labelMap.setInt(7, 2, 12);
        labelMap.setInt(6, 3, 12);
        labelMap.setInt(7, 3, 12);

        ChamferMask2D mask = ChamferMask2D.CITY_BLOCK;
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(mask, false);
        ScalarArray2D<?> res = (ScalarArray2D<?>) op.process2d(marker, labelMap);
        
        assertTrue(res instanceof UInt16Array);
        assertEquals(0, res.getValue(3, 3), 1e-6);
        assertEquals(2, res.getValue(1, 3), 1e-6);
        assertEquals(4, res.getValue(1, 1), 1e-6);
        assertEquals(6, res.getValue(3, 1), 1e-6);
        
        assertTrue(res.getValue(7,3) > 100);
    }
    
    /**
     * Test method for {@link net.sci.image.label.geoddist.GeodesicDistanceTransform2DFloat32Hybrid5x5#process2d(net.sci.array.binary.BinaryArray2D, net.sci.array.binary.BinaryArray2D)}.
     * @throws IOException 
     */
    @Test
    public final void testProcess_Blobs() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/marker.tif").getFile();
        Image markerImage = Image.readImage(new File(fileName));
        UInt8Array2D marker = (UInt8Array2D) markerImage.getData();
        BinaryArray2D marker2d = BinaryArray2D.wrap(BinaryArray.convert(marker));

        fileName = getClass().getResource("/images/binary/mask-lbl.tif").getFile();
        Image maskImage = Image.readImage(new File(fileName));
        UInt8Array2D labelMap = (UInt8Array2D) maskImage.getData();

        ChamferMask2D mask = ChamferMask2D.CHESSKNIGHT;
        GeodesicDistanceTransform2D op = new GeodesicDistanceTransform2DUInt16Hybrid(mask, true);
        ScalarArray2D<?> distMap = (ScalarArray2D<?>) op.process2d(marker2d, labelMap);
        
        assertTrue(distMap instanceof UInt16Array);
        assertEquals(421, distMap.getValue(119, 71), 1e-6);
        assertEquals(UInt16.MAX_INT, distMap.getValue(15, 30), 1e-6);
        assertEquals(UInt16.MAX_INT, distMap.getValue(150, 160), 1e-6);
    }
}
