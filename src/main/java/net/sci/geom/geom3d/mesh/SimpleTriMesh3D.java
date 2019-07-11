/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * A simple class for representing immutable triangular meshes in 3D.
 * 
 * Vertices are stored in an ArrayList. Faces are stored in an ArrayList of integer triplets.
 * 
 * @author dlegland
 *
 */
public class SimpleTriMesh3D implements Mesh3D
{
    // ===================================================================
    // Class variables

    /**
     * The position of the vertices. 
     */
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
    public SimpleTriMesh3D()
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
    
    

    // ===================================================================
    // Management of vertices
    
    @Override
    public int vertexNumber()
    {
        return vertexPositions.size();
    }

    @Override
    public Vertices vertices()
    {
        return new Vertices();
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
    public Vertex addVertex(Point3D position)
    {
        int index = vertexPositions.size();
        vertexPositions.add(position);
        return new Vertex(index);
    }

    @Override
    public void removeVertex(Mesh3D.Vertex vertex)
    {
        throw new UnsupportedOperationException("This implementation does not support vertex removal");
    }

    public Vertex getVertex(int index)
    {
        return new Vertex(index);
    }

    public Point3D vertexPosition(int index)
    {
        return vertexPositions.get(index);
    }

    public Collection<Point3D> vertexPositions()
    {
        return vertexPositions();
    }

    /**
     * Cast to local Vertex class
     * 
     * @param vertex
     *            the Vertex instance
     * @return the same instance casted to local Vertex implementation
     */
    private Vertex getVertex(Mesh3D.Vertex vertex)
    {
        if (!(vertex instanceof Vertex))
        {
            throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
        }
        return (Vertex) vertex;
    }


    // ===================================================================
    // Management of edges
    
    @Override
    public int edgeNumber()
    {
        return 0;
    }

    public Edges edges()
    {
        throw new UnsupportedOperationException("This implementation does not support edges");
    }

    @Override
    public Edge addEdge(Mesh3D.Vertex v1, Mesh3D.Vertex v2)
    {
        throw new UnsupportedOperationException("This implementation does not support edges");
    }

    @Override
    public void removeEdge(Edge edge)
    {
        throw new UnsupportedOperationException("This implementation does not support edges");
    }


    // ===================================================================
    // Management of faces
    
    @Override
    public Faces faces()
    {
        return new Faces();
    }
    
    @Override
    public int faceNumber()
    {
        return faces.size();
    }

    /**
     * Adds a triangular face defined by references to its three vertices.
     * 
     * @param v1
     *            reference to the first face vertex
     * @param v2
     *            reference to the second face vertex
     * @param v3
     *            reference to the third face vertex
     * @return the index of the newly created face
     */
    public int addFace(Mesh3D.Vertex v1, Mesh3D.Vertex v2, Mesh3D.Vertex v3)
    {
        int iv1 = getVertex(v1).index;
        int iv2 = getVertex(v2).index;
        int iv3 = getVertex(v3).index;
        int index = addFace(iv1, iv2, iv3);
        return index;
    }

    /**
     * Adds a triangular face defined by the indices of its three vertices.
     * 
     * @param iv1
     *            index of the first face vertex (0-based)
     * @param iv2
     *            index of the second face vertex (0-based)
     * @param iv3
     *            index of the third face vertex (0-based)
     * @return the index of the newly created face
     */
    public int addFace(int iv1, int iv2, int iv3)
    {
        int index = faces.size();
        faces.add(new int[] { iv1, iv2, iv3 });
        return index;
    }

    @Override
    public void removeFace(Mesh3D.Face face)
    {
        throw new UnsupportedOperationException("This implementation does not support face removal");
    }

    public Triangle3D getFacePolygon(int faceIndex)
    {
        int[] inds = faces.get(faceIndex);
        Point3D p1 = this.vertexPositions.get(inds[0]);
        Point3D p2 = this.vertexPositions.get(inds[1]);
        Point3D p3 = this.vertexPositions.get(inds[2]);
        
        return new Triangle3D(p1, p2, p3);
    }

    public Face getFace(int index)
    {
        int[] inds = this.faces.get(index);
        return new Face(inds[0], inds[1], inds[2]);
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
 
    public class Vertex implements Mesh3D.Vertex
    {
        // the index of the vertex
        int index;

        public Vertex(int index)
        {
            this.index = index;
        }
        
        @Override
        public Collection<Mesh3D.Face> faces()
        {
            // Allocate typical number of neighbor edge equal to 6.
            ArrayList<Mesh3D.Face> vertexFaces = new ArrayList<>(6);
            
            // iterate over the collection of faces
            for (int[] inds : faces)
            {
                if (inds[0] == this.index || inds[1] == this.index || inds[2] == this.index)
                {
                    vertexFaces.add(new Face(inds[0], inds[1], inds[2]));
                }
            }
            
            // return edges around vertex
            return vertexFaces;
        }

        @Override
        public Collection<Mesh3D.Edge> edges()
        {
            throw new UnsupportedOperationException("This implementation does not support edges");
        }

        @Override
        public Point3D position()
        {
            return vertexPositions.get(index);
        }

        
        // ===================================================================
        // Override equals and hashcode to allow indexing
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Vertex))
            {
                return false;
            }
            
            Vertex that = (Vertex) obj;
            return this.index == that.index;
        }
        
        @Override
        public int hashCode()
        {
            return this.index + 17;
        }
    }
    

    /**
     * The collection of vertices stored in a mesh.
     */
    public class Vertices implements Mesh3D.Vertices
    {
        public Vertex get(int index)
        {
            return new Vertex(index);
        }
        
        public Point3D position(int index)
        {
            return vertexPositions.get(index);
        }
        
        public int size()
        {
            return vertexPositions.size();
        }

        @Override
        public Iterator<Mesh3D.Vertex> iterator()
        {
            return new Iterator<Mesh3D.Vertex>()
            {
                int index = 0;
                @Override
                public boolean hasNext()
                {
                    return index < vertexPositions.size();
                }

                @Override
                public Vertex next()
                {
                    return new Vertex(index++);
                }
            };
        }
        
    }

    public class Face implements Mesh3D.Face
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
        public Triangle3D polygon()
        {
            Point3D p1 = vertexPositions.get(this.iv1);
            Point3D p2 = vertexPositions.get(this.iv2);
            Point3D p3 = vertexPositions.get(this.iv3);
            
            return new Triangle3D(p1, p2, p3);    
        }
        
        public int[] vertexIndices()
        {
            return new int[] {iv1, iv2, iv3};
        }

        @Override
        public Collection<Vertex> vertices()
        {
            return Arrays.asList(new Vertex(iv1), new Vertex(iv2), new Vertex(iv3));
        }

        @Override
        public Collection<? extends Mesh3D.Edge> edges()
        {
            throw new UnsupportedOperationException("This implementation does not support edges");
        }

        @Override
        public Vector3D normal()
        {
            Point3D p1 = vertexPositions.get(iv1);
            Vector3D v12 = new Vector3D(p1, vertexPosition(iv2));
            Vector3D v13 = new Vector3D(p1, vertexPosition(iv3));
            return Vector3D.crossProduct(v12, v13);
        }
        
        // ===================================================================
        // Override equals and hashcode to allow indexing
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Face))
            {
                return false;
            }
            
            Face that = (Face) obj;
            if (this.iv1 != that.iv1) return false;
            if (this.iv2 != that.iv2) return false;
            if (this.iv3 != that.iv3) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + iv1;
            hash = hash * 17 + iv2;
            hash = hash * 17 + iv3;
            return hash;
        }
    }

    public class Faces implements Mesh3D.Faces
    {
        public Face get(int index)
        {
            int[] inds = faces.get(index);
            return new Face(inds[0], inds[1], inds[2]);
        }
        
        public int[] vertexIndices(int index)
        {
            return faces.get(index);
        }
        
        public int size()
        {
            return faces.size();
        }

        @Override
        public Iterator<Mesh3D.Face> iterator()
        {
            return new Iterator<Mesh3D.Face>()
            {
                int index = 0;
                @Override
                public boolean hasNext()
                {
                    return index < faces.size();
                }

                @Override
                public Face next()
                {
                    int[] inds = faces.get(index++);
                    return new Face(inds[0], inds[1], inds[2]);
                }
            };
        }

    }

}
