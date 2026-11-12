/**
 * 
 */
package net.sci.geom.mesh3d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;

/**
 * 
 */
public class TriMesh3DTest
{
    /**
     * Test method for {@link net.sci.geom.mesh3d.TriMesh3D#convert(net.sci.geom.mesh3d.Mesh3D)}.
     */
    @Test
    public final void test_convert_trimesh()
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        TriMesh3D trimesh = TriMesh3D.convert(mesh);
        
        // should be the same reference
        assertEquals(mesh, trimesh);
    }

    /**
     * Test method for {@link net.sci.geom.mesh3d.TriMesh3D#convert(net.sci.geom.mesh3d.Mesh3D)}.
     */
    @Test
    public final void test_convert_cube()
    {
        Bounds3D bounds = new Bounds3D(Point3D.of(0, 0, 0), Point3D.of(1, 1, 1));
        Mesh3D mesh = Meshes3D.fromBounds(bounds);
        
        TriMesh3D trimesh = TriMesh3D.convert(mesh);
        
        // should have same vertex count
        assertEquals(8, trimesh.vertexCount());
        // 6 square faces result 12 triangle faces
        assertEquals(12, trimesh.faceCount()); 
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.TriMesh3D#convert(net.sci.geom.mesh3d.Mesh3D)}.
     */
    @Test
    public final void test_convert_generic()
    {
        SimplePolygonalMesh3D mesh = new SimplePolygonalMesh3D(4, 4);
        Mesh3D.Vertex v1 = mesh.addVertex(Point3D.of(0, 0, 1));
        Mesh3D.Vertex v2 = mesh.addVertex(Point3D.of(1, 0, 0));
        Mesh3D.Vertex v3 = mesh.addVertex(Point3D.of(0, 1, 0));
        Mesh3D.Vertex v4 = mesh.addVertex(Point3D.of(-1, 0, 0));
        Mesh3D.Vertex v5 = mesh.addVertex(Point3D.of(0, -1, 0));
        Mesh3D.Vertex v6 = mesh.addVertex(Point3D.of(0, 0, -1));
        mesh.addFace(v2, v3, v1);
        mesh.addFace(v3, v4, v1);
        mesh.addFace(v4, v5, v1);
        mesh.addFace(v5, v2, v1);
        mesh.addFace(v3, v2, v6);
        mesh.addFace(v4, v3, v6);
        mesh.addFace(v5, v4, v6);
        mesh.addFace(v2, v5, v6);
        
        TriMesh3D trimesh = TriMesh3D.convert(mesh);
        
        // should have same element counts
        assertEquals(mesh.vertexCount(), trimesh.vertexCount());
        assertEquals(mesh.faceCount(), trimesh.faceCount()); 
    }
}
