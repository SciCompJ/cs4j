/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Collection;

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
     * @return the number of vertices within this polygon.
     */
    public int vertexCount();

    /**
     * @return the list of vertices that compose this polygon.
     */
    public Collection<Point3D> vertexPositions();


    // ===================================================================
    // Specialization of the Geometry interface
    
    @Override
    public Polygon3D duplicate();
}
