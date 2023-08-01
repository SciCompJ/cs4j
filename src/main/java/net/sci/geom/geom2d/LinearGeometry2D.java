/**
 * 
 */
package net.sci.geom.geom2d;



/**
 * A curve that can be inscribed in a straight line, like a ray, a straight
 * line, or a line segment. 
 * 
 * 
 * @see StraightLine2D
 * @see LineSegment2D
 * 
 * @author dlegland
 *
 */
public interface LinearGeometry2D extends Curve2D
{
    // ===================================================================
    // Static methods

    /**
     * Determines if two linear geometries are parallel up to a default
     * tolerance factor.
     * 
     * @param line1
     *            the first line
     * @param line2
     *            the second line
     * @return true if the two lines are parallel
     */
    public static boolean isParallel(LinearGeometry2D line1, LinearGeometry2D line2)
    {
        Vector2D v1 = line1.direction();
        Vector2D v2 = line2.direction();
        return Vector2D.isParallel(v1, v2);
    }

    /**
     * Determines if two linear geometries are parallel up to the specified
     * tolerance factor.
     * 
     * @param line1
     *            the first line
     * @param line2
     *            the second line
     * @param tol
     *            the tolerance used for testing the product
     * @return true if the two lines are parallel
     */
    public static boolean isParallel(LinearGeometry2D line1, LinearGeometry2D line2, double tol)
    {
        Vector2D v1 = line1.direction();
        Vector2D v2 = line2.direction();
        return Vector2D.isParallel(v1, v2, tol);
    }

    /**
     * Determines if two linear geometries are perpendicular up to a default
     * tolerance factor.
     * 
     * @param line1
     *            the first line
     * @param line2
     *            the second line
     * @return true if the two lines are perpendicular
     */
    public static boolean isPerpendicular(LinearGeometry2D line1, LinearGeometry2D line2)
    {
        Vector2D v1 = line1.direction();
        Vector2D v2 = line2.direction();
        return Vector2D.isPerpendicular(v1, v2);
    }

    /**
     * Determines if two linear geometries are perpendicular up to the specified
     * tolerance factor.
     * 
     * @param line1
     *            the first line
     * @param line2
     *            the second line
     * @param tol
     *            the tolerance used for testing the product
     * @return true if the two lines are perpendicular
     */
    public static boolean isPerpendicular(LinearGeometry2D line1, LinearGeometry2D line2, double tol)
    {
        Vector2D v1 = line1.direction();
        Vector2D v2 = line2.direction();
        return Vector2D.isPerpendicular(v1, v2, tol);
    }

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines, short edges), return null.
     */
    public static Point2D intersection(LinearGeometry2D line1, LinearGeometry2D line2) 
    {
        Vector2D v1 = line1.direction();
        Vector2D v2 = line2.direction();
   
        // return null in case of parallel lines
        if (Vector2D.isParallel(v1, v2))
        {
            return null;
        }
        
        // extract direction vector components
        double dx1 = v1.x();
        double dy1 = v1.y();

        double dx2 = v2.x();
        double dy2 = v2.y();


        Point2D origin1 = line1.origin();
        double x1 = origin1.x();
        double y1 = origin1.y();
        Point2D origin2 = line2.origin();
        double x2 = origin2.x();
        double y2 = origin2.y();
        
        // compute position on the lines
        double denom = dx1 * dy2 - dy1 * dx2;
        double t1 = ((y1 - y2) * dx2 - (x1 - x2) * dy2) / denom;
        if (t1 < line1.t0() || t1 > line1.t1())
        {
            return null;
        }
        double t2 = ((y1 - y2) * dx1 - (x1 - x2) * dy1) / denom;
        if (t2 < line2.t0() || t2 > line2.t1())
        {
            return null;
        }

        // compute position of intersection point
        Point2D point = new Point2D(x1 + t1 * dx1, y1 + t1 * dy1);

        return point;
    }

    
    // ===================================================================
    // Declaration of new methods

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines, short edges), return null.
     * 
     * @param line
     *            the linear geometry to intersect with
     * @return the intersection point if it exists, or null.
     */
    public default Point2D intersection(LinearGeometry2D line) 
    {
        return LinearGeometry2D.intersection(this, line);
    }

//    public double projectedPosition(Point2D point);
//    
    /**
     * Returns true if the orthogonal projection of the specified point is
     * contains within this linear geometry.
     * 
     * @param point
     *            the point to project
     * @param tol
     *            the tolerance for comparing the projected position with
     *            position bounds
     * @return true if this line contains the orthogonal projection of the point
     */
    public boolean containsProjection(Point2D point, double tol);

    /**
     * Returns the position of the point used as origin for this linear
     * geometry.
     * 
     * @return the origin of this linear geometry
     */
    public Point2D origin();
    
    /**
     * @return the direction vector of this geometry
     */
    public Vector2D direction();
    
    /**
     * @return the straight line that contains this geometry
     */
    public StraightLine2D supportingLine();
    
    
    // ===================================================================
    // Specialization of Geometry2D interface

    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public LinearGeometry2D transform(AffineTransform2D trans);
    
    @Override
    public LinearGeometry2D duplicate();
}
