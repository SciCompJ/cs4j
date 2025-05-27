/**
 * 
 */
package net.sci.geom.mesh3d;

import static java.lang.Math.sqrt;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.geom.geom3d.Point3D;

/**
 * @author dlegland
 *
 */
public class Triangle3DTest
{
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.Triangle3D#distance(double, double, double)}.
     */
    @Test
    public final void testDistance_HorizontalTriangle()
    {
        Point3D p1 = new Point3D(10, 10, 10);
        Point3D p2 = new Point3D(20, 10, 10);
        Point3D p3 = new Point3D(10, 20, 10);
        Triangle3D triangle = new Triangle3D(p1, p2, p3);
        
        // Distances around lower left corner
        assertEquals(2, triangle.distance(new Point3D(10,  8, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D( 8, 10, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D(12,  8, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D( 8, 12, 10)), .0001);
        assertEquals(2*sqrt(2), triangle.distance(new Point3D( 8,  8, 10)), .0001);

        // Distances around right corner
        assertEquals(2, triangle.distance(new Point3D(20,  8, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D(18,  8, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D(22, 10, 10)), .0001);
        assertEquals(2*sqrt(2), triangle.distance(new Point3D(22,  8, 10)), .0001);

        // Distances around top corner
        assertEquals(2, triangle.distance(new Point3D( 8, 20, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D( 8, 18, 10)), .0001);
        assertEquals(2, triangle.distance(new Point3D(10, 22, 10)), .0001);
        assertEquals(2*sqrt(2), triangle.distance(new Point3D(8, 22, 10)), .0001);
    }    
}
