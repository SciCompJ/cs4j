/**
 * 
 */
package net.sci.image.analyze.region3d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Rotation3D;
import net.sci.geom.geom3d.surface.Ellipsoid3D;
import net.sci.image.Calibration;

/**
 * @author dlegland
 *
 */
public class EquivalentEllipsoid3DTest
{

    /**
     * Test method for {@link net.sci.image.analyze.region3d.EquivalentEllipsoid3D#equivalentEllipsoids(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testEquivalentEllipsoids_singleVoxel()
    {
        UInt8Array3D array = UInt8Array3D.create(7, 7, 7);
        array.setInt(5, 4, 3, 10);
        Calibration calib = new Calibration(3);
        
        Ellipsoid3D elli = EquivalentEllipsoid3D.equivalentEllipsoids(array, new int[] {10}, calib)[0];
        
        double expectedRadius = Math.sqrt(5.0 / 12.0);
        assertEquals(5.0, elli.center().x(), 0.1);
        assertEquals(4.0, elli.center().y(), 0.1);
        assertEquals(3.0, elli.center().z(), 0.1);
        assertEquals(expectedRadius, elli.radiusList()[0], 0.1);
        assertEquals(expectedRadius, elli.radiusList()[1], 0.1);
        assertEquals(expectedRadius, elli.radiusList()[2], 0.1);
        double[] eulerAngles = elli.orientation().eulerAngles(); 
        assertEquals(0.0, eulerAngles[0], 1.0);
        assertEquals(0.0, eulerAngles[1], 1.0);
        assertEquals(0.0, eulerAngles[2], 1.0);
    }

    /**
     * Test method for {@link net.sci.image.analyze.region3d.EquivalentEllipsoid3D#equivalentEllipsoids(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testEquivalentEllipsoids_emptyRegion()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 5, 5);
        array.setInt(3, 2, 1, 10);
        Calibration calib = new Calibration(3);
        
        Ellipsoid3D elli = EquivalentEllipsoid3D.equivalentEllipsoids(array, new int[] {5}, calib)[0];
        
        double expectedRadius = 0.0;
        assertEquals(expectedRadius, elli.radiusList()[0], 0.1);
        assertEquals(expectedRadius, elli.radiusList()[1], 0.1);
        assertEquals(expectedRadius, elli.radiusList()[2], 0.1);
    }
    
    /**
     * Test method for {@link net.sci.image.analyze.region3d.EquivalentEllipsoid3D#equivalentEllipsoids(net.sci.array.scalar.IntArray3D, int[], net.sci.image.Calibration)}.
     */
    @Test
    public final void testEquivalentEllipsoids_arbitraryEllipsoid()
    {
        Point3D center = new Point3D(25.3, 21.1, 18.4);
        double[] radList = new double[] {20.4, 16.3, 11.7};
        double[] refAngles = new double[] {0.1, 0.2, 0.3};
        Rotation3D orient = Rotation3D.fromEulerAngles(refAngles[0], refAngles[1], refAngles[2]);
        Ellipsoid3D refElli = new Ellipsoid3D(center, radList[0], radList[1], radList[2], orient);
        
        BinaryArray3D array = BinaryArray3D.create(50, 50, 50);
        array.fillBooleans((x,y,z) -> refElli.isInside(x, y, z));
        
        Calibration calib = new Calibration(3);
        Ellipsoid3D elli = EquivalentEllipsoid3D.equivalentEllipsoids(array, new int[] {1}, calib)[0];
        
        assertEquals(center.x(), elli.center().x(), 0.1);
        assertEquals(center.y(), elli.center().y(), 0.1);
        assertEquals(center.z(), elli.center().z(), 0.1);
        assertEquals(radList[0], elli.radiusList()[0], 0.1);
        assertEquals(radList[1], elli.radiusList()[1], 0.1);
        assertEquals(radList[2], elli.radiusList()[2], 0.1);
        double[] angles = elli.orientation().eulerAngles();
        assertEquals(refAngles[0], angles[0], 0.02);
        assertEquals(refAngles[1], angles[1], 0.02);
        assertEquals(refAngles[2], angles[2], 0.02);
    }
    
}
