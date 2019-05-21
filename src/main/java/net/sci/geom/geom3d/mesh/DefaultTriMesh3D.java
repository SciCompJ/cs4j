/**
 * 
 */
package net.sci.geom.geom3d.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.LineSegment3D;
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
    ArrayList<Point3D> vertexPositions;
    
    /**
     * For each face, the triplet of vertex indices.
     */
    ArrayList<int[]> faces;
    
    //TODO: use a TreeSet instead?
    ArrayList<Edge> edges = null;
    
    
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
    
    public Vertex getVertex(int index)
    {
        return new Vertex(index);
    }

    // ===================================================================
    // Management of faces
    
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
        
        // clear edge information as it is now outdated
        this.edges = null;
        
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
    
    public Face getFace(int index)
    {
        int[] inds = this.faces.get(index);
        return new Face(inds[0], inds[1], inds[2]);
    }
    
    // ===================================================================
    // Management of edges
    
    public Edge getEdge(int index)
    {
        if (edges == null)
        {
            computeEdgeVertices();
        }
        return edges.get(index);
    }
    

    public Collection<Edge> edges()
    {
        if (edges == null)
        {
            computeEdgeVertices();
        }
        return edges;
    }

    private void computeEdgeVertices()
    {
        // number of vertices
        int nv = this.vertexPositions.size();

        // Creates adjacency data structure: for each vertex, keep the list of
        // adjacent vertices with greater index
        ArrayList<TreeSet<Integer>> vertexAdjList = new ArrayList<TreeSet<Integer>>(nv-1);

        // Initialize vertex adjacency list with a small number of vertices
        for (int iv = 0; iv < nv-1; iv++)
        {
            vertexAdjList.add(new TreeSet<Integer>());
        }
        
        // Iterate over faces to create edges
        int nEdges = 0;
        for (int[] inds : this.faces)
        {
            // iterate over pairs of consecutive indices
            for (int i = 0; i < 3; i++)
            {
                int iv1 = inds[i];
                int iv2 = inds[(i + 1) % 3];
                
                // make sure iv1 is lower than iv2
                if (iv1 > iv2)
                {
                    int tmp = iv1;
                    iv1 = iv2;
                    iv2 = tmp;
                }
                
                TreeSet<Integer> adjVertices = vertexAdjList.get(iv1);
                if (!adjVertices.contains(iv2))
                {
                    adjVertices.add(iv2);
                    nEdges++;
                }
            }
        }
        
        // Convert to an array of edges
        this.edges = new ArrayList<Edge>(nEdges);
        for (int iv1 = 0; iv1 < nv - 1; iv1++)
        {
            for (int iv2 : vertexAdjList.get(iv1))
            {
                this.edges.add(new Edge(iv1, iv2));
            }
        }
    }
    
    
    // ===================================================================
    // Implementation of the Mesh3D interface

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
    public Vertices vertices()
    {
        return new Vertices();
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

//    /* (non-Javadoc)
//     * @see net.sci.geom.geom3d.mesh.Mesh3D#vertexNumber()
//     */
//    @Override
//    public int vertexNumber()
//    {
//        return vertexPositions.size();
//    }

    @Override
    public Faces faces()
    {
        return new Faces();
    }

//    @Override
//    public int faceNumber()
//    {
//        return faces.size();
//    }

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
        public Collection<Face> faces()
        {
            // Allocate typical number of neighbor edge equal to 6.
            ArrayList<Face> vertexFaces = new ArrayList<Face>(6);
            
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
        public Collection<Edge> edges()
        {
            // Allocate typical number of neighbor edge equal to 6.
            ArrayList<Edge> vertexEdges = new ArrayList<Edge>(6);
            
            // iterate over the collection of edges
            for (Edge edge : edges)
            {
                if (edge.iv1 == this.index || edge.iv2 == this.index)
                {
                    vertexEdges.add(edge);
                }
            }
            
            // return edges around vertex
            return vertexEdges;
        }

        @Override
        public Point3D position()
        {
            return vertexPositions.get(index);
        }

        @Override
        public Vector3D normal()
        {
            Vector3D normal = new Vector3D();
            for (Face face : this.faces())
            {
                normal.plus(face.normal());
            }
            return normal.normalize();
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
        public Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Vertex> iterator()
        {
            return new Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Vertex>()
            {
                int index = 0;
                @Override
                public boolean hasNext()
                {
                    return index < vertexPositions.size();
                }

                @Override
                public net.sci.geom.geom3d.mesh.Mesh3D.Vertex next()
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
        
        @Override
        public Collection<Vertex> vertices()
        {
            return Arrays.asList(new Vertex(iv1), new Vertex(iv2), new Vertex(iv3));
        }

        @Override
        public Collection<Edge> edges()
        {
            ArrayList<Edge> faceEdges = new ArrayList<Edge>(3);
            faceEdges.add(new Edge(iv1, iv2));
            faceEdges.add(new Edge(iv2, iv3));
            faceEdges.add(new Edge(iv3, iv1));
            return faceEdges;
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
        public Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Face> iterator()
        {
            return new Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Face>()
            {
                int index = 0;
                @Override
                public boolean hasNext()
                {
                    return index < faces.size();
                }

                @Override
                public net.sci.geom.geom3d.mesh.Mesh3D.Face next()
                {
                    int[] inds = faces.get(index++);
                    return new Face(inds[0], inds[1], inds[2]);
                }
            };
        }

    }
    
    public class Edge implements Mesh3D.Edge, Comparable<Edge>
    {
        /** index of first vertex  (iv1 < iv2) */
        int iv1;
        
        /** index of second vertex (iv1 < iv2) */
        int iv2;

        public Edge(int iv1, int iv2)
        {
            if (iv1 < iv2)
            {
                this.iv1 = iv1;
                this.iv2 = iv2;
            }
            else
            {
                this.iv1 = iv2;
                this.iv2 = iv1;
            }
        }
        
        public LineSegment3D curve()
        {
            Point3D p1 = vertexPosition(iv1);
            Point3D p2 = vertexPosition(iv2);
            return new LineSegment3D(p1, p2);
        }
        
        @Override
        public Collection<Vertex> vertices()
        {
            return Arrays.asList(new Vertex(iv1), new Vertex(iv2));
        }
        
        @Override
        public Collection<Face> faces()
        {
            // TODO: complete me!
            ArrayList<Face> edgeFaces = new ArrayList<Face>(2);
//            if (if1 != -1) edgeFaces.add(getFace(if1));
//            if (if2 != -1) edgeFaces.add(getFace(if2));
            return edgeFaces;
        }

        
        /**
         * Implements compareTo to allows for fast indexing.
         */
        @Override
        public int compareTo(Edge that)
        {
            int diff = this.iv1 - that.iv1;
            if (diff != 0)
                return diff;
            return this.iv2 - that.iv2;
        }

        // ===================================================================
        // Override equals and hashcode to allow indexing
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Edge))
            {
                return false;
            }
            
            Edge that = (Edge) obj;
            if (this.iv1 != that.iv1) return false;
            if (this.iv2 != that.iv2) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + iv1;
            hash = hash * 17 + iv2;
            return hash;
        }
    }
    
    public class Edges implements Mesh3D.Edges
    {
         @Override
        public int size()
        {
            return edges.size();
        }
        
       @Override
        public Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Edge> iterator()
        {
           return new Iterator<net.sci.geom.geom3d.mesh.Mesh3D.Edge>()
           {
               int index = 0;
               @Override
               public boolean hasNext()
               {
                   return index < edges.size();
               }

               @Override
               public net.sci.geom.geom3d.mesh.Mesh3D.Edge next()
               {
                   return edges.get(index);
               }
           };
        }

    }
}
