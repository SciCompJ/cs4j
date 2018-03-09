/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.Collection;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Geometry3D;

/**
 * A polygonal mesh in 3D space
 * 
 * @author dlegland
 *
 */
public interface Mesh3D extends Geometry3D
{
    // ===================================================================
    // Management of vertices
    
    public Collection<Point3D> vertexPositions();
    
//    public Iterator<Point3D> vertexIterator();
    
    /**
     * @return the number of vertices in this mesh.
     */
    public int vertexNumber();
    
    
    // ===================================================================
    // Management of edges ? 
    
    // ===================================================================
    // Management of faces
    
}
