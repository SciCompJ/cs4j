/**
 * 
 */
package net.sci.geom.mesh.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.Mesh3D.Face;
import net.sci.geom.mesh.Mesh3D.Vertex;

/**
 * @author dlegland
 *
 */
public class OffMeshWriter implements MeshWriter
{
    File file = null;
    
    /**
     * Public constructor.
     */
    public OffMeshWriter(File file)
    {
        this.file = file;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.mesh.io.MeshWriter#writeMesh(net.sci.geom.mesh.Mesh3D)
     */
    @Override
    public void writeMesh(Mesh3D mesh) throws IOException
    {
        // Open print writer
        PrintWriter writer;
        try 
        {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Could not open file: " + file, ex);
        }
        
        // number of element within the mesh
        int nv = mesh.vertexCount();
        int nf = mesh.faceCount();
        
        // write header
        writer.println("OFF");
        writer.printf("%d %d\n", nv, nf);

        // iterate over vertices to print their coordinates and to create map to indices
        Map<Vertex, Integer> vertexIndices = new HashMap<>();
        int index = 0;
        for (Vertex vertex : mesh.vertices())
        {
            Point3D pos = vertex.position();
            writer.printf(Locale.ENGLISH, "%f %f %f\n", pos.x(), pos.y(), pos.z());
            
            vertexIndices.put(vertex, index++);
        }

        // print vertex indices of each face
        for (Face face : mesh.faces())
        {
            Collection<? extends Vertex> vertices = mesh.faceVertices(face);
            writer.printf("%d", vertices.size());
            
            for (Vertex v : vertices)
            {
                writer.printf(" %d", vertexIndices.get(v));
            }
            writer.println();
        }
        
        // close writer
        writer.close();
    }
    
}
