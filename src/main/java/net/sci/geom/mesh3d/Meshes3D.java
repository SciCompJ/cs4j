/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.HashMap;
import java.util.Iterator;

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
        // retrieve min/max coords
        double xmin = bounds.minX();
        double xmax = bounds.maxX();
        double ymin = bounds.minY();
        double ymax = bounds.maxY();
        double zmin = bounds.minZ();
        double zmax = bounds.maxZ();
        SimplePolygonalMesh3D mesh = new SimplePolygonalMesh3D(8, 6);
        Mesh3D.Vertex v000 = mesh.addVertex(new Point3D(xmin, ymin, zmin));
        Mesh3D.Vertex v001 = mesh.addVertex(new Point3D(xmax, ymin, zmin));
        Mesh3D.Vertex v010 = mesh.addVertex(new Point3D(xmin, ymax, zmin));
        Mesh3D.Vertex v011 = mesh.addVertex(new Point3D(xmax, ymax, zmin));
        Mesh3D.Vertex v100 = mesh.addVertex(new Point3D(xmin, ymin, zmax));
        Mesh3D.Vertex v101 = mesh.addVertex(new Point3D(xmax, ymin, zmax));
        Mesh3D.Vertex v110 = mesh.addVertex(new Point3D(xmin, ymax, zmax));
        Mesh3D.Vertex v111 = mesh.addVertex(new Point3D(xmax, ymax, zmax));
        
        mesh.addFace(new Mesh3D.Vertex[] {v000, v010, v011, v001});
        mesh.addFace(new Mesh3D.Vertex[] {v100, v101, v111, v110});
        mesh.addFace(new Mesh3D.Vertex[] {v001, v011, v111, v101});
        mesh.addFace(new Mesh3D.Vertex[] {v000, v100, v110, v010});
        mesh.addFace(new Mesh3D.Vertex[] {v000, v001, v101, v100});
        mesh.addFace(new Mesh3D.Vertex[] {v010, v110, v111, v011});
        return mesh;
    }
    
    /**
     * Creates a regular tetrahedron whose vertices correspond to four corners of
     * the unit cube, including the origin.
     * 
     * @return a Mesh instance representing a tetrahedron
     */
    public static final TriMesh3D createTetrahedron()
    {
        SimpleTriMesh3D mesh = new SimpleTriMesh3D(4, 4);
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
     * Creates a regular Octahedron, with eight faces, and six vertices located at
     * extremity of one of the unit vectors (in each direction).
     * 
     * @return a Mesh instance representing an octahedron
     */
    public static final TriMesh3D createOctahedron()
    {
        SimpleTriMesh3D mesh = new SimpleTriMesh3D(6, 8);
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
     * Creates a regular Octahedron, with eight faces, and six vertices located at
     * extremity of one of the unit vectors (in each direction).
     * 
     * @return a Mesh instance representing an octahedron
     */
    public static final TriMesh3D createIcosahedron()
    {
        // pre-compute some constants
        double theta = 2 * Math.PI / 5;                 // angular increment
        double l = 1 / Math.sin(theta / 2) / 2;         // edge length
        double z1 = Math.sqrt(1 - l * l);               // height of first five vertices
        double h = Math.sqrt(l * l - .5 * .5);          // height of second five vertices
        double z2 = Math.sqrt(3.0 / 4.0 - (l - h) * (l - h));

        // initialize new trimesh
        SimpleTriMesh3D mesh = new SimpleTriMesh3D(12, 20);
        
        // base vertex
        mesh.addVertex(new Point3D( 0,  0,  0));
        
        // vertices from first layer
        for (int i = 0; i < 5; i++)
        {
            double x1 = l * Math.cos(theta * i);
            double y1 = l * Math.sin(theta * i);
            mesh.addVertex(new Point3D(x1, y1, z1));
        }

        // vertices from second layer
        for (int i = 0; i < 5; i++)
        {
            double x2 = l * Math.cos(theta * i + theta / 2);
            double y2 = l * Math.sin(theta * i + theta / 2);
            mesh.addVertex(new Point3D(x2, y2, z1 + z2));
        }
        
        // summit vertex
        mesh.addVertex(new Point3D(0, 0, 2 * z1 + z2));
        
        // use face indices based on the order of insertion of the vertices
        mesh.addFace(0, 2, 1);
        mesh.addFace(0, 3, 2);
        mesh.addFace(0, 4, 3);
        mesh.addFace(0, 5, 4);
        mesh.addFace(0, 1, 5);
        
        mesh.addFace(1, 2, 6);
        mesh.addFace(2, 3, 7);
        mesh.addFace(3, 4, 8);
        mesh.addFace(4, 5, 9);
        mesh.addFace(5, 1, 10);
        
        mesh.addFace(6, 2, 7);
        mesh.addFace(7, 3, 8);
        mesh.addFace(8, 4, 9);
        mesh.addFace(9, 5, 10);
        mesh.addFace(10, 1, 6);

        mesh.addFace(6, 7, 11);
        mesh.addFace(7, 8, 11);
        mesh.addFace(8, 9, 11);
        mesh.addFace(9, 10, 11);
        mesh.addFace(10, 6, 11);
       
        return mesh;
    }
    
    public static final TriMesh3D triangulate(Mesh3D mesh)
    {
        int nv = mesh.vertexCount();
        SimpleTriMesh3D trimesh = new SimpleTriMesh3D(nv, mesh.faceCount());
        HashMap<Mesh3D.Vertex, Mesh3D.Vertex> vertexMap = new HashMap<>(nv);
        
        for (Mesh3D.Vertex v : mesh.vertices())
        {
            vertexMap.put(v, trimesh.addVertex(v.position()));
        }
        
        for (Mesh3D.Face face : mesh.faces())
        {
            if (face.vertexCount() < 3)
            {
                throw new RuntimeException("Can not triangulate a face with less than three vertices");
            }
            
            Iterator<? extends Mesh3D.Vertex> iter = face.vertices().iterator();
            Mesh3D.Vertex v0 = iter.next();
            Mesh3D.Vertex v1 = iter.next();
            while (iter.hasNext())
            {
                Mesh3D.Vertex v2 = iter.next();
                trimesh.addFace(vertexMap.get(v0), vertexMap.get(v1), vertexMap.get(v2));
                v1 = v2;
            }
        }
        
        return trimesh;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Meshes3D()
    {
    }
}
