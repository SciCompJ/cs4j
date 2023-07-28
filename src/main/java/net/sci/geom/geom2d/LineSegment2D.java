/**
 * 
 */
package net.sci.geom.geom2d;

/**
 * A line segment between two extremity points.
 * 
 * @see StraightLine2D
 * 
 * @author dlegland
 */
public class LineSegment2D implements LinearGeometry2D
{
    // =============================================================
    // class variables

    private double x1;
    private double y1;
    private double x2;
    private double y2;
 
    
    // =============================================================
    // Constructor
    
    public LineSegment2D(Point2D p1, Point2D p2)
    {
        this.x1 = p1.x();
        this.y1 = p1.y();
        this.x2 = p2.x();
        this.y2 = p2.y();
    }
    
    
    // =============================================================
    // General methods
    
    public double length()
    {
        return Math.hypot(x2 - x1, y2 - y1);
    }
    
    
    // =============================================================
    // Accessors
    
    public Point2D getP1()
    {
        return new Point2D(x1, y1);
    }
    
    public Point2D getP2()
    {
        return new Point2D(x2, y2);
    }

    
    // ===================================================================
    // Implementation of the LinearGeometry interface 
    
    /**
	 * Compute the orthogonal projection of the input point onto this line segment.
	 * 
	 * @param point
	 *            the point to project.
	 * @return the position of the projected point.
	 */
    public Point2D projection(Point2D point)
	{
      double dx = x2 - x1;
      double dy = y2 - y1;
      if (Math.hypot(dx, dy) < Geometry2D.MIN_VECTOR_NORM)
      {
          throw new RuntimeException("The direction vector of the line has too small norm.");
      }

      double denom = dx * dx + dy * dy;
      double t = ((point.y - y1) * dy + (point.x - x1) * dx) / denom;
      
      // clamp between 0 and 1
      t = Math.min(Math.max(t, 0), 1);
      
      return new Point2D(x1 + t * dx, y1 + t * dy);
	}


	@Override
    public boolean containsProjection(Point2D point, double tol)
    {
        double pos = supportingLine().projectedPosition(point);
        return pos >= 0-tol && pos <= 1+tol;
    }

    /**
     * Transforms this line segment with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line segment
     */
    @Override
    public LineSegment2D transform(AffineTransform2D trans)
    {
        return new LineSegment2D(getP1().transform(trans), getP2().transform(trans));
    }

    /**
     * Returns the origin point of this line.
     */
    public Point2D origin() 
    {
        return new Point2D(this.x1, this.y1);
    }

    /**
     * Returns the direction vector of this line.
     */
    public Vector2D direction() 
    {
        return new Vector2D(this.x2 - this.x1, this.y2 - this.y1);
    }


    @Override
    public StraightLine2D supportingLine()
    {
        return new StraightLine2D(this.x1, this.y1, this.x2 - this.x1,  this.y2 - this.y1);
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    /**
     * Returns the point at the specified position using the parametric
     * representation of this line.
     * 
     * @param t the position on the line
     * @return the point located at specified position
     */
    @Override
    public Point2D getPoint(double t)
    {
        t = Math.min(Math.max(t, 0), 1);
        double x = this.x1 + t * (this.x2 - this.x1);
        double y = this.y1 + t * (this.y2 - this.y1);
        return new Point2D(x, y);
    }

    @Override
    public double getT0()
    {
        return 0;
    }

    @Override
    public double getT1()
    {
        return 1;
    }

    @Override
    public boolean isClosed()
    {
        return false;
    }
    
    
    // ===================================================================
    // Implements the Geometry2D interface

    public boolean contains(Point2D point, double eps) 
    {
        if (!supportContains(point, eps))
            return false;

        // compute position on the support line
        double t = positionOnLine(point.x(), point.y());

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
     * @see StraightLine2D.contains(Point2D, double)
     */
    private boolean supportContains(Point2D point, double eps) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        
        double denom = Math.hypot(dx, dy);
        if (denom < eps)
        {
            throw new DegeneratedLine2DException(this);
        }
        
        double x = point.x();
        double y = point.y();
        return Math.sqrt(Math.abs((x - this.x1) * dy - (y - this.y1) * dx)) / denom < eps;
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
    private double positionOnLine(double x, double y) 
    {
        double dx = this.x2 - this.x1;
        double dy = this.y2 - this.y1;
        double denom = dx * dx + dy * dy;
        return ((y - this.y1) * dy + (x - this.x1) * dx) / denom;
    }

    @Override
    public double distance(double x, double y)
    {
        // In case of line segment with same extremities, computes distance to initial point 
        if (length() < 100 * Double.MIN_VALUE)
        {
            return Math.hypot(this.x1 - x, this.y1 - y);
        }
        
        // compute position on the supporting line
        StraightLine2D line = this.supportingLine();
        double t = line.projectedPosition(x, y);

        // clamp with parameterization bounds of edge
        t = Math.max(Math.min(t, 1), 0);
        
        // compute position of projected point on the edge
        Point2D proj = line.getPoint(t);
        
        // return distance to projected point
        return proj.distance(x, y);
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
    public Bounds2D bounds()
    {
        return new Bounds2D(getP1(), getP2());
    }
    
    @Override
    public LineSegment2D duplicate()
    {
        return new LineSegment2D(new Point2D(x1, y1), new Point2D(x2, y2));
    }
}
