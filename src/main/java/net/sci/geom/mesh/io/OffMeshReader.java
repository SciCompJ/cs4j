/**
 * 
 */
package net.sci.geom.mesh.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Locale;
import java.util.Scanner;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.SimpleTriMesh3D;

/**
 * @author dlegland
 *
 */
public class OffMeshReader implements MeshReader
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
        String line = readNextNonCommentLine(reader);
        if ("OFF".compareToIgnoreCase(line) != 0)
        {
            reader.close();
            throw new RuntimeException("Not a valid OFF file");
        }
        
        // Parses number of vertices and of faces
        String dimString = readNextNonCommentLine(reader);
        Scanner scanner = new Scanner(dimString);
        int nVertices = scanner.nextInt();
        int nFaces = scanner.nextInt();
        scanner.close();
        
        SimpleTriMesh3D mesh = new SimpleTriMesh3D(nVertices, nFaces);
        
        for (int iVertex = 0; iVertex < nVertices; iVertex++)
        {
            Scanner s = new Scanner(readNextNonCommentLine(reader));
            s.useLocale(Locale.ENGLISH);
            double vx = s.nextDouble();
            double vy = s.nextDouble();
            double vz = s.nextDouble();
            mesh.addVertex(new Point3D(vx, vy, vz));
            s.close();
        }
        
        for (int iFace = 0; iFace < nFaces; iFace++)
        {
            Scanner s = new Scanner(readNextNonCommentLine(reader));
            if (s.nextInt() != 3)
            {
                s.close();
                throw new RuntimeException("Can only process triangular meshes");
            }
            int iv1 = s.nextInt();
            int iv2 = s.nextInt();
            int iv3 = s.nextInt();
            mesh.addFace(iv1, iv2, iv3);
            s.close();
        }
        
        reader.close();

        return mesh;
    }
    
    private String readNextNonCommentLine(LineNumberReader reader) throws IOException
    {
        String line;
        while((line = reader.readLine()) != null)
        {
            line = line.trim();
            if (line.startsWith("#"))
            {
                continue;
            }
            return line;
        }
        return null;
    }
}
