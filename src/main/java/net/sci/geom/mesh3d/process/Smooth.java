/**
 * 
 */
package net.sci.geom.mesh3d.process;

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
     * Empty contructor.
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
        
        // number of elements in the input mesh
        int nv = mesh.vertexCount();
        int nf = mesh.faceCount();

        // keep correspondence between vertices of the original mesh and position within new mesh
        this.fireStatusChanged(this, "initalize positions");
        Map<Mesh3D.Vertex, Point3D> refPositions = new HashMap<>();
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            refPositions.put(vertex, vertex.position());   
        }
        
        // Iterate over vertices
        this.fireStatusChanged(this, "Smooth positions");
        Map<Mesh3D.Vertex, Point3D> newPositions = new HashMap<>(nv);
        int i = 0;
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            this.fireProgressChanged(this, i++, nv);
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
        i = 0;
        for (Mesh3D.Vertex vertex : mesh.vertices())
        {
            this.fireProgressChanged(this, i++, nv);
            Vertex newVertex = result.addVertex(newPositions.get(vertex));
            vertexMap.put(vertex, newVertex);
        }
        
        // also add faces, using vertex correspondence
        this.fireStatusChanged(this, "Create result faces");
        int fIdx = 0;
        for (Face face : mesh.faces())
        {
            this.fireProgressChanged(this, fIdx++, nf);
            Iterator<? extends Vertex> iter = mesh.faceVertices(face).iterator();
            Vertex v1 = vertexMap.get(iter.next());
            Vertex v2 = vertexMap.get(iter.next());
            Vertex v3 = vertexMap.get(iter.next());
            result.addFace(v1, v2, v3);
        }
        this.fireProgressChanged(this, nf, nf);
        
        return result;
    }
}
