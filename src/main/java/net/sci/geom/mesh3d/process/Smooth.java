/**
 * 
 */
package net.sci.geom.mesh3d.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimpleTriMesh3D;
import net.sci.geom.mesh3d.TriMesh3D;
import net.sci.geom.mesh3d.Mesh3D.Face;
import net.sci.geom.mesh3d.Mesh3D.Vertex;

/**
 * Applies smoothing to a 3D mesh, by averaging vertex coordinates with that of neighbors.
 * 
 * @author dlegland
 *
 */
public class Smooth extends AlgoStub
{
    /**
     * Default empty constructor.
     */
    public Smooth()
    {
    }
    
    public Mesh3D process(Mesh3D mesh)
    {
        if (!(mesh instanceof TriMesh3D))
        {
            throw new RuntimeException("Requires a triangular mesh as input");
        }
        
        // check for cases that can be optimized
        if (mesh instanceof SimpleTriMesh3D trimesh)
        {
            return processSimpleTriMesh(trimesh);
        }
        
        // number of elements in the input mesh
        int nv = mesh.vertexCount();
        int nf = mesh.faceCount();

        // keep correspondence between vertices of the original mesh and position within new mesh
        this.fireStatusChanged(this, "Initialize positions");
        Map<Mesh3D.Vertex, Point3D> refPositions = new HashMap<>();
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            refPositions.put(vertex, vertex.position());   
        }
        
        // Iterate over vertices
        this.fireStatusChanged(this, "Smooth positions");
        Map<Mesh3D.Vertex, Point3D> newPositions = new HashMap<>(nv);
        int iVertex = 0;
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            if (iVertex++ % 500 == 0) this.fireProgressChanged(this, iVertex, nv);
            // initialize average with position of current vertex
            Point3D pos = refPositions.get(vertex);
            double xm = pos.x(), ym = pos.y(), zm = pos.z();
            int nn = 1;
            
            // iterate over neighbors
            for (Vertex neigh : mesh.vertexNeighbors(vertex))
            {
                pos = refPositions.get(neigh);
                xm += pos.x(); 
                ym += pos.y(); 
                zm += pos.z();
                nn++;
            }
            
            // divide by number of neighbors
            xm /= nn;
            ym /= nn;
            zm /= nn;
            
            newPositions.put(vertex, new Point3D(xm, ym, zm));
        }
        this.fireProgressChanged(this, nv, nv);
        
        
        // create result mesh (assuming triangular mesh)
        SimpleTriMesh3D result = new SimpleTriMesh3D(nv, nf);

        // create the new vertices, and keep correspondence with initial vertex
        this.fireStatusChanged(this, "Create result vertices");
        Map<Mesh3D.Vertex, Mesh3D.Vertex> vertexMap = new HashMap<>(nv);
        iVertex = 0;
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            if (iVertex++ % 500 == 0) this.fireProgressChanged(this, iVertex, nv);
            Vertex newVertex = result.addVertex(newPositions.get(vertex));
            vertexMap.put(vertex, newVertex);
        }
        
        // also add faces, using vertex correspondence
        this.fireStatusChanged(this, "Create result faces");
        int iFace = 0;
        for (Face face : mesh.faces())
        {
            if (iFace++ % 500 == 0) this.fireProgressChanged(this, iFace, nf);
            Iterator<? extends Vertex> iter = mesh.faceVertices(face).iterator();
            Vertex v1 = vertexMap.get(iter.next());
            Vertex v2 = vertexMap.get(iter.next());
            Vertex v3 = vertexMap.get(iter.next());
            result.addFace(v1, v2, v3);
        }
        this.fireProgressChanged(this, nf, nf);
        
        return result;
    }
    
    private SimpleTriMesh3D processSimpleTriMesh(SimpleTriMesh3D mesh)
    {
        // number of elements in the input mesh
        int nv = mesh.vertexCount();
        int nf = mesh.faceCount();
        
        // initialize average positions to initial position of mesh
        // (use a factor 2 as each neighbor will be considered twice)
        this.fireStatusChanged(this, "Initialize positions");
        Point3D[] positions = new Point3D[nv];
        for (int i = 0; i < nv; i++)
        {
            positions[i] = mesh.vertexPosition(i).times(2.0);   
        }
        int[] counts = new int[nv];
        
        // Iterate over faces
        int[][] faces = new int[nf][];
        this.fireStatusChanged(this, "Smooth positions");
        for (int iFace = 0; iFace < nf; iFace++)
        {
            if (iFace % 500 == 0) this.fireProgressChanged(this, iFace, nf);
            
            // retrieve index of neighbor vertices
            int[] inds = mesh.faceVertexIndices(iFace);
            int iv1 = inds[0];
            int iv2 = inds[1];
            int iv3 = inds[2];
            
            // position of neighbor vertices
            Point3D v1 = mesh.vertexPosition(iv1);
            Point3D v2 = mesh.vertexPosition(iv2);
            Point3D v3 = mesh.vertexPosition(iv3);
            
            // add position of neighbors to current position
            positions[iv1] = positions[iv1].plus(v2.plus(v3));
            positions[iv2] = positions[iv2].plus(v1.plus(v3));
            positions[iv3] = positions[iv3].plus(v1.plus(v2));
            counts[iv1] += 2;
            counts[iv2] += 2;
            counts[iv3] += 2;
            
            // also duplicate face info
            faces[iFace] = Arrays.copyOf(inds, 3);
        }
        this.fireProgressChanged(this, nv, nv);
        
        // divide by number of neighbors
        for (int i = 0; i < nv; i++)
        {
            positions[i] = positions[i].dividedBy((counts[i] + 2));
        }
        this.fireProgressChanged(this, 1, 1);
        
        return new SimpleTriMesh3D(positions, faces);
    }
}
