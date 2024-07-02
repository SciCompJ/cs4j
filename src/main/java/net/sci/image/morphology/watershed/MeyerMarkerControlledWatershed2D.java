/**
 * 
 */
package net.sci.image.morphology.watershed;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.Connectivity2D;
import net.sci.image.binary.BinaryImages;

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
public class MeyerMarkerControlledWatershed2D extends AlgoStub
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
     * Connectivity of regions (expected 4 or 8). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity2D connectivity = Connectivity2D.C4;
    
    
    // ==============================================================
    // Constructors
    
    /**
     * Empty constructor, initialized with default C4 connectivity.
     */
    public MeyerMarkerControlledWatershed2D()
    {
    }
    
    /**
     * Constructor that allows specification of connectivity.
     * 
     * @param conn
     *            the connectivity to use.
     */
    public MeyerMarkerControlledWatershed2D(Connectivity2D conn)
    {
        
    }
    
    
    // ==============================================================
    // Methods
    
    public void setConnectivity(Connectivity2D conn)
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
    public IntArray2D<?> process(ScalarArray2D<?> relief, BinaryArray2D markers)
    {
        // compute labels of the minima
        this.fireStatusChanged(this, "Connected component labeling of markers");
        IntArray2D<?> labelMap = BinaryImages.componentsLabeling(markers, connectivity, 32);
        
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
    public void processInPlace(ScalarArray2D<?> relief, IntArray2D<?> labelMap)
    {
        // retrieve array size
        int sizeX = relief.size(0);
        int sizeY = relief.size(1);
        
        // create priority queue of pixels
        OrderedQueue<Double, int[]> queue = new OrderedQueue<Double, int[]>();

        // iterate over elements of output array:
        // - replace zero values by INIT values
        // - add elements around markers into the queue, and replace their value by INQUEUE
        this.fireStatusChanged(this, "Initialize Watershed Queue");
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelMap.getInt(x,y);
                if (label == 0)
                {
                    // replace value by INIT
                    labelMap.setInt(x, y, INIT);
                }
                else if (label > 0)
                {                    
                    // if within a marker, iterate over neighbors to add them to the queue
                    for (int[] pos2 : connectivity.neighbors(x, y))
                    {
                        int x2 = pos2[0];
                        int y2 = pos2[1];
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        
                        int label2 = labelMap.getInt(x2, y2); 
                        if (label2 == 0 || label2 == INIT)
                        {
                            queue.add(relief.getValue(x2, y2), new int[] {x2, y2});
                            labelMap.setInt(x2, y2, INQUEUE);
                        }
                    }
                }
            }
        }
        
        // initialize an array to store position of neighbors
        int[][] neighbors = new int[connectivity.offsets().size()][];
        int neighborCount = 0; 
                
        // Process pixels and eventually add neighbors until the queue is empty 
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
            
            // Iterate over neighbors of current pixel
            for (int[] pos2 : connectivity.neighbors(x, y))
            {
                // Look in neighborhood for labeled pixels with
                // smaller or equal original value
                int x2 = pos2[0];
                int y2 = pos2[1];
                
                // check neighbor is inside image
                if (x2 < 0 || x2 >= sizeX) continue;
                if (y2 < 0 || y2 >= sizeY) continue;
                
                int label = labelMap.getInt(x2, y2); 
                if (label == INIT)
                {
                    neighbors[neighborCount++] = new int[] {x2, y2}; 
                }
                else if (label != INQUEUE && label != WSHED)
                {
                    // if another label is found, then the current pixel will
                    // have label WSHED, and we can switch to next pixel
                    if (label != lastLabel && lastLabel != -1)
                    {
                        labelMap.setInt(x, y, WSHED);
                        continue queue;
                    }
                    
                    // keep reference to the neighbor label to compare with that
                    // of other neighbors
                    lastLabel = label;
                }
            }
            
            // If we have not escaped the loop over neighbors, then all
            // neighbors of the current pixel all have the same
            // label, and the current pixel is associated to this label
            labelMap.setInt(x, y, lastLabel);
            
            // once label of current pixel is known, we can enqueue the
            // positions of unlabeled neighbor
            for (int i = 0; i < neighborCount; i++)
            {   
                int[] pos2 = neighbors[i];
                labelMap.setInt(pos2[0], pos2[1], INQUEUE);
                double value = relief.getValue(pos2[0], pos2[1]);
                queue.add(value, pos2);
            }
        }

        // post-processing:
        // assign unlabeled pixels the WSHED flag
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
