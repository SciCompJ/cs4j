/**
 * 
 */
package net.sci.image.vectorize;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.SimpleTriMesh3D;
import net.sci.geom.mesh.io.OffMeshWriter;

/**
 * Marching cubes algorithm based on a connectivity option for foreground /
 * background voxels.</p>
 * 
 * Connectivity 6 is used for foreground voxels.
 * 
 * @author dlegland
 *
 */
public class MarchingCubes extends AlgoStub
{
    /**
     * For each of the eight vertices within the current tile, the shift along
     * each coordinate from the reference vertex.
     */
    static final int[][] vertexPositions = {
            {0, 0, 0},
            {1, 0, 0},
            {0, 1, 0},
            {1, 1, 0},
            {0, 0, 1},
            {1, 0, 1},
            {0, 1, 1},
            {1, 1, 1},
    };
    
    /**
     * For each of the twelve vertices, the indices of the first and second
     * vertices.
     */
    static final int[][] edgeVertices = {
            // current plane
            {0, 1}, {0, 2}, {1, 3}, {2, 3},
            // vertical edges
            {0, 4}, {1, 5}, {2, 6}, {3, 7},
            // next plane
            {4, 5}, {4, 6}, {5, 7}, {6, 7},
    };
    
    /**
     * For each 2-by-2-by-2 configuration, the indices of the edges that form
     * the faces. Each sub array is either empty or has a length which is a
     * multiple of 3.
     */
    static final int[][] confiFaces = {
            {}, // 0
            {0, 1, 4}, // 1
            {0, 5, 2}, // 2
            {1, 4, 2, 2, 4, 5}, // 3 -> 4 edges, 2 faces
            {1, 3, 6}, // 4
            {0, 3, 4, 3, 6, 4}, // 5
            {}, // 6
            {}, // 7
            {3, 2, 7}, // 8
            {}, // 9
            {0, 5, 3, 3, 5, 7}, // 10
            {}, // 11
            {1, 2, 6, 2, 7, 6}, // 12
            {}, // 13
            {}, // 14
            {4, 5, 6, 5, 7, 6}, // 15
            
            {4, 9, 8}, // 16
            {0, 1, 8, 1, 9, 8}, // 17
            {}, // 18
            {}, // 19
            {}, // 20
            {}, // 21
            {}, // 22
            {2, 3, 5, 3, 6, 5, 5, 6, 8, 6, 9, 8}, // 23
            {}, // 24
            {}, // 25
            {}, // 26
            {}, // 27
            {}, // 28
            {}, // 29
            {}, // 30
            {}, // 31
            
            {5, 8, 10}, // 32
            {}, // 33
            {0, 8, 2, 2, 8, 10}, // 34
            {}, // 35
            {}, // 36
            {}, // 37
            {}, // 38
            {}, // 39
            {}, // 40
            {}, // 41
            {}, // 42
            {1, 4, 3,  3, 4, 7,  4, 8, 7,  7, 8, 10}, // 43
            {}, // 44
            {}, // 45
            {}, // 46
            {}, // 47
            
            {4, 9, 5, 5, 9, 10}, // 48
            {}, // 49
            {}, // 50
            {1, 9, 2, 2, 9, 10}, // 51
            {}, // 52
            {}, // 53
            {}, // 54
            {}, // 55
            {}, // 56
            {}, // 57
            {}, // 58
            {}, // 59
            {}, // 60
            {}, // 61
            {}, // 62
            {}, // 63
            
            {6, 11, 9}, // 64
            {}, // 65
            {}, // 66
            {}, // 67
            {1, 3, 9, 3, 11, 9}, // 68
            {}, // 69
            {}, // 70
            {}, // 71
            {}, // 72
            {}, // 73
            {}, // 74
            {}, // 75
            {}, // 76
            {0, 2, 4, 2, 7, 4, 4, 7, 9, 7, 11, 9}, // 77
            {}, // 78
            {}, // 79
            
            {4, 6, 8, 6, 11, 8}, // 80
            {}, // 81
            {}, // 82
            {}, // 83
            {}, // 84
            {0, 3, 8, 3, 11, 8}, // 85
            {}, // 86
            {}, // 87
            {}, // 88
            {}, // 89
            {}, // 90
            {}, // 91
            {}, // 92
            {}, // 93
            {}, // 94
            {}, // 95
            
            {}, // 96
            {}, // 1
            {}, // 2
            {}, // 3
            {}, // 4
            {}, // 5
            {}, // 6
            {}, // 7
            {}, // 8
            {}, // 9
            {}, // 10
            {}, // 11
            {}, // 12
            {}, // 13
            {}, // 14
            {}, // 15
            
            {}, // 112
            {0, 1, 5,  1, 6, 5,  5, 6, 10,  6, 11, 10}, // 113
            {}, // 114
            {}, // 115
            {}, // 116
            {}, // 117
            {}, // 118
            {}, // 119
            {}, // 120
            {}, // 121
            {}, // 122
            {}, // 123
            {}, // 124
            {}, // 125
            {}, // 126
            {}, // 127
            
            {7, 10, 11}, // 128
            {}, // 129
            {}, // 130
            {}, // 131
            {}, // 132
            {}, // 133
            {}, // 134
            {}, // 135
            {2, 10, 3, 3, 10, 11}, // 136
            {}, // 137
            {}, // 138
            {}, // 139
            {}, // 140
            {}, // 141
            {0, 5, 1,  1, 5, 6,  5, 10, 6,  6, 10, 11}, // 142
            {}, // 143
            
            {}, // 144
            {}, // 1
            {}, // 2
            {}, // 3
            {}, // 4
            {}, // 5
            {}, // 6
            {}, // 7
            {}, // 8
            {}, // 9
            {}, // 10
            {}, // 11
            {}, // 12
            {}, // 13
            {}, // 14
            {}, // 15
            
            {5, 8, 7, 7, 8, 11}, // 160
            {}, // 161
            {}, // 162
            {}, // 163
            {}, // 164
            {}, // 165
            {}, // 166
            {}, // 167
            {}, // 168
            {}, // 169
            {0, 8, 3, 3, 8, 11}, // 170
            {}, // 171
            {}, // 172
            {}, // 173
            {}, // 174
            {}, // 175
            
            {}, // 176
            {}, // 177
            {0, 4, 2, 2, 4, 7, 4, 9, 7, 7, 9, 11}, // 178
            {}, // 179
            {}, // 180
            {}, // 181
            {}, // 182
            {}, // 183
            {}, // 184
            {}, // 185
            {}, // 186
            {}, // 187
            {}, // 188
            {}, // 189
            {}, // 190
            {}, // 191
            
            {6, 7, 9, 7, 10, 9}, // 192
            {}, // 193
            {}, // 194
            {}, // 195
            {}, // 196
            {}, // 197
            {}, // 198
            {}, // 199
            {}, // 200
            {}, // 201
            {}, // 202
            {}, // 203
            {1, 2, 9, 2, 10, 9}, // 204
            {}, // 205
            {}, // 206
            {}, // 207
            
            {}, // 208
            {}, // 209
            {}, // 210
            {}, // 211
            {1, 3, 4,  3, 7, 4,  4, 7, 8,  7, 10, 8}, // 212
            {}, // 213
            {}, // 214
            {}, // 215
            {}, // 216
            {}, // 217
            {}, // 218
            {}, // 219
            {}, // 220
            {}, // 221
            {}, // 222
            {}, // 223
            
            {}, // 224
            {}, // 225
            {}, // 226
            {}, // 227
            {}, // 228
            {}, // 229
            {}, // 230
            {}, // 231
            {2, 5, 3, 3, 5, 6, 5, 8, 6, 6, 8, 9}, // 232
            {}, // 233
            {}, // 234
            {}, // 235
            {}, // 236
            {}, // 237
            {}, // 238
            {}, // 239
            
            {4, 6, 5, 5, 6, 7}, // 240
            {}, // 241
            {}, // 242
            {}, // 243
            {}, // 244
            {}, // 245
            {}, // 246
            {}, // 247
            {}, // 248
            {}, // 249
            {}, // 250
            {}, // 251
            {}, // 252
            {}, // 253
            {}, // 254
            {}, // 255
    };
 
    
    double value;
    
    /**
     * Default constructor.
     * 
     * @param value
     *            the threshold value
     */
    public MarchingCubes(double value)
    {
        this.value = value;
    }
    
    public Mesh3D process(ScalarArray3D<?> array)
    {
        // get array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // Create the instance of result mesh
        SimpleTriMesh3D mesh = new SimpleTriMesh3D();
        
        // array of values for vertices of current configuration
        double[] values = new double[8];

        // the lists of intersections already computed for each plane
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesX0 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesX1 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesY0 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesY1 = new HashMap<>();
        HashMap<IntPoint3D, Mesh3D.Vertex> edgesZ = new HashMap<>();
        
        // iterate over 2-by-2-by-2 configurations
        for (int z = 0; z < sizeZ - 1; z++)
        {
            // update the lists of edges
            edgesX0 = edgesX1;
            edgesY0 = edgesY1;
            edgesX1 = new HashMap<>();
            edgesY1 = new HashMap<>();
            edgesZ.clear();
            
            for (int y = 0; y < sizeY - 1; y++)
            {
                // extract first values of the current configuration row
                values[1] = array.getValue(0, y    , z    );
                values[3] = array.getValue(0, y + 1, z    );
                values[5] = array.getValue(0, y    , z + 1);
                values[7] = array.getValue(0, y + 1, z + 1);
                
                for (int x = 0; x < sizeX - 1; x++)
                {
                    // get voxel values within configuration, using values from previous config
                    values[0] = values[1];
                    values[2] = values[3];
                    values[4] = values[5];
                    values[6] = values[7];
                    values[1] = array.getValue(x + 1, y,     z    );
                    values[3] = array.getValue(x + 1, y + 1, z    );
                    values[5] = array.getValue(x + 1, y,     z + 1);
                    values[7] = array.getValue(x + 1, y + 1, z + 1);

                    // compute configuration index
                    int index = configurationIndex(values);
                    
                    // depending on configuration, get edge indices for each face
                    int[] edgeIndices = confiFaces[index];
                    if (edgeIndices.length < 3)
                    {
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
                            int iv1 = edgeVertices[ie][0];
                            int iv2 = edgeVertices[ie][1];
                            
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
                            IntPoint3D intPos = new IntPoint3D(x + dx1, y + dy1, z + dz1);
                            
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
                                Point3D p1 = createVertex(x, y, z, iv1);
                                Point3D p2 = createVertex(x, y, z, iv2);
                                Point3D pos = interpolatePosition(p1, values[iv1], p2, values[iv2]);
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
        
        return mesh;
    }
    
    /**
     * Compute configuration index from the values associated to each of the
     * eight vertices.
     * 
     * @param values
     *            the values associated to each of the eight vertices
     * @param value
     *            the threshold value
     * @return the configuration index deduced from the values
     */
    private int configurationIndex(double[] values)
    {
        int index = 0;
        if (values[0] > this.value) index +=   1;
        if (values[1] > this.value) index +=   2;
        if (values[2] > this.value) index +=   4;
        if (values[3] > this.value) index +=   8;
        if (values[4] > this.value) index +=  16;
        if (values[5] > this.value) index +=  32;
        if (values[6] > this.value) index +=  64;
        if (values[7] > this.value) index += 128;
        return index;
    }
    
    private Point3D createVertex(int x, int y, int z, int index)
    {
        switch (index)
        {
        case 0:
            return new Point3D(x, y, z);
        case 1:
            return new Point3D(x + 1, y, z);
        case 2:
            return new Point3D(x, y + 1, z);
        case 3:
            return new Point3D(x + 1, y + 1, z);
        case 4:
            return new Point3D(x, y, z + 1);
        case 5:
            return new Point3D(x + 1, y, z + 1);
        case 6:
            return new Point3D(x, y + 1, z + 1);
        case 7:
            return new Point3D(x + 1, y + 1, z + 1);
        default:
            throw new IllegalArgumentException("Index must be comprised between 0 and 7");
        }
    }
   
    private Point3D interpolatePosition(Point3D p1, double v1, Point3D p2, double v2)
    {
        double t = (this.value - v1) / (v2 - v1);
        if (t <= 0) return p1;
        if (t >= 1) return p2;
        
        double x = p1.getX() * (1 - t) + p2.getX() * t;
        double y = p1.getY() * (1 - t) + p2.getY() * t;
        double z = p1.getZ() * (1 - t) + p2.getZ() * t;
        return new Point3D(x, y, z);
    }
    
    class EdgeVertex
    {
        int x;
        int y;
        int z;
        Mesh3D.Vertex vertex;
        
        public EdgeVertex(int x, int y, int z, Mesh3D.Vertex vertex)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vertex = vertex;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof EdgeVertex))
                return false;
            EdgeVertex that = (EdgeVertex) obj;
            if (this.x != that.x) return false;
            if (this.y != that.y) return false;
            if (this.z != that.z) return false;
            return true;
        }
    }
    
    class IntPoint3D implements Comparable<IntPoint3D>
    {
        int x;
        int y;
        int z;
        
        public IntPoint3D(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public int compareTo(IntPoint3D that)
        {
            if (this.z != that.z) return that.z - this.z;
            if (this.y != that.y) return that.y - this.y;
            if (this.x != that.x) return that.x - this.x;
            return 0;
        }

        @Override
        public int hashCode()
        {
            int code = 17;
            code = 31 * code + this.x;
            code = 31 * code + this.y;
            code = 31 * code + this.z;
            return code;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof IntPoint3D))
                return false;
            IntPoint3D that = (IntPoint3D) obj;
            if (this.x != that.x) return false;
            if (this.y != that.y) return false;
            if (this.z != that.z) return false;
            return true;
        }
    }
    
    public static final void main(String[] args) throws IOException
    {
        UInt8Array3D array = UInt8Array3D.create(3, 3, 3);
        array.fillValue(0);
        array.setValue(1, 1, 1, 10);
        
        MarchingCubes mc = new MarchingCubes(5.0);
        Mesh3D mesh = mc.process(array);
        
        System.out.println("Vertices: " + mesh.vertexNumber());
        System.out.println("Faces:    " + mesh.faceNumber());
        
        new OffMeshWriter(new File("singleVertexMarchingCube.off")).writeMesh(mesh);
    }
    
}
