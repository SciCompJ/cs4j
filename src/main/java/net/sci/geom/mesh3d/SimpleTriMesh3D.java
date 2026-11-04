/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;

/**
 * A simple class for representing triangle meshes in 3D with limited edition possibilities.
 * 
 * Specific features of this implementation:
 * <ul>
 * <li>All faces are triangles.</li>
 * <li>Limited edition possibilities. Vertices and faces can be added, but not removed.</li>
 * <li>No management of edges.</li>
 * <li>Vertices and faces are indexed.</li>
 * </ul>
 * 
 * Vertices are stored in an ArrayList. Faces are stored in an ArrayList of integer triplets.
 * 
 * @author dlegland
 *
 */
public class SimpleTriMesh3D implements TriMesh3D, IndexedMesh3D
{
    // ===================================================================
    // Class variables

    /**
     * The position of the vertices. 
     */
    ArrayList<Point3D> vertices;
    
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
        this.vertices = new ArrayList<Point3D>();
        this.faces = new ArrayList<int[]>();
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
    public SimpleTriMesh3D(int nv, int nf)
    {
        this.vertices = new ArrayList<Point3D>(nv);
        this.faces = new ArrayList<int[]>(nf);
    }

    /**
     * Create a new mesh initialized with the array of vertex positions and face
     * vertex indices.
     * 
     * @param vertices
     *            the array of vertex positions
     * @param faces
     *            the array of face vertex indices
     */
    public SimpleTriMesh3D(Point3D[] vertices, int[][] faces)
    {
        this.vertices = new ArrayList<>(Arrays.asList(vertices));
        this.faces = new ArrayList<>(Arrays.asList(faces));
    }

    /**
     * Create a new mesh by using directly the array of vertex positions and
     * face vertex indices.
     * 
     * @param vertices
     *            the array of vertex positions
     * @param faces
     *            the array of face vertex indices
     */
    public SimpleTriMesh3D(ArrayList<Point3D> vertices, ArrayList<int[]> faces)
    {
        this.vertices = vertices;
        this.faces = faces;
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
        for (int[] inds : faces)
        {
            Point3D v1 = this.vertices.get(inds[0]);
            Point3D v2 = this.vertices.get(inds[1]);
            Point3D v3 = this.vertices.get(inds[2]);
            surf += Vector3D.crossProduct(v1, v2, v3).norm();
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
    

    // ===================================================================
    // Topological queries
    
    @Override
    public Collection<TriMesh3D.Face> vertexFaces(Mesh3D.Vertex vertex)
    {
        // pre-allocate result array
        int index = castVertex(vertex).index;
        ArrayList<TriMesh3D.Face> vertexFaces = new ArrayList<TriMesh3D.Face>(6);
        
        // iterate on face indices to avoid creating face instances
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
        int index = castVertex(vertex).index;
        
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
    public Collection<Mesh3D.Vertex> faceVertices(Mesh3D.Face face)
    {
        int[] inds = faces.get(castFace(face).index);
        ArrayList<Mesh3D.Vertex> verts = new ArrayList<Mesh3D.Vertex>(3);
        verts.add(new Vertex(inds[0]));
        verts.add(new Vertex(inds[1]));
        verts.add(new Vertex(inds[2]));
        return verts;
    }

    
    // ===================================================================
    // Management of vertices

    @Override
    public int vertexCount()
    {
        return vertices.size();
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
        int index = vertices.size();
        vertices.add(position);
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
    
    /**
	 * Returns the index of the specified vertex.
	 * 
	 * @param vertex
	 *            a vertex belonging to this mesh.
	 * @return the index of the vertex in the vertex array.
	 * @throws RuntimeException if the vertex does not belong to the mesh.
	 */
    public int indexOf(Mesh3D.Vertex vertex)
    {
    	if (vertex instanceof Vertex vertex2) return vertex2.index;
    	throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
    }

    public Point3D vertexPosition(int index)
    {
        return vertices.get(index);
    }

    @Override
    public Collection<Point3D> vertexPositions()
    {
        return vertices;
    }

    /**
     * Cast to local Vertex class
     * 
     * @param vertex
     *            the Vertex instance
     * @return the same instance casted to local Vertex implementation
     */
    private Vertex castVertex(Mesh3D.Vertex vertex)
    {
        if (vertex instanceof Vertex vertex2) return vertex2;
        throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
    }


    // ===================================================================
    // Management of faces
    
    @Override
    public Iterable<TriMesh3D.Face> faces()
    {
        return new Iterable<TriMesh3D.Face>() {
            @Override
            public Iterator<TriMesh3D.Face> iterator()
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
     * @param v1
     *            reference to the first face vertex
     * @param v2
     *            reference to the second face vertex
     * @param v3
     *            reference to the third face vertex
     * @return the index of the newly created face
     */
    @Override
    public Mesh3D.Face addFace(Mesh3D.Vertex v1, Mesh3D.Vertex v2, Mesh3D.Vertex v3)
    {
        int iv1 = castVertex(v1).index;
        int iv2 = castVertex(v2).index;
        int iv3 = castVertex(v3).index;
        int index = addFace(iv1, iv2, iv3);
        return new Face(index);
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
        Point3D p1 = this.vertices.get(inds[0]);
        Point3D p2 = this.vertices.get(inds[1]);
        Point3D p3 = this.vertices.get(inds[2]);
        
        return new Triangle3D(p1, p2, p3);
    }

    /**
     * Returns the face corresponding to the given index.
     * 
     * @param faceIndex
     *            the face index
     * @return the face at the specified index
     */
    public Face getFace(int faceIndex)
    {
        return new Face(faceIndex);
    }

    /**
     * Returns the vertex indices for the face corresponding to the given index.
     * 
     * @param faceIndex
     *            the face index
     * @return the array of vertex indices for the face
     */
    @Override
    public int[] faceVertexIndices(int faceIndex)
    {
        return faces.get(faceIndex);
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
        if (face instanceof Face face2) return face2.index;
        throw new IllegalArgumentException("Face should be an instance of inner Face implementation");
    }

    /**
     * Cast to local Face class
     * 
     * @param face
     *            the Face instance
     * @return the same instance casted to local Face implementation
     */
    private Face castFace(Mesh3D.Face face)
    {
        if (face instanceof Face face2) return face2;
        throw new IllegalArgumentException("Face should be an instance of inner Face implementation");
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
            Point3D p1 = this.vertices.get(inds[0]);
            Point3D p2 = this.vertices.get(inds[1]);
            Point3D p3 = this.vertices.get(inds[2]);
            
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
            Point3D p1 = this.vertices.get(inds[0]);
            Point3D p2 = this.vertices.get(inds[1]);
            Point3D p3 = this.vertices.get(inds[2]);
            
            double dist = new Triangle3D(p1, p2, p3).distance(x, y, z);
            distMin = Math.min(distMin,  dist);
        }
        
        return distMin;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.geom3d.Geometry3D#boundingBox()
     */
    @Override
    public Bounds3D bounds()
    {
        return Bounds3D.of(vertices);
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
    public Mesh3D duplicate()
    {
        // create new empty graph
        SimpleTriMesh3D dup = new SimpleTriMesh3D(vertexCount(), faceCount());
        
        // copy vertices, keeping mapping between old and new references
        Map<Mesh3D.Vertex, Mesh3D.Vertex> vertexMap = new HashMap<>();
        for (Mesh3D.Vertex v : this.vertices())
        {
            Mesh3D.Vertex v2 = dup.addVertex(v.position());
            vertexMap.put(v, v2);
        }
        
        // copy edges using vertex mapping
        for (Mesh3D.Face face : faces())
        {
            Iterator<? extends Mesh3D.Vertex> iter = face.vertices().iterator();
            Mesh3D.Vertex v1 = vertexMap.get(iter.next());
            Mesh3D.Vertex v2 = vertexMap.get(iter.next());
            Mesh3D.Vertex v3 = vertexMap.get(iter.next());
            dup.addFace(v1, v2, v3);
        }
        
        // return graph
        return dup;
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
            return vertices.get(index);
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
    

    private class VertexIterator implements Iterator<Mesh3D.Vertex>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < vertices.size();
        }
        
        @Override
        public Vertex next()
        {
            return new Vertex(index++);
        }
    }

    public class Face implements TriMesh3D.Face
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
            Point3D p1 = vertices.get(indices[0]);
            Point3D p2 = vertices.get(indices[1]);
            Point3D p3 = vertices.get(indices[2]);
            
            return new Triangle3D(p1, p2, p3);    
        }
        
        @Override
        public Vector3D normal()
        {
            int[] inds = faces.get(this.index);
            return Vector3D.crossProduct(vertices.get(inds[0]), vertices.get(inds[1]), vertices.get(inds[2]));
        }
        
        public Collection<Mesh3D.Vertex> vertices()
        {
            int[] indices = faces.get(this.index);
            ArrayList<Mesh3D.Vertex> faceVertices = new ArrayList<Mesh3D.Vertex>(3);
            faceVertices.add(new Vertex(indices[0]));
            faceVertices.add(new Vertex(indices[1]));
            faceVertices.add(new Vertex(indices[2]));
            return faceVertices;
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

    private class FaceIterator implements Iterator<TriMesh3D.Face>
    {
        int index = 0;
        @Override
        public boolean hasNext()
        {
            return index < faces.size();
        }
        
        @Override
        public TriMesh3D.Face next()
        {
            return new Face(index++);
        }
    }
}
