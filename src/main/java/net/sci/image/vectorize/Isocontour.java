/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.graph.SimpleGraph2D;

/**
 * Computes the set of curves (polylines) corresponding to isocontour of a
 * scalar 2D image, using marching squares algorithm.
 * 
 * @author dlegland
 *
 */
public class Isocontour extends AlgoStub
{
    // =============================================================
    // class variables

    /**
     * The list of vertex indices for each configuration.
     */
    private int[][] configVertexCodes = new int[][]{
            {}, 
            {1, 0},
            {0, 2},
            {1, 2},
            {3, 1},
            {3, 0},
            {0, 1, 3, 2},
            {3, 2},
            {2, 3},
            {1, 3, 2, 0},
            {0, 3},
            {1, 3},
            {2, 1},
            {2, 0},
            {0, 1},
            {},
    };
    
    /**
     * The threshold value
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
		// create intermediate data structure 
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<int[]> adjacencies = new ArrayList<int[]>();
		
		// size of array
		int size0 = array.size(0);
		int size1 = array.size(1);
		
		// iterate over image pixels
		for (int y = 0; y < size1 - 1; y++)
		{
			for (int x = 0; x < size0 - 1; x++)
			{
				// get value of current pixel

				// get values of the top-left neighbors
                double v00 = array.getValue(x,     y);
                double v01 = array.getValue(x + 1, y);
                double v10 = array.getValue(x,     y + 1);
                double v11 = array.getValue(x + 1, y + 1);
                
                // boolean flags indicating whether pixels are below or above threshold value
                boolean b00 = v00 >= value; 
                boolean b01 = v01 >= value; 
                boolean b10 = v10 >= value; 
                boolean b11 = v11 >= value;
               
                // compute the configuration code
                int confCode = 0;
                confCode += b00 ? 1 : 0;
                confCode += b01 ? 2 : 0;
                confCode += b10 ? 4 : 0;
                confCode += b11 ? 8 : 0;
                
                // get the code (between 0 and 3) corresponding to vertex position within configuration
                int[] vertexCodes = configVertexCodes[confCode];
                
                // iterate over pairs of vertex indices to create edges
                for (int i = 0; i < vertexCodes.length; i += 2)
                {
                    // find vertex codes (top, left, right or bottom) for current configuration
                    int vertexCode1 = vertexCodes[i];
                    int vertexCode2 = vertexCodes[i + 1];
                    
                    // get vertex indices, creating them if necessary
                    int indV1 = findVertexIndex(vertices, x, y, vertexCode1);
                    int indV2 = findVertexIndex(vertices, x, y, vertexCode2);
                    
                    // update position of vertices if necessary
                    Vertex v1 = vertices.get(indV1);
                    if (v1.position == null)
                    {
                        computeVertexPosition(v1, vertexCode1, v00, v01, v10, v11);
                    }
                    Vertex v2 = vertices.get(indV2);
                    if (v2.position == null)
                    {
                        computeVertexPosition(v2, vertexCode2, v00, v01, v10, v11);
                    }
                    
                    adjacencies.add(new int[]{indV1, indV2});
                }
			}
		}
		

		// create the graph
		SimpleGraph2D graph = new SimpleGraph2D();
		for (Vertex v : vertices)
		{
			graph.addVertex(v.getPosition());
		}
		for (int[] adj : adjacencies)
		{
			graph.addEdge(adj[0], adj[1]);
		}
		
		return graph;
	}
	
	private int findVertexIndex(ArrayList<Vertex> vertices, int x, int y, int code)
	{
	    Vertex vertex = createVertex(x, y, code);
	    int index = vertices.indexOf(vertex);
	    if (index < 0)
	    {
	        index = vertices.size();
	        vertices.add(vertex);
	    }
	    return index;
	}
	
	private Vertex createVertex(int x, int y, int code)
	{
	    switch (code)
	    {
        case 0: return new Vertex(x, y, 0);
        case 1: return new Vertex(x, y, 1);
        case 2: return new Vertex(x + 1, y, 1);
        case 3: return new Vertex(x, y + 1, 0);
        default:
            throw new IllegalArgumentException("Code should be comprised between 0 and 3");
	    }
	}

	private void computeVertexPosition(Vertex vertex, int vertexCode, double v00, double v01, double v10, double v11)
	{
        switch(vertexCode)
        {
        case 0:
            vertex.position = new Point2D(vertex.ix + interpolate(value, v00, v01), vertex.iy);
            break;
        case 1:
            vertex.position = new Point2D(vertex.ix, vertex.iy + interpolate(value, v00, v10));
            break;
        case 2:
            vertex.position = new Point2D(vertex.ix, vertex.iy + interpolate(value, v01, v11));
            break;
        case 3:
            vertex.position = new Point2D(vertex.ix + interpolate(value, v10, v11), vertex.iy);
            break;
        }
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
     * Representation of a 2-dimensional point with integer coordinates, and a
     * flag for indicating the position on horizontal or vertical lines.
     * 
     * @author dlegland
     */
	static class Vertex implements Comparable<Vertex>
	{
	    /**
         * Coordinates of the upper-left corner of the grid tile containing this
         * vertex
         */
		int ix;
		int iy;
		
		/**
         * Equals 0 if vertex lies on a horizontal line (vertex codes 0 and 3),
         * and 1 if vertex lies on vertical line (vertex codes 1 and 2).
         */
		int code;
		
		Point2D position;
		
		public Vertex(int x, int y, int code)
		{
			this.ix = x;
			this.iy = y;
			this.code = code;
		}
		
		public Point2D getPosition()
		{
		    return position;
		}
        
		@Override
		public int compareTo(Vertex that)
		{
            if (this.iy != that.iy)
                return this.iy - that.iy;
			if (this.ix != that.ix)
				return this.ix - that.ix;
			return this.code - that.code;
		}

		@Override
		public int hashCode()
		{
			// uses values given by J. Bloch.
			int res = 23;
			res =  res * 37 + this.ix;
            res =  res * 37 + this.iy;
            res =  res * 37 + this.code;
			return res;
		}
		
		@Override
		public boolean equals(Object that)
		{
			if (that instanceof Vertex)
			{
				Vertex ip = (Vertex) that;
				return this.ix == ip.ix && this.iy == ip.iy && this.code == ip.code;
			}
			return false;
		}
	}
}
