/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.CurveShape2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A collection of curves that implements the CurveShape2D interface.
 * 
 * @author dlegland
 *
 */
public class MultiCurve2D implements CurveShape2D
{
    // ===================================================================
    // Class variables
    
    ArrayList<Curve2D> curves;

    
    // ===================================================================
    // Constructors
    
    public MultiCurve2D(Collection<? extends Curve2D> curves)
    {
        this.curves = new ArrayList<Curve2D>(curves);
    }
    
    public MultiCurve2D(Curve2D... curves)
    {
        this.curves = new ArrayList<Curve2D>(curves.length);
        for (Curve2D c : curves)
        {
            this.curves.add(c);
        }
    }
    
    
    // ===================================================================
    // Implementation of CurveShape2D interface
    
    @Override
    public Collection<Curve2D> curves()
    {
        return this.curves;
    }
    
    /**
     * Returns the result of the given transformation applied to this curve shape.
     * 
     * @param trans
     *            the transformation to apply
     * @return the transformed geometry
     */
    public MultiCurve2D transform(AffineTransform2D trans)
    {
        ArrayList<Curve2D> newCurves = new ArrayList<Curve2D>(this.curves.size());
        for (Curve2D curve : this.curves)
        {
            newCurves.add(curve.transform(trans));
        }
        return new MultiCurve2D(newCurves);
    }

    
    // ===================================================================
    // Geometry2D interface
    
    @Override
    public boolean contains(Point2D point, double eps)
    {
        for (Curve2D curve : this.curves)
        {
            if (!curve.contains(point, eps))
                return true;
        }
        return false;
    }

    @Override
    public double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        for (Curve2D curve : this.curves)
        {
            minDist = Math.min(minDist,  curve.distance(x, y));
        }
        return minDist;
    }

    @Override
    public Bounds2D bounds()
    {
        // initialize extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        for (Curve2D curve : curves)
        {
            Bounds2D box = curve.bounds();
            xmin = Math.min(xmin, box.xMin());
            xmax = Math.max(xmax, box.xMax());
            ymin = Math.min(ymin, box.yMin());
            ymax = Math.max(ymax, box.yMax());
        }
        
        return new Bounds2D(xmin, xmax, ymin, ymax);
    }

    /**
     * @return true only if all the curves contained within this MultiCurve2D
     *         instance are unbounded.
     */
    @Override
    public boolean isBounded()
    {
        for (Curve2D curve : this.curves)
        {
            if (!curve.isBounded())
                return false;
        }
        return true;
    }

    /**
     * Duplicates this MultiCurve2D by creating a new multi curve with all the
     * duplicated curves.
     * 
     * @return a deep copy of this MultiCurve2D.
     */
    @Override
    public MultiCurve2D duplicate()
    {
        ArrayList<Curve2D> curves2 = new ArrayList<>(curves.size());
        for (Curve2D curve : curves)
        {
            curves2.add(curve.duplicate());
        }
        return new MultiCurve2D(curves2);
    }
}
