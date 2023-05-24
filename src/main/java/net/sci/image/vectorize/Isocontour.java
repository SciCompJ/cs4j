/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;
import java.util.HashSet;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.MultiCurve2D;
import net.sci.geom.geom2d.polygon.LineString2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom2d.polygon.Polyline2D;
import net.sci.geom.graph.AdjListDirectedGraph2D;
import net.sci.geom.graph.DirectedGraph2D;
import net.sci.geom.graph.Graph2D;

/**
 * Computes the set of curves (polylines) corresponding to isocontour of a
 * scalar 2D image, using marching squares algorithm.
 * 
 * @see LabelMapBoundaryPolygons
 * @see MorphologicalMarchingCubes
 * 
 * @author dlegland
 *
 */
public class Isocontour extends AlgoStub
{
    // =============================================================
    // Static declarations
    
    
    public static final MultiCurve2D convertContourGraphToPolylines(AdjListDirectedGraph2D graph)
    {
        int nv = graph.vertexCount();
        
        // initialize lists of vertices to process
        HashSet<DirectedGraph2D.Vertex> verticesToProcess = new HashSet<>(nv);
        HashSet<DirectedGraph2D.Vertex> initialVertices = new HashSet<>(nv);
        for (DirectedGraph2D.Vertex vertex : graph.vertices())
        {
            verticesToProcess.add(vertex);
            switch (vertex.inDegree())
            {
                case 0 -> initialVertices.add(vertex);
                case 1 ->
                {
                    // check validity of total degree
                    if (vertex.outDegree() > 1)
                    {
                        throw new RuntimeException("Do not expect vertices with more than one outgoing edge");
                    }
                }
                default -> throw new RuntimeException("Do not expect vertices with more than one incoming edge");
            }
        }
        
        // allocate array for storing result polylines
        ArrayList<Polyline2D> allPolylines = new ArrayList<>();
        
        // start by processing open polylines
        while(!initialVertices.isEmpty())
        {
            // retrieve a vertex from the pool
            DirectedGraph2D.Vertex currentVertex = initialVertices.iterator().next();
            initialVertices.remove(currentVertex);
            verticesToProcess.remove(currentVertex);
            
            // initialize with first vertex
            ArrayList<Point2D> positions = new ArrayList<Point2D>();
            positions.add(currentVertex.position());
            
            // iterate until we find extremity
            while (currentVertex.outDegree() > 0)
            {
                // switch to next vertex (the unique neighbor)
                currentVertex = currentVertex.outEdges().iterator().next().target();
                verticesToProcess.remove(currentVertex);
                
                positions.add(currentVertex.position());
            }
            
            // create an open polyline from the list of positions
            allPolylines.add(LineString2D.create(positions));
        }
        
        // process remaining vertices, that form closed polylines
        while(!verticesToProcess.isEmpty())
        {
            // retrieve a vertex from the pool
            DirectedGraph2D.Vertex currentVertex = verticesToProcess.iterator().next();
            verticesToProcess.remove(currentVertex);
            DirectedGraph2D.Vertex initialVertex = currentVertex;
            
            // initialize with first vertex
            ArrayList<Point2D> positions = new ArrayList<Point2D>();
            positions.add(currentVertex.position());
            
            // switch to next vertex (one of the two neighbors)
            currentVertex = currentVertex.outEdges().iterator().next().target();
            
            // iterate until we come back at initial vertex
            while (currentVertex != initialVertex)
            {
                positions.add(currentVertex.position());
                
                // identify next vertex
                verticesToProcess.remove(currentVertex);
                currentVertex = currentVertex.outEdges().iterator().next().target();
            }
            
            // can create a polyline from the list of positions
            allPolylines.add(LinearRing2D.create(positions));
        }
        
        return new MultiCurve2D(allPolylines);
    }

    /**
     * The location of a grid edge with respect to the current 2-by-2 pixel
     * configuration.
     */
    private static enum EdgeLocation
    {
        /** The (horizontal) edge between configuration vertices v0 and v1. */
        E0,
        /** The (vertical) edge between configuration vertices v0 and v2. */
        E1,
        /** The (vertical) edge between configuration vertices v1 and v3. */
        E2,
        /** The (horizontal) edge between configuration vertices v2 and v3. */
        E3;
    };

    /**
     * The list of edge type pairs that need to be linked for each
     * configuration.
     * 
     * Corresponds to the 4-connectivity.
     */
    private EdgeLocation[][] configLinkedEdges = new EdgeLocation[][]
    { 
        {}, 
        { EdgeLocation.E1, EdgeLocation.E0 }, 
        { EdgeLocation.E0, EdgeLocation.E2 }, 
        { EdgeLocation.E1, EdgeLocation.E2 }, 
        { EdgeLocation.E3, EdgeLocation.E1 },
        { EdgeLocation.E3, EdgeLocation.E0 }, 
        { EdgeLocation.E0, EdgeLocation.E1, EdgeLocation.E3, EdgeLocation.E2 }, 
        { EdgeLocation.E3, EdgeLocation.E2 }, 
        { EdgeLocation.E2, EdgeLocation.E3 },
        { EdgeLocation.E1, EdgeLocation.E3, EdgeLocation.E2, EdgeLocation.E0 }, 
        { EdgeLocation.E0, EdgeLocation.E3 }, 
        { EdgeLocation.E1, EdgeLocation.E3 }, 
        { EdgeLocation.E2, EdgeLocation.E1 }, 
        { EdgeLocation.E2, EdgeLocation.E0 },
        { EdgeLocation.E0, EdgeLocation.E1 }, 
        {}, 
    };
    
    
    // =============================================================
    // class variables
    
    /**
     * The threshold value.
     */
    double value;

    
    // =============================================================
    // Constructor

    /**
     * Default empty constructor.
     */
    public Isocontour(double value)
    {
        this.value = value;
    }


    // =============================================================
    // Methods

    public Geometry2D processScalar2d(ScalarArray2D<? extends Scalar> array)
    {
        return convertContourGraphToPolylines(computeGraph(array));
    }

    public AdjListDirectedGraph2D computeGraph(ScalarArray2D<? extends Scalar> array)
    {
        // create intermediate data structure
        ArrayList<EdgeVertex> edgeVertices = new ArrayList<EdgeVertex>();
        ArrayList<int[]> adjacencies = new ArrayList<int[]>();

        // size of array
        int size0 = array.size(0);
        int size1 = array.size(1);

        // iterate over image pixels
        for (int iy = 0; iy < size1 - 1; iy++)
        {

            double v01 = array.getValue(0, iy);
            double v11 = array.getValue(0, iy + 1);
            boolean b01 = v01 >= value;
            boolean b11 = v11 >= value;

            for (int ix = 0; ix < size0 - 1; ix++)
            {
                // sweep pixel values of grid vertices already considered
                double v00 = v01;
                double v10 = v11;
                // compute value of right-configuration pixels
                v01 = array.getValue(ix + 1, iy);
                v11 = array.getValue(ix + 1, iy + 1);

                // boolean flags indicating whether pixels are below or above
                // threshold value
                boolean b00 = b01;
                boolean b10 = b11;
                b01 = v01 >= value;
                b11 = v11 >= value;

                // compute the configuration code from binary pattern of pixel
                // value thresholds
                int configCode = 0;
                configCode += b00 ? 1 : 0;
                configCode += b01 ? 2 : 0;
                configCode += b10 ? 4 : 0;
                configCode += b11 ? 8 : 0;

                // get the edge type corresponding to the vertices to link
                // within configuration
                EdgeLocation[] linkedEdges = configLinkedEdges[configCode];

                // iterate over pairs of vertex indices to create edges
                for (int i = 0; i < linkedEdges.length; i += 2)
                {
                    // identifies the edges that need to be linked
                    EdgeLocation edge1 = linkedEdges[i];
                    EdgeLocation edge2 = linkedEdges[i + 1];

                    // retrieve index of first vertex, or create a new one if it
                    // does not exist
                    int indV1 = findVertexIndex(edgeVertices, ix, iy, edge1);
                    if (indV1 == -1)
                    {
                        EdgeVertex vertex = createVertex(ix, iy, edge1);
                        vertex.position = computeVertexPosition(vertex, edge1, v00, v01, v10, v11);
                        indV1 = edgeVertices.size();
                        edgeVertices.add(vertex);
                    }

                    // retrieve index of second vertex, or create a new one if
                    // it does not exist
                    int indV2 = findVertexIndex(edgeVertices, ix, iy, edge2);
                    if (indV2 == -1)
                    {
                        EdgeVertex vertex = createVertex(ix, iy, edge2);
                        vertex.position = computeVertexPosition(vertex, edge2, v00, v01, v10, v11);
                        indV2 = edgeVertices.size();
                        edgeVertices.add(vertex);
                    }

                    adjacencies.add(new int[] { indV1, indV2 });
                }
            }
        }

        // create the graph
        AdjListDirectedGraph2D graph = new AdjListDirectedGraph2D();
        
        // populate with vertices
        Graph2D.Vertex[] vertices = new Graph2D.Vertex[edgeVertices.size()];
        int iv = 0;
        for (EdgeVertex v : edgeVertices)
        {
            vertices[iv++] = graph.addVertex(v.getPosition());
        }
        
        // populate with edges
        for (int[] adj : adjacencies)
        {
            graph.addEdge(vertices[adj[0]], vertices[adj[1]]);
        }

        return graph;
    }

    private int findVertexIndex(ArrayList<EdgeVertex> vertices, int x, int y, EdgeLocation edge)
    {
        int x2 = x;
        int y2 = y;
        if (edge == EdgeLocation.E2) x2++;
        if (edge == EdgeLocation.E3) y2++;
        
        boolean horiz = edge == EdgeLocation.E0 || edge == EdgeLocation.E3;
        int index = 0;
        for (EdgeVertex vertex : vertices)
        {
            if (vertex.ix == x2 && vertex.iy == y2 && vertex.horiz == horiz)
            { 
                return index; 
            }
            index++;
        }
        // could not find vertex within array
        return -1;
    }

    private EdgeVertex createVertex(int x, int y, EdgeLocation edgeType)
    {
        return switch (edgeType)
        {
            case E0 -> new EdgeVertex(x, y, true);
            case E1 -> new EdgeVertex(x, y, false);
            case E2 -> new EdgeVertex(x + 1, y, false);
            case E3 -> new EdgeVertex(x, y + 1, true);
        };
    }

    private Point2D computeVertexPosition(EdgeVertex vertex, EdgeLocation edge, double v00, double v01, double v10, double v11)
    {
        return switch (edge)
        {
            case E0 -> new Point2D(vertex.ix + interpolate(value, v00, v01), vertex.iy);
            case E1 -> new Point2D(vertex.ix, vertex.iy + interpolate(value, v00, v10));
            case E2 -> new Point2D(vertex.ix, vertex.iy + interpolate(value, v01, v11));
            case E3 -> new Point2D(vertex.ix + interpolate(value, v10, v11), vertex.iy);
        };
    }

    /**
     * Returns the fraction (between 0 and 1) corresponding to value-vmin within
     * the interval vmax-vmin.
     * 
     * @param value
     *            the value whose position need to be interpolated
     * @param vmin
     *            the value at the beginning of the interval
     * @param vmax
     *            the value at the end of the interval
     * @return the relative position of the value within the interval
     */
    private static final double interpolate(double value, double vmin, double vmax)
    {
        return (value - vmin) / (vmax - vmin);
    }

    /**
     * Representation of a 2-dimensional point locates on an edge between two
     * grid vertices. The EdgeVertex is identified by the coordinates of the
     * reference vertex of the edge, and by a boolean flag indicating whether
     * the supporting edge is horizontal or vertical.
     * 
     * @author dlegland
     */
    static class EdgeVertex implements Comparable<EdgeVertex>
    {
        /**
         * Coordinates of the upper-left corner of the grid tile containing this
         * vertex (largest int values lower than or equal to vertex
         * coordinates).
         */
        int ix;
        int iy;

        /**
         * Indicates whether the vertex lies on a horizontal line (vertex codes
         * 0 and 3), and 1 if vertex lies on vertical line (vertex codes 1 and
         * 2).
         */
        boolean horiz;

        Point2D position;

        public EdgeVertex(int x, int y, boolean horiz)
        {
            this.ix = x;
            this.iy = y;
            this.horiz = horiz;
        }

        public Point2D getPosition()
        {
            return position;
        }

        @Override
        public int compareTo(EdgeVertex that)
        {
            if (this.iy != that.iy) return this.iy - that.iy;
            if (this.ix != that.ix) return this.ix - that.ix;
            if (this.horiz == that.horiz) return 0;
            return this.horiz ? +1 : -1;
        }

        // ===================================================================
        // Update display
        
        @Override
        public String toString()
        {
            return "EdgeVertex(" + this.ix + ", " + this.iy + ", " + this.horiz + ")";
        }
        
        @Override
        public int hashCode()
        {
            // uses values given by J. Bloch.
            int res = 23;
            res = res * 37 + this.ix;
            res = res * 37 + this.iy;
            res = res * 37 + (this.horiz ? 1 : 0);
            return res;
        }

        @Override
        public boolean equals(Object that)
        {
            if (that instanceof EdgeVertex)
            {
                EdgeVertex ip = (EdgeVertex) that;
                return this.ix == ip.ix && this.iy == ip.iy && this.horiz == ip.horiz;
            }
            return false;
        }
    }
    
}
