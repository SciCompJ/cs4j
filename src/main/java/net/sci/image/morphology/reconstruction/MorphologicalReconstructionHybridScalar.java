/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.impl.ReverseOrderPositionIterator;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.image.connectivity.Connectivity;
import net.sci.image.morphology.MorphologicalReconstruction;
import net.sci.image.morphology.MorphologicalReconstruction.Type;

/**
 * <p>
 * Morphological reconstruction for arrays of scalar with any dimensionality,
 * using an "hybrid" algorithm.
 * 
 * The algorithms is composed of three main steps:
 * <ol>
 * <li>a forward scan updates element values based on neighbors in the
 * upper-left part,</li>,
 * <li>a backward scan updates element values based on neighbors in the
 * lower-right part, and initializes a queue of positions that need
 * updates</li>,
 * <li>the elements in the queue are updated based on all neighbors, and adding
 * position of neighbors to the queue when appropriate.</li>
 * </ol>
 * </p>
 * 
 * @see MorphologicalReconstruction2DHybrid
 * @see MorphologicalReconstruction3DHybrid
 * 
 * @author dlegland
 */
public class MorphologicalReconstructionHybridScalar extends AlgoStub
{
    // ==================================================
    // Class variables 
    
    /**
     * The connectivity of the algorithm.
     */
    protected Connectivity connectivity;

    /**
     * The type of morphological reconstruction.
     */
    protected Type reconstructionType = Type.BY_DILATION;
    
    
    // ==================================================
    // Constructors 
        
    /**
     * Creates a new instance of morphological reconstruction by dilation algorithm,
     * using the default connectivity 4.
     */
    public MorphologicalReconstructionHybridScalar()
    {
    }
    
    /**
     * Creates a new instance of morphological reconstruction algorithm,
     * that specifies the type of reconstruction, and using the default connectivity 4.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     */
    public MorphologicalReconstructionHybridScalar(MorphologicalReconstruction.Type type) 
    {
        setReconstructionType(type);
    }

    /**
     * Creates a new instance of morphological reconstruction algorithm,
     * that specifies the connectivity to use.
     * 
     * @param connectivity
     *            the connectivity to use.
     */
    public MorphologicalReconstructionHybridScalar(Connectivity connectivity)
    {
        setConnectivity(connectivity);
    }

    /**
     * Creates a new instance of morphological reconstruction algorithm,
     * that specifies the type of reconstruction, and the connectivity to use.
     * 
     * @param type
     *            the type of reconstruction (erosion or dilation)
     * @param connectivity
     *            the connectivity to use.
     */
    public MorphologicalReconstructionHybridScalar(MorphologicalReconstruction.Type type, 
            Connectivity connectivity) 
    {
        setReconstructionType(type);
        setConnectivity(connectivity);
    }
    
    
    // ==================================================
    // Getters and setters
    
    /**
     * @return the reconstructionType
     */
    public MorphologicalReconstruction.Type getReconstructionType()
    {
        return reconstructionType;
    }

    /**
     * @param reconstructionType the reconstructionType to set
     */
    public void setReconstructionType(MorphologicalReconstruction.Type reconstructionType) 
    {
        this.reconstructionType = reconstructionType;
    }

    /**
     * @return the connectivity
     */
    public Connectivity getConnectivity()
    {
        return connectivity;
    }

    /**
     * @param connectivity the connectivity to set
     */
    public void setConnectivity(Connectivity connectivity)
    {
        this.connectivity = connectivity;
    }
    

    // ==================================================
    // Processing methods
    
    /**
     * Applies morphological reconstruction algorithm to the input marker and
     * mask arrays.
     * 
     * @param marker
     *            the marker array used to initialize the reconstruction
     * @param mask
     *            the mask array used to constrain the reconstruction
     * @return the morphological reconstruction of marker array constrained by mask
     *         array
     */
    public ScalarArray<?> process(ScalarArray<?> marker, ScalarArray<?> mask)
    {
        // Check sizes are consistent
        if (!Arrays.isSameSize(marker, mask))
        {
            throw new IllegalArgumentException("Marker and Mask images must have the same size");
        }
        
        if (marker instanceof IntArray && mask instanceof IntArray)
        {
            fireStatusChanged(this, "Initialize result");
            IntArray<?> result = initializeResult_int((IntArray<?>) marker, (IntArray<?>) mask);
            processInPlace_int((IntArray<?>) result, (IntArray<?>)mask);
            return result;
        }
        else
        {
            fireStatusChanged(this, "Initialize result");
            ScalarArray<?> result = initializeResult(marker, mask);
            processInPlace(result, mask);
            return result;
        }
    }
    
    
    // ==================================================
    // Specific processing methods
    
    private ScalarArray<?> initializeResult(ScalarArray<?> marker, ScalarArray<?> mask)
    {
        // Create result image the same size as the mask image
        ScalarArray<?> result = mask.newInstance(mask.size());
        
        // initialize with min or max of marker and mask values
        if (this.reconstructionType == Type.BY_DILATION)
        {
            for (int[] pos : marker.positions())
            {
                double v1 = marker.getValue(pos); 
                double v2 = mask.getValue(pos); 
                result.setValue(pos, Math.min(v1, v2));
            }
        }
        else
        {
            for (int[] pos : marker.positions())
            {
                double v1 = marker.getValue(pos); 
                double v2 = mask.getValue(pos); 
                result.setValue(pos, Math.max(v1, v2));
            }
        }
        
        return result;
    }

    private IntArray<?> initializeResult_int(IntArray<?> marker, IntArray<?> mask)
    {
        // Create result image the same size as the mask image
        IntArray<?> result = mask.newInstance(mask.size());
        
        // initialize with min or max of marker and mask values
        if (this.reconstructionType == Type.BY_DILATION)
        {
            for (int[] pos : marker.positions())
            {
                int v1 = marker.getInt(pos); 
                int v2 = mask.getInt(pos); 
                result.setInt(pos, Math.min(v1, v2));
            }
        }
        else
        {
            for (int[] pos : marker.positions())
            {
                int v1 = marker.getInt(pos); 
                int v2 = mask.getInt(pos); 
                result.setInt(pos, Math.max(v1, v2));
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
    public void processInPlace(ScalarArray<?> result, ScalarArray<?> mask)
    {
        // Check sizes are consistent
        if (!Arrays.isSameSize(result, mask))
        {
            throw new IllegalArgumentException("Result and Mask images must have the same size");
        }
        
        // check connectivity, creating a default one if necessary
        Connectivity conn = this.connectivity;
        if (conn == null)
        {
            conn = Connectivity.createOrtho(result.dimensionality());
        }
        if (conn.dimensionality() != mask.dimensionality())
        {
            throw new IllegalArgumentException("Connectivity must have same dimensionality as marker and mask, currently " + conn.dimensionality());
        }
        
        fireStatusChanged(this, "Morpho. Rec. Forward");
        forwardScan(result, mask, conn);

        fireStatusChanged(this, "Morpho. Rec. Backward");
        Deque<int[]> queue = backwardScan(result, mask, conn);
        
        fireStatusChanged(this, "Morpho. Rec. Process Queue");
        processQueue(result, queue, mask, conn);
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
    private void processInPlace_int(IntArray<?> result, IntArray<?> mask)
    {
        // Check sizes are consistent
        if (!Arrays.isSameSize(result, mask))
        {
            throw new IllegalArgumentException("Result and Mask images must have the same size");
        }
        if (connectivity.dimensionality() != mask.dimensionality())
        {
            throw new IllegalArgumentException("Connectivity must have same dimensionality as marker and mask, currently " + connectivity.dimensionality());
        }
        
        fireStatusChanged(this, "Morpho. Rec. Forward");
        forwardScan_int(result, mask);

        fireStatusChanged(this, "Morpho. Rec. Backward");
        Deque<int[]> queue = backwardScan_int(result, mask);
        
        fireStatusChanged(this, "Morpho. Rec. Process Queue");
        processQueue_int(result, queue, mask);
    }
    
    
    /**
     * Update result image using pixels in the upper left neighborhood.
     */
    private void forwardScan(ScalarArray<?> result, ScalarArray<?> mask, Connectivity conn) 
    {
        // initializations
        int[] dims = mask.size();
        int sign = reconstructionType.getSign();
        Collection<int[]> forwardOffsets = forwardOffsets(conn);
        
        // process positions
        for(int[] pos : result.positions())
        {
            double currentValue = result.getValue(pos) * sign;
            double maxValue = currentValue;
            
            for (int[] offset : forwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // do not compare with neighbors outside bounds
                if(isWithinBounds(pos2, dims))
                {
                    maxValue = Math.max(maxValue, result.getValue(pos2) * sign);
                }
            }
            
            // update value of current array element
            maxValue = min(maxValue, mask.getValue(pos) * sign);
            if (maxValue > currentValue)
            {
                result.setValue(pos, maxValue * sign);
            }
        }
    }
    
    /**
     * Update result image using pixels in the upper left neighborhood.
     */
    private void forwardScan_int(IntArray<?> result, IntArray<?> mask) 
    {
        // initializations
        int[] dims = mask.size();
        int sign = reconstructionType.getSign();
        Collection<int[]> forwardOffsets = forwardOffsets(connectivity);
        
        // process positions
        for(int[] pos : result.positions())
        {
            int currentValue = result.getInt(pos) * sign;
            int maxValue = currentValue;
            
            for (int[] offset : forwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // do not compare with neighbors outside bounds
                if(isWithinBounds(pos2, dims))
                {
                    maxValue = Math.max(maxValue, result.getInt(pos2) * sign);
                }
            }
            
            // update value of current array element
            maxValue = min(maxValue, mask.getInt(pos) * sign);
            if (maxValue > currentValue)
            {
                result.setInt(pos, maxValue * sign);
            }
        }
    }
    
    /**
     * Update result image using pixels in the lower-right neighborhood, and
     * returns the priority queue of positions to update.
     */
    private Deque<int[]> backwardScan(ScalarArray<?> result, ScalarArray<?> mask, Connectivity conn) 
    {
        // initializations
        int[] dims = mask.size();
        int sign = reconstructionType.getSign();
        Collection<int[]> backwardOffsets = backwardOffsets(conn);
        
        // create the queue containing the positions that need update
        Deque<int[]> queue = new ArrayDeque<>();
        
        // iterate on positions of target array
        ReverseOrderPositionIterator iter = new ReverseOrderPositionIterator(dims);
        while(iter.hasNext())
        {
            int[] pos = iter.next();
            
            double currentValue = result.getValue(pos) * sign;
            double maxValue = currentValue;
            
            for (int[] offset : backwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // do not compare with neighbors outside bounds
                if(isWithinBounds(pos2, dims))
                {
                    maxValue = Math.max(maxValue, result.getValue(pos2) * sign);
                }
            }
            
            // combine with mask value
            maxValue = min(maxValue, mask.getValue(pos) * sign);
            
            // check if update is required
            if (maxValue <= currentValue)
            {
                continue;
            }
            
            // update value of current element
            result.setValue(pos, maxValue * sign);
            
            // eventually add lower-right neighbors to queue
            for (int[] offset : backwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // check bounds
                if(!isWithinBounds(pos2, dims))
                {
                    continue;
                }
                
                // combine current (max) value with neighbor mask value
                double maskValue = mask.getValue(pos2) * sign;
                double value = Math.min(maxValue, maskValue);
                
                // Update result value only if value is strictly greater
                if (value > result.getValue(pos2) * sign) 
                {
                    queue.add(pos2);
                }
            }
        }
        
        return queue;
    }
    
    /**
     * Update result image using pixels in the lower-right neighborhood, and
     * returns the priority queue of positions to update.
     */
    private Deque<int[]> backwardScan_int(IntArray<?> result, IntArray<?> mask) 
    {
        // initializations
        int[] dims = mask.size();
        int sign = reconstructionType.getSign();
        Collection<int[]> backwardOffsets = backwardOffsets(connectivity);
        
        // create the queue containing the positions that need update
        Deque<int[]> queue = new ArrayDeque<>();
        
        // iterate on positions of target array
        ReverseOrderPositionIterator iter = new ReverseOrderPositionIterator(dims);
        while(iter.hasNext())
        {
            int[] pos = iter.next();
            
            int currentValue = result.getInt(pos) * sign;
            int maxValue = currentValue;
            
            for (int[] offset : backwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // do not compare with neighbors outside bounds
                if(isWithinBounds(pos2, dims))
                {
                    maxValue = Math.max(maxValue, result.getInt(pos2) * sign);
                }
            }
            
            // combine with mask value
            maxValue = min(maxValue, mask.getInt(pos) * sign);
            
            // check if update is required
            if (maxValue <= currentValue)
            {
                continue;
            }
            
            // update value of current element
            result.setInt(pos, maxValue * sign);
            
            // eventually add lower-right neighbors to queue
            for (int[] offset : backwardOffsets)
            {
                int[] pos2 = addCoords(pos, offset);
                
                // check bounds
                if(!isWithinBounds(pos2, dims))
                {
                    continue;
                }
                
                // combine current (max) value with neighbor mask value
                int maskValue = mask.getInt(pos2) * sign;
                int value = Math.min(maxValue, maskValue);
                
                // Update result value only if value is strictly greater
                if (value > result.getInt(pos2) * sign) 
                {
                    queue.add(pos2);
                }
            }
        }
        
        return queue;
    }
    
    private void processQueue(ScalarArray<?> result, Deque<int[]> queue, ScalarArray<?> mask, Connectivity conn)
    {
        int sign = reconstructionType.getSign();
        int[] dims = mask.size();
        
        // the maximal value around current pixel
        double value;
        
        while (!queue.isEmpty())
        {
            int[] pos = queue.removeFirst();
            value = result.getValue(pos) * sign;
            
            // compare with each one of the neighbors
            for (int[] pos2 : conn.neighbors(pos))
            {
                if(isWithinBounds(pos2, dims))
                {
                    value = max(value, result.getValue(pos2) * sign);
                }
            }
            
            // bound with mask value
            value = min(value, mask.getValue(pos) * sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getValue(pos) * sign) 
                continue;
            
            // update result for current position
            result.setValue(pos, value * sign);
            
            // Eventually add each neighbor
            for (int[] pos2 : conn.neighbors(pos))
            {
                if(!isWithinBounds(pos2, dims))
                {
                    continue;
                }

                // combine current (max) value with neighbor mask value
                double maskValue = mask.getValue(pos2) * sign;
                double neighborValue = Math.min(value, maskValue);

                // Update result value only if value is strictly greater
                if (neighborValue > result.getValue(pos2) * sign)
                {
                    queue.add(pos2);
                }
            }
        }
    }
    
    private void processQueue_int(IntArray<?> result, Deque<int[]> queue, IntArray<?> mask)
    {
        int sign = reconstructionType.getSign();
        int[] dims = mask.size();
        
        // the maximal value around current pixel
        int value;
        
        while (!queue.isEmpty())
        {
            int[] pos = queue.removeFirst();
            value = result.getInt(pos) * sign;
            
            // compare with each one of the neighbors
            for (int[] pos2 : connectivity.neighbors(pos))
            {
                if(isWithinBounds(pos2, dims))
                {
                    value = max(value, result.getInt(pos2) * sign);
                }
            }
            
            // bound with mask value
            value = min(value, mask.getInt(pos) * sign);
            
            // if no update is needed, continue to next item in queue
            if (value <= result.getInt(pos) * sign) 
                continue;
            
            // update result for current position
            result.setInt(pos, value * sign);
            
            // Eventually add each neighbor
            for (int[] pos2 : connectivity.neighbors(pos))
            {
                if(!isWithinBounds(pos2, dims))
                {
                    continue;
                }

                // combine current (max) value with neighbor mask value
                int maskValue = mask.getInt(pos2) * sign;
                int neighborValue = Math.min(value, maskValue);

                // Update result value only if value is strictly greater
                if (neighborValue > result.getInt(pos2) * sign)
                {
                    queue.add(pos2);
                }
            }
        }
    }
    
    /**
     * Filters the offsets from specified connectivity that will be used during
     * the forward iteration.
     * 
     * @param conn
     *            the connectivity that specifies all offsets
     * @return the offsets to use during the forward iteration
     */
    private static final Collection<int[]> forwardOffsets(Connectivity conn)
    {
        ArrayList<int[]> res = new ArrayList<>();
        offset:
        for (int[] offset : conn.offsets())
        {
            for (int d = 0; d < offset.length; d++)
            {
                if (offset[d] < 0)
                {
                    res.add(offset);
                    continue offset;
                }
            }
        }
        return res;
    }

    /**
     * Filters the offsets from specified connectivity that will be used during
     * the backward iteration.
     * 
     * @param conn
     *            the connectivity that specifies all offsets
     * @return the offsets to use during the backward iteration
     */
    private static final Collection<int[]> backwardOffsets(Connectivity conn)
    {
        ArrayList<int[]> res = new ArrayList<>();
        offset:
        for (int[] offset : conn.offsets())
        {
            for (int d = 0; d < offset.length; d++)
            {
                if (offset[d] > 0)
                {
                    res.add(offset);
                    continue offset;
                }
            }
        }
        return res;
    }

    private static final int[] addCoords(int[] pos, int[] offset)
    {
        int nd = pos.length;
        int[] res = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            res[d] = pos[d] + offset[d];
        }
        return res;
    }
    
    private static final boolean isWithinBounds(int[] pos, int[] dims)
    {
        for (int d = 0; d < pos.length; d++)
        {
            if (pos[d] < 0) return false;
            if (pos[d] >= dims[d]) return false;
        }
        return true;
    }
}
