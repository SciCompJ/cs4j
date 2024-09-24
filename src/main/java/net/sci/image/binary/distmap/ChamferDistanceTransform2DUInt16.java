/**
 * 
 */
package net.sci.image.binary.distmap;


import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * <p>
 * Computes 2D Chamfer distance maps using an arbitrary chamfer mask, and
 * storing result in a 2D array UInt16.
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
 * The resulting map is stored within an instance of UInt16Array2D, usually a
 * good compromise between largest possible distance and memory occupation. The
 * <code>ChamferDistanceTransform2DInt</code> class allows for choosing other
 * data types for storing the result maps.
 * </p>
 * 
 * @author David Legland
 * 
 * @see ChamferDistanceTransform2DInt
 * @see ChamferDistanceTransform2DFloat32
 */
public class ChamferDistanceTransform2DUInt16 extends AlgoStub implements
		ArrayOperator, ChamferDistanceTransform2D
{
    // ==================================================
    // Class variables

    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask2D mask;
    
    /**
     * Flag for dividing final distance map by the value first weight. This
     * results in distance map values closer to Euclidean, but with non integer
     * values.
     */
    private boolean normalizeMap = true;
    
    
    // ==================================================
    // Constructors
    
    public ChamferDistanceTransform2DUInt16(ChamferMask2D mask)
    {
        this.mask = mask;
    }
    
    public ChamferDistanceTransform2DUInt16(ChamferMask2D mask, boolean normalize)
    {
        this.mask = mask;
        this.normalizeMap = normalize;
    }
    
    
    // ==================================================
    // Computation methods
    
    public UInt16Array2D process2d(BinaryArray2D array)
    {
        // Allocate result array
        UInt16Array2D distMap = initializeResult(array);
        
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
        if (array.dimensionality() != 2) throw new IllegalArgumentException("Requires an array of dimensionality 2");
        BinaryArray2D array2d = BinaryArray2D.wrap(array);
        
        // Allocate result array
        UInt16Array2D distMap = initializeResult(array2d);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array2d);
        int distMax = backwardIteration(distMap, array2d);
        
        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array2d);
            
            // normalize distMax such that it is greater than actual max value
            double w0 = mask.getIntegerNormalizationWeight();
            distMax = (int) Math.ceil(distMax / w0);
        }
        
        return new DistanceTransform.Result(distMap, distMax);
    }
    
    
    // ==================================================
    // Inner computation methods
    
    private UInt16Array2D initializeResult(BinaryArray2D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create new empty image, and fill it with black
        UInt16Array2D result = UInt16Array2D.create(sizeX, sizeY);
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                boolean inside = array.getBoolean(x, y);
                result.setInt(x, y, inside ? UInt16.MAX_INT : 0);
            }
        }
        
        return result;
    }
    
    private void forwardIteration(UInt16Array2D distMap, BinaryArray2D maskImage)
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
    
    private int backwardIteration(UInt16Array2D distMap, BinaryArray2D maskImage)
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
    
    private void normalizeResult(UInt16Array2D distMap, BinaryArray2D array)
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
