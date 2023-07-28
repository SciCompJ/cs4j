/**
 * 
 */
package net.sci.geom.geom2d.polygon.process;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.DefaultPolygon2D;
import net.sci.geom.geom2d.polygon.Polygon2D;



/**
 * Computes the convex hull of a set of points as a single Polygon2D by gift
 * wrapping algorithm, also known as Jarvis March.
 * </p>
 * 
 * The complexity of the algorithm is of <code>O(n * h)</code>, where
 * <code>n</code> is the number of input points and <code>h</code> the number of
 * vertices of the convex hull. This low complexity makes it a good algorithm
 * for computing convex hull, even if worst case complexity is not optimum.
 * 
 * @author dlegland
 */
public class GiftWrappingConvexHull2D
{
    private static final double TWO_PI = 2 * Math.PI;
 
    public GiftWrappingConvexHull2D()
    {
        
    }
    
    /**
     * Computes the convex hull of a set of points as a single Polygon2D.
     * Current implementation start at the point with lowest y-coord. The points
     * are considered in counter-clockwise order. Result is an instance of
     * SimplePolygon2D. Complexity is O(n*h), with n number of points, h number
     * of points of the hull. Worst case complexity is O(n^2).
     */
    public Polygon2D process(Collection<? extends Point2D> points)
    {
        // Init iteration on points
        Point2D lowestPoint = findLowestRightmostPoint(points);

        // initialize array of points located on convex hull
        ArrayList<Point2D> hullPoints = new ArrayList<Point2D>();

        // Init iteration on points
        Point2D currentPoint = lowestPoint;
        Point2D nextPoint = null;
        double angle = 0;

        // Iterate on point set to find point with smallest angle with respect
        // to previous line
        do 
        {
            hullPoints.add(currentPoint);
            nextPoint = findNextPoint(currentPoint, angle, points);
            angle = computeAngle(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        while (currentPoint != lowestPoint);

        // Create a polygon with points located on the convex hull
        return new DefaultPolygon2D(hullPoints);
    }

    private Point2D findLowestRightmostPoint(Collection<? extends Point2D> points)
    {
        // Init iteration on points
        Point2D lowestPoint = null;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;

        // Iteration on the set of points to find point with lowest y-coord.
        // If two points have same y coord, the one with largest x coord is kept.
        for (Point2D point : points)
        {
            double y = point.y();
            if (y < ymin)
            {
                ymin = y;
                xmax = point.x();
                lowestPoint = point;
            }
            else if (y == ymin)
            {
                double x = point.x();
                if (x > xmax)
                {
                    xmax = x;
                    lowestPoint = point;
                }
            }
        }
        
        return lowestPoint;
    }
    
    private Point2D findNextPoint(Point2D basePoint, double startAngle,
            Collection<? extends Point2D> points)
    {
        Point2D minPoint = null;
        double minAngle = Double.MAX_VALUE;
        double angle;
        
        for (Point2D point : points)
        {
            // Avoid to test same point
            if (basePoint.equals(point))
                continue;
            
            // Compute angle between current direction and next point
            angle = computeAngle(basePoint, point);
            angle = diffAngle(startAngle, angle);
            
            // Keep current point if angle is minimal
            if (angle < minAngle)
            {
                minAngle = angle;
                minPoint = point;
            }
            else if (angle == minAngle && basePoint.distance(point) > basePoint.distance(minPoint))
            {
                // if angle is the same, keep only the furthest point
                minAngle = angle;
                minPoint = point;
            }
        }
        
        return minPoint;
    }
    
    /**
     * Computes the horizontal angle of the straight line going through two
     * points.
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @return the horizontal angle of the straight line going through the two
     *         points, between 0 and 2*PI.
     */
    private double computeAngle(Point2D p1, Point2D p2)
    {
        return (Math.atan2(p2.y() - p1.y(), p2.x() - p1.x()) + TWO_PI) % TWO_PI;
    }
    
    private double diffAngle(double startAngle, double endAngle)
    {
        return (endAngle - startAngle + TWO_PI) % TWO_PI; 
    }
}
