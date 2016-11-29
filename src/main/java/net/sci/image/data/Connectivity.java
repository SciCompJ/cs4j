package net.sci.image.data;

import java.util.Collection;

public interface Connectivity
{
	/**
	 * Returns the set of neighbors associated to a given position
	 * @param x the position of the pixel/voxel
	 * @return the list of neighbors of specified pixel/voxel
	 */
	public Collection<int[]> getNeighbors(int[] pos);

	/**
	 * @return the list of offsets computed relative to the center pixel.
	 */
	public Collection<int[]> getOffsets();
}
