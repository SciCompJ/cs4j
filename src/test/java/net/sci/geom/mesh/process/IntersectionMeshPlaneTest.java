/**
 * 
 */
package net.sci.geom.mesh.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.polyline.Polyline3D;
import net.sci.geom.mesh.DefaultTriMesh3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.Meshes3D;
import net.sci.geom.mesh.TriMesh3D;

/**
 * @author dlegland
 *
 */
public class IntersectionMeshPlaneTest
{
    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_octahedron_xyPlane_z05()
    {
        TriMesh3D mesh = Meshes3D.createOctahedron();
        Plane3D plane = new Plane3D(new Point3D(0, 0, 0.5), new Vector3D(0, 0, 1));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(1, curves.size());
        
        Polyline3D ring1 = curves.iterator().next();
        assertEquals(4, ring1.vertexCount());
    }

    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_octahedron_yzPlane_x05()
    {
        TriMesh3D mesh = Meshes3D.createOctahedron();
        Plane3D plane = new Plane3D(new Point3D(0.5, 0, 0), new Vector3D(1, 0, 0));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(1, curves.size());
        
        Polyline3D ring1 = curves.iterator().next();
        assertEquals(4, ring1.vertexCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_singleTriangle()
    {
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        Mesh3D.Vertex v0 = mesh.addVertex(new Point3D( 0,  0,  0));
        Mesh3D.Vertex v1 = mesh.addVertex(new Point3D(10,  0,  0));
        Mesh3D.Vertex v2 = mesh.addVertex(new Point3D( 0,  0, 10));
        mesh.addFace(v0, v1, v2);
        
        Plane3D plane = new Plane3D(new Point3D(0.0, 0, 5.0), new Vector3D(0, 0, 1));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(1, curves.size());
        
        Polyline3D poly = curves.iterator().next();
        assertEquals(2, poly.vertexCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_openMesh()
    {
        DefaultTriMesh3D mesh = new DefaultTriMesh3D();
        Mesh3D.Vertex v0 = mesh.addVertex(new Point3D( 0,  0,  0));
        Mesh3D.Vertex v1 = mesh.addVertex(new Point3D(10,  0,  0));
        Mesh3D.Vertex v2 = mesh.addVertex(new Point3D( 0, 10,  0));
        Mesh3D.Vertex v3 = mesh.addVertex(new Point3D(10, 10,  0));
        Mesh3D.Vertex v4 = mesh.addVertex(new Point3D( 0,  0, 10));
        Mesh3D.Vertex v5 = mesh.addVertex(new Point3D(10,  0, 10));
        Mesh3D.Vertex v6 = mesh.addVertex(new Point3D( 0, 10, 10));
        Mesh3D.Vertex v7 = mesh.addVertex(new Point3D(10, 10, 10));
        mesh.addFace(v0, v1, v4);
        mesh.addFace(v5, v4, v1);
        mesh.addFace(v1, v3, v5);
        mesh.addFace(v7, v5, v3);
        mesh.addFace(v3, v2, v7);
        mesh.addFace(v6, v7, v2);
        mesh.addFace(v2, v0, v6);
        mesh.addFace(v4, v6, v0);

        Plane3D plane = new Plane3D(new Point3D(5.0, 0, 0.0), new Vector3D(1, 0, 0));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(2, curves.size());
        
        Iterator<Polyline3D> iter = curves.iterator(); 
        Polyline3D ring1 = iter.next();
        assertEquals(3, ring1.vertexCount());
        Polyline3D poly2 = iter.next();
        assertEquals(3, poly2.vertexCount());
    }
}
