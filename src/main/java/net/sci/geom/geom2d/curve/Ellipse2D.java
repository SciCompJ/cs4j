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

import java.util.ArrayList;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * An ellipse, defined by a center, two semi-axis lengths, and one orientation angle in degrees.
 * 
 * @author dlegland
 *
 */
public class Ellipse2D implements Contour2D
{
    // ===================================================================
    // Static methods
    
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
        double[][] mat = trans.getMatrix();
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
        delta = delta * delta;

        double A2 = (A * d * d + C * b * b - B * b * d) / delta;
        double B2 = (B * (a * d + b * c) - 2 * (A * c * d + C * a * b)) / delta;
        double C2 = (A * c * c + C * a * a - B * a * c) / delta;

        // return only 3 parameters if needed
        if (coefs.length==3)
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
    private static Ellipse2D reduceCentered(double[] coefs)
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

    
    // ===================================================================
    // Constructors
    
    /**
     * Define center by point, major and minor semi axis lengths, and
     * orientation angle.
     */
    public Ellipse2D(Point2D center, double r1, double r2, double theta)
    {
        this(center.getX(), center.getY(), r1, r2, theta);
    }
    
    /**
     * Define center by coordinate, major and minor semi axis lengths, and
     * orientation angle.
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
     * Converts this ellipse into a new LinearRing2D with the specified number of vertices.
     * 
     * @param nVertices the number of vertices of the created linear ring
     * @return a new instance of LinearRing2D
     */
    public LinearRing2D asPolyline(int nVertices)
    {
        double thetaRad = Math.toRadians(this.theta);
        double cost = Math.cos(thetaRad);
        double sint = Math.sin(thetaRad);
        double dt = Math.toRadians(360.0 / nVertices);
        
        ArrayList<Point2D> vertices = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            double x = Math.cos(i * dt) * this.r1;
            double y = Math.sin(i * dt) * this.r2;
            double x2 = x * cost - y * sint + this.xc;
            double y2 = x * sint + y * cost + this.yc;
            vertices.add(new Point2D(x2, y2));
        }
        
        return new LinearRing2D(vertices);
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
    /* @eturn the orientation of major semi-axis, in degrees, between 0 and 180.
     */
    public double orientation()
    {
        return theta;
    }
    
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    public double signedDistance(Point2D point)
    {
        // TODO: use exact computation 
        return this.asPolyline(200).signedDistance(point.getX(), point.getY());
    }

    public double signedDistance(double x, double y)
    {
        // TODO: use exact computation 
        return this.asPolyline(200).signedDistance(x, y);
    }

    public boolean isInside(Point2D point)
    {
    	return isInside(point.getX(), point.getY());
    }

    public boolean isInside(double x, double y)
    {
    	return quasiDistanceToCenter(x, y) <= 1;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public Point2D getPoint(double t)
    {
        // pre-copute rotation coefficients
        double thetaRad = Math.toRadians(this.theta);
        double cot = Math.cos(thetaRad);
        double sit = Math.sin(thetaRad);

        // position for a centered and axis-aligned ellipse
        double x0 = this.r1 * cos(t);
        double y0 = this.r2 * sin(t);
        
        //apply rotation and translatino
        double x = x0 * cot - y0 * sit + this.xc;
        double y = x0 * sit + y0 * cot + this.yc;

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
        result.xc = center.getX();
        result.yc = center.getY();
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
    	double rho = quasiDistanceToCenter(point.getX(), point.getY());    	
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
        double cost = Math.cos(thetaRad);
        double sint = Math.sin(thetaRad);
        
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
        // TODO Auto-generated method stub
        return asPolyline(200).distance(x, y);
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
    public Box2D boundingBox()
    {
        // TODO could be more precise
        return asPolyline(200).boundingBox();
    }
}
