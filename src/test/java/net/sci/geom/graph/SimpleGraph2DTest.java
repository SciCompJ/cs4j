/**
 * 
 */
package net.sci.geom.graph;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.graph.SimpleGraph2D;

/**
 * @author dlegland
 *
 */
public class SimpleGraph2DTest
{
    
    /**
     * Test method for {@link net.sci.geom.graph.SimpleGraph2D#SimpleGraph2D()}.
     */
    @Test
    public final void testSimpleGraph2D()
    {
        Point2D p1 = new Point2D(10, 10);
        Point2D p2 = new Point2D(20, 10);
        Point2D p3 = new Point2D(20, 20);
        Point2D p4 = new Point2D(10, 20);
        Point2D p5 = new Point2D(17, 15);
        
        SimpleGraph2D graph = new SimpleGraph2D();
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(p3);
        graph.addVertex(p4);
        graph.addVertex(p5);
        
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        graph.addEdge(1, 4);
        graph.addEdge(2, 4);
        
        assertEquals(5, graph.vertexNumber());
        assertEquals(6, graph.edgeNumber());
    }
    
}
