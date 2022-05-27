/**
 * 
 */
package net.sci.image.binary.geoddist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.binary.distmap.ChamferMask3D;

/**
 * @author dlegland
 *
 */
public class GeodesicDistanceTransform3DUInt16HybridTest
{
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid#process3d(net.sci.array.binary.BinaryArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void testProcess3d_Cube()
    {
        BinaryArray3D mask = BinaryArray3D.create(10, 10, 10);
        for (int z = 1; z < 9; z++)
        {
            for (int y = 1; y < 9; y++)
            {
                for (int x = 1; x < 9; x++)
                {
                    mask.setBoolean(x, y, z, true);
                }
            }
        }
        
        BinaryArray3D marker = BinaryArray3D.create(10, 10, 10);
        for (int z = 4; z < 6; z++)
        {
            for (int y = 4; y < 6; y++)
            {
                for (int x = 4; x < 6; x++)
                {
                    marker.setBoolean(x, y, z, true);
                }
            }
        }
        
        GeodesicDistanceTransform3D op = new GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D.BORGEFORS, false);
        ScalarArray3D<?> res = op.process3d(marker, mask);
        
        // distance equals 0 within marker
        assertEquals(0, res.getValue(4, 4, 4), 0.001);
        assertEquals(0, res.getValue(5, 5, 5), 0.001);
        
        // distance equals 3*w3 = 15 at cube corners
        assertEquals(15.0, res.getValue(1, 1, 1), 0.001);
        assertEquals(15.0, res.getValue(8, 1, 1), 0.001);
        assertEquals(15.0, res.getValue(1, 8, 1), 0.001);
        assertEquals(15.0, res.getValue(8, 8, 1), 0.001);
        assertEquals(15.0, res.getValue(1, 1, 8), 0.001);
        assertEquals(15.0, res.getValue(8, 1, 8), 0.001);
        assertEquals(15.0, res.getValue(1, 8, 8), 0.001);
        assertEquals(15.0, res.getValue(8, 8, 8), 0.001);
        
        // distance equals 3*w2 = 12 at edge centers
        assertEquals(12.0, res.getValue(4, 1, 1), 0.001);
        assertEquals(12.0, res.getValue(4, 8, 8), 0.001);
        assertEquals(12.0, res.getValue(1, 4, 1), 0.001);
        assertEquals(12.0, res.getValue(8, 4, 8), 0.001);
        assertEquals(12.0, res.getValue(1, 1, 4), 0.001);
        assertEquals(12.0, res.getValue(8, 8, 4), 0.001);

        // distance equals 3*w1 = 9 at face centers
        assertEquals( 9.0, res.getValue(4, 4, 1), 0.001);
        assertEquals( 9.0, res.getValue(4, 4, 8), 0.001);
        assertEquals( 9.0, res.getValue(4, 1, 4), 0.001);
        assertEquals( 9.0, res.getValue(4, 8, 4), 0.001);
        assertEquals( 9.0, res.getValue(1, 4, 4), 0.001);
        assertEquals( 9.0, res.getValue(8, 4, 4), 0.001);
    }

    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid#process3d(net.sci.array.binary.BinaryArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void testProcess3d_FullCube()
    {
        BinaryArray3D mask = BinaryArray3D.create(10, 10, 10);
        mask.fill(Binary.TRUE);
        
        BinaryArray3D marker = BinaryArray3D.create(10, 10, 10);
        for (int z = 4; z < 6; z++)
        {
            for (int y = 4; y < 6; y++)
            {
                for (int x = 4; x < 6; x++)
                {
                    marker.setBoolean(x, y, z, true);
                }
            }
        }
        
        GeodesicDistanceTransform3D op = new GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D.BORGEFORS, false);
        ScalarArray3D<?> res = op.process3d(marker, mask);
        
        // distance equals 0 within marker
        assertEquals(0, res.getValue(4, 4, 4), 0.001);
        assertEquals(0, res.getValue(5, 5, 5), 0.001);
        
        // distance equals 4*w3 = 20 at cube corners
        assertEquals(20.0, res.getValue(0, 0, 0), 0.001);
        assertEquals(20.0, res.getValue(9, 0, 0), 0.001);
        assertEquals(20.0, res.getValue(0, 9, 0), 0.001);
        assertEquals(20.0, res.getValue(9, 9, 0), 0.001);
        assertEquals(20.0, res.getValue(0, 0, 9), 0.001);
        assertEquals(20.0, res.getValue(9, 0, 9), 0.001);
        assertEquals(20.0, res.getValue(0, 9, 9), 0.001);
        assertEquals(20.0, res.getValue(9, 9, 9), 0.001);
        
        // distance equals 4*w2 = 16 at edge centers
        assertEquals(16.0, res.getValue(4, 0, 0), 0.001);
        assertEquals(16.0, res.getValue(4, 9, 9), 0.001);
        assertEquals(16.0, res.getValue(0, 4, 0), 0.001);
        assertEquals(16.0, res.getValue(9, 4, 9), 0.001);
        assertEquals(16.0, res.getValue(0, 0, 4), 0.001);
        assertEquals(16.0, res.getValue(9, 9, 4), 0.001);

        // distance equals 4*w1 = 12 at face centers
        assertEquals(12.0, res.getValue(4, 4, 0), 0.001);
        assertEquals(12.0, res.getValue(4, 4, 9), 0.001);
        assertEquals(12.0, res.getValue(4, 0, 4), 0.001);
        assertEquals(12.0, res.getValue(4, 9, 4), 0.001);
        assertEquals(12.0, res.getValue(0, 4, 4), 0.001);
        assertEquals(12.0, res.getValue(9, 4, 4), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid#process3d(net.sci.array.binary.BinaryArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void testProcess3d_HollowCube()
    {
        BinaryArray3D mask = BinaryArray3D.create(6, 6, 6);
        mask.fill(Binary.TRUE);
        for (int z = 1; z < 5; z++)
        {
            for (int y = 1; y < 5; y++)
            {
                for (int x = 1; x < 5; x++)
                {
                    mask.setBoolean(x, y, z, false);
                }
            }
        }
        
        BinaryArray3D marker = BinaryArray3D.create(6, 6, 6);
        marker.setBoolean(0, 0, 0, true);
        
        GeodesicDistanceTransform3D op = new GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D.BORGEFORS, false);
        ScalarArray3D<?> res = op.process3d(marker, mask);
        
        // distance equals 0 within marker
        assertEquals(0, res.getValue(0, 0, 0), 0.001);
        
        // distance equals 5*w1 = 15 at 3 cube corners
        assertEquals(15.0, res.getValue(5, 0, 0), 0.001);
        assertEquals(15.0, res.getValue(0, 5, 0), 0.001);
        assertEquals(15.0, res.getValue(0, 0, 5), 0.001);
        
        // distance equals 5*w2 = 20 at 3 cube corners
        assertEquals(20.0, res.getValue(5, 5, 0), 0.001);
        assertEquals(20.0, res.getValue(5, 0, 5), 0.001);
        assertEquals(20.0, res.getValue(0, 5, 5), 0.001);
        
        // distance equals 4*w2+w3+4*w1 = 16+5+12 = 33 at opposite corner
        assertEquals(33.0, res.getValue(5, 5, 5), 0.001);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid#process3d(net.sci.array.binary.BinaryArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void testProcess3d_HollowCube_Normalized()
    {
        BinaryArray3D mask = BinaryArray3D.create(6, 6, 6);
        mask.fill(Binary.TRUE);
        for (int z = 1; z < 5; z++)
        {
            for (int y = 1; y < 5; y++)
            {
                for (int x = 1; x < 5; x++)
                {
                    mask.setBoolean(x, y, z, false);
                }
            }
        }
        
        BinaryArray3D marker = BinaryArray3D.create(6, 6, 6);
        marker.setBoolean(0, 0, 0, true);
        
        GeodesicDistanceTransform3D op = new GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D.BORGEFORS, true);
        ScalarArray3D<?> res = op.process3d(marker, mask);
        
        // distance equals 0 within marker
        assertEquals(0, res.getValue(0, 0, 0), 0.001);
        
        // distance equals 5*w1 = 15 -> 5 at 3 cube corners
        assertEquals( 5.0, res.getValue(5, 0, 0), 0.01);
        assertEquals( 5.0, res.getValue(0, 5, 0), 0.01);
        assertEquals( 5.0, res.getValue(0, 0, 5), 0.01);
        
        // distance equals 5*w2 = 20 -> round(6.66) = 7 at 3 cube corners
        assertEquals( 7.0, res.getValue(5, 5, 0), 0.01);
        assertEquals( 7.0, res.getValue(5, 0, 5), 0.01);
        assertEquals( 7.0, res.getValue(0, 5, 5), 0.01);
        
        // distance equals 4*w2+w3+4*w1 = 16+5+12 = 33 -> 11 at opposite corner
        assertEquals(11.0, res.getValue(5, 5, 5), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.binary.geoddist.GeodesicDistanceTransform3DUInt16Hybrid#process3d(net.sci.array.binary.BinaryArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public void testProcess3d_HollowCubeReverse()
    {
        BinaryArray3D mask = BinaryArray3D.create(6, 6, 6);
        mask.fill(Binary.TRUE);
        for (int z = 1; z < 5; z++)
        {
            for (int y = 1; y < 5; y++)
            {
                for (int x = 1; x < 5; x++)
                {
                    mask.setBoolean(x, y, z, false);
                }
            }
        }
        
        BinaryArray3D marker = BinaryArray3D.create(6, 6, 6);
        marker.setBoolean(5, 5, 5, true);
        
        GeodesicDistanceTransform3D op = new GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D.BORGEFORS, false);
        ScalarArray3D<?> res = op.process3d(marker, mask);
        
        // distance equals 0 within marker
        assertEquals(0, res.getValue(5, 5, 5), 0.001);
        
        // distance equals 5*w1 = 15 at 3 cube corners
        assertEquals(15.0, res.getValue(5, 5, 0), 0.001);
        assertEquals(15.0, res.getValue(5, 0, 5), 0.001);
        assertEquals(15.0, res.getValue(0, 5, 5), 0.001);
        
        // distance equals 5*w2 = 20 at 3 cube corners
        assertEquals(20.0, res.getValue(5, 0, 0), 0.001);
        assertEquals(20.0, res.getValue(0, 5, 0), 0.001);
        assertEquals(20.0, res.getValue(0, 0, 5), 0.001);
        
        // distance equals 4*w2+w3+4*w1 = 16+5+12 = 33 at opposite corner
        assertEquals(33.0, res.getValue(0, 0, 0), 0.001);
    }
}
