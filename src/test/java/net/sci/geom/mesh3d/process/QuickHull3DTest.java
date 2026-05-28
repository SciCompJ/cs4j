/**
 * 
 */
package net.sci.geom.mesh3d.process;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.io.OffMeshReader;
import net.sci.table.Table;
import net.sci.table.io.DelimitedTableReader;

/**
 * 
 */
public class QuickHull3DTest
{
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_UnitSphere_N10() throws IOException
    {
        // Create array of points
        int nv = 10;
        Point3D[] points = new Point3D[nv];
        
        // initialize the Random class to ensure reproducibility
        Random rng = new Random(17);
        for (int i = 0; i < nv; i++)
        {
            double x = rng.nextGaussian();
            double y = rng.nextGaussian();
            double z = rng.nextGaussian();
            double norm = Math.hypot(Math.hypot(x, y), z);
            points[i] = new Point3D(x/norm, y/norm, z/norm);
        }

        // compute the convex hull
        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
        
        // all vertices must be kept within the hull
        assertEquals(nv, hull.vertexCount());
        
        // all faces should be triangles. Number of faces should equal 2*nv - 2
        int expectedFaceCount = 2 * nv - 4;
        assertEquals(expectedFaceCount, hull.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_UnitBall_N20() throws IOException
    {
        ArrayList<Point3D> points = readPoints_UnitBallN20();
        Mesh3D refHull = readConvexHull_PointsUnitBall_N20();
        
        // compute the convex hull
        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
        
        assertEquals(refHull.faceCount(), hull.faceCount());
        
        assertTrue(isTopologicallyEquivalent(hull, refHull));
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_UnitBall_N20_setAbsoluteTolerance() throws IOException
    {
        ArrayList<Point3D> points = readPoints_UnitBallN20();
        Mesh3D refHull = readConvexHull_PointsUnitBall_N20();
        
        // compute the convex hull
        QuickHull3D op = new QuickHull3D();
        op.setToleranceStrategy(new QuickHull3D.AbsoluteTolerance(1e-10));
        QuickHull3D.Mesh hull = op.process(points);

        assertEquals(refHull.faceCount(), hull.faceCount());
        
        assertTrue(isTopologicallyEquivalent(hull, refHull));
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_UnitBall_N20_setRelativeTolerance() throws IOException
    {
        ArrayList<Point3D> points = readPoints_UnitBallN20();
        Mesh3D refHull = readConvexHull_PointsUnitBall_N20();
        
        // compute the convex hull
        QuickHull3D op = new QuickHull3D();
        op.setToleranceStrategy(new QuickHull3D.RelativeTolerance(1e-10));
        QuickHull3D.Mesh hull = op.process(points);
        
        assertEquals(refHull.faceCount(), hull.faceCount());
        
        assertTrue(isTopologicallyEquivalent(hull, refHull));
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_Octahedron() throws IOException
    {
        // Create array of points
        Point3D[] points = new Point3D[] { 
                new Point3D( 0,  0,  0),
                new Point3D( 5,  0,  0),
                new Point3D(-5,  0,  0),
                new Point3D( 0,  4,  0),
                new Point3D( 0, -4,  0),
                new Point3D( 0,  0,  3),
                new Point3D( 0,  0, -3),
        };

        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
   
        // expect all vertices to be kept
        assertEquals(6, hull.vertexCount());
        
        // expect 8 faces
        assertEquals(8, hull.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_Octahedron_bounds() throws IOException
    {
        // Create array of points
        Point3D[] points = new Point3D[] { 
                new Point3D( 0,  0,  0),
                new Point3D( 5,  0,  0),
                new Point3D(-5,  0,  0),
                new Point3D( 0,  4,  0),
                new Point3D( 0, -4,  0),
                new Point3D( 0,  0,  3),
                new Point3D( 0,  0, -3),
        };

        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
   
        // expect all vertices to be kept
        assertEquals(6, hull.vertexCount());
        
        // expect 8 faces
        assertEquals(8, hull.faceCount());
        
        Bounds3D bounds = hull.bounds();
        
        assertEquals(-5.0, bounds.xMin(), 0.01);
        assertEquals(+5.0, bounds.xMax(), 0.01);
        assertEquals(-4.0, bounds.yMin(), 0.01);
        assertEquals(+4.0, bounds.yMax(), 0.01);
        assertEquals(-3.0, bounds.zMin(), 0.01);
        assertEquals(+3.0, bounds.zMax(), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_Octahedron_distance() throws IOException
    {
        // Create array of points
        Point3D[] points = new Point3D[] { 
                new Point3D( 0,  0,  0),
                new Point3D( 5,  0,  0),
                new Point3D(-5,  0,  0),
                new Point3D( 0,  4,  0),
                new Point3D( 0, -4,  0),
                new Point3D( 0,  0,  3),
                new Point3D( 0,  0, -3),
        };

        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
   
        // expect all vertices to be kept
        assertEquals(6, hull.vertexCount());
        
        // expect 8 faces
        assertEquals(8, hull.faceCount());
        
        assertEquals(1.0, hull.distance(new Point3D( 6, 0, 0)), 0.01);
        assertEquals(1.0, hull.distance(new Point3D(-6, 0, 0)), 0.01);
        assertEquals(1.0, hull.distance(new Point3D(0,  5, 0)), 0.01);
        assertEquals(1.0, hull.distance(new Point3D(0, -5, 0)), 0.01);
        assertEquals(1.0, hull.distance(new Point3D(0, 0,  4)), 0.01);
        assertEquals(1.0, hull.distance(new Point3D(0, 0, -4)), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_UnitCube() throws IOException
    {
        // Create array of points
        Point3D[] points = new Point3D[] { 
                new Point3D(0, 0, 0), 
                new Point3D(1, 0, 0), 
                new Point3D(0, 1, 0), 
                new Point3D(1, 1, 0), 
                new Point3D(0, 0, 1), 
                new Point3D(1, 0, 1), 
                new Point3D(0, 1, 1), 
                new Point3D(1, 1, 1)
        };

        QuickHull3D op = new QuickHull3D();
        QuickHull3D.Mesh hull = op.process(points);
   
        // expect all vertices to be kept
        assertEquals(8, hull.vertexCount());
        
        // as faces are merged, expect 6 faces
        assertEquals(6, hull.faceCount());
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_KeepOriginalIndices() throws IOException
    {
        // Create array of points
        // use eight points close to origin (inds: 0 -> 7) 
        // and six points for the convex hull (inds: 8 -> 13)
        Point3D[] points = new Point3D[] { 
                new Point3D(  0.5,  0.5,  0.5),
                new Point3D( -0.5,  0.5,  0.5),
                new Point3D(  0.5, -0.5,  0.5),
                new Point3D( -0.5, -0.5,  0.5),
                new Point3D(  0.5,  0.5, -0.5),
                new Point3D( -0.5,  0.5, -0.5),
                new Point3D(  0.5, -0.5, -0.5),
                new Point3D( -0.5, -0.5, -0.5),
                new Point3D( 5,  0,  0),
                new Point3D(-5,  0,  0),
                new Point3D( 0,  4,  0),
                new Point3D( 0, -4,  0),
                new Point3D( 0,  0,  3),
                new Point3D( 0,  0, -3),
        };

        QuickHull3D op = new QuickHull3D();
        op.setRecomputeVertexIndices(false);
        QuickHull3D.Mesh hull = op.process(points);
   
        int[][] faceIndices = hull.getFaceVertexIndices();
        
        // expect all vertices to be kept
        assertEquals(14, hull.vertexCount());
        
        // as faces are merged, expect 6 faces
        assertEquals(8, hull.faceCount());
        
        boolean[] hullVertex = new boolean[14];
       for (int[] inds : faceIndices)
        {
            for (int index : inds)
            {
                hullVertex[index] = true;
            }
        }
        assertFalse(hullVertex[0]);
        assertFalse(hullVertex[1]);
        assertFalse(hullVertex[2]);
        assertFalse(hullVertex[3]);
        assertFalse(hullVertex[4]);
        assertFalse(hullVertex[5]);
        assertFalse(hullVertex[6]);
        assertFalse(hullVertex[7]);
        assertTrue(hullVertex[8]);
        assertTrue(hullVertex[9]);
        assertTrue(hullVertex[10]);
        assertTrue(hullVertex[11]);
        assertTrue(hullVertex[12]);
        assertTrue(hullVertex[13]);
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.QuickHull3D#build(net.sci.geom.geom3d.Point3D[])}.
     */
    @Test
    public final void test_process_OctahedronSurface_N400() throws IOException
    {
        // Create array of points
        ArrayList<Point3D> points = readPointsFromCsv("pointsOnOctahedron_N400.csv");

        QuickHull3D op = new QuickHull3D();
        op.setToleranceStrategy(new QuickHull3D.AbsoluteTolerance(1e-10));
        QuickHull3D.Mesh hull = op.process(points);
   
        // expect vertices to be kept
        assertEquals(124, hull.vertexCount());
        
        // as faces are merged, expect around 235 faces.
        // Matlab finds 235 with tolerance 1e-10 (less faces when tolerance is below 1e-6). 
//        System.out.println("face count: " + faceIndices.length);
        assertTrue(hull.faceCount() == 235);
    }
    
    private static final ArrayList<Point3D> readPointsFromCsv(String fileName) throws IOException
    {
        String filePath = QuickHull3DTest.class.getResource("/points/" + fileName).getFile();
        File file = new File(filePath);
        assertTrue(file.exists());
        
        DelimitedTableReader reader = new DelimitedTableReader(";");
        reader.setReadHeader(true);
        reader.setReadRowNames(false);
        
        Table table = reader.readTable(file);
        
        int nv = table.rowCount();
        ArrayList<Point3D> pts = new ArrayList<Point3D>(nv);
        for (int i = 0; i < nv; i++)
        {
            double x = table.getValue(i, 0);
            double y = table.getValue(i, 1);
            double z = table.getValue(i, 2);
            pts.add(new Point3D(x, y, z));
        }
        
        return pts;
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.VertexList#first()}.
     * @throws IOException 
     */
    private static final ArrayList<Point3D> readPoints_UnitBallN20() throws IOException
    {
        String fileName = QuickHull3DTest.class.getResource("/points/pointsInUnitBall_N20.csv").getFile();
        File file = new File(fileName);
        assertTrue(file.exists());
        
        DelimitedTableReader reader = new DelimitedTableReader(";");
        reader.setReadHeader(true);
        reader.setReadRowNames(false);
        
        Table table = reader.readTable(file);
        
        int nv = table.rowCount();
        ArrayList<Point3D> pts = new ArrayList<Point3D>(nv);
        for (int i = 0; i < nv; i++)
        {
            double x = table.getValue(i, 0);
            double y = table.getValue(i, 1);
            double z = table.getValue(i, 2);
            pts.add(new Point3D(x, y, z));
        }
        
        return pts;
    }
    
    /**
     * Test method for {@link net.sci.qhull3d.VertexList#first()}.
     * @throws IOException 
     */
    private static final Mesh3D readConvexHull_PointsUnitBall_N20() throws IOException
    {
        String fileName = QuickHull3DTest.class.getResource("/points/pointsInUnitBall_N20_hull.off").getFile();
        File file = new File(fileName);
        assertTrue(file.exists());
        
        OffMeshReader reader = new OffMeshReader(file);
        Mesh3D mesh = reader.readMesh();
        return mesh;
    }
    
    private static final boolean isTopologicallyEquivalent(QuickHull3D.Mesh hull, Mesh3D mesh)
    {
        // retrieve position of hull vertices (including non-hull vertices)
        Point3D[] hullVertices = hull.getVertexPositions();
        int nv = mesh.vertexCount();
        Point3D[] meshVertices = new Point3D[nv];
        int i = 0;
        for (Mesh3D.Vertex v : mesh.vertices())
        {
            meshVertices[i++] = v.position();
        }
        
        // find index of reference mesh within full array of points
        int[] vertexIndices = matchPoints(meshVertices, hullVertices);
        
        // create array of mesh face vertex indices
        int[][] hullFaces = new int[hull.faceCount()][];
        int iFace = 0;
        for (QuickHull3D.Mesh.Face face : hull.faces())
        {
            int nvf = face.vertexCount();
            if (nvf != 3)
            {
                System.out.println(String.format("hull face index %d has %d vertices", iFace, nvf));
            }
            hullFaces[iFace++] = face.vertexIndices();
        }
        
        // compare faces
        for (Mesh3D.Face face : mesh.faces())
        {
            int[] inds = new int[3];
            int iv0 = 0;
            for (Mesh3D.Vertex v : face.vertices())
            {
                inds[iv0++] = findClosestPoint(v.position(), meshVertices);
            }
            int[] inds2 = new int[3];
            for (int j = 0; j < 3; j++)
            {
                inds2[j] = vertexIndices[inds[j]];
            }
//            System.out.println("face : " + inds2[0] + ", " + inds2[1] + ", " + + inds2[2]);
            
            if (!containsElement(hullFaces, inds2)) return false;
        }
        
        return true;
    }
    
    private static final int[] matchPoints(Point3D[] points, Point3D[] target)
    {
        int[] inds = new int[points.length];
        for (int i = 0; i < points.length; i++)
        {
            inds[i] = findClosestPoint(points[i], target);
        }
        return inds;
    }
    
    private static final int findClosestPoint(Point3D point, Point3D[] array)
    {
        double distMax = Double.POSITIVE_INFINITY;
        int ind = -1;
        
        for (int i = 0; i < array.length; i++)
        {
            double dist = point.distance(array[i]);
            if (dist < distMax)
            {
                 distMax = dist;
                 ind = i;
            }
        }
        return ind;
    }
    
    private static final boolean containsElement(int[][] faces, int[] inds)
    {
        for (int[] face : faces)
        {
            if (hasSameIndices(face, inds)) return true;
        }
        return false;
    }
    
    private static final boolean hasSameIndices(int[] inds1, int[] inds2)
    {
        if (inds1.length != inds2.length) return false;
        for (int ind : inds1)
        {
            if (!contains(inds2, ind)) return  false;
        }
        return true;
    }
    
    private static final boolean contains(int[] inds, int ind)
    {
        for (int idx : inds)
        {
            if (idx == ind) return true;
        }
        return false;
    }
}
