package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import org.junit.Test;

public class Vector2DTest
{
    @Test
    public final void testAdd()
    {
        Vector2D v1 = new Vector2D(10, 20);
        Vector2D v2 = new Vector2D(30, 40);
        Vector2D v3 = new Vector2D(40, 60);
        
        double eps = 1e-10;
        assertTrue(v3.almostEquals(v1.plus(v2), eps));
    }
    
    @Test
    public final void testSubtract()
    {
        Vector2D v1 = new Vector2D(10, 20);
        Vector2D v2 = new Vector2D(30, 40);
        Vector2D v3 = new Vector2D(-20, -20);
        
        double eps = 1e-10;
        assertTrue(v3.almostEquals(v1.minus(v2), eps));
    }
    
    @Test
    public final void testMultiply()
    {
        Vector2D v1 = new Vector2D(10, 20);
        double eps = 1e-10;
        
        Vector2D v2 = new Vector2D(30, 60);
        assertTrue(v2.almostEquals(v1.times(3), eps));

        Vector2D v3 = new Vector2D(5, 10);
        assertTrue(v3.almostEquals(v1.times(0.5), eps));
    }
    
    @Test
    public final void testNormalize()
    {
        Vector2D v1 = new Vector2D(2, 3);
        Vector2D v1n = v1.normalize();
        
        Vector2D exp = new Vector2D(2.0 / Math.sqrt(13), 3.0 / Math.sqrt(13));
        double eps = 1e-10;
        assertTrue(exp.almostEquals(v1n, eps));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.Vector2D#dotProduct(net.sci.geom.geom2d.Vector2D, net.sci.geom.geom2d.Vector2D)}.
     */
    @Test
    public final void test_dotProduct_Vector2DVector2D()
    {
        double dot1 = Vector2D.dotProduct(new Vector2D(1, 0), new Vector2D(0, 1));
        assertEquals(0.0, dot1, 0.01);

        double dot2 = Vector2D.dotProduct(new Vector2D(30, 0), new Vector2D(0, 20));
        assertEquals(0.0, dot2, 0.01);

        double dot3 = Vector2D.dotProduct(new Vector2D(3, 4), new Vector2D(4, 5));
        assertEquals(32.0, dot3, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.Vector2D#dotProduct(double, double, double, double)}.
     */
    @Test
    public final void test_dotProduct_DoubleDoubleDoubleDouble()
    {
        double dot1 = Vector2D.dotProduct(1, 0, 0, 1);
        assertEquals(0.0, dot1, 0.01);
        
        double dot2 = Vector2D.dotProduct(30, 0, 0, 20);
        assertEquals(0.0, dot2, 0.01);

        double dot3 = Vector2D.dotProduct(3, 4, 4, 5);
        assertEquals(32.0, dot3, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.Vector2D#crossProduct(net.sci.geom.geom2d.Vector2D, net.sci.geom.geom2d.Vector2D)}.
     */
    @Test
    public final void test_crossProduct_Vector2DVector2D()
    {
        double dot1 = Vector2D.crossProduct(new Vector2D(1, 0), new Vector2D(0, 1));
        assertEquals(1.0, dot1, 0.01);

        double dot2 = Vector2D.crossProduct(new Vector2D(30, 0), new Vector2D(20, 0));
        assertEquals(0.0, dot2, 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.Vector2D#crossProduct(double, double, double, double)}.
     */
    @Test
    public final void test_crossProduct_DoubleDoubleDoubleDouble()
    {
        double dot1 = Vector2D.crossProduct(1, 0, 0, 1);
        assertEquals(1.0, dot1, 0.01);

        double dot2 = Vector2D.crossProduct(30, 0, 20, 0);
        assertEquals(0.0, dot2, 0.01);
    }
}
