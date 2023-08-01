/**
 * 
 */
package net.sci.geom.geom3d;

/**
 * A 3D line segment defined by its two extremity points.
 * 
 * @see net.sci.geom.geom2d.cs4j.geom.geom2d.line.LineSegment2D
 * @see StraightLine3D
 * 
 * @author dlegland
 */
public class LineSegment3D implements LinearGeometry3D
{
    // =============================================================
    // class variables

    private double x1;
    private double y1;
    private double z1;
    private double x2;
    private double y2;
    private double z2;
 
    
    // =============================================================
    // Constructor
    
    public LineSegment3D(Point3D p1, Point3D p2)
    {
        this.x1 = p1.x();
        this.y1 = p1.y();
        this.z1 = p1.z();
        this.x2 = p2.x();
        this.y2 = p2.y();
        this.z2 = p2.z();
    }
    
    private LineSegment3D(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    
    // =============================================================
    // General methods
    
    public double length()
    {
        return Math.hypot(Math.hypot(x2 - x1, y2 - y1), z2- z1);
    }
    
    
    // =============================================================
    // Accessors
    
    public Point3D getP1()
    {
        return new Point3D(x1, y1, z1);
    }
    
    public Point3D getP2()
    {
        return new Point3D(x2, y2, z2);
    }

    /**
     * Computes the intersection point of this 3D line with a plane.
     * 
     * @param plane
     *            the plane to intersect
     * @return the intersection point of this line with the given plane
     */
    public Point3D intersection(Plane3D plane)
    {
        if (Vector3D.isPerpendicular(direction(), plane.normal()))
        {
            return null;
        }
        Point3D inter = plane.intersection(this.supportingLine());
        double pos = positionOnLine(inter.x(), inter.y(), inter.z());
        
        double eps = 1e-12;
        if (pos <  -eps || pos > 1+eps)
            return null;
        return inter;
    }
    
    public boolean intersects(Plane3D plane)
    {
        // plane normal
        Vector3D n = plane.normal();

        // difference between origins of line segment and plane
        Vector3D dp = new Vector3D(this.origin(), plane.origin()); 

        // dot product of line segment direction with plane normal
        double denom = Vector3D.dotProduct(n, this.direction());

        // relative position of the intersection point on supporting line
        // (can be inf in case of a line parallel to the plane)
        double pos = Vector3D.dotProduct(n, dp) / denom;

        // check whether intersection lies within [0 1] interval, with tolerance
        double eps = 1e-12;
        return pos >= -eps && pos < 1+eps;
    }

    
    // ===================================================================
    // Implementation of the LinearGeometry interface 

    /**
     * Returns the origin point of this line.
     * 
     * @return the origin of this line
     */
    public Point3D origin() 
    {
        return new Point3D(this.x1, this.y1, this.z1);
    }

    /**
     * Returns the direction vector of this line.
     * 
     * @return the direction vector of this line
     */
    public Vector3D direction() 
    {
        return new Vector3D(this.x2 - this.x1, this.y2 - this.y1, this.z2 - this.z1);
    }


    @Override
    public StraightLine3D supportingLine()
    {
        return new StraightLine3D(this.x1, this.y1, this.z1, this.x2 - this.x1, this.y2 - this.y1, this.z2 - this.z1);
    }

    /**
     * Transforms this line segment with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line segment
     */
    @Override
    public LineSegment3D transform(AffineTransform3D trans)
    {
        return new LineSegment3D(getP1().transform(trans), getP2().transform(trans));
    }


    // ===================================================================
    // Methods implementing the Curve3D interface
    
    /**
     * Returns the point at the specified position using the parametric
     * representation of this line segment.
     * 
     * @param t
     *            the position on the line segment, between 0 and 1
     * @return the point located at specified position
     */
    public Point3D getPoint(double t)
    {
        // clamp to [0 , 1]
        t = Math.min(Math.max(t, 0), 1);
        
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return new Point3D(x1 + dx * t, y1 + dy * t, z1 + dz * t);
    }

    @Override
    public double t0()
    {
        return 0;
    }

    @Override
    public double t1()
    {
        return 1;
    }

    @Override
    public boolean isClosed()
    {
        return false;
    }
    
    
    // ===================================================================
    // Implements the Geometry3D interface

    public boolean contains(Point3D point, double eps) 
    {
        if (!supportContains(point, eps))
            return false;

        // compute position on the support line
        double t = positionOnLine(point.x(), point.y(), point.z());

        if (t < -eps)
            return false;
        if (t - 1 > eps)
            return false;

        return true;
    }

    /**
     * Returns true if the specified point lies on the line covering the object,
     * with the given precision.
     * 
     * @see StraightLine2D.contains(Point3D, double)
     */
    private boolean supportContains(Point3D point, double eps) 
    {
        return supportingLine().contains(point, eps);
//        double dx = this.x2 - this.x1;
//        double dy = this.y2 - this.y1;
//        
//        double denom = Math.hypot(dx, dy);
//        if (denom < eps)
//        {
//            throw new DegeneratedLine2DException(this);
//        }
//        
//        double x = point.getX();
//        double y = point.getY();
//        return Math.sqrt(Math.abs((x - this.x1) * dy - (y - this.y1) * dx)) / denom < eps;
    }

    /**
     * Computes position on the line of the given point. 
     * The position is the number t such that if the point
     * belong to the line, it location is given by x=x0+t*dx and y=y0+t*dy.
     * <p>
     * If the point does not belong to the line, the method returns the position
     * of its projection on the line.
     * 
     * Assumes a non-degenerated line.
     */
    private double positionOnLine(double x, double y, double z) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        double dz = this.z2 - this.z1;
        double denom = dx * dx + dy * dy + dz * dz;
        return ((x - this.x1) * dx + (y - this.y1) * dy + (z - this.z1) * dz) / denom;
    }

    @Override
    public double distance(double x, double y, double z)
    {
        // In case of line segment with same extremities, computes distance to initial point 
        if (length() < 10 * Double.MIN_VALUE)
        {
            return Math.hypot(Math.hypot(this.x1 - x, this.y1 - y), this.z1 - z);
        }
        
        // compute position on the supporting line
        StraightLine3D line = this.supportingLine();
        double t = line.projectedPosition(x, y, z);

        // clamp with parameterization bounds of edge
        t = Math.max(Math.min(t, 1), 0);
        
        // compute position of projected point on the edge
        Point3D proj = line.getPoint(t);
        
        // return distance to projected point
        return proj.distance(x, y, z);
    }

    
    // ===================================================================
    // Implements the Geometry interface

    /**
     * Returns true, as a line segment is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Bounds3D bounds()
    {
        return new Bounds3D(getP1(), getP2());
    }

    @Override
    public LineSegment3D duplicate()
    {
        return new LineSegment3D(x1, y1, z1, x2, y2, z2);
    }
}
