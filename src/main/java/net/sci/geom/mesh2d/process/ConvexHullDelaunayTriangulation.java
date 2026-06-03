/**
 * 
 */
package net.sci.geom.mesh2d.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.mesh2d.Mesh2D;
import net.sci.geom.mesh2d.SimplePolygonalMesh2D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.process.QuickHull3D;
import net.sci.geom.mesh3d.process.QuickHull3D.Mesh;

/**
 * Computation of the Delaunay triangulation of a set of 2D points using 3D
 * convex hull computation.
 */
public class ConvexHullDelaunayTriangulation
{
    /**
     * Default empty constructor.
     */
    public ConvexHullDelaunayTriangulation()
    {
    }
    
    public Mesh2D process(Collection<Point2D> points)
    {
        Map<Point3D, Point2D> map = new HashMap<Point3D, Point2D>(points.size());
        for (Point2D p : points)
        {
            double x = p.x();
            double y = p.y();
            Point3D p3d = Point3D.of(x, y, x * x + y * y);
            map.put(p3d, p);
        }
        
        QuickHull3D algo = new QuickHull3D();
        Mesh hull = algo.process(map.keySet());
        
        SimplePolygonalMesh2D res = new SimplePolygonalMesh2D();
        Map<Point2D, Mesh2D.Vertex> vertexMap = new HashMap<Point2D, Mesh2D.Vertex>(points.size());
        for (Point2D p : points)
        {
            Mesh2D.Vertex v = res.addVertex(p);
            vertexMap.put(p, v);
        }
        
        for (Mesh3D.Face face : hull.faces())
        {
            // filter faces according to the normal
            if (face.normal().dotProduct(Vector3D.E_3) > 0.0) continue;

            Mesh2D.Vertex[] faceVertices = new Mesh2D.Vertex[face.vertexCount()];
            int iv = 0;
            for (Mesh3D.Vertex v : face.vertices())
            {
                Point2D pos2d = map.getOrDefault(v.position(), null);
                faceVertices[iv++] = vertexMap.getOrDefault(pos2d, null);
            }
            res.addFace(faceVertices);
        }
        
        return res;
    }
    
    public static final void main(String[] args)
    {
        ArrayList<Point2D> inputPoints = new ArrayList<Point2D>();
        inputPoints.add(Point2D.of(10, 10));
        inputPoints.add(Point2D.of(20, 10));
        inputPoints.add(Point2D.of(15, 17));
        inputPoints.add(Point2D.of(25, 17));
        
        ConvexHullDelaunayTriangulation algo = new ConvexHullDelaunayTriangulation();
        Mesh2D res = algo.process(inputPoints);
        
        System.out.println("vertex count: " + res.vertexCount());
        System.out.println("face count: " + res.faceCount());
    }
}
