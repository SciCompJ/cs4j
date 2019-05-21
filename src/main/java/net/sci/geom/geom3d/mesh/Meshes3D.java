/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import net.sci.geom.geom3d.Point3D;

/**
 * A collection of static methods for working with 3D meshes
 * 
 * @author dlegland
 */
public class Meshes3D
{
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Meshes3D()
    {
    }
    
    /**
     * Creates a basic tetrahedron whose vertices correspond to four corners of
     * the unit cube, including the origin.
     * 
     * @return a Mesh instance representing a tetrahedron
     */
    public static final Mesh3D createTetrahedron()
    {
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        mesh.addVertex(new Point3D(0, 0, 0));
        mesh.addVertex(new Point3D(1, 1, 0));
        mesh.addVertex(new Point3D(1, 0, 1));
        mesh.addVertex(new Point3D(0, 1, 1));
        mesh.addFace(0, 1, 2);
        mesh.addFace(0, 2, 3);
        mesh.addFace(0, 3, 1);
        mesh.addFace(3, 2, 1);
        return mesh;
    }

    /**
     * Creates a basic Octahedron 
     * 
     * @return a Mesh instance representing an octahedron
     */
    public static final Mesh3D createOctahedron()
    {
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        mesh.addVertex(new Point3D( 1,  0,  0));
        mesh.addVertex(new Point3D( 0,  1,  0));
        mesh.addVertex(new Point3D(-1,  0,  0));
        mesh.addVertex(new Point3D( 0, -1,  0));
        mesh.addVertex(new Point3D( 0,  0,  1));
        mesh.addVertex(new Point3D( 0,  0, -1));
        mesh.addFace(0, 1, 4);
        mesh.addFace(1, 2, 4);
        mesh.addFace(2, 3, 4);
        mesh.addFace(3, 0, 4);
        mesh.addFace(0, 5, 1);
        mesh.addFace(1, 5, 2);
        mesh.addFace(2, 5, 3);
        mesh.addFace(0, 3, 5);
        return mesh;
    }
}
