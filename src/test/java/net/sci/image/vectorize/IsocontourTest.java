/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.MultiCurve2D;
import net.sci.geom.geom2d.polygon.LineString2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.graph.AdjListDirectedGraph2D;
import net.sci.geom.graph.DirectedGraph2D;
import net.sci.geom.graph.Graph2D;

/**
 * @author dlegland
 *
 */
public class IsocontourTest
{
    /**
     * Test method for {@link net.sci.image.vectorize.Isocontour#convertContourGraphToPolylines(net.sci.geom.graph.AdjListGraph2D)}.
     */
    @Test
    public final void testConvertContourGraphToPolylines_openPolyline()
    {
        AdjListDirectedGraph2D graph = new AdjListDirectedGraph2D();
        Graph2D.Vertex v1 = graph.addVertex(new Point2D(10, 10));
        Graph2D.Vertex v2 = graph.addVertex(new Point2D(20, 10));
        Graph2D.Vertex v3 = graph.addVertex(new Point2D(20, 20));
        graph.addEdge(v1, v2);
        graph.addEdge(v2, v3);
        
        MultiCurve2D curveSet = Isocontour.convertContourGraphToPolylines(graph);
        
        assertEquals(1, curveSet.curves().size());
        Curve2D curve = curveSet.curves().iterator().next();
        assertTrue(curve instanceof LineString2D);
        assertEquals(3, ((LineString2D) curve).vertexCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.Isocontour#convertContourGraphToPolylines(net.sci.geom.graph.AdjListGraph2D)}.
     */
    @Test
    public final void testConvertContourGraphToPolylines_closedPolyline()
    {
        AdjListDirectedGraph2D graph = new AdjListDirectedGraph2D();
        DirectedGraph2D.Vertex v1 = graph.addVertex(new Point2D(10, 10));
        DirectedGraph2D.Vertex v2 = graph.addVertex(new Point2D(20, 10));
        DirectedGraph2D.Vertex v3 = graph.addVertex(new Point2D(20, 10));
        DirectedGraph2D.Vertex v4 = graph.addVertex(new Point2D(20, 20));
        graph.addEdge(v1, v2);
        graph.addEdge(v3, v1);
        graph.addEdge(v2, v4);
        graph.addEdge(v4, v3);
        
        MultiCurve2D curveSet = Isocontour.convertContourGraphToPolylines(graph);
        
        assertEquals(1, curveSet.curves().size());
        Curve2D curve = curveSet.curves().iterator().next();
        assertTrue(curve instanceof LinearRing2D);
        assertEquals(4, ((LinearRing2D) curve).vertexCount());
    }

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#processScalar2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcessScalar2d_4x4()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 4);
        array.setInt(1, 1, 10);
        array.setInt(2, 1, 10);
        array.setInt(1, 2, 10);
        array.setInt(2, 2, 10);

        Isocontour algo = new Isocontour(5.0);

        Geometry2D geom = algo.computeGraph(array);

        assertFalse(geom == null);
    }

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#processScalar2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcessScalar2d_5x5()
    {
        UInt8Array2D array = UInt8Array2D.create(5, 5);
        array.fillInts((x, y) -> (x > 0 && x < 4 && y > 0 && y < 4) ? 10 : 0);
        
        Isocontour algo = new Isocontour(4.0);
        Geometry2D geom = algo.processScalar2d(array);

        assertFalse(geom == null);
    }

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#processScalar2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcessScalar2d_diskR2()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);
        array.fillValues((x, y) -> Math.hypot((x - 5.0), (y - 5.0)));
        
        Isocontour algo = new Isocontour(2.0);
        Geometry2D geom = algo.processScalar2d(array);

        assertFalse(geom == null);
    }

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#computeGraph(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testComputeGraph_simpleSquare()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 4);
        array.setInt(1, 1, 10);
        array.setInt(2, 1, 10);
        array.setInt(1, 2, 10);
        array.setInt(2, 2, 10);

        Isocontour algo = new Isocontour(5.0);

        Graph2D graph = algo.computeGraph(array);

        assertEquals(8, graph.vertexCount());
        assertEquals(8, graph.edgeCount());
    }

}
