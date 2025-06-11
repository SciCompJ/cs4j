/**
 * 
 */
package net.sci.geom.mesh3d;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;

/**
 * 
 */
public class SimplePolygonalMesh3DTest
{
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#findClosestVertexIndex(net.sci.geom.geom3d.Point3D)}.
     */
    @Test
    public final void testFindClosestVertexIndex()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();
        
        assertEquals(0, mesh.findClosestVertexIndex(new Point3D(25,  5, 10)));
        assertEquals(1, mesh.findClosestVertexIndex(new Point3D( 5, 25, 10)));
        assertEquals(2, mesh.findClosestVertexIndex(new Point3D(25, 25, 10)));
        assertEquals(3, mesh.findClosestVertexIndex(new Point3D(25,  5, 30)));
        assertEquals(4, mesh.findClosestVertexIndex(new Point3D( 5, 25, 30)));
        assertEquals(5, mesh.findClosestVertexIndex(new Point3D(25, 25, 30)));
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#faceCount()}.
     */
    @Test
    public final void testFaceCount()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();
        
        assertEquals(6, mesh.vertexCount());
        assertEquals(5, mesh.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#getFacePolygon(int)}.
     */
    @Test
    public final void testGetFacePolygon()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();

        assertEquals(3, mesh.getFacePolygon(0).vertexCount());
        assertEquals(4, mesh.getFacePolygon(1).vertexCount());
        assertEquals(4, mesh.getFacePolygon(2).vertexCount());
        assertEquals(4, mesh.getFacePolygon(3).vertexCount());
        assertEquals(3, mesh.getFacePolygon(4).vertexCount());
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#contains(net.sci.geom.geom3d.Point3D, double)}.
     */
    @Test
    public final void testContains()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();

        // on mesh boundary
        assertTrue(mesh.contains(new Point3D(18, 18, 10), 0.01));
        assertTrue(mesh.contains(new Point3D(15, 15, 20), 0.01));
        assertTrue(mesh.contains(new Point3D(20, 15, 20), 0.01));
        assertTrue(mesh.contains(new Point3D(15, 20, 20), 0.01));
        assertTrue(mesh.contains(new Point3D(18, 18, 30), 0.01));
        
        // within the mesh
        assertFalse(mesh.contains(new Point3D(18, 18, 20), 0.01));

        // outside the mesh
        assertFalse(mesh.contains(new Point3D(18, 18,  5), 0.01));
        assertFalse(mesh.contains(new Point3D(18, 18, 25), 0.01));
        assertFalse(mesh.contains(new Point3D(12, 12, 20), 0.01));
        assertFalse(mesh.contains(new Point3D(25, 18, 20), 0.01));
        assertFalse(mesh.contains(new Point3D(18, 25, 20), 0.01));
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#distance(double, double, double)}.
     */
    @Test
    public final void testDistance()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();

        // on mesh boundary
        assertEquals(5.0, mesh.distance(new Point3D(18, 18,  5)), 0.01);
        assertEquals(5.0 * Math.sqrt(2), mesh.distance(new Point3D(10, 10, 20)), 0.01);
        assertEquals(5.0, mesh.distance(new Point3D(20, 25, 20)), 0.01);
        assertEquals(5.0, mesh.distance(new Point3D(25, 20, 20)), 0.01);
        assertEquals(5.0, mesh.distance(new Point3D(18, 18, 35)), 0.01);
        
        // within the mesh
        assertEquals(2.0, mesh.distance(new Point3D(18, 18, 20)), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.geom.mesh3d.SimplePolygonalMesh3D#bounds()}.
     */
    @Test
    public final void testBounds()
    {
        SimplePolygonalMesh3D mesh = createTrianglePrism();
        
        Bounds3D bounds = mesh.bounds();
        assertEquals(10.0, bounds.xMin(), 0.01);
        assertEquals(20.0, bounds.xMax(), 0.01);
        assertEquals(10.0, bounds.yMin(), 0.01);
        assertEquals(20.0, bounds.yMax(), 0.01);
        assertEquals(10.0, bounds.zMin(), 0.01);
        assertEquals(30.0, bounds.zMax(), 0.01);
    }
    
    /**
     *         
     * (4)-------(5)
     *  |  -     /|  
     *  |    -(3) |
     *  |      |  |
     * (1)-----|-(2)
     *     -   | /  
     *       -(0) 
     * 
     * @return a mesh representing a prism with a triangular basis.
     */
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
