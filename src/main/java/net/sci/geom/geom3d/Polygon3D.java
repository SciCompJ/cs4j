/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Collection;

import net.sci.geom.geom3d.line.Plane3D;

/**
 * @author dlegland
 *
 */
public interface Polygon3D extends Geometry3D
{
    // ===================================================================
    // New methods
    
    /**
     * @return the 3D plane containing this polygon
     */
    public Plane3D supportingPlane();
    
    /**
     * @return the list of vertices that compose this polygon.
     */
    public Collection<Point3D> vertices();
}
