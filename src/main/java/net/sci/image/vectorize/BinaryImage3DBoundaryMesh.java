/**
 * 
 */
package net.sci.image.vectorize;

import java.util.TreeMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimplePolygonalMesh3D;

/**
 * Computes the 3D mesh with square faces that corresponds to the boundary
 * between foreground voxels and background voxels.
 * 
 * @author dlegland
 * 
 * @see BinaryImageBoundaryGraph
 * @see Image3DIsosurface
 */
public class BinaryImage3DBoundaryMesh extends AlgoStub
{
    /**
     * Default empty constructor.
     */
    public BinaryImage3DBoundaryMesh()
    {
    }

    /**
     * Computes the boundary mesh with square faces from the input array. If
     * input array is not 3D or does not contain {@code Binary} elements, an
     * exception is thrown.
     * 
     * @param array
     *            the array to process
     * @return the boundary mesh with square faces computed from the input array
     * @throws RuntimeException
     *             If input array is not 3D or does not contain {@code Binary}
     *             elements
     */
    public Mesh3D process(Array<?> array)
    {
        if (array.dimensionality() != 3)
        {
            throw new RuntimeException("Requires a 3D array as input");
        }
        if (!array.elementInstanceOf(Binary.class))
        {
            throw new RuntimeException("Requires an array containing Binary eements, not " + array.elementClass().getName());
        }
        
        return processBinary3d(BinaryArray3D.wrap(BinaryArray.wrap(array)));
    }

    /**
     * Computes the boundary mesh with square faces from the input 3D array of
     * binary elements.
     * 
     * @param array
     *            the array to process
     * @return the boundary mesh with square faces computed from the input array
     */
    public Mesh3D processBinary3d(BinaryArray3D array)
    {
        // retrieve array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // organize the mesh vertices within a nested map structure, 
        // using the z, y and x coordinates as keys, and using vertex references as final entries
        TreeMap<Double, TreeMap<Double, TreeMap<Double, Mesh3D.Vertex>>> vertexMap = new TreeMap<Double, TreeMap<Double, TreeMap<Double, Mesh3D.Vertex>>>();

        // Create the instance of result mesh
        SimplePolygonalMesh3D mesh = new SimplePolygonalMesh3D();
        
        // value of current, up, and left pixels.
        boolean currentVoxel, topVoxel, upVoxel, leftVoxel;
        

        // iterate over image voxels
        for (int z = 0; z < sizeZ + 1; z++)
        {
            this.fireProgressChanged(this, z, sizeZ + 1);
            
            for (int y = 0; y < sizeY + 1; y++)
            {
                leftVoxel = false;
                for (int x = 0; x < sizeX + 1; x++)
                {
                    // update pixel values of configuration
                    currentVoxel = x < sizeX && y < sizeY && z < sizeZ ? array.getBoolean(x, y, z) : false;
                    topVoxel = x < sizeX && y < sizeY  && z > 0 ? array.getBoolean(x, y, z - 1) : false;
                    upVoxel = x < sizeX && y > 0  && z < sizeZ ? array.getBoolean(x, y - 1, z) : false;

                    // check face in the above XY plane
                    if (topVoxel != currentVoxel)
                    {
                        // retrieve vertices
                        Mesh3D.Vertex v00 = retrieveVertex(mesh, x - 0.5, y - 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v01 = retrieveVertex(mesh, x + 0.5, y - 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v10 = retrieveVertex(mesh, x - 0.5, y + 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v11 = retrieveVertex(mesh, x + 0.5, y + 0.5, z - 0.5, vertexMap);
                        // add a square XY face, with adequate orientation
                        if (currentVoxel) mesh.addFace(v00, v10, v11, v01);
                        else mesh.addFace(v00, v01, v11, v10);
                    }

                    // check face in the upper XZ plane
                    if (upVoxel != currentVoxel)
                    {
                        // retrieve vertices
                        Mesh3D.Vertex v00 = retrieveVertex(mesh, x - 0.5, y - 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v01 = retrieveVertex(mesh, x + 0.5, y - 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v10 = retrieveVertex(mesh, x - 0.5, y - 0.5, z + 0.5, vertexMap);
                        Mesh3D.Vertex v11 = retrieveVertex(mesh, x + 0.5, y - 0.5, z + 0.5, vertexMap);
                        // add a square XZ face, with adequate orientation
                        if (currentVoxel) mesh.addFace(v00, v10, v11, v01);
                        else mesh.addFace(v00, v01, v11, v10);
                    }

                    // check face in the left YZ plane
                    if (leftVoxel != currentVoxel)
                    {
                        // retrieve vertices
                        Mesh3D.Vertex v00 = retrieveVertex(mesh, x - 0.5, y - 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v01 = retrieveVertex(mesh, x - 0.5, y + 0.5, z - 0.5, vertexMap);
                        Mesh3D.Vertex v10 = retrieveVertex(mesh, x - 0.5, y - 0.5, z + 0.5, vertexMap);
                        Mesh3D.Vertex v11 = retrieveVertex(mesh, x - 0.5, y + 0.5, z + 0.5, vertexMap);
                        // add a square YZ face, with adequate orientation
                        if (currentVoxel) mesh.addFace(v00, v10, v11, v01);
                        else mesh.addFace(v00, v01, v11, v10);
                    }
                    
                    // prepare for next voxel
                    leftVoxel = currentVoxel;
                }
            }
        }
        
        return mesh;
    }
    
    private static final Mesh3D.Vertex retrieveVertex(Mesh3D mesh, double x, double y, double z, TreeMap<Double, TreeMap<Double, TreeMap<Double, Mesh3D.Vertex>>> map)
    {
        // retrieve map for z-coordinate
        TreeMap<Double, TreeMap<Double, Mesh3D.Vertex>> mapZ = map.get(z);
        if (mapZ == null)
        {
            mapZ = new TreeMap<Double, TreeMap<Double, Mesh3D.Vertex>>();
        }
        
        // retrieve map for z-coordinate
        TreeMap<Double, Mesh3D.Vertex> mapY = mapZ.get(y);
        if (mapY == null)
        {
            mapY = new TreeMap<Double, Mesh3D.Vertex>();
        }
        
        // check if there is a vertex for the given point
        Mesh3D.Vertex vertex = mapY.get(x);
        if (vertex != null)
        {
            return mapY.get(x);
        }
        
        // create a new vertex, and update nested data structure
        vertex = mesh.addVertex(new Point3D(x, y, z));
        mapY.put(x, vertex);
        mapZ.put(y, mapY);
        map.put(z, mapZ);
        return vertex;
    }

}
