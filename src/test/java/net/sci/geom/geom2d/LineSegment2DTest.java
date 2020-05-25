package net.sci.geom.geom2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LineSegment2DTest
{
    @Test
    public void testProjection_Point2D()
    {
        Point2D src = new Point2D(10, 10);
        Point2D tgt = new Point2D(70, 40);
        LineSegment2D seg = new LineSegment2D(src, tgt);
        
        Point2D p1 = new Point2D(20, 40);
        Point2D p2 = new Point2D(60, 10);
        Point2D p3 = new Point2D(00, 20);
        Point2D p4 = new Point2D(80, 60);
        
        Point2D proj1 = seg.projection(p1);
        Point2D proj2 = seg.projection(p2);
        Point2D proj3 = seg.projection(p3);
        Point2D proj4 = seg.projection(p4);
        
        assertEquals(0.0, new Point2D(30, 20).distance(proj1), 0.01);
        assertEquals(0.0, new Point2D(50, 30).distance(proj2), 0.01);
        assertEquals(0.0, src.distance(proj3), 0.01);
        assertEquals(0.0, tgt.distance(proj4), 0.01);
    }

    @Test
    public void testLength()
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(10+30, 10+40);
        LineSegment2D line = new LineSegment2D(p1, p2);
        
        double lineLength = line.length();
        
        assertEquals(50, lineLength, .1);
    }
    
}
