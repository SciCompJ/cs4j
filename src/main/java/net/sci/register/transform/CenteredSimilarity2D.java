/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Transform2D;

/**
 * Transformation model for a centered similarity: rotation+scaling around the
 * specified center, followed by a translation.
 * 
 * @author dlegland
 *
 */
public class CenteredSimilarity2D implements Transform2D
{
    public double centerX = 0.0;
    public double centerY = 0.0;
    
    public double shiftX = 0.0;
    public double shiftY = 0.0;
    public double angleDeg = 0.0;
    public double logScaling = 0.0;
    

    public CenteredSimilarity2D(Point2D center, double logScaling, double angleInDegrees, double tx, double ty)
    {
        this.centerX = center.x();
        this.centerY = center.y();
        this.logScaling = logScaling;
        this.angleDeg = angleInDegrees;
        this.shiftX = tx;
        this.shiftY = ty;
    }
    
    @Override
    public Point2D transform(Point2D point)
    {
        // recenter wrt to transform center
        double xc = point.x() - centerX;
        double yc = point.y() - centerY;
        
        // apply scaling
        double k = Math.pow(2, logScaling);
        double xcs = k * xc;
        double ycs = k * yc;
        
        // apply rotation
        double theta = Math.toRadians(angleDeg);
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double xcsr = xcs * cot - ycs * sit;
        double ycsr = xcs * sit + ycs * cot;
        
        // apply translation and recenter to global center
        return new Point2D(xcsr + shiftX + centerX, ycsr + shiftY + centerY);
    }

}
