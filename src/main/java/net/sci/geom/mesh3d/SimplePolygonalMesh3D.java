/**
 * 
 */
package net.sci.geom.mesh3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Polygon3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.impl.DefaultPolygon3D;

/**
 * Implementation of a polygonal 3D mesh where faces may have an arbitrary number of vertices.
 * 
 * Specificities of this implementation:
 * <ul>
 * <li>Faces are polygons.</li>
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
public class SimplePolygonalMesh3D implements Mesh3D
{
    // ===================================================================
    // Class variables

    /**
     * The position of the vertices. 
     */
    ArrayList<Point3D> vertexPositions;
    
    /**
     * For each face, the list of vertex indices.
     */
    ArrayList<int[]> faces;
    
    
    // ===================================================================
    // Constructors

    /**
     * Create a new empty mesh (no vertex, no face).
     */
    public SimplePolygonalMesh3D()
    {
        this.vertexPositions = new ArrayList<Point3D>();
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
    public SimplePolygonalMesh3D(Collection<Point3D> vertices, Collection<int[]> faces)
    {
        this.vertexPositions = new ArrayList<Point3D>(vertices.size());
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
    public SimplePolygonalMesh3D(int nv, int nf)
    {
        this.vertexPositions = new ArrayList<Point3D>(nv);
        this.faces = new ArrayList<int[]>(nf);
    }

    
    // ===================================================================
    // Methods specific to Mesh3D
    
//    /**
//     * Computes the surface area of the mesh.
//     * 
//     * @return the surface area of the mesh
//     */
//    public double surfaceArea()
//    {
//        double surf = 0;
//        
//        // Computes the sum of the norm of the cross products.
//        for (int[] faceIndices : faces)
//        {
//            Point3D v1 = this.vertexPositions.get(faceIndices[0]);
//            Point3D v2 = this.vertexPositions.get(faceIndices[1]);
//            Point3D v3 = this.vertexPositions.get(faceIndices[2]);
//            
//            Vector3D v12 = new Vector3D(v1, v2);
//            Vector3D v13 = new Vector3D(v1, v3);
//            surf += Vector3D.crossProduct(v12,  v13).norm();
//        }
//
//        return surf / 2;
//    }
 
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
    public Collection<Mesh3D.Face> vertexFaces(Mesh3D.Vertex vertex)
    {
        int index = getVertex(vertex).index;
        ArrayList<Mesh3D.Face> vertexFaces = new ArrayList<Mesh3D.Face>(6);
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
    public Collection<? extends Mesh3D.Vertex> vertexNeighbors(Mesh3D.Vertex vertex)
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
        ArrayList<Mesh3D.Vertex> vertices = new ArrayList<Mesh3D.Vertex>(neighInds.size());
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
    public Collection<Mesh3D.Vertex> faceVertices(Mesh3D.Face face)
    {
        int[] inds = faces.get(getFace(face).index);
        ArrayList<Mesh3D.Vertex> verts = new ArrayList<Mesh3D.Vertex>(inds.length);
        for (int index : inds) 
        {
            verts.add(new Vertex(index));
        }
        return verts;
    }


    // ===================================================================
    // Management of vertices

    @Override
    public int vertexCount()
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
    
    /**
     * Returns the index of the specified vertex.
     * 
     * @param vertex
     *            a vertex belonging to this mesh.
     * @return the index of the vertex in the vertex array.
     * @throws RuntimeException
     *             if the vertex does not belong to the mesh.
     */
    public int indexOf(Mesh3D.Vertex vertex)
    {
        if (vertex instanceof Vertex)
        {
            return ((Vertex) vertex).index;
        }
        
        throw new IllegalArgumentException("Vertex should be an instance of inner Vertex implementation");
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
    public Mesh3D.Face addFace(Mesh3D.Vertex[] vertices)
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
     * Adds a triangular face defined by the indices of its vertices.
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
    public void removeFace(Mesh3D.Face face)
    {
        throw new UnsupportedOperationException("This implementation does not support face removal");
    }

    public Polygon3D getFacePolygon(int faceIndex)
    {
        int[] inds = faces.get(faceIndex);
        ArrayList<Point3D> faceVertices = new ArrayList<>(inds.length);
        for (int index : inds)
        {
            faceVertices.add(this.vertexPositions.get(index));
        }
        return new DefaultPolygon3D(faceVertices);
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
    public int indexOf(Mesh3D.Face face)
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
    private Face getFace(Mesh3D.Face face)
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
     * @see net.sci.geom.geom3d.Geometry3D#contains(net.sci.geom.geom3d.Point3D, double)
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            if (getFacePolygon(iFace).contains(point, eps)) return true;
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
        
        for (int iFace = 0; iFace < faces.size(); iFace++)
        {
            double dist = getFacePolygon(iFace).distance(x, y, z);
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
        return Bounds3D.of(vertexPositions);
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
        SimplePolygonalMesh3D dup = new SimplePolygonalMesh3D(vertexCount(), faceCount());
        
        // copy vertices, keeping mapping between old and new references
        Map<Mesh3D.Vertex, Mesh3D.Vertex> vertexMap = new HashMap<>();
        for (Mesh3D.Vertex v : this.vertices())
        {
            Mesh3D.Vertex v2 = dup.addVertex(v.position());
            vertexMap.put(v, v2);
        }
        
        // copy edges using vertex mapping
        for (int[] face : faces)
        {
            Mesh3D.Vertex[] faceVertices = new Mesh3D.Vertex[face.length];
            for (int fv = 0; fv < face.length; fv++)
            {
                faceVertices[fv] = vertexMap.get(getVertex(face[fv]));
            }
            dup.addFace(faceVertices);
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
        public Polygon3D polygon()
        {
            return SimplePolygonalMesh3D.this.getFacePolygon(this.index);
        }
        
        @Override
        public Vector3D normal()
        {
            int[] inds = faces.get(this.index);
            return Vector3D.crossProduct(
                    vertexPositions.get(inds[0]), 
                    vertexPositions.get(inds[1]),
                    vertexPositions.get(inds[2]));
        }
        
        @Override
        public int vertexCount()
        {
            return faces.get(this.index).length;
        }

        public Collection<Mesh3D.Vertex> vertices()
        {
            int[] indices = faces.get(this.index);
            ArrayList<Mesh3D.Vertex> faceVertices = new ArrayList<>(indices.length);
            for (int ind : indices)
            {
                faceVertices.add(getVertex(ind));
            }
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
}
