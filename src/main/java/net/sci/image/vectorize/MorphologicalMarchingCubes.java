/**
 * 
 */
package net.sci.image.vectorize;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.mesh.Mesh3D;
import net.sci.geom.mesh.SimpleTriMesh3D;

/**
 * Marching cubes algorithm based on a connectivity option for foreground /
 * background voxels.
 * </p>
 * 
 * The implementation is called "morphological" because the following constraint
 * was taken into account: the Euler number of the resulting mesh should
 * correspond to the Euler number computed on the 3D binarisation of the array.
 * 
 * The Euler number of the mesh is obtained by counting vertices and faces (and
 * using Euler formula for graphs). The Euler number of the binary image is
 * obtained by summing individual contributions of 2-by-2-by-2 elementary binary
 * configurations that compose the array. The relationship does not hold if the
 * binarisation touches array border.
 * 
 * In practice, connectivity 6 is used for foreground voxels.
 * 
 * @author dlegland
 *
 */
public class MorphologicalMarchingCubes extends AlgoStub
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
     * For each of the twelve edges, the indices of the first and second
     * vertices.
     * 
     * Indices are ordered from lower to larger index.
     */
    static final int[][] edgeVertexIndices = {
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
    static final int[][] faceEdgeIndices = {
            {}, // 0
            {0, 1, 4}, // 1
            {0, 5, 2}, // 2
            {1, 4, 2,  2, 4, 5}, // 3 -> 4 edges, 2 triangles
            {1, 3, 6}, // 4
            {0, 3, 4,  3, 6, 4}, // 5
            {0, 5, 2,  1, 3, 6}, // 6
            {2, 3, 4,  2, 4, 5,  3, 6, 4}, // 7
            {2, 7, 3}, // 8
            {0, 1, 4,  2, 7, 3}, // 9
            {0, 5, 3,  3, 5, 7}, // 10
            {3, 1, 5,  3, 5, 7,  1, 4, 5}, // 11
            {1, 2, 6,  2, 7, 6}, // 12
            {0, 2, 6,  0, 6, 4,  2, 7, 6}, // 13
            {1, 0, 7,  1, 7, 6,  0, 5, 7}, // 14
            {4, 5, 6,  5, 7, 6}, // 15
            
            {4, 9, 8}, // 16
            {0, 1, 8,  1, 9, 8}, // 17
            {0, 5, 2,  4, 9, 8}, // 18
            {8, 5, 1,  8, 1, 9,  5, 2, 1}, // 19
            {1, 3, 6,  4, 9, 8}, // 20
            {6, 9, 0,  6, 0, 3,  9, 8, 0}, // 21
            {0, 5, 2,  1, 3, 6,  4, 9, 8}, // 22
            {2, 3, 5,  3, 6, 5,  5, 6, 8,  6, 9, 8}, // 23
            {2, 7, 3,  4, 9, 8}, // 24
            {0, 1, 8,  8, 1, 9,  2, 7, 3}, // 25
            {0, 5, 3,  3, 5, 7,  4, 9, 8}, // 26
            {3, 1, 5,  3, 5, 7,  1, 8, 5,  1, 9, 8}, // 27
            {6, 1, 7,  7, 1, 2,  9, 8, 4}, // 28
            {0, 2, 6,  0, 6, 9,  0, 9, 8,  2, 7, 6}, // 29
            {1, 0, 7,  1, 7, 6,  0, 5, 7,  4, 9, 8}, // 30
            {7, 6, 5,  9, 5, 6,  8, 5, 9}, // 31
            
            {5, 8, 10}, // 32
            { 0, 1, 4,  5, 8, 10}, // 33
            { 0, 8, 2,  2, 8, 10}, // 34
            { 4, 8, 2,   4, 2, 1,  8, 10, 2}, // 35
            { 1, 3, 6,  5, 8, 10}, // 36
            { 4, 0, 6,   6, 0, 3,  8, 10, 5}, // 37
            {2, 0, 10,  10, 0, 8,   3, 6, 1}, // 38
            { 2, 3, 4,   2, 4, 8,  2, 8, 10,  3, 6, 4}, // 39
            { 2, 7, 3,  5, 8, 10}, // 40
            { 0, 1, 4,  5, 8, 10,   2, 7, 3}, // 41
            {10, 7, 0,  10, 0, 8,   7, 3, 0}, // 42
            { 1, 4, 3,   3, 4, 7,   4, 8, 7,  7, 8, 10}, // 43
            { 2, 7, 1,   1, 7, 6,  5, 8, 10}, // 44
            { 0, 2, 6,   0, 6, 4,   2, 7, 6,  5, 8, 10}, // 45
            { 1, 0, 7,   1, 7, 6,  0, 10, 7,  0, 8, 10}, // 46
            { 6, 4, 7,   8, 7, 4,  10, 7, 8}, // 47
            
            { 4, 9, 5,  5, 9, 10}, // 48
            { 5, 0, 9,  5, 9, 10,   0, 1, 9}, // 49
            {0, 4, 10,  0, 10, 2,  4, 9, 10}, // 50
            { 1, 9, 2,  2, 9, 10}, // 51
            { 4, 9, 5,  5, 9, 10,   1, 3, 6}, // 52
            { 5, 0, 9,  5, 9, 10,   0, 6, 9,  0, 3, 6}, // 53
            {0, 4, 10,  0, 10, 2,  4, 9, 10,  1, 3, 6}, // 54
            {10, 2, 9,   3, 9, 2,   6, 9, 3}, // 55
            {10, 5, 9,   9, 5, 4,   7, 3, 2}, // 56
            { 5, 0, 9,  5, 9, 10,   0, 1, 9,   2, 7, 3}, // 57
            {0, 4, 10,  0, 10, 7,   0, 7, 3,  4, 9, 10}, // 58
            {9, 10, 1,  7, 1, 10,   3, 1, 7}, // 59
            {10, 5, 4,  10, 4, 9,   7, 6, 2,   2, 6, 1}, // 60
            { 0, 2, 6,   0, 6, 9,   0, 9, 5,   2, 7, 6,  5, 9, 10}, // 61
            {0, 4, 10,  0, 10, 7,   0, 7, 1,  4, 9, 10,   1, 7, 6}, // 62
            { 6, 9, 7,  7, 9, 10}, // 63
            
            {6, 11, 9}, // 64
            { 0, 1, 4,  6, 11, 9}, // 65
            { 0, 5, 2,  6, 11, 9}, // 66
            { 1, 4, 2,   2, 4, 5,  6, 11, 9}, // 67
            { 1, 3, 9,  3, 11, 9}, // 68
            { 9, 4, 3,  9, 3, 11,   4, 0, 3}, // 69
            { 1, 3, 9,  9, 3, 11,   0, 5, 2}, // 70
            { 2, 3, 4,   2, 4, 5,   3, 9, 4,  3, 11, 9}, // 71
            { 2, 7, 3,  6, 11, 9}, // 72
            { 3, 2, 7,  6, 11, 9,   1, 4, 0}, // 73
            { 7, 3, 5,   5, 3, 0,  11, 9, 6}, // 74
            { 3, 1, 5,   3, 5, 7,   1, 4, 5,  6, 11, 9}, // 75
            {7, 11, 1,   7, 1, 2,  11, 9, 1}, // 76
            { 0, 2, 4,   2, 7, 4,   4, 7, 9,  7, 11, 9}, // 77
            { 1, 0, 7,  1, 7, 11,  1, 11, 9,  0, 5, 7}, // 78
            { 5, 7, 4,  11, 7, 4,  9, 4, 11}, // 79
            
            { 4, 6, 8,  6, 11, 8}, // 80
            { 1, 6, 8,   1, 8, 0,  6, 11, 8}, // 81
            {11, 4, 8,   8, 4, 6,   5, 2, 8}, // 82
            { 8, 5, 1,   8, 1, 6,  8, 6, 11,   5, 2, 1}, // 83
            {4, 1, 11,  4, 11, 8,  1, 3, 11}, // 84
            { 0, 3, 8,  3, 11, 8}, // 85
            {4, 1, 11,  4, 11, 8,  1, 3, 11,   0, 5, 2}, // 86
            {11, 8, 3,   5, 3, 8,   2, 3, 5}, // 87
            {6, 11, 4,  4, 11, 8,   3, 2, 7}, // 88
            { 1, 6, 8,   1, 8, 0,  6, 11, 8,   3, 2, 7}, // 89
            { 8, 4, 6,  8, 6, 11,   5, 7, 0,   0, 7, 3}, // 90
            { 1, 6, 8,   1, 8, 5,   1, 5, 3,  6, 11, 8,  3, 5, 7}, // 91
            {7, 11, 1,   7, 1, 2,  11, 4, 1,  11, 8, 4}, // 92
            {8, 0, 11,  2, 11, 0,  7, 11, 2}, // 93
            { 1, 0, 7,  1, 7, 11,  1, 11, 4,   0, 5, 7,  4, 11, 8}, // 94
            { 5, 7, 8,  7, 11, 8}, // 95
            
            {5, 8, 10,  6, 11, 9}, // 96
            { 0, 1, 4,  5, 8, 10,  6, 11, 9}, // 97
            {8, 10, 0,  0, 10, 2,  9, 6, 11}, // 98
            { 4, 8, 2,   4, 2, 1,  8, 10, 2,  9, 6, 11}, // 99
            {11, 9, 3,   3, 9, 1,  10, 5, 0}, // 100
            { 9, 4, 3,  9, 3, 11,   4, 0, 3,  8, 10, 5}, // 101
            {11, 9, 1,  11, 1, 3,  10, 2, 8,   8, 2, 0}, // 102
            { 4, 8, 2,   4, 2, 3,   4, 3, 9,  8, 10, 2,  9, 3, 11}, // 103
            {11, 9, 6,   7, 3, 2,  10, 5, 8}, // 104
            { 0, 1, 4,   2, 7, 3,  5, 8, 10,  6, 11, 9}, // 105
            {10, 7, 0,  10, 0, 8,   7, 3, 0,  11, 9, 6}, // 106
            { 1, 3, 4,   3, 7, 4,   4, 7, 8,  7, 10, 8,  6, 9, 11}, // 107
            {7, 11, 1,   7, 1, 2,  11, 9, 1,  10, 5, 8}, // 108
            { 2, 0, 7,   0, 4, 7,  7, 4, 11,  4, 9, 11,  5, 10, 8}, // 109
            {7, 11, 1,   7, 1, 0,  7, 0, 10,  11, 9, 1,  10, 0, 8}, // 110
            {0, 11, 9,   7, 9, 4,  7, 4, 10,  10, 4, 8}, // 111
            
            {11, 10, 4,  11, 4, 6,    10, 5, 4}, // 112
            { 0, 1, 5,    1, 6, 5,    5, 6, 10,  6, 11, 10}, // 113
            {11, 10, 4,  11, 4, 6,    10, 0, 4,   10, 2, 0}, // 114
            {2, 1, 10,   6, 10, 1,   11, 10, 6}, // 115
            {11, 10, 4,  11, 4, 1,    11, 1, 3,   10, 5, 4}, // 116
            {3, 11, 0,   10, 0, 11,   5, 0, 10},  // 117
            {4, 1, 11,   4, 11, 10,   4, 10, 0,   1, 3, 11,  0, 10, 2}, // 118
            {2, 3, 10,   3, 11, 10}, // 119
            {11, 10, 4,  11, 4, 6,    10, 5, 4,    7, 3, 2}, // 120
            {10, 11, 5,  11, 6, 5,     5, 6, 0,    6, 1, 0,  7, 2, 3}, // 121
            {10, 7, 0,   10, 0, 4,   10, 4, 11,    7, 3, 0,  11, 4, 6}, // 122
            {1, 6, 11,   1, 11, 10,   1, 10, 3,   3, 10, 7}, // 123
            {11, 10, 4,  11, 4, 1,    11, 1, 7,   10, 5, 4,  7, 1, 2}, // 124
            { 0, 2, 7,   0, 7, 11,    0, 11, 5,  5, 11, 10}, // 125
            { 0, 7, 1,   7, 0, 10,    10, 0, 4,  10, 4, 11,  11, 4, 1,  1, 7, 11}, // 126
            {7, 11, 10}, // 127
            
            {7, 10, 11}, // 128
            {  0, 1, 4,  7, 10, 11}, // 129
            {  0, 5, 2,  7, 10, 11}, // 130
            {  5, 2, 4,    4, 2, 1,  10, 11, 6}, // 131
            {  1, 3, 6,  7, 10, 11}, // 132
            {  3, 6, 0,    0, 6, 4,  7, 10, 11}, // 133
            {  3, 6, 1,    2, 0, 5,  7, 10, 11}, // 134
            {  2, 3, 4,    2, 4, 5,    3, 6, 4,  7, 10, 11}, // 135
            { 2, 10, 3,  3, 10, 11}, // 136
            { 3, 2, 11,  11, 2, 10,    1, 4, 0}, // 137
            { 5, 10, 3,    5, 3, 0,  10, 11, 3}, // 138
            {  3, 1, 5,   3, 5, 10,  3, 10, 11,    1, 4, 5}, // 139
            { 11, 6, 2,  11, 2, 10,    6, 1, 2}, // 140
            {  0, 2, 6,    0, 6, 4,   2, 11, 6,  2, 10, 11}, // 141
            {  0, 5, 1,    1, 5, 6,   5, 10, 6,  6, 10, 11}, // 142
            {  4, 5, 6,   10, 6, 5,  11, 6, 10}, // 143
            
            {  4, 9, 8,  7, 10, 11}, // 144
            {  0, 1, 8,    1, 9, 8,  7, 10, 11}, // 145  { 2, 0, 10,   10, 0, 8,    3, 6, 1}
            {  8, 4, 9,  10, 11, 7,    5, 2, 0}, // 146
            {  8, 5, 1,    5, 1, 9,    5, 2, 1,  7, 10, 11}, // 147
            {11, 7, 10,    9, 8, 4,    6, 1, 3}, // 148
            {  6, 9, 0,    6, 0, 3,    9, 8, 0,  11, 7, 10}, // 149
            {  0, 5, 2,    1, 3, 6,    4, 9, 8,  7, 10, 11}, // 150
            {  5, 8, 2,    8, 9, 2,    2, 9, 3,    9, 6, 3,  10, 7, 11}, // 151
            {10, 11, 2,   2, 11, 3,    8, 4, 9}, // 152
            {  0, 1, 9,    0, 9, 8,   2, 10, 3,  3, 10, 11}, // 153
            { 5, 10, 3,    5, 3, 0,  10, 11, 3,    8, 4, 9}, // 154
            { 5, 10, 3,    5, 3, 1,    5, 1, 8,  10, 11, 3,    8, 1, 9}, // 155
            { 11, 6, 2,  11, 2, 10,    6, 1, 2,    9, 8, 4}, // 156
            {  6, 9, 0,    6, 0, 2,   6, 2, 11,    9, 8, 0,  11, 2, 10}, // 157
            { 6, 11, 1,  11, 10, 8,   1, 10, 0,   10, 5, 0,  9, 4, 8}, // 158
            {5, 10, 11,   5, 11, 6,    5, 6, 8,    8, 6, 9}, // 159
            
            { 5, 8, 7,  7, 8, 11}, // 160
            { 5, 8, 7,  7, 8, 11,   0, 1, 4}, // 161
            { 7, 2, 8,  7, 8, 11,   2, 0, 8}, // 162
            { 4, 8, 2,   4, 2, 1,   8, 7, 2,  8, 11, 7}, // 163
            {11, 7, 8,   8, 7, 5,   6, 1, 3}, // 164
            { 3, 6, 4,   3, 4, 0,  7, 5, 11,  11, 5, 8}, // 165
            { 7, 2, 8,  7, 8, 11,   2, 0, 8,   3, 6, 1}, // 166
            { 2, 3, 4,   2, 4, 8,   2, 8, 7,   3, 6, 4,  7, 8, 11}, // 167
            {2, 5, 11,  2, 11, 3,  5, 8, 11}, // 168
            {2, 5, 11,  2, 11, 3,  5, 8, 11,   0, 1, 4}, // 169
            { 0, 8, 3,  3, 8, 11}, // 170
            {11, 3, 8,   1, 8, 3,   4, 8, 1}, // 171
            {11, 6, 2,  11, 2, 5,  11, 5, 8,   6, 1, 2}, // 172
            {2, 5, 11,  2, 11, 6,   2, 6, 0,  5, 8, 11,  0, 6, 4}, // 173
            {8, 11, 0,  6, 0, 11,   1, 0, 6}, // 174
            { 4, 8, 6,  6, 8, 11}, // 175
            
            {9, 11, 5,   9, 5, 4,  11, 7, 5}, // 176 
            {9, 11, 5,   9, 5, 0,   9, 0, 1,  11, 7, 5}, // 177
            { 0, 4, 2,   2, 4, 7,   4, 9, 7,  7, 9, 11}, // 178
            { 1, 9, 2,  11, 2, 9,  7, 2, 11}, // 179
            {9, 11, 5,   9, 5, 4,  11, 7, 5,   6, 1, 3}, // 180
            {9, 11, 5,   9, 5, 0,   9, 0, 6,  11, 7, 5,  6, 0, 3}, // 181
            { 4, 0, 9,   0, 2, 9,  9, 2, 11,  2, 7, 11,  1, 6, 3}, // 182
            { 2, 3, 6,   2, 6, 9,   2, 9, 7,  7, 9, 11}, // 183
            {9, 11, 5,   9, 5, 4,  11, 2, 5,  11, 3, 2}, // 184
            { 5, 0, 9,  5, 9, 11,  5, 11, 2,  0, 1, 9,  2, 11, 3}, // 185
            {3, 0, 11,  4, 11, 0,  9, 11, 4}, // 186
            { 1, 9, 3,  3, 9, 11}, // 187
            {11, 6, 2,  11, 2, 5,  11, 5, 9,  6, 1, 2,  9, 5, 4}, // 188
            {11, 5, 9,  5, 11, 2,  2, 11, 6,  2, 6, 0,  0, 6, 9,  9, 5, 0}, // 189
            {11, 6, 1,  11, 1, 0,  11, 0, 9,  9, 0, 4}, // 190
            {6, 9, 11}, // 191
            
            { 6, 7, 9,  7, 10, 9}, // 192
            {9, 6, 10,  10, 6, 7,  6, 11, 9}, // 193
            {7, 10, 6,  6, 10, 9,   2, 0, 5}, // 194
            { 1, 4, 5,   1, 5, 2,   6, 7, 9,  9, 7, 10}, // 195
            { 3, 7, 9,   3, 9, 1,  7, 10, 9}, // 196
            { 3, 7, 9,   3, 9, 4,   3, 4, 0,  7, 10, 9}, // 197
            { 3, 7, 9,   3, 9, 1,  7, 10, 9,   2, 0, 5}, // 198
            { 3, 7, 9,   3, 9, 4,   3, 4, 2,  7, 10, 9,   2, 4, 5}, // 199
            {6, 3, 10,  6, 10, 9,  3, 2, 10}, // 200
            {6, 3, 10,  6, 10, 9,  3, 2, 10,   1, 4, 0}, // 201
            {6, 3, 10,  6, 10, 9,  3, 5, 10,   3, 0, 5}, // 202
            { 3, 1, 5,  3, 5, 10,  3, 10, 6,   1, 4, 5,  6, 10, 9}, // 203
            { 1, 2, 9,  2, 10, 9}, // 204
            {10, 9, 2,   4, 2, 9,   0, 2, 4}, // 205
            {9, 1, 10,  0, 10, 1,  5, 10, 0}, // 206
            { 4, 5, 9,  5, 10, 9}, // 207
            
            {10, 8, 6,  10, 6, 7,   8, 4, 6}, // 208
            {10, 8, 6,  10, 6, 7,   8, 1, 6,   8, 0, 1}, // 209
            {10, 8, 6,  10, 6, 7,   8, 4, 6,   5, 2, 0}, // 210
            { 8, 5, 1,   8, 1, 6,  8, 6, 10,   5, 2, 1,  10, 6, 7}, // 211
            { 1, 3, 4,   3, 7, 4,   4, 7, 8,  7, 10, 8}, // 212
            { 0, 3, 8,   7, 3, 8,  10, 8, 7},  // 213
            {7, 3, 10,  3, 1, 10,  10, 1, 8,   1, 4, 8,   2, 5, 0}, // 214
            {3, 7, 10,  3, 10, 8,   3, 8, 2,   2, 8, 5}, // 215
            {10, 8, 6,  10, 6, 3,  10, 3, 2,   8, 4, 6}, // 216
            {6, 3, 10,  6, 10, 8,   6, 8, 1,  3, 2, 10,   1, 8, 0}, // 217
            {10, 8, 6,  10, 6, 3,  10, 3, 5,   8, 4, 6,   5, 3, 0}, // 218
            {3, 10, 6,  10, 3, 5,   5, 3, 1,   5, 1, 8,   8, 1, 6,  6, 10, 8}, // 219
            {2, 10, 1,  8, 1, 10,   4, 1, 8}, // 220
            { 0, 2, 8,  2, 10, 8}, // 221
            { 1, 0, 5,  1, 5, 10,  1, 10, 4,   4, 10, 8}, // 222
            {5, 10, 8}, // 223
            
            {8, 9, 7,  8, 7, 5,  9, 6, 7}, // 224
            {8, 9, 7,  8, 7, 5,  9, 6, 7,  4, 0, 1}, // 225
            {8, 9, 7,  8, 7, 2,  8, 2, 0,  9, 6, 7}, // 226
            {8, 9, 7,  8, 7, 2,  8, 2, 4,  9, 6, 7,  4, 2, 1}, // 227
            {8, 9, 7,  8, 7, 5,  9, 3, 7,  9, 1, 3}, // 228
            {9, 4, 3,  9, 3, 7,  9, 7, 8,  4, 0, 3,  8, 7, 5}, // 229
            {7, 2, 8,  7, 8, 9,  7, 9, 3,  2, 0, 8,  3, 9, 1}, // 230
            {8, 2, 4,  2, 8, 7,  7, 8, 9,  7, 9, 3,  3, 9, 4,  4, 2, 3}, // 231
            {2, 5, 3,  3, 5, 6,  5, 8, 6,  6, 8, 9}, // 232
            {9, 8, 6,  8, 5, 6,  6, 5, 3,  5, 2, 3,  4, 1, 0}, // 233
            {0, 8, 3,  9, 3, 8,  6, 3, 9}, // 234
            {8, 9, 6,  8, 6, 3,  8, 3, 4,  4, 3, 1}, // 235
            {1, 2, 9,  5, 9, 2,  8, 9, 5}, // 236
            {2, 5, 8,  2, 8, 9,  2, 9, 0,  0, 9, 4}, // 237
            {0, 8, 1,  1, 8, 9}, // 238
            {4, 8, 9}, // 239
            
            {4, 6, 5,  5, 6, 7}, // 240
            {7, 5, 6,  0, 6, 5,  1, 6, 0}, // 241
            {6, 7, 4,  2, 4, 7,  0, 4, 2}, // 242
            {1, 6, 2,  2, 6, 7}, // 243
            {5, 4, 7,  1, 7, 4,  3, 7, 1}, // 244
            {0, 3, 5,  3, 7, 5}, // 245
            {4, 1, 3,  4, 3, 7,  4, 7, 0,  0, 7, 2}, // 246
            {2, 3, 7}, // 247
            {4, 6, 5,  3, 5, 6,  2, 5, 3}, // 248
            {6, 3, 2,  6, 2, 5,  6, 5, 1,  1, 5, 0}, // 249
            {0, 4, 3,  3, 4, 6}, // 250
            {1, 6, 3}, // 251
            {1, 2, 4,  2, 5, 4}, // 252
            {0, 2, 5}, // 253
            {0, 4, 1}, // 254
            {}, // 255
    };
 
    
    double value;
    
    /**
     * Default constructor.
     * 
     * @param value
     *            the threshold value
     */
    public MorphologicalMarchingCubes(double value)
    {
        this.value = value;
    }
    
    /**
     * Converts the input 3D array into a triangle mesh, by computing an
     * isosurface using the inner threshold value.
     * 
     * @param array
     *            the array containing scalar 3D data
     * @return a triangular mesh representing the isosurface.
     */
    public Mesh3D process(ScalarArray3D<?> array)
    {
        // retrieve array size
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
            this.fireProgressChanged(this, z, sizeZ);
            
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
                            int iv1 = edgeVertexIndices[ie][0];
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
            
            this.fireProgressChanged(this, 1, 1);
        }
        
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
    
    /**
     * Stores the coordinates of a voxel within a 3D image, in image coordinates
     * (0-based).
     */
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
}
