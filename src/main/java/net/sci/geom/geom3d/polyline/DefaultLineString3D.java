/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LineString3D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * 
 * @author dlegland
 * @see LinearRing2D
 */
public class DefaultLineString3D implements LineString3D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point3D> vertices;
    
    
    // ===================================================================
    // Constructors

    public DefaultLineString3D() 
    {
        this.vertices = new ArrayList<Point3D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLineString3D(int nVertices)
    {
        this.vertices = new ArrayList<Point3D>(nVertices);
    }
    
    public DefaultLineString3D(Point3D... vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.length);
        for (Point3D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLineString3D(Collection<? extends Point3D> vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public DefaultLineString3D(double[] xcoords, double[] ycoords, double[] zcoords)
    {
        this.vertices = new ArrayList<Point3D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point3D(xcoords[i], ycoords[i], zcoords[i]));
        }
    }
    

    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount()
    {
        return vertices.size();
    }

    @Override
    public Point3D vertexPosition(int index)
    {
        return this.vertices.get(index);
    }

    public void addVertex(Point3D vertexPosition)
    {
        this.vertices.add(vertexPosition);
    }
    
    /**
     * Returns the inner collection of vertices.
     */
    public ArrayList<Point3D> vertexPositions()
    {
        return vertices;
    }

    public Iterator<LineSegment3D> edgeIterator()
    {
    	return new EdgeIterator();
    }
    

    // ===================================================================
    // Methods implementing the Polyline3D interface
    

    // ===================================================================
    // Edge iterator implementation
    
    class EdgeIterator implements Iterator<LineSegment3D>
    {
    	/**
    	 * Index of the first vertex of current edge
    	 */
    	int index = -1;

    	@Override
		public boolean hasNext()
		{
			return index < vertices.size() - 2;
		}

		@Override
		public LineSegment3D next()
		{
			index++;
			int index2 = (index + 1) % vertices.size();
			return new LineSegment3D(vertices.get(index), vertices.get(index2));
		}
    }
}
