/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.impl.ReverseOrderPositionIterator;
import net.sci.array.numeric.ScalarArray;
import net.sci.image.Connectivity;
import net.sci.image.Connectivity2D;
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
 * @see MorphologicalREconstruction2DHybrid
 * @see MorphologicalREconstruction3DHybrid
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
    protected Connectivity connectivity = Connectivity2D.C4;

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
//        this.sign = reconstructionType.getSign();
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
        if (connectivity.dimensionality() != mask.dimensionality())
        {
            throw new IllegalArgumentException("Connectivity must have same dimensionality as marker and mask, currently " + connectivity.dimensionality());
        }
        
        fireStatusChanged(this, "Initialize result");
        ScalarArray<?> result = initializeResult(marker, mask);
        
        processInPlace(result, mask);
        
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
        if (connectivity.dimensionality() != mask.dimensionality())
        {
            throw new IllegalArgumentException("Connectivity must have same dimensionality as marker and mask, currently " + connectivity.dimensionality());
        }
        
        fireStatusChanged(this, "Morpho. Rec. Forward");
        forwardScan(result, mask);

        fireStatusChanged(this, "Morpho. Rec. Backward");
        Deque<int[]> queue = backwardScan(result, mask);
        
        fireStatusChanged(this, "Morpho. Rec. Process Queue");
        processQueue(result, queue, mask);
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
    
    /**
     * Update result image using pixels in the upper left neighborhood.
     */
    private void forwardScan(ScalarArray<?> result, ScalarArray<?> mask) 
    {
        // initializations
        int[] dims = mask.size();
        int sign = reconstructionType.getSign();
        Collection<int[]> forwardOffsets = forwardOffsets(connectivity);
        
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
    
    private static final Collection<int[]> forwardOffsets(Connectivity conn)
    {
        ArrayList<int[]> offsets = new ArrayList<>();
        for (int[] offset : conn.offsets())
        {
            for (int d = 0; d < offset.length; d++)
            {
                if (offset[d] < 0)
                {
                    offsets.add(offset);
                    // break iteration on d, then continue on iteration on offset
                    break;
                }
            }
        }
        return offsets;
    }
    
    /**
     * Update result image using pixels in the lower-right neighborhood.
     */
    private Deque<int[]> backwardScan(ScalarArray<?> result, ScalarArray<?> mask) 
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
    
    private static final Collection<int[]> backwardOffsets(Connectivity conn)
    {
        ArrayList<int[]> offsets = new ArrayList<>();
        offset:
        for (int[] offset : conn.offsets())
        {
            for (int d = 0; d < offset.length; d++)
            {
                if (offset[d] > 0)
                {
                    offsets.add(offset);
                    continue offset;
                }
            }
        }
        return offsets;
    }
    
    private void processQueue(ScalarArray<?> result, Deque<int[]> queue, ScalarArray<?> mask)
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
            for (int[] pos2 : connectivity.neighbors(pos))
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
            for (int[] pos2 : connectivity.neighbors(pos))
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
