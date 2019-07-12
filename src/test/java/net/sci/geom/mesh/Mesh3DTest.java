/**
 * 
 */
package net.sci.geom.mesh;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.Meshes3D;
import net.sci.geom.mesh.Mesh3D.Edge;
import net.sci.geom.mesh.Mesh3D.Face;
import net.sci.geom.mesh.Mesh3D.Vertex;

/**
 * High-level test class. Some tests are duplicated for the specific
 * implementations.
 * 
 * @author dlegland
 *
 */
public class Mesh3DTest
{
    @Test
    public final void testVertices_Iterator()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Vertex v : mesh.vertices())
        {
            count++;
        }
        
        assertEquals(6, count);
    }
    
    @Test
    public final void testEdges_Iterator()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Edge e : mesh.edges())
        {
            count++;
        }
        
        assertEquals(12, count);
    }
    
    @Test
    public final void testFaces_Iterator()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Face f : mesh.faces())
        {
            count++;
        }
        
        assertEquals(8, count);
    }
    
    @Test
    public final void testEdges()
    {
        // Create an octahedron
        Mesh3D mesh = Meshes3D.createOctahedron();
        assertEquals(12, mesh.edgeNumber());
    }
    
    @Test
    public final void testIntersectEdgesWithPlane()
    {
        // Create an octahedron
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        Point3D p0 = new Point3D(0, 0, .5);
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Plane3D plane = new Plane3D(p0, v1, v2);
        
        assertEquals(12, mesh.edgeNumber());
       
        ArrayList<Point3D> intersections = new ArrayList<>();
        for (Edge edge : mesh.edges())
        {
            Point3D inter = edge.curve().intersection(plane);
            if (inter!= null)
            {
                intersections.add(inter);
            }
        }
        
        assertEquals(4, intersections.size());
    }
    
}
