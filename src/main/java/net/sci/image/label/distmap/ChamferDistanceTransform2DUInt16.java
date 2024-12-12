/**
 * 
 */
package net.sci.image.label.distmap;


import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * <p>Computes 2D Chamfer distance maps using in a 3x3 or 5x5 neighborhood of each
 * pixel, and storing result in a 2D UInt16 array.</p>
 * 
 * <p>
 * Uses 5x5 chamfer map, with two passes (one forward, one backward). Three
 * different weights are provided for orthogonal, diagonal, and "chess-knight"
 * moves. Weights equal to (5,7,11) usually give nice results.
 * </p>
 * 
 * 
 * @author David Legland
 * @see ChamferDistanceTransform2DFloat32
 */
public class ChamferDistanceTransform2DUInt16 extends AlgoStub
        implements ArrayOperator, DistanceTransform2D
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

    public ChamferDistanceTransform2DUInt16(short[] weights, boolean normalize)
    {
        this.mask = ChamferMask2D.fromWeights(weights);
        this.normalizeMap = normalize;
    }
    

    // ==================================================
    // Computation methods

    public UInt16Array2D process2d(IntArray2D<?> labelMap)
    {
        // Allocate result array
        UInt16Array2D distMap = initializeResult(labelMap);

        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, labelMap);
        backwardIteration(distMap, labelMap);

        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, labelMap);
        }

        this.fireStatusChanged(new AlgoEvent(this, ""));
        return distMap;
    }
    

    // ==================================================
    // Inner computation methods

    private UInt16Array2D initializeResult(IntArray2D<?> labelMap)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));

        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        
        // create new empty image, and fill it with black
        UInt16Array2D distMap = UInt16Array2D.create(sizeX, sizeY);
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                distMap.setInt(x, y, labelMap.getInt(x, y) > 0 ? UInt16.MAX_INT : 0);
            }
        }

        return distMap;
    }

    private void forwardIteration(UInt16Array2D distMap, IntArray2D<?> labelMap)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));

        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        Collection<Offset> offsets = mask.getForwardOffsets();

        // Iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // get current label
                int label = labelMap.getInt(x, y);

                // do not process background pixels
                if (label == 0) continue;

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
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (labelMap.getInt(x2, y2) == label)
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

    private void backwardIteration(UInt16Array2D distMap, IntArray2D<?> labelMap)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));

        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        Collection<Offset> offsets = mask.getBackwardOffsets();

        // Iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
        {
            for (int x = sizeX - 1; x >= 0; x--)
            {
                // get current label
                int label = labelMap.getInt(x, y);

                // do not process background pixels
                if (label == 0) continue;

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
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    if (labelMap.getInt(x2, y2) == label)
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
    } // end of backward iteration

    private void normalizeResult(UInt16Array2D distMap, IntArray2D<?> labelMap)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));

        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);

        // retrieve the minimum weight
        double w0 = mask.getIntegerNormalizationWeight();

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (labelMap.getInt(x, y) > 0)
                {
                    distMap.setInt(x, y, (int) Math.round(distMap.getInt(x, y) / w0));
                }
            }
        }
    }
}
