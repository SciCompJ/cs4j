/**
 * 
 */
package net.sci.geom.geom2d.graph;

import java.util.Iterator;

import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public interface Graph2D <V extends Graph2D.Vertex, E extends Graph2D.Edge> extends Graph<V, E>, Geometry2D
{
    public default Iterable<Point2D> vertexPositions()
    {
        return new Iterable<Point2D>()
        {
            @Override
            public Iterator<Point2D> iterator()
            {
                return vertexPositionIterator();
            }
            
        };
    }

    public default Iterator<Point2D> vertexPositionIterator()
    {
        return new VertexPositionIterator<V>(this);
    }
    
    public class VertexPositionIterator<V extends Graph2D.Vertex> implements Iterator<Point2D>
    {
        Iterator<V> iter = null;

        public VertexPositionIterator(Graph2D<V,?> graph)
        {
            iter = graph.vertexIterator();
        }
        
        @Override
        public boolean hasNext()
        {
            return iter.hasNext();
        }

        @Override
        public Point2D next()
        {
            return iter.next().position();
        }
        
    }
    
    public interface Vertex extends Graph.Vertex
    {
        public Point2D position();
        public void setPosition(Point2D pos);
    }

    public interface Edge extends Graph.Edge
    {
        public Curve2D curve();
        
        @Override
        public Vertex source();

        @Override
        public Vertex target();
    }
}
