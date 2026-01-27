/**
 * 
 */
package net.sci.geom.mesh3d;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom3d.Bounds3D;

/**
 * 
 */
public class Meshes3DTest
{
    /**
     * Test method for {@link net.sci.geom.mesh3d.Meshes3D#fromBounds(net.sci.geom.geom3d.Bounds3D)}.
     */
    @Test
    public final void test_fromBounds()
    {
        Bounds3D bounds = new Bounds3D(0, 50, 0, 40, 0, 30);
        Mesh3D mesh = Meshes3D.fromBounds(bounds);
        
        assertEquals(8, mesh.vertexCount());
        assertEquals(6, mesh.faceCount());
        
        assertTrue(bounds.almostEquals(mesh.bounds(), 0.001));
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.Meshes3D#createTetrahedron()}.
     */
    @Test
    public final void test_createTetrahedron()
    {
        TriMesh3D mesh = Meshes3D.createTetrahedron();
        assertEquals(4, mesh.vertexCount());
        assertEquals(4, mesh.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.Meshes3D#createOctahedron()}.
     */
    @Test
    public final void test_createOctahedron()
    {
        TriMesh3D mesh = Meshes3D.createOctahedron();
        assertEquals(6, mesh.vertexCount());
        assertEquals(8, mesh.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.Meshes3D#triangulate(net.sci.geom.mesh3d.Mesh3D)}.
     */
    @Test
    public final void test_triangulate()
    {
        Bounds3D bounds = new Bounds3D(0, 50, 0, 40, 0, 30);
        Mesh3D mesh = Meshes3D.fromBounds(bounds);

        TriMesh3D trimesh = Meshes3D.triangulate(mesh);
        
        assertEquals(8, trimesh.vertexCount());
        assertEquals(12, trimesh.faceCount());
    }
    
}
