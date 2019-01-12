/**
 * 
 */
package net.sci.geom.geom2d.graph;

import java.util.Iterator;

/**
 * @author dlegland
 *
 */
public interface Graph <V extends Graph.Vertex, E extends Graph.Edge> 
{
    public Iterable<E> adjacentEdges(V vertex);

    public Iterable<V> neighborVertices(V vertex);
    
    
    public int vertexNumber();
    
    public default Iterable<?> vertices()
    {
        return new Iterable<V>()
        {
            @Override
            public Iterator<V> iterator()
            {
                return vertexIterator();
            }
        };
    }
    
    public Iterator<V> vertexIterator();
    
    
    
    public int edgeNumber();
    
    public Iterable<?> edges();
    
    public interface Vertex
    {
        
    }

    public interface Edge
    {
        public Vertex source();
        public Vertex target();
    }
}
