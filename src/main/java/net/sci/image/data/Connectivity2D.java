/**
 * 
 */
package net.sci.image.data;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines the connectivity for planar images.
 * 
 * Contains static classes for classical C4 and C8 connectivities, and C6
 * connectivity (orthogonal neighbors plus lower-left and upper right neighbors).
 * 
 * @author dlegland
 *
 */
public interface Connectivity2D extends Connectivity
{
	
	/**
	 * Returns a new connectivity object from a connectivity value.
	 * @param conn the connectivity value, either 4 or 8
	 * @return a Connectivity object
	 */
	public static Connectivity2D fromValue(int conn)
	{
		if (conn == 4)
			return C4;
		else if (conn == 8)
			return C8;
		else
			throw new IllegalArgumentException("Connectivity value should be either 4 or 8");
	}

	/**
	 * Planar connectivity that considers the four orthogonal neighbors of a pixel.
	 */
	public static final Connectivity2D C4 = new Connectivity2D()
	{
		@Override
		public Collection<int[]> getNeighbors(int x, int y)
		{
			ArrayList<int[]> array = new ArrayList<int[]>(4);
			array.add(new int[]{x, y-1});
			array.add(new int[]{x-1, y});
			array.add(new int[]{x+1, y});
			array.add(new int[]{x, y+1});
			return array;
		}

		@Override
		public Collection<int[]> getOffsets()
		{
			ArrayList<int[]> array = new ArrayList<int[]>(4);
			array.add(new int[]{ 0, -1});
			array.add(new int[]{-1,  0});
			array.add(new int[]{+1,  0});
			array.add(new int[]{ 0, +1});
			return array;
		}

	};

	/**
	 * Defines the C6_1 connectivity, corresponding to orthogonal neighbors plus
	 * lower-left and upper right neighbors.
	 */
	public static final Connectivity2D C6_1 = new Connectivity2D()
	{
		@Override
		public Collection<int[]> getNeighbors(int x, int y)
		{
			ArrayList<int[]> array = new ArrayList<int[]>(6);
			array.add(new int[]{  x, y-1});
			array.add(new int[]{x+1, y-1});
			array.add(new int[]{x-1,  y});
			array.add(new int[]{x+1,   y});
			array.add(new int[]{x-1, y+1});
			array.add(new int[]{  x, y+1});
			return array;
		}

		@Override
		public Collection<int[]> getOffsets()
		{
			ArrayList<int[]> array = new ArrayList<int[]>(6);
			array.add(new int[]{ 0, -1});
			array.add(new int[]{+1, -1});
			array.add(new int[]{-1,  0});
			array.add(new int[]{+1,  0});
			array.add(new int[]{-1, +1});
			array.add(new int[]{ 0, +1});
			return array;
		}
	};
	
	/**
	 * Planar connectivity that considers the eight neighbors (orthogonal plus
	 * diagonal) of a pixel.
	 */
	public static final Connectivity2D C8 = new Connectivity2D()
	{
		@Override
		public Collection<int[]> getNeighbors(int x, int y)
		{
			ArrayList<int[]> array = new ArrayList<int[]>(8);
			array.add(new int[]{x-1, y-1});
			array.add(new int[]{  x, y-1});
			array.add(new int[]{x+1, y-1});
			array.add(new int[]{x-1,   y});
			array.add(new int[]{x+1,   y});
			array.add(new int[]{x-1, y+1});
			array.add(new int[]{  x, y+1});
			array.add(new int[]{x+1, y+1});
			return array;
		}

		@Override
		public Collection<int[]> getOffsets()
		{
			ArrayList<int[]> array = new ArrayList<int[]>(8);
			array.add(new int[]{-1, -1});
			array.add(new int[]{ 0, -1});
			array.add(new int[]{+1, -1});
			array.add(new int[]{-1,  0});
			array.add(new int[]{+1,  0});
			array.add(new int[]{-1, +1});
			array.add(new int[]{ 0, +1});
			array.add(new int[]{+1, +1});
			return array;
		}
	};

	
	/**
	 * Returns the set of neighbors associated to a given position
	 * @param x the x position of the pixel
	 * @param y the y position of the pixel
	 * @return the list of neighbors of specified pixel
	 */
	public Collection<int[]> getNeighbors(int x, int y);

	public default Collection<int[]> getNeighbors(int[] pos)
	{
		return getNeighbors(pos[0], pos[1]);
	}

	/**
	 * @return the list of offsets computed relative to the center pixel.
	 */
	public Collection<int[]> getOffsets();

}
