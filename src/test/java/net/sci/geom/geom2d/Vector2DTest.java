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
    
}
