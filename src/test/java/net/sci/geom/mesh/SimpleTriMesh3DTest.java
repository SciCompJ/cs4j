/**
 * 
 */
package net.sci.geom.mesh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh.Mesh3D.Face;
import net.sci.geom.mesh.Mesh3D.Vertex;

/**
 * @author dlegland
 *
 */
public class SimpleTriMesh3DTest
{
    @Test
    public final void testIndoxOf_Vertex()
    {
        SimpleTriMesh3D mesh1 = createTetrahedron();
        Mesh3D.Vertex v1 = mesh1.getVertex(0);
        
        int ind = mesh1.indexOf(v1);
        
        assertEquals(0, ind);
    }
    
    @Test
    public final void testIndoxOf_Vertex_DifferentMesh()
    {
        SimpleTriMesh3D mesh1 = createTetrahedron();
        Mesh3D.Vertex v1 = mesh1.getVertex(0);
        SimpleTriMesh3D mesh2 = createTetrahedron();
        
        boolean flag = false;
        try
        {
        	@SuppressWarnings("unused")
			int ind = mesh2.indexOf(v1);
        }
        catch(Exception ex)
        {
        	flag = true;
        }
        
        assertTrue(flag);
    }
    
	
    @Test
    public final void testVertices_Iterator_tetrahedron()
    {
        SimpleTriMesh3D mesh = createTetrahedron();
        
        int count = 0;
        Iterator<Vertex> iterator = mesh.vertices().iterator();
        while(iterator.hasNext())
        {
            iterator.next();
            count++;
        }
        
        assertEquals(4, count);
    }
    
    @Test
    public final void testVertices_Iterator_octahedron()
    {
        SimpleTriMesh3D mesh = createOctahedron();
        
        int count = 0;
        Iterator<Vertex> iterator = mesh.vertices().iterator();
        while(iterator.hasNext())
        {
            iterator.next();
            count++;
        }
        
        assertEquals(6, count);
    }
    
    @Test
    public final void testFaces_Iterator()
    {
        SimpleTriMesh3D mesh = createOctahedron();
        
        int count = 0;
        Iterator<Face> iterator = mesh.faces().iterator();
        while(iterator.hasNext())
        {
            iterator.next();
            count++;
        }
        
        assertEquals(8, count);
    }
        
    /**
     * Creates a basic tetrahedron whose vertices correspond to four corners of
     * the unit cube, including the origin.
     * 
     * @return a Mesh instance representing a tetrahedron
     */
    private static final SimpleTriMesh3D createTetrahedron()
    {
        SimpleTriMesh3D mesh = new SimpleTriMesh3D();
        Mesh3D.Vertex v0 = mesh.addVertex(new Point3D(0, 0, 0));
        Mesh3D.Vertex v1 = mesh.addVertex(new Point3D(1, 1, 0));
        Mesh3D.Vertex v2 = mesh.addVertex(new Point3D(1, 0, 1));
        Mesh3D.Vertex v3 = mesh.addVertex(new Point3D(0, 1, 1));
        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
        mesh.addFace(v0, v3, v1);
        mesh.addFace(v3, v2, v1);
        return mesh;
    }

    /**
     * Creates a basic Octahedron 
     * 
     * @return a SimpleTriMesh3D instance representing an octahedron
     */
    private static final SimpleTriMesh3D createOctahedron()
    {
        SimpleTriMesh3D mesh = new SimpleTriMesh3D();
        Mesh3D.Vertex v0 = mesh.addVertex(new Point3D( 1,  0,  0));
        Mesh3D.Vertex v1 = mesh.addVertex(new Point3D( 0,  1,  0));
        Mesh3D.Vertex v2 = mesh.addVertex(new Point3D(-1,  0,  0));
        Mesh3D.Vertex v3 = mesh.addVertex(new Point3D( 0, -1,  0));
        Mesh3D.Vertex v4 = mesh.addVertex(new Point3D( 0,  0,  1));
        Mesh3D.Vertex v5 = mesh.addVertex(new Point3D( 0,  0, -1));
        mesh.addFace(v0, v1, v4);
        mesh.addFace(v1, v2, v4);
        mesh.addFace(v2, v3, v4);
        mesh.addFace(v3, v0, v4);
        mesh.addFace(v0, v5, v1);
        mesh.addFace(v1, v5, v2);
        mesh.addFace(v2, v5, v3);
        mesh.addFace(v0, v3, v5);
        return mesh;
    }

}
