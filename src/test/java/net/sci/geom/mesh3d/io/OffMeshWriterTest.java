/**
 * 
 */
package net.sci.geom.mesh3d.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.Meshes3D;
import net.sci.geom.mesh3d.SimplePolygonalMesh3D;

/**
 * @author dlegland
 *
 */
public class OffMeshWriterTest
{
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.io.OffMeshWriter#writeMesh(net.sci.geom.mesh3d.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteMesh() throws IOException
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        File file = new File("test_mesh_write_off_octahedron.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);
        
        // cleanup
        file.delete();
    }

    /**
     * Test method for {@link net.sci.geom.mesh3d.io.OffMeshWriter#writeMesh(net.sci.geom.mesh3d.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteAndReadMesh() throws IOException
    {
        Mesh3D mesh = Meshes3D.createOctahedron();
        
        File file = new File("test_mesh_write_off_octahedron.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);
        
        MeshReader reader = new OffMeshReader(file);
        Mesh3D mesh2 = reader.readMesh();
        assertEquals(mesh.vertexCount(), mesh2.vertexCount());
        assertEquals(mesh.faceCount(), mesh2.faceCount());

        // cleanup
        file.delete();
    }
    
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.io.OffMeshWriter#writeMesh(net.sci.geom.mesh3d.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteMesh_trianglePrism() throws IOException
    {
        Mesh3D mesh = createTrianglePrism();
        
        File file = new File("test_mesh_write_off_trianglePrism.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);

        // cleanup
        file.delete();
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.io.OffMeshWriter#writeMesh(net.sci.geom.mesh3d.Mesh3D)}.
     * @throws IOException 
     */
    @Test
    public final void testWriteReadMesh_trianglePrism() throws IOException
    {
        Mesh3D mesh = createTrianglePrism();
        
        File file = new File("test_mesh_write_off_trianglePrism.off");
        MeshWriter writer = new OffMeshWriter(file);
        
        writer.writeMesh(mesh);
        
        MeshReader reader = new OffMeshReader(file);
        Mesh3D mesh2 = reader.readMesh();
        assertEquals(mesh.vertexCount(), mesh2.vertexCount());
        assertEquals(mesh.faceCount(), mesh2.faceCount());

        // cleanup
        file.delete();
    }
    
    private static final SimplePolygonalMesh3D createTrianglePrism()
    {
        ArrayList<Point3D> vertices = new ArrayList<Point3D>(6);
        vertices.add(new Point3D(20, 10, 10));
        vertices.add(new Point3D(10, 20, 10));
        vertices.add(new Point3D(20, 20, 10));
        vertices.add(new Point3D(20, 10, 30));
        vertices.add(new Point3D(10, 20, 30));
        vertices.add(new Point3D(20, 20, 30));
        
        ArrayList<int[]> faces = new ArrayList<>(5);
        faces.add(new int[] {0, 1, 2});
        faces.add(new int[] {0, 2, 5, 3});
        faces.add(new int[] {1, 0, 3, 4});
        faces.add(new int[] {2, 1, 4, 5});
        faces.add(new int[] {3, 5, 4});
        
        return new SimplePolygonalMesh3D(vertices, faces);
    }
}
