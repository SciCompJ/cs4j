/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.connectivity.Connectivity3D;

/**
 * Watershed algorithm with markers and dams following Meyer implementation.
 * 
 * The watershed is propagated from (outer) boundary pixels of the specified
 * markers. Pixels to update are stored in an <code>OrderedQueue</code> instance
 * 
 * References:
 * <ul>
 * <li>Meyer, F. Un algorithme optimal de ligne de partage des eaux. In:
 * Proceedings of the 8th congress AFCET, Lyon, France, 25-28 November 1991;
 * Vol. 2, pp. 847-859.</li>
 * <li>Kornilov, A., Safonov, I., Yakimchuk, I. A review of watershed
 * implementations for segmentation of volumetric images. Journal of Imaging,
 * 2022, Vol. 8, https:/doi.org/10.3390/jimaging8050127</li>
 * </ul>
 */
public class MeyerMarkerControlledWatershed3D extends AlgoStub
{
    // ==============================================================
    // Constants
    
    /** Value of the pixels belonging to the watershed */
    static final int WSHED = 0;
    
    /** Initial value of the labeled pixels */
    static final int INIT = -1;
    
    /** Value assigned to the pixels stored into the queue */
    static final int INQUEUE = -3;
    
    
    // ==============================================================
    // Class variables
    
    /** 
     * Connectivity of regions (expected 6 or 26). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity3D connectivity = Connectivity3D.C6;
    
    
    // ==============================================================
    // Constructors
    
    /**
     * Empty constructor, initialized with default C6 connectivity.
     */
    public MeyerMarkerControlledWatershed3D()
    {
    }
    
    /**
     * Constructor that allows specification of connectivity.
     * 
     * @param conn
     *            the connectivity to use.
     */
    public MeyerMarkerControlledWatershed3D(Connectivity3D conn)
    {
        
    }
    
    
    // ==============================================================
    // Methods
    
    public void setConnectivity(Connectivity3D conn)
    {
        this.connectivity = conn;
    }
    
    /**
     * Computes the watershed of the specified relief image, starting from the
     * specified markers. The result is returned in a new instance of Int array,
     * and the marker image is not modified.
     * 
     * @param relief
     *            the array containing intensity map, considered as a
     *            topographic relief
     * @param markers
     *            the binary image of markers. Each connected component of the
     *            marker array will correspond to a basin of the watershed
     * @return the label map corresponding to the resulting basins, using 0 as
     *         label for the background
     */
    public IntArray3D<?> process(ScalarArray3D<?> relief, BinaryArray3D markers)
    {
        // compute labels of the minima
        this.fireStatusChanged(this, "Connected component labeling of markers");
        IntArray3D<?> labelMap = BinaryImages.componentsLabeling(markers, connectivity, 32);
        
        processInPlace(relief, labelMap);
        return labelMap;
    }


    /**
     * Computes the watershed of the specified relief image, starting from the
     * specified markers and updating the specified marker image during the
     * process, and contains the result basin labels.
     * 
     * @param relief
     *            the array containing intensity map, considered as a
     *            topographic relief
     * @param labelMap
     *            the label map containing marker labels when the method is
     *            called, and containing watershed basin label at the end of the
     *            process
     */
    public void processInPlace(ScalarArray3D<?> relief, IntArray3D<?> labelMap)
    {
        // retrieve array size
        int sizeX = relief.size(0);
        int sizeY = relief.size(1);
        int sizeZ = relief.size(2);
        
        // create priority queue of pixels
        OrderedQueue<Double, int[]> queue = new OrderedQueue<Double, int[]>();

        // iterate over elements of output array:
        // - replace zero values by INIT values
        // - add elements around markers into the queue, and replace their value by INQUEUE
        this.fireStatusChanged(this, "Initialize Watershed Queue");
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelMap.getInt(x, y, z);
                    if (label == 0)
                    {
                        // replace value by INIT
                        labelMap.setInt(x, y, z, INIT);
                    }
                    else if (label > 0)
                    {                    
                        // if within a marker, iterate over neighbors to add them to the queue
                        for (int[] pos2 : connectivity.neighbors(x, y, z))
                        {
                            int x2 = pos2[0];
                            int y2 = pos2[1];
                            int z2 = pos2[2];
                            if (x2 < 0 || x2 >= sizeX) continue;
                            if (y2 < 0 || y2 >= sizeY) continue;
                            if (z2 < 0 || z2 >= sizeZ) continue;

                            int label2 = labelMap.getInt(x2, y2, z2); 
                            if (label2 == 0 || label2 == INIT)
                            {
                                queue.add(relief.getValue(x2, y2, z2), new int[] {x2, y2, z2});
                                labelMap.setInt(x2, y2, z2, INQUEUE);
                            }
                        }
                    }
                }
            }
        }

        // initialize an array to store position of neighbors
        int[][] neighbors = new int[connectivity.offsets().size()][];
        int neighborCount = 0; 
                
        // Process voxels and eventually add neighbors until the queue is empty 
        this.fireStatusChanged(this, "Recursively process Queue");
        queue:
        while (!queue.isEmpty())
        {
            if (Thread.currentThread().isInterrupted())
            {
                return;
            }
            
            // reset state of neighborhood info
            neighborCount = 0;
            int lastLabel = -1;
            
            // retrieve the next position from the queue
            int[] pos = queue.remove();
            int x = pos[0];
            int y = pos[1];
            int z = pos[2];
            
            // Iterate over neighbors of current pixel
            for (int[] pos2 : connectivity.neighbors(x, y, z))
            {
                // Look in neighborhood for labeled voxels with
                // smaller or equal original value
                int x2 = pos2[0];
                int y2 = pos2[1];
                int z2 = pos2[2];
                
                // check neighbor is inside image
                if (x2 < 0 || x2 >= sizeX) continue;
                if (y2 < 0 || y2 >= sizeY) continue;
                if (z2 < 0 || z2 >= sizeZ) continue;
                
                int label = labelMap.getInt(x2, y2, z2); 
                if (label == INIT)
                {
                    neighbors[neighborCount++] = new int[] {x2, y2, z2}; 
                }
                else if (label != INQUEUE && label != WSHED)
                {
                    // if another label is found, then the current pixel will
                    // have label WSHED, and we can switch to next pixel
                    if (label != lastLabel && lastLabel != -1)
                    {
                        labelMap.setInt(x, y, z, WSHED);
                        continue queue;
                    }
                    
                    // keep reference to the neighbor label to compare with that
                    // of other neighbors
                    lastLabel = label;
                }
            }
            
            
            // If we have not escaped the loop over neighbors, then all
            // neighbors of the current pixel all have the same
            // label, and the current voxel is associated to this label
            labelMap.setInt(x, y, z, lastLabel);
            
            // once label of current voxel is known, we can enqueue the
            // positions of unlabeled neighbor
            for (int i = 0; i < neighborCount; i++)
            {   
                int[] pos2 = neighbors[i];
                labelMap.setInt(pos2[0], pos2[1], pos2[2], INQUEUE);
                double value = relief.getValue(pos2[0], pos2[1], pos2[2]);
                queue.add(value, pos2);
            }
        }

        // post-processing:
        // assign unlabeled voxels the WSHED flag
        IntArray.Iterator<?> iter = labelMap.iterator(); 
        while (iter.hasNext())
        {
            if (iter.nextInt() == INIT)
            {
                iter.setInt(WSHED);
            }
        }
    }
}
