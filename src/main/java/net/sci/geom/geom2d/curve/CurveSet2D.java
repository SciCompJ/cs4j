/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.CurveShape2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A collection of curves that implements the CurveShape2D interface.
 * 
 * @author dlegland
 *
 */
public class CurveSet2D implements CurveShape2D
{
    ArrayList<Curve2D> curves;
    
    public CurveSet2D(Collection<Curve2D> curves)
    {
        this.curves = new ArrayList<Curve2D>(curves.size());
        this.curves.addAll(curves);
    }
    
    public CurveSet2D(Curve2D... curves)
    {
        this.curves = new ArrayList<Curve2D>(curves.length);
        for (Curve2D c : curves)
            this.curves.add(c);
    }
    
    
    @Override
    public Collection<Curve2D> curves()
    {
        return this.curves;
    }
    

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
    public double distance(Point2D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        
        for (Curve2D curve : this.curves)
        {
            minDist = Math.min(minDist,  curve.distance(point));
        }
        return minDist;
    }

    @Override
    public Box2D boundingBox()
    {
        // initialize extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        for (Curve2D curve : curves)
        {
            Box2D box = curve.boundingBox();
            xmin = Math.min(xmin, box.getMinX());
            xmax = Math.max(xmax, box.getMaxX());
            ymin = Math.min(ymin, box.getMinY());
            ymax = Math.max(ymax, box.getMaxY());
        }
        
        return new Box2D(xmin, xmax, ymin, ymax);
    }

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
}
