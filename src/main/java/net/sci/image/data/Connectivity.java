package net.sci.image.data;

import java.util.Collection;

import net.sci.array.Dimensional;

public interface Connectivity extends Dimensional
{
    /**
     * Returns the set of neighbors associated to a given position
     * 
     * @param pos
     *            the position of the pixel/voxel
     * @return the list of neighbors of specified pixel/voxel
     */
    public Collection<int[]> getNeighbors(int[] pos);
    
    /**
     * @return the list of offsets computed relative to the center pixel.
     */
    public Collection<int[]> getOffsets();
}
