/**
 * 
 */
package net.sci.geom.geom3d.line;

import net.sci.geom.geom3d.Curve3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * A curve that can be inscribed in a straight line, like a ray, a straight
 * line, or a line segment. 
 * 
 * @see StraightLine3D
 * 
 * @author dlegland
 *
 */
public interface LinearGeometry3D extends Curve3D
{
    /**
     * Returns the position of the point used as origin for this linear
     * geometry.
     * 
     * @return the origin of this linear geometry
     */
    public Point3D origin();
    
    /**
     * @return the direction vector of this geometry
     */
    public Vector3D direction();
    
    /**
     * @return the straight line that contains this geometry
     */
    public StraightLine3D supportingLine();

    public LinearGeometry3D transform(AffineTransform3D transform);
}