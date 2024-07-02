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
import net.sci.array.numeric.Float32Array2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * <p>Computes 2D Chamfer distance maps using in a 3x3 or 5x5 neighborhood of each
 * pixel, and storing result in a 2D Float32 array.</p>
 * 
 * <p>Uses 5x5 chamfer mask, with two passes (one forward, one backward).
 * Three different weights are provided for orthogonal, diagonal, and 
 * "chess-knight" moves. Weights equal to (5,7,11) usually give nice results.
 * </p>
 * 
 * @author David Legland
 * @see ChamferDistanceTransform2DUInt16
 */
public class ChamferDistanceTransform2DFloat32 extends AlgoStub implements ArrayOperator, DistanceTransform, ChamferDistanceTransform2D
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
    
	public ChamferDistanceTransform2DFloat32(ChamferMask2D mask)
	{
        this.mask = mask;
	}

	public ChamferDistanceTransform2DFloat32(ChamferMask2D mask, boolean normalize)
	{
        this.mask = mask;
		this.normalizeMap = normalize;
	}

	
    // ==================================================
    // Implementation of the DistanceTransform interface

    @Override
    public Result computeResult(BinaryArray array)
    {
        if (array.dimensionality() != 2) throw new IllegalArgumentException("Requires an array of dimensionity 2");
        BinaryArray2D array2d = BinaryArray2D.wrap(array);
        
        // Allocate result array
        Float32Array2D distMap = initializeResult(array2d);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array2d);
        double distMax = backwardIteration(distMap, array2d);

        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array2d);
            double w0 = mask.getNormalizationWeight();
            distMax = distMax / w0;
        }
        
        return new DistanceTransform.Result(distMap, distMax);
    }
    

    // ==================================================
    // Implementation of the DistanceTransform2D interface

	public Float32Array2D process2d(BinaryArray2D array)
	{
        // Allocate result array
	    Float32Array2D distMap = initializeResult(array);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array);
        backwardIteration(distMap, array);

        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array);
        }
        
        this.fireStatusChanged(new AlgoEvent(this, ""));        
        return distMap;
	}
	
    // ==================================================
    // Inner computation methods 
    
    private Float32Array2D initializeResult(BinaryArray2D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create new empty image, and fill it with black
        Float32Array2D result = Float32Array2D.create(sizeX, sizeY);
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                boolean inside = array.getBoolean(x, y);
                result.setFloat(x, y, inside ? Float.MAX_VALUE : 0);
            }
        }
        
        return result;
    }
    
	private void forwardIteration(Float32Array2D distMap, BinaryArray2D maskImage)
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
                if (!maskImage.getBoolean(x, y))
                    continue;
                
                // current distance value
                float currentDist = distMap.getFloat(x, y);
                float newDist = currentDist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (maskImage.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getFloat(x2, y2) + (float) offset.weight);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, (float) offset.weight);
                    }
                }
                
                if (newDist < currentDist) 
                {
                    distMap.setFloat(x, y, newDist);
                }
            }
        } // end of processing for current line
        
        this.fireProgressChanged(this, sizeY, sizeY);
	} // end of forward iteration

	private double backwardIteration(Float32Array2D distMap, BinaryArray2D maskImage)
	{
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));
        
        // size of image
        int sizeX = maskImage.size(0);
        int sizeY = maskImage.size(1);
        Collection<Offset> offsets = mask.getBackwardOffsets();
        
        // initialize largest distance to 0
        double distMax = 0;
        
        // Iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
        {
            for (int x = sizeX - 1; x >= 0; x--)
            {
                // process only pixels within the mask
                if (!maskImage.getBoolean(x, y))
                    continue;
                
                // current distance value
                float currentDist = distMap.getFloat(x, y);
                float newDist = currentDist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (maskImage.getBoolean(x2, y2))
                    {
                        // Foreground pixel: increment distance
                        newDist = Math.min(newDist, distMap.getFloat(x2, y2) + (float) offset.weight);
                    }
                    else
                    {
                        // Background pixel: init with first weight
                        newDist = Math.min(newDist, (float) offset.weight);
                    }
                }
                
                if (newDist < currentDist) 
                {
                    distMap.setFloat(x, y, newDist);
                }
                
                distMax = Math.max(distMax, newDist);
            }
        } // end of processing for current line 
        
        this.fireProgressChanged(this, sizeY, sizeY);
        return distMax;
	} // end of backward iteration
	
    private void normalizeResult(Float32Array2D distMap, BinaryArray2D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // retrieve the minimum weight
        double w0 = mask.getNormalizationWeight();
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                if (array.getBoolean(x, y)) 
                {
                    distMap.setFloat(x, y, (float) (distMap.getFloat(x, y) / w0));
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
