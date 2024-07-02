package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.PriorityQueue;

import net.sci.array.Arrays;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Connectivity3D;

/**
 * Tentative implementation of Watershed algorithm for 3D scalar images.
 * 
 * Reference: Fernand Meyer and Serge Beucher. "Morphological segmentation." 
 * Journal of visual communication and image representation 1.1 (1990): 21-46.
 * 
 * @author dlegland
 *
 */
public class MarkerBasedWatershed3D
{
    /** 
     * Connectivity of the regions (expected 6 or 26).
     *  
     * The watershed usually has complementary connectivity. 
     */
    Connectivity3D connectivity = Connectivity3D.C6;
    
    /** Value of the voxels belonging to the watershed */
    static final int WATERSHED = 0;
    
    /** Initial value of the labeled voxels */
    static final int INIT = -1;
    
    /** Value assigned to the voxels stored into the queue */
    static final int INQUEUE = -3;
    
    /** used to setup voxel timestamps. */
    long timeStamp = Long.MIN_VALUE;
    

    public MarkerBasedWatershed3D()
    {
    }

    public MarkerBasedWatershed3D(Connectivity3D connectivity)
    {
        this.connectivity = connectivity;
    }

    
    public IntArray3D<?> process(ScalarArray3D<?> array, IntArray3D<?> markers)
    {
        // extract array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        if (!Arrays.isSameSize(array, markers))
        {
            throw new IllegalArgumentException("Marker and input arrays must have the same size");
        }
        
        // Initialize output array
        IntArray3D<?> labelArray = markers.duplicate();
        labelArray.fillValue(INIT);

        // initial queue of original voxel values and corresponding coordinates
        timeStamp = Long.MIN_VALUE;
        PriorityQueue<Voxel> queue = createQueue(array, markers, labelArray);
        
        // list to store neighbor labels
        ArrayList<Integer> labels = new ArrayList<Integer>();
        ArrayList<Voxel> neighbors = new ArrayList<Voxel>();
        
        // Process pixels and eventually add neighbors until the queue is empty 
        while (!queue.isEmpty())
        {
            if (Thread.currentThread().isInterrupted())
            {
                return null;
            }
            
            // reset state
            labels.clear();
            neighbors.clear();
            
            // get next record
            Voxel voxel = queue.poll();

            // coordinates of current pixel
            int x = voxel.x;
            int y = voxel.y;
            int z = voxel.z;
            
            // Iterate over neighbors of current pixel
            for (int[] pos : connectivity.neighbors(x, y, z))
            {                                       
                // Look in neighborhood for labeled pixels with
                // smaller or equal original value
                int x2 = pos[0];
                int y2 = pos[1];
                int z2 = pos[2];
                
                // check neighbor is inside image
                if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY && z2 >= 0 && z2 < sizeZ)
                {
                    // Unlabeled neighbors go into the queue if they are not there yet
                    int label = labelArray.getInt(x2, y2, z2); 
                    if (label == INIT)
                    {
                        neighbors.add(new Voxel(x2, y2, z2, array.getValue(x2, y2, z2), timeStamp++));
                    }
                    else if (label > 0 && !labels.contains(label))
                    {
                        // store labels of neighbor in a list without repetitions
                        labels.add(label);
                    }
                }
            }
            
            // if the neighbors of the extracted pixel that have already been labeled 
            // all have the same label, then the pixel is labeled with their label
            if (labels.size() == 1)
            {
                labelArray.setInt(x, y, z, labels.get(0));
                
                // now that we know the pixel is labeled, add unlabeled neighbors to list
                for (Voxel neighbor : neighbors)
                {   
                    labelArray.setInt(neighbor.x, neighbor.y, neighbor.z, INQUEUE);
                    queue.add(neighbor);
                }
            }
            else if( labels.size() > 1 )
            {
                // If neighbors have more than two labels, then the current
                // pixel is set to watershed
                labelArray.setInt(x, y, z, WATERSHED);
            }   
        }
        
        // assign unlabeled pixels the WSHED flag
        IntArray.Iterator<?> iter = labelArray.iterator(); 
        while (iter.hasNext())
        {
            if (iter.nextInt() == INIT)
            {
                iter.setInt(WATERSHED);
            }
        }

        return labelArray;
    }
    
    private PriorityQueue<Voxel> createQueue(final ScalarArray3D<?> array,
            final IntArray3D<?> markers, IntArray3D<?> result)
    {
        // get image size
        final int sizeX = array.size(0);
        final int sizeY = array.size(1);
        final int sizeZ = array.size(2);

        // create priority queue of voxels
        PriorityQueue<Voxel> queue = new PriorityQueue<Voxel>();

        // Iterate over image voxels
        timeStamp = Long.MIN_VALUE;
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = markers.getInt(x, y, z);
                    if (label > 0)
                    {
                        // Iterate over neighbors of current voxel
                        for (int[] pos : connectivity.neighbors(x, y, z))
                        {
                            // coordinates of neighbor voxel
                            int x2 = pos[0];
                            int y2 = pos[1];
                            int z2 = pos[2];
                            
                            // add unlabeled neighbors to priority queue
                            if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY && z2 >= 0 && z2 < sizeZ
                                    && markers.getInt(x2, y2, z2) == 0 
                                    && result.getInt(x2, y2, z2) != INQUEUE)
                            {
                                queue.add(new Voxel(x2, y2, z2, array.getValue(x2, y2, z2), timeStamp++));
                                result.setInt(x2, y2, z2, INQUEUE);
                            }
                        }
                        
                        result.setInt(x, y, z, label);
                    }
                }
            }
        }
        
        return queue;
    }
    
    
    /**
     * Stores the coordinates of a voxel together with its value and a unique
     * identifier.
     * 
     * Based on the class "VoxelRecord" from Ignacio Arganda-Carreras, in the
     * MorphoLibJ library.
     */
    private class Voxel implements Comparable<Voxel>
    {
        int x;
        int y;
        int z;
        double value = 0;
        
        /** time stamp for this voxel */
        long time;
        
        /**
         * Create voxel record with from a position and a double value
         * 
         * @param x
         *            the x-coordinate of the voxel position
         * @param y
         *            the y-coordinate of the voxel position
         * @param z
         *            the z-coordinate of the voxel position
         * @param value
         *            pixel intensity value
         * @param time
         *            the timestamp for this voxel
         */
        public Voxel(final int x, final int y, int z, final double value, long time)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.value = value;
            this.time = time;
        }
        
        /**
         * Compare with a pixel based on its value and timestamp
         * 
         * @param other
         *            another record to compare with
         * @return a value smaller than 0 if the v2 voxel value is larger this
         *         record voxel value, a value larger than 0 if it is lower. If
         *         equal, the voxel created before is set as smaller.
         */
        @Override
        public int compareTo(Voxel other)
        {
            int res = Double.compare(value, other.value);
            if (res == 0)
                return time < other.time ? -1 : 1;
            return res;
        }
    }
    
}
