/**
 * 
 */
package net.sci.geom.mesh3d;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;

/**
 * A collection of static methods for working with 3D meshes
 * 
 * @author dlegland
 */
public class Meshes3D
{
    /**
     * Creates a cuboidal mesh with rectangle faces corresponding to the
     * specified 3D bounds.
     * 
     * @param bounds
     *            an instance of Bounds
     * @return a 3D mesh with rectangle faces that corresponds to the bounds
     */
    public static final Mesh3D fromBounds(Bounds3D bounds)
    {
        SimplePolygonalMesh3D mesh = new SimplePolygonalMesh3D(8, 6);
        Mesh3D.Vertex v000 = mesh.addVertex(new Point3D(bounds.xMin(), bounds.yMin(), bounds.zMin()));
        Mesh3D.Vertex v001 = mesh.addVertex(new Point3D(bounds.xMax(), bounds.yMin(), bounds.zMin()));
        Mesh3D.Vertex v010 = mesh.addVertex(new Point3D(bounds.xMin(), bounds.yMax(), bounds.zMin()));
        Mesh3D.Vertex v011 = mesh.addVertex(new Point3D(bounds.xMax(), bounds.yMax(), bounds.zMin()));
        Mesh3D.Vertex v100 = mesh.addVertex(new Point3D(bounds.xMin(), bounds.yMin(), bounds.zMax()));
        Mesh3D.Vertex v101 = mesh.addVertex(new Point3D(bounds.xMax(), bounds.yMin(), bounds.zMax()));
        Mesh3D.Vertex v110 = mesh.addVertex(new Point3D(bounds.xMin(), bounds.yMax(), bounds.zMax()));
        Mesh3D.Vertex v111 = mesh.addVertex(new Point3D(bounds.xMax(), bounds.yMax(), bounds.zMax()));
        
        mesh.addFace(new Mesh3D.Vertex[] {v000, v010, v011, v001});
        mesh.addFace(new Mesh3D.Vertex[] {v100, v101, v111, v110});
        mesh.addFace(new Mesh3D.Vertex[] {v001, v011, v111, v101});
        mesh.addFace(new Mesh3D.Vertex[] {v000, v100, v110, v010});
        mesh.addFace(new Mesh3D.Vertex[] {v000, v001, v101, v100});
        mesh.addFace(new Mesh3D.Vertex[] {v010, v110, v111, v011});
        return mesh;
    }
    
    /**
     * Creates a basic tetrahedron whose vertices correspond to four corners of
     * the unit cube, including the origin.
     * 
     * @return a Mesh instance representing a tetrahedron
     */
    public static final TriMesh3D createTetrahedron()
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
     * Creates a basic Octahedron, with eight faces, and six vertices located at
     * extremity of one of the unit vectors (in each direction).
     * 
     * @return a Mesh instance representing an octahedron
     */
    public static final TriMesh3D createOctahedron()
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
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Meshes3D()
    {
    }
}
