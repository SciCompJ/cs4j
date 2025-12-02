/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Collection;

import net.sci.geom.geom3d.impl.DefaultPolygon3D;

/**
 * A 3D polygon, embedded within a plane.
 * 
 * @author dlegland
 *
 */
public interface Polygon3D extends Geometry3D
{
    /**
     * Constructor from an array of points
     * 
     * @param vertices
     *            the vertices stored in an array of Point3D
     */
    public static Polygon3D create(Point3D... vertices)
    {
        return new DefaultPolygon3D(vertices);
    }
    

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
