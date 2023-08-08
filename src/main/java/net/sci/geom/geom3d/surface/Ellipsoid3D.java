/**
 * 
 */
package net.sci.geom.geom3d.surface;

import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Geometry3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.PrincipalAxes3D;
import net.sci.geom.geom3d.Rotation3D;


/**
 * A 3D ellipsoid, defined by a center, three semi-axis lengths, and a 3D
 * rotation.
 * 
 * @author dlegland
 *
 */
public class Ellipsoid3D implements Geometry3D
{
    // ===================================================================
    // Static methods
    
    /**
     * Creates a new Ellipsoid3D instance from a center and the unique
     * coefficients of the inertia matrix. The diagonal coefficients of the
     * inertia matrix are provided first.
     * 
     * Internally rely on a conversion into a PrincipalAxes instance, then from
     * principal axes to ellipsoid.
     * 
     * @param center
     *            the center of the ellipsoid
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Izz
     *            the second-order inertia coefficient along the z axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @param Ixz
     *            the second-order inertia coefficient along the (xz) diagonal
     *            axis
     * @param Iyz
     *            the second-order inertia coefficient along the (yz) diagonal
     *            axis
     * @return the corresponding Ellipsoid3D
     */
    public static final Ellipsoid3D fromInertiaCoefficients(Point3D center, double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz)
    {
        return fromAxes(PrincipalAxes3D.fromInertiaCoefficients(center, Ixx, Iyy, Izz, Ixy, Ixz, Iyz));
    }
    
    /**
     * Creates the Ellipsoid3D with parameters that corresponds to that of the
     * specified PrincipalAxes object.
     * 
     * @param axes
     *            an instance of PrincipalAxes3D
     * @return the corresponding ellipsoid
     */
    public static final Ellipsoid3D fromAxes(PrincipalAxes3D axes)
    {
        // retrieve and convert features
        Point3D center = axes.center();
        Rotation3D orient = Rotation3D.fromMatrix(axes.rotationMatrix());
        
        // convert axis scalings to ellipsoid radii 
        double[] values = axes.scalings();
        double r1 = sqrt(5) * values[0];
        double r2 = sqrt(5) * values[1];
        double r3 = sqrt(5) * values[2];

        // concatenate into a new ellipsoid
        return new Ellipsoid3D(center, r1, r2, r3, orient);
    }
    
    
    // ===================================================================
    // Class variables

    /**
     * The center of the ellipsoid.
     */
    final Point3D center;
    
    /**
     * The length of the first ellipsoid semi-axis.
     */
    final double r1;
    /**
     * The length of the second ellipsoid semi-axis.
     */
    final double r2;
    /**
     * The length of the third ellipsoid semi-axis.
     */
    final double r3;

    /**
     * The first rotation applied to the ellipsoid, around the X-axis, in degrees.
     */
    final double eulerAngleX; 
    /**
     * The second  rotation applied to the ellipsoid, around the Y-axis, in degrees.
     */
    final double eulerAngleY; 
    /**
     * The third rotation applied to the ellipsoid, around the Z-axis, in degrees.
     */
    final double eulerAngleZ;
    
    
    // ===================================================================
    // Constructors

    /**
     * Creates a new ellipsoid aligned with the main axes.
     * 
     * @param center
     *            the center of the ellipsoid
     * @param r1
     *            the length of the first ellipsoid semi-axis
     * @param r2
     *            the length of the second ellipsoid semi-axis
     * @param r3
     *            the length of the third ellipsoid semi-axis
     */
    public Ellipsoid3D(Point3D center, double r1, double r2, double r3)
    {
        this.center = center;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.eulerAngleX = 0.0;
        this.eulerAngleY = 0.0;
        this.eulerAngleZ = 0.0;
    }

    /**
     * Creates a new ellipsoid with a specific orientation given by a Rotation3D
     * instance.
     * 
     * @param center
     *            the center of the ellipsoid
     * @param r1
     *            the length of the first ellipsoid semi-axis
     * @param r2
     *            the length of the second ellipsoid semi-axis
     * @param r3
     *            the length of the third ellipsoid semi-axis
     * @param orientation
     *            the 3D orientation of the ellipsoid around its center
     */
    public Ellipsoid3D(Point3D center, double r1, double r2, double r3, Rotation3D orientation)
    {
        this.center = center;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        double[] angles = orientation.eulerAngles();
        this.eulerAngleX = Math.toDegrees(angles[0]);
        this.eulerAngleY = Math.toDegrees(angles[1]);
        this.eulerAngleZ = Math.toDegrees(angles[2]);
    }

    /**
     * Creates a new ellipsoid with a specific orientation given by three Euler
     * angles (in degrees).
     * 
     * @param center
     *            the center of the ellipsoid
     * @param r1
     *            the length of the first ellipsoid semi-axis
     * @param r2
     *            the length of the second ellipsoid semi-axis
     * @param r3
     *            the length of the third ellipsoid semi-axis
     * @param eulerAngleX
     *            the first rotation applied to the ellipsoid, around the
     *            X-axis, in degrees.
     * @param eulerAngleY
     *            the second rotation applied to the ellipsoid, around the
     *            Y-axis, in degrees.
     * @param eulerAngleZ
     *            the third rotation applied to the ellipsoid, around the
     *            Z-axis, in degrees.
     */
    public Ellipsoid3D(Point3D center, double r1, double r2, double r3, double eulerAngleX, double eulerAngleY, double eulerAngleZ)
    {
        this.center = center;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.eulerAngleX = eulerAngleX;
        this.eulerAngleY = eulerAngleY;
        this.eulerAngleZ = eulerAngleZ;
    }
    
    /**
     * Creates a new ellipsoid with a specific orientation given by three Euler
     * angles (in degrees).
     * 
     * @param centerX
     *            the x-coordinate of ellipsoid center
     * @param centerY
     *            the y-coordinate of ellipsoid center
     * @param centerZ
     *            the z-coordinate of ellipsoid center
     * @param r1
     *            the length of the first ellipsoid semi-axis
     * @param r2
     *            the length of the second ellipsoid semi-axis
     * @param r3
     *            the length of the third ellipsoid semi-axis
     * @param eulerAngleX
     *            the first rotation applied to the ellipsoid, around the
     *            X-axis, in degrees.
     * @param eulerAngleY
     *            the second rotation applied to the ellipsoid, around the
     *            Y-axis, in degrees.
     * @param eulerAngleZ
     *            the third rotation applied to the ellipsoid, around the
     *            Z-axis, in degrees.
     */
    public Ellipsoid3D(double centerX, double centerY, double centerZ, double r1, double r2, double r3, double eulerAngleX, double eulerAngleY, double eulerAngleZ)
    {
        this.center = new Point3D(centerX, centerY, centerZ);
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.eulerAngleX = eulerAngleX;
        this.eulerAngleY = eulerAngleY;
        this.eulerAngleZ = eulerAngleZ;
    }

    
    // ===================================================================
    // Methods specific to Ellipsoid3D
    
    /**
     * Returns the center of this ellipsoid as a Point3D.
     * 
     * @return the center of the ellipsoid.
     */
    public Point3D center()
    {
        return this.center;
    }
    
    public double[] radiusList()
    {
        return new double[] {r1, r2, r3};
    }
    
    /**
     * Returns the orientation of this ellipsoid, as a Rotation3D object. 
     * 
     * @return the orientation of the ellipsoid.
     */
    public Rotation3D orientation()
    {
        return Rotation3D.fromEulerAngles(Math.toRadians(eulerAngleX), Math.toRadians(eulerAngleY), Math.toRadians(eulerAngleZ));
    }
    

    // ===================================================================
    // Methods that mimics the Boundary2D interface

    /**
     * Returns the signed distance of the point to this boundary.
     * 
     * Let <em>dist</em> be the distance of the point to the curve. The signed
     * distance is defined by:
     * <ul>
     * <li>-dist if the point is inside the region defined by the boundary</li>
     * <li>+dist if the point is outside the region.</li>
     * </ul>
     * 
     * @param point
     *            a point in the plane
     * @return the signed distance of the point to the boundary
     * 
     * @see net.sci.geom.geom2d.Geometry2D#distance(Point2D)
     */
    public double signedDistance(Point3D point)
    {
        return distance(point) * (isInside(point) ? -1 : +1);
    }
    
    /**
     * Returns the signed distance of the point to this boundary.
     * 
     * Let <em>dist</em> be the distance of the point to the curve. The signed
     * distance is defined by:
     * <ul>
     * <li>-dist if the point is inside the region defined by the boundary</li>
     * <li>+dist if the point is outside the region.</li>
     * </ul>
     * 
     * @param x
     *            the x-coordinate of the query point
     * @param y
     *            the y-coordinate of the query point
     * @return the signed distance of the point to the boundary
     * 
     * @see net.sci.geom.geom2d.Geometry2D#distance(Point2D)
     */
    public double signedDistance(double x, double y, double z)
    {
        return distance(x, y, z) * (isInside(x, y, z) ? -1 : +1);
    }

    /**
     * Checks if the specified point is contained within the domain bounded by
     * this boundary.
     * 
     * @param point
     *            the point to test
     * @return true is the point is within the domain corresponding to this
     *         boundary.
     */
    public boolean isInside(Point3D point)
    {
        return globalToLocalTransform().transform(point).distance(0, 0, 0) <= 1;
    }

    /**
     * Checks if the specified point is contained within the domain bounded by
     * this boundary.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return true is the point is within the domain corresponding to this
     *         boundary.
     */
    public boolean isInside(double x, double y, double z)
    {
        return globalToLocalTransform().transform(new Point3D(x, y, z)).distance(0, 0, 0) <= 1;
    }
    
    
    // ===================================================================
    // Implementation of the Geometry3D interface

    @Override
    public double distance(double x, double y, double z)
    {
        // use a discrete approximation of the ellipsoid
        Collection<Point3D> verts = surfaceVertices(240, 120);
        
        // initialize distance
        double dist = Double.POSITIVE_INFINITY;
        
        // iterate over vertices
        for (Point3D point : verts)
        {
            dist = Math.min(dist, point.distance(x, y, z));
        }
        
        // concatenate into a new Bounds3D object
        return dist;
    }

    @Override
    public Bounds3D bounds()
    {
        // use a discrete approximation of the ellipsoid
        return Bounds3D.of(surfaceVertices(240, 120));
    }

    @Override
    public boolean contains(Point3D point, double eps)
    {
        Point3D point2 = globalToLocalTransform().transform(point);
        return Math.abs(point2.distance(0, 0, 0) - 1.0) < eps;
    }
    
    
    // ===================================================================
    // Implementation of the Geometry interface

    @Override
    public Geometry3D duplicate()
    {
        return new Ellipsoid3D(center, r1, r2, r3, eulerAngleX, eulerAngleY, eulerAngleZ);
    }

    /**
     * Returns true, as an ellipsoid is always bounded.
     * 
     * @return true.
     * 
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }
    
    
    // ===================================================================
    // Private computation methods
    
    private Collection<Point3D> surfaceVertices(int nPhi, int nTheta)
    {
        // pre-compute angle values for phi
        double[] phiList = new double[nPhi+1];
        for (int i = 0; i <= nPhi; i++)
        {
            phiList[i] = i * 2.0 * Math.PI / (double) nPhi;
        }
        
        // pre-compute sin and cos values for theta
        double[] sinTheta = new double[nTheta+1];
        double[] cosTheta = new double[nTheta+1];
        for (int i = 0; i <= nTheta; i++)
        {
            double theta = (double) i * Math.PI / (double) nTheta;
            sinTheta[i] = Math.sin(theta);
            cosTheta[i] = Math.cos(theta);
        }
        
        // retrieve coordinates transform
        AffineTransform3D transfo = localToGlobalTransform();
        
        // allocate memory
        ArrayList<Point3D> res = new ArrayList<Point3D>((nTheta+1) * (nPhi+1));
        
        // iterate over pairs of spherical coordinates
        for (int iPhi = 0; iPhi <= nPhi; iPhi++)
        {
            // pre-compute trigonometric projections of phi
            double cosPhi = Math.cos(phiList[iPhi]);
            double sinPhi = Math.sin(phiList[iPhi]);
            
            // process current "meridian"
            for (int iTheta = 0; iTheta <= nTheta; iTheta++)
            {
                double sit = sinTheta[iTheta];
                double cot = cosTheta[iTheta];
                res.add(transfo.transform(new Point3D(cosPhi * sit, sinPhi * sit, cot)));
            }
        }
        
        return res;
    }
    
    private AffineTransform3D localToGlobalTransform()
    {
        AffineTransform3D sca = AffineTransform3D.createScaling(r1, r2, r3);
        AffineTransform3D rot = AffineTransform3D.fromMatrix(orientation().affineMatrix());
        AffineTransform3D tra = AffineTransform3D.createTranslation(center);
        return tra.concatenate(rot).concatenate(sca);
    }
    
    private AffineTransform3D globalToLocalTransform()
    {
        AffineTransform3D tra = AffineTransform3D.createTranslation(-center.x(), -center.y(), -center.z());
        AffineTransform3D rot = AffineTransform3D.fromMatrix(orientation().inverse().affineMatrix());
        AffineTransform3D sca = AffineTransform3D.createScaling(1.0 / r1, 1.0 / r2, 1.0 / r3);
        return sca.concatenate(rot).concatenate(tra);
    }

}
