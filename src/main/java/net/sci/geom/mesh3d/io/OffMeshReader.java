/**
 * 
 */
package net.sci.geom.mesh3d.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimplePolygonalMesh3D;
import net.sci.geom.mesh3d.SimpleTriMesh3D;

/**
 * Read a 3D mesh from a text file using the OFF format.
 * 
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
        
        // read vertices
        ArrayList<Point3D> vertices = new ArrayList<Point3D>(nVertices); 
        for (int iVertex = 0; iVertex < nVertices; iVertex++)
        {
            Scanner s = new Scanner(readNextNonCommentLine(reader));
            s.useLocale(Locale.ENGLISH);
            double vx = s.nextDouble();
            double vy = s.nextDouble();
            double vz = s.nextDouble();
            vertices.add(new Point3D(vx, vy, vz));
            s.close();
        }
        
        // read faces
        boolean triMesh = true;
        ArrayList<int[]> faces = new ArrayList<int[]>(nFaces); 
        for (int iFace = 0; iFace < nFaces; iFace++)
        {
            Scanner s = new Scanner(readNextNonCommentLine(reader));
            int nvf = s.nextInt();
            if (nvf != 3) triMesh = false;
            int[] inds = new int[nvf];
            for (int iv = 0; iv < nvf; iv++)
            {
                inds[iv] = s.nextInt();
            }
            faces.add(inds);
            s.close();
        }
        
        reader.close();

        // create mesh, choosing best type depending on number of vertices per face
        if (triMesh)
        {
            SimpleTriMesh3D mesh = new SimpleTriMesh3D(nVertices, nFaces);
            for (Point3D v : vertices) mesh.addVertex(v);
            for (int[] face : faces) mesh.addFace(face[0], face[1], face[2]);
            return mesh;
        }
        else
        {
            return new SimplePolygonalMesh3D(vertices, faces);
        }
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
