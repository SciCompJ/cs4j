/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import static java.lang.Math.max;
import static java.lang.Math.min;
import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.MorphologicalReconstruction;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * <p>
 * Morphological reconstruction for 3D arrays of scalar values, using hybrid
 * algorithm. This class manages both reconstructions by dilation and erosion.
 * </p>
 * 
 * <p>
 * This version first performs forward scan, then performs a backward scan that
 * also add lower-right neighbors to the queue, and finally processes voxels in
 * the queue. It is intended to work on 3D images, using either the 6 or 26 connectivity.
 * </p>
 * 
 * @author David Legland
 * @see MorphologicalReconstruction2DHybrid
 */
public class MorphologicalReconstruction3DHybrid extends AlgoStub implements MorphologicalReconstruction3D
{
    // ==================================================
    // Class variables
    
    protected MorphologicalReconstruction.Type reconstructionType = MorphologicalReconstruction.Type.BY_DILATION;
    
    /**
     * The sign value associated to reconstruction type.
     * <ul>
     * <li>+1 : reconstruction by dilation.</li>
     * <li>-1 : reconstruction by erosion.</li>
     * </ul>
     */
    protected int sign = 1;
    
    /**
     * The connectivity of the algorithm, usually either C6 or C26.
     */
    protected Connectivity3D connectivity = Connectivity3D.C6;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Creates a new instance of 3D morphological reconstruction by dilation
     * algorithm, using the default connectivity 6.
     */
    public MorphologicalReconstruction3DHybrid()
    {
    }
    
    /**
     * Creates a new instance of 3D morphological reconstruction algorithm, that
     * specifies the type of reconstruction, and using the connectivity 6.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     */
    public MorphologicalReconstruction3DHybrid(MorphologicalReconstruction.Type type)
    {
        setReconstructionType(type);
    }
    
    /**
     * Creates a new instance of 3D morphological reconstruction algorithm, that
     * specifies the type of reconstruction, and the connectivity to use.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     * @param connectivity
     *            the 3D connectivity to use (either C6 or C26)
     */
    public MorphologicalReconstruction3DHybrid(MorphologicalReconstruction.Type type, Connectivity3D connectivity)
    {
        setReconstructionType(type);
        setConnectivity(connectivity);
    }
    
    /**
     * Creates a new instance of 3D morphological reconstruction by dilation
     * algorithm, that specifies the connectivity to use.
     * 
     * @param connectivity
     *            the 3D connectivity to use (either C6 or C26)
     */
    public MorphologicalReconstruction3DHybrid(Connectivity3D connectivity)
    {
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
    public Connectivity3D getConnectivity()
    {
        return connectivity;
    }
    
    /**
     * @param connectivity
     *            the connectivity to set
     */
    public void setConnectivity(Connectivity3D connectivity)
    {
        this.connectivity = connectivity;
    }
    
    
    // ==================================================
    // Methods implementing the MorphologicalReconstruction interface
    
    /**
     * Run the morphological reconstruction algorithm using the specified arrays
     * as argument.
     * 
     * @param marker
     *            the 3D array of the marker
     * @param mask
     *            the 3D array of the mask
     * @return the morphological reconstruction of the marker array constrained
     *         to the mask array
     */
    public ScalarArray3D<?> process(ScalarArray3D<?> marker, ScalarArray3D<?> mask)
    {
        // Check dimensions consistency
        if (!Arrays.isSameSize(marker, mask))
        {
            throw new IllegalArgumentException("Marker and Mask images must have the same size");
        }
        
        // Check connectivity has a correct value
        if (connectivity != Connectivity3D.C6 && connectivity != Connectivity3D.C26)
        {
            throw new RuntimeException("Connectivity for stacks must be either 6 or 26, not " + connectivity);
        }
        
        fireStatusChanged(this, "Initialize result");
        ScalarArray3D<?> result = initializeResult(marker, mask);
        
        processInPlace(result, mask);
        
        return result;
    }
    
    
    // ==================================================
    // Inner processing methods
    
    /**
     * Initialize the result image with the minimum value of marker and mask
     * images.
     */
    private ScalarArray3D<?> initializeResult(ScalarArray3D<?> marker, ScalarArray3D<?> mask)
    {
        // retrieve image size
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        int sizeZ = marker.size(2);
        
        // Create result image the same size as marker image
        ScalarArray3D<?> result = ScalarArray3D.wrap(mask.newInstance(sizeX, sizeY, sizeZ));
        
        // Initialize integer result stack
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double v1 = marker.getValue(x, y, z) * this.sign;
                    double v2 = mask.getValue(x, y, z) * this.sign;
                    result.setValue(x, y, z, min(v1, v2) * this.sign);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Run the morphological reconstruction algorithm using the specified arrays
     * as argument.
     * 
     * @param marker
     *            the 3D array of the marker
     * @param mask
     *            the 3D array of the mask
     * @return the morphological reconstruction of the marker array constrained
     *         to the mask array
     */
    public void processInPlace(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        // Check dimensions consistency
        if (!Arrays.isSameSize(result, mask))
        {
            throw new IllegalArgumentException("Result and Mask images must have the same size");
        }
        
        // Check connectivity has a correct value
        if (connectivity != Connectivity3D.C6 && connectivity != Connectivity3D.C26)
        {
            throw new RuntimeException("Connectivity for stacks must be either 6 or 26, not " + connectivity);
        }
        
        // Display current status
        fireStatusChanged(this, "Forward iteration");
        forwardScan(result, mask);
        
        // Display current status
        fireStatusChanged(this, "Backward iteration");
        Deque<int[]> queue = backwardScanInitQueue(result, mask);
        
        // Display current status
        fireStatusChanged(this, "Process queue");
        processQueue(result, queue, mask);
    }
    

    private void forwardScan(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        if (this.connectivity == Connectivity3D.C6)
        {
            forwardScanC6(result, mask);
        }
        else
        {
            forwardScanC26(result, mask);
        }
    }
    
    /**
     * Update result image using pixels in the upper left neighborhood, using
     * the 6-adjacency, assuming pixels are stored in bytes.
     */
    private void forwardScanC6(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double maxValue;
        
        // Iterate over voxels
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            
            // process current slice
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double currentValue = result.getValue(x, y, z) * this.sign;
                    maxValue = currentValue;
                    
                    // Iterate over the 3 'upper' neighbors of current pixel
                    if (x > 0) maxValue = max(maxValue, result.getValue(x - 1, y, z) * this.sign);
                    if (y > 0) maxValue = max(maxValue, result.getValue(x, y - 1, z) * this.sign);
                    if (z > 0) maxValue = max(maxValue, result.getValue(x, y, z - 1) * this.sign);
                    
                    // update value of current voxel
                    maxValue = min(maxValue, mask.getValue(x, y, z) * this.sign);
                    if (maxValue > currentValue)
                    {
                        result.setValue(x, y, z, maxValue * this.sign);
                    }
                }
            }
        } // end of voxel iteration
        
        fireProgressChanged(this, sizeZ, sizeZ);
    }
    
    /**
     * Update result image using pixels in the upper left neighborhood, using
     * the 26-adjacency, assuming pixels are stored using integer data types.
     */
    private void forwardScanC26(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double maxValue;
        
        // Iterate over voxels
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            
            // process current slice
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double currentValue = result.getValue(x, y, z) * this.sign;
                    maxValue = currentValue;
                    
                    // Iterate over neighbors of current pixel
                    int zmax = min(z + 1, sizeZ);
                    for (int z2 = max(z - 1, 0); z2 < zmax; z2++)
                    {
                        int ymax = z2 == z ? y : min(y + 1, sizeY - 1);
                        for (int y2 = max(y - 1, 0); y2 <= ymax; y2++)
                        {
                            int xmax = (z2 == z && y2 == y) ? x - 1 : min(x + 1, sizeX - 1);
                            for (int x2 = max(x - 1, 0); x2 <= xmax; x2++)
                            {
                                maxValue = max(maxValue, result.getValue(x2, y2, z2) * this.sign);
                            }
                        }
                    }
                    
                    // update value of current voxel
                    maxValue = min(maxValue, mask.getValue(x, y, z) * sign);
                    if (maxValue > currentValue)
                    {
                        result.setValue(x, y, z, maxValue * this.sign);
                    }
                }
            }
        }
        
        fireProgressChanged(this, sizeZ, sizeZ);
    }
    
    private Deque<int[]> backwardScanInitQueue(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        if (this.connectivity == Connectivity3D.C6)
        {
            return backwardScanInitQueueC6(result, mask);
        }
        else
        {
            return backwardScanInitQueueC26(result, mask);
        }
    }
    
    /**
     * Update result image using pixels in the lower right neighborhood, using
     * the 6-adjacency.
     */
    private Deque<int[]> backwardScanInitQueueC6(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        int[][] offsets = new int[][] { { +1, 0, 0 }, { 0, +1, 0 }, { 0, 0, +1 } };
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double value;
        
        ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
        
        // Iterate over voxels
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ - 1 - z, sizeZ);
            
            // process current slice
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    double currentValue = result.getValue(x, y, z) * this.sign;
                    value = currentValue;
                    
                    // Iterate over the 3 'lower' neighbors of current voxel
                    if (x < sizeX - 1) value = max(value, result.getValue(x + 1, y, z) * this.sign);
                    if (y < sizeY - 1) value = max(value, result.getValue(x, y + 1, z) * this.sign);
                    if (z < sizeZ - 1) value = max(value, result.getValue(x, y, z + 1) * this.sign);
                    
                    // combine with mask
                    value = min(value, mask.getValue(x, y, z) * this.sign);
                    
                    // check if modification is required
                    if (value <= currentValue) continue;
                    
                    // update value of current voxel
                    result.setValue(x, y, z, value * this.sign);
                    
                    // eventually add lower-right neighbors to queue
                    for (int[] offset : offsets)
                    {
                        int x2 = x + offset[0];
                        int y2 = y + offset[1];
                        int z2 = z + offset[2];
                        if (x2 < sizeX && y2 < sizeY && z2 < sizeZ)
                        {
                            // combine current value with neighbor mask value
                            double maskValue = mask.getValue(x2, y2, z2) * this.sign;
                            double neighborValue = Math.min(value, maskValue);
                            
                            // add to queue only if value is strictly greater
                            if (neighborValue > result.getValue(x2, y2, z2) * sign)
                            {
                                queue.add(new int[] { x2, y2, z2});
                            }
                        }
                    }
                }
            }
        }
        
        fireProgressChanged(this, sizeZ, sizeZ);
        return queue;
    }
    
    /**
     * Update result image using pixels in the upper left neighborhood, using
     * the 26-adjacency.
     */
    private Deque<int[]> backwardScanInitQueueC26(ScalarArray3D<?> result, ScalarArray3D<?> mask)
    {
        int[][] offsets = new int[][] { 
            { +1, +1, +1 }, { 0, +1, +1 }, { -1, +1, +1 }, 
            { +1,  0, +1 }, { 0,  0, +1 }, { -1,  0, +1 }, 
            { +1, -1, +1 }, { 0, -1, +1 }, { -1, -1, +1 }, 
            { +1, +1,  0 }, { 0, +1,  0 }, { -1, +1,  0 }, 
            { +1,  0,  0 }, 
        };
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double value;
        
        ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
        
        // Iterate over voxels
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ - 1 - z, sizeZ);
            
            // process current slice
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    double currentValue = result.getValue(x, y, z) * this.sign;
                    value = currentValue;
                    
                    // Iterate over neighbors of current voxel
                    for (int z2 = min(z + 1, sizeZ - 1); z2 >= z; z2--)
                    {
                        int ymin = z2 == z ? y : max(y - 1, 0);
                        for (int y2 = min(y + 1, sizeY - 1); y2 >= ymin; y2--)
                        {
                            int xmin = (z2 == z && y2 == y) ? x : max(x - 1, 0);
                            for (int x2 = min(x + 1, sizeX - 1); x2 >= xmin; x2--)
                            {
                                value = max(value, result.getValue(x2, y2, z2) * this.sign);
                            }
                        }
                    }
                    
                    // combine with mask
                    value = min(value, mask.getValue(x, y, z) * this.sign);
                    
                    // check if modification is required
                    if (value <= currentValue) continue;
                    
                    // update value of current voxel
                    result.setValue(x, y, z, value * this.sign);
                    
                    // eventually add lower-right neighbors to queue
                    for (int[] offset : offsets)
                    {
                        int x2 = x + offset[0];
                        int y2 = y + offset[1];
                        int z2 = z + offset[2];
                        if (result.containsPosition(x2, y2, z2))
                        {
                            // combine current value with neighbor mask value
                            double maskValue = mask.getValue(x2, y2, z2) * this.sign;
                            double neighborValue = Math.min(value, maskValue);
                            
                            // add to queue only if value is strictly greater
                            if (neighborValue > result.getValue(x2, y2, z2) * sign)
                            {
                                queue.add(new int[] { x2, y2, z2});
                            }
                        }
                    }
                }
            }
        }
        
        fireProgressChanged(this, sizeZ, sizeZ);
        return queue;
    }
    
    private void processQueue(ScalarArray3D<?> result, Deque<int[]> queue, ScalarArray3D<?> mask)
    {
        if (this.connectivity == Connectivity3D.C6)
        {
            processQueueC6(result, queue, mask);
        }
        else
        {
            processQueueC26(result, queue, mask);
        }
    }
    
    /**
     * Update result image using next pixel in the queue, using the 6-adjacency.
     */
    private void processQueueC6(ScalarArray3D<?> result, Deque<int[]> queue, ScalarArray3D<?> mask)
    {
        Collection<int[]> offsets = Connectivity3D.C6.offsets();
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double value;
        
        while (!queue.isEmpty())
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            int z = p[2];
            value = result.getValue(x, y, z) * this.sign;
            
            // compare with each one of the neighbors
            if (x > 0) value = max(value, result.getValue(x - 1, y, z) * this.sign);
            if (x < sizeX - 1) value = max(value, result.getValue(x + 1, y, z) * this.sign);
            if (y > 0) value = max(value, result.getValue(x, y - 1, z) * this.sign);
            if (y < sizeY - 1) value = max(value, result.getValue(x, y + 1, z) * this.sign);
            if (z > 0) value = max(value, result.getValue(x, y, z - 1) * this.sign);
            if (z < sizeZ - 1) value = max(value, result.getValue(x, y, z + 1) * this.sign);
            
            // bound with mask value
            value = min(value, mask.getValue(x, y, z) * this.sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getValue(x, y, z) * this.sign) continue;
            
            // update result for current position
            result.setValue(x, y, z, value * this.sign);
            
            // eventually add each neighbor
            for (int[] offset : offsets)
            {
                int x2 = x + offset[0];
                int y2 = y + offset[1];
                int z2 = z + offset[2];
                if (result.containsPosition(x2, y2, z2))
                {
                    // combine current value with neighbor mask value
                    double maskValue = mask.getValue(x2, y2, z2) * this.sign;
                    double neighborValue = Math.min(value, maskValue);
                    
                    // add to queue only if value is strictly greater
                    if (neighborValue > result.getValue(x2, y2, z2) * sign)
                    {
                        queue.add(new int[] { x2, y2, z2});
                    }
                }
            }
        }
    }
    
    /**
     * Update result image using next pixel in the queue, using the
     * 26-adjacency.
     */
    private void processQueueC26(ScalarArray3D<?> result, Deque<int[]> queue, ScalarArray3D<?> mask)
    {
        Collection<int[]> offsets = Connectivity3D.C26.offsets();
        
        // retrieve array size
        int sizeX = result.size(0);
        int sizeY = result.size(1);
        int sizeZ = result.size(2);
        
        // the maximal value around current pixel
        double value;
        
        while (!queue.isEmpty())
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            int z = p[2];
            value = result.getValue(x, y, z) * this.sign;
            
            // compute bounds of neighborhood
            int xmin = max(x - 1, 0);
            int xmax = min(x + 1, sizeX - 1);
            int ymin = max(y - 1, 0);
            int ymax = min(y + 1, sizeY - 1);
            int zmin = max(z - 1, 0);
            int zmax = min(z + 1, sizeZ - 1);
            
            // compare with each one of the neighbors
            for (int z2 = zmin; z2 <= zmax; z2++)
            {
                for (int y2 = ymin; y2 <= ymax; y2++)
                {
                    for (int x2 = xmin; x2 <= xmax; x2++)
                    {
                        value = max(value, result.getValue(x2, y2, z2) * this.sign);
                    }
                }
            }
            
            // bound with mask value
            value = min(value, mask.getValue(x, y, z) * this.sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getValue(x, y, z) * this.sign) continue;
            
            // update result for current position
            result.setValue(x, y, z, value * this.sign);
            
            // eventually add each neighbor
            for (int[] offset : offsets)
            {
                int x2 = x + offset[0];
                int y2 = y + offset[1];
                int z2 = z + offset[2];
                if (result.containsPosition(x2, y2, z2))
                {
                    // combine current value with neighbor mask value
                    double maskValue = mask.getValue(x2, y2, z2) * this.sign;
                    double neighborValue = Math.min(value, maskValue);
                    
                    // add to queue only if value is strictly greater
                    if (neighborValue > result.getValue(x2, y2, z2) * sign)
                    {
                        queue.add(new int[] { x2, y2, z2});
                    }
                }
            }
        }
    }
}
