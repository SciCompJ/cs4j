/**
 * 
 */
package net.sci.geom.geom3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 */
public class Vector3DTest
{
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#dotProduct(net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void test_dotProductVector3DVector3D()
    {
        Vector3D vx1 = new Vector3D(1, 0, 0);
        Vector3D vy1 = new Vector3D(0, 1, 0);
        Vector3D vz1 = new Vector3D(0, 0, 1);
        Vector3D vx2 = new Vector3D(5, 0, 0);
        Vector3D vy2 = new Vector3D(0, 4, 0);
        Vector3D vz2 = new Vector3D(0, 0, 3);
        
        assertEquals(0.0, Vector3D.dotProduct(vx1, vy1), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(vx1, vz1), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(vy1, vz1), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(vx2, vy2), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(vx2, vz2), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(vy2, vz2), 0.01);

        double dot3 = Vector3D.dotProduct(new Vector3D(2, 3, 4), new Vector3D(3, 4, 5));
        double exp = 2*3 + 3*4 + 4*5;
        assertEquals(exp, dot3, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#dotProduct(double, double, double, double, double, double)}.
     */
    @Test
    public final void test_dotProduct_double()
    {
        assertEquals(0.0, Vector3D.dotProduct(1, 0, 0, 0, 1, 0), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(1, 0, 0, 0, 0, 1), 0.01);
        assertEquals(0.0, Vector3D.dotProduct(0, 1, 0, 0, 0, 1), 0.01);
        
        assertEquals(38.0, Vector3D.dotProduct(2, 3, 4, 3, 4, 5), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#crossProduct(net.sci.geom.geom3d.Vector3D, net.sci.geom.geom3d.Vector3D)}.
     */
    @Test
    public final void test_crossProduct_Vector3DVector3D()
    {
        Vector3D v0 = new Vector3D();
        assertTrue(v0.almostEquals(Vector3D.crossProduct(new Vector3D(5, 0, 0), new Vector3D(3, 0, 0)), 0.01));
        assertTrue(v0.almostEquals(Vector3D.crossProduct(new Vector3D(0, 4, 0), new Vector3D(0, 2, 0)), 0.01));
        assertTrue(v0.almostEquals(Vector3D.crossProduct(new Vector3D(0, 0, 4), new Vector3D(0, 0, 3)), 0.01));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#crossProduct(double, double, double, double, double, double)}.
     */
    @Test
    public final void test_crossProduct_double()
    {
        Vector3D v0 = new Vector3D();
        assertTrue(v0.almostEquals(Vector3D.crossProduct(5, 0, 0, 3, 0, 0), 0.01));
        assertTrue(v0.almostEquals(Vector3D.crossProduct(0, 4, 0, 0, 2, 0), 0.01));
        assertTrue(v0.almostEquals(Vector3D.crossProduct(0, 0, 4, 0, 0, 3), 0.01));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#norm()}.
     */
    @Test
    public final void test_norm()
    {
        double norm1 = new Vector3D(2, 3, 6).norm();
        assertEquals(7.0, norm1, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom3d.Vector3D#normalize()}.
     */
    @Test
    public final void test_normalize()
    {
        Vector3D v1 = new Vector3D(5, 4, 3);
        Vector3D v1n = v1.normalize();
        assertEquals(1.0, v1n.norm(), 0.01);
    }
}
