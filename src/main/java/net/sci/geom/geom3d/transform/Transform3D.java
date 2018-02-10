/**
 * 
 */
package net.sci.geom.geom3d.transform;

import net.sci.geom.geom3d.Point3D;

/**
 * @author dlegland
 *
 */
public interface Transform3D
{
	public Point3D transform(Point3D point);
}
