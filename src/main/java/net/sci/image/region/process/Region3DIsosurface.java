/**
 * 
 */
package net.sci.image.region.process;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimpleTriMesh3D;
import net.sci.geom.mesh3d.TriMesh3D;
import net.sci.image.region.BinaryRegion3D;
import net.sci.image.vectorize.MorphologicalMarchingCubes;
import net.sci.image.vectorize.MorphologicalMarchingCubes.IntPoint3D;

import static net.sci.image.vectorize.MorphologicalMarchingCubes.vertexPositions;
import static net.sci.image.vectorize.MorphologicalMarchingCubes.edgeVertexIndices;
import static net.sci.image.vectorize.MorphologicalMarchingCubes.faceEdgeIndices;

/**
 * Computes the Mesh3D representing the isosurface of a binary region.
 * 
 * @see net.sci.image.vectorize.MorphologicalMarchingCubes
 */
public class Region3DIsosurface extends AlgoStub
{
    /**
     * Default empty constructor.
     */
    public Region3DIsosurface()
    {
    }
    
    public TriMesh3D process(BinaryRegion3D region)
    {
        // retrieve region bounds
        int[][] bounds = region.intBounds();
        
        // add one extra voxel in each direction to prevent open meshes
        int xmin = bounds[0][0] - 1;
        int xmax = bounds[0][1] + 1;
        int ymin = bounds[1][0] - 1;
        int ymax = bounds[1][1] + 1;
        int zmin = bounds[2][0] - 1;
        int zmax = bounds[2][1] + 1;
        
        // Create the instance of result mesh
        SimpleTriMesh3D mesh = new SimpleTriMesh3D();
        
        // array of values for vertices of current configuration
        boolean[] values = new boolean[8];

        // the lists of intersections already computed for each plane
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesX0 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesX1 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesY0 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesY1 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesZ = new HashMap<>();
        
        // iterate over z-slices
        for (int z = zmin; z <= zmax; z++)
        {
            this.fireProgressChanged(this, z - zmin, zmax - zmin + 1);
            
            // update the lists of edges
            edgesX0 = edgesX1;
            edgesY0 = edgesY1;
            edgesX1 = new HashMap<>();
            edgesY1 = new HashMap<>();
            edgesZ.clear();
            
            // iterate over slices
            for (int y = ymin; y <= ymax; y++)
            {
                // extract first values of the current configuration row
                values[1] = region.get(0, y, z);
                values[3] = region.get(0, y + 1, z    );
                values[5] = region.get(0, y    , z + 1);
                values[7] = region.get(0, y + 1, z + 1);
                
                for (int x = xmin; x <= xmax; x++)
                {
                    // get voxel values within configuration, using values from previous config
                    values[0] = values[1];
                    values[2] = values[3];
                    values[4] = values[5];
                    values[6] = values[7];
                    values[1] = region.get(x + 1, y,     z    );
                    values[3] = region.get(x + 1, y + 1, z    );
                    values[5] = region.get(x + 1, y,     z + 1);
                    values[7] = region.get(x + 1, y + 1, z + 1);
                    
                    // compute configuration index
                    int index = configurationIndex(values);
                    
                    // depending on configuration, get edge indices for each face
                    int[] edgeIndices = faceEdgeIndices[index];
                    if (edgeIndices.length < 3)
                    {
                        if (index != 0 && index != 255)
                        {
                            System.out.println("Configuration without face: " + index);
                        }
                        continue;
                    }
                    
                    // number of faces within current configuration
                    int nFaces = edgeIndices.length / 3;
                    
                    for (int iFace = 0; iFace < nFaces; iFace++)
                    {
                        // create new edges within configuration
                        Mesh3D.Vertex[] faceVertices = new Mesh3D.Vertex[3];
                        for (int iEdge = 0; iEdge < 3; iEdge++)
                        {
                            int ie = edgeIndices[iFace * 3 + iEdge];
                            int iv1 = MorphologicalMarchingCubes.edgeVertexIndices[ie][0];
                            int iv2 = edgeVertexIndices[ie][1];
                            
                            // shift between the two vertices
                            int dx1 = vertexPositions[iv1][0];
                            int dy1 = vertexPositions[iv1][1];
                            int dz1 = vertexPositions[iv1][2];
                            int dx = vertexPositions[iv2][0] - dx1;
                            int dy = vertexPositions[iv2][1] - dy1;
                            int dz = vertexPositions[iv2][2] - dz1;
                            
                            // identify the map of edges that should contain the intersection 
                            HashMap<IntPoint3D, Mesh3D.Vertex> edges = null;
                            if (dx > 0)
                            {
                                // edge in X direction
                                edges = dz1 == 0 ? edgesX0 : edgesX1;
                            }
                            else if (dy > 0)
                            {
                                // edge in Y direction
                                edges = dz1 == 0 ? edgesY0 : edgesY1;
                            }
                            else if (dz > 0)
                            {
                                // edge in Z direction
                                edges = edgesZ;
                            }
                            else
                            {
                                System.err.println(String.format("Edge with wrong direction: iv1=%d, iv2=%d, dv=(%d,%d,%d)", iv1, iv2, dx, dy, dz));
                            }
                            
                            // position of first edge vertex
                            IntPoint3D intPos = new MorphologicalMarchingCubes.IntPoint3D(x + dx1, y + dy1, z + dz1);
                            
                            // try to retrieve existing vertex, or create a new one
                            Mesh3D.Vertex vertex = null;
                            if (edges.containsKey(intPos))
                            {
                                // retrieve existing vertex
                                vertex = edges.get(intPos);
                            }
                            else
                            {
                                // create a new vertex
                                Point3D p1 = MorphologicalMarchingCubes.createVertex(x, y, z, iv1);
                                Point3D p2 = MorphologicalMarchingCubes.createVertex(x, y, z, iv2);
                                Point3D pos = Point3D.interpolate(p1, p2, 0.5);
                                vertex = mesh.addVertex(pos);
                                edges.put(intPos, vertex);
                            }
                            
                            faceVertices[iEdge] = vertex;
                            
                        }
                        
                        // create face
                        mesh.addFace(faceVertices[0], faceVertices[1], faceVertices[2]);                
                    }
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);
        return mesh;
    }

    /**
     * Computes configuration index from the values associated to each of the
     * eight vertices.
     * 
     * @param values
     *            the values associated to each of the eight vertices
     * @param value
     *            the threshold value
     * @return the configuration index deduced from the values
     */
    private int configurationIndex(boolean[] values)
    {
        int index = 0;
        if (values[0]) index +=   1;
        if (values[1]) index +=   2;
        if (values[2]) index +=   4;
        if (values[3]) index +=   8;
        if (values[4]) index +=  16;
        if (values[5]) index +=  32;
        if (values[6]) index +=  64;
        if (values[7]) index += 128;
        return index;
    }
    

}
