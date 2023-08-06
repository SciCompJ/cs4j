package net.sci.geom.geom2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.geom.UnboundedGeometryException;

public class StraightLine2DTest
{
    
    @Test
    public final void testPoint()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        double eps = 1e-12;
        
        Point2D point = line.point(3);
        Point2D exp = new Point2D(10, 14);
        assertTrue(exp.almostEquals(point, eps));
    }
    
    @Test
    public final void testProject()
    {
        StraightLine2D line;
        double eps = 1e-12;
        
        Point2D p1 = new Point2D(2, 3);
        Point2D p2 = new Point2D(1, 3);
        Point2D p3 = new Point2D(2, 2);

        line = new StraightLine2D(1, 2, 1, 1);
        assertTrue(line.project(p1).almostEquals(p1, eps));
        assertTrue(line.project(p2).almostEquals(new Point2D(1.5, 2.5), eps));
        assertTrue(line.project(p3).almostEquals(new Point2D(1.5, 2.5), eps));
        
        line = new StraightLine2D(1, 2, -1, -1);
        assertTrue(line.project(p1).almostEquals(p1, eps));
        assertTrue(line.project(p2).almostEquals(new Point2D(1.5, 2.5), eps));
        assertTrue(line.project(p3).almostEquals(new Point2D(1.5, 2.5), eps));
        
        line = new StraightLine2D(1, 2, 1, 0);
        assertTrue(line.project(p1).almostEquals(p3, eps));
        assertTrue(line.project(p2).almostEquals(new Point2D(1, 2), eps));
        assertTrue(line.project(p3).almostEquals(p3, eps));

        line = new StraightLine2D(1, 2, -1, 0);
        assertTrue(line.project(p1).almostEquals(p3, eps));
        assertTrue(line.project(p2).almostEquals(new Point2D(1, 2), eps));
        assertTrue(line.project(p3).almostEquals(p3, eps));

        line = new StraightLine2D(1, 2, 0, 1);
        assertTrue(line.project(p1).almostEquals(p2, eps));
        assertTrue(line.project(p2).almostEquals(p2, eps));
        assertTrue(line.project(p3).almostEquals(new Point2D(1, 2), eps));

        line = new StraightLine2D(1, 2, 0, -1);
        assertTrue(line.project(p1).almostEquals(p2, eps));
        assertTrue(line.project(p2).almostEquals(p2, eps));
        assertTrue(line.project(p3).almostEquals(new Point2D(1, 2), eps));
    }
    
    @Test
    public final void testOrigin()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        double eps = 1e-12;
        
        Point2D origin = line.origin();
        
        assertTrue(new Point2D(1, 2).almostEquals(origin, eps));
    }
    
    @Test
    public final void testDirection()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        double eps = 1e-12;
        
        Vector2D vect = line.direction();
        
        assertTrue(new Vector2D(3, 4).almostEquals(vect, eps));
    }
    
    @Test
    public final void testContains()
    {
        StraightLine2D line;
        double eps = 1e-12;
        
        line = new StraightLine2D(1, 2, 1, 1);
        assertTrue(line.contains(new Point2D(2, 3), eps));
        assertTrue(!line.contains(new Point2D(1, 3), eps));
        assertTrue(!line.contains(new Point2D(2, 2), eps));   
        assertTrue(!line.contains(new Point2D(0, 0), eps));
            
        line = new StraightLine2D(1, 2, 1, 0);
        assertTrue(!line.contains(new Point2D(1, 3), eps));
        assertTrue(line.contains(new Point2D(2, 2), eps));
        assertTrue(!line.contains(new Point2D(1, 1), eps));

        line = new StraightLine2D(1, 2, 0, 1);
        assertTrue(line.contains(new Point2D(1, 3), eps));
        assertTrue(!line.contains(new Point2D(0, 2), eps));
        assertTrue(!line.contains(new Point2D(2, 2), eps));       
    }
    
    @Test
    public final void testDistance()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        Point2D pt;
        
        // test origin point
        pt = new Point2D(1, 2);
        assertEquals(0, line.distance(pt), 1e-14);
        
        // point on the line (positive extent)
        pt = new Point2D(1+1.5*3, 2+1.5*4);
        assertEquals(0, line.distance(pt), 1e-14);
        
        // point on the line (negative extent)
        pt = new Point2D(1-1.5*3, 2-1.5*4);
        assertEquals(0, line.distance(pt), 1e-14);
        
        // point outside the line
        pt = new Point2D(5, -1);
        assertEquals(5, line.distance(pt), 1e-14);  
        
        // point outside the line, in the other side
        pt = new Point2D(-3, 5);
        assertEquals(5, line.distance(pt), 1e-14);  
    }
    
    @Test(expected=UnboundedGeometryException.class)
    public final void testBoundingBox()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        line.bounds();
    }
    
    @Test
    public final void testIsBounded()
    {
        StraightLine2D line;
        
        line = new StraightLine2D(1, 2, 1, 1);
        assertFalse(line.isBounded());
    }
    
    @Test
    public final void testSignedDistance()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        Point2D pt;

        // point outside the line, in "Right" side
        pt = new Point2D(5, -1);
        assertEquals(5, line.signedDistance(pt), 1e-14);  
        
        // point outside the line, in "Left" side
        pt = new Point2D(-3, 5);
        assertEquals(-5, line.signedDistance(pt), 1e-14);  
    }
    
    @Test
    public final void testIsInside()
    {
        StraightLine2D line = new StraightLine2D(1, 2, 3, 4);
        Point2D pt;

        // point outside the domain bounded by the line
        pt = new Point2D(5, -1);
        assertFalse(line.isInside(pt));  
        
        // point inside the domain bounded by the line
        pt = new Point2D(-3, 5);
        assertTrue(line.isInside(pt));  
    }
}
