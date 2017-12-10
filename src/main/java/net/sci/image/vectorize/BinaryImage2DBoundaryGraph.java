/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;

import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.graph.SimpleGraph2D;

/**
 * @author dlegland
 *
 */
public class BinaryImage2DBoundaryGraph
{

	/**
	 * 
	 */
	public BinaryImage2DBoundaryGraph()
	{
	}

	public SimpleGraph2D process(BinaryArray2D array)
	{
		ArrayList<IntPoint2D> corners = new ArrayList<IntPoint2D>();
		ArrayList<int[]> adjacencies = new ArrayList<int[]>();
		
		int size0 = array.getSize(0);
		int size1 = array.getSize(1);
		
		for (int y = 0; y < size1; y++)
		{
			for (int x = 0; x < size0; x++)
			{
				// get values of current pixels, and neighbors
				boolean pixel = array.getBoolean(x, y);
				
				// detect transitions with upper pixel
				boolean top = (y > 0) ? array.getBoolean(x, y-1) : false;
				if (pixel != top)
				{
					int indV1 = findVertexIndex(corners, new IntPoint2D(x, y));
					int indV2 = findVertexIndex(corners, new IntPoint2D(x + 1, y));
					adjacencies.add(new int[]{indV1, indV2});
				}

				boolean left = (x > 0) ? array.getBoolean(x-1, y) : false;
				if (pixel != left)
				{
					int indV1 = findVertexIndex(corners, new IntPoint2D(x, y));
					int indV2 = findVertexIndex(corners, new IntPoint2D(x, y + 1));
					adjacencies.add(new int[]{indV1, indV2});
				}
			}
		}
		
		SimpleGraph2D graph = new SimpleGraph2D();
		for (IntPoint2D v : corners)
		{
			graph.addVertex(new Point2D(v.x - 0.5, v.y - 0.5));
		}
		for (int[] adj : adjacencies)
		{
			graph.addEdge(adj[0], adj[1]);
		}
		
		return graph;
	}
	
	private int findVertexIndex(ArrayList<IntPoint2D> vertices, IntPoint2D point)
	{
		int index = vertices.indexOf(point);
		if (index < 0)
		{
			index = vertices.size();
			vertices.add(point);
		}
		return index;
	}
	
	static class IntPoint2D implements Comparable<IntPoint2D>
	{
		int x;
		int y;
		
		public IntPoint2D(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int compareTo(IntPoint2D that)
		{
			if (this.x != that.x)
				return this.x - that.x;
			return this.y - that.y;
		}
		
		public boolean equals(Object that)
		{
			if (that instanceof IntPoint2D)
			{
				IntPoint2D ip = (IntPoint2D) that;
				return this.x == ip.x && this.y == ip.y;
			}
			return false;
		}
		
		//TODO: implements hashcode
	}
}
