/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.ArrayList;
import java.util.Arrays;
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

    /**
     * The position of the vertices. 
     */
    // TODO: allow null elements ?
    ArrayList<Point3D> vertexPositions;
    
    /**
     * For each face, the triplet of vertex indices.
     */
    ArrayList<int[]> faces;
    
    
    // ===================================================================
    // Constructors

    /**
     * Create a new empty mesh (no vertex, no face).
     */
    public DefaultTriMesh3D()
    {
        this.vertexPositions = new ArrayList<Point3D>();
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
            Point3D v1 = this.vertexPositions.get(faceIndices[0]);
            Point3D v2 = this.vertexPositions.get(faceIndices[1]);
            Point3D v3 = this.vertexPositions.get(faceIndices[2]);
            
            Vector3D v12 = new Vector3D(v1, v2);
            Vector3D v13 = new Vector3D(v1, v3);
            surf += Vector3D.crossProduct(v12,  v13).norm();
        }

        return surf / 2;
    }


    // ===================================================================
    // Management of vertices
   
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
        for (int i = 0; i < vertexPositions.size(); i++)
        {
            double dist = vertexPositions.get(i).distance(point);
            if (dist < minDist)
            {
                minDist = dist;
                index = i;
            }
        }
        return index;
    }
    
    public Point3D vertexPosition(int index)
    {
        return vertexPositions.get(index);
    }
    
    /**
     * Adds a triangular face defined by the indices of its three vertices.
     * 
     * @param iv1 index of the first face vertex (0-based)
     * @param iv2 index of the second face vertex (0-based)
     * @param iv3 index of the third face vertex (0-based)
     * @return the index of the newly created face
     */
    public int addFace(Vertex v1, Vertex v2, Vertex v3)
    {
        int iv1 = v1.index;
        int iv2 = v2.index;
        int iv3 = v3.index;
        int index = addFace(iv1, iv2, iv3);
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
        Point3D p1 = this.vertexPositions.get(inds[0]);
        Point3D p2 = this.vertexPositions.get(inds[1]);
        Point3D p3 = this.vertexPositions.get(inds[2]);
        
        return new Triangle3D(p1, p2, p3);
    }
    

    // ===================================================================
    // Implementation of the Mesh3D interface

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
    public Vertex addVertex(Point3D position)
    {
        int index = vertexPositions.size();
        vertexPositions.add(position);
        return new Vertex(index);
    }

    @Override
    public Collection<Vertex> vertices()
    {
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>(vertexPositions.size());
        for (int i = 0; i < vertexPositions.size(); i++)
        {
            if (vertexPositions.get(i) != null)
            {
                vertexList.add(new Vertex(i));
            }
        }
        
        return vertexList;
    }
  
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
        return vertexPositions.size();
    }

    @Override
    public Collection<? extends Face> faces()
    {
        ArrayList<Face> faceList = new ArrayList<Face>(faces.size());
        for (int[] inds : faces)
        {
            if (inds != null)
            {
                faceList.add(new Face(inds[0], inds[1], inds[2]));
            }
        }
        return faceList;
    }

    @Override
    public int faceNumber()
    {
        return faces.size();
    }

    
    // ===================================================================
    // Implementation of the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        for (int[] inds : faces)
        {
            Point3D p1 = this.vertexPositions.get(inds[0]);
            Point3D p2 = this.vertexPositions.get(inds[1]);
            Point3D p3 = this.vertexPositions.get(inds[2]);
            
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
        
        for (int[] inds : faces)
        {
            Point3D p1 = this.vertexPositions.get(inds[0]);
            Point3D p2 = this.vertexPositions.get(inds[1]);
            Point3D p3 = this.vertexPositions.get(inds[2]);
            
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
        for (Point3D vertex : this.vertexPositions)
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
 
    private class Vertex implements Mesh3D.Vertex
    {
        // the index of the vertex
        int index;

        public Vertex(int index)
        {
            this.index = index;
        }
        
        @Override
        public Collection<Face> faces()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Collection<Edge> edges()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Point3D position()
        {
            return vertexPositions.get(index);
        }

        @Override
        public Vector3D normal()
        {
            // TODO Auto-generated method stub
            return null;
        }
        
        
        // TODO: implements equals+hashcode
    }
    
    private class Face implements Mesh3D.Face
    {
        // index of first vertex
        int iv1;
        // index of second vertex
        int iv2;
        // index of third vertex
        int iv3;

        public Face(int iv1, int iv2, int iv3)
        {
            this.iv1 = iv1;
            this.iv2 = iv2;
            this.iv3 = iv3;
        }

        @Override
        public Collection<Vertex> vertices()
        {
            return Arrays.asList(new Vertex(iv1), new Vertex(iv2), new Vertex(iv3));
        }

        @Override
        public Collection<Edge> edges()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Vector3D normal()
        {
            Point3D p1 = vertexPositions.get(iv1);
            Vector3D v12 = new Vector3D(p1, vertexPosition(iv2));
            Vector3D v13 = new Vector3D(p1, vertexPosition(iv3));
            return Vector3D.crossProduct(v12, v13);
        }
        
        // TODO: implements equals+hashcode
    }
}
