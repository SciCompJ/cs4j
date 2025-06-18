/**
 * 
 */
package net.sci.geom.geom2d.curve;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.PrincipalAxes2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;

/**
 * An ellipse, defined by a center, two semi-axis lengths, and an orientation
 * angle in degrees.
 * 
 * @author dlegland
 *
 * @see Circle2D
 */
public class Ellipse2D implements Contour2D
{
    // ===================================================================
    // Constants
    
    private static final double SQRT_2 = Math.sqrt(2);
    
    
    // ===================================================================
    // Static methods
    
    /**
     * Creates a new instance of Ellipse2D enclosed within the bounding box
     * defined by two corner points.
     * 
     * @param p1
     *            the first corner point
     * @param p2
     *            the second corner point
     * @return a new Ellipse2D fully enclosed within the bounds of the two
     *         points
     */
    public static final Ellipse2D fromCorners(Point2D p1, Point2D p2)
    {
        return fromCorners(p1.x(), p1.y(), p2.x(), p2.y());
    }

    /**
     * Creates a new instance of Ellipse2D based on the coordinates of two
     * points delimiting the bounding box of the ellipse.
     * 
     * @param x1
     *            the x-coordinate of the first corner point
     * @param y1
     *            the y-coordinate of the first corner point
     * @param x2
     *            the x-coordinate of the second corner point
     * @param y2
     *            the y-coordinate of the second corner point
     * @return a new Ellipse2D fully enclosed within the bounds of the two
     *         points
     */
    public static final Ellipse2D fromCorners(double x1, double y1, double x2, double y2)
    {
        // compute ellipse parameters
        double xc = (x1 + x2) * 0.5;
        double yc = (y1 + y2) * 0.5;
        double ra = Math.abs(x2 - x1) * 0.5;
        double rb = Math.abs(y2 - y1) * 0.5;
        double theta = 0;
        
        // ensure ra >= rb
        if (ra < rb)
        {
            double tmp = ra;
            ra = rb;
            rb = tmp;
            theta = 90;
        }
        return new Ellipse2D(xc, yc, ra, rb, theta);
    }

    /**
     * Creates a new Ellipse2D instance from a center and the unique
     * coefficients of the inertia matrix. The diagonal coefficients of the
     * inertia matrix are provided first.
     * 
     * @param center
     *            the center of the ellipse
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @return the corresponding Ellipse2D
     */
    public static final Ellipse2D fromInertiaCoefficients(Point2D center, double Ixx, double Iyy, double Ixy)
    {
        // compute ellipse semi-axis lengths
        double common = sqrt((Ixx - Iyy) * (Ixx - Iyy) + 4 * Ixy * Ixy);
        double ra = SQRT_2 * sqrt(Ixx + Iyy + common);
        double rb = SQRT_2 * sqrt(Ixx + Iyy - common);

        // compute ellipse angle and convert into degrees
        double theta = Math.toDegrees(atan2(2 * Ixy, Ixx - Iyy) / 2);
        
        // concatenate into an instance of Ellipse2D
        return new Ellipse2D(center, ra, rb, theta);
    }
    
    /**
     * Reduces the parameters of a PrincipalAxes instances into an Ellipse2D.
     * 
     * @param axes
     *            the instance of PrincipalAxes2D to convert
     * @return the equivalent ellipse
     */
    public Ellipse2D fromAxes(PrincipalAxes2D axes)
    {
        double[] sca = axes.scalings();
        double theta = Math.toDegrees(axes.rotationAngle());
        return new Ellipse2D(axes.center(), sca[0], sca[1], theta);
    }
    
    /**
     * Transform an ellipse, by supposing both the ellipse is centered and the
     * transform has no translation part.
     * 
     * @param ellipse an ellipse
     * @param trans an affine transform
     * @return the transformed ellipse, centered around origin
     */
    private static Ellipse2D transformCentered(Ellipse2D ellipse, AffineTransform2D trans) {
        // Extract inner parameter of ellipse
        double r1 = ellipse.r1;
        double r2 = ellipse.r2;
        double theta = Math.toRadians(ellipse.theta);

        // precompute some parts
        double r1Sq = r1 * r1;
        double r2Sq = r2 * r2;
        double cot = cos(theta);
        double sit = sin(theta);
        double cotSq = cot * cot;
        double sitSq = sit * sit;

        // compute coefficients of the centered conic
        double A = cotSq / r1Sq + sitSq / r2Sq;
        double B = 2 * cot * sit * (1 / r1Sq - 1 / r2Sq);
        double C = cotSq / r2Sq + sitSq / r1Sq;
        double[] coefs = new double[] { A, B, C };

        // Compute coefficients of the transformed conic
        double[] coefs2 = transformCenteredConicCoefficients(coefs, trans);

        // reduce conic coefficients to Ellipse
        return Ellipse2D.reduceCentered(coefs2);
    }

    /**
     * Transforms a conic centered around the origin, by dropping the
     * translation part of the transform. The array must be contains at least
     * 3 elements. If it contains 6 elements, the 3 remaining elements are
     * supposed to be 0, 0, and -1 in that order.
     * 
     * @param coefs an array of double with at least 3 coefficients
     * @param trans an affine transform
     * @return an array of double with as many elements as the input array
     */
    private final static double[] transformCenteredConicCoefficients(double[] coefs, AffineTransform2D trans)
    {
        // Extract transform coefficients
        double[][] mat = trans.affineMatrix();
        double a = mat[0][0];
        double b = mat[1][0];
        double c = mat[0][1];
        double d = mat[1][1];

        // Extract first conic coefficients
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];

        // compute matrix determinant
        double delta = a * d - b * c;
        double denom = 1.0 / ( delta * delta);

        double A2 = (A * d * d + C * b * b - B * b * d) * denom;
        double B2 = (B * (a * d + b * c) - 2 * (A * c * d + C * a * b)) * denom;
        double C2 = (A * c * c + C * a * a - B * a * c) * denom;

        // return only 3 parameters if needed
        if (coefs.length == 3)
            return new double[] { A2, B2, C2 };

        // Compute other coefficients
        double D = coefs[3];
        double E = coefs[4];
        double F = coefs[5];
        double D2 = D * d - E * b;
        double E2 = E * a - D * c;
        return new double[] { A2, B2, C2, D2, E2, F };
    }

    /**
     * Creates a new Ellipse by reducing the conic coefficients, assuming conic
     * type is ellipse, and ellipse is centered.
     * 
     * @param coefs an array of double with at least 3 coefficients containing
     *            coefficients for x^2, x*y, and y^2 factors.
     * @return the Ellipse2D corresponding to given coefficients
     */
    private static final Ellipse2D reduceCentered(double[] coefs)
    {
        double A = coefs[0];
        double B = coefs[1];
        double C = coefs[2];
        
        // Compute orientation angle of the ellipse
        double theta;
        if (abs(A - C) < 1e-10)
        {
            theta = PI / 4;
        }
        else
        {
            theta = atan2(B, (A - C)) / 2.0;
            if (B < 0)
                theta -= PI;
            theta = formatAngle(theta);
        }
        
        // compute ellipse in isothetic basis
        double[] coefs2 = transformCenteredConicCoefficients(coefs, AffineTransform2D.createRotation(-theta));
        
        // extract coefficients f if present
        double f = 1;
        if (coefs2.length > 5)
            f = abs(coefs[5]);
        
        assert abs(coefs2[1] / f) < 1e-10 : "Second conic coefficient should be zero";
        
        // extract major and minor axis lengths, ensuring r1 is greater
        double r1, r2;
        if (coefs2[0] < coefs2[2])
        {
            r1 = sqrt(f / coefs2[0]);
            r2 = sqrt(f / coefs2[2]);
        }
        else
        {
            r1 = sqrt(f / coefs2[2]);
            r2 = sqrt(f / coefs2[0]);
            theta = formatAngle(theta + PI / 2);
            theta = Math.min(theta, formatAngle(theta + PI));
        }
        
        // return the reduced ellipse
        return new Ellipse2D(0, 0, r1, r2, Math.toDegrees(theta));
    }
    
    private static final double formatAngle(double angle)
    {
        angle = angle % (Math.PI * 2);
        if (angle < 0)
        {
            angle += (Math.PI * 2);
        }
        return angle;
    }
    

    // ===================================================================
    // Class variables
    
    /** X-coordinate of the center. */
    protected double  xc;

    /** Y-coordinate of the center. */
    protected double  yc;

    /** Length of semi-major axis. Must be positive. */
    protected double  r1;
    
    /** Length of semi-minor axis. Must be positive. */
    protected double  r2;

    /** Orientation of major semi-axis, in degrees, between 0 and 180. */
    protected double  theta  = 0;

    /**
     * Private instance of Polyline2D used to approximate computation of
     * distances, insideness... Lazy loading.
     */
    private LinearRing2D ring = null;
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Define center by point, major and minor semi axis lengths, and
     * orientation angle.
     * 
     * @param center
     *            the center of the ellipse
     * @param r1
     *            the length of the semi-major axis
     * @param r2
     *            the length of the semi-minor axis
     * @param theta
     *            the orientation of the ellipse, in degrees, counter-clockwise.
     */
    public Ellipse2D(Point2D center, double r1, double r2, double theta)
    {
        this(center.x(), center.y(), r1, r2, theta);
    }
    
    /**
     * Define center by coordinate, major and minor semi axis lengths, and
     * orientation angle.
     * 
     * @param xc
     *            the x-coordinate of ellipse center
     * @param yc
     *            the y-coordinate of ellipse center
     * @param r1
     *            the length of the semi-major axis
     * @param r2
     *            the length of the semi-minor axis
     * @param theta
     *            the orientation of the ellipse, in degrees, counter-clockwise.
     */
    public Ellipse2D(double xc, double yc, double r1, double r2, double theta)
    {
        this.xc = xc;
        this.yc = yc;
        this.r1 = r1;
        this.r2 = r2;
        this.theta = theta;
    }

    // ===================================================================
    // Specific methods
    
    /**
     * Converts this ellipse into a new LinearRing2D with the specified number
     * of vertices.
     * 
     * @param nVertices
     *            the number of vertices of the created linear ring
     * @return a new instance of LinearRing2D
     */
    public LinearRing2D asPolyline(int nVertices)
    {
        double thetaRad = Math.toRadians(this.theta);
        double cost = cos(thetaRad);
        double sint = sin(thetaRad);
        double dt = Math.toRadians(360.0 / nVertices);
        
        LinearRing2D res = LinearRing2D.create(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            double x = cos(i * dt) * this.r1;
            double y = sin(i * dt) * this.r2;
            double x2 = x * cost - y * sint + this.xc;
            double y2 = x * sint + y * cost + this.yc;
            res.addVertex(new Point2D(x2, y2));
        }
        
        return res;
    }

    /**
     * Computes the area of this ellipse, by multiplying the semi axis lengths
     * by PI.
     * 
     * @return the area of this ellipse.
     * @see net.sci.geom.geom2d.curve.Circle2D#area()
     */
    public double area()
    {
        return this.r1 * this.r2 * Math.PI;
    }
    
    public Point2D center()
    {
        return new Point2D(xc, yc);
    }
    
    /** 
     * @return the length of the semi-major axis.
     */
    public double semiMajorAxisLength()
    {
        return r1;
    }
    
    /** 
     * @return the length of the semi-minor axis.
     */
    public double semiMinorAxisLength()
    {
        return r2;
    }
    
    /**
     * Returns the orientation of the ellipse, in degrees. 
     * 
     * @return the orientation of major semi-axis, in degrees, between 0 and 180.
     */
    public double orientation()
    {
        return theta;
    }
    
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    public double signedDistance(Point2D point)
    {
        // TODO: could be more precise
        ensurePolylineExist();
        return ring.signedDistance(point.x(), point.y());
    }

    public double signedDistance(double x, double y)
    {
        // TODO: could be more precise
        ensurePolylineExist();
        return ring.signedDistance(x, y);
    }

    public boolean isInside(Point2D point)
    {
    	return isInside(point.x(), point.y());
    }

    public boolean isInside(double x, double y)
    {
    	return quasiDistanceToCenter(x, y) <= 1;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public Point2D point(double t)
    {
        // pre-copute rotation coefficients
        double thetaRad = Math.toRadians(this.theta);
        double cot = cos(thetaRad);
        double sit = sin(thetaRad);

        // position for a centered and axis-aligned ellipse
        double x0 = this.r1 * cos(t);
        double y0 = this.r2 * sin(t);
        
        //apply rotation and translatino
        double x = x0 * cot - y0 * sit + this.xc;
        double y = x0 * sit + y0 * cot + this.yc;

        return new Point2D(x, y);
    }

    @Override
    public double t0()
    {
        return 0;
    }

    @Override
    public double t1()
    {
        return 2 * Math.PI;
    }

    @Override
    public boolean isClosed()
    {
        return true;
    }
    
    @Override
    public Ellipse2D transform(AffineTransform2D trans)
    {
        Ellipse2D result = Ellipse2D.transformCentered(this, trans);
        Point2D center = this.center().transform(trans);
        result.xc = center.x();
        result.yc = center.y();
        return result;
    }


    // ===================================================================
    // Methods implementing the Geometry2D interface
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom2d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
    	double rho = quasiDistanceToCenter(point.x(), point.y());    	
        return Math.abs(rho - 1) <= eps;
    }
    
    /**
	 * Apply to the point the transform that transforms this ellipse into unit
	 * circle, and computes distance to origin.
	 * 
	 * @param x
	 *            the x-coordinate of the point
	 * @param y
	 *            the y-coordinate of the point
	 * @return the distance of the transformed point to the origin
	 */
    private double quasiDistanceToCenter(double x, double y)
    {
    	// recenter point
    	x -= this.xc;
    	y -= this.yc;
    	
    	// pre-computes trigonometric values
    	double thetaRad = Math.toRadians(this.theta);
        double cost = cos(thetaRad);
        double sint = sin(thetaRad);
        
        // orient along main axes
    	double x2 = x * cost + y * sint;
        double y2 = -x * sint + y * cost;
        
        // and divides by semi axes length
        x2 /= this.r1;
        y2 /= this.r2;
    	
        return Math.hypot(x2, y2);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#distance(double, double)
     */
    @Override
    public double distance(double x, double y)
    {
        double[] normCoords = toAlignedEllipse(new double[] {x, y});
        // restrict to first quadrant
        normCoords[0] = Math.abs(normCoords[0]);
        normCoords[1] = Math.abs(normCoords[1]);
        
        if (this.r1 >= this.r2)
        {
            return distancePointEllipse(this.r1, this.r2, normCoords[0], normCoords[1]);
        }
        else
        {
            // use symmetric wrt to xy-diagonal
            return distancePointEllipse(this.r2, this.r1, normCoords[1], normCoords[0]);
        }
    }
    
    /**
     * Computes the coordinates in the space of the ellipse centered, aligned with x-axis, and with same size.
     * @param coords the coordinates in the original space
     * @return the coordinates in normalized space
     */
    private double[] toAlignedEllipse(double[] coords)
    {
        // compute translated coordinates
        double xt = coords[0] - this.xc;  
        double yt = coords[1] - this.yc;
        
        // compute rotation coefficients
        double thetaRadians = Math.toRadians(-this.theta);
        double cot = Math.cos(thetaRadians);
        double sit = Math.sin(thetaRadians);
        
        return new double[] {
                xt * cot - yt * sit, 
                xt * sit + yt * cot, 
        };
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#boundingBox()
     */
    @Override
    public Bounds2D bounds()
    {
        // TODO could be more precise
        ensurePolylineExist();
        return ring.bounds();
    }
    
    @Override
    public Ellipse2D duplicate()
    {
        return new Ellipse2D(xc, yc, r1, r2, theta);
    }
    
    private void ensurePolylineExist()
    {
        if (this.ring == null)
        {
            ring = asPolyline(120);
        }
    }
    
    /**
     * Computes the distance of a point to an ellipse, in normalized coordinates
     * (centered ellipse with largest axis oriented along the x-axis). Assumes
     * e0 > e1.
     *
     * @param e0
     *            the semi-axis length along the x-axis
     * @param e1
     *            the semi-axis length along the y-axis
     * @param y0
     *            the x-coordinate of the query point
     * @param y1
     *            the y-coordinate of the query point
     * @return the distance of the point to the ellipse
     */
    private static final double distancePointEllipse(double e0, double e1, double y0, double y1)
    {
        double distance;
        double x0, x1;
        
        if (y1 > 0)
        {
            if (y0 > 0)
            {
                // need to compute the unique root "tbar" of F(t) within (-e1*e1, infinity)
                
                // small change of variable
                double z0 = y0 / e0;
                double z1 = y1 / e1;
                double g = z0 * z0 + z1 * z1 - 1;
                if (g != 0)
                {
                    double r0 = (e0 / e1) * (e0 / e1);
                    double sbar = findRoot(r0, z0, z1, g);
                    x0 = r0 * y0 / (sbar + r0);
                    x1 = y1 / (sbar + 1);
                    distance = Math.hypot(x0 - y0, x1 - y1);
                }
                else
                {
                    x0 = y0;
                    x1 = y1;
                    distance = 0;
                }
            }
            else
            {
                // process  case y0 == 0
                x0 = y0;
                x1 = e1;
                distance = Math.abs(y1 - e1);
            }
        }
        else
        {
            // process case y1 == 0
            double numer0 = e0 * y0;
            double denom0 = e0 * e0 - e1 * e1;
            if (numer0 < denom0)
            {
                double xde0 = numer0 / denom0;
                x0 = e0 * xde0;
                x1 = e1 * Math.sqrt(1 - xde0 * xde0);
                distance = Math.hypot(x0 - y0, x1);
            }
            else
            {
                x0 = e0;
                x1 = 0;
                distance = Math.abs(y0 - e0);
            }
        }
        
        return distance;
    }
    
    /**
     * Robust root finder based on bisection method. Code from David Eberly, in
     * "Distance from a Point to an Ellipse, an Ellipsoid, or a Hyperellipsoid".
     * 
     * Function to solve:
     * <pre>
     *            e0 z0             e1 z1 
     * F(t) = ( ---------- )^2 + (---------- )^2 - 1 = 0 
     *          t + e0^2           t + e1^2
     * </pre>
     * 
     * @param r0
     *            the ratio of squared extent, equal to (e0^2 / e1^2).
     * @param z0
     *            the first coordinate
     * @param z1
     *            the second coordinate
     * @param g
     *            quantity related to (equal to?) the signed distance
     * @return the value of {@code t} such that {@code F(t) = 0}.
     */
    private static final double findRoot(double r0, double z0, double z1, double g)
    {
        // First transform the problem into  the following equation:
        //      
        // G(s) = ( r0 * z0 / (s + r0) )^2  + ( z1 / (s + 1))^2 - 1 
        // with s ranging from -1 to infinity
        
        // compute interval containing the root
        double n0 = r0 * z0;
        double s0 = z1 - 1;
        double s1 = g < 0 ? 0 : Math.hypot(n0, z1) - 1;
        
        // the result value
        double s = 0;
        
        // iterate. Use a fixed iteration number, but 
        // see Eberly's paper for number of iterations
        final int maxIters = 100;
        for (int i = 0; i < maxIters; i++)
        {
            // cut in the middle of the interval
            s = (s0 + s1) * 0.5;
            if (s == s0 || s == s1) break;
            
            
            double ratio0 = n0 / (s + r0);
            double ratio1 = z1 / (s + 1);
            g = ratio0 * ratio0 + ratio1 * ratio1 - 1;
            if (g > 0)
            {
                s0 = s;
            }
            else if (g < 0)
            {
                s1 = s;
            }
            else
            {
                break;
            }
        }
        return s;
    }
    
}
