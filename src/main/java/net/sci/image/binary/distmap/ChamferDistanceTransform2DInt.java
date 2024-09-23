/**
 * 
 */
package net.sci.image.binary.distmap;

import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * <p>
 * Computes 2D Chamfer distance maps using an arbitrary chamfer mask, and
 * storing result in a 2D array of Integer values with arbitrary type.
 * </p>
 * 
 * <p>
 * This algorithm performs two passes to update the distance map, one forward
 * and one backward. The neighborhood of pixels to consider for update it
 * determined by the chamfer mask. Larger masks usually provide more accurate
 * results, but may increase computation time.
 * </p>
 * 
 * <p>
 * The resulting map is stored within a 2D integer array. This implementation
 * allows to choose the type of the result by specifying the factory to use for
 * creating the result array.
 * </p>
 * 
 * @author David Legland
 * 
 * @see ChamferDistanceTransform3DInt
 * @see ChamferDistanceTransform2DFloat32
 */
public class ChamferDistanceTransform2DInt extends AlgoStub implements DistanceTransform, ChamferDistanceTransform2D
{
    // ==================================================
    // Class variables
    
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask2D mask;
    
    /**
     * The factory to use for creating the result array.
     */
    private IntArray.Factory<?> factory = UInt16Array.defaultFactory;
    
    /**
     * Flag for dividing final distance map by the value first weight. This
     * results in distance map values closer to Euclidean, but with non integer
     * values.
     */
    private boolean normalizeMap = true;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Default constructor that specifies the chamfer mask.
     * 
     * @param mask
     *            the chamfer mask to use for propagating distances.
     */
    public ChamferDistanceTransform2DInt(ChamferMask2D mask)
    {
        this.mask = mask;
    }
    
    /**
     * Constructor specifying the chamfer mask and the optional normalization
     * option.
     * 
     * @param mask
     *            the chamfer mask to use for propagating distances.
     * @param normalize
     *            flag indicating whether the final distance map should be
     *            normalized by the first weight
     */
    public ChamferDistanceTransform2DInt(ChamferMask2D mask, boolean normalize)
    {
        this.mask = mask;
        this.normalizeMap = normalize;
    }
    
    /**
     * Set the factory for creating the result array.
     * 
     * @param factory
     *            the factory for creating the result array.
     */
    public void setFactory(IntArray.Factory<?> factory)
    {
        this.factory = factory;
    }
    
    
    // ==================================================
    // Implementation of the DistanceTransform2D interface
    
    public IntArray2D<?> process2d(BinaryArray2D array)
    {
        // Allocate result array
        IntArray2D<?> distMap = initializeResult(array);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array);
        backwardIteration(distMap, array);
        
        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array);
        }
        
        return distMap;
    }
    
    
    // ==================================================
    // Implementation of the DistanceTransform interface
    
    @Override
    public Result computeResult(BinaryArray array)
    {
        if (array.dimensionality() != 2) throw new IllegalArgumentException("Requires an array of dimensionity 2");
        BinaryArray2D array2d = BinaryArray2D.wrap(array);
        
        // Allocate result array
        IntArray2D<?> distMap = initializeResult(array2d);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array2d);
        int distMax = backwardIteration(distMap, array2d);
        
        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array2d);
            double w0 = mask.getIntegerNormalizationWeight();
            distMax = (int) (distMax / w0);
        }
        
        return new DistanceTransform.Result(distMap, distMax);
    }
    
    
    // ==================================================
    // Inner computation methods
    
    private IntArray2D<?> initializeResult(BinaryArray2D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create new empty image, and fill it with black
        IntArray2D<?> result = IntArray2D.wrap(factory.create(sizeX, sizeY));
        int maxValue = result.typeMax().getInt();
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                boolean inside = array.getBoolean(x, y);
                result.setInt(x, y, inside ? maxValue : 0);
            }
        }
        
        return result;
    }
    
    private void forwardIteration(IntArray2D<?> distMap, BinaryArray2D maskImage)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));
        
        // size of image
        int sizeX = maskImage.size(0);
        int sizeY = maskImage.size(1);
        Collection<Offset> offsets = mask.getForwardOffsets();
        
        // Iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // process only pixels within the mask
                if (!maskImage.getBoolean(x, y)) continue;
                
                // current distance value
                int currentDist = distMap.getInt(x, y);
                int newDist = currentDist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (!distMap.containsPosition(x2, y2)) continue;
                    
                    if (maskImage.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getInt(x2, y2) + offset.intWeight);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, offset.intWeight);
                    }
                }
                
                if (newDist < currentDist)
                {
                    distMap.setInt(x, y, newDist);
                }
            }
        } // end of processing for current line
        
        this.fireProgressChanged(this, sizeY, sizeY);
        
    } // end of forward iteration
    
    private int backwardIteration(IntArray2D<?> distMap, BinaryArray2D maskImage)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));
        
        // size of image
        int sizeX = maskImage.size(0);
        int sizeY = maskImage.size(1);
        Collection<Offset> offsets = mask.getBackwardOffsets();
        
        // initialize largest distance to 0
        int distMax = 0;
        
        // Iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
        {
            for (int x = sizeX - 1; x >= 0; x--)
            {
                // process only pixels within the mask
                if (!maskImage.getBoolean(x, y)) continue;
                
                // current distance value
                int currentDist = distMap.getInt(x, y);
                int newDist = currentDist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (!distMap.containsPosition(x2, y2)) continue;
                    
                    if (maskImage.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getInt(x2, y2) + offset.intWeight);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, offset.intWeight);
                    }
                }
                
                if (newDist < currentDist)
                {
                    distMap.setInt(x, y, newDist);
                }
                
                distMax = Math.max(distMax, newDist);
            }
        } // end of processing for current line
        
        this.fireProgressChanged(this, sizeY, sizeY);
        return distMax;
    } // end of backward iteration
    
    private void normalizeResult(IntArray2D<?> distMap, BinaryArray2D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // retrieve the minimum weight
        double w0 = mask.getIntegerNormalizationWeight();
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (array.getBoolean(x, y))
                {
                    distMap.setInt(x, y, (int) Math.round(distMap.getInt(x, y) / w0));
                }
            }
        }
    }
    
    // ==================================================
    // Implementation of the ChamferDistanceTransform2D interface
    
    @Override
    public ChamferMask2D mask()
    {
        return this.mask;
    }
}
