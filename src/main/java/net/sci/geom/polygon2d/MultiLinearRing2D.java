/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Boundary2D;

/**
 * An implementation of <code>Boundary2D</code> based on a collection of linear
 * rings. Can be used to represent the boundary of an arbitrary polygonal
 * domain.
 * 
 * @see PolygonalDomain2D
  */
public class MultiLinearRing2D implements Boundary2D
{
    // ===================================================================
    // Static factories
    
    public static final MultiLinearRing2D from(LinearRing2D... contours)
    {
        return new MultiLinearRing2D(Stream.of(contours).collect(Collectors.toList()));
    }
    
    
    // ===================================================================
    // Members
    
    ArrayList<LinearRing2D> rings;
    

    // ===================================================================
    // Constructors
    
    public MultiLinearRing2D()
    {
        this.rings = new ArrayList<>();
    }
    
    public MultiLinearRing2D(Collection<? extends LinearRing2D> rings)
    {
        this.rings = new ArrayList<>(rings);
    }
    
    
    // ===================================================================
    // Management of rings
    
    public void add(LinearRing2D ring)
    {
        this.rings.add(ring);
    }
    
    
    // ===================================================================
    // Implementation of the Boundary2D interface
    
    @Override
    public Collection<? extends LinearRing2D> curves()
    {
        return Collections.unmodifiableList(this.rings);
    }

    @Override
    public double signedDistance(Point2D point)
    {
        return signedDistance(point.x(), point.y());
    }

    @Override
    public double signedDistance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (LinearRing2D ring : rings)
        {
            double dist = ring.signedDistance(x, y);
            if (Math.abs(dist) < Math.abs(minDist))
            {
                minDist = dist;
            }
        }
        
        return minDist;
    }

    @Override
    public boolean isInside(Point2D point)
    {
        return isInside(point.x(), point.y());
    }

    @Override
    public boolean isInside(double x, double y)
    {
        return signedDistance(x, y) <= 0;
    }
    
    @Override
    public Boundary2D duplicate()
    {
        return new MultiLinearRing2D(rings.stream()
                .map(LinearRing2D::duplicate)
                .toList());
    }


    // ===================================================================
    // Implementation of the Geometry2D interface
    
    @Override
    public boolean contains(Point2D point, double eps)
    {
        return distance(point) < eps;
    }

    @Override
    public double distance(double x, double y)
    {
        return this.rings.stream()
                .mapToDouble(c -> c.distance(x, y))
                .min()
                .orElse(Double.NaN);
    }


    // ===================================================================
    // Implementation of the Geometry interface
    
    @Override
    public Bounds2D bounds()
    {
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        for (LinearRing2D ring : rings)
        {
            Bounds2D bounds = ring.bounds();
            xmin = Math.min(xmin, bounds.xMin());
            xmax = Math.max(xmax, bounds.xMax());
            ymin = Math.min(ymin, bounds.yMin());
            ymax = Math.max(ymax, bounds.xMax());
        }
        
        return new Bounds2D(xmin, xmax, ymin, ymax);
    }

    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public MultiLinearRing2D transform(AffineTransform2D trans)
    {
        return new MultiLinearRing2D(rings.stream()
                .map(c -> c.transform(trans))
                .toList());
    }

}
