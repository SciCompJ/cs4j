/**
 * 
 */
package net.sci.geom.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class AdjListDirectedGraph2DTest
{

    /**
     * Test method for {@link net.sci.geom.graph.AdjListDirectedGraph2D#vertexCount()}.
     */
    @Test
    public final void testVertexCount()
    {
        AdjListDirectedGraph2D graph = createDemoGraph();
        
        assertEquals(4, graph.vertexCount());
    }

    /**
     * Test method for {@link net.sci.geom.graph.AdjListDirectedGraph2D#edgeCount()}.
     */
    @Test
    public final void testEdgeCount()
    {
        AdjListDirectedGraph2D graph = createDemoGraph();
        
        assertEquals(4, graph.edgeCount());
    }

//    /**
//     * Test method for {@link net.sci.geom.graph.AdjListDirectedGraph2D#sourceVertex(net.sci.geom.graph.Graph2D.Edge)}.
//     */
//    @Test
//    public final void testSourceVertex()
//    {
//        AdjListDirectedGraph2D graph = createDemoGraph();
//        
//        
//        assertEquals(4, graph.vertexCount());
//    }
//
//    /**
//     * Test method for {@link net.sci.geom.graph.AdjListDirectedGraph2D#targetVertex(net.sci.geom.graph.Graph2D.Edge)}.
//     */
//    @Test
//    public final void testTargetVertex()
//    {
//        fail("Not yet implemented"); // TODO
//    }

    /**
     * Create a simple directed graph such that:
     * <ul>
     * <li> v1 has 1 in edges (v2) and 2 out edges (v2 and v3)</li>
     * <li> v2 has 2 in edges (v1 and v4) and 1 out edge (v1)</li>
     * <li> v3 has 1 in edges (v1) and 0 out edges</li>
     * <li> v4 has 0 in edges and 1 out edge (v2)</li>
     * </ul>>
     * @return
     */
    private AdjListDirectedGraph2D createDemoGraph()
    {
        AdjListDirectedGraph2D graph = new AdjListDirectedGraph2D();
        
        DirectedGraph2D.Vertex v1 = graph.addVertex(new Point2D(10, 10));
        DirectedGraph2D.Vertex v2 = graph.addVertex(new Point2D(20, 10));
        DirectedGraph2D.Vertex v3 = graph.addVertex(new Point2D(20, 20));
        DirectedGraph2D.Vertex v4 = graph.addVertex(new Point2D(10, 20));
        
        graph.addEdge(v1, v2);
        graph.addEdge(v2, v1);
        graph.addEdge(v1, v3);
        graph.addEdge(v4, v2);
        
        return graph;
    }
}
