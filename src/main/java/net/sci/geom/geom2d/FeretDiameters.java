/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.List;

import net.sci.geom.polygon2d.Polygon2D;
import net.sci.geom.polygon2d.Polygons2D;

/**
 * Utility methods for computing minimal and maximal Feret diameters.
 * 
 * @author dlegland
 */
public class FeretDiameters
{
    /**
     * Computes Maximum Feret diameter of a set of points.
     * 
     * Note: it is often a good idea to compute convex hull before computing
     * Feret diameter.
     * 
     * @param points
     *            a collection of planar points
     * @return the maximum Feret diameter of the point set
     * 
     * @see net.sci.geom.polygon2d.Polygons2D#convexHull(java.util.Collection)
     */
    public final static PointPair2D maxFeretDiameter(List<Point2D> points)
    {
        double distMax = Double.NEGATIVE_INFINITY;
        PointPair2D maxDiam = null;
        
        // iterate over all pairs of points
        int n = points.size();
        for (int i1 = 0; i1 < n - 1; i1++)
        {
            Point2D p1 = points.get(i1);
            for (int i2 = i1 + 1; i2 < n; i2++)
            {
                Point2D p2 = points.get(i2);
                
                // keep the pair with largest distance
                double dist = p1.distance(p2);
                if (dist > distMax)
                {
                    maxDiam = new PointPair2D(p1, p2);
                    distMax = dist;
                }
            }
        }
        
        return maxDiam;
    }
    
    /**
     * Computes Minimum Feret diameter of a set of points and returns both the
     * diameter and the corresponding angle.
     * 
     * First computes convex hull of the input points, then uses a naive
     * algorithm with complexity of O(n^2).
     *      
     * @param points
     *            a collection of planar points
     * @return the minimum Feret diameter of the point set
     * 
     * @see net.sci.geom.polygon2d.Polygons2D#convexHull(java.util.Collection)
     */
    public final static AngleDiameterPair minFeretDiameter(List<Point2D> points)
    {
        // first compute convex hull to simplify
        Polygon2D convexHull = Polygons2D.convexHull(points);
        List<Point2D> vertices = convexHull.vertexPositions();
        int n = vertices.size();
        
        // initialize result
        double widthMin = Double.POSITIVE_INFINITY;
        double angleMin = 0;
        StraightLine2D line;
        
        // iterate over edges of the polygon
        for (int iEdge = 0; iEdge < n; iEdge++)
        {
            Point2D p1 = vertices.get(iEdge);
            Point2D p2 = vertices.get((iEdge + 1) % n);
            
            // avoid degenerated lines
            if (p1.distance(p2) < 1e-12)
            {
                continue;
            }
            
            // Compute the width for this polygon edge
            line = new StraightLine2D(p1, p2);
            double width = 0;
            for (Point2D p : vertices)
            {
                double dist = line.distance(p);
                width = Math.max(width, dist);
            }
            
            // check if smallest width
            if (width < widthMin)
            {
                widthMin = width;
                double dx = p2.x() - p1.x();
                double dy = p2.y() - p1.y();
                angleMin = Math.atan2(dy, dx);
            }
        }
        
        return new AngleDiameterPair(angleMin - Math.PI / 2, widthMin);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private FeretDiameters()
    {
    }
    
    /**
     * Data structure used to return result of minimum Feret diameter
     * computation.
     * 
     * @author dlegland
     */
    public static class AngleDiameterPair
    {
        /** Angle in radians */
        public double angle;
        
        /** Diameter computed in the direction of the angle */
        public double diameter;
        
        /**
         * Default constructor, using angle in degrees and diameter.
         * 
         * @param angle
         *            the orientation angle, in degrees
         * @param diameter
         *            the diameter along the direction
         */
        public AngleDiameterPair(double angle, double diameter)
        {
            this.angle = angle;
            this.diameter = diameter;
        }
    }
}
