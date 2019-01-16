package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;

public class LineSegment2DTest
{
    
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
