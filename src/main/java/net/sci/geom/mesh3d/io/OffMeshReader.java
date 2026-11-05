/**
 * 
 */
package net.sci.geom.mesh3d.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimplePolygonalMesh3D;
import net.sci.geom.mesh3d.SimpleTriMesh3D;

/**
 * Reads a 3D mesh from a text file using the OFF format.
 * 
 * Can read meshes with arbitrary number of vertices per face. However, reading
 * additional data from mesh is not supported.
 * 
 * 
 * @see OffMeshWriter
 */
public class OffMeshReader extends AlgoStub implements MeshReader
{
    File file;
    
    public OffMeshReader(File file) throws IOException 
    {
        this.file = file;
    }
    
    /* (non-Javadoc)
     * @see net.sci.geom.mesh.io.MeshReader#readMesh()
     */
    @Override
    public Mesh3D readMesh() throws IOException
    {
        // create reader
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        
        // First line should contain "OFF" string
        String headerLine = readNextNonCommentLine(reader);
        if ("OFF".compareToIgnoreCase(headerLine) != 0)
        {
            reader.close();
            throw new RuntimeException("Not a valid OFF file");
        }
        
        // Parses number of vertices and of faces
        String dimString = readNextNonCommentLine(reader);
        String[] tokens = dimString.split(" ");
        int nVertices = Integer.parseInt(tokens[0]);
        int nFaces = Integer.parseInt(tokens[1]);
        
        // read vertices
        this.fireStatusChanged(this, "read vertices");
        ArrayList<Point3D> vertices = new ArrayList<Point3D>(nVertices); 
        for (int iVertex = 0; iVertex < nVertices; iVertex++)
        {
            // monitor progress only for a fraction of the lines
            if (iVertex % 200 == 0) this.fireProgressChanged(this, iVertex, nVertices);
            tokens = readNextNonCommentLine(reader).split(" ");
            
            // parse vertex coordinates
            double vx = Double.parseDouble(tokens[0]);
            double vy = Double.parseDouble(tokens[1]);
            double vz = Double.parseDouble(tokens[2]);
            vertices.add(new Point3D(vx, vy, vz));
            // (drop remaining data)
        }
        
        // read faces
        this.fireStatusChanged(this, "read faces");
        boolean triMesh = true;
        ArrayList<int[]> faces = new ArrayList<int[]>(nFaces); 
        for (int iFace = 0; iFace < nFaces; iFace++)
        {
            // monitor progress only for a fraction of the lines
            if (iFace % 200 == 0) this.fireProgressChanged(this, iFace, nFaces);
            tokens = readNextNonCommentLine(reader).split(" ");
            
            // parse index of face vertices
            int nvf = Integer.parseInt(tokens[0]);
            if (nvf != 3) triMesh = false;
            int[] inds = new int[nvf];
            for (int iv = 0; iv < nvf; iv++)
            {
                inds[iv] = Integer.parseInt(tokens[iv + 1]);
            }            
            faces.add(inds);
            // (drop remaining data)
        }
        
        reader.close();

        // create resulting mesh, choosing the most appropriate class depending
        // on the number of vertices per face
        this.fireStatusChanged(this, "create mesh");
        return triMesh ? new SimpleTriMesh3D(vertices, faces)
                : new SimplePolygonalMesh3D(vertices, faces);
    }
    
    private String readNextNonCommentLine(LineNumberReader reader) throws IOException
    {
        while(true)
        {
            String line = reader.readLine();
            if (line == null) return null;
            
            line = line.trim();
            if (line.startsWith("#"))
            {
                continue;
            }
            return line;
        }
    }
}
