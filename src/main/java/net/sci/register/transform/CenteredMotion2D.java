/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Transform2D;

/**
 * @author dlegland
 *
 */
public class CenteredMotion2D implements Transform2D
{
    public double centerX = 0.0;
    public double centerY = 0.0;
    
    public double shiftX = 0.0;
    public double shiftY = 0.0;
    public double angleDeg = 0.0;

    public CenteredMotion2D(Point2D center, double angleInDegrees, double tx, double ty)
    {
        this.centerX = center.x();
        this.centerY = center.y();
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
        
        // apply rotation
        double theta = Math.toRadians(angleDeg);
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double xcsr = xc * cot - yc * sit;
        double ycsr = xc * sit + yc * cot;
        
        // apply translation and recenter to global center
        return new Point2D(xcsr + shiftX + centerX, ycsr + shiftY + centerY);
    }

}
