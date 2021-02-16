/**
 * 
 */
package net.sci.geom.geom3d;

import net.sci.geom.UnboundedGeometryException;

/**
 * A 3D straight line, with infinite bounds in each direction.
 * 
 * @author dlegland
 *
 */
public class StraightLine3D implements LinearGeometry3D
{
    // ===================================================================
    // class variables

    /**
     * Coordinates of starting point of the line
     */
    protected double x0, y0, z0;

    /**
     * Direction vector of the line. dx, dy and dz should not be all zero.
     */
    protected double dx, dy, dz;


    // ===================================================================
    // Constructors

    public StraightLine3D(Point3D p1, Point3D p2)
    {
        this.x0 = p1.getX();
        this.y0 = p1.getY();
        this.z0 = p1.getZ();
        this.dx = p2.getX() - this.x0;
        this.dy = p2.getY() - this.y0;
        this.dz = p2.getZ() - this.z0;
    }
    
    public StraightLine3D(Point3D origin, Vector3D direction)
    {
        this.x0 = origin.getX();
        this.y0 = origin.getY();
        this.z0 = origin.getZ();
        this.dx = direction.getX();
        this.dy = direction.getY();
        this.dz = direction.getZ();
    }
    
    public StraightLine3D(double x0, double y0, double z0, double dx, double dy, double dz)
    {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
    

    // ===================================================================
    // Methods specific to StraightLine3D 
    
    /**
     * Computes the coordinates of the projection of the specified point on this
     * line.
     * 
     * @param point
     *            a point
     * @return the projection of the point on this line
     */
    public Point3D project(Point3D point)
    {
        // compute position on the line
        double t = projectedPosition(point);

        // compute position of intersection point
        return new Point3D(x0 + t * dx, y0 + t * dy, z0 + dz * t);
    }

    public double projectedPosition(Point3D point)
    {
        double denom = dx * dx + dy * dy + dz * dz;
//        if (Math.abs(denom) < Shape2D.ACCURACY)
//            throw new DegeneratedLine2DException(this);
        return ((point.getY() - y0) * dy + (point.getX() - x0) * dx + (point.getZ() - z0) * dz) / denom;
    }
    
    public double projectedPosition(double x, double y, double z)
    {
        double denom = dx * dx + dy * dy + dz * dz;
//        if (Math.abs(denom) < Shape2D.ACCURACY)
//            throw new DegeneratedLine2DException(this);
        return ((y - y0) * dy + (x - x0) * dx + (z - z0) * dz) / denom;
    }
    
    
    // ===================================================================
    // Implementation of the LinearGeometry3D interface 

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.line.LinearGeometry3D#origin()
     */
    @Override
    public Point3D origin()
    {
        return new Point3D(x0, y0, z0);
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.line.LinearGeometry3D#direction()
     */
    @Override
    public Vector3D direction()
    {
        return new Vector3D(dx, dy, dz);
    }

    @Override
    public StraightLine3D supportingLine()
    {
        return this;
    }


    // ===================================================================
    // Implementation of the Curve3D interface 

    /**
     * Returns the point at the specified position using the parametric
     * representation of this line.
     * 
     * @param t
     *            the position on the line
     * @return the point located at specified position
     */
    public Point3D getPoint(double t)
    {
        return new Point3D(x0 + dx * t, y0 + dy * t, z0 + dz * t);
    }

    @Override
    public double getT0()
    {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getT1()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public boolean isClosed()
    {
        return false;
    }

    
    // ===================================================================
    // Implementation of the Geometry3D interface 

    @Override
    public boolean contains(Point3D point, double eps)
    {
        return project(point).distance(point) < eps;
    }

    @Override
    public double distance(double x, double y, double z)
    {
        return project(new Point3D(x, y, z)).distance(x, y, z);
    }
    
    /**
     * Transforms this straight line with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed straight line
     */
    @Override
    public StraightLine3D transform(AffineTransform3D trans)
    {
        return new StraightLine3D(trans.transform(origin()), trans.transform(direction()));
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Bounds3D bounds()
    {
        throw new UnboundedGeometryException(this);
    }

    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return false;
    }
}
