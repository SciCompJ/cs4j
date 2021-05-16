/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class Polygons2DTest
{

    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Polygons2D#convexHull(java.util.Collection)}.
     */
    @Test
    public void testConvexHull_FivePoints()
    {
        ArrayList<Point2D> points = new ArrayList<Point2D>(5);
        points.add(new Point2D(15, 15));
        points.add(new Point2D(10, 10));
        points.add(new Point2D(10, 20));
        points.add(new Point2D(20, 10));
        points.add(new Point2D(20, 20));
        
        Polygon2D hull = Polygons2D.convexHull(points);
        
        assertEquals(4, hull.vertexCount());
    }

    /**
     * Test method for {@link net.sci.geom.geom2d.polygon.Polygons2D#convexHull(java.util.Collection)}.
     */
    @Test
    public void testConvexHull_ORourke()
    {
        ArrayList<Point2D> points = new ArrayList<Point2D>(19);
        points.add(new Point2D( 3,  3));
        points.add(new Point2D( 3,  5));
        points.add(new Point2D( 0,  1));
        points.add(new Point2D( 2,  5));
        points.add(new Point2D(-2,  2));
        points.add(new Point2D(-3,  2));
        points.add(new Point2D( 6,  5));
        points.add(new Point2D(-3,  4));
        points.add(new Point2D(-5,  2));
        points.add(new Point2D(-5, -1));
        points.add(new Point2D( 1, -2));
        points.add(new Point2D(-3, -2));
        points.add(new Point2D( 4,  2));
        points.add(new Point2D( 5,  1));
        points.add(new Point2D(-5,  1));
        points.add(new Point2D( 3, -2));
        points.add(new Point2D( 0,  5));
        points.add(new Point2D( 0,  0));
        points.add(new Point2D( 7,  4));
        assertEquals(19, points.size());
                        
        Polygon2D hull = Polygons2D.convexHull(points);
        
        assertEquals(8, hull.vertexCount());
    }

    /**
     *              x--o--x
     *              |     |
     *        x--o--o     o--o--x
     *        |                 |
     *     o--o                 o
     *     |                    |
     *  x--o                    o
     *  |                       |
     *  o                    o--x
     *  |                    |
     *  x--o--o              o
     *        |              |
     *        o           o--o
     *        |           |
     *        o--o        o
     *           |        |
     *           x--o--o--x
     *
     * Test method for {@link net.sci.geom.geom2d.polygon.Polygons2D#convexHull(java.util.Collection)}.
     */
    @Test
    public void testConvexHull_TypicalContourPolygonFromBoundaryImage()
    {
        ArrayList<Point2D> points = new ArrayList<Point2D>(32);
        points.add(new Point2D(50, 90)); points.add(new Point2D(60, 90)); points.add(new Point2D(70, 90));
        points.add(new Point2D(30, 80)); points.add(new Point2D(40, 80)); points.add(new Point2D(50, 80));
        points.add(new Point2D(70, 80)); points.add(new Point2D(80, 80)); points.add(new Point2D(90, 80));
        points.add(new Point2D(20, 70)); points.add(new Point2D(30, 70)); points.add(new Point2D(90, 70));
        points.add(new Point2D(10, 60)); points.add(new Point2D(20, 60)); points.add(new Point2D(90, 60));
        points.add(new Point2D(10, 50)); points.add(new Point2D(80, 50)); points.add(new Point2D(90, 50));
        points.add(new Point2D(10, 40)); points.add(new Point2D(20, 40)); points.add(new Point2D(30, 40)); points.add(new Point2D(80, 40));
        points.add(new Point2D(30, 30)); points.add(new Point2D(70, 30)); points.add(new Point2D(80, 30));
        points.add(new Point2D(30, 20)); points.add(new Point2D(40, 20)); points.add(new Point2D(70, 20));
        points.add(new Point2D(40, 10)); points.add(new Point2D(50, 10)); points.add(new Point2D(60, 10));; points.add(new Point2D(70, 10));
        
        assertEquals(32, points.size());
                        
        Polygon2D hull = Polygons2D.convexHull(points);
        
        assertEquals(9, hull.vertexCount());
    }

}
