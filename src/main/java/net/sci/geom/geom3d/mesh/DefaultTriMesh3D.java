/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * Default class for representing triangular meshes in 3D.
 * 
 * Vertices are stored in an ArrayList. Faces are stored in an ArrayList of integer triplets.
 * 
 * @author dlegland
 *
 */
public class DefaultTriMesh3D implements Mesh3D
{
    // ===================================================================
    // Class variables

    ArrayList<Point3D> vertices;
    
    ArrayList<int[]> faces;
    
    
    // ===================================================================
    // Constructors

    /**
     * Create a new empty mesh (no vertex, no face).
     */
    public DefaultTriMesh3D()
    {
        this.vertices = new ArrayList<Point3D>();
        this.faces = new ArrayList<int[]>();
    }
    
    // ===================================================================
    // Methods specific to Mesh3D
    
    /**
     * Computes the surface area of the mesh.
     * 
     * @return the surface area of the mesh
     */
    public double surfaceArea()
    {
        double surf = 0;
        
        // Computes the sum of the norm of the cross products.
        for (int[] faceIndices : faces)
        {
            Point3D v1 = this.vertices.get(faceIndices[0]);
            Point3D v2 = this.vertices.get(faceIndices[1]);
            Point3D v3 = this.vertices.get(faceIndices[2]);
            
            Vector3D v12 = new Vector3D(v1, v2);
            Vector3D v13 = new Vector3D(v1, v3);
            surf += Vector3D.crossProduct(v12,  v13).norm();
        }

        return surf / 2;
    }


    // ===================================================================
    // Management of vertices
    
    /**
     * Adds a vertex to the mesh and returns the index associated to its
     * position.
     * 
     * @param position
     *            the position of the new vertex
     * @return the index of the new vertex
     */
    public int addVertex(Point3D position)
    {
        int index = vertices.size();
        vertices.add(position);
        return index;
    }

    /**
     * Finds the index of the closest vertex to the input point.
     * 
     * @param point
     *            a query point
     * @return the index of the vertex the closest to query point
     */
    public int findClosestVertexIndex(Point3D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        for (int i = 0; i < vertices.size(); i++)
        {
            double dist = vertices.get(i).distance(point);
            if (dist < minDist)
            {
                minDist = dist;
                index = i;
            }
        }
        return index;
    }
    
    /**
     * Adds a triangular face defined by the indices of its three vertices.
     * 
     * @param iv1 index of the first face vertex (0-based)
     * @param iv2 index of the second face vertex (0-based)
     * @param iv3 index of the third face vertex (0-based)
     * @return the index of the newly created face
     */
    public int addFace(int iv1, int iv2, int iv3)
    {
        int index = faces.size();
        faces.add(new int[] { iv1, iv2, iv3 });
        return index;
    }

    public Triangle3D getFacePolygon(int faceIndex)
    {
        int[] inds = faces.get(faceIndex);
        Point3D p1 = this.vertices.get(inds[0]);
        Point3D p2 = this.vertices.get(inds[1]);
        Point3D p3 = this.vertices.get(inds[2]);
        
        return new Triangle3D(p1, p2, p3);
    }
    

    // ===================================================================
    // Implementation of the Mesh3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.mesh.Mesh3D#vertices()
     */
    @Override
    public Collection<Point3D> vertexPositions()
    {
        return this.vertexPositions();
    }

//    /* (non-Javadoc)
//     * @see net.sci.geom.geom3d.mesh.Mesh3D#vertexIterator()
//     */
//    @Override
//    public Iterator<Point3D> vertexIterator()
//    {
//        return this.vertices.iterator();
//    }

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.mesh.Mesh3D#vertexNumber()
     */
    @Override
    public int vertexNumber()
    {
        return vertices.size();
    }

    
    // ===================================================================
    // Implementation of the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        for (int[] faceIndices : faces)
        {
            Point3D p1 = this.vertices.get(faceIndices[0]);
            Point3D p2 = this.vertices.get(faceIndices[1]);
            Point3D p3 = this.vertices.get(faceIndices[2]);
            
            if (new Triangle3D(p1, p2, p3).contains(point, eps))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#distance(double, double, double)
     */
    @Override
    public double distance(double x, double y, double z)
    {
        double distMin = Double.POSITIVE_INFINITY;
        
        for (int[] faceIndices : faces)
        {
            Point3D p1 = this.vertices.get(faceIndices[0]);
            Point3D p2 = this.vertices.get(faceIndices[1]);
            Point3D p3 = this.vertices.get(faceIndices[2]);
            
            double dist = new Triangle3D(p1, p2, p3).distance(x, y, z);
            distMin = Math.min(distMin,  dist);
        }
        
        return distMin;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Box3D boundingBox()
    {
        // initialize to extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;
        
        // compute min max in each direction
        for (Point3D vertex : this.vertices)
        {
            xmin = Math.min(xmin, vertex.getX());
            xmax = Math.max(xmax, vertex.getX());
            ymin = Math.min(ymin, vertex.getY());
            ymax = Math.max(ymax, vertex.getY());
            zmin = Math.min(zmin, vertex.getZ());
            zmax = Math.max(zmax, vertex.getZ());
        }
        
        // create the resulting box
        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }
    
}
