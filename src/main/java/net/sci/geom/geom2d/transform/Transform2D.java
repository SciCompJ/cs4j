/**
 * 
 */
package net.sci.geom.geom2d.transform;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public interface Transform2D
{
	public Point2D transform(Point2D point);
}
