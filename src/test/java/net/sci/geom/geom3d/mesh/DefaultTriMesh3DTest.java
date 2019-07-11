/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * @author dlegland
 *
 */
public class DefaultTriMesh3DTest
{
    @Test
    public final void testVertices_Iterator()
    {
        DefaultTriMesh3D mesh = (DefaultTriMesh3D) Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Mesh3D.Vertex v : mesh.vertices())
        {
            count++;
        }
        
        assertEquals(6, count);
    }
    
    @Test
    public final void testEdges_Iterator()
    {
        DefaultTriMesh3D mesh = (DefaultTriMesh3D) Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Mesh3D.Edge e : mesh.edges())
        {
            count++;
        }
        
        assertEquals(12, count);
    }
    
    @Test
    public final void testFaces_Iterator()
    {
        DefaultTriMesh3D mesh = (DefaultTriMesh3D) Meshes3D.createOctahedron();
        
        int count = 0;
        for (@SuppressWarnings("unused") Mesh3D.Face f : mesh.faces())
        {
            count++;
        }
        
        assertEquals(8, count);
    }
    
    @Test
    public final void testEdges()
    {
        // Create an octahedron
        DefaultTriMesh3D mesh = (DefaultTriMesh3D) Meshes3D.createOctahedron();
        
        assertEquals(12, mesh.edgeNumber());
//        for (DefaultTriMesh3D.Edge edge : mesh.edges())
//        {
//            System.out.println("Edge from " + edge.iv1 + " to " + edge.iv2);
//        }
    }
    
    @Test
    public final void testRemoveVertex()
    {
        // Create an simple mesh
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        Mesh3D.Vertex v0 = mesh.addVertex(new Point3D(0, 0, 0));
        Mesh3D.Vertex v1 = mesh.addVertex(new Point3D(1, 0, 0));
        Mesh3D.Vertex v2 = mesh.addVertex(new Point3D(0, 1, 0));
        Mesh3D.Vertex v3 = mesh.addVertex(new Point3D(0, 0, 1));
        mesh.addFace(v1, v2, v3);
        
        // remove vertex
        mesh.removeVertex(v0);
        
        assertEquals(3, mesh.vertexNumber());
    }

    
    @Test
    public final void testIntersectEdgesWithPlane()
    {
        // Create an octahedron
        DefaultTriMesh3D mesh = (DefaultTriMesh3D) Meshes3D.createOctahedron();
        
        Point3D p0 = new Point3D(0, 0, .5);
        Vector3D v1 = new Vector3D(1, 0, 0);
        Vector3D v2 = new Vector3D(0, 1, 0);
        Plane3D plane = new Plane3D(p0, v1, v2);
        
        assertEquals(12, mesh.edgeNumber());
       
        ArrayList<Point3D> intersections = new ArrayList<>();
        for (Mesh3D.Edge edge : mesh.edges())
        {
            Point3D inter = edge.curve().intersection(plane);
            if (inter!= null)
            {
                intersections.add(inter);
//                System.out.println(String.format("%5.2f, %5.2f, %5.2f", inter.getX(), inter.getY(), inter.getZ()));
            }
        }
        
        assertEquals(4, intersections.size());
    }
    
    @Test
    public final void testEdgeCompareTo()
    {
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        for (int i = 0; i < 4; i++)
        {
            mesh.addVertex(new Point3D(i, i, i));
        }
                
        DefaultTriMesh3D.Edge edge1 = mesh.new Edge(0, 1); 
        DefaultTriMesh3D.Edge edge2 = mesh.new Edge(2, 3); 
        DefaultTriMesh3D.Edge edge3 = mesh.new Edge(0, 3); 

        
        assertTrue(edge1.compareTo(edge1) == 0);
        assertTrue(edge1.compareTo(edge2) < 0);
        assertTrue(edge2.compareTo(edge1) > 0);
        assertTrue(edge1.compareTo(edge3) < 0);
        assertTrue(edge3.compareTo(edge1) > 0);
    }
    
}
