/**
 * 
 */
package net.sci.geom.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
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
    
    /**
     * The array of edges. Each edge contains indices of source and target vertices.
     */
    ArrayList<Edge> edges = null;

    /**
     * Associates each edge to its linear index in the "edges" array.
     */
    TreeMap<Edge, Integer> edgeIndices = new TreeMap<>();

    /**
     * Indices of faces associated to each edge.
     */
    ArrayList<int[]> edgeFaces = null;
    
    
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
    
    
    // ===================================================================
    // Topological queries
    
    @Override
    public Collection<Mesh3D.Edge> vertexEdges(Mesh3D.Vertex vertex)
    {
        ensureValidEdges();
        // cast to local vertex class
        int index = getVertex(vertex).index;
        ArrayList<Mesh3D.Edge> edges = new ArrayList<>();
        for (Edge edge : this.edges)
        {
            if (edge.iv1 == index || edge.iv2 == index)
            {
                edges.add(edge);
            }
        }
        return edges;
    }

    @Override
    public Collection<Mesh3D.Face> vertexFaces(Mesh3D.Vertex vertex)
    {
        int index = getVertex(vertex).index;
        ArrayList<Mesh3D.Face> vertexFaces = new ArrayList<Mesh3D.Face>(6);
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            int[] inds = faces.get(iFace);
            if (inds[0] == index || inds[1] == index || inds[2] == index)
            {
                vertexFaces.add(new Face(iFace));
            }
        }
        return vertexFaces;
    }

    @Override
    public Collection<? extends Mesh3D.Vertex> vertexNeighbors(Mesh3D.Vertex vertex)
    {
        int index = getVertex(vertex).index;
        
        // identifies indices of neighbor vertices by iterating over faces
        TreeSet<Integer> neighInds = new TreeSet<>();
        for (int[] inds : faces)
        {
            if (inds[0] == index || inds[1] == index  || inds[2] == index)
            {
                if (inds[0] != index && !neighInds.contains(inds[0]))
                {
                    neighInds.add(inds[0]);
                }
                if (inds[1] != index && !neighInds.contains(inds[1]))
                {
                    neighInds.add(inds[1]);
                }
                if (inds[2] != index && !neighInds.contains(inds[2]))
                {
                    neighInds.add(inds[2]);
                }
            }
        }
        
        // convert to vertex collection
        ArrayList<Mesh3D.Vertex> vertices = new ArrayList<Mesh3D.Vertex>(neighInds.size());
        for (int ind : neighInds)
        {
            vertices.add(new Vertex(ind));
        }
        return vertices;
    }

    @Override
    public Collection<Mesh3D.Vertex> edgeVertices(Mesh3D.Edge edge)
    {
        Edge edge2 = getEdge(edge);
        return Arrays.asList(new Vertex(edge2.iv1), new Vertex(edge2.iv2));
    }

    @Override
    public Collection<Mesh3D.Face> edgeFaces(Mesh3D.Edge edge)
    {
        ensureValidEdgeFaces();
        int[] inds = edgeFaces.get(edgeIndices.get(getEdge(edge)));
        return Arrays.asList(new Face(inds[0]), new Face(inds[1]), new Face(inds[2]));
    }

    @Override
    public Collection<Mesh3D.Vertex> faceVertices(Mesh3D.Face face)
    {
        int[] inds = this.faces.get(getFace(face).index);
        return Arrays.asList(new Vertex(inds[0]), new Vertex(inds[1]), new Vertex(inds[2]));
    }

    @Override
    public Collection<Mesh3D.Edge> faceEdges(Mesh3D.Face face)
    {
        int[] indices = faces.get(getFace(face).index);
        ArrayList<Mesh3D.Edge> faceEdges = new ArrayList<Mesh3D.Edge>(3);
        faceEdges.add(new Edge(indices[0], indices[1]));
        faceEdges.add(new Edge(indices[1], indices[2]));
        faceEdges.add(new Edge(indices[2], indices[0]));
        return faceEdges;
    }
    
    // ===================================================================
    // Management of vertices
   
    @Override
    public int vertexNumber()
    {
        return vertexPositions.size();
    }

    @Override
    public Iterable<Mesh3D.Vertex> vertices()
    {
        return new Iterable<Mesh3D.Vertex>() {
            @Override
            public Iterator<Mesh3D.Vertex> iterator()
            {
                return new VertexIterator();
            }
        };
    }
  

    public Point3D vertexPosition(int index)
    {
        return vertexPositions.get(index);
    }

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
        int index = getVertex(vertex).index;
        for (int[] inds : faces)
        {
            if (inds[0] == index || inds[1] == index || inds[2] == index)
            {
                throw new RuntimeException("Can not remove a vertex if it belongs to a face");
            }
        }
        vertexPositions.remove(index);
    }

    /* (non-Javadoc)
     * @see Mesh3D#vertices()
     */
    public Collection<Point3D> vertexPositions()
    {
        return this.vertexPositions();
    }

    public Vertex getVertex(int index)
    {
        return new Vertex(index);
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
        ensureValidEdges();
        return edges.size();
    }

    @Override
    public Iterable<Mesh3D.Edge> edges()
    {
        ensureValidEdges();
        return new Iterable<Mesh3D.Edge>() {
            @Override
            public Iterator<Mesh3D.Edge> iterator()
            {
                return new EdgeIterator();
            }
        };
    }

    /**
     * Adds an edge between a source and a target vertices.
     * 
     * @param v1
     *            the source vertex (0-based)
     * @param v2
     *            the target vertex (0-based)
     * @return the index of the newly created edge
     */
    public Edge addEdge(Mesh3D.Vertex v1, Mesh3D.Vertex v2)
    {
        // create new edge
        Edge edge = new Edge((Vertex) v1, (Vertex) v2); 
        
        // ensure edge structure is created
        if (edges == null)
        {
            this.edges = new ArrayList<>();
            this.edgeIndices = new TreeMap<>();
        }
        
        // add new edge to mesh
        int index = edges.size();
        edges.add(edge);
        edgeIndices.put(edge, index);
        
        // return edge instance
        return edge;
    }

    @Override
    public void removeEdge(Mesh3D.Edge edge)
    {
        Edge edge2 = getEdge(edge);
        int index = edgeIndices.get(edge2);
        this.edges.remove(index);
        this.edgeIndices.remove(edge2);
    }

    public Edge getEdge(int index)
    {
        ensureValidEdges();
        return edges.get(index);
    }

    /**
     * Cast to local Edge class
     * 
     * @param edge
     *            the Edge instance
     * @return the same instance casted to local Edge implementation
     */
    private Edge getEdge(Mesh3D.Edge edge)
    {
        if (!(edge instanceof Edge))
        {
            throw new IllegalArgumentException("Edge should be an instance of inner Edge implementation");
        }
        return (Edge) edge;
    }

    /**
     * Ensures the "edges" information is created, and recomputes edge array if
     * it is null.
     */
    private void ensureValidEdges()
    {
        if (edges == null)
        {
            computeEdges();
        }
    }

    private void computeEdges()
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
                
        // create structures for storing edge information
        this.edges = new ArrayList<Edge>(nEdges);
        this.edgeIndices = new TreeMap<>();
    
        // Convert to an array of edges
        int index = 0;
        for (int iv1 = 0; iv1 < nv - 1; iv1++)
        {
            for (int iv2 : vertexAdjList.get(iv1))
            {
                Edge edge = new Edge(iv1, iv2);
                this.edges.add(edge);
                this.edgeIndices.put(edge, index++);
            }
        }
    }

    /**
     * Ensures the "edgeFaces" information is created, and recomputes edgeFaces
     * array if it is null.
     */
    private void ensureValidEdgeFaces()
    {
        if (edgeFaces == null)
        {
            computeEdgeFaces();
        }
    }

    /**
     * Computes the faces adjacent to each edge of the mesh.
     * 
     * Populates the edgeFaces array.
     */
    private void computeEdgeFaces()
    {
        ensureValidEdges();
        
        // allocate memory
        int nEdges = edges.size();
        this.edgeFaces = new ArrayList<>(nEdges);
        
        // populate face array for each edge
        for (int iEdge = 0; iEdge < nEdges; iEdge++)
        {
            int[] inds = new int[] {-1, -1};
            this.edgeFaces.add(inds);
        }
        
        // Iterate over faces
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            // vertex indices of the current face
            int[] inds = faces.get(iFace);
            
            // iterate over the three vertices of current face
            for (int iVertex = 0; iVertex < 3; iVertex++)
            {
                // vertex indices
                int iv1 = inds[iVertex];
                int iv2 = inds[(iVertex + 1) % 3];
                
                // identify edge index
                Edge edge = new Edge(iv1, iv2);
                int edgeIndex = -1;
                if(edgeIndices.containsKey(edge))
                {
                    edgeIndex = edgeIndices.get(edge); 
                }
                else
                {
                    throw new RuntimeException(String.format("Could not find index of edge with vertex indices (%d;%d)", iv1, iv2));
                }
                
                // get indices of adjacent faces associated to current edge
                int[] faces = edgeFaces.get(edgeIndex);
                
                // Switch processing depending on face side
                if (edge.iv1 == iv1)
                {
                    // current face on the left side of the edge
                    if (faces[0] != -1)
                    {
                        throw new RuntimeException(String.format("Tow faces were found on left side of edge %d (%d;%d)", edgeIndex, iv1, iv2));
                    }
                    faces[0] = iFace;
                }
                else
                {
                    // current face on the right side of the edge
                    if (faces[1] != -1)
                    {
                        throw new RuntimeException(String.format("Tow faces were found on right side of edge %d (%d;%d)", edgeIndex, iv1, iv2));
                    }
                    faces[1] = iFace;
                }
                
                edgeFaces.set(edgeIndex, faces);
            }
        }
    }
    
    // ===================================================================
    // Management of faces
    
    @Override
    public Iterable<Mesh3D.Face> faces()
    {
        return new Iterable<Mesh3D.Face>() {
            @Override
            public Iterator<Mesh3D.Face> iterator()
            {
                return new FaceIterator();
            }
        };
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
     *            the first face vertex
     * @param v2
     *            the second face vertex
     * @param v3
     *            the third face vertex
     * @return the newly created face
     */
    public Face addFace(Mesh3D.Vertex v1, Mesh3D.Vertex v2, Mesh3D.Vertex v3)
    {
        int iv1 = ((Vertex) v1).index;
        int iv2 = ((Vertex) v2).index;
        int iv3 = ((Vertex) v3).index;
        return addFace(iv1, iv2, iv3);
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
    public Face addFace(int iv1, int iv2, int iv3)
    {
        int index = faces.size();
        faces.add(new int[] { iv1, iv2, iv3 });
        
        // clear edge information as it is now outdated
        this.edges = null;
        
        return new Face(index);
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
        return new Face(index);
    }
    
    /**
	 * Returns the index of the specified face.
	 * 
	 * @param face
	 *            a face belonging to this mesh.
	 * @return the index of the face in the face array.
	 * @throws RuntimeException if the face does not belong to the mesh.
	 */
    public int indexOf(Mesh3D.Face face)
    {
    	if (face instanceof Face)
    	{
    		Face face2 = (Face) face;
            if (face2.mesh() == this)
            {
    			return face2.index;		
            }
    	}

    	throw new RuntimeException("face does not belong to mesh");
    }

    @Override
    public void removeFace(Mesh3D.Face face)
    {
        // Cast to local Face class
        Face face2 = getFace(face);
        this.faces.remove(face2.index);
    }

    /**
     * Cast to local Face class
     * 
     * @param face
     *            the Face instance
     * @return the same instance casted to local Face implementation
     */
    private Face getFace(Mesh3D.Face face)
    {
        if (!(face instanceof Face))
        {
            throw new IllegalArgumentException("Face should be an instance of inner Face implementation");
        }
        return (Face) face;
    }
    
    // ===================================================================
    // Management of edges

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
        public Point3D position()
        {
            return vertexPositions.get(index);
        }

        @Override
        public Vector3D normal()
        {
            Vector3D normal = new Vector3D();
            for (Mesh3D.Face face : vertexFaces(this))
            {
                normal.plus(face.normal());
            }
            return normal.normalize();
        }
        
        @Override
        public Mesh3D mesh()
        {
        	return DefaultTriMesh3D.this;
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
            if (this.index != that.index) return false;
            if (this.mesh() != that.mesh()) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            return this.index + 17;
        }
    }

    
    private class VertexIterator implements Iterator<Mesh3D.Vertex>
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
    }

    public class Face implements Mesh3D.Face
    {
        /**
         * The index of the face, used to retrieve index vertices in "faces" array.
         */
        int index;

        public Face(int index)
        {
            this.index = index;
        }

        @Override
        public Triangle3D polygon()
        {
            int[] indices = faces.get(this.index);
            Point3D p1 = vertexPositions.get(indices[0]);
            Point3D p2 = vertexPositions.get(indices[1]);
            Point3D p3 = vertexPositions.get(indices[2]);
            
            return new Triangle3D(p1, p2, p3);    
        }
        
        @Override
        public Vector3D normal()
        {
            int[] indices = faces.get(this.index);
            Point3D p1 = vertexPositions.get(indices[0]);
            Vector3D v12 = new Vector3D(p1, vertexPosition(indices[1]));
            Vector3D v13 = new Vector3D(p1, vertexPosition(indices[2]));
            return Vector3D.crossProduct(v12, v13);
        }
        
        @Override
        public Mesh3D mesh()
        {
        	return DefaultTriMesh3D.this;
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
            if (this.index != that.index) return false;
            if (this.mesh() != that.mesh()) return false;
            return true;
        }
        
        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + index;
            return hash;
        }
    }

    
    private class FaceIterator implements Iterator<Mesh3D.Face>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < faces.size();
        }
        
        @Override
        public Mesh3D.Face next()
        {
            return new Face(index++);
        }
    }

    public class Edge implements Mesh3D.Edge, Comparable<Edge>
    {
        /** index of first vertex  (iv1 < iv2) */
        int iv1;
        
        /** index of second vertex (iv1 < iv2) */
        int iv2;

        public Edge(Vertex v1, Vertex v2)
        {
            this(v1.index, v2.index);
        }
        
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
        
        @Override
        public Mesh3D.Vertex source()
        {
            return new Vertex(iv1);
        }

        @Override
        public Mesh3D.Vertex target()
        {
            return new Vertex(iv2);
        }

        public LineSegment3D curve()
        {
            Point3D p1 = vertexPosition(iv1);
            Point3D p2 = vertexPosition(iv2);
            return new LineSegment3D(p1, p2);
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

        @Override
        public Mesh3D mesh()
        {
        	return DefaultTriMesh3D.this;
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
            if (this.mesh() != that.mesh()) return false;
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
    
    private class EdgeIterator implements Iterator<Mesh3D.Edge>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < edges.size();
        }
        
        @Override
        public Mesh3D.Edge next()
        {
            return edges.get(index++);
        }
    }

}
