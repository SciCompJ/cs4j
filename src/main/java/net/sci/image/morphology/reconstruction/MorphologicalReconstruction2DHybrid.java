/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.morphology.MorphologicalReconstruction;
import net.sci.image.morphology.MorphologicalReconstruction.Type;

/**
 * <p>
 * Morphological reconstruction for planar arrays, using hybrid algorithm. The
 * algorithms performs forward scan, backward scan that also initialize a queue
 * of positions that need updates, and finally recursively the positions in the
 * queue.
 * </p>
 * 
 * @author David Legland
 * @see MorphologicalReconstruction3DHybrid
 *
 */
public class MorphologicalReconstruction2DHybrid extends AlgoStub implements MorphologicalReconstruction2D
{
    // ==================================================
    // Class variables
    
    /**
     * The type of morphological reconstruction.
     */
    protected Type reconstructionType = Type.BY_DILATION;
    
    /**
     * The sign value associated to reconstruction type.
     * <ul>
     * <li>+1: reconstruction by dilation.</li>
     * <li>-1: reconstruction by erosion.</li>
     * </ul>
     */
    protected int sign = 1;
    
    /**
     * The connectivity of the algorithm, usually either C4 or C8.
     */
    protected Connectivity2D connectivity = Connectivity2D.C4;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Creates a new instance of morphological reconstruction by dilation
     * algorithm, using the default connectivity 4.
     */
    public MorphologicalReconstruction2DHybrid()
    {
    }
    
    /**
     * Creates a new instance of morphological reconstruction algorithm, that
     * specifies the type of reconstruction, and using the default connectivity
     * 4.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     */
    public MorphologicalReconstruction2DHybrid(MorphologicalReconstruction.Type type)
    {
        setReconstructionType(type);
    }
    
    /**
     * Creates a new instance of morphological reconstruction algorithm, that
     * specifies the connectivity to use.
     * 
     * @param connectivity
     *            the 2D connectivity to use (either C4 or C8)
     */
    public MorphologicalReconstruction2DHybrid(Connectivity2D connectivity)
    {
        setConnectivity(connectivity);
    }
    
    /**
     * Creates a new instance of morphological reconstruction algorithm, that
     * specifies the type of reconstruction, and the connectivity to use.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     * @param connectivity
     *            the 2D connectivity to use (either C4 or C8)
     */
    public MorphologicalReconstruction2DHybrid(MorphologicalReconstruction.Type type, Connectivity2D connectivity)
    {
        setReconstructionType(type);
        setConnectivity(connectivity);
    }
    
    
    // ==================================================
    // Accesors and mutators
    
    /**
     * @return the reconstructionType
     */
    public MorphologicalReconstruction.Type getReconstructionType()
    {
        return reconstructionType;
    }
    
    /**
     * @param reconstructionType
     *            the reconstructionType to set
     */
    public void setReconstructionType(MorphologicalReconstruction.Type reconstructionType)
    {
        this.reconstructionType = reconstructionType;
        this.sign = reconstructionType.getSign();
    }
    
    /**
     * @return the connectivity
     */
    public Connectivity2D getConnectivity()
    {
        return connectivity;
    }
    
    /**
     * @param connectivity
     *            the connectivity to set
     */
    public void setConnectivity(Connectivity2D connectivity)
    {
        this.connectivity = connectivity;
    }
    
    
    // ==================================================
    // Methods implementing the MorphologicalReconstruction interface
    
    /**
     * Run the morphological reconstruction algorithm using the specified arrays
     * as argument.
     */
    public ScalarArray2D<?> process(ScalarArray2D<?> marker, ScalarArray2D<?> mask)
    {
        // Check sizes are consistent
        if (!Arrays.isSameSize(marker, mask))
        {
            throw new IllegalArgumentException("Marker and Mask images must have the same size");
        }
        
        // Check connectivity has a correct value
        if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
        {
            throw new RuntimeException("Connectivity for planar images must be either 4 or 8, not " + connectivity);
        }
        
        // Initialize the result array with the minimum value of marker and mask
        // arrays
        ScalarArray2D<?> result = initializeResult(marker, mask);
        
        processInPlace(result, mask);
        
        return result;
    }
    
    private ScalarArray2D<?> initializeResult(ScalarArray2D<?> marker, ScalarArray2D<?> mask)
    {
        // retrieve image size
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        
        // Create result image the same size as the mask image
        ScalarArray2D<?> result = ScalarArray2D.wrap(mask.newInstance(sizeX, sizeY));
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                double v1 = marker.getValue(x, y) * this.sign;
                double v2 = mask.getValue(x, y) * this.sign;
                result.setValue(x, y, Math.min(v1, v2) * this.sign);
            }
        }
        return result;
    }
    
    /**
     * Applies morphological reconstruction algorithm directly to the specified
     * result array.
     * 
     * @param result
     *            the array that will be used for morphological reconstruction,
     *            and that will be updated during the process.
     * @param mask
     *            the mask array used to constrain the reconstruction
     */
    public void processInPlace(ScalarArray2D<?> result, ScalarArray2D<?> mask)
    {
        // Check sizes are consistent
        if (!Arrays.isSameSize(result, mask))
        {
            throw new IllegalArgumentException("Result and Mask images must have the same size");
        }
        
        // Check connectivity has a correct value
        if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
        {
            throw new RuntimeException("Connectivity for planar images must be either 4 or 8, not " + connectivity);
        }
        
        // Display current status
        fireStatusChanged(this, "Morpho. Rec. Forward");
        
        // forward iteration
        if (connectivity == Connectivity2D.C4)
        {
            forwardScanC4(result, mask);
        }
        else if (connectivity == Connectivity2D.C8)
        {
            forwardScanC8(result, mask);
        }
        
        // Display current status
        fireStatusChanged(this, "Morpho. Rec. Backward");
        
        // backward iteration
        Deque<int[]> queue;
        if (connectivity == Connectivity2D.C4)
        {
            queue = backwardScanC4(result, mask);
        }
        else
        {
            queue = backwardScanC8(result, mask);
        }
        
        fireStatusChanged(this, "Morpho. Rec. Processing queue");
        
        // Process queue
        if (connectivity == Connectivity2D.C4)
        {
            processQueueC4(result, queue, mask);
        }
        else if (connectivity == Connectivity2D.C8)
        {
            processQueueC8(result, queue, mask);
        }
    }

    /**
     * Update result image using pixels in the upper left neighborhood, using
     * the 4-adjacency.
     */
    private void forwardScanC4(ScalarArray2D<?> result, ScalarArray2D<?> mask)
    {
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        fireProgressChanged(this, 0, sizeY);
        
        // Process all other lines
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // Process pixels in the middle of the line
            for (int x = 0; x < sizeX; x++)
            {
                double currentValue = result.getValue(x, y) * this.sign;
                double maxValue = currentValue;
                
                if (x > 0) maxValue = Math.max(maxValue, result.getValue(x - 1, y) * this.sign);
                if (y > 0) maxValue = Math.max(maxValue, result.getValue(x, y - 1) * this.sign);
                
                // update value of current pixel
                maxValue = min(maxValue, mask.getValue(x, y) * this.sign);
                if (maxValue > currentValue)
                {
                    result.setValue(x, y, maxValue * this.sign);
                }
            }
        } // end of forward iteration
        
        // reset progress display
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    /**
     * Update result image using pixels in the upper left neighborhood, using
     * the 8-adjacency.
     */
    private void forwardScanC8(ScalarArray2D<?> result, ScalarArray2D<?> mask)
    {
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        fireProgressChanged(this, 0, sizeY);
        
        // Process all other lines
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // Process pixels in the middle of the line
            for (int x = 0; x < sizeX; x++)
            {
                double currentValue = result.getValue(x, y) * this.sign;
                double maxValue = currentValue;
                
                if (y > 0)
                {
                    // process the 3 values on the line above current pixel
                    if (x > 0) maxValue = Math.max(maxValue, result.getValue(x - 1, y - 1) * this.sign);
                    maxValue = Math.max(maxValue, result.getValue(x, y - 1) * this.sign);
                    if (x < sizeX - 1) maxValue = Math.max(maxValue, result.getValue(x + 1, y - 1) * this.sign);
                }
                if (x > 0) maxValue = Math.max(maxValue, result.getValue(x - 1, y) * this.sign);
                
                // update value of current pixel
                maxValue = min(maxValue, mask.getValue(x, y) * this.sign);
                if (maxValue > currentValue)
                {
                    result.setValue(x, y, maxValue * this.sign);
                }
            }
        } // end of forward iteration
        
        // reset progress display
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    /**
     * Update result image using pixels in the lower-right neighborhood, using
     * the 4-adjacency.
     */
    private Deque<int[]> backwardScanC4(ScalarArray2D<?> result, ScalarArray2D<?> mask)
    {
        int[][] offsets = new int[][] { { +1, 0}, { 0, +1} };
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        Deque<int[]> queue = new ArrayDeque<int[]>();
        
        fireProgressChanged(this, 0, sizeY);
        
        // Process regular lines
        for (int y = sizeY - 1; y >= 0; y--)
        {
            fireProgressChanged(this, sizeY - 1 - y, sizeY);
            
            // Process pixels in the middle of the current line
            // consider pixels on the right and below
            for (int x = sizeX - 1; x >= 0; x--)
            {
                
                double currentValue = result.getValue(x, y) * this.sign;
                double value = currentValue;
                
                if (x < sizeX - 1) value = Math.max(value, result.getValue(x + 1, y) * this.sign);
                if (y < sizeY - 1) value = Math.max(value, result.getValue(x, y + 1) * this.sign);
                
                // combine with mask
                value = min(value, mask.getValue(x, y) * this.sign);
                
                // check if update is required
                if (value <= currentValue)
                {
                    continue;
                }
                
                // update value of current pixel
                result.setValue(x, y, value * this.sign);
                
                // eventually add lower-right neighbors to queue
                for (int[] offset : offsets)
                {
                    int x2 = x + offset[0];
                    int y2 = y + offset[1];
                    if (result.containsPosition(x2, y2))
                    {
                        // combine current value with neighbor mask value
                        double maskValue = mask.getValue(x2, y2) * this.sign;
                        double neighborValue = Math.min(value, maskValue);
                        
                        // add to queue only if value is strictly greater
                        if (neighborValue > result.getValue(x2, y2) * sign)
                        {
                            queue.add(new int[] { x2, y2});
                        }
                    }
                }
            }
        } // end of backward iteration
        
        // reset progress display
        fireProgressChanged(this, sizeY, sizeY);
        return queue;
    }
    
    /**
     * Update result image using pixels in the lower-right neighborhood, using
     * the 8-adjacency.
     */
    private Deque<int[]> backwardScanC8(ScalarArray2D<?> result, ScalarArray2D<?> mask)
    {
        int[][] offsets = new int[][] { { +1, +1 }, { 0, +1 }, { -1, +1 }, { +1, 0 }, };
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        Deque<int[]> queue = new ArrayDeque<int[]>();
        
        fireProgressChanged(this, 0, sizeY);
        
        // Process regular lines
        for (int y = sizeY - 1; y >= 0; y--)
        {
            fireProgressChanged(this, sizeY - 1 - y, sizeY);
            
            // Process pixels in the middle of the current line
            for (int x = sizeX - 1; x >= 0; x--)
            {
                double currentValue = result.getValue(x, y) * this.sign;
                double value = currentValue;
                
                if (y < sizeY - 1)
                {
                    // process the 3 values on the line below current pixel
                    if (x > 0) value = Math.max(value, result.getValue(x - 1, y + 1) * this.sign);
                    value = Math.max(value, result.getValue(x, y + 1) * this.sign);
                    if (x < sizeX - 1) value = Math.max(value, result.getValue(x + 1, y + 1) * this.sign);
                }
                if (x < sizeX - 1) value = Math.max(value, result.getValue(x + 1, y) * this.sign);
                
                // combine with mask
                value = min(value, mask.getValue(x, y) * this.sign);
                
                // check if update is required
                if (value <= currentValue)
                {
                    continue;
                }
                
                // update value of current pixel
                result.setValue(x, y, value * this.sign);
                
                // eventually add lower-right neighbors to queue
                for (int[] offset : offsets)
                {
                    int x2 = x + offset[0];
                    int y2 = y + offset[1];
                    if (result.containsPosition(x2, y2))
                    {
                        // combine current value with neighbor mask value
                        double maskValue = mask.getValue(x2, y2) * this.sign;
                        double neighborValue = Math.min(value, maskValue);
                        
                        // add to queue only if value is strictly greater
                        if (neighborValue > result.getValue(x2, y2) * sign)
                        {
                            queue.add(new int[] { x2, y2});
                        }
                    }
                }
            }
        } // end of backward iteration
        
        // reset progress display
        fireProgressChanged(this, sizeY, sizeY);
        return queue;
    }
    
    /**
     * Update result image using next pixel in the queue, using the 4-adjacency.
     */
    private void processQueueC4(ScalarArray2D<?> result, Deque<int[]> queue, ScalarArray2D<?> mask)
    {
        Collection<int[]> offsets = Connectivity2D.C4.offsets();
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        // the maximal value around current pixel
        double value;
        
        while (!queue.isEmpty())
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            value = result.getValue(x, y) * sign;
            
            // compare with each one of the four neighbors
            if (x > 0) value = max(value, result.getValue(x - 1, y) * this.sign);
            if (x < sizeX - 1) value = max(value, result.getValue(x + 1, y) * this.sign);
            if (y > 0) value = max(value, result.getValue(x, y - 1) * this.sign);
            if (y < sizeY - 1) value = max(value, result.getValue(x, y + 1) * this.sign);
            
            // bound with mask value
            value = min(value, mask.getValue(x, y) * this.sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getValue(x, y) * this.sign) continue;
            
            // update result for current position
            result.setValue(x, y, value * this.sign);
            
            // eventually add lower-right neighbors to queue
            for (int[] offset : offsets)
            {
                int x2 = x + offset[0];
                int y2 = y + offset[1];
                if (result.containsPosition(x2, y2))
                {
                    // combine current value with neighbor mask value
                    double maskValue = mask.getValue(x2, y2) * this.sign;
                    double neighborValue = Math.min(value, maskValue);
                    
                    // add to queue only if value is strictly greater
                    if (neighborValue > result.getValue(x2, y2) * sign)
                    {
                        queue.add(new int[] { x2, y2});
                    }
                }
            }
        }
    }
    
    /**
     * Update result image using next pixel in the queue, using the 8-adjacency.
     */
    private void processQueueC8(ScalarArray2D<?> result, Deque<int[]> queue, ScalarArray2D<?> mask)
    {
        Collection<int[]> offsets = Connectivity2D.C8.offsets();
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        
        // sign for adapting dilation and erosion algorithms
        final int sign = this.reconstructionType.getSign();
        
        // the maximal value around current pixel
        double value;
        
        while (!queue.isEmpty())
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            value = result.getValue(x, y) * sign;
            
            // compute bounds of neighborhood
            int xmin = max(x - 1, 0);
            int xmax = min(x + 1, sizeX - 1);
            int ymin = max(y - 1, 0);
            int ymax = min(y + 1, sizeY - 1);
            
            // compare with each one of the neighbors
            for (int y2 = ymin; y2 <= ymax; y2++)
            {
                for (int x2 = xmin; x2 <= xmax; x2++)
                {
                    value = max(value, result.getValue(x2, y2) * sign);
                }
            }
            
            // bound with mask value
            value = min(value, mask.getValue(x, y) * sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getValue(x, y) * sign) continue;
            
            // update result for current position
            result.setValue(x, y, value * sign);
            
            // eventually add each neighbor
            for (int[] offset : offsets)
            {
                int x2 = x + offset[0];
                int y2 = y + offset[1];
                if (result.containsPosition(x2, y2))
                {
                    // combine current value with neighbor mask value
                    double maskValue = mask.getValue(x2, y2) * this.sign;
                    double neighborValue = Math.min(value, maskValue);
                    
                    // add to queue only if value is strictly greater
                    if (neighborValue > result.getValue(x2, y2) * sign)
                    {
                        queue.add(new int[] { x2, y2});
                    }
                }
            }
        }
    }
}
