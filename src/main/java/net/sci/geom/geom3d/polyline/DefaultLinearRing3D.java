/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LinearRing3D is a polyline whose last point is connected to the first one.
 * </p>
 * @author dlegland
 */
public class DefaultLinearRing3D implements LinearRing3D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point3D> vertices;
    
    // ===================================================================
    // Constructors

    public DefaultLinearRing3D() 
    {
        this.vertices = new ArrayList<Point3D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public DefaultLinearRing3D(int nVertices)
    {
        this.vertices = new ArrayList<Point3D>(nVertices);
    }
    
    public DefaultLinearRing3D(Point3D... vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.length);
        for (Point3D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public DefaultLinearRing3D(Collection<? extends Point3D> vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public DefaultLinearRing3D(double[] xcoords, double[] ycoords, double[] zcoords)
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
    public void addVertex(Point3D pos)
    {
        this.vertices.add(pos);
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
			return index < vertices.size() - 1;
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
