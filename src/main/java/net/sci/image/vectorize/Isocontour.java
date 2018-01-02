/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;

import net.sci.algo.AlgoStub;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.type.Scalar;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.graph.SimpleGraph2D;

/**
 * Computes the set of curves (polylines) corresponding to isocontour of a scalar 2D image.
 *  
 * @author dlegland
 *
 */
public class Isocontour extends AlgoStub
{
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
    
	/**
	 * Default empty constructor.
	 */
	public Isocontour(double value)
	{
	    this.value = value;
	}

	public Geometry2D processScalar2d(ScalarArray2D<? extends Scalar> array)
	{
		// create intermediate data structure 
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<int[]> adjacencies = new ArrayList<int[]>();
		
		// size of array
		int size0 = array.getSize(0);
		int size1 = array.getSize(1);
		
		// iterate over image pixels
		// TODO: manage array borders
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
                    Vertex v1 = createVertex(x, y, vertexCodes[i]); 
                    Vertex v2 = createVertex(x, y, vertexCodes[i + 1]);
                    
                    int indV1 = findVertexIndex(vertices, v1);
                    int indV2 = findVertexIndex(vertices, v2);
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
	
	private int findVertexIndex(ArrayList<Vertex> vertices, Vertex point)
	{
		int index = vertices.indexOf(point);
		if (index < 0)
		{
			index = vertices.size();
			vertices.add(point);
		}
		return index;
	}
	
	/**
	 * Representation of a 2-dimensional point with integer coordinates.
	 * 
	 * @author dlegland
	 */
	static class Vertex implements Comparable<Vertex>
	{
		int ix;
		int iy;
		
		/**
         * Equals 0 if vertex lies on a horizontal line (vertex codes 0 and 3),
         * and 1 if vertex lies on vertical line (vertex codes 1 and 2).
         */
		int pos;
		
		public Vertex(int x, int y, int pos)
		{
			this.ix = x;
			this.iy = y;
			this.pos = pos;
		}
		
		public Point2D getPosition()
		{
		    if (pos == 0)
                return new Point2D(this.ix + 0.5, this.iy);
		    else
		        return new Point2D(this.ix, this.iy + 0.5);
		}
		
		@Override
		public int compareTo(Vertex that)
		{
			if (this.ix != that.ix)
				return this.ix - that.ix;
			if (this.iy != that.iy)
	            return this.iy - that.iy;
			return this.pos - that.pos;
		}

		@Override
		public int hashCode()
		{
			// uses values given by J. Bloch.
			int res = 23;
			res =  res * 37 + this.ix;
            res =  res * 37 + this.iy;
            res =  res * 37 + this.pos;
			return res;
		}
		
		@Override
		public boolean equals(Object that)
		{
			if (that instanceof Vertex)
			{
				Vertex ip = (Vertex) that;
				return this.ix == ip.ix && this.iy == ip.iy && this.pos == ip.pos;
			}
			return false;
		}
	}
}
