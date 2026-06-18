/**
 * 
 */
package net.sci.geom.mesh2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.IntStream;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.Polygon2D;

/**
 * Implementation of a polygonal 2D mesh where faces may have an arbitrary
 * number of vertices.
 * 
 * 
 */
public class SimplePolygonalMesh2D implements Mesh2D
{
    // ===================================================================
    // Class variables

    /**
     * The position of the vertices. 
     */
    ArrayList<Point2D> vertexPositions;
    
    /**
     * For each face, the list of vertex indices.
     */
    ArrayList<int[]> faces;
    
    
    // ===================================================================
    // Constructors

    /**
     * Create a new empty mesh (no vertex, no face).
     */
    public SimplePolygonalMesh2D()
    {
        this.vertexPositions = new ArrayList<Point2D>();
        this.faces = new ArrayList<int[]>();
    }
    
    /**
     * Create a new polygonal mesh by providing the collection of vertices and
     * the collection face vertex indices.
     * 
     * @param vertices
     *            the position of mesh vertices
     * @param faces
     *            the collection of vertex indices for each face
     */
    public SimplePolygonalMesh2D(Collection<Point2D> vertices, Collection<int[]> faces)
    {
        this.vertexPositions = new ArrayList<Point2D>(vertices.size());
        this.vertexPositions.addAll(vertices);
        this.faces = new ArrayList<int[]>(faces.size());
        this.faces.addAll(faces);
    }
    
    /**
     * Create a new empty mesh by allocating enough memory for storing the
     * specified amount of vertices and faces.
     * 
     * @param nv
     *            the number of vertices
     * @param nf
     *            the number of faces
     */
    public SimplePolygonalMesh2D(int nv, int nf)
    {
        this.vertexPositions = new ArrayList<Point2D>(nv);
        this.faces = new ArrayList<int[]>(nf);
    }

    
    // ===================================================================
    // Methods specific to Mesh2D
    

    
    // ===================================================================
    // Topological queries
    
    @Override
    public Collection<Mesh2D.Face> vertexFaces(Mesh2D.Vertex vertex)
    {
        int index = getVertex(vertex).index;
        ArrayList<Mesh2D.Face> vertexFaces = new ArrayList<Mesh2D.Face>(6);
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            int[] inds = faces.get(iFace);
            for (int ind : inds)
            {
                if (index == ind)
                {
                    vertexFaces.add(new Face(iFace));
                    break;
                }
            }
        }
        return vertexFaces;
    }

    @Override
    public Collection<? extends Mesh2D.Vertex> vertexNeighbors(Mesh2D.Vertex vertex)
    {
        int index = getVertex(vertex).index;
        
        // identifies indices of neighbor vertices by iterating over faces
        TreeSet<Integer> neighInds = new TreeSet<>();
        for (int[] faceVertices : faces)
        {
            int pos = indexOf(index, faceVertices);
            if (pos > 0)
            {
                int nvf = faceVertices.length;
                int indNext = faceVertices[(pos + 1) % nvf];
                if(!neighInds.contains(indNext)) neighInds.add(indNext);
                int indPrev = faceVertices[(pos + nvf - 1) % nvf];
                if(!neighInds.contains(indPrev)) neighInds.add(indPrev);
            }
        }
        
        // convert to vertex collection
        ArrayList<Mesh2D.Vertex> vertices = new ArrayList<Mesh2D.Vertex>(neighInds.size());
        for (int ind : neighInds)
        {
            vertices.add(new Vertex(ind));
        }
        return vertices;
    }
    
    /**
     * Finds the index of the first occurrence of the value within the specified
     * array, or -1 if the array does not contain the value.
     * 
     * @param value
     *            the value to query
     * @param array
     *            an array of values
     * @return the index of the first occurrence, or -1
     */
    private static final int indexOf(int value, int[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            if (array[i] == value) return i;
        }
        return -1;
    }

    @Override
    public Collection<? extends Mesh2D.Vertex> faceVertices(Mesh2D.Face face)
    {
        int[] inds = faces.get(getFace(face).index);
        return IntStream.of(inds).mapToObj(i -> new Vertex(i)).toList();
    }


    // ===================================================================
    // Management of vertices

    @Override
    public int vertexCount()
    {
        return vertexPositions.size();
    }

    @Override
    public Iterable<Mesh2D.Vertex> vertices()
    {
        return new Iterable<Mesh2D.Vertex>() {
            @Override
            public Iterator<Mesh2D.Vertex> iterator()
            {
                return new VertexIterator();
            }
        };
    }
  
    /**
     * Adds a vertex to the mesh and returns the index associated to its
     * position.
     * 
     * @param position
     *            the position of the new vertex
     * @return the index of the new vertex
     */
    public Vertex addVertex(Point2D position)
    {
        int index = vertexPositions.size();
        vertexPositions.add(position);
        return new Vertex(index);
    }

    @Override
    public void removeVertex(Mesh2D.Vertex vertex)
    {
        throw new UnsupportedOperationException("This implementation does not support vertex removal");
    }

    public Vertex getVertex(int index)
    {
        return new Vertex(index);
    }
    
    /**
     * Returns the index of the specified vertex.
     * 
     * @param vertex
     *            a vertex belonging to this mesh.
     * @return the index of the vertex in the vertex array.
     * @throws RuntimeException
     *             if the vertex does not belong to the mesh.
     */
    public int indexOf(Mesh2D.Vertex vertex)
    {
        if (vertex instanceof Vertex)
        {
            return ((Vertex) vertex).index;
        }
        
        throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
    }

    public Point2D vertexPosition(int index)
    {
        return vertexPositions.get(index);
    }

    @Override
    public Collection<Point2D> vertexPositions()
    {
        return vertexPositions;
    }

    /**
     * Cast to local Vertex class
     * 
     * @param vertex
     *            the Vertex instance
     * @return the same instance casted to local Vertex implementation
     */
    private Vertex getVertex(Mesh2D.Vertex vertex)
    {
        if (!(vertex instanceof Vertex))
        {
            throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
        }
        return (Vertex) vertex;
    }


    // ===================================================================
    // Management of faces
    
    @Override
    public Iterable<Mesh2D.Face> faces()
    {
        return new Iterable<Mesh2D.Face>() {
            @Override
            public Iterator<Mesh2D.Face> iterator()
            {
                return new FaceIterator();
            }
        };
    }
  
    @Override
    public int faceCount()
    {
        return faces.size();
    }

    /**
     * Adds a triangular face defined by references to its three vertices.
     * 
     * @param vertices
     *            reference to the vertices of the face
     * @return the newly created face
     */
    public Mesh2D.Face addFace(Mesh2D.Vertex[] vertices)
    {
        int[] inds = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++)
        {
            inds[i] = indexOf(vertices[i]);
        }
        int index = addFace(inds);
        return new Face(index);
    }

    /**
     * Adds a new face defined by the indices of its vertices.
     * 
     * @param indices
     *            the array of vertex indices
     * @return the index of the newly created face
     */
    public int addFace(int[] indices)
    {
        int index = faces.size();
        faces.add(indices);
        return index;
    }

    @Override
    public void removeFace(Mesh2D.Face face)
    {
        throw new UnsupportedOperationException("This implementation does not support face removal");
    }

    public Polygon2D getFacePolygon(int faceIndex)
    {
        List<Point2D> pts = IntStream.of(faces.get(faceIndex))
                .mapToObj(ind -> this.vertexPositions.get(ind))
                .toList();
        return Polygon2D.create(pts);
    }

    public Face getFace(int index)
    {
        return new Face(index);
    }

    /**
     * Returns the index of the specified vertex.
     * 
     * @param face
     *            a face belonging to this mesh.
     * @return the index of the face in the face array.
     * @throws RuntimeException if the face does not belong to the mesh.
     */
    public int indexOf(Mesh2D.Face face)
    {
        if (face instanceof Face)
        {
            return ((Face) face).index;     
        }

        throw new IllegalArgumentException("Face should be an instance of inner Face implementation");
    }

    /**
     * Cast to local Face class
     * 
     * @param face
     *            the Face instance
     * @return the same instance casted to local Face implementation
     */
    private Face getFace(Mesh2D.Face face)
    {
        if (!(face instanceof Face))
        {
            throw new IllegalArgumentException("Face should be an instance of inner Face implementation");
        }
        return (Face) face;
    }
    

    // ===================================================================
    // Implementation of the Geometry3D interface

    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#contains(net.sci.geom.geom3d.Point2D, double)
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            if (getFacePolygon(iFace).contains(point, eps)) return true;
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#distance(double, double)
     */
    @Override
    public double distance(double x, double y)
    {
        double distMin = Double.POSITIVE_INFINITY;
        
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            double dist = getFacePolygon(iFace).distance(x, y);
            distMin = Math.min(distMin,  dist);
        }
        
        return distMin;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom2d.Geometry2D#bounds()
     */
    @Override
    public Bounds2D bounds()
    {
        return Bounds2D.of(vertexPositions);
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.Geometry#isBounded()
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }
 
    @Override
    public Mesh2D duplicate()
    {
        // create new empty graph
        SimplePolygonalMesh2D dup = new SimplePolygonalMesh2D(vertexCount(), faceCount());
        
        // copy vertices, keeping mapping between old and new references
        Map<Mesh2D.Vertex, Mesh2D.Vertex> vertexMap = new HashMap<>();
        for (Mesh2D.Vertex v : this.vertices())
        {
            Mesh2D.Vertex v2 = dup.addVertex(v.position());
            vertexMap.put(v, v2);
        }
        
        // copy edges using vertex mapping
        for (int[] face : faces)
        {
            Mesh2D.Vertex[] faceVertices = new Mesh2D.Vertex[face.length];
            for (int fv = 0; fv < face.length; fv++)
            {
                faceVertices[fv] = vertexMap.get(getVertex(face[fv]));
            }
            dup.addFace(faceVertices);
        }
        
        // return graph
        return dup;
    }
    
    public class Vertex implements Mesh2D.Vertex
    {
        // the index of the vertex
        int index;

        public Vertex(int index)
        {
            this.index = index;
        }
        
        @Override
        public Point2D position()
        {
            return vertexPositions.get(index);
        }

       
        // ===================================================================
        // Override equals and hashCode to allow indexing

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
    

    private class VertexIterator implements Iterator<Mesh2D.Vertex>
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

    public class Face implements Mesh2D.Face
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
        public Polygon2D polygon()
        {
            return SimplePolygonalMesh2D.this.getFacePolygon(this.index);
        }
        
        @Override
        public int vertexCount()
        {
            return faces.get(this.index).length;
        }

        public Collection<? extends Mesh2D.Vertex> vertices()
        {
            return IntStream.of(faces.get(this.index))
                    .mapToObj(ind -> getVertex(ind))
                    .toList();
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

    private class FaceIterator implements Iterator<Mesh2D.Face>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < faces.size();
        }
        
        @Override
        public Mesh2D.Face next()
        {
            return new Face(index++);
        }
    }
}
