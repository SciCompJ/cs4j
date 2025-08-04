/**
 * 
 */
package net.sci.geom.geom2d;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 */
public class FeretDiametersTest
{
    
    /**
     * Test method for {@link net.sci.geom.geom2d.FeretDiameters#maxFeretDiameter(java.util.List)}.
     */
    @Test
    public final void test_maxFeretDiameter()
    {
        ArrayList<Point2D> points = pointsWithinLozenge();
        PointPair2D pair = FeretDiameters.maxFeretDiameter(points);
        Point2D p1 = new Point2D(-4, 0);
        Point2D p2 = new Point2D(+4, 0);
        assertTrue(pair.p1.almostEquals(p1, 0.001) || pair.p1.almostEquals(p2, 0.001));
        assertTrue(pair.p2.almostEquals(p1, 0.001) || pair.p2.almostEquals(p2, 0.001));
    }
    
    /**
     * Test method for {@link net.sci.geom.geom2d.FeretDiameters#minFeretDiameter(java.util.List)}.
     */
    @Test
    public final void test_minFeretDiameter()
    {
        ArrayList<Point2D> points = pointsWithinRectangle();
        FeretDiameters.AngleDiameterPair res = FeretDiameters.minFeretDiameter(points);
        assertEquals(4.0, res.diameter, 0.01);
        assertTrue(Math.abs(res.angle - Math.PI/2) < 0.001 || Math.abs(res.angle - 3 * Math.PI/2) < 0.001);
    }
    
    /**
     * Creates an array of points located within an isothetic lozenge with max
     * diameter 8 and min diameter 4.
     * 
     * @return a list of test points
     */
    private static final ArrayList<Point2D> pointsWithinLozenge()
    {
        ArrayList<Point2D> points = new ArrayList<Point2D>(10);
        points.add(new Point2D( 0,  0));
        points.add(new Point2D( 1,  1));
        points.add(new Point2D(-1,  1));
        points.add(new Point2D( 1, -1));
        points.add(new Point2D(-1, -1));
        points.add(new Point2D( 4,  0));
        points.add(new Point2D(-4,  0));
        points.add(new Point2D( 0,  2));
        points.add(new Point2D( 0, -2));
        return points;
    }
    
    /**
     * Creates an array of points located within an isothetic rectangle with
     * side lengths equal to 8 and 4.
     * 
     * @return a list of test points
     */
    private static final ArrayList<Point2D> pointsWithinRectangle()
    {
        ArrayList<Point2D> points = new ArrayList<Point2D>(10);
        points.add(new Point2D( 0,  0));
        points.add(new Point2D( 1,  1));
        points.add(new Point2D(-1,  1));
        points.add(new Point2D( 1, -1));
        points.add(new Point2D(-1, -1));
        points.add(new Point2D( 4,  2));
        points.add(new Point2D(-4,  2));
        points.add(new Point2D( 4, -2));
        points.add(new Point2D(-4, -2));
        return points;
    }
}
